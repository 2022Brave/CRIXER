package com.example.data.provider

import com.example.domain.model.Innings
import com.example.domain.model.Match
import com.example.domain.model.MatchFormat
import com.example.domain.model.MatchStatus
import com.example.domain.model.PlayerProfile
import com.example.domain.model.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

interface CricketDataProvider {
    suspend fun getLiveMatches(): List<Match>
    suspend fun getCompletedMatches(): List<Match>
    suspend fun getUpcomingMatches(): List<Match>
    suspend fun getMatchDetails(matchId: String): Match?
    suspend fun getPlayerProfile(playerId: String): PlayerProfile?
}

/**
 * Real CRIXER network provider.
 * Data is sourced from a Cricbuzz-backed public feed.
 * Never falls back to hard-coded/demo scores.
 */
class VerifiedCricketDataProvider(
    private val httpClient: OkHttpClient = defaultHttpClient()
) : CricketDataProvider {

    companion object {
        private const val BASE_URL = "https://cricbuzz-live.vercel.app/v1/matches"
        private const val INTERNATIONAL = "international"
        private const val CRICBUZZ_LIVE_URL = "https://www.cricbuzz.com/cricket-match/live-scores"
        private const val CRICBUZZ_UPCOMING_URL = "https://www.cricbuzz.com/cricket-match/live-scores/upcoming-matches"
        private const val CRICBUZZ_RECENT_URL = "https://www.cricbuzz.com/cricket-match/live-scores/recent-matches"
        private const val CRICINFO_LIVE_URL = "https://www.cricinfo.com/live-cricket-score"
        private const val CRICINFO_UPCOMING_URL = "https://www.cricinfo.com/live-cricket-match-schedule-fixtures"
        private const val CRICINFO_RECENT_URL = "https://www.cricinfo.com/live-cricket-match-results"
        private const val ICC_ODI_TEAM_RANKINGS_URL =
            "https://www.icc-cricket.com/rankings/team-rankings/mens/odi"

        // Safety fallback only. Runtime ranking eligibility is refreshed from the ICC
        // Men's ODI Team Rankings page and replaces this set when the official page
        // can be reached.
        private val FALLBACK_top12Codes = setOf(
            "IND", "NZ", "AUS", "SA", "PAK", "ENG",
            "SL", "AFG", "BAN", "WI", "ZIM", "IRE"
        )

        private fun defaultHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .writeTimeout(12, TimeUnit.SECONDS)
                .build()
    }

    private val teams = mapOf(
        "IND" to Team("IND", "India", "IND", "🇮🇳", 0xFFFF9933, 0xFF138808),
        "NZ" to Team("NZ", "New Zealand", "NZ", "🇳🇿", 0xFF000000, 0xFFFFFFFF),
        "AUS" to Team("AUS", "Australia", "AUS", "🇦🇺", 0xFF002B7F, 0xFFFFCD00),
        "SA" to Team("SA", "South Africa", "SA", "🇿🇦", 0xFF007749, 0xFFFFB612),
        "PAK" to Team("PAK", "Pakistan", "PAK", "🇵🇰", 0xFF115740, 0xFFFFFFFF),
        "ENG" to Team("ENG", "England", "ENG", "🏴", 0xFF00205B, 0xFFCE1126),
        "SL" to Team("SL", "Sri Lanka", "SL", "🇱🇰", 0xFF0A2A66, 0xFFFFB81C),
        "AFG" to Team("AFG", "Afghanistan", "AFG", "🇦🇫", 0xFF007A36, 0xFFD32030),
        "BAN" to Team("BAN", "Bangladesh", "BAN", "🇧🇩", 0xFF006A4E, 0xFFF42A41),
        "WI" to Team("WI", "West Indies", "WI", "🏝️", 0xFF7B002C, 0xFFFFC72C),
        "ZIM" to Team("ZIM", "Zimbabwe", "ZIM", "🇿🇼", 0xFF006400, 0xFFFFD700),
        "IRE" to Team("IRE", "Ireland", "IRE", "🇮🇪", 0xFF169B62, 0xFFFF883E),
        "NEP" to Team("NEP", "Nepal", "NEP", "🇳🇵", 0xFFDC143C, 0xFF003893),
        "MLY" to Team("MLY", "Malaysia", "MLY", "🇲🇾", 0xFF010066, 0xFFFFCC00),
        "HKC" to Team("HKC", "Hong Kong", "HKC", "🇭🇰", 0xFFDE2910, 0xFFFFFFFF),
        "OMAN" to Team("OMAN", "Oman", "OMA", "🇴🇲", 0xFFDB161B, 0xFFFFFFFF),
        "JPN" to Team("JPN", "Japan", "JPN", "🇯🇵", 0xFFBC002D, 0xFFFFFFFF)
    )

    @Volatile
    private var top12Codes: Set<String> = FALLBACK_top12Codes
    @Volatile
    private var rankingsFetchedAtMs: Long = 0L

    private val aliases = mapOf(
        "INDIA" to "IND", "IND" to "IND",
        "NEW ZEALAND" to "NZ", "NEWZEALAND" to "NZ", "NZ" to "NZ",
        "AUSTRALIA" to "AUS", "AUS" to "AUS",
        "SOUTH AFRICA" to "SA", "RSA" to "SA", "SA" to "SA",
        "PAKISTAN" to "PAK", "PAK" to "PAK",
        "ENGLAND" to "ENG", "ENG" to "ENG",
        "SRI LANKA" to "SL", "SL" to "SL",
        "AFGHANISTAN" to "AFG", "AFG" to "AFG",
        "BANGLADESH" to "BAN", "BAN" to "BAN",
        "WEST INDIES" to "WI", "WI" to "WI",
        "ZIMBABWE" to "ZIM", "ZIM" to "ZIM",
        "IRELAND" to "IRE", "IRE" to "IRE",
        "NEPAL" to "NEP", "NEP" to "NEP",
        "MALAYSIA" to "MLY", "MLY" to "MLY",
        "HONG KONG" to "HKC", "HONGKONG" to "HKC", "HKC" to "HKC",
        "OMAN" to "OMAN", "OMA" to "OMAN",
        "JAPAN" to "JPN", "JPN" to "JPN"
    )

    override suspend fun getLiveMatches(): List<Match> =
        fetchMatches("live", MatchStatus.LIVE)

    override suspend fun getCompletedMatches(): List<Match> =
        fetchMatches("recent", MatchStatus.COMPLETED)

    override suspend fun getUpcomingMatches(): List<Match> =
        fetchMatches("upcoming", MatchStatus.UPCOMING)

    private suspend fun fetchMatches(
        endpoint: String,
        status: MatchStatus
    ): List<Match> = withContext(Dispatchers.IO) {
        ensureTop12Codes()
        val apiMatches = sanitizeMatches(fetchFromCricbuzzApi(endpoint, status), status)
        if (apiMatches.isNotEmpty()) return@withContext apiMatches

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

        val codes = if (slugTeams != null) {
            listOf(
                resolveTeamToken(slugTeams.groupValues[1]),
                resolveTeamToken(slugTeams.groupValues[2])
            )
        } else {
            extractTeamCodes(rawContainer)
        }.filterNotNull().distinct()

        if (codes.size < 2 || codes[0] == codes[1]) return null

        val code1 = codes[0]
        val code2 = codes[1]
        val team1 = teams[code1] ?: return null
        val team2 = teams[code2] ?: return null

        val containerText = cleanText(
            listOf(
                link.text(),
                link.parent()?.text().orEmpty()
            ).joinToString(" ")
        )
        val combinedText = cleanText("$containerText $slug")

        if (combinedText.contains("U19", true) ||
            combinedText.contains("UNDER-19", true) ||
            combinedText.contains("WOMEN", true)
        ) return null

        val scoreValues = Regex(
            """\b\d{1,3}\s*[-/]\s*\d{1,2}(?:\s*\(\d+(?:\.\d+)?\))?\b"""
        ).findAll(containerText)
            .map { it.value }
            .distinct()
            .toList()

        val score1 = scoreValues.getOrNull(0)?.let(::parseScore) ?: Score()
        val score2 = scoreValues.getOrNull(1)?.let(::parseScore) ?: Score()
        val format = inferFormat(combinedText)

        return Match(
            id = "cricbuzz-web-" + (href.ifBlank { slug }).hashCode(),
            title = canonicalTitle(team1, team2, link.text().ifBlank { slug.replace('-', ' ') }, format),
            venue = extractVenue(containerText),
            format = format,
            status = status,
            team1 = team1,
            team2 = team2,
            innings1 = innings(1, code1, code2, score1, format),
            innings2 = if (status == MatchStatus.UPCOMING) null else innings(2, code2, code1, score2, format),
            currentInningsNumber = if (
                status != MatchStatus.UPCOMING &&
                (score2.runs > 0 || score2.wickets > 0 || score2.overs > 0f)
            ) 2 else 1,
            targetRuns = if (score1.runs > 0 && format != MatchFormat.TEST && status != MatchStatus.UPCOMING) {
                score1.runs + 1
            } else null,
            requiredRuns = Regex(
                """need(?:s)?\s+(\d+)\s+runs?""",
                RegexOption.IGNORE_CASE
            ).find(containerText)?.groupValues?.getOrNull(1)?.toIntOrNull(),
            remainingBalls = Regex(
                """(\d+)\s+balls?""",
                RegexOption.IGNORE_CASE
            ).find(containerText)?.groupValues?.getOrNull(1)?.toIntOrNull(),
            requiredRunRate = null,
            currentRunRate = if (score2.overs > 0f) score2.runs / score2.overs
            else if (score1.overs > 0f) score1.runs / score1.overs else 0f,
            situationSummary = extractSituationSummary(containerText, status),
            resultSummary = extractResultSummary(containerText, status),
            scheduledDateText = if (status == MatchStatus.UPCOMING) cleanSchedule(extractScheduleText(containerText)) else null
        )
    }

    private fun extractTeamCodes(text: String): List<String> =
        aliases.entries
            .sortedByDescending { it.key.length }
            .mapNotNull { (alias, code) ->
                if (Regex("""\b${Regex.escape(alias)}\b""", RegexOption.IGNORE_CASE).containsMatchIn(text)) {
                    code
                } else null
            }
            .distinct()
            .take(2)

    private fun resolveTeamToken(token: String): String? {
        val normalized = token.uppercase(Locale.US)
        return aliases[normalized]
    }

    private fun extractVenue(text: String): String {
        val normalized = cleanText(text)
        return normalized
            .substringAfter("•", "")
            .substringBefore("Match abandoned", "")
            .substringBefore("won by", "")
            .trim()
    }

    private suspend fun ensureTop12Codes() {
        val now = System.currentTimeMillis()
        if (now - rankingsFetchedAtMs < 6 * 60 * 60 * 1000L) return

        val fetched = try {
            val document = Jsoup.connect(ICC_ODI_TEAM_RANKINGS_URL)
                .userAgent("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/128 Mobile Safari/537.36")
                .referrer("https://www.google.com/")
                .timeout(15_000)
                .ignoreHttpErrors(true)
                .followRedirects(true)
                .get()

            document.select("table tbody tr")
                .mapNotNull { row ->
                    val position = Regex("""^0?(\d{1,2})\b""")
                        .find(cleanText(row.text()))
                        ?.groupValues?.getOrNull(1)
                        ?.toIntOrNull()
                    val code = resolveRankedTeam(row.text())
                    if (position != null && position <= 12 && code != null) position to code else null
                }
                .sortedBy { it.first }
                .map { it.second }
                .toSet()
        } catch (_: Exception) {
            emptySet()
        }

        if (fetched.size >= 8) {
            top12Codes = fetched
        }
        rankingsFetchedAtMs = now
    }

    private fun resolveRankedTeam(text: String): String? {
        val names = listOf(
            "New Zealand" to "NZ",
            "South Africa" to "SA",
            "West Indies" to "WI",
            "Sri Lanka" to "SL",
            "Afghanistan" to "AFG",
            "Bangladesh" to "BAN",
            "Zimbabwe" to "ZIM",
            "Ireland" to "IRE",
            "Australia" to "AUS",
            "Pakistan" to "PAK",
            "England" to "ENG",
            "India" to "IND"
        )
        return names.firstOrNull { (name, _) ->
            Regex("""\b${Regex.escape(name)}\b""", RegexOption.IGNORE_CASE).containsMatchIn(text)
        }?.second
    }

    private fun fetchFromCricinfoPage(
        url: String,
        status: MatchStatus
    ): List<Match> = try {
        val document = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/128 Mobile Safari/537.36")
            .referrer("https://www.google.com/")
            .timeout(15_000)
            .get()

        document.select("a[href*='live-cricket-score']")
            .mapNotNull { parseCricinfoLink(it, status) }
            .distinctBy { it.id }
            .filter { it.team1.id in top12Codes || it.team2.id in top12Codes }
    } catch (_: Exception) {
        emptyList()
    }

    private fun parseCricinfoLink(
        link: Element,
        status: MatchStatus
    ): Match? {
        val rawTitle = cleanText(link.text().ifBlank { link.attr("title") })
        if (rawTitle.isBlank()) return null

        val matchTeams = Regex(
            """^(.+?)\s+(?:vs|v)\s+(.+?)(?:\s+-|,|$)""",
            RegexOption.IGNORE_CASE
        ).find(rawTitle) ?: return null

        val rawTeam1 = cleanText(matchTeams.groupValues[1])
        val rawTeam2 = cleanText(matchTeams.groupValues[2])
        if (isNonSeniorTeamLabel(rawTeam1) || isNonSeniorTeamLabel(rawTeam2) ||
            isNonSeniorTeamLabel(rawTitle)
        ) return null

        val code1 = resolveTeamStrict(rawTeam1) ?: return null
        val code2 = resolveTeamStrict(rawTeam2) ?: return null
        if (code1 == code2) return null

        val parentText = cleanText(link.parent()?.text().orEmpty())
        val scores = Regex(
            """\b\d{1,3}\s*[-/]\s*\d{1,2}(?:\s*\(\d+(?:\.\d+)?\))?\b"""
        ).findAll(parentText).map { it.value }.toList()

        val score1 = scores.getOrNull(0)?.let(::parseScore) ?: Score()
        val score2 = scores.getOrNull(1)?.let(::parseScore) ?: Score()
        val format = inferFormat(rawTitle + " " + parentText)

        return Match(
            id = "cricinfo-web-" + link.attr("href").hashCode(),
            title = canonicalTitle(teams[code1] ?: return null, teams[code2] ?: return null, rawTitle, format),
            venue = "",
            format = format,
            status = status,
            team1 = teams[code1] ?: return null,
            team2 = teams[code2] ?: return null,
            innings1 = innings(1, code1, code2, score1, format),
            innings2 = if (status == MatchStatus.UPCOMING) null else innings(2, code2, code1, score2, format),
            currentInningsNumber = if (score2.runs > 0 || score2.wickets > 0 || score2.overs > 0f) 2 else 1,
            targetRuns = null,
            requiredRuns = null,
            remainingBalls = null,
            requiredRunRate = null,
            currentRunRate = if (score2.overs > 0f) score2.runs / score2.overs else if (score1.overs > 0f) score1.runs / score1.overs else 0f,
            situationSummary = extractSituationSummary(parentText, status),
            resultSummary = extractResultSummary(parentText, status),
            scheduledDateText = if (status == MatchStatus.UPCOMING) cleanSchedule(extractScheduleText(parentText)) else null
        )
    }

    private fun parseCricbuzzCard(card: Element, status: MatchStatus): Match? {
        val teamNodes = card.select(".cb-hmscg-tm-name span, .cb-hmscg-tm-name")
            .map { cleanText(it.text()) }
            .filter { it.isNotBlank() }
            .distinct()

        val titleNode = card.selectFirst("a[title], a")
        val rawTitle = cleanText(
            titleNode?.attr("title").orEmpty().ifBlank { titleNode?.text().orEmpty() }
        )

        val titleTeams = Regex(
            """^(.+?)\s+vs\s+(.+?)(?:,|\s+-|$)""",
            RegexOption.IGNORE_CASE
        ).find(rawTitle)

        val candidates = if (teamNodes.size >= 2) {
            teamNodes
        } else if (titleTeams != null) {
            listOf(
                cleanText(titleTeams.groupValues[1]),
                cleanText(titleTeams.groupValues[2])
            )
        } else {
            emptyList()
        }

        if (candidates.size < 2) return null

        if (candidates.any(::isNonSeniorTeamLabel) || isNonSeniorTeamLabel(rawTitle)) return null

        val code1 = resolveTeamStrict(candidates[0]) ?: return null
        val code2 = resolveTeamStrict(candidates[1]) ?: return null
        if (code1 == code2) return null

        val team1 = teams[code1] ?: return null
        val team2 = teams[code2] ?: return null
        val format = inferFormat(rawTitle + " " + card.text())

        val scoreNodes = card.select(".cb-ovr-flo, .cb-hmscg-tm-bat, .cb-hmscg-tm-bwl")
            .map { cleanText(it.text()) }
            .filter { it.matches(Regex(""".*\d+.*""")) }

        val score1 = scoreNodes.getOrNull(0)?.let(::parseScore) ?: Score()
        val score2 = scoreNodes.getOrNull(1)?.let(::parseScore) ?: Score()
        val innings1 = innings(1, code1, code2, score1, format)
        val innings2 = innings(2, code2, code1, score2, format)

        val cardText = cleanText(card.text())
        val statusText = cleanText(
            card.selectFirst(".cb-mtch-crd-state, .cb-text-complete, .cb-text-preview")
                ?.text().orEmpty()
        )
        val overview = statusText.ifBlank { cardText }

        val currentInnings = if (
            status != MatchStatus.UPCOMING &&
            (score2.runs > 0 || score2.wickets > 0 || score2.overs > 0f)
        ) 2 else 1

        val schedule = if (status == MatchStatus.UPCOMING) {
            cardText.split("  ").firstOrNull {
                it.contains("AM", true) || it.contains("PM", true) ||
                    it.contains("Today", true) || it.contains("Tomorrow", true)
            }
        } else null

        return Match(
            id = "cricbuzz-web-" + (titleNode?.attr("href").orEmpty().ifBlank { rawTitle }).hashCode(),
            title = cleanTitle(rawTitle, format),
            venue = "",
            format = format,
            status = status,
            team1 = team1,
            team2 = team2,
            innings1 = innings1,
            innings2 = if (status == MatchStatus.UPCOMING) null else innings2,
            currentInningsNumber = currentInnings,
            targetRuns = null,
            requiredRuns = null,
            remainingBalls = null,
            requiredRunRate = null,
            currentRunRate = if (currentInnings == 2 && score2.overs > 0f) score2.runs / score2.overs else if (score1.overs > 0f) score1.runs / score1.overs else 0f,
            situationSummary = extractSituationSummary(overview, status),
            resultSummary = extractResultSummary(overview, status),
            scheduledDateText = if (status == MatchStatus.UPCOMING) cleanSchedule(schedule) else null
        )
    }

    /**
     * The upstream unofficial API has historically returned overlapping lists
     * when its cache is stale. Never trust the endpoint name alone: validate
     * the actual match text before putting it into a CRIXER tab.
     */
    private fun isValidForStatus(match: Match, expected: MatchStatus): Boolean {
        val text = cleanText(
            listOf(
                match.title,
                match.situationSummary,
                match.resultSummary.orEmpty(),
                match.scheduledDateText.orEmpty()
            ).joinToString(" ")
        ).lowercase(Locale.US)

        val completedMarkers = listOf(
            "won by", "match drawn", "drawn", "tie", "tied",
            "no result", "abandoned", "retired hurt", "all out"
        )
        val upcomingMarkers = listOf(
            "match starts", "starts at", "preview",
            "tomorrow", "today", "scheduled"
        )
        val liveMarkers = listOf(
            "day ", "session", "trail by", "lead by", "need ",
            "opt to bat", "opt to bowl", "in progress", "live"
        )

        return when (expected) {
            MatchStatus.LIVE -> {
                completedMarkers.none { text.contains(it) } &&
                    upcomingMarkers.none { text.contains(it) } &&
                    (
                        liveMarkers.any { text.contains(it) } ||
                            match.innings1.runs > 0 ||
                            (match.innings2?.runs ?: 0) > 0 ||
                            match.innings1.overs > 0f ||
                            (match.innings2?.overs ?: 0f) > 0f
                    )
            }

            MatchStatus.COMPLETED -> {
                completedMarkers.any { text.contains(it) } &&
                    upcomingMarkers.none { text.contains(it) }
            }

            MatchStatus.UPCOMING -> {
                completedMarkers.none { text.contains(it) } &&
                    liveMarkers.none { text.contains(it) } &&
                    (
                        upcomingMarkers.any { text.contains(it) } ||
                            !match.scheduledDateText.isNullOrBlank()
                    )
            }

            else -> true
        }
    }

    private fun parseMatches(array: JSONArray, status: MatchStatus): List<Match> {
        val result = mutableListOf<Match>()

        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val match = parseMatch(item, status) ?: continue

            val title = item.optString("title").uppercase(Locale.US)
            val youthOrWomen = title.contains("U19") ||
                title.contains("UNDER-19") ||
                title.contains("WOMEN")

            if (!youthOrWomen &&
                (match.team1.id in top12Codes || match.team2.id in top12Codes)
            ) {
                result += match
            }
        }

        return result
            .distinctBy { it.id }
            .filter { isValidForStatus(it, status) }
    }

    private fun parseMatch(item: JSONObject, status: MatchStatus): Match? {
        val rawTitle = cleanText(item.optString("title"))
        val rawTeams = item.optJSONArray("teams") ?: return null
        if (rawTeams.length() < 2) return null

        val first = rawTeams.optJSONObject(0) ?: return null
        val second = rawTeams.optJSONObject(1) ?: return null

        val rawTeam1 = cleanText(first.optString("team"))
        val rawTeam2 = cleanText(second.optString("team"))
        if (isNonSeniorTeamLabel(rawTeam1) || isNonSeniorTeamLabel(rawTeam2) ||
            isNonSeniorTeamLabel(rawTitle)
        ) return null

        val code1 = resolveTeamStrict(rawTeam1) ?: return null
        val code2 = resolveTeamStrict(rawTeam2) ?: return null
        if (code1 == code2) return null

        val team1 = teams[code1] ?: return null
        val team2 = teams[code2] ?: return null
        val format = inferFormat(rawTitle)

        val score1 = parseScore(first.optString("run"))
        val score2 = parseScore(second.optString("run"))
        val innings1 = innings(1, code1, code2, score1, format)
        val innings2 = innings(2, code2, code1, score2, format)

        val location = item.optJSONObject("timeAndPlace")
        val venue = cleanPlace(location?.optString("place").orEmpty())
        val date = cleanText(location?.optString("date").orEmpty())
        val time = cleanText(location?.optString("time").orEmpty())
        val schedule = listOf(date, time)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        val overview = cleanText(item.optString("overview"))
        val currentInnings = if (
            status != MatchStatus.UPCOMING &&
            (score2.runs > 0 || score2.wickets > 0 || score2.overs > 0f)
        ) 2 else 1

        val target = if (
            currentInnings == 2 &&
            score1.runs > 0 &&
            format != MatchFormat.TEST
        ) score1.runs + 1 else null

        val requiredRuns = Regex(
            """need(?:s)?\s+(\d+)\s+runs?""",
            RegexOption.IGNORE_CASE
        ).find(overview)?.groupValues?.getOrNull(1)?.toIntOrNull()

        val remainingBalls = Regex(
            """(\d+)\s+balls?""",
            RegexOption.IGNORE_CASE
        ).find(overview)?.groupValues?.getOrNull(1)?.toIntOrNull()

        val currentScore = if (currentInnings == 2) score2 else score1
        val crr = if (currentScore.overs > 0f) {
            currentScore.runs / currentScore.overs
        } else 0f

        return Match(
            id = "cricbuzz-" + item.optString("id", rawTitle.hashCode().toString()),
            title = cleanTitle(rawTitle, format),
            venue = venue,
            format = format,
            status = status,
            team1 = team1,
            team2 = team2,
            innings1 = innings1,
            innings2 = if (status == MatchStatus.UPCOMING) null else innings2,
            currentInningsNumber = currentInnings,
            targetRuns = target,
            requiredRuns = requiredRuns,
            remainingBalls = remainingBalls,
            requiredRunRate = if (requiredRuns != null && remainingBalls != null && remainingBalls > 0) {
                requiredRuns * 6f / remainingBalls
            } else null,
            currentRunRate = crr,
            situationSummary = extractSituationSummary(overview, status),
            resultSummary = extractResultSummary(overview, status),
            scheduledDateText = if (status == MatchStatus.UPCOMING) cleanSchedule(schedule) else null
        )
    }

    private data class Score(
        val runs: Int = 0,
        val wickets: Int = 0,
        val overs: Float = 0f
    )

    private fun parseScore(value: String): Score {
        val clean = cleanText(value)
        if (clean.isBlank() || clean.equals("To bat", true)) return Score()

        val score = Regex("""(\d+)\s*[-/]\s*(\d+)""").find(clean)
        val overs = Regex(
            """\((\d+(?:\.\d+)?)\s*Ovs?\)""",
            RegexOption.IGNORE_CASE
        ).find(clean)?.groupValues?.getOrNull(1)?.toFloatOrNull() ?: 0f

        return Score(
            runs = score?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0,
            wickets = score?.groupValues?.getOrNull(2)?.toIntOrNull() ?: 0,
            overs = overs
        )
    }

    private fun innings(
        number: Int,
        batting: String,
        bowling: String,
        score: Score,
        format: MatchFormat
    ) = Innings(
        inningsNumber = number,
        battingTeamId = batting,
        bowlingTeamId = bowling,
        runs = score.runs,
        wickets = score.wickets,
        overs = score.overs,
        maxOvers = when (format) {
            MatchFormat.T20I -> 20f
            MatchFormat.ODI -> 50f
            MatchFormat.TEST -> 450f
        }
    )

    private fun resolveTeam(
        raw: String,
        title: String,
        exclude: String? = null
    ): String? {
        val direct = resolveTeamStrict(raw)
        if (direct != null && direct != exclude) return direct

        if (cleanText(raw).isNotBlank()) return null

        val upperTitle = cleanText(title).uppercase(Locale.US)
        return aliases.entries
            .sortedByDescending { it.key.length }
            .firstOrNull { (alias, code) ->
                code != exclude &&
                    !isNonSeniorTeamLabel(upperTitle) &&
                    Regex("""\b${Regex.escape(alias)}\b""").containsMatchIn(upperTitle)
            }?.second
    }

    private fun resolveTeamStrict(raw: String): String? {
        val normalized = cleanText(raw)
            .uppercase(Locale.US)
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (normalized.isBlank() || isNonSeniorTeamLabel(normalized)) return null

        return aliases[normalized]
    }

    private fun isNonSeniorTeamLabel(value: String): Boolean {
        val text = cleanText(value).uppercase(Locale.US)
        if (text.isBlank()) return false

        return text.contains("WOMEN") ||
            text.contains("UNDER-19") ||
            text.contains("UNDER 19") ||
            text.contains("U19") ||
            text.contains("ACADEMY") ||
            text.contains("EMERGING") ||
            Regex("""(?:^|[\s_-])(A|XI|W)(?:$|[\s_-])""").containsMatchIn(text)
    }

    private fun inferFormat(title: String): MatchFormat {
        val upper = title.uppercase(Locale.US)
        return when {
            upper.contains("TEST") -> MatchFormat.TEST
            upper.contains("ODI") -> MatchFormat.ODI
            upper.contains("T20") -> MatchFormat.T20I
            else -> MatchFormat.ODI
        }
    }

    private fun cleanTitle(title: String, format: MatchFormat): String {
        val clean = cleanText(title)
            .replace(Regex("""\s*-\s*(LIVE|PREVIEW|CRICKET SCORE|SCORECARD|COMMENTARY).*$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\s*\|.*$"""), "")
            .trim()

        val parts = clean.split(",").map { it.trim() }.filter { it.isNotBlank() }
        return if (parts.size >= 2) {
            parts[0] + " · " + parts[1]
        } else if (clean.isNotBlank()) {
            clean + " · " + formatLabel(format)
        } else {
            "International Match · " + formatLabel(format)
        }
    }

    private fun canonicalTitle(
        team1: Team,
        team2: Team,
        sourceTitle: String,
        format: MatchFormat
    ): String {
        val clean = cleanTitle(sourceTitle, format)
        val competitionPart = clean
            .substringAfter(" · ", "")
            .trim()
            .takeIf { it.isNotBlank() && !it.equals(formatLabel(format), true) }

        return if (competitionPart != null &&
            !competitionPart.contains("live score", true) &&
            !competitionPart.contains("scorecard", true) &&
            !competitionPart.contains("commentary", true)
        ) {
            "${team1.name} vs ${team2.name} · $competitionPart"
        } else {
            "${team1.name} vs ${team2.name} · ${formatLabel(format)}"
        }
    }

    private fun extractSituationSummary(raw: String, status: MatchStatus): String {
        val text = cleanText(raw)
        if (text.isBlank()) return ""

        return when (status) {
            MatchStatus.LIVE -> {
                val chase = Regex(
                    """(?i)(?:need(?:s)?|require(?:s)?)\s+\d+\s+runs?(?:\s+(?:from|off)\s+\d+\s+balls?)?"""
                ).find(text)?.value
                val margin = Regex(
                    """(?i)(?:trail(?:s)?|lead(?:s)?)\s+by\s+\d+(?:\s+runs?)?"""
                ).find(text)?.value
                val session = Regex(
                    """(?i)day\s+\d+(?::\s*[^|,]+)?"""
                ).find(text)?.value

                chase ?: margin ?: session ?: ""
            }
            MatchStatus.COMPLETED -> extractResultSummary(text, status).orEmpty()
            else -> ""
        }
    }

    private fun extractResultSummary(raw: String, status: MatchStatus): String? {
        if (status != MatchStatus.COMPLETED) return null
        val text = cleanText(raw)
        if (text.isBlank()) return null

        val result = Regex(
            """(?i)([A-Za-z][A-Za-z .'-]{1,40}\s+won\s+by\s+[^|,.]+(?:\s+(?:runs?|wickets?|innings?))?)"""
        ).find(text)?.value
        if (result != null) return result.trim()

        val fallback = Regex(
            """(?i)(match\s+(?:drawn|tied)|no\s+result(?:\s+due\s+to\s+rain)?|match\s+abandoned[^|,.]*)"""
        ).find(text)?.value
        return fallback?.trim()
    }

    private fun extractScheduleText(raw: String): String {
        val text = cleanText(raw)
        if (text.isBlank()) return ""

        return Regex(
            """(?i)(?:match\s+starts?\s+at|starts?\s+at)\s+[^|]+"""
        ).find(text)?.value
            ?: Regex("""(?i)(?:today|tomorrow|[A-Za-z]{3},?\s+[A-Za-z]+\s+\d{1,2})[^|]*""")
                .find(text)?.value.orEmpty()
    }

    private fun cleanSchedule(value: String): String {
        val text = cleanText(value)
        if (text.isBlank()) return ""

        return text
            .replace(Regex("""(?i)\bmatch\s+starts?\s+at\s*"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun sanitizeMatches(matches: List<Match>, expectedStatus: MatchStatus): List<Match> {
        val seen = mutableSetOf<String>()

        return matches
            .asSequence()
            .filter { it.status == expectedStatus }
            .filter { it.team1.id in top12Codes || it.team2.id in top12Codes }
            .filter { !isNonSeniorTeamLabel("${it.title} ${it.team1.name} ${it.team2.name}") }
            .filter { isValidForStatus(it, expectedStatus) }
            .map { match ->
                match.copy(
                    title = canonicalTitle(match.team1, match.team2, match.title, match.format),
                    venue = cleanPlace(match.venue),
                    situationSummary = extractSituationSummary(match.situationSummary, expectedStatus),
                    resultSummary = extractResultSummary(
                        match.resultSummary.orEmpty().ifBlank { match.situationSummary },
                        expectedStatus
                    ),
                    scheduledDateText = if (expectedStatus == MatchStatus.UPCOMING) {
                        cleanSchedule(match.scheduledDateText.orEmpty())
                    } else null
                )
            }
            .filter { match ->
                val key = listOf(
                    match.team1.id,
                    match.team2.id,
                    match.format.name,
                    cleanText(match.title).lowercase(Locale.US)
                ).joinToString("|")
                seen.add(key)
            }
            .toList()
    }

    private fun formatLabel(format: MatchFormat) = when (format) {
        MatchFormat.T20I -> "T20I"
        MatchFormat.ODI -> "ODI"
        MatchFormat.TEST -> "Test"
    }

    private fun cleanPlace(value: String): String =
        cleanText(value).removePrefix("at ").removePrefix("At ").trim()

    private fun cleanText(value: String): String =
        value.replace("&nbsp;", " ")
            .replace(Regex("""<[^>]+>"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()

    override suspend fun getMatchDetails(matchId: String): Match? =
        (getLiveMatches() + getCompletedMatches() + getUpcomingMatches())
            .firstOrNull { it.id == matchId }

    override suspend fun getPlayerProfile(playerId: String): PlayerProfile? = null
}
