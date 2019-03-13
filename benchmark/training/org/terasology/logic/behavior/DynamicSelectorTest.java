/**
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.behavior;


import BehaviorState.FAILURE;
import BehaviorState.RUNNING;
import BehaviorState.SUCCESS;
import java.util.Arrays;
import org.junit.Test;


public class DynamicSelectorTest extends CountCallsTest {
    @Test
    public void testAllSuccess() {
        assertBT("{ dynamic:[success, success, success]}", Arrays.asList(SUCCESS, SUCCESS, SUCCESS), Arrays.asList(4, 1, 4, 1, 4, 1));
    }

    @Test
    public void testAllFail() {
        assertBT("{ dynamic:[failure, failure, failure]}", Arrays.asList(FAILURE, FAILURE, FAILURE), Arrays.asList(4, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3));
    }

    @Test
    public void testAllRunning() {
        assertBT("{ dynamic:[running, running, running]}", Arrays.asList(RUNNING, RUNNING, RUNNING), Arrays.asList(4, 1, 4, 1, 4, 1));
    }

    @Test
    public void testFailSuccess() {
        assertBT("{ dynamic:[failure, success, success]}", Arrays.asList(SUCCESS, SUCCESS, SUCCESS), Arrays.asList(4, 1, 2, 4, 1, 2, 4, 1, 2));
    }

    @Test
    public void testSuccessFail() {
        assertBT("{ dynamic:[success, failure, failure]}", Arrays.asList(SUCCESS, SUCCESS, SUCCESS), Arrays.asList(4, 1, 4, 1, 4, 1));
    }

    @Test
    public void testRunningFail() {
        assertBT("{ dynamic:[running, failure, failure]}", Arrays.asList(RUNNING, RUNNING, RUNNING), Arrays.asList(4, 1, 4, 1, 4, 1));
    }

    @Test
    public void testRunningSuccess() {
        assertBT("{ dynamic:[running, success, success]}", Arrays.asList(RUNNING, RUNNING, RUNNING), Arrays.asList(4, 1, 4, 1, 4, 1));
    }
}

