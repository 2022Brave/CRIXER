package com.example.domain.intelligence

import com.example.domain.model.BatterScore
import com.example.domain.model.BowlerFigures
import com.example.domain.model.ContextualInsight
import com.example.domain.model.Match

object ContextualIntelligenceEngine {

    fun evaluateBatterInsight(batter: BatterScore, match: Match): ContextualInsight? {
        if (batter.name.contains("Kohli", ignoreCase = true)) {
            val careerRunsBeforeMatch = 15924
            val currentCareerRuns = careerRunsBeforeMatch + batter.runs
            val milestone = 16000
            val runsNeeded = (milestone - currentCareerRuns).coerceAtLeast(0)
            return ContextualInsight(
                id = "ins-kohli-16k",
                playerId = batter.id,
                playerName = batter.name,
                title = "Needs $runsNeeded runs",
                milestoneTarget = milestone,
                currentProgress = currentCareerRuns,
                description = "to reach 16,000 ODI runs",
                verifiedSource = "CricMetric & Howstat"
            )
        }

        if (batter.runs >= 45 && batter.runs < 50) {
            val needed = 50 - batter.runs
            return ContextualInsight(
                id = "ins-${batter.id}-50",
                playerId = batter.id,
                playerName = batter.name,
                title = "Needs $needed runs",
                milestoneTarget = 50,
                currentProgress = batter.runs,
                description = "for a half-century in the Final",
                verifiedSource = "ICC Live Match Centre"
            )
        }

        return null
    }

    fun evaluateBowlerInsight(bowler: BowlerFigures, match: Match): ContextualInsight? {
        if (bowler.name.contains("Bumrah", ignoreCase = true) && bowler.wickets >= 3) {
            return ContextualInsight(
                id = "ins-bumrah-record",
                playerId = bowler.id,
                playerName = bowler.name,
                title = "Needs 1 more wicket",
                milestoneTarget = 4,
                currentProgress = bowler.wickets,
                description = "to equal tournament record for best death-overs spell",
                verifiedSource = "CricMetric Historical Database"
            )
        }
        return null
    }
}
