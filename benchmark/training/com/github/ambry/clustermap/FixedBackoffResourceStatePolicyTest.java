/**
 * Copyright 2017 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.clustermap;


import com.github.ambry.utils.MockTime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link FixedBackoffResourceStatePolicy}
 */
public class FixedBackoffResourceStatePolicyTest {
    private final FixedBackoffResourceStatePolicy policy;

    private final MockTime time;

    private final Resource resource;

    private static final int FAILURE_COUNT_THRESHOLD = 3;

    private static final int RETRY_BACKOFF_MS = 10;

    public FixedBackoffResourceStatePolicyTest() {
        // Ensure that the values for FAILURE_COUNT_THRESHOLD and RETRY_BACKOFF_MS are valid for the tests.
        Assert.assertTrue("Test initialization error, FAILURE_COUNT_THRESHOLD is too low", ((FixedBackoffResourceStatePolicyTest.FAILURE_COUNT_THRESHOLD) >= 2));
        Assert.assertTrue("Test initialization error, RETRY_BACKOFF_MS is too low", ((FixedBackoffResourceStatePolicyTest.RETRY_BACKOFF_MS) >= 2));
        time = new MockTime();
        resource = () -> null;
        policy = new FixedBackoffResourceStatePolicy(resource, false, FixedBackoffResourceStatePolicyTest.FAILURE_COUNT_THRESHOLD, FixedBackoffResourceStatePolicyTest.RETRY_BACKOFF_MS, time);
    }

    /**
     * Tests to validate that the thresholds and retries are serving exactly their intended purposes.
     */
    @Test
    public void testThresholdsAndRetries() {
        // Verify that the resource becomes down exactly after receiving 3 errors.
        initiateAndVerifyResourceGoesDownExactlyAtThresholdFailures();
        // Verify that the resource will stay down until 10 ms have passed.
        time.sleep(((FixedBackoffResourceStatePolicyTest.RETRY_BACKOFF_MS) / 2));
        Assert.assertTrue(policy.isDown());
        time.sleep(((FixedBackoffResourceStatePolicyTest.RETRY_BACKOFF_MS) - ((FixedBackoffResourceStatePolicyTest.RETRY_BACKOFF_MS) / 2)));
        Assert.assertFalse(policy.isDown());
        // At this time the resource is conditionally up. Verify that a single error should bring it back down.
        policy.onError();
        Assert.assertTrue(policy.isDown());
        // A single success should bring a down resource unconditionally up.
        policy.onSuccess();
        // Verify that the resource is unconditionally up - a single error should not bring it down.
        initiateAndVerifyResourceGoesDownExactlyAtThresholdFailures();
        time.sleep(FixedBackoffResourceStatePolicyTest.RETRY_BACKOFF_MS);
        Assert.assertFalse(policy.isDown());
        // Verify that a conditionally up resource becomes unconditionally up after receiving a single success.
        policy.onSuccess();
        initiateAndVerifyResourceGoesDownExactlyAtThresholdFailures();
    }

    /**
     * Tests that hard events are honored in the way expected.
     */
    @Test
    public void testHardDownandHardUp() {
        // Verify that once the resource is hard down, it stays down indefinitely unless an explicit event occurs.
        Assert.assertFalse(policy.isDown());
        policy.onHardDown();
        Assert.assertTrue(policy.isDown());
        time.sleep(((FixedBackoffResourceStatePolicyTest.RETRY_BACKOFF_MS) * 2));
        Assert.assertTrue(policy.isDown());
        Assert.assertTrue(policy.isHardDown());
        // A single success should not bring it back up, as the resource is hard down.
        policy.onSuccess();
        Assert.assertTrue(policy.isDown());
        Assert.assertTrue(policy.isHardDown());
        // A single hard up should bring it back up.
        policy.onHardUp();
        Assert.assertFalse(policy.isDown());
        Assert.assertFalse(policy.isHardDown());
        // Verify that a hard up resource goes down immediately if onHardDown() is called.
        policy.onHardDown();
        Assert.assertTrue(policy.isDown());
        Assert.assertTrue(policy.isHardDown());
        policy.onHardUp();
        Assert.assertFalse(policy.isDown());
        Assert.assertFalse(policy.isHardDown());
        // Verify that a hard up resource goes down as part of normal errors when thresholds are reached.
        initiateAndVerifyResourceGoesDownExactlyAtThresholdFailures();
    }
}

