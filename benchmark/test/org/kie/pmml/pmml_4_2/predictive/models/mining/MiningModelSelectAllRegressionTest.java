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
package org.kie.pmml.pmml_4_2.predictive.models.mining;


import PMML4ExecutionHelper.PMML4ExecutionHelperFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;
import org.kie.pmml.pmml_4_2.model.mining.SegmentExecution;


@RunWith(Parameterized.class)
public class MiningModelSelectAllRegressionTest {
    private static final String PMML_SOURCE = "org/kie/pmml/pmml_4_2/test_mining_model_selectall_regression.pmml";

    private static final String MINING_MODEL = "SampleMiningModel";

    private static final String INPUT1_FIELD_NAME = "input1";

    private static final String INPUT2_FIELD_NAME = "input2";

    private static final String INPUT3_FIELD_NAME = "input3";

    private static final String OUTPUT_FIELD_NAME = "Result";

    private static final double COMPARISON_DELTA = 0.001;

    private double input1;

    private double input2;

    private double input3;

    public MiningModelSelectAllRegressionTest(double input1, double input2, double input3) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
    }

    @Test
    public void testMiningModelSelectAllRegression() {
        final Resource res = ResourceFactory.newClassPathResource(MiningModelSelectAllRegressionTest.PMML_SOURCE);
        final PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper(MiningModelSelectAllRegressionTest.MINING_MODEL, res, null, true);
        final PMMLRequestDataBuilder rdb = new PMMLRequestDataBuilder("1234", MiningModelSelectAllRegressionTest.MINING_MODEL).addParameter(MiningModelSelectAllRegressionTest.INPUT1_FIELD_NAME, input1, Double.class).addParameter(MiningModelSelectAllRegressionTest.INPUT2_FIELD_NAME, input2, Double.class).addParameter(MiningModelSelectAllRegressionTest.INPUT3_FIELD_NAME, input3, Double.class);
        PMMLRequestData request = rdb.build();
        helper.submitRequest(request);
        final Map<String, Double> expected = expectedResults(input1, input2, input3);
        final Map<String, Double> executedSegments = new HashMap<>();
        for (Iterator<SegmentExecution> iter = helper.getChildModelSegments().iterator(); iter.hasNext();) {
            SegmentExecution cms = iter.next();
            executedSegments.put(cms.getSegmentId(), cms.getResult().getResultValue(MiningModelSelectAllRegressionTest.OUTPUT_FIELD_NAME, "value", Double.class).orElse(null));
        }
        compareMaps(expected, executedSegments);
    }
}

