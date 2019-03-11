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
package org.deeplearning4j.arbiter.optimize;


import DataSetIteratorFactoryProvider.FACTORY_KEY;
import GridSearchCandidateGenerator.Mode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.BooleanSpace;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.shade.jackson.core.JsonFactory;
import org.nd4j.shade.jackson.databind.ObjectMapper;
import org.nd4j.shade.jackson.dataformat.yaml.YAMLFactory;


/**
 * Created by Alex on 02/02/2017.
 */
public class TestJson {
    private static ObjectMapper jsonMapper = TestJson.getObjectMapper(new JsonFactory());

    private static ObjectMapper yamlMapper = TestJson.getObjectMapper(new YAMLFactory());

    @Test
    public void testParameterSpaceJson() throws Exception {
        List<ParameterSpace<?>> l = new ArrayList<>();
        l.add(new org.deeplearning4j.arbiter.optimize.parameter.FixedValue(1.0));
        l.add(new org.deeplearning4j.arbiter.optimize.parameter.FixedValue(1));
        l.add(new org.deeplearning4j.arbiter.optimize.parameter.FixedValue("string"));
        l.add(new ContinuousParameterSpace((-1), 1));
        l.add(new ContinuousParameterSpace(new LogNormalDistribution(1, 1)));
        l.add(new ContinuousParameterSpace(new NormalDistribution(2, 0.01)));
        l.add(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(1, 5, 7));
        l.add(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace("first", "second", "third"));
        l.add(new IntegerParameterSpace(0, 10));
        l.add(new IntegerParameterSpace(new UniformIntegerDistribution(0, 50)));
        l.add(new BooleanSpace());
        for (ParameterSpace<?> ps : l) {
            String strJson = TestJson.jsonMapper.writeValueAsString(ps);
            String strYaml = TestJson.yamlMapper.writeValueAsString(ps);
            ParameterSpace<?> fromJson = TestJson.jsonMapper.readValue(strJson, ParameterSpace.class);
            ParameterSpace<?> fromYaml = TestJson.yamlMapper.readValue(strYaml, ParameterSpace.class);
            Assert.assertEquals(ps, fromJson);
            Assert.assertEquals(ps, fromYaml);
        }
    }

    @Test
    public void testCandidateGeneratorJson() throws Exception {
        Map<String, Object> commands = new HashMap<>();
        commands.put(FACTORY_KEY, new HashMap());
        List<CandidateGenerator> l = new ArrayList<>();
        l.add(new org.deeplearning4j.arbiter.optimize.generator.GridSearchCandidateGenerator(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(0, 1, 2, 3, 4, 5), 10, Mode.Sequential, commands));
        l.add(new org.deeplearning4j.arbiter.optimize.generator.GridSearchCandidateGenerator(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(0, 1, 2, 3, 4, 5), 10, Mode.RandomOrder, commands));
        l.add(new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(0, 1, 2, 3, 4, 5), commands));
        for (CandidateGenerator cg : l) {
            String strJson = TestJson.jsonMapper.writeValueAsString(cg);
            String strYaml = TestJson.yamlMapper.writeValueAsString(cg);
            CandidateGenerator fromJson = TestJson.jsonMapper.readValue(strJson, CandidateGenerator.class);
            CandidateGenerator fromYaml = TestJson.yamlMapper.readValue(strYaml, CandidateGenerator.class);
            Assert.assertEquals(cg, fromJson);
            Assert.assertEquals(cg, fromYaml);
        }
    }
}

