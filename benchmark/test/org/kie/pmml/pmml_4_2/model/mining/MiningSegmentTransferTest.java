/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.pmml_4_2.model.mining;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;


public class MiningSegmentTransferTest {
    private PMML4Result simpleResult;

    private PMML4Result complexResult;

    public class MyResult {
        private String varA;

        private int varB;

        public MyResult(String varA, int varB) {
            this.varA = varA;
            this.varB = varB;
        }

        public String getVarA() {
            return varA;
        }

        public void setVarA(String varA) {
            this.varA = varA;
        }

        public int getVarB() {
            return varB;
        }

        public void setVarB(int varB) {
            this.varB = varB;
        }
    }

    @Test
    public void testSimpleResult() {
        MiningSegmentTransfer mst = new MiningSegmentTransfer(simpleResult, "SEGMENT_2");
        doBaselineAssertions(simpleResult, mst);
    }

    @Test
    public void testSimpleWithFieldNamesMap() {
        MiningSegmentTransfer mst = new MiningSegmentTransfer(simpleResult, "SEGMENT_2");
        mst.addResultToRequestMapping("var1", "someVarA");
        doBaselineAssertions(simpleResult, mst);
        Assert.assertEquals(1, mst.getResultFieldNameToRequestFieldName().size());
    }

    @Test
    public void testComplexResult() {
        MiningSegmentTransfer mst = new MiningSegmentTransfer(complexResult, "SEGMENT_2");
        mst.addResultToRequestMapping("firstObject", "object1");
        mst.addResultToRequestMapping("myComplex.varA", "stringFromMyComplex");
        mst.addResultToRequestMapping("myComplex.varB", "intValue");
        doBaselineAssertions(complexResult, mst);
        Assert.assertEquals(3, mst.getResultFieldNameToRequestFieldName().size());
        PMMLRequestData rqst = mst.getOutboundRequest();
        Assert.assertNotNull(rqst);
        Assert.assertEquals(complexResult.getCorrelationId(), rqst.getCorrelationId());
        Map<String, ParameterInfo> params = rqst.getMappedRequestParams();
        Assert.assertEquals(complexResult.getResultValue("firstObject", null), params.get("object1").getValue());
        Assert.assertEquals(complexResult.getResultValue("myComplex", "varA"), params.get("stringFromMyComplex").getValue());
        Assert.assertEquals(complexResult.getResultValue("myComplex", "varB"), params.get("intValue").getValue());
        System.out.println(rqst);
        System.out.println(complexResult);
    }
}

