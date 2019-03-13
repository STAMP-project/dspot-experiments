/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Marcin Mi?kowski
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


import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.TestTools;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;


public class MorfologikBritishSpellerRuleTest extends AbstractEnglishSpellerRuleTest {
    @Test
    public void testSuggestions() throws IOException {
        Language language = new BritishEnglish();
        Rule rule = new MorfologikBritishSpellerRule(TestTools.getMessages("en"), language, null, Collections.emptyList());
        super.testNonVariantSpecificSuggestions(rule, language);
        JLanguageTool langTool = new JLanguageTool(language);
        // suggestions from language specific spelling_en-XX.txt
        assertSuggestion(rule, langTool, "GBTestWordToBeIgnore", "GBTestWordToBeIgnored");
    }

    @Test
    public void testVariantMessages() throws IOException {
        BritishEnglish language = new BritishEnglish();
        JLanguageTool lt = new JLanguageTool(language);
        Rule rule = new MorfologikBritishSpellerRule(TestTools.getMessages("en"), language, null, Collections.emptyList());
        RuleMatch[] matches = rule.match(lt.getAnalyzedSentence("This is a nice color."));
        Assert.assertEquals(1, matches.length);
        Assert.assertTrue(matches[0].getMessage().contains("is American English"));
    }

    @Test
    public void testMorfologikSpeller() throws IOException {
        BritishEnglish language = new BritishEnglish();
        MorfologikBritishSpellerRule rule = new MorfologikBritishSpellerRule(TestTools.getMessages("en"), language, null, Collections.emptyList());
        JLanguageTool langTool = new JLanguageTool(language);
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("This is an example: we get behaviour as a dictionary word.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Why don't we speak today.")).length);
        // with doesn't
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("He doesn't know what to do.")).length);
        // with diacritics
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("The entr?e at the caf?.")).length);
        // with an abbreviation:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("This is my Ph.D. thesis.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence(",")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("123454")).length);
        // Greek letters
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("?")).length);
        // incorrect sentences:
        RuleMatch[] matches1 = rule.match(langTool.getAnalyzedSentence("Behavior"));
        // check match positions:
        Assert.assertEquals(1, matches1.length);
        Assert.assertEquals(0, matches1[0].getFromPos());
        Assert.assertEquals(8, matches1[0].getToPos());
        Assert.assertEquals("Behaviour", matches1[0].getSuggestedReplacements().get(0));
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("a?h")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("a")).length);
        // based on replacement pairs:
        RuleMatch[] matches2 = rule.match(langTool.getAnalyzedSentence("He teached us."));
        // check match positions:
        Assert.assertEquals(1, matches2.length);
        Assert.assertEquals(3, matches2[0].getFromPos());
        Assert.assertEquals(10, matches2[0].getToPos());
        Assert.assertEquals("taught", matches2[0].getSuggestedReplacements().get(0));
    }
}

