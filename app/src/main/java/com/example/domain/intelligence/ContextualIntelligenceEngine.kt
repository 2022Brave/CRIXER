package com.example.domain.intelligence

import com.example.domain.model.BatterScore
import com.example.domain.model.BowlerFigures
import com.example.domain.model.ContextualInsight
import com.example.domain.model.Match

/**
 * Contextual intelligence must be derived from verified match data.
 * Historical milestones are intentionally suppressed until a verified
 * historical provider is connected; never infer or hard-code career totals.
 */
object ContextualIntelligenceEngine {

    fun evaluateBatterInsight(batter: BatterScore, match: Match): ContextualInsight? {
        // This is safe because it uses only the verified current innings figure.
        if (batter.runs in 45..49) {
            val needed = 50 - batter.runs
            return ContextualInsight(
                id = "ins-${batter.id}-50",
                playerId = batter.id,
                playerName = batter.name,
                title = "Needs $needed runs",
                milestoneTarget = 50,
                currentProgress = batter.runs,
                description = "for a half-century",
                verifiedSource = "Current match scorecard"
            )
        }
        return null
    }

    fun evaluateBowlerInsight(bowler: BowlerFigures, match: Match): ContextualInsight? {
        // Historical/tournament records require a verified historical source.
        return null
    }
}
