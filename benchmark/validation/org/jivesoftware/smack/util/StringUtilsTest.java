/**
 * Copyright 2003-2007 Jive Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.util;


import StringUtils.UTF8;
import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test case for the StringUtils class.
 */
public class StringUtilsTest {
    @Test
    public void testEscapeForXml() {
        String input = null;
        Assert.assertNull(StringUtils.escapeForXml(null));
        input = "<b>";
        StringUtilsTest.assertCharSequenceEquals("&lt;b&gt;", StringUtils.escapeForXml(input));
        input = "\"";
        StringUtilsTest.assertCharSequenceEquals("&quot;", StringUtils.escapeForXml(input));
        input = "&";
        StringUtilsTest.assertCharSequenceEquals("&amp;", StringUtils.escapeForXml(input));
        input = "<b>\n\t\r</b>";
        StringUtilsTest.assertCharSequenceEquals("&lt;b&gt;\n\t\r&lt;/b&gt;", StringUtils.escapeForXml(input));
        input = "   &   ";
        StringUtilsTest.assertCharSequenceEquals("   &amp;   ", StringUtils.escapeForXml(input));
        input = "   \"   ";
        StringUtilsTest.assertCharSequenceEquals("   &quot;   ", StringUtils.escapeForXml(input));
        input = "> of me <";
        StringUtilsTest.assertCharSequenceEquals("&gt; of me &lt;", StringUtils.escapeForXml(input));
        input = "> of me & you<";
        StringUtilsTest.assertCharSequenceEquals("&gt; of me &amp; you&lt;", StringUtils.escapeForXml(input));
        input = "& <";
        StringUtilsTest.assertCharSequenceEquals("&amp; &lt;", StringUtils.escapeForXml(input));
        input = "&";
        StringUtilsTest.assertCharSequenceEquals("&amp;", StringUtils.escapeForXml(input));
        input = "It's a good day today";
        StringUtilsTest.assertCharSequenceEquals("It&apos;s a good day today", StringUtils.escapeForXml(input));
    }

    @Test
    public void testEncodeHex() throws UnsupportedEncodingException {
        String input = "";
        String output = "";
        Assert.assertEquals(new String(StringUtils.encodeHex(input.getBytes(UTF8))), output);
        input = "foo bar 123";
        output = "666f6f2062617220313233";
        Assert.assertEquals(new String(StringUtils.encodeHex(input.getBytes(UTF8))), output);
    }

    @Test
    public void testRandomString() {
        // Boundary test
        String result = StringUtils.randomString((-1));
        Assert.assertNull(result);
        // Zero length string test
        result = StringUtils.randomString(0);
        Assert.assertNull(result);
        // Test various lengths - make sure the same length is returned
        result = StringUtils.randomString(4);
        Assert.assertTrue(((result.length()) == 4));
        result = StringUtils.randomString(16);
        Assert.assertTrue(((result.length()) == 16));
        result = StringUtils.randomString(128);
        Assert.assertTrue(((result.length()) == 128));
    }
}

