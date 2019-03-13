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
import org.languagetool.language.AustrianGerman;


public class AustrianGermanSpellerRuleTest {
    private static final AustrianGerman DE_AT = new AustrianGerman();

    @Test
    public void testGetSuggestionsFromSpellingTxt() throws Exception {
        AustrianGermanSpellerRule rule = new AustrianGermanSpellerRule(TestTools.getEnglishMessages(), AustrianGermanSpellerRuleTest.DE_AT, null, null);
        JLanguageTool lt = new JLanguageTool(AustrianGermanSpellerRuleTest.DE_AT);
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Shopbewertung")).length, CoreMatchers.is(0));// from spelling.txt

        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Wahlzuckerl")).length, CoreMatchers.is(0));
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("Wahlzuckerls")).length, CoreMatchers.is(0));
        Assert.assertThat(rule.match(lt.getAnalyzedSentence("aifhdlidflifs")).length, CoreMatchers.is(1));
    }
}

