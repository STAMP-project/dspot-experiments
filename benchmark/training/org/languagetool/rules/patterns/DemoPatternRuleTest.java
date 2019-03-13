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
package org.languagetool.rules.patterns;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.TestTools;
import org.languagetool.language.Demo;
import org.languagetool.rules.RuleMatch;


public class DemoPatternRuleTest extends PatternRuleTest {
    private static final Language language = TestTools.getDemoLanguage();

    @Test
    public void testRules() throws IOException {
        runTestForLanguage(new Demo());
    }

    @Test
    public void testGrammarRulesFromXML2() throws IOException {
        new PatternRule("-1", DemoPatternRuleTest.language, Collections.<PatternToken>emptyList(), "", "", "");
    }

    @Test
    public void testMakeSuggestionUppercase() throws IOException {
        JLanguageTool langTool = new JLanguageTool(DemoPatternRuleTest.language);
        PatternToken patternToken = new PatternToken("Were", false, false, false);
        String message = "Did you mean: <suggestion>where</suggestion> or <suggestion>we</suggestion>?";
        PatternRule rule = new PatternRule("MY_ID", DemoPatternRuleTest.language, Collections.singletonList(patternToken), "desc", message, "msg");
        RuleMatch[] matches = rule.match(langTool.getAnalyzedSentence("Were are in the process of ..."));
        Assert.assertEquals(1, matches.length);
        RuleMatch match = matches[0];
        List<String> replacements = match.getSuggestedReplacements();
        Assert.assertEquals(2, replacements.size());
        Assert.assertEquals("Where", replacements.get(0));
        Assert.assertEquals("We", replacements.get(1));
    }

    @Test
    public void testRule() throws IOException {
        PatternRule pr;
        RuleMatch[] matches;
        JLanguageTool langTool = new JLanguageTool(DemoPatternRuleTest.language);
        pr = makePatternRule("one");
        matches = pr.match(langTool.getAnalyzedSentence("A non-matching sentence."));
        Assert.assertEquals(0, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("A matching sentence with one match."));
        Assert.assertEquals(1, matches.length);
        Assert.assertEquals(25, matches[0].getFromPos());
        Assert.assertEquals(28, matches[0].getToPos());
        // these two are not set if the rule is called standalone (not via
        // JLanguageTool):
        Assert.assertEquals((-1), matches[0].getColumn());
        Assert.assertEquals((-1), matches[0].getLine());
        Assert.assertEquals("ID1", matches[0].getRule().getId());
        Assert.assertTrue(matches[0].getMessage().equals("user visible message"));
        Assert.assertTrue(matches[0].getShortMessage().equals("short comment"));
        matches = pr.match(langTool.getAnalyzedSentence("one one and one: three matches"));
        Assert.assertEquals(3, matches.length);
        pr = makePatternRule("one two");
        matches = pr.match(langTool.getAnalyzedSentence("this is one not two"));
        Assert.assertEquals(0, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("this is two one"));
        Assert.assertEquals(0, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("this is one two three"));
        Assert.assertEquals(1, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("one two"));
        Assert.assertEquals(1, matches.length);
        pr = makePatternRule("one|foo|xxxx two", false, true);
        matches = pr.match(langTool.getAnalyzedSentence("one foo three"));
        Assert.assertEquals(0, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("one two"));
        Assert.assertEquals(1, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("foo two"));
        Assert.assertEquals(1, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("one foo two"));
        Assert.assertEquals(1, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("y x z one two blah foo"));
        Assert.assertEquals(1, matches.length);
        pr = makePatternRule("one|foo|xxxx two|yyy", false, true);
        matches = pr.match(langTool.getAnalyzedSentence("one, yyy"));
        Assert.assertEquals(0, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("one yyy"));
        Assert.assertEquals(1, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("xxxx two"));
        Assert.assertEquals(1, matches.length);
        matches = pr.match(langTool.getAnalyzedSentence("xxxx yyy"));
        Assert.assertEquals(1, matches.length);
    }

    @Test
    public void testSentenceStart() throws IOException {
        JLanguageTool langTool = new JLanguageTool(DemoPatternRuleTest.language);
        PatternRule pr = makePatternRule("SENT_START One");
        Assert.assertEquals(0, pr.match(langTool.getAnalyzedSentence("Not One word.")).length);
        Assert.assertEquals(1, pr.match(langTool.getAnalyzedSentence("One word.")).length);
    }

    @Test
    public void testFormatMultipleSynthesis() throws Exception {
        String[] suggestions1 = new String[]{ "blah blah", "foo bar" };
        Assert.assertEquals("This is how you should write: <suggestion>blah blah</suggestion>, <suggestion>foo bar</suggestion>.", PatternRuleMatcher.formatMultipleSynthesis(suggestions1, "This is how you should write: <suggestion>", "</suggestion>."));
        String[] suggestions2 = new String[]{ "test", " " };
        Assert.assertEquals("This is how you should write: <suggestion>test</suggestion>, <suggestion> </suggestion>.", PatternRuleMatcher.formatMultipleSynthesis(suggestions2, "This is how you should write: <suggestion>", "</suggestion>."));
    }
}

