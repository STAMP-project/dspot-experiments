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
package org.apache.hadoop.hbase.client;


import CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY;
import HConstants.HBASE_CLIENT_OPERATION_TIMEOUT;
import HConstants.HBASE_CLIENT_RETRIES_NUMBER;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncAdminBuilder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncAdminBuilder.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAsyncAdminBuilder.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static AsyncConnection ASYNC_CONN;

    @Parameterized.Parameter
    public Supplier<AsyncAdminBuilder> getAdminBuilder;

    private static final int DEFAULT_RPC_TIMEOUT = 10000;

    private static final int DEFAULT_OPERATION_TIMEOUT = 30000;

    private static final int DEFAULT_RETRIES_NUMBER = 2;

    @Test
    public void testRpcTimeout() throws Exception {
        TestAsyncAdminBuilder.TEST_UTIL.getConfiguration().set(MASTER_COPROCESSOR_CONF_KEY, TestAsyncAdminBuilder.TestRpcTimeoutCoprocessor.class.getName());
        TestAsyncAdminBuilder.TEST_UTIL.startMiniCluster(2);
        TestAsyncAdminBuilder.ASYNC_CONN = ConnectionFactory.createAsyncConnection(TestAsyncAdminBuilder.TEST_UTIL.getConfiguration()).get();
        try {
            getAdminBuilder.get().setRpcTimeout(((TestAsyncAdminBuilder.DEFAULT_RPC_TIMEOUT) / 2), TimeUnit.MILLISECONDS).build().getNamespaceDescriptor(NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR).get();
            Assert.fail("We expect an exception here");
        } catch (Exception e) {
            // expected
        }
        try {
            getAdminBuilder.get().setRpcTimeout(((TestAsyncAdminBuilder.DEFAULT_RPC_TIMEOUT) * 2), TimeUnit.MILLISECONDS).build().getNamespaceDescriptor(NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR).get();
        } catch (Exception e) {
            Assert.fail(("The Operation should succeed, unexpected exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testOperationTimeout() throws Exception {
        // set retry number to 100 to make sure that this test only be affected by operation timeout
        TestAsyncAdminBuilder.TEST_UTIL.getConfiguration().setInt(HBASE_CLIENT_RETRIES_NUMBER, 100);
        TestAsyncAdminBuilder.TEST_UTIL.getConfiguration().set(MASTER_COPROCESSOR_CONF_KEY, TestAsyncAdminBuilder.TestOperationTimeoutCoprocessor.class.getName());
        TestAsyncAdminBuilder.TEST_UTIL.startMiniCluster(2);
        TestAsyncAdminBuilder.ASYNC_CONN = ConnectionFactory.createAsyncConnection(TestAsyncAdminBuilder.TEST_UTIL.getConfiguration()).get();
        try {
            getAdminBuilder.get().setOperationTimeout(((TestAsyncAdminBuilder.DEFAULT_OPERATION_TIMEOUT) / 2), TimeUnit.MILLISECONDS).build().getNamespaceDescriptor(NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR).get();
            Assert.fail("We expect an exception here");
        } catch (Exception e) {
            // expected
        }
        try {
            getAdminBuilder.get().setOperationTimeout(((TestAsyncAdminBuilder.DEFAULT_OPERATION_TIMEOUT) * 2), TimeUnit.MILLISECONDS).build().getNamespaceDescriptor(NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR).get();
        } catch (Exception e) {
            Assert.fail(("The Operation should succeed, unexpected exception: " + (e.getMessage())));
        }
    }

    @Test
    public void testMaxRetries() throws Exception {
        // set operation timeout to 300s to make sure that this test only be affected by retry number
        TestAsyncAdminBuilder.TEST_UTIL.getConfiguration().setInt(HBASE_CLIENT_OPERATION_TIMEOUT, 300000);
        TestAsyncAdminBuilder.TEST_UTIL.getConfiguration().set(MASTER_COPROCESSOR_CONF_KEY, TestAsyncAdminBuilder.TestMaxRetriesCoprocessor.class.getName());
        TestAsyncAdminBuilder.TEST_UTIL.startMiniCluster(2);
        TestAsyncAdminBuilder.ASYNC_CONN = ConnectionFactory.createAsyncConnection(TestAsyncAdminBuilder.TEST_UTIL.getConfiguration()).get();
        try {
            getAdminBuilder.get().setMaxRetries(((TestAsyncAdminBuilder.DEFAULT_RETRIES_NUMBER) / 2)).build().getNamespaceDescriptor(NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR).get();
            Assert.fail("We expect an exception here");
        } catch (Exception e) {
            // expected
        }
        try {
            getAdminBuilder.get().setMaxRetries(((TestAsyncAdminBuilder.DEFAULT_RETRIES_NUMBER) * 2)).build().getNamespaceDescriptor(NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR).get();
        } catch (Exception e) {
            Assert.fail(("The Operation should succeed, unexpected exception: " + (e.getMessage())));
        }
    }

    public static class TestRpcTimeoutCoprocessor implements MasterCoprocessor , MasterObserver {
        public TestRpcTimeoutCoprocessor() {
        }

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> ctx, String namespace) throws IOException {
            Threads.sleep(TestAsyncAdminBuilder.DEFAULT_RPC_TIMEOUT);
        }
    }

    public static class TestOperationTimeoutCoprocessor implements MasterCoprocessor , MasterObserver {
        AtomicLong sleepTime = new AtomicLong(0);

        public TestOperationTimeoutCoprocessor() {
        }

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> ctx, String namespace) throws IOException {
            Threads.sleep(((TestAsyncAdminBuilder.DEFAULT_RPC_TIMEOUT) / 2));
            if ((sleepTime.addAndGet(((TestAsyncAdminBuilder.DEFAULT_RPC_TIMEOUT) / 2))) < (TestAsyncAdminBuilder.DEFAULT_OPERATION_TIMEOUT)) {
                throw new IOException("call fail");
            }
        }
    }

    public static class TestMaxRetriesCoprocessor implements MasterCoprocessor , MasterObserver {
        AtomicLong retryNum = new AtomicLong(0);

        public TestMaxRetriesCoprocessor() {
        }

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetNamespaceDescriptor(ObserverContext<MasterCoprocessorEnvironment> ctx, String namespace) throws IOException {
            if ((retryNum.getAndIncrement()) < (TestAsyncAdminBuilder.DEFAULT_RETRIES_NUMBER)) {
                throw new IOException("call fail");
            }
        }
    }
}

