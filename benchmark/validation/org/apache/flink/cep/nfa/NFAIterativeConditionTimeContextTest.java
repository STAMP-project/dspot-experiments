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
package org.apache.flink.cep.nfa;


import java.util.Collections;
import java.util.List;
import org.apache.flink.cep.Event;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.utils.EventBuilder;
import org.apache.flink.cep.utils.NFATestHarness;
import org.apache.flink.cep.utils.NFATestUtilities;
import org.apache.flink.cep.utils.TestTimerService;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Tests for accesing time properties from {@link IterativeCondition}.
 */
public class NFAIterativeConditionTimeContextTest extends TestLogger {
    @Test
    public void testEventTimestamp() throws Exception {
        final Event event = EventBuilder.event().withId(1).build();
        final long timestamp = 3;
        final Pattern<Event, ?> pattern = Pattern.<Event>begin("start").where(new org.apache.flink.cep.pattern.conditions.IterativeCondition<Event>() {
            @Override
            public boolean filter(Event value, Context<Event> ctx) throws Exception {
                return (ctx.timestamp()) == timestamp;
            }
        });
        final NFATestHarness testHarness = NFATestHarness.forPattern(pattern).build();
        final List<List<Event>> resultingPattern = testHarness.feedRecord(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(event, timestamp));
        NFATestUtilities.compareMaps(resultingPattern, Collections.singletonList(Collections.singletonList(event)));
    }

    @Test
    public void testCurrentProcessingTime() throws Exception {
        final Event event1 = EventBuilder.event().withId(1).build();
        final Event event2 = EventBuilder.event().withId(2).build();
        final Pattern<Event, ?> pattern = Pattern.<Event>begin("start").where(new org.apache.flink.cep.pattern.conditions.IterativeCondition<Event>() {
            @Override
            public boolean filter(Event value, Context<Event> ctx) throws Exception {
                return (ctx.currentProcessingTime()) == 3;
            }
        });
        final TestTimerService cepTimerService = new TestTimerService();
        final NFATestHarness testHarness = NFATestHarness.forPattern(pattern).withTimerService(cepTimerService).build();
        cepTimerService.setCurrentProcessingTime(1);
        final List<List<Event>> resultingPatterns1 = testHarness.feedRecord(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(event1, 7));
        cepTimerService.setCurrentProcessingTime(3);
        final List<List<Event>> resultingPatterns2 = testHarness.feedRecord(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(event2, 8));
        NFATestUtilities.compareMaps(resultingPatterns1, Collections.emptyList());
        NFATestUtilities.compareMaps(resultingPatterns2, Collections.singletonList(Collections.singletonList(event2)));
    }
}

