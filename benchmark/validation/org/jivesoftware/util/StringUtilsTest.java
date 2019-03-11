package org.jivesoftware.util;


import JiveConstants.DAY;
import JiveConstants.HOUR;
import JiveConstants.MINUTE;
import JiveConstants.SECOND;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static JiveConstants.DAY;
import static JiveConstants.HOUR;
import static JiveConstants.MINUTE;
import static JiveConstants.SECOND;


public class StringUtilsTest {
    @Test
    public void testValidDomainNames() {
        assertValidDomainName("www.mycompany.com");
        assertValidDomainName("www.my-company.com");
        assertValidDomainName("abc.de");
        assertValidDomainName("tron?on.be", "xn--tronon-zua.be");
        assertValidDomainName("?bb.at", "xn--bb-eka.at");
    }

    @Test
    public void testInvalidDomainNames() {
        assertInvalidDomainName("www.my_company.com", "Contains non-LDH characters");
        assertInvalidDomainName("www.-dash.com", "Has leading or trailing hyphen");
        assertInvalidDomainName("www.dash-.com", "Has leading or trailing hyphen");
        assertInvalidDomainName("abc.<test>.de", "Contains non-LDH characters");
    }

    @Test
    public void testStringReplace() {
        Assert.assertEquals(StringUtils.replace("Hello Foo Foo", "Foo", "World"), "Hello World World");
        Assert.assertEquals(StringUtils.replace("Hello Foo foo", "Foo", "World"), "Hello World foo");
        Assert.assertEquals(StringUtils.replaceIgnoreCase("Hello Foo foo", "Foo", "World"), "Hello World World");
        int[] count = new int[1];
        Assert.assertEquals(StringUtils.replaceIgnoreCase("Hello Foo foo", "Foo", "World", count), "Hello World World");
        Assert.assertEquals(count[0], 2);
    }

    @Test
    public void testElapsedTimeInMilliseconds() throws Exception {
        Assert.assertThat(StringUtils.getFullElapsedTime(0), CoreMatchers.is("0 ms"));
        Assert.assertThat(StringUtils.getFullElapsedTime(1), CoreMatchers.is("1 ms"));
        Assert.assertThat(StringUtils.getFullElapsedTime(250), CoreMatchers.is("250 ms"));
    }

    @Test
    public void testElapsedTimeInSeconds() throws Exception {
        Assert.assertThat(StringUtils.getFullElapsedTime(SECOND), CoreMatchers.is("1 second"));
        Assert.assertThat(StringUtils.getFullElapsedTime(((SECOND) + 1)), CoreMatchers.is("1 second, 1 ms"));
        Assert.assertThat(StringUtils.getFullElapsedTime((((SECOND) * 30) + 30)), CoreMatchers.is("30 seconds, 30 ms"));
    }

    @Test
    public void testElapsedTimeInMinutes() throws Exception {
        Assert.assertThat(StringUtils.getFullElapsedTime(MINUTE), CoreMatchers.is("1 minute"));
        Assert.assertThat(StringUtils.getFullElapsedTime((((MINUTE) + (SECOND)) + 1)), CoreMatchers.is("1 minute, 1 second, 1 ms"));
        Assert.assertThat(StringUtils.getFullElapsedTime((((MINUTE) * 30) + ((SECOND) * 30))), CoreMatchers.is("30 minutes, 30 seconds"));
    }

    @Test
    public void testElapsedTimeInHours() throws Exception {
        Assert.assertThat(StringUtils.getFullElapsedTime(HOUR), CoreMatchers.is("1 hour"));
        Assert.assertThat(StringUtils.getFullElapsedTime(((((HOUR) + (MINUTE)) + (SECOND)) + 1)), CoreMatchers.is("1 hour, 1 minute, 1 second, 1 ms"));
        Assert.assertThat(StringUtils.getFullElapsedTime((((HOUR) * 10) + ((MINUTE) * 30))), CoreMatchers.is("10 hours, 30 minutes"));
    }

    @Test
    public void testElapsedTimeInDays() throws Exception {
        Assert.assertThat(StringUtils.getFullElapsedTime(DAY), CoreMatchers.is("1 day"));
        Assert.assertThat(StringUtils.getFullElapsedTime((((((DAY) + (HOUR)) + (MINUTE)) + (SECOND)) + 1)), CoreMatchers.is("1 day, 1 hour, 1 minute, 1 second, 1 ms"));
        Assert.assertThat(StringUtils.getFullElapsedTime((((DAY) * 10) + ((HOUR) * 10))), CoreMatchers.is("10 days, 10 hours"));
    }
}

