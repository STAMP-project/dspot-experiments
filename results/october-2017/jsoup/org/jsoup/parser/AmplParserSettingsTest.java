package org.jsoup.parser;


public class AmplParserSettingsTest {
    @org.junit.Test
    public void caseSupport() {
        org.jsoup.parser.ParseSettings bothOn = new org.jsoup.parser.ParseSettings(true, true);
        org.jsoup.parser.ParseSettings bothOff = new org.jsoup.parser.ParseSettings(false, false);
        org.jsoup.parser.ParseSettings tagOn = new org.jsoup.parser.ParseSettings(true, false);
        org.jsoup.parser.ParseSettings attrOn = new org.jsoup.parser.ParseSettings(false, true);
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", bothOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", bothOff.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("FOO", tagOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("foo", tagOn.normalizeAttribute("FOO"));
        org.junit.Assert.assertEquals("foo", attrOn.normalizeTag("FOO"));
        org.junit.Assert.assertEquals("FOO", attrOn.normalizeAttribute("FOO"));
    }
}

