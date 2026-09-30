        endpoint: String,
        status: MatchStatus
    ): List<Match> = withContext(Dispatchers.IO) {
        ensureTop12Codes()
        val apiMatches = sanitizeMatches(fetchFromCricbuzzApi(endpoint, status), status)
        if (apiMatches.isNotEmpty()) {
            // Match-list feeds provide scores and match identity; the Cricbuzz
            // match-center feed provides current batters/ball context. If the
            // detail call fails, we deliberately keep those fields empty.
            return@withContext apiMatches.map { enrichFromCricbuzzMatchCenter(it) }
        }

        val pageUrl = when (endpoint) {
            "live" -> CRICBUZZ_LIVE_URL
            "upcoming" -> CRICBUZZ_UPCOMING_URL
            else -> CRICBUZZ_RECENT_URL
        }
        val pageMatches = sanitizeMatches(fetchFromCricbuzzPage(pageUrl, status), status)
        if (pageMatches.isNotEmpty()) return@withContext pageMatches

        val cricinfoUrl = when (endpoint) {
            "live" -> CRICINFO_LIVE_URL
            "upcoming" -> CRICINFO_UPCOMING_URL
            else -> CRICINFO_RECENT_URL
        }
        sanitizeMatches(fetchFromCricinfoPage(cricinfoUrl, status), status)
    }

    private fun fetchFromCricbuzzApi(
        endpoint: String,
        status: MatchStatus
    ): List<Match> = try {
        val url = BASE_URL + "/" + endpoint + "?type=" + INTERNATIONAL
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("User-Agent", "CRIXER/1.0 Android")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@use emptyList()
            val body = response.body?.string().orEmpty()
            if (body.isBlank()) return@use emptyList()
            val matches = JSONObject(body)
                .optJSONObject("data")
                ?.optJSONArray("matches")
                ?: return@use emptyList()
            parseMatches(matches, status)
                .filter { isValidForStatus(it, status) }
        }
    } catch (_: Exception) {
        emptyList()
    }

    /**
     * Cricbuzz changed its match-list markup. Do not depend on the old
     * cb-match-card/cb-mtch-lst classes; match URLs are much more stable.
     * The slug contains the two team codes (for example /sl-vs-nep-...).
     */

    /**
     * Pulls the live match-center JSON for an API-discovered match.
     * Cricbuzz documents the current miniscore as containing the active
     * batting team and two current batters. We only accept those names when
     * the team identity agrees with CRIXER's normalized innings.
     */
    private fun enrichFromCricbuzzMatchCenter(match: Match): Match {
        if (match.status == MatchStatus.UPCOMING) return match

        val sourceId = match.id.removePrefix("cricbuzz-").takeIf { it.all(Char::isDigit) } ?: return match

        return try {
            val request = Request.Builder()
                .url("https://www.cricbuzz.com/api/mcenter/comm/$sourceId")
                .header("Accept", "application/json")
                .header("User-Agent", "CRIXER/1.0 Android")
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return match
                val body = response.body?.string().orEmpty()
                if (body.isBlank()) return match

                val root = JSONObject(body)
                val mini = root.optJSONObject("miniscore") ?: return match
                val batTeamName = mini.optJSONObject("batTeamScoreObj")
                    ?.optString("teamName")
                    .orEmpty()
                val battingCode = resolveTeamToken(batTeamName) ?: return match

                val expectedInnings = if (match.currentInningsNumber == 2) match.innings2 else match.innings1
                if (expectedInnings == null || expectedInnings.battingTeamId != battingCode) return match

                val batters = listOfNotNull(
                    parseMiniBatter(mini.optJSONObject("batsmanStriker"), battingCode),
                    parseMiniBatter(mini.optJSONObject("batsmanNonStriker"), battingCode)
                ).distinctBy { it.id }

                if (batters.isEmpty()) return match

                val updatedInnings = expectedInnings.copy(batters = batters)
                if (match.currentInningsNumber == 2) {
                    match.copy(innings2 = updatedInnings)
                } else {
                    match.copy(innings1 = updatedInnings)
                }
            }
        } catch (_: Exception) {
            match
        }
    }

    private fun parseMiniBatter(json: JSONObject?, teamCode: String): com.example.domain.model.BatterScore? {
        if (json == null) return null
        val name = cleanText(json.optString("name"))
        if (name.isBlank()) return null
        val runs = json.optInt("runs", -1)
        val balls = json.optInt("balls", -1)
        if (runs < 0 || balls < 0) return null
        val id = json.optString("id").ifBlank {
            json.optString("batId").ifBlank { name.lowercase(Locale.US).replace(Regex("[^a-z0-9]+"), "-") }
        }
        val sr = if (balls > 0) runs * 100f / balls else 0f
        return com.example.domain.model.BatterScore(
            id = "$teamCode-$id",
            name = name,
            shortName = name,
            runs = runs,
            balls = balls,
            fours = json.optInt("fours", json.optInt("batFours", 0)),
            sixes = json.optInt("sixes", json.optInt("batSixes", 0)),
            strikeRate = sr,
            isNotOut = true,
            isOnStrike = json.optBoolean("isStriker", false)
        )
    }

    private fun fetchFromCricbuzzPage(url: String, status: MatchStatus): List<Match> = try {
        val document = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/128 Mobile Safari/537.36")
            .referrer("https://www.google.com/")
            .timeout(15_000)
            .ignoreHttpErrors(true)
            .followRedirects(true)
            .get()

        document.select("a[href*='/live-cricket-scores/']")
            // The page contains a global "MATCHES" strip before the actual
            // Live/Recent/Upcoming section. Actual score entries contain the
            // bullet-separated match metadata in their own link/container.
            .filter { cleanText(it.text()).contains("•") }
            .mapNotNull { parseCricbuzzMatchLink(it, status) }
            .filter { isValidForStatus(it, status) }
            .distinctBy { it.id }
            .filter { it.team1.id in top12Codes || it.team2.id in top12Codes }
    } catch (_: Exception) {
        emptyList()
    }

    private fun parseCricbuzzMatchLink(
        link: Element,
        status: MatchStatus
    ): Match? {
        val href = link.attr("href").orEmpty()
        val slug = href.substringAfter("/live-cricket-scores/", "")
            .substringBefore("?")
            .substringBefore("#")

        val rawLinkText = cleanText(link.text())
        val rawParentText = cleanText(link.parent()?.text().orEmpty())
        if (isNonSeniorTeamLabel("$slug $rawLinkText $rawParentText")) return null

        val slugTeams = Regex(
            """^([a-z]{2,6})-(?:vs|v)-([a-z]{2,6})(?:-|$)""",
            RegexOption.IGNORE_CASE
        ).find(slug)

        val rawContainer = buildString {
            append(link.text())
            append(" ")
            append(link.parent()?.text().orEmpty())
            append(" ")
            append(slug.replace('-', ' '))
        }