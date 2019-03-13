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
package org.languagetool.rules.ca;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;


/**
 *
 *
 * @author Jaume Ortol?
 */
public class SimpleReplaceVerbsRuleTest {
    private SimpleReplaceVerbsRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        // correct sentences:
        // incorrect sentences:
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("abarca"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("abra?a", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("abasta", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("agafa", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("estreny", matches[0].getSuggestedReplacements().get(3));
        Assert.assertEquals("compr?n", matches[0].getSuggestedReplacements().get(4));
        Assert.assertEquals("compr?n", matches[0].getSuggestedReplacements().get(5));
        Assert.assertEquals("inclou", matches[0].getSuggestedReplacements().get(6));
        matches = rule.match(langTool.getAnalyzedSentence("abarcaven"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("abra?aven", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("abastaven", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("agafaven", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("estrenyien", matches[0].getSuggestedReplacements().get(3));
        Assert.assertEquals("comprenien", matches[0].getSuggestedReplacements().get(4));
        Assert.assertEquals("inclo?en", matches[0].getSuggestedReplacements().get(5));
        matches = rule.match(langTool.getAnalyzedSentence("abarqu?ssim"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("abrac?ssim", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("antoj?"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("pass? pel cap", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("pass? pel mag?", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("antull?", matches[0].getSuggestedReplacements().get(2));
        matches = rule.match(langTool.getAnalyzedSentence("alardeaven"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("feien gala", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("feien ostentaci?", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("alardejo"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("faig gala", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("faig ostentaci?", matches[0].getSuggestedReplacements().get(1));
        matches = rule.match(langTool.getAnalyzedSentence("aclares"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("aclareixes", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("aclarisques", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("aclaresques", matches[0].getSuggestedReplacements().get(2));
        matches = rule.match(langTool.getAnalyzedSentence("atossigues"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("acuites", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("apresses", matches[0].getSuggestedReplacements().get(1));
        // assertEquals("dones pressa", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("d?nes pressa", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("atabuixes", matches[0].getSuggestedReplacements().get(3));
        Assert.assertEquals("aclapares", matches[0].getSuggestedReplacements().get(4));
        Assert.assertEquals("afeixugues", matches[0].getSuggestedReplacements().get(5));
        Assert.assertEquals("mareges", matches[0].getSuggestedReplacements().get(6));
        Assert.assertEquals("afanyes", matches[0].getSuggestedReplacements().get(7));
        matches = rule.match(langTool.getAnalyzedSentence("agobiem"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("aclaparem", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("atabalem", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("angoixem", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("estressem", matches[0].getSuggestedReplacements().get(3));
        Assert.assertEquals("(estar) molt a sobre", matches[0].getSuggestedReplacements().get(4));
        Assert.assertEquals("(cansar) molt", matches[0].getSuggestedReplacements().get(5));
        Assert.assertEquals("(ser) molt pesat", matches[0].getSuggestedReplacements().get(6));
        matches = rule.match(langTool.getAnalyzedSentence("agobi?s"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("aclaparis", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals("atabalis", matches[0].getSuggestedReplacements().get(1));
        Assert.assertEquals("angoixis", matches[0].getSuggestedReplacements().get(2));
        Assert.assertEquals("estressis", matches[0].getSuggestedReplacements().get(3));
        Assert.assertEquals("(estar) molt a sobre", matches[0].getSuggestedReplacements().get(4));
        Assert.assertEquals("(cansar) molt", matches[0].getSuggestedReplacements().get(5));
        Assert.assertEquals("(ser) molt pesat", matches[0].getSuggestedReplacements().get(6));
    }
}

