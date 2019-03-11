package de.danoeh.antennapod.core.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for converter
 */
public class ConverterTest {
    @Test
    public void testGetDurationStringLong() {
        String expected = "13:05:10";
        int input = 47110000;
        Assert.assertEquals(expected, Converter.getDurationStringLong(input));
    }

    @Test
    public void testGetDurationStringShort() {
        String expected = "13:05";
        Assert.assertEquals(expected, Converter.getDurationStringShort(47110000, true));
        Assert.assertEquals(expected, Converter.getDurationStringShort(785000, false));
    }

    @Test
    public void testDurationStringLongToMs() {
        String input = "01:20:30";
        long expected = 4830000;
        Assert.assertEquals(expected, Converter.durationStringLongToMs(input));
    }

    @Test
    public void testDurationStringShortToMs() {
        String input = "8:30";
        Assert.assertEquals(30600000, Converter.durationStringShortToMs(input, true));
        Assert.assertEquals(510000, Converter.durationStringShortToMs(input, false));
    }
}

