/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.predictive.models;


import java.util.List;
import java.util.Map;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;


@RunWith(Parameterized.class)
public class SimpleRegressionTest extends DroolsAbstractPMMLTest {
    private static final String source1 = "org/kie/pmml/pmml_4_2/test_regression.pmml";

    private static final String source2 = "org/kie/pmml/pmml_4_2/test_regression_clax.pmml";

    private static final double COMPARISON_DELTA = 1.0E-6;

    private double fld1;

    private double fld2;

    private String fld3;

    public SimpleRegressionTest(double fld1, double fld2, String fld3) {
        this.fld1 = fld1;
        this.fld2 = fld2;
        this.fld3 = fld3;
    }

    @Test
    public void testRegression() throws Exception {
        RuleUnitExecutor executor = createExecutor(SimpleRegressionTest.source1);
        PMMLRequestData request = new PMMLRequestData("123", "LinReg");
        request.addRequestParam("fld1", fld1);
        request.addRequestParam("fld2", fld2);
        request.addRequestParam("fld3", fld3);
        PMML4Result resultHolder = new PMML4Result();
        List<String> possiblePackages = calculatePossiblePackageNames("LinReg");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator", ((InternalKnowledgeBase) (kbase)), possiblePackages);
        Assert.assertNotNull(unitClass);
        int x = executor.run(unitClass);
        data.insert(request);
        resultData.insert(resultHolder);
        executor.run(unitClass);
        Assert.assertEquals("OK", resultHolder.getResultCode());
        Assert.assertNotNull(resultHolder.getResultValue("Fld4", null));
        Double value = resultHolder.getResultValue("Fld4", "value", Double.class).orElse(null);
        Assert.assertNotNull(value);
        final double expectedValue = simpleRegressionResult(fld1, fld2, fld3);
        Assert.assertEquals(expectedValue, value, SimpleRegressionTest.COMPARISON_DELTA);
    }

    @Test
    public void testClassification() throws Exception {
        RuleUnitExecutor executor = createExecutor(SimpleRegressionTest.source2);
        PMMLRequestData request = new PMMLRequestData("123", "LinReg");
        request.addRequestParam("fld1", fld1);
        request.addRequestParam("fld2", fld2);
        request.addRequestParam("fld3", fld3);
        PMML4Result resultHolder = new PMML4Result();
        List<String> possiblePackages = calculatePossiblePackageNames("LinReg");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator", ((InternalKnowledgeBase) (kbase)), possiblePackages);
        Assert.assertNotNull(unitClass);
        data.insert(request);
        resultData.insert(resultHolder);
        executor.run(unitClass);
        Map<String, Double> probabilities = categoryProbabilities(fld1, fld2, fld3);
        String maxCategory = null;
        double maxValue = Double.MIN_VALUE;
        for (String key : probabilities.keySet()) {
            double value = probabilities.get(key);
            if (value > maxValue) {
                maxCategory = key;
                maxValue = value;
            }
        }
        Assert.assertNotNull(resultHolder.getResultValue("RegOut", null));
        Assert.assertNotNull(resultHolder.getResultValue("RegProb", null));
        Assert.assertNotNull(resultHolder.getResultValue("RegProbA", null));
        String regOut = resultHolder.getResultValue("RegOut", "value", String.class).orElse(null);
        Double regProb = resultHolder.getResultValue("RegProb", "value", Double.class).orElse(null);
        Double regProbA = resultHolder.getResultValue("RegProbA", "value", Double.class).orElse(null);
        Assert.assertEquals(("cat" + maxCategory), regOut);
        Assert.assertEquals(maxValue, regProb, SimpleRegressionTest.COMPARISON_DELTA);
        Assert.assertEquals(probabilities.get("A"), regProbA, SimpleRegressionTest.COMPARISON_DELTA);
    }

    @FunctionalInterface
    private interface RegressionInterface {
        public Double apply(double fld1, double fld2, String fld3);
    }
}

