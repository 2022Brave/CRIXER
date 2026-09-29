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

        // ICC Men's ODI top 12, rank date 23 Sep 2026.
        private val TOP_12_CODES = setOf(
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
        "IRE" to Team("IRE", "Ireland", "IRE", "🇮🇪", 0xFF169B62, 0xFFFF883E)
    )

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
        "IRELAND" to "IRE", "IRE" to "IRE"
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
        try {
            val url = BASE_URL + "/" + endpoint + "?type=" + INTERNATIONAL
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", "CRIXER/1.0 Android")
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string().orEmpty()
                if (body.isBlank()) return@withContext emptyList()

                val matches = JSONObject(body)
                    .optJSONObject("data")
                    ?.optJSONArray("matches")
                    ?: return@withContext emptyList()

                parseMatches(matches, status)
            }
        } catch (_: Exception) {
            emptyList()
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
                (match.team1.id in TOP_12_CODES || match.team2.id in TOP_12_CODES)
            ) {
                result += match
            }
        }

        return result.distinctBy { it.id }
    }

    private fun parseMatch(item: JSONObject, status: MatchStatus): Match? {
        val rawTitle = cleanText(item.optString("title"))
        val rawTeams = item.optJSONArray("teams") ?: return null
        if (rawTeams.length() < 2) return null

        val first = rawTeams.optJSONObject(0) ?: return null
        val second = rawTeams.optJSONObject(1) ?: return null

        val code1 = resolveTeam(first.optString("team"), rawTitle) ?: return null
        val code2 = resolveTeam(second.optString("team"), rawTitle, code1) ?: return null
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
            """need(?:s)?\\s+(\\d+)\\s+runs?""",
            RegexOption.IGNORE_CASE
        ).find(overview)?.groupValues?.getOrNull(1)?.toIntOrNull()

        val remainingBalls = Regex(
            """(\\d+)\\s+balls?""",
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
            situationSummary = overview,
            resultSummary = if (status == MatchStatus.COMPLETED) overview.ifBlank { null } else null,
            scheduledDateText = if (status == MatchStatus.UPCOMING) schedule.ifBlank { null } else null
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

        val score = Regex("""(\\d+)\\s*[-/]\\s*(\\d+)""").find(clean)
        val overs = Regex(
            """\\((\\d+(?:\\.\\d+)?)\\s*Ovs?\\)""",
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
        val normalized = cleanText(raw).uppercase(Locale.US).replace("..", "")
        val direct = aliases[normalized]
        if (direct != null && direct != exclude) return direct

        val upperTitle = title.uppercase(Locale.US)
        for ((alias, code) in aliases) {
            if (code != exclude && upperTitle.contains(alias)) return code
        }
        return null
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
        val clean = title
            .replace(Regex("""\\s*-\\s*LIVE.*$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\\s*-\\s*CRICKET SCORE.*$""", RegexOption.IGNORE_CASE), "")
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
            .replace(Regex("""\\s+"""), " ")
            .trim()

    override suspend fun getMatchDetails(matchId: String): Match? =
        (getLiveMatches() + getCompletedMatches() + getUpcomingMatches())
            .firstOrNull { it.id == matchId }

    override suspend fun getPlayerProfile(playerId: String): PlayerProfile? = null
}
