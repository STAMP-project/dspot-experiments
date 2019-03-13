/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.tokenizers;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class WordTokenizerTest {
    private final WordTokenizer wordTokenizer = new WordTokenizer();

    @Test
    public void testTokenize() {
        WordTokenizer wordTokenizer = new WordTokenizer();
        List<String> tokens = wordTokenizer.tokenize("This is\u00a0a test");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[This,  , is, \u00a0, a,  , test]", tokens.toString());
        tokens = wordTokenizer.tokenize("This\rbreaks");
        Assert.assertEquals(3, tokens.size());
        Assert.assertEquals("[This, \r, breaks]", tokens.toString());
        tokens = wordTokenizer.tokenize("dev.all@languagetool.org");
        Assert.assertEquals(1, tokens.size());
        tokens = wordTokenizer.tokenize("dev.all@languagetool.org.");
        Assert.assertEquals(2, tokens.size());
        tokens = wordTokenizer.tokenize("dev.all@languagetool.org:");
        Assert.assertEquals(2, tokens.size());
        tokens = wordTokenizer.tokenize("Schreiben Sie Hr. Meier (meier@mail.com).");
        Assert.assertEquals(tokens.size(), 13);
        tokens = wordTokenizer.tokenize("Get more at languagetool.org/foo, and via twitter");
        Assert.assertEquals(14, tokens.size());
        Assert.assertTrue(tokens.contains("languagetool.org/foo"));
        tokens = wordTokenizer.tokenize("Get more at sub.languagetool.org/foo, and via twitter");
        Assert.assertEquals(14, tokens.size());
        Assert.assertTrue(tokens.contains("sub.languagetool.org/foo"));
    }

    @Test
    public void testIsUrl() {
        Assert.assertTrue(WordTokenizer.isUrl("www.languagetool.org"));
        Assert.assertTrue(WordTokenizer.isUrl("languagetool.org/"));
        Assert.assertTrue(WordTokenizer.isUrl("languagetool.org/foo"));
        Assert.assertTrue(WordTokenizer.isUrl("subdomain.languagetool.org/"));
        Assert.assertTrue(WordTokenizer.isUrl("http://www.languagetool.org"));
        Assert.assertTrue(WordTokenizer.isUrl("https://www.languagetool.org"));
        Assert.assertFalse(WordTokenizer.isUrl("languagetool.org"));// not detected yet

        Assert.assertFalse(WordTokenizer.isUrl("sub.languagetool.org"));// not detected yet

        Assert.assertFalse(WordTokenizer.isUrl("something-else"));
    }

    @Test
    public void testIsEMail() {
        Assert.assertTrue(WordTokenizer.isEMail("martin.mustermann@test.de"));
        Assert.assertTrue(WordTokenizer.isEMail("martin.mustermann@test.languagetool.de"));
        Assert.assertTrue(WordTokenizer.isEMail("martin-mustermann@test.com"));
        Assert.assertFalse(WordTokenizer.isEMail("@test.de"));
        Assert.assertFalse(WordTokenizer.isEMail("f.test@test"));
        Assert.assertFalse(WordTokenizer.isEMail("f@t.t"));
    }

    @Test
    public void testUrlTokenize() {
        Assert.assertEquals("\"|This| |http://foo.org|.|\"", tokenize("\"This http://foo.org.\""));
        Assert.assertEquals("?|This| |http://foo.org|.|?", tokenize("?This http://foo.org.?"));
        Assert.assertEquals("This| |http://foo.org|.|.|.", tokenize("This http://foo.org..."));
        Assert.assertEquals("This| |http://foo.org|.", tokenize("This http://foo.org."));
        Assert.assertEquals("This| |http://foo.org| |blah", tokenize("This http://foo.org blah"));
        Assert.assertEquals("This| |http://foo.org| |and| |ftp://bla.com| |blah", tokenize("This http://foo.org and ftp://bla.com blah"));
        Assert.assertEquals("foo| |http://localhost:32000/?ch=1| |bar", tokenize("foo http://localhost:32000/?ch=1 bar"));
        Assert.assertEquals("foo| |ftp://localhost:32000/| |bar", tokenize("foo ftp://localhost:32000/ bar"));
        Assert.assertEquals("foo| |http://google.de/?aaa| |bar", tokenize("foo http://google.de/?aaa bar"));
        Assert.assertEquals("foo| |http://www.flickr.com/123@N04/hallo#test| |bar", tokenize("foo http://www.flickr.com/123@N04/hallo#test bar"));
        Assert.assertEquals("foo| |http://www.youtube.com/watch?v=wDN_EYUvUq0| |bar", tokenize("foo http://www.youtube.com/watch?v=wDN_EYUvUq0 bar"));
        Assert.assertEquals("foo| |http://example.net/index.html?s=A54C6FE2%23info| |bar", tokenize("foo http://example.net/index.html?s=A54C6FE2%23info bar"));
        Assert.assertEquals("foo| |https://joe:passwd@example.net:8080/index.html?action=x&session=A54C6FE2#info| |bar", tokenize("foo https://joe:passwd@example.net:8080/index.html?action=x&session=A54C6FE2#info bar"));
    }

    @Test
    public void testUrlTokenizeWithQuote() {
        Assert.assertEquals("This| |'|http://foo.org|'| |blah", tokenize("This 'http://foo.org' blah"));
        Assert.assertEquals("This| |\"|http://foo.org|\"| |blah", tokenize("This \"http://foo.org\" blah"));
        Assert.assertEquals("This| |(|\"|http://foo.org|\"|)| |blah", tokenize("This (\"http://foo.org\") blah"));// issue #1226

    }

    @Test
    public void testUrlTokenizeWithAppendedCharacter() {
        Assert.assertEquals("foo| |(|http://ex.net/p?a=x#i|)| |bar", tokenize("foo (http://ex.net/p?a=x#i) bar"));
        Assert.assertEquals("foo| |http://ex.net/p?a=x#i|,| |bar", tokenize("foo http://ex.net/p?a=x#i, bar"));
        Assert.assertEquals("foo| |http://ex.net/p?a=x#i|.| |bar", tokenize("foo http://ex.net/p?a=x#i. bar"));
        Assert.assertEquals("foo| |http://ex.net/p?a=x#i|:| |bar", tokenize("foo http://ex.net/p?a=x#i: bar"));
        Assert.assertEquals("foo| |http://ex.net/p?a=x#i|?| |bar", tokenize("foo http://ex.net/p?a=x#i? bar"));
        Assert.assertEquals("foo| |http://ex.net/p?a=x#i|!| |bar", tokenize("foo http://ex.net/p?a=x#i! bar"));
    }

    @Test
    public void testIncompleteUrlTokenize() {
        Assert.assertEquals("http|:|/", tokenize("http:/"));
        Assert.assertEquals("http://", tokenize("http://"));
        Assert.assertEquals("http://a", tokenize("http://a"));
        Assert.assertEquals("foo| |http| |bar", tokenize("foo http bar"));
        Assert.assertEquals("foo| |http|:| |bar", tokenize("foo http: bar"));
        Assert.assertEquals("foo| |http|:|/| |bar", tokenize("foo http:/ bar"));
        Assert.assertEquals("foo| |http://| |bar", tokenize("foo http:// bar"));
        Assert.assertEquals("foo| |http://a| |bar", tokenize("foo http://a bar"));
        Assert.assertEquals("foo| |http://|?| |bar", tokenize("foo http://? bar"));
    }
}

