/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.rate;


import com.twitter.util.MockTimer;
import com.twitter.util.TimeControl;
import org.junit.Assert;
import org.junit.Test;


public class TestMovingAverageRate {
    interface TcCallback {
        void apply(TimeControl tc);
    }

    @Test(timeout = 60000)
    public void testNoChangeInUnderMinInterval() {
        withCurrentTimeFrozen(new TestMovingAverageRate.TcCallback() {
            @Override
            public void apply(TimeControl time) {
                MockTimer timer = new MockTimer();
                MovingAverageRateFactory factory = new MovingAverageRateFactory(timer);
                MovingAverageRate avg60 = factory.create(60);
                avg60.add(1000);
                Assert.assertEquals(0, avg60.get(), 0);
                advance(time, timer, 1);
                Assert.assertEquals(0, avg60.get(), 0);
                advance(time, timer, 1);
                Assert.assertEquals(0, avg60.get(), 0);
            }
        });
    }

    @Test(timeout = 60000)
    public void testFactoryWithMultipleTimers() {
        withCurrentTimeFrozen(new TestMovingAverageRate.TcCallback() {
            @Override
            public void apply(TimeControl time) {
                MockTimer timer = new MockTimer();
                MovingAverageRateFactory factory = new MovingAverageRateFactory(timer);
                MovingAverageRate avg60 = factory.create(60);
                MovingAverageRate avg30 = factory.create(30);
                // Can't test this precisely because the Rate class uses its own
                // ticker. So we can control when it gets sampled but not the time
                // value it uses. So, just do basic validation.
                for (int i = 0; i < 30; i++) {
                    avg60.add(100);
                    avg30.add(100);
                    advance(time, timer, 1000);
                }
                double s1 = avg60.get();
                Assert.assertTrue(((avg30.get()) > 0));
                for (int i = 0; i < 30; i++) {
                    advance(time, timer, 1000);
                }
                Assert.assertTrue(((avg60.get()) > 0));
                Assert.assertTrue(((avg60.get()) < s1));
                Assert.assertEquals(0.0, avg30.get(), 0);
            }
        });
    }
}

