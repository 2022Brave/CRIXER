package com.example.domain.validation

import com.example.domain.model.Innings
import com.example.domain.model.Match

sealed class ValidationStatus {
    object Verified : ValidationStatus()
    data class Warning(val discrepancies: List<String>) : ValidationStatus()
    data class Pending(val reason: String) : ValidationStatus()
}

data class MatchValidationResult(
    val matchId: String,
    val status: ValidationStatus,
    val confidencePercentage: Int,
    val lastVerifiedTimestamp: Long = System.currentTimeMillis()
)

object CricketDataValidator {

    fun validateMatch(match: Match): MatchValidationResult {
        val discrepancies = mutableListOf<String>()

        val currentInnings = if (match.currentInningsNumber == 1) match.innings1 else match.innings2
        if (currentInnings != null) {
            val inningsErrors = validateInnings(currentInnings)
            discrepancies.addAll(inningsErrors)
        }

        // A score without any verified ball/player detail is not enough to
        // call the match fully verified. UI must not invent the missing layer.
        if (match.status != com.example.domain.model.MatchStatus.UPCOMING &&
            match.innings1.runs == 0 && match.innings1.overs == 0f &&
            (match.innings2 == null || (match.innings2.runs == 0 && match.innings2.overs == 0f))
        ) {
            discrepancies.add("No verified score data received")
        }

        // Validate target and required runs if chasing in 2nd innings
        if (match.currentInningsNumber == 2 && match.innings2 != null) {
            val target = match.innings1.runs + 1
            if (match.targetRuns != null && match.targetRuns != target) {
                discrepancies.add("Target mismatch: expected $target but got ${match.targetRuns}")
            }

            if (match.requiredRuns != null) {
                val expectedReqRuns = target - match.innings2.runs
                if (match.requiredRuns != expectedReqRuns && expectedReqRuns >= 0) {
                    discrepancies.add("Required runs mismatch: expected $expectedReqRuns, got ${match.requiredRuns}")
                }
            }

            if (match.remainingBalls != null && match.innings2.overs > 0) {
                val completedBalls = (match.innings2.overs.toInt() * 6) + Math.round((match.innings2.overs % 1) * 10)
                val totalBallsInInnings = (match.innings2.maxOvers * 6).toInt()
                val expectedRemaining = (totalBallsInInnings - completedBalls).coerceAtLeast(0)
                if (Math.abs(expectedRemaining - match.remainingBalls) > 1) {
                    discrepancies.add("Remaining balls discrepancy: expected $expectedRemaining, got ${match.remainingBalls}")
                }
            }
        }

        return if (discrepancies.isEmpty()) {
            MatchValidationResult(match.id, ValidationStatus.Verified, 100)
        } else {
            val confidence = (100 - (discrepancies.size * 12)).coerceIn(50, 95)
            MatchValidationResult(match.id, ValidationStatus.Warning(discrepancies), confidence)
        }
    }

    private fun validateInnings(innings: Innings): List<String> {
        val errors = mutableListOf<String>()

        // 1. Check sum of batter runs + extras vs total innings runs
        if (innings.batters.isNotEmpty()) {
            val batterRunsTotal = innings.batters.sumOf { it.runs }
            val calculatedTotal = batterRunsTotal + innings.extras
            if (calculatedTotal != innings.runs) {
                errors.add("Total runs mismatch: batters ($batterRunsTotal) + extras (${innings.extras}) != innings total (${innings.runs})")
            }
        }

        // 2. Check wickets count vs fall of wickets
        if (innings.fallOfWickets.isNotEmpty() && innings.fallOfWickets.size != innings.wickets) {
            errors.add("Wicket count mismatch: fall of wickets count (${innings.fallOfWickets.size}) != wickets total (${innings.wickets})")
        }

        // 3. Wickets cannot exceed 10 in cricket
        if (innings.wickets > 10) {
            errors.add("Invalid wicket count: ${innings.wickets} cannot exceed 10")
        }

        // 4. Validate balls in over decimal (should be .0 to .5)
        val ballFraction = Math.round((innings.overs % 1) * 10)
        if (ballFraction > 5) {
            errors.add("Invalid over decimal notation: ${innings.overs} (ball fraction cannot exceed 5)")
        }

        return errors
    }
}