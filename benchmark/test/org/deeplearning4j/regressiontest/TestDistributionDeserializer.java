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
package org.deeplearning4j.regressiontest;


import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.shade.jackson.databind.ObjectMapper;


/**
 * Created by Alex on 08/05/2017.
 */
public class TestDistributionDeserializer extends BaseDL4JTest {
    @Test
    public void testDistributionDeserializer() throws Exception {
        // Test current format:
        Distribution[] distributions = new Distribution[]{ new NormalDistribution(3, 0.5), new UniformDistribution((-2), 1), new GaussianDistribution(2, 1.0), new BinomialDistribution(10, 0.3) };
        ObjectMapper om = NeuralNetConfiguration.mapper();
        for (Distribution d : distributions) {
            String json = om.writeValueAsString(d);
            Distribution fromJson = om.readValue(json, Distribution.class);
            Assert.assertEquals(d, fromJson);
        }
    }

    @Test
    public void testDistributionDeserializerLegacyFormat() throws Exception {
        ObjectMapper om = NeuralNetConfiguration.mapper();
        String normalJson = "{\n" + (((("          \"normal\" : {\n" + "            \"mean\" : 0.1,\n") + "            \"std\" : 1.2\n") + "          }\n") + "        }");
        Distribution nd = om.readValue(normalJson, Distribution.class);
        Assert.assertTrue((nd instanceof NormalDistribution));
        NormalDistribution normDist = ((NormalDistribution) (nd));
        Assert.assertEquals(0.1, normDist.getMean(), 1.0E-6);
        Assert.assertEquals(1.2, normDist.getStd(), 1.0E-6);
        String uniformJson = "{\n" + (((("                \"uniform\" : {\n" + "                  \"lower\" : -1.1,\n") + "                  \"upper\" : 2.2\n") + "                }\n") + "              }");
        Distribution ud = om.readValue(uniformJson, Distribution.class);
        Assert.assertTrue((ud instanceof UniformDistribution));
        UniformDistribution uniDist = ((UniformDistribution) (ud));
        Assert.assertEquals((-1.1), uniDist.getLower(), 1.0E-6);
        Assert.assertEquals(2.2, uniDist.getUpper(), 1.0E-6);
        String gaussianJson = "{\n" + (((("                \"gaussian\" : {\n" + "                  \"mean\" : 0.1,\n") + "                  \"std\" : 1.2\n") + "                }\n") + "              }");
        Distribution gd = om.readValue(gaussianJson, Distribution.class);
        Assert.assertTrue((gd instanceof GaussianDistribution));
        GaussianDistribution gDist = ((GaussianDistribution) (gd));
        Assert.assertEquals(0.1, gDist.getMean(), 1.0E-6);
        Assert.assertEquals(1.2, gDist.getStd(), 1.0E-6);
        String bernoulliJson = "{\n" + (((("                \"binomial\" : {\n" + "                  \"numberOfTrials\" : 10,\n") + "                  \"probabilityOfSuccess\" : 0.3\n") + "                }\n") + "              }");
        Distribution bd = om.readValue(bernoulliJson, Distribution.class);
        Assert.assertTrue((bd instanceof BinomialDistribution));
        BinomialDistribution binDist = ((BinomialDistribution) (bd));
        Assert.assertEquals(10, binDist.getNumberOfTrials());
        Assert.assertEquals(0.3, binDist.getProbabilityOfSuccess(), 1.0E-6);
    }
}

