/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.nn.misc;


import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class TestNetConversion extends BaseDL4JTest {
    @Test
    public void testMlnToCompGraph() {
        Nd4j.getRandom().setSeed(12345);
        for (int i = 0; i < 3; i++) {
            MultiLayerNetwork n;
            switch (i) {
                case 0 :
                    n = getNet1(false);
                    break;
                case 1 :
                    n = getNet1(true);
                    break;
                case 2 :
                    n = getNet2();
                    break;
                default :
                    throw new RuntimeException();
            }
            INDArray in = (i <= 1) ? Nd4j.rand(new int[]{ 8, 3, 10, 10 }) : Nd4j.rand(new int[]{ 8, 5, 10 });
            INDArray labels = (i <= 1) ? Nd4j.rand(new int[]{ 8, 10 }) : Nd4j.rand(new int[]{ 8, 10, 10 });
            ComputationGraph cg = n.toComputationGraph();
            INDArray out1 = n.output(in);
            INDArray out2 = cg.outputSingle(in);
            Assert.assertEquals(out1, out2);
            n.setInput(in);
            n.setLabels(labels);
            cg.setInputs(in);
            cg.setLabels(labels);
            n.computeGradientAndScore();
            cg.computeGradientAndScore();
            Assert.assertEquals(n.score(), cg.score(), 1.0E-6);
            Assert.assertEquals(n.gradient().gradient(), cg.gradient().gradient());
            n.fit(in, labels);
            cg.fit(new INDArray[]{ in }, new INDArray[]{ labels });
            Assert.assertEquals(n.params(), cg.params());
        }
    }
}

