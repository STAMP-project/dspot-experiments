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
package org.apache.beam.runners.core.construction;


import java.nio.charset.StandardCharsets;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Timer}.
 */
@RunWith(JUnit4.class)
public class TimerTest {
    private static final Instant INSTANT = Instant.now();

    @Test
    public void testTimer() {
        Timer<Void> timerA = Timer.of(TimerTest.INSTANT);
        Assert.assertEquals(TimerTest.INSTANT, timerA.getTimestamp());
        Assert.assertNull(timerA.getPayload());
        Timer<String> timerB = Timer.of(TimerTest.INSTANT, "ABC");
        Assert.assertEquals(TimerTest.INSTANT, timerB.getTimestamp());
        Assert.assertEquals("ABC", timerB.getPayload());
    }

    @Test
    public void testTimerCoderWithInconsistentWithEqualsPayloadCoder() throws Exception {
        Coder<Timer<byte[]>> coder = Timer.Coder.of(ByteArrayCoder.of());
        CoderProperties.coderSerializable(coder);
        CoderProperties.structuralValueDecodeEncodeEqual(coder, Timer.of(TimerTest.INSTANT, "ABC".getBytes(StandardCharsets.UTF_8)));
        CoderProperties.structuralValueConsistentWithEquals(coder, Timer.of(TimerTest.INSTANT, "ABC".getBytes(StandardCharsets.UTF_8)), Timer.of(TimerTest.INSTANT, "ABC".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testTimerCoderWithConsistentWithEqualsPayloadCoder() throws Exception {
        Coder<Timer<String>> coder = Timer.Coder.of(StringUtf8Coder.of());
        CoderProperties.coderDecodeEncodeEqual(coder, Timer.of(TimerTest.INSTANT, "ABC"));
        CoderProperties.coderConsistentWithEquals(coder, Timer.of(TimerTest.INSTANT, "ABC"), Timer.of(TimerTest.INSTANT, "ABC"));
        CoderProperties.coderDeterministic(coder, Timer.of(TimerTest.INSTANT, "ABC"), Timer.of(TimerTest.INSTANT, "ABC"));
    }

    @Test
    public void testTimerCoderWireFormat() throws Exception {
        Coder<Timer<String>> coder = Timer.Coder.of(StringUtf8Coder.of());
        CoderProperties.coderEncodesBase64(coder, Timer.of(new Instant(255L), "ABC"), "gAAAAAAAAP8DQUJD");
    }
}

