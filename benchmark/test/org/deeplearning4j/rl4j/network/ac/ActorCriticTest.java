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
package org.deeplearning4j.rl4j.network.ac;


import ActorCriticFactorySeparateStdDense.Configuration;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.impl.ActivationSoftmax;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.RmsProp;


/**
 *
 *
 * @author saudet
 */
public class ActorCriticTest {
    public static Configuration NET_CONF = // number of layers
    // number of hidden nodes
    // l2 regularization
    new ActorCriticFactorySeparateStdDense.Configuration(4, 32, 0.001, new RmsProp(5.0E-4), null, false);

    public static ActorCriticFactoryCompGraphStdDense.Configuration NET_CONF_CG = // number of layers
    // number of hidden nodes
    // l2 regularization
    new ActorCriticFactoryCompGraphStdDense.Configuration(2, 128, 1.0E-5, new RmsProp(0.005), null, true);

    @Test
    public void testModelLoadSave() throws IOException {
        ActorCriticSeparate acs = buildActorCritic(new int[]{ 7 }, 5);
        File fileValue = File.createTempFile("rl4j-value-", ".model");
        File filePolicy = File.createTempFile("rl4j-policy-", ".model");
        acs.save(fileValue.getAbsolutePath(), filePolicy.getAbsolutePath());
        ActorCriticSeparate acs2 = ActorCriticSeparate.load(fileValue.getAbsolutePath(), filePolicy.getAbsolutePath());
        Assert.assertEquals(acs.valueNet, acs2.valueNet);
        Assert.assertEquals(acs.policyNet, acs2.policyNet);
        ActorCriticCompGraph accg = new ActorCriticFactoryCompGraphStdDense(ActorCriticTest.NET_CONF_CG).buildActorCritic(new int[]{ 37 }, 43);
        File file = File.createTempFile("rl4j-cg-", ".model");
        accg.save(file.getAbsolutePath());
        ActorCriticCompGraph accg2 = ActorCriticCompGraph.load(file.getAbsolutePath());
        Assert.assertEquals(accg.cg, accg2.cg);
    }

    @Test
    public void testLoss() {
        ActivationSoftmax activation = new ActivationSoftmax();
        ActorCriticLoss loss = new ActorCriticLoss();
        double n = 10;
        double eps = 1.0E-5;
        double maxRelError = 0.001;
        for (double i = eps; i < n; i++) {
            for (double j = eps; j < n; j++) {
                INDArray labels = Nd4j.create(new double[]{ i / n, 1 - (i / n) }, new long[]{ 1, 2 });
                INDArray output = Nd4j.create(new double[]{ j / n, 1 - (j / n) }, new long[]{ 1, 2 });
                INDArray gradient = loss.computeGradient(labels, output, activation, null);
                output = Nd4j.create(new double[]{ j / n, 1 - (j / n) }, new long[]{ 1, 2 });
                double score = loss.computeScore(labels, output, activation, null, false);
                INDArray output1 = Nd4j.create(new double[]{ (j / n) + eps, 1 - (j / n) }, new long[]{ 1, 2 });
                double score1 = loss.computeScore(labels, output1, activation, null, false);
                INDArray output2 = Nd4j.create(new double[]{ j / n, (1 - (j / n)) + eps }, new long[]{ 1, 2 });
                double score2 = loss.computeScore(labels, output2, activation, null, false);
                double gradient1 = (score1 - score) / eps;
                double gradient2 = (score2 - score) / eps;
                double error1 = gradient1 - (gradient.getDouble(0));
                double error2 = gradient2 - (gradient.getDouble(1));
                double relError1 = error1 / (gradient.getDouble(0));
                double relError2 = error2 / (gradient.getDouble(1));
                System.out.println((((((gradient.getDouble(0)) + "  ") + gradient1) + " ") + relError1));
                System.out.println((((((gradient.getDouble(1)) + "  ") + gradient2) + " ") + relError2));
                Assert.assertTrue((((gradient.getDouble(0)) < maxRelError) || ((Math.abs(relError1)) < maxRelError)));
                Assert.assertTrue((((gradient.getDouble(1)) < maxRelError) || ((Math.abs(relError2)) < maxRelError)));
            }
        }
    }
}

