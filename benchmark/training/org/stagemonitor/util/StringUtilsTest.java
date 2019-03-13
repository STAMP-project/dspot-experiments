package org.stagemonitor.util;


import org.junit.Assert;
import org.junit.Test;


public class StringUtilsTest {
    @Test
    public void testToCommaSeparatedString() {
        assertThat(StringUtils.toCommaSeparatedString("foo", "bar")).isEqualTo("foo,bar");
        assertThat(StringUtils.toCommaSeparatedString("foo")).isEqualTo("foo");
        assertThat(StringUtils.toCommaSeparatedString()).isEqualTo("");
    }

    @Test
    public void testRemoveStart() {
        Assert.assertEquals(StringUtils.removeStart("teststring", "test"), "string");
        Assert.assertEquals(StringUtils.removeStart("string", "test"), "string");
        Assert.assertEquals(StringUtils.removeStart("stringtest", "test"), "stringtest");
    }

    @Test
    public void testSha1Hash() throws Exception {
        // result from org.apache.commons.codec.digest.DigestUtils.sha1Hex
        Assert.assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", StringUtils.sha1Hash("test"));
    }
}

