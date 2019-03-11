package edu.umd.cs.findbugs.workflow;


import Filter.FilterCommandLine;
import edu.umd.cs.findbugs.AppVersion;
import java.util.Map;
import java.util.SortedMap;
import org.junit.Assert;
import org.junit.Test;


public class FindSeqNumTest {
    Map<String, AppVersion> versionNames;

    SortedMap<Long, AppVersion> timeStamps;

    @Test
    public void test0() {
        Assert.assertEquals(0, FilterCommandLine.getVersionNum(versionNames, timeStamps, "0", true, 3));
    }

    @Test
    public void testminusOne() {
        Assert.assertEquals(3, FilterCommandLine.getVersionNum(versionNames, timeStamps, "-1", true, 3));
    }

    @Test
    public void testminusTwo() {
        Assert.assertEquals(2, FilterCommandLine.getVersionNum(versionNames, timeStamps, "-2", true, 3));
    }

    @Test
    public void testLast() {
        Assert.assertEquals(3, FilterCommandLine.getVersionNum(versionNames, timeStamps, "last", true, 3));
    }

    @Test
    public void testlastVersion() {
        Assert.assertEquals(3, FilterCommandLine.getVersionNum(versionNames, timeStamps, "lastVersion", true, 3));
    }

    @Test
    public void test1() {
        Assert.assertEquals(1, FilterCommandLine.getVersionNum(versionNames, timeStamps, "1", true, 3));
    }

    @Test
    public void testV1_0() {
        Assert.assertEquals(0, FilterCommandLine.getVersionNum(versionNames, timeStamps, "v1.0", true, 3));
    }

    @Test
    public void testV1_1() {
        Assert.assertEquals(1, FilterCommandLine.getVersionNum(versionNames, timeStamps, "v1.1", true, 3));
    }

    @Test
    public void testV2_0() {
        Assert.assertEquals(2, FilterCommandLine.getVersionNum(versionNames, timeStamps, "v2.0", true, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testV2_1() {
        FilterCommandLine.getVersionNum(versionNames, timeStamps, "v2.1", true, 0);
        Assert.fail("Should have rejected version number 2.1");
    }

    @Test
    public void testAfterMay5() {
        Assert.assertEquals(0, FilterCommandLine.getVersionNum(versionNames, timeStamps, "5/5/2005", true, 3));
    }

    @Test
    public void testAfterJune5() {
        Assert.assertEquals(1, FilterCommandLine.getVersionNum(versionNames, timeStamps, "6/5/2005", true, 3));
    }

    @Test
    public void testAfterJune15() {
        Assert.assertEquals(2, FilterCommandLine.getVersionNum(versionNames, timeStamps, "June 15, 2005", true, 3));
    }

    @Test
    public void testAfterJune25() {
        Assert.assertEquals(Long.MAX_VALUE, FilterCommandLine.getVersionNum(versionNames, timeStamps, "June 25, 2005", true, 3));
    }

    @Test
    public void testBeforeMay5() {
        Assert.assertEquals(Long.MIN_VALUE, FilterCommandLine.getVersionNum(versionNames, timeStamps, "5/5/2005", false, 3));
    }

    @Test
    public void testBeforeJune5() {
        Assert.assertEquals(0, FilterCommandLine.getVersionNum(versionNames, timeStamps, "6/5/2005", false, 3));
    }

    @Test
    public void testBeforeJune15() {
        Assert.assertEquals(1, FilterCommandLine.getVersionNum(versionNames, timeStamps, "June 15, 2005", false, 3));
    }

    @Test
    public void testBeforeJune25() {
        Assert.assertEquals(2, FilterCommandLine.getVersionNum(versionNames, timeStamps, "June 25, 2005", false, 3));
    }
}

