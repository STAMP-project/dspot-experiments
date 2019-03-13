/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Daniel Naber (http://www.danielnaber.de)
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


import java.io.IOException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.German;
import org.languagetool.language.GermanyGerman;
import org.languagetool.rules.RuleMatch;


@Ignore
public class MorfologikGermanyGermanSpellerRuleTest {
    @Test
    public void testMorfologikSpeller() throws IOException {
        MorfologikGermanyGermanSpellerRule rule = new MorfologikGermanyGermanSpellerRule(TestTools.getMessages("en"), new GermanyGerman(), null, Collections.emptyList());
        JLanguageTool lt = new JLanguageTool(new German());
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("Hier stimmt jedes Wort!")).length);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("Hir nicht so ganz.")).length);
        Assert.assertEquals(0, rule.match(lt.getAnalyzedSentence("?berall ?u?erst b?se Umlaute!")).length);
        Assert.assertEquals(1, rule.match(lt.getAnalyzedSentence("?perall ?u?erst b?se Umlaute!")).length);
        RuleMatch[] matches = rule.match(lt.getAnalyzedSentence("da?"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("das", matches[0].getSuggestedReplacements().get(0));// "dass" would actually be better...

        Assert.assertEquals("dass", matches[0].getSuggestedReplacements().get(1));
    }
}

