/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.de;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.SwissGerman;
import org.languagetool.rules.RuleMatch;


public class SwissGermanSpellerRuleTest {
    private static final SwissGerman DE_CH = new SwissGerman();

    @Test
    public void testGetSuggestionsFromSpellingTxt() throws Exception {
        SwissGermanSpellerRule rule = new SwissGermanSpellerRule(TestTools.getEnglishMessages(), SwissGermanSpellerRuleTest.DE_CH);
        JLanguageTool lt = new JLanguageTool(SwissGermanSpellerRuleTest.DE_CH);
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Shopbewertung")).length, CoreMatchers.is(0));// from spelling.txt

        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Abwart")).length, CoreMatchers.is(0));
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Abwarts")).length, CoreMatchers.is(0));
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("aifhdlidflifs")).length, CoreMatchers.is(1));
        RuleMatch[] matches = rule.match(lt.getAnalyzedSentence("Trottinettens"));// from spelling-de-CH.txt

        Assert.assertThat(((("Matches: " + (matches.length)) + ", Suggestions of first match: ") + (matches[0].getSuggestedReplacements())), matches[0].getSuggestedReplacements().get(0), CoreMatchers.is("Trottinetten"));
    }
}

