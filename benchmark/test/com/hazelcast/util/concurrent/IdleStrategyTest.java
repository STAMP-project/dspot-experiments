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
package com.hazelcast.util.concurrent;


import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IdleStrategyTest {
    private static final int MAX_CALLS = 10;

    @Parameterized.Parameter
    public IdleStrategyTest.StrategyToTest strategyToTest;

    private IdleStrategy idler;

    enum StrategyToTest {

        NO_OP() {
            IdleStrategy create() {
                return new NoOpIdleStrategy();
            }
        },
        BUSY_SPIN() {
            IdleStrategy create() {
                return new BusySpinIdleStrategy();
            }
        },
        BACK_OFF() {
            IdleStrategy create() {
                return new BackoffIdleStrategy(1, 1, 1, 2);
            }
        };
        abstract IdleStrategy create();
    }

    @Test
    public void when_idle_thenReturnTrueEventually() {
        for (int i = 0; i < (IdleStrategyTest.MAX_CALLS); i++) {
            if (idler.idle(i)) {
                return;
            }
        }
        Assert.fail();
    }
}

