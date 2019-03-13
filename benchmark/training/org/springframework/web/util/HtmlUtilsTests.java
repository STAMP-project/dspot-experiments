/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alef Arendsen
 * @author Martin Kersten
 * @author Rick Evans
 */
public class HtmlUtilsTests {
    @Test
    public void testHtmlEscape() {
        String unescaped = "\"This is a quote\'";
        String escaped = HtmlUtils.htmlEscape(unescaped);
        Assert.assertEquals("&quot;This is a quote&#39;", escaped);
        escaped = HtmlUtils.htmlEscapeDecimal(unescaped);
        Assert.assertEquals("&#34;This is a quote&#39;", escaped);
        escaped = HtmlUtils.htmlEscapeHex(unescaped);
        Assert.assertEquals("&#x22;This is a quote&#x27;", escaped);
    }

    @Test
    public void testHtmlUnescape() {
        String escaped = "&quot;This is a quote&#39;";
        String unescaped = HtmlUtils.htmlUnescape(escaped);
        Assert.assertEquals("\"This is a quote\'", unescaped);
    }

    @Test
    public void testEncodeIntoHtmlCharacterSet() {
        Assert.assertEquals("An empty string should be converted to an empty string", "", HtmlUtils.htmlEscape(""));
        Assert.assertEquals("A string containing no special characters should not be affected", "A sentence containing no special characters.", HtmlUtils.htmlEscape("A sentence containing no special characters."));
        Assert.assertEquals("'< >' should be encoded to '&lt; &gt;'", "&lt; &gt;", HtmlUtils.htmlEscape("< >"));
        Assert.assertEquals("'< >' should be encoded to '&#60; &#62;'", "&#60; &#62;", HtmlUtils.htmlEscapeDecimal("< >"));
        Assert.assertEquals("The special character 8709 should be encoded to '&empty;'", "&empty;", HtmlUtils.htmlEscape(("" + ((char) (8709)))));
        Assert.assertEquals("The special character 8709 should be encoded to '&#8709;'", "&#8709;", HtmlUtils.htmlEscapeDecimal(("" + ((char) (8709)))));
        Assert.assertEquals("The special character 977 should be encoded to '&thetasym;'", "&thetasym;", HtmlUtils.htmlEscape(("" + ((char) (977)))));
        Assert.assertEquals("The special character 977 should be encoded to '&#977;'", "&#977;", HtmlUtils.htmlEscapeDecimal(("" + ((char) (977)))));
    }

    // SPR-9293
    @Test
    public void testEncodeIntoHtmlCharacterSetFromUtf8() {
        String utf8 = "UTF-8";
        Assert.assertEquals("An empty string should be converted to an empty string", "", HtmlUtils.htmlEscape("", utf8));
        Assert.assertEquals("A string containing no special characters should not be affected", "A sentence containing no special characters.", HtmlUtils.htmlEscape("A sentence containing no special characters."));
        Assert.assertEquals("'< >' should be encoded to '&lt; &gt;'", "&lt; &gt;", HtmlUtils.htmlEscape("< >", utf8));
        Assert.assertEquals("'< >' should be encoded to '&#60; &#62;'", "&#60; &#62;", HtmlUtils.htmlEscapeDecimal("< >", utf8));
        Assert.assertEquals("UTF-8 supported chars should not be escaped", "??????? ????????? &quot;??????????&quot;", HtmlUtils.htmlEscape("\u039c\u03b5\u03c1\u03b9\u03ba\u03bf\u03af \u0395\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03bf\u03af \"\u03c7\u03b1\u03c1\u03b1\u03ba\u03c4\u03ae\u03c1\u03b5\u03c2\"", utf8));
    }

    @Test
    public void testDecodeFromHtmlCharacterSet() {
        Assert.assertEquals("An empty string should be converted to an empty string", "", HtmlUtils.htmlUnescape(""));
        Assert.assertEquals("A string containing no special characters should not be affected", "This is a sentence containing no special characters.", HtmlUtils.htmlUnescape("This is a sentence containing no special characters."));
        Assert.assertEquals("'A&nbsp;B' should be decoded to 'A B'", (("A" + ((char) (160))) + "B"), HtmlUtils.htmlUnescape("A&nbsp;B"));
        Assert.assertEquals("'&lt; &gt;' should be decoded to '< >'", "< >", HtmlUtils.htmlUnescape("&lt; &gt;"));
        Assert.assertEquals("'&#60; &#62;' should be decoded to '< >'", "< >", HtmlUtils.htmlUnescape("&#60; &#62;"));
        Assert.assertEquals("'&#x41;&#X42;&#x43;' should be decoded to 'ABC'", "ABC", HtmlUtils.htmlUnescape("&#x41;&#X42;&#x43;"));
        Assert.assertEquals("'&phi;' should be decoded to uni-code character 966", ("" + ((char) (966))), HtmlUtils.htmlUnescape("&phi;"));
        Assert.assertEquals("'&Prime;' should be decoded to uni-code character 8243", ("" + ((char) (8243))), HtmlUtils.htmlUnescape("&Prime;"));
        Assert.assertEquals("A not supported named reference leads should be ignored", "&prIme;", HtmlUtils.htmlUnescape("&prIme;"));
        Assert.assertEquals("An empty reference '&;' should be survive the decoding", "&;", HtmlUtils.htmlUnescape("&;"));
        Assert.assertEquals("The longest character entity reference '&thetasym;' should be processable", ("" + ((char) (977))), HtmlUtils.htmlUnescape("&thetasym;"));
        Assert.assertEquals("A malformed decimal reference should survive the decoding", "&#notADecimalNumber;", HtmlUtils.htmlUnescape("&#notADecimalNumber;"));
        Assert.assertEquals("A malformed hex reference should survive the decoding", "&#XnotAHexNumber;", HtmlUtils.htmlUnescape("&#XnotAHexNumber;"));
        Assert.assertEquals("The numerical reference '&#1;' should be converted to char 1", ("" + ((char) (1))), HtmlUtils.htmlUnescape("&#1;"));
        Assert.assertEquals("The malformed hex reference '&#x;' should remain '&#x;'", "&#x;", HtmlUtils.htmlUnescape("&#x;"));
    }
}

