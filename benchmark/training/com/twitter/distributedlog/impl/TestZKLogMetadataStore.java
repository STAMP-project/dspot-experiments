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
package com.twitter.distributedlog.impl;


import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import java.net.URI;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test ZK based metadata store.
 */
public class TestZKLogMetadataStore extends TestDistributedLogBase {
    private static final int zkSessionTimeoutMs = 2000;

    @Rule
    public TestName runtime = new TestName();

    protected final DistributedLogConfiguration baseConf = new DistributedLogConfiguration();

    protected ZooKeeperClient zkc;

    protected ZKLogMetadataStore metadataStore;

    protected OrderedScheduler scheduler;

    protected URI uri;

    @Test(timeout = 60000)
    public void testCreateLog() throws Exception {
        Assert.assertEquals(uri, FutureUtils.result(metadataStore.createLog("test")));
    }

    @Test(timeout = 60000)
    public void testGetLogLocation() throws Exception {
        Optional<URI> uriOptional = FutureUtils.result(metadataStore.getLogLocation("test"));
        Assert.assertTrue(uriOptional.isPresent());
        Assert.assertEquals(uri, uriOptional.get());
    }

    @Test(timeout = 60000)
    public void testGetLogs() throws Exception {
        Set<String> logs = Sets.newHashSet();
        for (int i = 0; i < 10; i++) {
            String logName = "test-" + i;
            logs.add(logName);
            createLogInNamespace(uri, logName);
        }
        Set<String> result = Sets.newHashSet(FutureUtils.result(metadataStore.getLogs()));
        Assert.assertEquals(10, result.size());
        Assert.assertTrue(Sets.difference(logs, result).isEmpty());
    }
}

