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
package org.languagetool.dev.bigdata;


import NeuralNetworkRuleEvaluator.EvalResult;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class NeuralNetworkRuleEvaluatorTest {
    @Test
    public void testConfusionSetConfig() throws Exception {
        Map<Double, NeuralNetworkRuleEvaluator.EvalResult> evaluationResults = new HashMap<>();
        evaluationResults.put(0.5, new NeuralNetworkRuleEvaluator.EvalResult("summary 1", 0.7F, 0.8F));
        evaluationResults.put(1.0, new NeuralNetworkRuleEvaluator.EvalResult("summary 2", 0.99F, 0.7F));
        evaluationResults.put(1.5, new NeuralNetworkRuleEvaluator.EvalResult("summary 3", 0.998F, 0.5F));
        Assert.assertThat(NeuralNetworkRuleEvaluator.confusionSetConfig(evaluationResults, 0.9F), Is.is("summary 2"));
        Assert.assertThat(NeuralNetworkRuleEvaluator.confusionSetConfig(evaluationResults, 0.99F), Is.is("summary 2"));
        Assert.assertThat(NeuralNetworkRuleEvaluator.confusionSetConfig(evaluationResults, 0.999F), Is.is("###"));
    }
}

