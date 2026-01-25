package fr.celld.smartalarm.service.sensors

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

/**
 * Tests unitaires pour MotionMonitoringManager
 */
class MotionMonitoringManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: SharedPreferences

    @Before
    fun setup() {
        mockContext = mockk()
        mockSharedPreferences = mockk()
        every { mockContext.getSharedPreferences(any(), any()) } returns mockSharedPreferences
    }

    @Test
    fun testHasMotionInLastMinutes_NoMotionRecorded() {
        // No motion recorded (default value = 0)
        every { mockSharedPreferences.getLong(any(), any()) } returns 0L

        val hasMotion = MotionMonitoringManager.hasMotionInLastMinutes(mockContext, 5)

        Assertions.assertThat(hasMotion)
            .describedAs("Should return false when no motion is recorded")
            .isFalse()
    }

    @Test
    fun testHasMotionInLastMinutes_RecentMotion() {
        // Motion detected 2 minutes ago
        val twoMinutesAgo = System.currentTimeMillis() - (2 * 60 * 1000)
        every { mockSharedPreferences.getLong(any(), any()) } returns twoMinutesAgo

        val hasMotion = MotionMonitoringManager.hasMotionInLastMinutes(mockContext, 5)

        Assertions.assertThat(hasMotion)
            .describedAs("Should return true for motion in the last 5 minutes")
            .isTrue()
    }

    @Test
    fun testHasMotionInLastMinutes_OldMotion() {
        // Motion detected 10 minutes ago
        val tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000)
        every { mockSharedPreferences.getLong(any(), any()) } returns tenMinutesAgo

        val hasMotion = MotionMonitoringManager.hasMotionInLastMinutes(mockContext, 5)

        Assertions.assertThat(hasMotion)
            .describedAs("Should return false for motion more than 5 minutes ago")
            .isFalse()
    }

    @Test
    fun testHasMotionInLastMinutes_ExactlyAtThreshold() {
        // Motion detected exactly 5 minutes minus 1 second ago
        val fiveMinutesAgo = System.currentTimeMillis() - (4 * 60 * 1000) + 59 * 1000
        every { mockSharedPreferences.getLong(any(), any()) } returns fiveMinutesAgo

        val hasMotion = MotionMonitoringManager.hasMotionInLastMinutes(mockContext, 5)

        Assertions.assertThat(hasMotion)
            .describedAs("Should return true for motion exactly at the threshold")
            .isTrue()
    }

    @Test
    fun testHasMotionInLastMinutes_DifferentTimeWindows() {
        // Motion detected 4 minutes ago
        val fourMinutesAgo = System.currentTimeMillis() - (4 * 60 * 1000)
        every { mockSharedPreferences.getLong(any(), any()) } returns fourMinutesAgo

        // Check with 3 minutes window (should be false)
        val hasMotion3Min = MotionMonitoringManager.hasMotionInLastMinutes(mockContext, 3)
        Assertions.assertThat(hasMotion3Min)
            .describedAs("Should return false with 3 minutes window")
            .isFalse()

        // Check with 5 minutes window (should be true)
        val hasMotion5Min = MotionMonitoringManager.hasMotionInLastMinutes(mockContext, 5)
        Assertions.assertThat(hasMotion5Min)
            .describedAs("Should return true with 5 minutes window")
            .isTrue()
    }
}