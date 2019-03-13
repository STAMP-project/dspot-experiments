/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
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
 */
package com.github.ambry.rest;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link RestRequestMetricsTracker}.
 */
public class RestRequestMetricsTrackerTest {
    /**
     * Tests the common case uses of {@link RestRequestMetricsTracker} i.e. with and without a custom
     * {@link RestRequestMetrics}.
     */
    @Test
    public void commonCaseTest() throws InterruptedException {
        withDefaultsTest(false);
        withDefaultsTest(true);
        withInjectedMetricsTest(false);
        withInjectedMetricsTest(true);
    }

    /**
     * Tests reaction of {@link RestRequestMetricsTracker#injectMetrics(RestRequestMetrics)} to bad input.
     */
    @Test
    public void injectMetricsBadInputTest() {
        RestRequestMetricsTracker requestMetrics = new RestRequestMetricsTracker();
        try {
            requestMetrics.injectMetrics(null);
            Assert.fail("There was no exception even though a null RestRequestMetrics was provided as input for injectMetrics()");
        } catch (IllegalArgumentException e) {
            // expected. nothing to do.
        }
    }

    /**
     * Tests reaction to bad calls to {@link RestRequestMetricsTracker.NioMetricsTracker#markRequestCompleted()} and
     * {@link RestRequestMetricsTracker.ScalingMetricsTracker#markRequestCompleted()}
     */
    @Test
    public void requestMarkingExceptionsTest() {
        RestRequestMetricsTracker requestMetrics = new RestRequestMetricsTracker();
        try {
            requestMetrics.nioMetricsTracker.markFirstByteSent();
            Assert.fail("Marking request as complete before marking it received should have thrown exception");
        } catch (IllegalStateException e) {
            // expected. nothing to do.
        }
        try {
            requestMetrics.nioMetricsTracker.markRequestCompleted();
            Assert.fail("Marking request as complete before marking it received should have thrown exception");
        } catch (IllegalStateException e) {
            // expected. nothing to do.
        }
        try {
            requestMetrics.scalingMetricsTracker.markRequestCompleted();
            Assert.fail("Marking request as complete before marking it received should have thrown exception");
        } catch (IllegalStateException e) {
            // expected. nothing to do.
        }
    }
}

