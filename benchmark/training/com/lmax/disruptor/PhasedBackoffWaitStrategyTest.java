/**
 * Copyright 2012 LMAX Ltd.
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
package com.lmax.disruptor;


import com.lmax.disruptor.support.WaitStrategyTestUtil;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class PhasedBackoffWaitStrategyTest {
    @Test
    public void shouldHandleImmediateSequenceChange() throws Exception {
        WaitStrategyTestUtil.assertWaitForWithDelayOf(0, PhasedBackoffWaitStrategy.withLock(1, 1, TimeUnit.MILLISECONDS));
        WaitStrategyTestUtil.assertWaitForWithDelayOf(0, PhasedBackoffWaitStrategy.withSleep(1, 1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldHandleSequenceChangeWithOneMillisecondDelay() throws Exception {
        WaitStrategyTestUtil.assertWaitForWithDelayOf(1, PhasedBackoffWaitStrategy.withLock(1, 1, TimeUnit.MILLISECONDS));
        WaitStrategyTestUtil.assertWaitForWithDelayOf(1, PhasedBackoffWaitStrategy.withSleep(1, 1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldHandleSequenceChangeWithTwoMillisecondDelay() throws Exception {
        WaitStrategyTestUtil.assertWaitForWithDelayOf(2, PhasedBackoffWaitStrategy.withLock(1, 1, TimeUnit.MILLISECONDS));
        WaitStrategyTestUtil.assertWaitForWithDelayOf(2, PhasedBackoffWaitStrategy.withSleep(1, 1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldHandleSequenceChangeWithTenMillisecondDelay() throws Exception {
        WaitStrategyTestUtil.assertWaitForWithDelayOf(10, PhasedBackoffWaitStrategy.withLock(1, 1, TimeUnit.MILLISECONDS));
        WaitStrategyTestUtil.assertWaitForWithDelayOf(10, PhasedBackoffWaitStrategy.withSleep(1, 1, TimeUnit.MILLISECONDS));
    }
}

