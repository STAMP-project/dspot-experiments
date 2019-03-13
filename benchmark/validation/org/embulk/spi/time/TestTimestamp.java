package org.embulk.spi.time;


import org.junit.Assert;
import org.junit.Test;


public class TestTimestamp {
    @Test
    public void testEqualsToTimestamp() {
        assertEqualsMethods(Timestamp.ofEpochSecond(0), Timestamp.ofEpochSecond(0));
        assertEqualsMethods(Timestamp.ofEpochSecond(10), Timestamp.ofEpochSecond(10));
        assertEqualsMethods(Timestamp.ofEpochSecond(10, 2), Timestamp.ofEpochSecond(10, 2));
    }

    @Test
    public void testNotEqualsToTimestamp() {
        Assert.assertFalse(Timestamp.ofEpochSecond(0).equals(Timestamp.ofEpochSecond(1)));
        Assert.assertFalse(Timestamp.ofEpochSecond(10).equals(Timestamp.ofEpochSecond(10, 2)));
        Assert.assertFalse(Timestamp.ofEpochSecond(10, 2).equals(Timestamp.ofEpochSecond(20, 2)));
    }

    @Test
    public void testEqualsToNull() {
        Assert.assertFalse(Timestamp.ofEpochSecond(0).equals(null));
        Assert.assertFalse(Timestamp.ofEpochSecond(1, 2).equals(null));
    }

    @Test
    public void testEqualsOtherClass() {
        Assert.assertFalse(Timestamp.ofEpochSecond(0).equals(new Object()));
        Assert.assertFalse(Timestamp.ofEpochSecond(1, 2).equals("other"));
    }

    @Test
    public void testAdjustMillisToNanos() {
        Timestamp t = Timestamp.ofEpochMilli(3);// 3 msec = 3_000 usec == 3_000_000 nsec

        Assert.assertEquals(0L, t.getEpochSecond());
        Assert.assertEquals(3000000, t.getNano());
    }

    @Test
    public void testAdjustMillisToSeconds() {
        Timestamp t = Timestamp.ofEpochMilli(3000);// 3_000 msec = 3 sec

        Assert.assertEquals(3L, t.getEpochSecond());
        Assert.assertEquals(0, t.getNano());
    }

    @Test
    public void testAdjustNano() {
        Timestamp t = Timestamp.ofEpochSecond(0, 1000000000);// 1_000_000_000 nsec = 1_000_000 usec = 1_000 msec = 1 sec

        Assert.assertEquals(1L, t.getEpochSecond());
        Assert.assertEquals(0, t.getNano());
    }

    @Test
    public void testCompareTo() {
        Assert.assertTrue(((Timestamp.ofEpochSecond(3).compareTo(Timestamp.ofEpochSecond(4))) < 0));
        Assert.assertTrue(((Timestamp.ofEpochSecond(3).compareTo(Timestamp.ofEpochSecond(3, 4))) < 0));
        Assert.assertTrue(((Timestamp.ofEpochSecond(4).compareTo(Timestamp.ofEpochSecond(3))) > 0));
        Assert.assertTrue(((Timestamp.ofEpochSecond(3, 4).compareTo(Timestamp.ofEpochSecond(3))) > 0));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("1970-01-01 00:00:00 UTC", Timestamp.ofEpochSecond(0).toString());
        Assert.assertEquals("2015-01-19 07:36:10 UTC", Timestamp.ofEpochSecond(1421652970).toString());
        Assert.assertEquals("2015-01-19 07:36:10.100 UTC", Timestamp.ofEpochSecond(1421652970, ((100 * 1000) * 1000)).toString());
        Assert.assertEquals("2015-01-19 07:36:10.120 UTC", Timestamp.ofEpochSecond(1421652970, ((120 * 1000) * 1000)).toString());
        Assert.assertEquals("2015-01-19 07:36:10.123 UTC", Timestamp.ofEpochSecond(1421652970, ((123 * 1000) * 1000)).toString());
        Assert.assertEquals("2015-01-19 07:36:10.123400 UTC", Timestamp.ofEpochSecond(1421652970, (123400 * 1000)).toString());
        Assert.assertEquals("2015-01-19 07:36:10.123450 UTC", Timestamp.ofEpochSecond(1421652970, (123450 * 1000)).toString());
        Assert.assertEquals("2015-01-19 07:36:10.123456 UTC", Timestamp.ofEpochSecond(1421652970, (123456 * 1000)).toString());
        Assert.assertEquals("2015-01-19 07:36:10.123456700 UTC", Timestamp.ofEpochSecond(1421652970, 123456700).toString());
        Assert.assertEquals("2015-01-19 07:36:10.123456780 UTC", Timestamp.ofEpochSecond(1421652970, 123456780).toString());
        Assert.assertEquals("2015-01-19 07:36:10.123456789 UTC", Timestamp.ofEpochSecond(1421652970, 123456789).toString());
    }
}

