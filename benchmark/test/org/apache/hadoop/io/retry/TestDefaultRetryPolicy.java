/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.retry;


import RetryPolicy.RetryAction;
import RetryPolicy.RetryAction.RetryDecision.FAIL;
import RetryPolicy.RetryAction.RetryDecision.FAILOVER_AND_RETRY;
import RetryPolicy.RetryAction.RetryDecision.RETRY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.RetriableException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test the behavior of the default retry policy.
 */
public class TestDefaultRetryPolicy {
    @Rule
    public Timeout timeout = new Timeout(30000);

    /**
     * Verify FAIL < RETRY < FAILOVER_AND_RETRY.
     */
    @Test
    public void testRetryDecisionOrdering() throws Exception {
        Assert.assertTrue(((FAIL.compareTo(RETRY)) < 0));
        Assert.assertTrue(((RETRY.compareTo(FAILOVER_AND_RETRY)) < 0));
        Assert.assertTrue(((FAIL.compareTo(FAILOVER_AND_RETRY)) < 0));
    }

    /**
     * Verify that the default retry policy correctly retries
     * RetriableException when defaultRetryPolicyEnabled is enabled.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testWithRetriable() throws Exception {
        Configuration conf = new Configuration();
        RetryPolicy policy = // defaultRetryPolicyEnabled = true
        RetryUtils.getDefaultRetryPolicy(conf, "Test.No.Such.Key", true, "Test.No.Such.Key", "10000,6", null);
        RetryPolicy.RetryAction action = policy.shouldRetry(new RetriableException("Dummy exception"), 0, 0, true);
        Assert.assertThat(action.action, Is.is(RETRY));
    }

    /**
     * Verify that the default retry policy correctly retries
     * a RetriableException wrapped in a RemoteException when
     * defaultRetryPolicyEnabled is enabled.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testWithWrappedRetriable() throws Exception {
        Configuration conf = new Configuration();
        RetryPolicy policy = // defaultRetryPolicyEnabled = true
        RetryUtils.getDefaultRetryPolicy(conf, "Test.No.Such.Key", true, "Test.No.Such.Key", "10000,6", null);
        RetryPolicy.RetryAction action = policy.shouldRetry(new RemoteException(RetriableException.class.getName(), "Dummy exception"), 0, 0, true);
        Assert.assertThat(action.action, Is.is(RETRY));
    }

    /**
     * Verify that the default retry policy does *not* retry
     * RetriableException when defaultRetryPolicyEnabled is disabled.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testWithRetriableAndRetryDisabled() throws Exception {
        Configuration conf = new Configuration();
        RetryPolicy policy = // defaultRetryPolicyEnabled = false
        RetryUtils.getDefaultRetryPolicy(conf, "Test.No.Such.Key", false, "Test.No.Such.Key", "10000,6", null);
        RetryPolicy.RetryAction action = policy.shouldRetry(new RetriableException("Dummy exception"), 0, 0, true);
        Assert.assertThat(action.action, Is.is(FAIL));
    }
}

