/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class StateMachineTest {
    private enum State {

        A,
        B,
        C;}

    private StateMachine<StateMachineTest.State> machine = StateMachine.of(StateMachineTest.State.A).withTransition(StateMachineTest.State.A, StateMachineTest.State.B).withTransition(StateMachineTest.State.B, StateMachineTest.State.C);

    @Test
    public void testIsInInitialState_whenCreated() {
        Assert.assertTrue(machine.is(StateMachineTest.State.A));
    }

    @Test
    public void testChangesState_whenTransitionValid() {
        machine.next(StateMachineTest.State.B);
        Assert.assertTrue(machine.is(StateMachineTest.State.B));
        machine.next(StateMachineTest.State.C);
        Assert.assertTrue(machine.is(StateMachineTest.State.C));
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsException_whenTransitionInvalid() {
        machine.next(StateMachineTest.State.C);
    }

    @Test
    public void testStaysAtState_whenAlreadyThere() {
        machine.nextOrStay(StateMachineTest.State.A);
        Assert.assertTrue(machine.is(StateMachineTest.State.A));
    }
}

