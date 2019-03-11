/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.nn;


import org.apache.ignite.Ignite;
import org.apache.ignite.ml.optimization.updatecalculators.RPropUpdateCalculator;
import org.apache.ignite.ml.optimization.updatecalculators.SimpleGDUpdateCalculator;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for {@link MLPTrainer} that require to start the whole Ignite infrastructure.
 */
public class MLPTrainerIntegrationTest extends GridCommonAbstractTest {
    /**
     * Number of nodes in grid
     */
    private static final int NODE_COUNT = 3;

    /**
     * Ignite instance.
     */
    private Ignite ignite;

    /**
     * Test 'XOR' operation training with {@link SimpleGDUpdateCalculator}.
     */
    @Test
    public void testXORSimpleGD() {
        xorTest(new UpdatesStrategy(new SimpleGDUpdateCalculator(0.3), SimpleGDParameterUpdate::sumLocal, SimpleGDParameterUpdate::avg));
    }

    /**
     * Test 'XOR' operation training with {@link RPropUpdateCalculator}.
     */
    @Test
    public void testXORRProp() {
        xorTest(new UpdatesStrategy(new RPropUpdateCalculator(), RPropParameterUpdate::sumLocal, RPropParameterUpdate::avg));
    }

    /**
     * Test 'XOR' operation training with {@link NesterovUpdateCalculator}.
     */
    @Test
    public void testXORNesterov() {
        xorTest(new UpdatesStrategy(new org.apache.ignite.ml.optimization.updatecalculators.NesterovUpdateCalculator<MultilayerPerceptron>(0.1, 0.7), NesterovParameterUpdate::sum, NesterovParameterUpdate::avg));
    }

    /**
     * Labeled point data class.
     */
    private static class LabeledPoint {
        /**
         * X coordinate.
         */
        private final double x;

        /**
         * Y coordinate.
         */
        private final double y;

        /**
         * Point label.
         */
        private final double lb;

        /**
         * Constructs a new instance of labeled point data.
         *
         * @param x
         * 		X coordinate.
         * @param y
         * 		Y coordinate.
         * @param lb
         * 		Point label.
         */
        public LabeledPoint(double x, double y, double lb) {
            this.x = x;
            this.y = y;
            this.lb = lb;
        }
    }
}

