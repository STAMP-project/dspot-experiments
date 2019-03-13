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
package org.languagetool.rules.uk;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Ukrainian;
import org.languagetool.rules.RuleMatch;


public class MorfologikUkrainianSpellerRuleTest {
    @Test
    public void testMorfologikSpeller() throws IOException {
        MorfologikUkrainianSpellerRule rule = new MorfologikUkrainianSpellerRule(TestTools.getMessages("uk"), new Ukrainian(), null, Collections.emptyList());
        JLanguageTool langTool = new JLanguageTool(new Ukrainian());
        // correct sentences:
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("?? ??? ?????? ??????????!")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence(",")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("123454")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("?? ??? ?????? The Beatles!")).length);
        // soft hyphen
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("\u043f\u0456\u0441\u00ad\u043d\u0456")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("\u043f\u0456\u0441\u00ad\u043d\u0456 \u043f\u0456\u0441\u00ad\u043d\u0456")).length);
        // non-breaking hyphen
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("\u043e\u0441\u044c\u2011\u043e\u0441\u044c")).length);
        // incorrect sentences:
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("?????????"));
        // check match positions:
        Assert.assertEquals(1, matches.length);
        matches = rule.match(langTool.getAnalyzedSentence("???????"));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("???????", matches[0].getSuggestedReplacements().get(0));
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("?")).length);
        // mix alphabets
        matches = rule.match(langTool.getAnalyzedSentence("????????i?"));// latin 'i'

        Assert.assertEquals(1, matches.length);
        Assert.assertEquals("??????????", matches[0].getSuggestedReplacements().get(0));
        // ??????? ??? ???????
        matches = rule.match(langTool.getAnalyzedSentence("????"));
        Assert.assertEquals(0, matches.length);
        // ????????? ?????????
        matches = rule.match(langTool.getAnalyzedSentence("??????"));
        Assert.assertEquals(1, matches.length);
        // compounding
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("????? ??? ?????-??????? ???????")).length);
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("??? ?????? ????? ?? ????????-???????")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("??? ?????? ????? ?? ????????-???????")).length);
        // dynamic tagging
        Assert.assertEquals(0, rule.match(langTool.getAnalyzedSentence("???-????????")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("????-??????????.")).length);
        Assert.assertEquals(1, rule.match(langTool.getAnalyzedSentence("??????-?????????")).length);
        // abbreviations
        RuleMatch[] match = rule.match(langTool.getAnalyzedSentence("??????? ?????? ?.?.???????? ? ?.?????????"));
        Assert.assertEquals(new ArrayList<RuleMatch>(), Arrays.asList(match));
        match = rule.match(langTool.getAnalyzedSentence("??????? ?????? ?. ?. ???????? ? ?. ?????????"));
        Assert.assertEquals(new ArrayList<RuleMatch>(), Arrays.asList(match));
        match = rule.match(langTool.getAnalyzedSentence("??????????? ???? (????. English language, English) ???????? ?? ??????????? ?????"));
        Assert.assertEquals(new ArrayList<RuleMatch>(), Arrays.asList(match));
        match = rule.match(langTool.getAnalyzedSentence("??????????? ???? (???? English language, English) ???????? ?? ??????????? ?????"));
        Assert.assertEquals(1, match.length);
        match = rule.match(langTool.getAnalyzedSentence("100 ???. ???????"));
        Assert.assertEquals(new ArrayList<RuleMatch>(), Arrays.asList(match));
        match = rule.match(langTool.getAnalyzedSentence("100 ??. ?"));
        Assert.assertEquals(new ArrayList<RuleMatch>(), Arrays.asList(match));
        match = rule.match(langTool.getAnalyzedSentence("100 ???"));
        Assert.assertEquals(new ArrayList<RuleMatch>(), Arrays.asList(match));
        match = rule.match(langTool.getAnalyzedSentence("100 ?? ?"));
        Assert.assertEquals(1, Arrays.asList(match).size());
        match = rule.match(langTool.getAnalyzedSentence("2 ????"));
        Assert.assertEquals(1, Arrays.asList(match).size());
        match = rule.match(langTool.getAnalyzedSentence("??????? ????"));
        Assert.assertEquals(0, match.length);
        match = rule.match(langTool.getAnalyzedSentence("????"));
        Assert.assertEquals(1, Arrays.asList(match).size());
    }

    @Test
    public void testProhibitedSuggestions() throws IOException {
        MorfologikUkrainianSpellerRule rule = new MorfologikUkrainianSpellerRule(TestTools.getMessages("uk"), new Ukrainian(), null, Collections.emptyList());
        JLanguageTool langTool = new JLanguageTool(new Ukrainian());
        RuleMatch[] match = rule.match(langTool.getAnalyzedSentence("??????????????"));
        Assert.assertEquals(1, match.length);
        // assertEquals(Arrays.asList("??????-????????"), match[0].getSuggestedReplacements());
        match = rule.match(langTool.getAnalyzedSentence("???????????"));
        Assert.assertEquals(1, match.length);
        Assert.assertTrue(("Should be empty: " + (match[0].getSuggestedReplacements().toString())), match[0].getSuggestedReplacements().isEmpty());
        match = rule.match(langTool.getAnalyzedSentence("????-???????"));
        Assert.assertEquals(1, match.length);
        Assert.assertEquals(Arrays.asList("???????????"), match[0].getSuggestedReplacements());
        match = rule.match(langTool.getAnalyzedSentence("?????-???????"));
        Assert.assertEquals(1, match.length);
        Assert.assertEquals(new ArrayList<String>(), match[0].getSuggestedReplacements());
        match = rule.match(langTool.getAnalyzedSentence("????-???????"));
        Assert.assertEquals(1, match.length);
        Assert.assertTrue(("Unexpected suggestions: " + (match[0].getSuggestedReplacements().toString())), match[0].getSuggestedReplacements().isEmpty());
        match = rule.match(langTool.getAnalyzedSentence("?????- ?? ??????????????????"));
        Assert.assertEquals(0, match.length);
        match = rule.match(langTool.getAnalyzedSentence("?????- ??????"));
        Assert.assertEquals(1, match.length);
    }
}

