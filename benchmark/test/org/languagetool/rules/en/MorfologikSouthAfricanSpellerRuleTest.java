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
import org.languagetool.language.SouthAfricanEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;


public class MorfologikSouthAfricanSpellerRuleTest extends AbstractEnglishSpellerRuleTest {
    @Test
    public void testSuggestions() throws IOException {
        Language language = new SouthAfricanEnglish();
        Rule rule = new MorfologikSouthAfricanSpellerRule(TestTools.getMessages("en"), language, null, Collections.emptyList());
        super.testNonVariantSpecificSuggestions(rule, language);
        JLanguageTool langTool = new JLanguageTool(language);
        // suggestions from language specific spelling_en-XX.txt
        assertSuggestion(rule, langTool, "ZATestWordToBeIgnore", "ZATestWordToBeIgnored");
    }

    @Test
    public void testMorfologikSpeller() throws IOException {
        SouthAfricanEnglish language = new SouthAfricanEnglish();
        MorfologikSouthAfricanSpellerRule rule = new MorfologikSouthAfricanSpellerRule(TestTools.getMessages("en"), language, null, Collections.emptyList());
        JLanguageTool langTool = new JLanguageTool(language);
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("This is an example: we get behaviour as a dictionary word.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Why don't we speak today.")).length);
        // with doesn't
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("He doesn't know what to do.")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence(",")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("123454")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("?")).length);
        // South African dict:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("Amanzimnyama")).length);
        // incorrect sentences:
        RuleMatch[] matches1 = rule.match(langTool.getAnalyzedSentence("behavior"));
        // check match positions:
        Assert.assertEquals(1, matches1.length);
        Assert.assertEquals(0, matches1[0].getFromPos());
        Assert.assertEquals(8, matches1[0].getToPos());
        Assert.assertEquals("behaviour", matches1[0].getSuggestedReplacements().get(0));
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

