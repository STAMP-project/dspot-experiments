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
package org.deeplearning4j.rl4j.learning.sync.qlearning;


import QLearning.QLConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class QLConfigurationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void serialize() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        QLearning.QLConfiguration qlConfiguration = // Random seed
        // Max step By epoch
        // Max step
        // Max size of experience replay
        // size of batches
        // target update (hard)
        // num step noop warmup
        // reward scaling
        // gamma
        // td error clipping
        // min epsilon
        // num step for eps greedy anneal
        // double DQN
        new QLearning.QLConfiguration(123, 200, 8000, 150000, 32, 500, 10, 0.01, 0.99, 1.0, 0.1F, 10000, true);
        // Should not throw..
        String json = mapper.writeValueAsString(qlConfiguration);
        QLearning.QLConfiguration cnf = mapper.readValue(json, QLConfiguration.class);
    }
}

