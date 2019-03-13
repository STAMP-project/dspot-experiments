/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
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
package com.cloudera.oryx.app.rdf.decision;


import com.cloudera.oryx.app.classreg.example.Feature;
import com.cloudera.oryx.app.classreg.example.NumericFeature;
import com.cloudera.oryx.common.OryxTest;
import org.junit.Test;


/**
 * Tests {@link NumericDecision}.
 */
public final class NumericDecisionTest extends OryxTest {
    @Test
    public void testDecisionBasics() {
        NumericDecision decision = new NumericDecision(0, (-1.5), false);
        assertEquals(0, decision.getFeatureNumber());
        assertEquals((-1.5), decision.getThreshold());
        assertFalse(decision.getDefaultDecision());
        assertEquals(FeatureType.NUMERIC, decision.getType());
    }

    @Test
    public void testDecision() {
        Decision decision = new NumericDecision(0, (-3.1), true);
        assertFalse(decision.isPositive(new com.cloudera.oryx.app.classreg.example.Example(null, NumericFeature.forValue((-3.5)))));
        assertTrue(decision.isPositive(new com.cloudera.oryx.app.classreg.example.Example(null, NumericFeature.forValue((-3.1)))));
        assertTrue(decision.isPositive(new com.cloudera.oryx.app.classreg.example.Example(null, NumericFeature.forValue((-3.0)))));
        assertTrue(decision.isPositive(new com.cloudera.oryx.app.classreg.example.Example(null, NumericFeature.forValue(3.1))));
        assertTrue(decision.isPositive(new com.cloudera.oryx.app.classreg.example.Example(null, new Feature[]{ null })));
    }

    @Test
    public void testToString() {
        NumericDecision decision = new NumericDecision(0, 0.5, true);
        assertEquals("(#0 >= 0.5)", decision.toString());
    }

    @Test
    public void testEqualsHashCode() {
        NumericDecision a = new NumericDecision(0, 0.5, true);
        NumericDecision b = new NumericDecision(0, 0.5, true);
        NumericDecision c = new NumericDecision(1, 0.5, true);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
    }
}

