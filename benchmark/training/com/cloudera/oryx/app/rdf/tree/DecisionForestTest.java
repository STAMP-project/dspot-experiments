/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.rdf.tree;


import com.cloudera.oryx.app.classreg.example.NumericFeature;
import com.cloudera.oryx.app.classreg.predict.NumericPrediction;
import com.cloudera.oryx.common.OryxTest;
import org.junit.Test;


public final class DecisionForestTest extends OryxTest {
    @Test
    public void testPredict() {
        DecisionForest forest = DecisionForestTest.buildTestForest();
        NumericPrediction prediction = ((NumericPrediction) (forest.predict(new com.cloudera.oryx.app.classreg.example.Example(null, NumericFeature.forValue(0.5)))));
        assertEquals(1.0, prediction.getPrediction());
    }

    @Test
    public void testToString() {
        String s = DecisionForestTest.buildTestForest().toString();
        assertTrue(s.startsWith("(#0 >= 1.0)"));
        assertContains(s, "(#0 >= -1.0)");
    }
}

