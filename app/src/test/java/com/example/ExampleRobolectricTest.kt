package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.provider.VerifiedCricketDataProvider
import com.example.domain.model.BallOutcome
import com.example.domain.validation.CricketDataValidator
import com.example.domain.validation.ValidationStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Crixer", appName)
    }

    @Test
    fun `cricket data provider returns verified matches`() = runTest {
        val provider = VerifiedCricketDataProvider()
        val liveMatches = provider.getLiveMatches()
        assertTrue(liveMatches.isNotEmpty())

        val indVsAus = liveMatches.first { it.id == "match-ind-aus-2026-final" }
        assertEquals("Asia Cup 2026 · Final", indVsAus.title)
        assertEquals("IND", indVsAus.team1.shortName)
        assertEquals("AUS", indVsAus.team2.shortName)

        val validation = CricketDataValidator.validateMatch(indVsAus)
        assertEquals(ValidationStatus.Verified, validation.status)
        assertEquals(100, validation.confidencePercentage)
    }
}
