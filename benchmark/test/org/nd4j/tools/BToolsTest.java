package org.nd4j.tools;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author clavvis
 */
public class BToolsTest {
    // 
    @Test
    public void testgetMtLvESS() throws Exception {
        // 
        Assert.assertEquals("?", BTools.getMtLvESS((-5)));
        Assert.assertEquals("", BTools.getMtLvESS(0));
        Assert.assertEquals("...", BTools.getMtLvESS(3));
        // 
    }

    @Test
    public void testgetMtLvISS() throws Exception {
        // 
        Assert.assertEquals(" ", BTools.getMtLvISS());
        // 
    }

    @Test
    public void testgetSpaces() throws Exception {
        // 
        Assert.assertEquals("?", BTools.getSpaces((-3)));
        Assert.assertEquals("", BTools.getSpaces(0));
        Assert.assertEquals("    ", BTools.getSpaces(4));
        // 
    }

    @Test
    public void testgetSBln() throws Exception {
        // 
        Assert.assertEquals("?", BTools.getSBln());
        Assert.assertEquals("?", BTools.getSBln(null));
        Assert.assertEquals("T", BTools.getSBln(true));
        Assert.assertEquals("F", BTools.getSBln(false));
        Assert.assertEquals("TFFT", BTools.getSBln(true, false, false, true));
        Assert.assertEquals("FTFFT", BTools.getSBln(false, true, false, false, true));
        // 
    }

    @Test
    public void testgetSDbl() throws Exception {
        // 
        Assert.assertEquals("NaN", BTools.getSDbl(Double.NaN, 0));
        Assert.assertEquals("-6", BTools.getSDbl((-5.5), 0));
        Assert.assertEquals("-5.50", BTools.getSDbl((-5.5), 2));
        Assert.assertEquals("-5.30", BTools.getSDbl((-5.3), 2));
        Assert.assertEquals("-5", BTools.getSDbl((-5.3), 0));
        Assert.assertEquals("0.00", BTools.getSDbl(0.0, 2));
        Assert.assertEquals("0", BTools.getSDbl(0.0, 0));
        Assert.assertEquals("0.30", BTools.getSDbl(0.3, 2));
        Assert.assertEquals("4.50", BTools.getSDbl(4.5, 2));
        Assert.assertEquals("4", BTools.getSDbl(4.5, 0));
        Assert.assertEquals("6", BTools.getSDbl(5.5, 0));
        Assert.assertEquals("12 345 678", BTools.getSDbl(1.2345678E7, 0));
        // 
        Assert.assertEquals("-456", BTools.getSDbl((-456.0), 0, false));
        Assert.assertEquals("-456", BTools.getSDbl((-456.0), 0, true));
        Assert.assertEquals("+456", BTools.getSDbl(456.0, 0, true));
        Assert.assertEquals("456", BTools.getSDbl(456.0, 0, false));
        Assert.assertEquals(" 0", BTools.getSDbl(0.0, 0, true));
        Assert.assertEquals("0", BTools.getSDbl(0.0, 0, false));
        // 
        Assert.assertEquals("  4.50", BTools.getSDbl(4.5, 2, false, 6));
        Assert.assertEquals(" +4.50", BTools.getSDbl(4.5, 2, true, 6));
        Assert.assertEquals("   +456", BTools.getSDbl(456.0, 0, true, 7));
        Assert.assertEquals("    456", BTools.getSDbl(456.0, 0, false, 7));
        // 
    }

    @Test
    public void testgetSInt() throws Exception {
        // 
        Assert.assertEquals("23", BTools.getSInt(23, 1));
        Assert.assertEquals("23", BTools.getSInt(23, 2));
        Assert.assertEquals(" 23", BTools.getSInt(23, 3));
        // 
        Assert.assertEquals("0000056", BTools.getSInt(56, 7, '0'));
        // 
    }

    @Test
    public void testgetSIntA() throws Exception {
        // 
        Assert.assertEquals("?", BTools.getSIntA(null));
        Assert.assertEquals("?", BTools.getSIntA());
        Assert.assertEquals("0", BTools.getSIntA(0));
        Assert.assertEquals("5, 6, 7", BTools.getSIntA(5, 6, 7));
        int[] intA = new int[]{ 2, 3, 4, 5, 6 };
        Assert.assertEquals("2, 3, 4, 5, 6", BTools.getSIntA(intA));
        // 
    }

    @Test
    public void testgetIndexCharsCount() throws Exception {
        // 
        Assert.assertEquals(1, BTools.getIndexCharsCount((-5)));
        Assert.assertEquals(1, BTools.getIndexCharsCount(5));
        Assert.assertEquals(3, BTools.getIndexCharsCount(345));
        // 
    }

    @Test
    public void testgetSLcDtTm() throws Exception {
        // 
        Assert.assertEquals(15, BTools.getSLcDtTm().length());
        Assert.assertEquals("LDTm: ", BTools.getSLcDtTm().substring(0, 6));
        // 
    }
}

