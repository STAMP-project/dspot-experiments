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
package org.apache.beam.sdk.transforms.windowing;


import java.util.Arrays;
import java.util.List;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Trigger}.
 */
@RunWith(JUnit4.class)
public class TriggerTest {
    @Test
    public void testTriggerToString() throws Exception {
        Assert.assertEquals("AfterWatermark.pastEndOfWindow()", AfterWatermark.pastEndOfWindow().toString());
        Assert.assertEquals("Repeatedly.forever(AfterWatermark.pastEndOfWindow())", Repeatedly.forever(AfterWatermark.pastEndOfWindow()).toString());
    }

    @Test
    public void testIsCompatible() throws Exception {
        Assert.assertTrue(isCompatible(new TriggerTest.Trigger1(null)));
        Assert.assertTrue(isCompatible(new TriggerTest.Trigger1(Arrays.asList(new TriggerTest.Trigger2(null)))));
        Assert.assertFalse(new TriggerTest.Trigger1(null).isCompatible(new TriggerTest.Trigger2(null)));
        Assert.assertFalse(isCompatible(new TriggerTest.Trigger1(Arrays.asList(new TriggerTest.Trigger2(null)))));
    }

    private static class Trigger1 extends Trigger {
        private Trigger1(List<Trigger> subTriggers) {
            super(subTriggers);
        }

        @Override
        protected Trigger getContinuationTrigger(List<Trigger> continuationTriggers) {
            return null;
        }

        @Override
        public Instant getWatermarkThatGuaranteesFiring(BoundedWindow window) {
            return null;
        }
    }

    private static class Trigger2 extends Trigger {
        private Trigger2(List<Trigger> subTriggers) {
            super(subTriggers);
        }

        @Override
        protected Trigger getContinuationTrigger(List<Trigger> continuationTriggers) {
            return null;
        }

        @Override
        public Instant getWatermarkThatGuaranteesFiring(BoundedWindow window) {
            return null;
        }
    }
}

