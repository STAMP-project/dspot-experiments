package com.baeldung.java.clock;


import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClockUnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClockUnitTest.class);

    @Test
    public void givenClock_withSytemUTC_retrievesInstant() {
        Clock clockUTC = Clock.systemUTC();
        Assert.assertEquals(clockUTC.getZone(), ZoneOffset.UTC);
        Assert.assertEquals(clockUTC.instant().equals(null), false);
        ClockUnitTest.LOGGER.debug(("UTC instant :: " + (clockUTC.instant())));
    }

    @Test
    public void givenClock_withSytem_retrievesInstant() {
        Clock clockSystem = Clock.system(ZoneId.of("Asia/Calcutta"));
        Assert.assertEquals(clockSystem.getZone(), ZoneId.of("Asia/Calcutta"));
        Assert.assertEquals(clockSystem.instant().equals(null), false);
        ClockUnitTest.LOGGER.debug(("System zone :: " + (clockSystem.getZone())));
    }

    @Test
    public void givenClock_withSytemDefaultZone_retrievesInstant() {
        Clock clockSystemDefault = Clock.systemDefaultZone();
        Assert.assertEquals(clockSystemDefault.getZone().equals(null), false);
        Assert.assertEquals(clockSystemDefault.instant().equals(null), false);
        ClockUnitTest.LOGGER.debug(("System Default instant :: " + (clockSystemDefault.instant())));
    }

    @Test
    public void givenClock_withSytemUTC_retrievesTimeInMillis() {
        Clock clockMillis = Clock.systemDefaultZone();
        Assert.assertEquals(clockMillis.instant().equals(null), false);
        Assert.assertTrue(((clockMillis.millis()) > 0));
        ClockUnitTest.LOGGER.debug(("System Default millis :: " + (clockMillis.millis())));
    }

    @Test
    public void givenClock_usingOffset_retrievesFutureDate() {
        Clock baseClock = Clock.systemDefaultZone();
        // result clock will be later than baseClock
        Clock futureClock = Clock.offset(baseClock, Duration.ofHours(72));
        Assert.assertEquals(futureClock.instant().equals(null), false);
        Assert.assertTrue(((futureClock.millis()) > (baseClock.millis())));
        ClockUnitTest.LOGGER.debug(("Future Clock instant :: " + (futureClock.instant())));
    }

    @Test
    public void givenClock_usingOffset_retrievesPastDate() {
        Clock baseClock = Clock.systemDefaultZone();
        // result clock will be later than baseClock
        Clock pastClock = Clock.offset(baseClock, Duration.ofHours((-72)));
        Assert.assertEquals(pastClock.instant().equals(null), false);
        Assert.assertTrue(((pastClock.millis()) < (baseClock.millis())));
        ClockUnitTest.LOGGER.debug(("Past Clock instant :: " + (pastClock.instant())));
    }

    @Test
    public void givenClock_usingTick_retrievesInstant() {
        Clock clockDefaultZone = Clock.systemDefaultZone();
        Clock clocktick = Clock.tick(clockDefaultZone, Duration.ofSeconds(300));
        Assert.assertEquals(clockDefaultZone.instant().equals(null), false);
        Assert.assertEquals(clocktick.instant().equals(null), false);
        Assert.assertTrue(((clockDefaultZone.millis()) > (clocktick.millis())));
        ClockUnitTest.LOGGER.debug(("Clock Default Zone instant : " + (clockDefaultZone.instant())));
        ClockUnitTest.LOGGER.debug(("Clock tick instant: " + (clocktick.instant())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenClock_usingTickDurationNegative_throwsException() {
        Clock clockDefaultZone = Clock.systemDefaultZone();
        Clock.tick(clockDefaultZone, Duration.ofSeconds((-300)));
    }

    @Test
    public void givenClock_usingTickSeconds_retrievesInstant() {
        ZoneId zoneId = ZoneId.of("Asia/Calcutta");
        Clock tickSeconds = Clock.tickSeconds(zoneId);
        Assert.assertEquals(tickSeconds.instant().equals(null), false);
        ClockUnitTest.LOGGER.debug(("Clock tick seconds instant :: " + (tickSeconds.instant())));
        tickSeconds = Clock.tick(Clock.system(ZoneId.of("Asia/Calcutta")), Duration.ofSeconds(100));
        Assert.assertEquals(tickSeconds.instant().equals(null), false);
    }

    @Test
    public void givenClock_usingTickMinutes_retrievesInstant() {
        Clock tickMinutes = Clock.tickMinutes(ZoneId.of("Asia/Calcutta"));
        Assert.assertEquals(tickMinutes.instant().equals(null), false);
        ClockUnitTest.LOGGER.debug(("Clock tick seconds instant :: " + (tickMinutes.instant())));
        tickMinutes = Clock.tick(Clock.system(ZoneId.of("Asia/Calcutta")), Duration.ofMinutes(5));
        Assert.assertEquals(tickMinutes.instant().equals(null), false);
    }

    @Test
    public void givenClock_usingWithZone_retrievesInstant() {
        ZoneId zoneSingapore = ZoneId.of("Asia/Singapore");
        Clock clockSingapore = Clock.system(zoneSingapore);
        Assert.assertEquals(clockSingapore.instant().equals(null), false);
        ClockUnitTest.LOGGER.debug(("clockSingapore instant : " + (clockSingapore.instant())));
        ZoneId zoneCalcutta = ZoneId.of("Asia/Calcutta");
        Clock clockCalcutta = clockSingapore.withZone(zoneCalcutta);
        Assert.assertEquals(clockCalcutta.instant().equals(null), false);
        ClockUnitTest.LOGGER.debug(("clockCalcutta instant : " + (clockSingapore.instant())));
    }

    @Test
    public void givenClock_usingGetZone_retrievesZoneId() {
        Clock clockDefaultZone = Clock.systemDefaultZone();
        ZoneId zone = clockDefaultZone.getZone();
        Assert.assertEquals(zone.getId().equals(null), false);
        ClockUnitTest.LOGGER.debug(("Default zone instant : " + (clockDefaultZone.instant())));
    }
}

