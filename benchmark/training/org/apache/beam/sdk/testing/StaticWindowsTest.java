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
package org.apache.beam.sdk.testing;


import GlobalWindow.INSTANCE;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IncompatibleWindowException;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link StaticWindows}.
 */
@RunWith(JUnit4.class)
public class StaticWindowsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final IntervalWindow first = new IntervalWindow(new Instant(0), new Instant(100000L));

    private final IntervalWindow second = new IntervalWindow(new Instant(1000000L), INSTANCE.maxTimestamp());

    @Test
    public void singleWindowSucceeds() throws Exception {
        WindowFn<Object, BoundedWindow> fn = StaticWindows.of(IntervalWindow.getCoder(), first);
        Assert.assertThat(WindowFnTestUtils.assignedWindows(fn, 100L), Matchers.contains(first));
        Assert.assertThat(WindowFnTestUtils.assignedWindows(fn, (-100L)), Matchers.contains(first));
    }

    @Test
    public void multipleWindowsSucceeds() throws Exception {
        WindowFn<Object, BoundedWindow> fn = StaticWindows.of(IntervalWindow.getCoder(), ImmutableList.of(first, second));
        Assert.assertThat(WindowFnTestUtils.assignedWindows(fn, 100L), Matchers.containsInAnyOrder(first, second));
        Assert.assertThat(WindowFnTestUtils.assignedWindows(fn, 1000000000L), Matchers.containsInAnyOrder(first, second));
        Assert.assertThat(WindowFnTestUtils.assignedWindows(fn, (-100L)), Matchers.containsInAnyOrder(first, second));
    }

    @Test
    public void getSideInputWindowIdentity() {
        WindowFn<Object, BoundedWindow> fn = StaticWindows.of(IntervalWindow.getCoder(), ImmutableList.of(first, second));
        Assert.assertThat(fn.getDefaultWindowMappingFn().getSideInputWindow(first), Matchers.equalTo(first));
        Assert.assertThat(fn.getDefaultWindowMappingFn().getSideInputWindow(second), Matchers.equalTo(second));
    }

    @Test
    public void getSideInputWindowNotPresent() {
        WindowFn<Object, BoundedWindow> fn = StaticWindows.of(IntervalWindow.getCoder(), ImmutableList.of(second));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("contains");
        fn.getDefaultWindowMappingFn().getSideInputWindow(first);
    }

    @Test
    public void emptyIterableThrows() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("may not be empty");
        StaticWindows.of(GlobalWindow.Coder.INSTANCE, ImmutableList.of());
    }

    @Test
    public void testCompatibility() throws IncompatibleWindowException {
        StaticWindows staticWindows = StaticWindows.of(IntervalWindow.getCoder(), ImmutableList.of(first, second));
        staticWindows.verifyCompatibility(StaticWindows.of(IntervalWindow.getCoder(), ImmutableList.of(first, second)));
        thrown.expect(IncompatibleWindowException.class);
        staticWindows.verifyCompatibility(StaticWindows.of(IntervalWindow.getCoder(), ImmutableList.of(first)));
    }
}

