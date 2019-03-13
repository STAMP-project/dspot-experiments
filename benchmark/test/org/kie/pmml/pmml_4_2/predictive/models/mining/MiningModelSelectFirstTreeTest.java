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
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.io.Resource;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;


@RunWith(Parameterized.class)
public class MiningModelSelectFirstTreeTest {
    private static final String PMML_SOURCE = "org/kie/pmml/pmml_4_2/test_mining_model_select_first_tree.pmml";

    private static final String MINING_MODEL = "SampleMiningModel";

    private static final String INPUT1_FIELD_NAME = "input1";

    private static final String INPUT2_FIELD_NAME = "input2";

    private static final String INPUT3_FIELD_NAME = "input3";

    private static final String OUTPUT_FIELD_NAME = "Output";

    private Optional<Double> input1;

    private Optional<Double> input2;

    private Optional<String> input3;

    private String output;

    public MiningModelSelectFirstTreeTest(Optional<Double> input1, Optional<Double> input2, Optional<String> input3, String output) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.output = output;
    }

    @Test
    public void testMiningModelSelectFirstTree() {
        final Resource res = ResourceFactory.newClassPathResource(MiningModelSelectFirstTreeTest.PMML_SOURCE);
        final PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper(MiningModelSelectFirstTreeTest.MINING_MODEL, res, null, true);
        final PMMLRequestDataBuilder rdb = new PMMLRequestDataBuilder("1234", MiningModelSelectFirstTreeTest.MINING_MODEL);
        input1.ifPresent(( x) -> rdb.addParameter(MiningModelSelectFirstTreeTest.INPUT1_FIELD_NAME, x, Double.class));
        input2.ifPresent(( x) -> rdb.addParameter(MiningModelSelectFirstTreeTest.INPUT2_FIELD_NAME, x, Double.class));
        input3.ifPresent(( x) -> rdb.addParameter(MiningModelSelectFirstTreeTest.INPUT3_FIELD_NAME, x, String.class));
        PMMLRequestData request = rdb.build();
        helper.submitRequest(request);
        helper.getResultData().iterator().forEachRemaining(( rd) -> {
            assertEquals(request.getCorrelationId(), rd.getCorrelationId());
            if ((rd.getSegmentationId()) == null) {
                assertEquals("OK", rd.getResultCode());
                String value = rd.getResultValue(OUTPUT_FIELD_NAME, "value", .class).orElse(null);
                assertNotNull(value);
                assertEquals(output, value);
            }
        });
    }
}

