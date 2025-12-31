package com.example.simulatorservice

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SimulatorServiceTest {

    private lateinit var service: SimulatorService
    private lateinit var binder: ISimulatorInterface

    @Before
    fun setup() {
       // val context = InstrumentationRegistry.getInstrumentation().targetContext
        service = SimulatorService()
       // service.attachBaseContext(context)
        service.onCreate()

        binder = service.onBind(Intent()) as ISimulatorInterface
    }

    @Test
    fun testBinderReturnsSpeed() {
        service.serviceSpeed = 60.5f
        Assert.assertEquals(60.5f, binder.speed, 0.01f)
    }

    @Test
    fun testBinderReturnsDistance() {
        service.serviceDistance = 12.3f
        Assert.assertEquals(12.3f, binder.distance, 0.01f)
    }

    @Test
    fun testTimeExtraction() {
        service.serviceTime = "12:30:45"
        Assert.assertEquals("12:30:45", binder.time)
    }

    @Test
    fun testNightModeReturnsTrue() {
        service.serviceMode = "Night"
        Assert.assertTrue(binder.mode)
    }

    @Test
    fun testDayModeReturnsFalse() {
        service.serviceMode = "Day"
        Assert.assertFalse(binder.mode)
    }

    @Test
    fun testUnitValue() {
        service.serviceUnit = "mph"
        Assert.assertEquals("mph", binder.unit)
    }

    @Test
    fun testTotalDistance() {
        service.serviceTotalDistance = 120.7f
        Assert.assertEquals(120.7f, binder.totalDistance, 0.01f)
    }

    @Test
    fun testTotalTime() {
        service.serviceTotalTime = 3600f
        Assert.assertEquals(3600f, binder.totalTime, 0.01f)
    }
}