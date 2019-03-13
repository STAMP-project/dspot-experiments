/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
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


import ITSIssueType.Addition;
import ITSIssueType.Characters;
import ITSIssueType.Duplication;
import ITSIssueType.Grammar;
import ITSIssueType.Uncategorized;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.chunking.ChunkTag;
import org.languagetool.rules.IncorrectExample;
import org.languagetool.rules.Rule;


public class PatternRuleLoaderTest {
    @Test
    public void testGetRules() throws Exception {
        PatternRuleLoader prg = new PatternRuleLoader();
        String name = "/xx/grammar.xml";
        List<AbstractPatternRule> rules = prg.getRules(JLanguageTool.getDataBroker().getFromRulesDirAsStream(name), name);
        Assert.assertTrue(((rules.size()) >= 30));
        Rule demoRule1 = getRuleById("DEMO_RULE", rules);
        Assert.assertEquals("http://fake-server.org/foo-bar-error-explained", demoRule1.getUrl().toString());
        Assert.assertEquals("[This is <marker>fuu bah</marker>.]", demoRule1.getCorrectExamples().toString());
        List<IncorrectExample> incorrectExamples = demoRule1.getIncorrectExamples();
        Assert.assertEquals(1, incorrectExamples.size());
        Assert.assertEquals("This is <marker>foo bar</marker>.", incorrectExamples.get(0).getExample());
        Rule demoRule2 = getRuleById("API_OUTPUT_TEST_RULE", rules);
        Assert.assertNull(demoRule2.getUrl());
        Assert.assertEquals(Uncategorized, demoRule1.getLocQualityIssueType());
        Assert.assertEquals("tag inheritance failed", Addition, getRuleById("TEST_GO", rules).getLocQualityIssueType());
        Assert.assertEquals("tag inheritance overwrite failed", Uncategorized, getRuleById("TEST_PHRASES1", rules).getLocQualityIssueType());
        Assert.assertEquals("tag inheritance overwrite failed", Characters, getRuleById("test_include", rules).getLocQualityIssueType());
        List<Rule> groupRules1 = getRulesById("test_spacebefore", rules);
        Assert.assertEquals("tag inheritance form category failed", Addition, groupRules1.get(0).getLocQualityIssueType());
        Assert.assertEquals("tag inheritance overwrite failed", Duplication, groupRules1.get(1).getLocQualityIssueType());
        List<Rule> groupRules2 = getRulesById("test_unification_with_negation", rules);
        Assert.assertEquals("tag inheritance from rulegroup failed", Grammar, groupRules2.get(0).getLocQualityIssueType());
        Set<String> categories = getCategoryNames(rules);
        Assert.assertEquals(5, categories.size());
        Assert.assertTrue(categories.contains("misc"));
        Assert.assertTrue(categories.contains("otherCategory"));
        Assert.assertTrue(categories.contains("Test tokens with min and max attributes"));
        Assert.assertTrue(categories.contains("A category that's off by default"));
        PatternRule demoRuleWithChunk = ((PatternRule) (getRuleById("DEMO_CHUNK_RULE", rules)));
        List<PatternToken> patternTokens = demoRuleWithChunk.getPatternTokens();
        Assert.assertEquals(2, patternTokens.size());
        Assert.assertEquals(null, patternTokens.get(1).getPOStag());
        Assert.assertEquals(new ChunkTag("B-NP-singular"), patternTokens.get(1).getChunkTag());
        List<Rule> orRules = getRulesById("GROUP_WITH_URL", rules);
        Assert.assertEquals(3, orRules.size());
        Assert.assertEquals("http://fake-server.org/rule-group-url", orRules.get(0).getUrl().toString());
        Assert.assertEquals("http://fake-server.org/rule-group-url-overwrite", orRules.get(1).getUrl().toString());
        Assert.assertEquals("http://fake-server.org/rule-group-url", orRules.get(2).getUrl().toString());
        Assert.assertEquals("short message on rule group", getShortMessage());
        Assert.assertEquals("overwriting short message", getShortMessage());
        Assert.assertEquals("short message on rule group", getShortMessage());
        // make sure URLs don't leak to the next rule:
        List<Rule> orRules2 = getRulesById("OR_GROUPS", rules);
        for (Rule rule : orRules2) {
            Assert.assertNull("http://fake-server.org/rule-group-url", rule.getUrl());
        }
        Rule nextRule = getRuleById("DEMO_CHUNK_RULE", rules);
        Assert.assertNull("http://fake-server.org/rule-group-url", nextRule.getUrl());
    }
}

