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


import com.cloudera.oryx.app.classreg.predict.NumericPrediction;
import com.cloudera.oryx.app.classreg.predict.Prediction;
import com.cloudera.oryx.common.OryxTest;
import org.junit.Test;


public final class TerminalNodeTest extends OryxTest {
    @Test
    public void testNode() {
        Prediction prediction = new NumericPrediction(1.2, 3);
        TerminalNode node = new TerminalNode("1", prediction);
        assertTrue(node.isTerminal());
        assertSame(prediction, node.getPrediction());
        assertEquals(3, node.getCount());
    }

    @Test
    public void testEquals() {
        Prediction a = new NumericPrediction(1.5, 10);
        Prediction b = new NumericPrediction(1.5, 10);
        TerminalNode ta = new TerminalNode("a", a);
        TerminalNode tb = new TerminalNode("b", b);
        assertEquals(ta.hashCode(), tb.hashCode());
        assertEquals(ta, tb);
    }
}

