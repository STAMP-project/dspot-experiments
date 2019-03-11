package com.svgandroid;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Vlad Medvedev on 25.01.2016.
 * vladislav.medvedev@devfactory.com
 */
public class ParserHelperTest {
    @Test
    public void testNextFloat() {
        ParserHelper parserHelper = new ParserHelper("+0.1 +05.1 -10.2E-13 1.234e2 3.8E8 .7e3 -1.4e-45 3.4e+38f", 0);
        Assert.assertThat(parserHelper.parseFloat(), Is.is(0.1F));
        Assert.assertThat(parserHelper.nextFloat(), Is.is(5.1F));
        Assert.assertThat(parserHelper.nextFloat(), Is.is((-1.02E-12F)));
        Assert.assertThat(parserHelper.nextFloat(), Is.is(123.4F));
        Assert.assertThat(parserHelper.nextFloat(), Is.is(3.8E8F));
        Assert.assertThat(parserHelper.nextFloat(), Is.is(700.0F));
        Assert.assertThat(parserHelper.nextFloat(), Is.is((-1.4E-45F)));
        Assert.assertThat(parserHelper.nextFloat(), Is.is(3.4E38F));
    }

    @Test
    public void testParseFloat() {
        Assert.assertThat(new ParserHelper("0", 0).parseFloat(), Is.is(0.0F));
        Assert.assertThat(new ParserHelper("0.09", 0).parseFloat(), Is.is(0.09F));
        Assert.assertThat(new ParserHelper("0.0e3", 0).parseFloat(), Is.is(0.0F));
        Assert.assertThat(new ParserHelper("0.01", 0).parseFloat(), Is.is(0.01F));
        Assert.assertThat(new ParserHelper("foo", 0).parseFloat(), Is.is(Float.NaN));
    }

    @Test
    public void testParseFloat_UnexpectedCharAfterE() {
        testParseFloat_UnexpectedChar("10.2EA13");
    }

    @Test
    public void testParseFloat_UnexpectedCharAfterPlus() {
        testParseFloat_UnexpectedChar("10.2E+A");
    }

    @Test
    public void testParseFloat_UnexpectedCharAfterDot() {
        testParseFloat_UnexpectedChar(".e+3");
    }

    @Test
    public void testBuildFloat() {
        Assert.assertThat(ParserHelper.buildFloat(0, 0), Is.is(0.0F));
        Assert.assertThat(ParserHelper.buildFloat(1, 129), Is.is(Float.POSITIVE_INFINITY));
        Assert.assertThat(ParserHelper.buildFloat((-1), 128), Is.is(Float.NEGATIVE_INFINITY));
        Assert.assertThat(ParserHelper.buildFloat(1, 0), Is.is(1.0F));
        Assert.assertThat(ParserHelper.buildFloat(1, 2), Is.is(100.0F));
        Assert.assertThat(ParserHelper.buildFloat(100000000, 1), Is.is(1.0E9F));
    }
}

