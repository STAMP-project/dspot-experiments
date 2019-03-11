/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import java.time.Duration;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ProcessorContextTest {
    private ProcessorContext context;

    @Test
    public void shouldNotAllowToScheduleZeroMillisecondPunctuation() {
        try {
            context.schedule(Duration.ofMillis(0L), null, null);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.equalTo("The minimum supported scheduling interval is 1 millisecond."));
        }
    }

    @Test
    public void shouldNotAllowToScheduleSubMillisecondPunctuation() {
        try {
            context.schedule(Duration.ofNanos(999999L), null, null);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.equalTo("The minimum supported scheduling interval is 1 millisecond."));
        }
    }
}

