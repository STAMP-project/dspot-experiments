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
package org.apache.beam.runners.gearpump.translators.utils;


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import io.gearpump.streaming.dsl.window.impl.Window;
import java.util.List;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.GlobalWindow;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;

import static java.time.Instant.EPOCH;
import static java.time.Instant.ofEpochMilli;


/**
 * Tests for {@link TranslatorUtils}.
 */
public class TranslatorUtilsTest {
    private static final List<KV<Instant, java.time.Instant>> TEST_VALUES = Lists.newArrayList(KV.of(new Instant(0), EPOCH), KV.of(new Instant(42), ofEpochMilli(42)), KV.of(new Instant(Long.MIN_VALUE), ofEpochMilli(Long.MIN_VALUE)), KV.of(new Instant(Long.MAX_VALUE), ofEpochMilli(Long.MAX_VALUE)));

    @Test
    public void testJodaTimeAndJava8TimeConversion() {
        for (KV<Instant, java.time.Instant> kv : TranslatorUtilsTest.TEST_VALUES) {
            Assert.assertThat(TranslatorUtils.jodaTimeToJava8Time(kv.getKey()), Matchers.equalTo(kv.getValue()));
            Assert.assertThat(TranslatorUtils.java8TimeToJodaTime(kv.getValue()), Matchers.equalTo(kv.getKey()));
        }
    }

    @Test
    public void testBoundedWindowToGearpumpWindow() {
        Assert.assertThat(TranslatorUtils.boundedWindowToGearpumpWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(0), new Instant(Long.MAX_VALUE))), Matchers.equalTo(Window.apply(EPOCH, ofEpochMilli(Long.MAX_VALUE))));
        Assert.assertThat(TranslatorUtils.boundedWindowToGearpumpWindow(new org.apache.beam.sdk.transforms.windowing.IntervalWindow(new Instant(Long.MIN_VALUE), new Instant(Long.MAX_VALUE))), Matchers.equalTo(Window.apply(ofEpochMilli(Long.MIN_VALUE), ofEpochMilli(Long.MAX_VALUE))));
        BoundedWindow globalWindow = GlobalWindow.INSTANCE;
        Assert.assertThat(TranslatorUtils.boundedWindowToGearpumpWindow(globalWindow), Matchers.equalTo(Window.apply(ofEpochMilli(TIMESTAMP_MIN_VALUE.getMillis()), ofEpochMilli(((globalWindow.maxTimestamp().getMillis()) + 1)))));
    }
}

