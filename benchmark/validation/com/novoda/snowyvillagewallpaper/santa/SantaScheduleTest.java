package com.novoda.snowyvillagewallpaper.santa;


import java.util.Calendar;
import org.junit.Test;
import org.mockito.Mock;


public class SantaScheduleTest {
    @Mock
    private Clock mockClock;

    @Mock
    private RoundDelay mockRoundDelay;

    private SantaSchedule santaSchedule;

    @Test
    public void givenCurrentTimeIsBeforeChristmas_WhenCheckingIfSantaIsInTown_ThenReturnFalse() {
        givenTheCurrentDateIs(2015, Calendar.DECEMBER, 23);
        santaSchedule.calculateNextVisitTime();
        boolean isSantaInTown = santaSchedule.isSantaInTown();
        assertThat(isSantaInTown).isFalse();
    }

    @Test
    public void givenCurrentTimeIsDuringChristmas_WhenCheckingIfSantaIsInTown_ThenReturnTrue() {
        givenTheCurrentDateIs(2015, Calendar.DECEMBER, 25);
        santaSchedule.calculateNextVisitTime();
        boolean isSantaInTown = santaSchedule.isSantaInTown();
        assertThat(isSantaInTown).isTrue();
    }

    @Test
    public void givenCurrentTimeIsAfterChristmas_WhenCheckingIfSantaIsInTown_ThenReturnFalse() {
        givenTheCurrentDateIs(2015, Calendar.DECEMBER, 26);
        santaSchedule.calculateNextVisitTime();
        boolean isSantaInTown = santaSchedule.isSantaInTown();
        assertThat(isSantaInTown).isFalse();
    }
}

