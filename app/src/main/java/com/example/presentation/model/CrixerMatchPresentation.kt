package com.example.presentation.model

import com.example.domain.model.Match
import com.example.domain.model.MatchFormat
import com.example.domain.model.MatchStatus

data class CrixerMatchPresentation(
    val seriesName: String,
    val matchLabel: String,
    val venueLabel: String?,
    val formatLabel: String,
    val team1: TeamPresentation,
    val team2: TeamPresentation,
    val statusLabel: String,
    val situation: String?,
    val result: String?,
    val schedule: String?
)

data class TeamPresentation(
    val id: String,
    val shortName: String,
    val emoji: String,
    val score: String?,
    val overs: String?
)

fun Match.toCrixerPresentation(): CrixerMatchPresentation {
    val matchLabel = extractMatchLabel(title, format)
    val seriesName = extractSeriesName(title, matchLabel)
    val battingTeam = if (currentInningsNumber == 2) team2 else team1

    return CrixerMatchPresentation(
        seriesName = seriesName,
        matchLabel = matchLabel,
        venueLabel = venue.cleanVenue(),
        formatLabel = formatLabel(format),
        team1 = team1.toPresentation(innings1),
        team2 = team2.toPresentation(innings2),
        statusLabel = when (status) {
            MatchStatus.LIVE -> "LIVE"
            MatchStatus.COMPLETED -> "FINAL"
            MatchStatus.UPCOMING -> "UP NEXT"
            MatchStatus.DELAYED -> "DELAYED"
            MatchStatus.ABANDONED -> "ABANDONED"
        },
        situation = buildSituation(this, battingTeam.shortName),
        result = resultSummary.cleanCricketText(),
        schedule = scheduledDateText.cleanCricketText()
    )
}

private fun com.example.domain.model.Team.toPresentation(
    innings: com.example.domain.model.Innings?
): TeamPresentation {
    val hasScore = innings != null && (innings.runs > 0 || innings.wickets > 0)
    return TeamPresentation(
        id = id,
        shortName = shortName,
        emoji = flagEmoji,
        score = if (hasScore) "\${innings!!.runs}/\${innings.wickets}" else null,
        overs = if (hasScore && innings!!.overs > 0f) formatOvers(innings.overs) else null
    )
}

private fun buildSituation(match: Match, battingTeam: String): String? {
    if (match.status == MatchStatus.LIVE) {
        if (match.requiredRuns != null && match.remainingBalls != null) {
            return "\$battingTeam need \${match.requiredRuns} off \${match.remainingBalls} balls"
        }
        if (match.requiredRuns != null) {
            return "\$battingTeam need \${match.requiredRuns} runs"
        }
        match.situationSummary.cleanCricketText()?.let { summary ->
            if (summary.length <= 72 && !looksLikeProviderDump(summary)) return summary
        }
    }
    return null
}

private fun extractMatchLabel(title: String, format: MatchFormat): String {
    val normalized = title.replace("•", "·").replace(Regex("\\s+"), " ").trim()
    val ordinal = Regex("(?i)\\b\\d+(?:st|nd|rd|th)\\s+(?:ODI|T20I|TEST)\\b")
        .find(normalized)?.value
    return ordinal ?: when (format) {
        MatchFormat.ODI -> "ODI"
        MatchFormat.T20I -> "T20I"
        MatchFormat.TEST -> "TEST"
    }
}

private fun extractSeriesName(title: String, matchLabel: String): String {
    val normalized = title.replace("•", "·").replace(Regex("\\s+"), " ").trim()
    val cleaned = normalized
        .replace(Regex("(?i)\\b\\d+(?:st|nd|rd|th)\\s+(?:ODI|T20I|TEST)\\b"), "")
        .split("·")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .firstOrNull { !it.equals(matchLabel, ignoreCase = true) }
        ?: normalized.takeIf { it.isNotBlank() }
        ?: "International cricket"

    return if (looksLikeProviderDump(cleaned)) "International cricket" else cleaned
}

private fun formatLabel(format: MatchFormat): String = when (format) {
    MatchFormat.ODI -> "ODI"
    MatchFormat.T20I -> "T20I"
    MatchFormat.TEST -> "TEST"
}

private fun formatOvers(overs: Float): String {
    val whole = overs.toInt()
    val balls = ((overs - whole) * 10f).toInt().coerceIn(0, 5)
    return "\$whole.\$balls"
}

private fun String?.cleanVenue(): String? =
    this?.trim()?.takeIf { it.isNotBlank() && !looksLikeProviderDump(it) }

private fun String?.cleanCricketText(): String? =
    this?.replace(Regex("\\s+"), " ")?.trim()
        ?.takeIf { it.isNotBlank() && !looksLikeProviderDump(it) }

private fun looksLikeProviderDump(text: String): Boolean {
    val lower = text.lowercase()
    return lower.length > 120 ||
        lower.count { it == '•' } > 1 ||
        (lower.contains("senwes park") && lower.contains("need")) ||
        (lower.contains("live score") && lower.contains("scorecard"))
}
