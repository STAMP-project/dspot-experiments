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
package org.languagetool;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AnalyzedTokenReadingsTest {
    @Test
    public void testNewTags() {
        AnalyzedTokenReadings tokenReadings = new AnalyzedTokenReadings(new AnalyzedToken("word", "POS", "lemma"));
        Assert.assertEquals(false, tokenReadings.isLinebreak());
        Assert.assertEquals(false, tokenReadings.isSentenceEnd());
        Assert.assertEquals(false, tokenReadings.isParagraphEnd());
        Assert.assertEquals(false, tokenReadings.isSentenceStart());
        tokenReadings.setSentEnd();
        Assert.assertEquals(false, tokenReadings.isSentenceStart());
        Assert.assertEquals(true, tokenReadings.isSentenceEnd());
        // test SEND_END or PARA_END added without directly via addReading
        // which is possible e.g. in rule disambiguator
        tokenReadings = new AnalyzedTokenReadings(new AnalyzedToken("word", null, "lemma"));
        tokenReadings.addReading(new AnalyzedToken("word", "SENT_END", null));
        Assert.assertEquals(true, tokenReadings.isSentenceEnd());
        Assert.assertEquals(false, tokenReadings.isParagraphEnd());
        tokenReadings.addReading(new AnalyzedToken("word", "PARA_END", null));
        Assert.assertEquals(true, tokenReadings.isParagraphEnd());
        Assert.assertEquals(false, tokenReadings.isSentenceStart());
        // but you can't add SENT_START to a non-empty token
        // and get isSentStart == true
        tokenReadings.addReading(new AnalyzedToken("word", "SENT_START", null));
        Assert.assertEquals(false, tokenReadings.isSentenceStart());
        AnalyzedToken aTok = new AnalyzedToken("word", "POS", "lemma");
        aTok.setWhitespaceBefore(true);
        tokenReadings = new AnalyzedTokenReadings(aTok);
        Assert.assertEquals(aTok, tokenReadings.getAnalyzedToken(0));
        AnalyzedToken aTok2 = new AnalyzedToken("word", "POS", "lemma");
        Assert.assertTrue((!(aTok2.equals(tokenReadings.getAnalyzedToken(0)))));
        AnalyzedToken aTok3 = new AnalyzedToken("word", "POS", "lemma");
        aTok3.setWhitespaceBefore(true);
        Assert.assertEquals(aTok3, tokenReadings.getAnalyzedToken(0));
        AnalyzedTokenReadings testReadings = new AnalyzedTokenReadings(aTok3);
        testReadings.removeReading(aTok3);
        Assert.assertTrue(((testReadings.getReadingsLength()) == 1));
        Assert.assertEquals("word", testReadings.getToken());
        Assert.assertTrue((!(testReadings.hasPosTag("POS"))));
        // now what about removing something that does not belong to testReadings?
        testReadings.leaveReading(aTok2);
        Assert.assertEquals("word", testReadings.getToken());
        Assert.assertTrue((!(testReadings.hasPosTag("POS"))));
        testReadings.removeReading(aTok2);
        Assert.assertEquals("word", testReadings.getToken());
        Assert.assertTrue((!(testReadings.hasPosTag("POS"))));
    }

    @Test
    public void testToString() {
        AnalyzedTokenReadings tokenReadings = new AnalyzedTokenReadings(new AnalyzedToken("word", "POS", "lemma"));
        Assert.assertEquals("word[lemma/POS*]", tokenReadings.toString());
        AnalyzedToken aTok2 = new AnalyzedToken("word", "POS2", "lemma2");
        tokenReadings.addReading(aTok2);
        Assert.assertEquals("word[lemma/POS*,lemma2/POS2*]", tokenReadings.toString());
    }

    @Test
    public void testHasPosTag() {
        AnalyzedTokenReadings tokenReadings = new AnalyzedTokenReadings(new AnalyzedToken("word", "POS:FOO:BAR", "lemma"));
        Assert.assertTrue(tokenReadings.hasPosTag("POS:FOO:BAR"));
        Assert.assertFalse(tokenReadings.hasPosTag("POS:FOO:bar"));
        Assert.assertFalse(tokenReadings.hasPosTag("POS:FOO"));
        Assert.assertFalse(tokenReadings.hasPosTag("xaz"));
    }

    @Test
    public void testHasPartialPosTag() {
        AnalyzedTokenReadings tokenReadings = new AnalyzedTokenReadings(new AnalyzedToken("word", "POS:FOO:BAR", "lemma"));
        Assert.assertTrue(tokenReadings.hasPartialPosTag("POS:FOO:BAR"));
        Assert.assertTrue(tokenReadings.hasPartialPosTag("POS:FOO:"));
        Assert.assertTrue(tokenReadings.hasPartialPosTag("POS:FOO"));
        Assert.assertTrue(tokenReadings.hasPartialPosTag(":FOO:"));
        Assert.assertTrue(tokenReadings.hasPartialPosTag("FOO:BAR"));
        Assert.assertFalse(tokenReadings.hasPartialPosTag("POS:FOO:BARX"));
        Assert.assertFalse(tokenReadings.hasPartialPosTag("POS:foo:BAR"));
        Assert.assertFalse(tokenReadings.hasPartialPosTag("xaz"));
    }

    @Test
    public void testMatchesPosTagRegex() {
        AnalyzedTokenReadings tokenReadings = new AnalyzedTokenReadings(new AnalyzedToken("word", "POS:FOO:BAR", "lemma"));
        Assert.assertTrue(tokenReadings.matchesPosTagRegex("POS:FOO:BAR"));
        Assert.assertTrue(tokenReadings.matchesPosTagRegex("POS:...:BAR"));
        Assert.assertTrue(tokenReadings.matchesPosTagRegex("POS:[A-Z]+:BAR"));
        Assert.assertFalse(tokenReadings.matchesPosTagRegex("POS:[AB]OO:BAR"));
        Assert.assertFalse(tokenReadings.matchesPosTagRegex("POS:FOO:BARX"));
    }

    @Test
    public void testIteration() {
        AnalyzedTokenReadings tokenReadings = new AnalyzedTokenReadings(Arrays.asList(new AnalyzedToken("word1", null, null), new AnalyzedToken("word2", null, null)), 0);
        int i = 0;
        for (AnalyzedToken tokenReading : tokenReadings) {
            if (i == 0) {
                Assert.assertThat(tokenReading.getToken(), CoreMatchers.is("word1"));
            } else
                if (i == 1) {
                    Assert.assertThat(tokenReading.getToken(), CoreMatchers.is("word2"));
                } else {
                    Assert.fail();
                }

            i++;
        }
    }
}

