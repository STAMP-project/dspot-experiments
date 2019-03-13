package com.amaze.filemanager.utils;


import org.junit.Assert;
import org.junit.Test;


public class UtilsTest {
    @Test
    public void testSanitizeInput() {
        // This function is sanitize the string. It removes ";","|","&&","..." from string.
        Assert.assertEquals("a", Utils.sanitizeInput("|a|"));// test the removing of pipe sign from string.

        Assert.assertEquals("a", Utils.sanitizeInput("...a..."));// test the removing of dots from string.

        Assert.assertEquals("a", Utils.sanitizeInput(";a;"));// test the removing of semicolon sign from string.

        Assert.assertEquals("a", Utils.sanitizeInput("&&a&&"));// test the removing of AMP sign from string.

        Assert.assertEquals("a", Utils.sanitizeInput("|a..."));// test the removing of pipe sign and semicolon sign from string.

        Assert.assertEquals("an apple", Utils.sanitizeInput("an &&apple"));// test the removing of AMP sign which are between two words.

        Assert.assertEquals("an apple", Utils.sanitizeInput("an ...apple"));// test the removing of dots which are between two words.

        Assert.assertEquals("an apple.", Utils.sanitizeInput(";an |apple...."));// test the removing of pipe sign and dots which are between two words. And test the fourth dot is not removed.

    }

    @Test
    public void testFormatTimer() {
        Assert.assertEquals("10:00", Utils.formatTimer(600));
        Assert.assertEquals("00:00", Utils.formatTimer(0));
        Assert.assertEquals("00:45", Utils.formatTimer(45));
        Assert.assertEquals("02:45", Utils.formatTimer(165));
        Assert.assertEquals("30:33", Utils.formatTimer(1833));
    }
}

