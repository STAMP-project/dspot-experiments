package com.blankj.utilcode.util;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/16
 *     desc  : test RegexUtils
 * </pre>
 */
public class RegexUtilsTest extends BaseTest {
    @Test
    public void isMobileSimple() {
        Assert.assertTrue(RegexUtils.isMobileSimple("11111111111"));
    }

    @Test
    public void isMobileExact() {
        Assert.assertFalse(RegexUtils.isMobileExact("11111111111"));
        Assert.assertTrue(RegexUtils.isMobileExact("13888880000"));
    }

    @Test
    public void isTel() {
        Assert.assertTrue(RegexUtils.isTel("033-88888888"));
        Assert.assertTrue(RegexUtils.isTel("033-7777777"));
        Assert.assertTrue(RegexUtils.isTel("0444-88888888"));
        Assert.assertTrue(RegexUtils.isTel("0444-7777777"));
        Assert.assertTrue(RegexUtils.isTel("033 88888888"));
        Assert.assertTrue(RegexUtils.isTel("033 7777777"));
        Assert.assertTrue(RegexUtils.isTel("0444 88888888"));
        Assert.assertTrue(RegexUtils.isTel("0444 7777777"));
        Assert.assertTrue(RegexUtils.isTel("03388888888"));
        Assert.assertTrue(RegexUtils.isTel("0337777777"));
        Assert.assertTrue(RegexUtils.isTel("044488888888"));
        Assert.assertTrue(RegexUtils.isTel("04447777777"));
        Assert.assertFalse(RegexUtils.isTel("133-88888888"));
        Assert.assertFalse(RegexUtils.isTel("033-666666"));
        Assert.assertFalse(RegexUtils.isTel("0444-999999999"));
    }

    @Test
    public void isIDCard18() {
        Assert.assertTrue(RegexUtils.isIDCard18("33698418400112523x"));
        Assert.assertTrue(RegexUtils.isIDCard18("336984184001125233"));
        Assert.assertFalse(RegexUtils.isIDCard18("336984184021125233"));
    }

    @Test
    public void isIDCard18Exact() {
        Assert.assertFalse(RegexUtils.isIDCard18Exact("33698418400112523x"));
        Assert.assertTrue(RegexUtils.isIDCard18Exact("336984184001125233"));
        Assert.assertFalse(RegexUtils.isIDCard18Exact("336984184021125233"));
    }

    @Test
    public void isEmail() {
        Assert.assertTrue(RegexUtils.isEmail("blankj@qq.com"));
        Assert.assertFalse(RegexUtils.isEmail("blankj@qq"));
    }

    @Test
    public void isURL() {
        Assert.assertTrue(RegexUtils.isURL("http://blankj.com"));
        Assert.assertFalse(RegexUtils.isURL("https:blank"));
    }

    @Test
    public void isZh() {
        Assert.assertTrue(RegexUtils.isZh("?"));
        Assert.assertFalse(RegexUtils.isZh("wo"));
    }

    @Test
    public void isUsername() {
        Assert.assertTrue(RegexUtils.isUsername("??233333"));
        Assert.assertFalse(RegexUtils.isUsername("??"));
        Assert.assertFalse(RegexUtils.isUsername("??233333_"));
    }

    @Test
    public void isDate() {
        Assert.assertTrue(RegexUtils.isDate("2016-08-16"));
        Assert.assertTrue(RegexUtils.isDate("2016-02-29"));
        Assert.assertFalse(RegexUtils.isDate("2015-02-29"));
        Assert.assertFalse(RegexUtils.isDate("2016-8-16"));
    }

    @Test
    public void isIP() {
        Assert.assertTrue(RegexUtils.isIP("255.255.255.0"));
        Assert.assertFalse(RegexUtils.isIP("256.255.255.0"));
    }

    @Test
    public void isMatch() {
        Assert.assertTrue(RegexUtils.isMatch("\\d?", "1"));
        Assert.assertFalse(RegexUtils.isMatch("\\d?", "a"));
    }

    @Test
    public void getMatches() {
        // ??
        System.out.println(RegexUtils.getMatches("b.*j", "blankj blankj"));
        // ??
        System.out.println(RegexUtils.getMatches("b.*?j", "blankj blankj"));
    }

    @Test
    public void getSplits() {
        System.out.println(Arrays.asList(RegexUtils.getSplits("1 2 3", " ")));
    }

    @Test
    public void getReplaceFirst() {
        System.out.println(RegexUtils.getReplaceFirst("1 2 3", " ", ", "));
    }

    @Test
    public void getReplaceAll() {
        System.out.println(RegexUtils.getReplaceAll("1 2 3", " ", ", "));
    }
}

