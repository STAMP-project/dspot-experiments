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


public final class DecisionTreeTest extends OryxTest {
    @Test
    public void testPredict() {
        DecisionTree tree = DecisionTreeTest.buildTestTree();
        NumericPrediction prediction = ((NumericPrediction) (tree.predict(new com.cloudera.oryx.app.classreg.example.Example(null, NumericFeature.forValue(0.5)))));
        assertEquals(1.0, prediction.getPrediction());
    }

    @Test
    public void testFindTerminal() {
        DecisionTree tree = DecisionTreeTest.buildTestTree();
        TerminalNode node = tree.findTerminal(new com.cloudera.oryx.app.classreg.example.Example(null, NumericFeature.forValue(0.5)));
        NumericPrediction prediction = ((NumericPrediction) (node.getPrediction()));
        assertEquals(1.0, prediction.getPrediction());
    }

    @Test
    public void testFindByID() {
        DecisionTree tree = DecisionTreeTest.buildTestTree();
        TerminalNode node = ((TerminalNode) (tree.findByID("r-+")));
        assertEquals(1.0, getPrediction());
    }

    @Test
    public void testToString() {
        String s = DecisionTreeTest.buildTestTree().toString();
        assertTrue(s.startsWith("(#0 >= 1.0)"));
        assertContains(s, "(#0 >= -1.0)");
    }
}

