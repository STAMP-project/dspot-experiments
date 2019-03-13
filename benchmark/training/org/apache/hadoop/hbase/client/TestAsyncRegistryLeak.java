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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.FutureUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, SmallTests.class })
public class TestAsyncRegistryLeak {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncRegistryLeak.class);

    public static final class AsyncRegistryForTest extends DoNothingAsyncRegistry {
        private boolean closed = false;

        public AsyncRegistryForTest(Configuration conf) {
            super(conf);
            TestAsyncRegistryLeak.CREATED.add(this);
        }

        @Override
        public CompletableFuture<String> getClusterId() {
            return FutureUtils.failedFuture(new IOException("inject error"));
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    private static final List<TestAsyncRegistryLeak.AsyncRegistryForTest> CREATED = new ArrayList<>();

    private static Configuration CONF = HBaseConfiguration.create();

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            try {
                ConnectionFactory.createAsyncConnection(TestAsyncRegistryLeak.CONF).get();
                Assert.fail();
            } catch (ExecutionException e) {
                // expected
            }
        }
        Assert.assertEquals(10, TestAsyncRegistryLeak.CREATED.size());
        TestAsyncRegistryLeak.CREATED.forEach(( r) -> Assert.assertTrue(r.closed));
    }
}

