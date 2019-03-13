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
import java.util.HashMap;
import java.util.Map;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.optimize.runner.listener.impl.LoggingStatusListener;
import org.junit.Test;


/**
 * Test random search on the Branin Function:
 * http://www.sfu.ca/~ssurjano/branin.html
 */
public class TestRandomSearch {
    @Test
    public void test() throws Exception {
        Map<String, Object> commands = new HashMap<>();
        commands.put(FACTORY_KEY, new HashMap());
        // Define configuration:
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(new BraninFunction.BraninSpace(), commands);
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(candidateGenerator).scoreFunction(new BraninFunction.BraninScoreFunction()).terminationConditions(new MaxCandidatesCondition(50)).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new BraninFunction.BraninTaskCreator());
        runner.addListeners(new LoggingStatusListener());
        runner.execute();
        System.out.println("----- Complete -----");
    }
}

