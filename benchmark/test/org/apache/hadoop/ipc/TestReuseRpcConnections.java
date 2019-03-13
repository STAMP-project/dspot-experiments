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


import RetryPolicies.RETRY_FOREVER;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.io.retry.TestConnectionRetryPolicy;
import org.junit.Test;


/**
 * This class mainly tests behaviors of reusing RPC connections for various
 * retry policies.
 */
public class TestReuseRpcConnections extends TestRpcBase {
    @Test(timeout = 60000)
    public void testDefaultRetryPolicyReuseConnections() throws Exception {
        RetryPolicy rp1 = null;
        RetryPolicy rp2 = null;
        RetryPolicy rp3 = null;
        /* test the same setting */
        rp1 = TestReuseRpcConnections.getDefaultRetryPolicy(true, "10000,2");
        rp2 = TestReuseRpcConnections.getDefaultRetryPolicy(true, "10000,2");
        verifyRetryPolicyReuseConnections(rp1, rp2, RETRY_FOREVER);
        /* test enabled and different specifications */
        rp1 = TestReuseRpcConnections.getDefaultRetryPolicy(true, "20000,3");
        rp2 = TestReuseRpcConnections.getDefaultRetryPolicy(true, "20000,3");
        rp3 = TestReuseRpcConnections.getDefaultRetryPolicy(true, "30000,4");
        verifyRetryPolicyReuseConnections(rp1, rp2, rp3);
        /* test disabled and the same specifications */
        rp1 = TestReuseRpcConnections.getDefaultRetryPolicy(false, "40000,5");
        rp2 = TestReuseRpcConnections.getDefaultRetryPolicy(false, "40000,5");
        verifyRetryPolicyReuseConnections(rp1, rp2, RETRY_FOREVER);
        /* test disabled and different specifications */
        rp1 = TestReuseRpcConnections.getDefaultRetryPolicy(false, "50000,6");
        rp2 = TestReuseRpcConnections.getDefaultRetryPolicy(false, "60000,7");
        verifyRetryPolicyReuseConnections(rp1, rp2, RETRY_FOREVER);
        /* test different remoteExceptionToRetry */
        rp1 = TestReuseRpcConnections.getDefaultRetryPolicy(true, "70000,8", new RemoteException(RpcNoSuchMethodException.class.getName(), "no such method exception").getClassName());
        rp2 = TestReuseRpcConnections.getDefaultRetryPolicy(true, "70000,8", new RemoteException(PathIOException.class.getName(), "path IO exception").getClassName());
        verifyRetryPolicyReuseConnections(rp1, rp2, RETRY_FOREVER);
    }

    @Test(timeout = 60000)
    public void testRetryPolicyTryOnceThenFail() throws Exception {
        final RetryPolicy rp1 = TestConnectionRetryPolicy.newTryOnceThenFail();
        final RetryPolicy rp2 = TestConnectionRetryPolicy.newTryOnceThenFail();
        verifyRetryPolicyReuseConnections(rp1, rp2, RETRY_FOREVER);
    }
}

