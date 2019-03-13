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
package org.languagetool.rules.en;


import Determiner.A;
import Determiner.AN;
import Determiner.A_OR_AN;
import Determiner.UNKNOWN;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.language.English;
import org.languagetool.rules.RuleMatch;


public class AvsAnRuleTest {
    private AvsAnRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        assertCorrect("This is a test sentence.");
        assertCorrect("It was an hour ago.");
        assertCorrect("A university is ...");
        assertCorrect("A one-way street ...");
        assertCorrect("An hour's work ...");
        assertCorrect("Going to an \"industry party\".");
        assertCorrect("An 8-year old boy ...");
        assertCorrect("An 18-year old boy ...");
        assertCorrect("The A-levels are ...");
        assertCorrect("An NOP check ...");
        assertCorrect("A USA-wide license ...");
        assertCorrect("...asked a UN member.");
        assertCorrect("In an un-united Germany...");
        // fixed false alarms:
        assertCorrect("Here, a and b are supplementary angles.");
        assertCorrect("The Qur'an was translated into Polish.");
        assertCorrect("See an:Grammatica");
        assertCorrect("See http://www.an.com");
        assertCorrect("Station A equals station B.");
        // errors:
        assertIncorrect("It was a hour ago.");
        assertIncorrect("It was an sentence that's long.");
        assertIncorrect("It was a uninteresting talk.");
        assertIncorrect("An university");
        assertIncorrect("A unintersting ...");
        assertIncorrect("A hour's work ...");
        assertIncorrect("Going to a \"industry party\".");
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("It was a uninteresting talk with an long sentence."));
        Assert.assertEquals(2, matches.length);
        // With uppercase letters:
        assertCorrect("A University");
        assertCorrect("A Europe wide something");
        assertIncorrect("then an University sdoj fixme sdoopsd");
        assertIncorrect("A 8-year old boy ...");
        assertIncorrect("A 18-year old boy ...");
        assertIncorrect("...asked an UN member.");
        assertIncorrect("In a un-united Germany...");
        // Test on acronyms/initials:
        assertCorrect("A. R.J. Turgot");
        // mixed case as dictionary-based exception
        assertCorrect("Anyone for an MSc?");
        assertIncorrect("Anyone for a MSc?");
        // mixed case from general case
        assertCorrect("Anyone for an XMR-based writer?");
        // Test on apostrophes
        assertCorrect("Its name in English is a[1] (), plural A's, As, as, or a's.");
        // Both are correct according to Merriam Webster (http://www.merriam-webster.com/dictionary/a%5B2%5D),
        // although some people disagree (http://www.theslot.com/a-an.html):
        assertCorrect("An historic event");
        assertCorrect("A historic event");
    }

    @Test
    public void testSuggestions() throws IOException {
        Assert.assertEquals("a string", rule.suggestAorAn("string"));
        Assert.assertEquals("a university", rule.suggestAorAn("university"));
        Assert.assertEquals("an hour", rule.suggestAorAn("hour"));
        Assert.assertEquals("an all-terrain", rule.suggestAorAn("all-terrain"));
        Assert.assertEquals("a UNESCO", rule.suggestAorAn("UNESCO"));
        Assert.assertEquals("a historical", rule.suggestAorAn("historical"));
    }

    @Test
    public void testGetCorrectDeterminerFor() throws IOException {
        Assert.assertEquals(A, getDeterminerFor("string"));
        Assert.assertEquals(A, getDeterminerFor("university"));
        Assert.assertEquals(A, getDeterminerFor("UNESCO"));
        Assert.assertEquals(A, getDeterminerFor("one-way"));
        Assert.assertEquals(AN, getDeterminerFor("interesting"));
        Assert.assertEquals(AN, getDeterminerFor("hour"));
        Assert.assertEquals(AN, getDeterminerFor("all-terrain"));
        Assert.assertEquals(A_OR_AN, getDeterminerFor("historical"));
        Assert.assertEquals(UNKNOWN, getDeterminerFor(""));
        Assert.assertEquals(UNKNOWN, getDeterminerFor("-way"));
        Assert.assertEquals(UNKNOWN, getDeterminerFor("camelCase"));
    }

    @Test
    public void testGetCorrectDeterminerForException() throws IOException {
        try {
            rule.getCorrectDeterminerFor(null);
            Assert.fail();
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void testPositions() throws IOException {
        RuleMatch[] matches;
        JLanguageTool langTool = new JLanguageTool(new English());
        // no quotes etc.:
        matches = rule.match(langTool.getAnalyzedSentence("a industry standard."));
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(1, matches[0].getToPos());
        // quotes..
        matches = rule.match(langTool.getAnalyzedSentence("a \"industry standard\"."));
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(1, matches[0].getToPos());
        matches = rule.match(langTool.getAnalyzedSentence("a - industry standard\"."));
        Assert.assertEquals(0, matches[0].getFromPos());
        Assert.assertEquals(1, matches[0].getToPos());
        matches = rule.match(langTool.getAnalyzedSentence("This is a \"industry standard\"."));
        Assert.assertEquals(8, matches[0].getFromPos());
        Assert.assertEquals(9, matches[0].getToPos());
        matches = rule.match(langTool.getAnalyzedSentence("\"a industry standard\"."));
        Assert.assertEquals(1, matches[0].getFromPos());
        Assert.assertEquals(2, matches[0].getToPos());
        matches = rule.match(langTool.getAnalyzedSentence("\"Many say this is a industry standard\"."));
        Assert.assertEquals(18, matches[0].getFromPos());
        Assert.assertEquals(19, matches[0].getToPos());
        matches = rule.match(langTool.getAnalyzedSentence("Like many \"an desperado\" before him, Bart headed south into Mexico."));
        Assert.assertEquals(11, matches[0].getFromPos());
        Assert.assertEquals(13, matches[0].getToPos());
    }
}

