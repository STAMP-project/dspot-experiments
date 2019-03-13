/**
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.utils;


import ExponentialBackoff.Params;
import java.util.PriorityQueue;
import org.bitcoinj.core.Utils;
import org.junit.Assert;
import org.junit.Test;


public class ExponentialBackoffTest {
    private Params params;

    private ExponentialBackoff backoff;

    @Test
    public void testSuccess() {
        Assert.assertEquals(Utils.currentTimeMillis(), backoff.getRetryTime());
        backoff.trackFailure();
        backoff.trackFailure();
        backoff.trackSuccess();
        Assert.assertEquals(Utils.currentTimeMillis(), backoff.getRetryTime());
    }

    @Test
    public void testFailure() {
        Assert.assertEquals(Utils.currentTimeMillis(), backoff.getRetryTime());
        backoff.trackFailure();
        backoff.trackFailure();
        backoff.trackFailure();
        Assert.assertEquals(((Utils.currentTimeMillis()) + 121), backoff.getRetryTime());
    }

    @Test
    public void testInQueue() {
        PriorityQueue<ExponentialBackoff> queue = new PriorityQueue<>();
        ExponentialBackoff backoff1 = new ExponentialBackoff(params);
        backoff.trackFailure();
        backoff.trackFailure();
        backoff1.trackFailure();
        backoff1.trackFailure();
        backoff1.trackFailure();
        queue.offer(backoff);
        queue.offer(backoff1);
        Assert.assertEquals(queue.poll(), backoff);// The one with soonest retry time

        Assert.assertEquals(queue.peek(), backoff1);
        queue.offer(backoff);
        Assert.assertEquals(queue.poll(), backoff);// Still the same one

    }
}

