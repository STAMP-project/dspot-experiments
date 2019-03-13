/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Markus Brenneis
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
package org.languagetool.rules.neuralnetwork;


import java.io.IOException;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.languagetool.AnalyzedSentence;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.TestTools;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.ScoredConfusionSet;


public class NeuralNetworkRuleTest {
    private static List<ScoredConfusionSet> confusionSets;

    private static Language language;

    private static JLanguageTool lt;

    @Test
    public void testNeuralNetworkRule() throws IOException {
        NeuralNetworkRule neuralNetworkRule = new NeuralNetworkRule(TestTools.getEnglishMessages(), NeuralNetworkRuleTest.language, NeuralNetworkRuleTest.confusionSets.get(0), ( context) -> new float[]{ -0.9F, 1.9F });
        MatcherAssert.assertThat(neuralNetworkRule.getId(), CoreMatchers.is("XX_foo_VS_bar_NEURALNETWORK"));
        AnalyzedSentence analyzedSentence = NeuralNetworkRuleTest.lt.getAnalyzedSentence("We go to the foo tomorrow.");
        RuleMatch[] ruleMatches = neuralNetworkRule.match(analyzedSentence);
        MatcherAssert.assertThat(ruleMatches.length, CoreMatchers.is(1));
        MatcherAssert.assertThat(ruleMatches[0].getMessage(), CoreMatchers.is("Our neural network thinks that 'bar' (ipsum) might be the correct word here, not 'foo' (lorem). Please check."));
        MatcherAssert.assertThat(ruleMatches[0].getSuggestedReplacements().get(0), CoreMatchers.is("bar"));
    }

    @Test
    public void testSuggestionTextRule() throws IOException {
        NeuralNetworkRule neuralNetworkRule = new NeuralNetworkRule(TestTools.getEnglishMessages(), NeuralNetworkRuleTest.language, NeuralNetworkRuleTest.confusionSets.get(0), ( context) -> new float[]{ 0.9F, -1.9F });
        MatcherAssert.assertThat(neuralNetworkRule.getId(), CoreMatchers.is("XX_foo_VS_bar_NEURALNETWORK"));
        AnalyzedSentence analyzedSentence = NeuralNetworkRuleTest.lt.getAnalyzedSentence("We go to the bar tomorrow.");
        RuleMatch[] ruleMatches = neuralNetworkRule.match(analyzedSentence);
        MatcherAssert.assertThat(ruleMatches.length, CoreMatchers.is(1));
        MatcherAssert.assertThat(ruleMatches[0].getMessage(), CoreMatchers.is("Our neural network thinks that 'foo' (lorem) might be the correct word here, not 'bar' (ipsum). Please check."));
        MatcherAssert.assertThat(ruleMatches[0].getSuggestedReplacements().get(0), CoreMatchers.is("foo"));
    }

    @Test
    public void testSuggestionTextWithoutDescription() throws IOException {
        NeuralNetworkRule neuralNetworkRule = new NeuralNetworkRule(TestTools.getEnglishMessages(), NeuralNetworkRuleTest.language, NeuralNetworkRuleTest.confusionSets.get(1), ( context) -> new float[]{ 0.9F, -1.9F });
        MatcherAssert.assertThat(neuralNetworkRule.getId(), CoreMatchers.is("XX_fizz_VS_buzz_NEURALNETWORK"));
        AnalyzedSentence analyzedSentence = NeuralNetworkRuleTest.lt.getAnalyzedSentence("We go to the buzz tomorrow.");
        RuleMatch[] ruleMatches = neuralNetworkRule.match(analyzedSentence);
        MatcherAssert.assertThat(ruleMatches.length, CoreMatchers.is(1));
        MatcherAssert.assertThat(ruleMatches[0].getMessage(), CoreMatchers.is("Our neural network thinks that 'fizz' might be the correct word here, not 'buzz'. Please check."));
        MatcherAssert.assertThat(ruleMatches[0].getSuggestedReplacements().get(0), CoreMatchers.is("fizz"));
    }
}

