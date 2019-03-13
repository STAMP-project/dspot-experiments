/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.api.gax.retrying.RetrySettings;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.threeten.bp.Duration;


public class RetryOptionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final RetryOption TOTAL_TIMEOUT = RetryOption.totalTimeout(Duration.ofMillis(420L));

    private static final RetryOption INITIAL_RETRY_DELAY = RetryOption.initialRetryDelay(Duration.ofMillis(42L));

    private static final RetryOption RETRY_DELAY_MULTIPLIER = RetryOption.retryDelayMultiplier(1.5);

    private static final RetryOption MAX_RETRY_DELAY = RetryOption.maxRetryDelay(Duration.ofMillis(100));

    private static final RetryOption MAX_ATTEMPTS = RetryOption.maxAttempts(100);

    private static final RetryOption JITTERED = RetryOption.jittered(false);

    private static final RetrySettings retrySettings = RetrySettings.newBuilder().setTotalTimeout(Duration.ofMillis(420L)).setInitialRetryDelay(Duration.ofMillis(42L)).setRetryDelayMultiplier(1.5).setMaxRetryDelay(Duration.ofMillis(100)).setMaxAttempts(100).setJittered(false).build();

    @Test
    public void testEqualsAndHashCode() {
        Assert.assertEquals(RetryOptionTest.TOTAL_TIMEOUT, RetryOptionTest.TOTAL_TIMEOUT);
        Assert.assertEquals(RetryOptionTest.INITIAL_RETRY_DELAY, RetryOptionTest.INITIAL_RETRY_DELAY);
        Assert.assertEquals(RetryOptionTest.RETRY_DELAY_MULTIPLIER, RetryOptionTest.RETRY_DELAY_MULTIPLIER);
        Assert.assertEquals(RetryOptionTest.MAX_RETRY_DELAY, RetryOptionTest.MAX_RETRY_DELAY);
        Assert.assertEquals(RetryOptionTest.MAX_ATTEMPTS, RetryOptionTest.MAX_ATTEMPTS);
        Assert.assertEquals(RetryOptionTest.JITTERED, RetryOptionTest.JITTERED);
        Assert.assertNotEquals(RetryOptionTest.TOTAL_TIMEOUT, RetryOptionTest.JITTERED);
        Assert.assertNotEquals(RetryOptionTest.INITIAL_RETRY_DELAY, RetryOptionTest.TOTAL_TIMEOUT);
        Assert.assertNotEquals(RetryOptionTest.RETRY_DELAY_MULTIPLIER, RetryOptionTest.INITIAL_RETRY_DELAY);
        Assert.assertNotEquals(RetryOptionTest.MAX_RETRY_DELAY, RetryOptionTest.RETRY_DELAY_MULTIPLIER);
        Assert.assertNotEquals(RetryOptionTest.MAX_ATTEMPTS, RetryOptionTest.MAX_RETRY_DELAY);
        Assert.assertNotEquals(RetryOptionTest.JITTERED, RetryOptionTest.MAX_ATTEMPTS);
        RetryOption totalTimeout = RetryOption.totalTimeout(Duration.ofMillis(420L));
        RetryOption initialRetryDelay = RetryOption.initialRetryDelay(Duration.ofMillis(42L));
        RetryOption retryDelayMultiplier = RetryOption.retryDelayMultiplier(1.5);
        RetryOption maxRetryDelay = RetryOption.maxRetryDelay(Duration.ofMillis(100));
        RetryOption maxAttempts = RetryOption.maxAttempts(100);
        RetryOption jittered = RetryOption.jittered(false);
        Assert.assertEquals(RetryOptionTest.TOTAL_TIMEOUT, totalTimeout);
        Assert.assertEquals(RetryOptionTest.INITIAL_RETRY_DELAY, initialRetryDelay);
        Assert.assertEquals(RetryOptionTest.RETRY_DELAY_MULTIPLIER, retryDelayMultiplier);
        Assert.assertEquals(RetryOptionTest.MAX_RETRY_DELAY, maxRetryDelay);
        Assert.assertEquals(RetryOptionTest.MAX_ATTEMPTS, maxAttempts);
        Assert.assertEquals(RetryOptionTest.JITTERED, jittered);
        Assert.assertEquals(RetryOptionTest.TOTAL_TIMEOUT.hashCode(), totalTimeout.hashCode());
        Assert.assertEquals(RetryOptionTest.INITIAL_RETRY_DELAY.hashCode(), initialRetryDelay.hashCode());
        Assert.assertEquals(RetryOptionTest.RETRY_DELAY_MULTIPLIER.hashCode(), retryDelayMultiplier.hashCode());
        Assert.assertEquals(RetryOptionTest.MAX_RETRY_DELAY.hashCode(), maxRetryDelay.hashCode());
        Assert.assertEquals(RetryOptionTest.MAX_ATTEMPTS.hashCode(), maxAttempts.hashCode());
        Assert.assertEquals(RetryOptionTest.JITTERED.hashCode(), jittered.hashCode());
    }

    @Test
    public void testMergeToSettings() {
        RetrySettings defRetrySettings = RetrySettings.newBuilder().build();
        Assert.assertEquals(defRetrySettings, RetryOption.mergeToSettings(defRetrySettings));
        RetrySettings mergedRetrySettings = RetryOption.mergeToSettings(defRetrySettings, RetryOptionTest.TOTAL_TIMEOUT, RetryOptionTest.INITIAL_RETRY_DELAY, RetryOptionTest.RETRY_DELAY_MULTIPLIER, RetryOptionTest.MAX_RETRY_DELAY, RetryOptionTest.MAX_ATTEMPTS, RetryOptionTest.JITTERED);
        Assert.assertEquals(RetryOptionTest.retrySettings, mergedRetrySettings);
        defRetrySettings = defRetrySettings.toBuilder().setTotalTimeout(Duration.ofMillis(420L)).build();
        mergedRetrySettings = RetryOption.mergeToSettings(defRetrySettings, RetryOptionTest.TOTAL_TIMEOUT);
        Assert.assertEquals(defRetrySettings, mergedRetrySettings);
        defRetrySettings = defRetrySettings.toBuilder().setMaxRetryDelay(Duration.ofMillis(100)).build();
        mergedRetrySettings = RetryOption.mergeToSettings(defRetrySettings, RetryOptionTest.MAX_RETRY_DELAY);
        Assert.assertEquals(defRetrySettings, mergedRetrySettings);
        defRetrySettings = defRetrySettings.toBuilder().setInitialRetryDelay(Duration.ofMillis(42L)).build();
        mergedRetrySettings = RetryOption.mergeToSettings(defRetrySettings, RetryOptionTest.INITIAL_RETRY_DELAY);
        Assert.assertEquals(defRetrySettings, mergedRetrySettings);
        defRetrySettings = defRetrySettings.toBuilder().setRetryDelayMultiplier(1.5).build();
        mergedRetrySettings = RetryOption.mergeToSettings(defRetrySettings, RetryOptionTest.RETRY_DELAY_MULTIPLIER);
        Assert.assertEquals(defRetrySettings, mergedRetrySettings);
        defRetrySettings = defRetrySettings.toBuilder().setMaxAttempts(100).build();
        mergedRetrySettings = RetryOption.mergeToSettings(defRetrySettings, RetryOptionTest.MAX_ATTEMPTS);
        Assert.assertEquals(defRetrySettings, mergedRetrySettings);
        defRetrySettings = defRetrySettings.toBuilder().setJittered(false).build();
        mergedRetrySettings = RetryOption.mergeToSettings(defRetrySettings, RetryOptionTest.JITTERED);
        Assert.assertEquals(defRetrySettings, mergedRetrySettings);
    }
}

