package de.danoeh.antennapod.core.util;


import org.junit.Assert;
import org.junit.Test;

import static RewindAfterPauseUtils.ELAPSED_TIME_FOR_LONG_REWIND;
import static RewindAfterPauseUtils.ELAPSED_TIME_FOR_MEDIUM_REWIND;
import static RewindAfterPauseUtils.ELAPSED_TIME_FOR_SHORT_REWIND;
import static RewindAfterPauseUtils.LONG_REWIND;
import static RewindAfterPauseUtils.MEDIUM_REWIND;
import static RewindAfterPauseUtils.SHORT_REWIND;


/**
 * Tests for {@link RewindAfterPauseUtils}.
 */
public class RewindAfterPauseUtilTest {
    @Test
    public void testCalculatePositionWithRewindNoRewind() {
        final int ORIGINAL_POSITION = 10000;
        long lastPlayed = System.currentTimeMillis();
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);
        Assert.assertEquals(ORIGINAL_POSITION, position);
    }

    @Test
    public void testCalculatePositionWithRewindSmallRewind() {
        final int ORIGINAL_POSITION = 10000;
        long lastPlayed = ((System.currentTimeMillis()) - (ELAPSED_TIME_FOR_SHORT_REWIND)) - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);
        Assert.assertEquals((ORIGINAL_POSITION - (SHORT_REWIND)), position);
    }

    @Test
    public void testCalculatePositionWithRewindMediumRewind() {
        final int ORIGINAL_POSITION = 10000;
        long lastPlayed = ((System.currentTimeMillis()) - (ELAPSED_TIME_FOR_MEDIUM_REWIND)) - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);
        Assert.assertEquals((ORIGINAL_POSITION - (MEDIUM_REWIND)), position);
    }

    @Test
    public void testCalculatePositionWithRewindLongRewind() {
        final int ORIGINAL_POSITION = 30000;
        long lastPlayed = ((System.currentTimeMillis()) - (ELAPSED_TIME_FOR_LONG_REWIND)) - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);
        Assert.assertEquals((ORIGINAL_POSITION - (LONG_REWIND)), position);
    }

    @Test
    public void testCalculatePositionWithRewindNegativeNumber() {
        final int ORIGINAL_POSITION = 100;
        long lastPlayed = ((System.currentTimeMillis()) - (ELAPSED_TIME_FOR_LONG_REWIND)) - 1000;
        int position = RewindAfterPauseUtils.calculatePositionWithRewind(ORIGINAL_POSITION, lastPlayed);
        Assert.assertEquals(0, position);
    }
}

