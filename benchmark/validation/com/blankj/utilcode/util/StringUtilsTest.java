package com.blankj.utilcode.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/16
 *     desc  : test StringUtils
 * </pre>
 */
public class StringUtilsTest extends BaseTest {
    @Test
    public void isEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertFalse(StringUtils.isEmpty(" "));
    }

    @Test
    public void isTrimEmpty() {
        Assert.assertTrue(StringUtils.isTrimEmpty(""));
        Assert.assertTrue(StringUtils.isTrimEmpty(null));
        Assert.assertTrue(StringUtils.isTrimEmpty(" "));
    }

    @Test
    public void isSpace() {
        Assert.assertTrue(StringUtils.isSpace(""));
        Assert.assertTrue(StringUtils.isSpace(null));
        Assert.assertTrue(StringUtils.isSpace(" "));
        Assert.assertTrue(StringUtils.isSpace("\u3000 \n\t\r"));
    }

    @Test
    public void equals() {
        Assert.assertTrue(StringUtils.equals(null, null));
        Assert.assertTrue(StringUtils.equals("blankj", "blankj"));
        Assert.assertFalse(StringUtils.equals("blankj", "Blankj"));
    }

    @Test
    public void equalsIgnoreCase() {
        Assert.assertTrue(StringUtils.equalsIgnoreCase(null, null));
        Assert.assertFalse(StringUtils.equalsIgnoreCase(null, "blankj"));
        Assert.assertTrue(StringUtils.equalsIgnoreCase("blankj", "Blankj"));
        Assert.assertTrue(StringUtils.equalsIgnoreCase("blankj", "blankj"));
        Assert.assertFalse(StringUtils.equalsIgnoreCase("blankj", "blank"));
    }

    @Test
    public void null2Length0() {
        Assert.assertEquals("", StringUtils.null2Length0(null));
    }

    @Test
    public void length() {
        Assert.assertEquals(0, StringUtils.length(null));
        Assert.assertEquals(0, StringUtils.length(""));
        Assert.assertEquals(6, StringUtils.length("blankj"));
    }

    @Test
    public void upperFirstLetter() {
        Assert.assertEquals("Blankj", StringUtils.upperFirstLetter("blankj"));
        Assert.assertEquals("Blankj", StringUtils.upperFirstLetter("Blankj"));
        Assert.assertEquals("1Blankj", StringUtils.upperFirstLetter("1Blankj"));
    }

    @Test
    public void lowerFirstLetter() {
        Assert.assertEquals("blankj", StringUtils.lowerFirstLetter("blankj"));
        Assert.assertEquals("blankj", StringUtils.lowerFirstLetter("Blankj"));
        Assert.assertEquals("1blankj", StringUtils.lowerFirstLetter("1blankj"));
    }

    @Test
    public void reverse() {
        Assert.assertEquals("jknalb", StringUtils.reverse("blankj"));
        Assert.assertEquals("knalb", StringUtils.reverse("blank"));
        Assert.assertEquals("????", StringUtils.reverse("????"));
        Assert.assertEquals("", StringUtils.reverse(null));
    }

    @Test
    public void toDBC() {
        Assert.assertEquals(" ,.&", StringUtils.toDBC("????"));
    }

    @Test
    public void toSBC() {
        Assert.assertEquals("????", StringUtils.toSBC(" ,.&"));
    }
}

