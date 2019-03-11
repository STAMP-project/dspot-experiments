/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flink.util;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static WrappingProxyUtil.SAFETY_NET_MAX_ITERATIONS;


/**
 * Tests for {@link WrappingProxyUtil}.
 */
public class WrappingProxyUtilTest {
    @Test
    public void testThrowsExceptionIfTooManyProxies() {
        try {
            WrappingProxyUtil.stripProxy(new WrappingProxyUtilTest.SelfWrappingProxy(SAFETY_NET_MAX_ITERATIONS));
            Assert.fail("Expected exception not thrown");
        } catch (final IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Are there loops in the object graph?"));
        }
    }

    @Test
    public void testStripsAllProxies() {
        final WrappingProxyUtilTest.SelfWrappingProxy wrappingProxy = new WrappingProxyUtilTest.SelfWrappingProxy(((SAFETY_NET_MAX_ITERATIONS) - 1));
        Assert.assertThat(WrappingProxyUtil.stripProxy(wrappingProxy), CoreMatchers.is(CoreMatchers.not(CoreMatchers.instanceOf(WrappingProxyUtilTest.SelfWrappingProxy.class))));
    }

    private static class Wrapped {}

    /**
     * Wraps around {@link Wrapped} a specified number of times.
     */
    private static class SelfWrappingProxy extends WrappingProxyUtilTest.Wrapped implements WrappingProxy<WrappingProxyUtilTest.Wrapped> {
        private int levels;

        private SelfWrappingProxy(final int levels) {
            this.levels = levels;
        }

        @Override
        public WrappingProxyUtilTest.Wrapped getWrappedDelegate() {
            if (((levels)--) == 0) {
                return new WrappingProxyUtilTest.Wrapped();
            } else {
                return this;
            }
        }
    }
}

