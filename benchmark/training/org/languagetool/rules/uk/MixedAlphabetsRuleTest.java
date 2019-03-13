/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2013 Andriy Rysin
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
package org.languagetool.rules.uk;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Ukrainian;
import org.languagetool.rules.RuleMatch;


public class MixedAlphabetsRuleTest {
    @Test
    public void testRule() throws IOException {
        final MixedAlphabetsRule rule = new MixedAlphabetsRule(TestTools.getMessages("uk"));
        final JLanguageTool langTool = new JLanguageTool(new Ukrainian());
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("??????")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("not mixed")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("123454")).length);
        // incorrect sentences:
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("??i???"));// latin i

        // check match positions:
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("??????"), matches[0].getSuggestedReplacements());
        matches = rule.match(langTool.getAnalyzedSentence("m??ed"));// cyrillic i and x

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("mixed"), matches[0].getSuggestedReplacements());
        matches = rule.match(langTool.getAnalyzedSentence("??????"));// umlaut instead of accented ?

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("???????"), matches[0].getSuggestedReplacements());
        matches = rule.match(langTool.getAnalyzedSentence("??????? i ??????????"));// latin i

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("?"), matches[0].getSuggestedReplacements());
        matches = rule.match(langTool.getAnalyzedSentence("X?"));// cyrillic ? and latin X

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("XI"), matches[0].getSuggestedReplacements());
        matches = rule.match(langTool.getAnalyzedSentence("?I"));// cyrillic X and latin I

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("XI"), matches[0].getSuggestedReplacements());
        matches = rule.match(langTool.getAnalyzedSentence("??"));// cyrillic both X and I used for latin number

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(Arrays.asList("XI"), matches[0].getSuggestedReplacements());
        matches = rule.match(langTool.getAnalyzedSentence("???????? ??? ???????? ?."));// cyrillic B

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("B", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("????? ?"));// cyrillic ?

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("A", matches[0].getSuggestedReplacements().get(0));
        matches = rule.match(langTool.getAnalyzedSentence("?? 0,6??."));// cyrillic ?

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("0,6?C", matches[0].getSuggestedReplacements().get(0));
    }
}

