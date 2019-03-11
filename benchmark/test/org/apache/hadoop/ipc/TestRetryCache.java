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
package org.apache.hadoop.ipc;


import Server.Call;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.ipc.RetryCache.CacheEntryWithPayload;
import org.junit.Test;


/**
 * Tests for {@link RetryCache}
 */
public class TestRetryCache {
    private static final byte[] CLIENT_ID = ClientId.getClientId();

    private static int callId = 100;

    private static final Random r = new Random();

    private static final TestRetryCache.TestServer testServer = new TestRetryCache.TestServer();

    static class TestServer {
        AtomicInteger retryCount = new AtomicInteger();

        AtomicInteger operationCount = new AtomicInteger();

        private RetryCache retryCache = new RetryCache("TestRetryCache", 1, (((100 * 1000) * 1000) * 1000L));

        /**
         * A server method implemented using {@link RetryCache}.
         *
         * @param input
         * 		is returned back in echo, if {@code success} is true.
         * @param failureOuput
         * 		returned on failure, if {@code success} is false.
         * @param methodTime
         * 		time taken by the operation. By passing smaller/larger
         * 		value one can simulate an operation that takes short/long time.
         * @param success
         * 		whether this operation completes successfully or not
         * @return return the input parameter {@code input}, if {@code success} is
        true, else return {@code failureOutput}.
         */
        int echo(int input, int failureOutput, long methodTime, boolean success) throws InterruptedException {
            CacheEntryWithPayload entry = RetryCache.waitForCompletion(retryCache, null);
            if ((entry != null) && (entry.isSuccess())) {
                System.out.println(("retryCount incremented " + (retryCount.get())));
                retryCount.incrementAndGet();
                return ((Integer) (entry.getPayload()));
            }
            try {
                operationCount.incrementAndGet();
                if (methodTime > 0) {
                    Thread.sleep(methodTime);
                }
            } finally {
                RetryCache.setState(entry, success, input);
            }
            return success ? input : failureOutput;
        }

        void resetCounters() {
            retryCount.set(0);
            operationCount.set(0);
        }
    }

    /**
     * This simlulates a long server retried operations. Multiple threads start an
     * operation that takes long time and finally succeeds. The retries in this
     * case end up waiting for the current operation to complete. All the retries
     * then complete based on the entry in the retry cache.
     */
    @Test
    public void testLongOperationsSuccessful() throws Exception {
        // Test long successful operations
        // There is no entry in cache expected when the first operation starts
        testOperations(TestRetryCache.r.nextInt(), 100, 20, true, false, TestRetryCache.newCall());
    }

    /**
     * This simlulates a long server operation. Multiple threads start an
     * operation that takes long time and finally fails. The retries in this case
     * end up waiting for the current operation to complete. All the retries end
     * up performing the operation again.
     */
    @Test
    public void testLongOperationsFailure() throws Exception {
        // Test long failed operations
        // There is no entry in cache expected when the first operation starts
        testOperations(TestRetryCache.r.nextInt(), 100, 20, false, false, TestRetryCache.newCall());
    }

    /**
     * This simlulates a short server operation. Multiple threads start an
     * operation that takes very short time and finally succeeds. The retries in
     * this case do not wait long for the current operation to complete. All the
     * retries then complete based on the entry in the retry cache.
     */
    @Test
    public void testShortOperationsSuccess() throws Exception {
        // Test long failed operations
        // There is no entry in cache expected when the first operation starts
        testOperations(TestRetryCache.r.nextInt(), 25, 0, false, false, TestRetryCache.newCall());
    }

    /**
     * This simlulates a short server operation. Multiple threads start an
     * operation that takes short time and finally fails. The retries in this case
     * do not wait for the current operation to complete. All the retries end up
     * performing the operation again.
     */
    @Test
    public void testShortOperationsFailure() throws Exception {
        // Test long failed operations
        // There is no entry in cache expected when the first operation starts
        testOperations(TestRetryCache.r.nextInt(), 25, 0, false, false, TestRetryCache.newCall());
    }

    @Test
    public void testRetryAfterSuccess() throws Exception {
        // Previous operation successfully completed
        Server.Call call = TestRetryCache.newCall();
        int input = TestRetryCache.r.nextInt();
        Server.getCurCall().set(call);
        TestRetryCache.testServer.echo(input, (input + 1), 5, true);
        testOperations(input, 25, 0, true, true, call);
    }

    @Test
    public void testRetryAfterFailure() throws Exception {
        // Previous operation failed
        Server.Call call = TestRetryCache.newCall();
        int input = TestRetryCache.r.nextInt();
        Server.getCurCall().set(call);
        TestRetryCache.testServer.echo(input, (input + 1), 5, false);
        testOperations(input, 25, 0, false, true, call);
    }
}

