/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2009 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.patterns;


import org.junit.Assert;
import org.junit.Test;
import org.languagetool.AnalyzedToken;


public class PatternTokenTest {
    @Test
    public void testSentenceStart() {
        PatternToken patternToken = new PatternToken("", false, false, false);
        patternToken.setPosToken(new PatternToken.PosToken(SENTENCE_START_TAGNAME, false, false));
        Assert.assertTrue(patternToken.isSentenceStart());
        patternToken.setPosToken(new PatternToken.PosToken(SENTENCE_START_TAGNAME, false, true));
        Assert.assertFalse(patternToken.isSentenceStart());
        patternToken.setPosToken(new PatternToken.PosToken(SENTENCE_START_TAGNAME, true, false));
        Assert.assertTrue(patternToken.isSentenceStart());
        patternToken.setPosToken(new PatternToken.PosToken(SENTENCE_START_TAGNAME, true, true));
        Assert.assertFalse(patternToken.isSentenceStart());
        PatternToken patternToken2 = new PatternToken("bla|blah", false, true, false);
        patternToken2.setPosToken(new PatternToken.PosToken("foo", true, true));
        Assert.assertFalse(patternToken2.isSentenceStart());
    }

    @Test
    public void testUnknownTag() {
        PatternToken patternToken = new PatternToken("", false, false, false);
        patternToken.setPosToken(new PatternToken.PosToken(PatternToken.UNKNOWN_TAG, false, false));
        PatternToken patternToken2 = new PatternToken("", false, false, false);
        patternToken2.setPosToken(new PatternToken.PosToken(PatternToken.UNKNOWN_TAG, false, true));
        PatternToken patternToken3 = new PatternToken("", false, false, false);
        patternToken3.setPosToken(new PatternToken.PosToken(((PatternToken.UNKNOWN_TAG) + "|VBG"), true, false));
        PatternToken patternToken4 = new PatternToken("", false, false, false);
        patternToken4.setPosToken(new PatternToken.PosToken(((PatternToken.UNKNOWN_TAG) + "|VBG"), true, true));
        PatternToken patternToken5 = new PatternToken("\\p{Ll}+", false, true, false);
        patternToken5.setPosToken(new PatternToken.PosToken(PatternToken.UNKNOWN_TAG, false, false));
        AnalyzedToken an = new AnalyzedToken("schword", null, null);
        Assert.assertTrue(patternToken.isMatched(an));
        Assert.assertFalse(patternToken2.isMatched(an));
        Assert.assertTrue(patternToken3.isMatched(an));
        Assert.assertFalse(patternToken4.isMatched(an));
        Assert.assertTrue(patternToken5.isMatched(an));
        // if the AnalyzedToken is in the set of readings that have
        // non-null tags...
        an.setNoPOSTag(false);
        Assert.assertFalse(patternToken.isMatched(an));
        Assert.assertTrue(patternToken2.isMatched(an));
        Assert.assertFalse(patternToken3.isMatched(an));
        Assert.assertTrue(patternToken4.isMatched(an));
        Assert.assertFalse(patternToken5.isMatched(an));
        AnalyzedToken anSentEnd = new AnalyzedToken("schword", SENTENCE_END_TAGNAME, null);
        Assert.assertTrue(patternToken.isMatched(anSentEnd));
        Assert.assertFalse(patternToken2.isMatched(anSentEnd));
        Assert.assertTrue(patternToken3.isMatched(anSentEnd));
        Assert.assertFalse(patternToken4.isMatched(anSentEnd));
        Assert.assertTrue(patternToken5.isMatched(anSentEnd));
        PatternToken patternToken6 = new PatternToken("\\p{Ll}+", false, true, false);
        patternToken6.setPosToken(new PatternToken.PosToken(SENTENCE_END_TAGNAME, false, false));
        Assert.assertTrue(patternToken6.isMatched(anSentEnd));
        PatternToken patternToken7 = new PatternToken("\\p{Ll}+", false, true, false);
        patternToken7.setPosToken(new PatternToken.PosToken(((SENTENCE_END_TAGNAME) + "|BLABLA"), true, false));
        Assert.assertTrue(patternToken7.isMatched(anSentEnd));
        // if the AnalyzedToken is in the set of readings that have
        // non-null tags...
        anSentEnd.setNoPOSTag(false);
        Assert.assertFalse(patternToken.isMatched(anSentEnd));
        Assert.assertTrue(patternToken2.isMatched(anSentEnd));
        Assert.assertFalse(patternToken3.isMatched(anSentEnd));
        Assert.assertTrue(patternToken4.isMatched(anSentEnd));
        Assert.assertFalse(patternToken5.isMatched(anSentEnd));
        AnalyzedToken anParaEnd = new AnalyzedToken("schword", PARAGRAPH_END_TAGNAME, null);
        Assert.assertTrue(patternToken.isMatched(anParaEnd));
        Assert.assertFalse(patternToken2.isMatched(anParaEnd));
        Assert.assertTrue(patternToken3.isMatched(anParaEnd));
        Assert.assertFalse(patternToken4.isMatched(anParaEnd));
        Assert.assertTrue(patternToken5.isMatched(anParaEnd));
        // if the AnalyzedToken is in the set of readings that have
        // non-null tags...
        anParaEnd.setNoPOSTag(false);
        Assert.assertFalse(patternToken.isMatched(anParaEnd));
        Assert.assertTrue(patternToken2.isMatched(anParaEnd));
        Assert.assertFalse(patternToken3.isMatched(anParaEnd));
        Assert.assertTrue(patternToken4.isMatched(anParaEnd));
        Assert.assertFalse(patternToken5.isMatched(anParaEnd));
        AnalyzedToken anWithPOS = new AnalyzedToken("schword", "POS", null);
        Assert.assertFalse(patternToken.isMatched(anWithPOS));
        Assert.assertTrue(patternToken2.isMatched(anWithPOS));
        Assert.assertFalse(patternToken3.isMatched(anWithPOS));
        Assert.assertTrue(patternToken4.isMatched(anWithPOS));
        Assert.assertFalse(patternToken5.isMatched(anWithPOS));
    }
}

