/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.checkpoint;


import PrioritizedOperatorSubtaskState.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class PrioritizedOperatorSubtaskStateTest extends TestLogger {
    private final Random random = new Random(66);

    /**
     * This tests attempts to test (almost) the full space of significantly different options for verifying and
     * prioritizing {@link OperatorSubtaskState} options for local recovery over primary/remote state handles.
     */
    @Test
    public void testPrioritization() {
        for (int i = 0; i < 81; ++i) {
            // 3^4 possible configurations.
            OperatorSubtaskState primaryAndFallback = generateForConfiguration(i);
            for (int j = 0; j < 9; ++j) {
                // we test 3^2 configurations.
                // mode 0: one valid state handle (deep copy of original).
                // mode 1: empty StateHandleCollection.
                // mode 2: one invalid state handle (e.g. wrong key group, different meta data)
                int modeFirst = j % 3;
                OperatorSubtaskState bestAlternative = createAlternativeSubtaskState(primaryAndFallback, modeFirst);
                int modeSecond = (j / 3) % 3;
                OperatorSubtaskState secondBestAlternative = createAlternativeSubtaskState(primaryAndFallback, modeSecond);
                List<OperatorSubtaskState> orderedAlternativesList = Arrays.asList(bestAlternative, secondBestAlternative);
                List<OperatorSubtaskState> validAlternativesList = new ArrayList<>(3);
                if (modeFirst == 0) {
                    validAlternativesList.add(bestAlternative);
                }
                if (modeSecond == 0) {
                    validAlternativesList.add(secondBestAlternative);
                }
                validAlternativesList.add(primaryAndFallback);
                PrioritizedOperatorSubtaskState.Builder builder = new PrioritizedOperatorSubtaskState.Builder(primaryAndFallback, orderedAlternativesList);
                PrioritizedOperatorSubtaskState prioritizedOperatorSubtaskState = builder.build();
                OperatorSubtaskState[] validAlternatives = validAlternativesList.toArray(new OperatorSubtaskState[validAlternativesList.size()]);
                OperatorSubtaskState[] onlyPrimary = new OperatorSubtaskState[]{ primaryAndFallback };
                Assert.assertTrue(checkResultAsExpected(OperatorSubtaskState::getManagedOperatorState, PrioritizedOperatorSubtaskState::getPrioritizedManagedOperatorState, prioritizedOperatorSubtaskState, ((primaryAndFallback.getManagedOperatorState().size()) == 1 ? validAlternatives : onlyPrimary)));
                Assert.assertTrue(checkResultAsExpected(OperatorSubtaskState::getManagedKeyedState, PrioritizedOperatorSubtaskState::getPrioritizedManagedKeyedState, prioritizedOperatorSubtaskState, ((primaryAndFallback.getManagedKeyedState().size()) == 1 ? validAlternatives : onlyPrimary)));
                Assert.assertTrue(checkResultAsExpected(OperatorSubtaskState::getRawOperatorState, PrioritizedOperatorSubtaskState::getPrioritizedRawOperatorState, prioritizedOperatorSubtaskState, ((primaryAndFallback.getRawOperatorState().size()) == 1 ? validAlternatives : onlyPrimary)));
                Assert.assertTrue(checkResultAsExpected(OperatorSubtaskState::getRawKeyedState, PrioritizedOperatorSubtaskState::getPrioritizedRawKeyedState, prioritizedOperatorSubtaskState, ((primaryAndFallback.getRawKeyedState().size()) == 1 ? validAlternatives : onlyPrimary)));
            }
        }
    }
}

