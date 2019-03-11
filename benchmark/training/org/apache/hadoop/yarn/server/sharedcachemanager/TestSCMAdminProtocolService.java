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
package org.apache.hadoop.yarn.server.sharedcachemanager;


import org.apache.hadoop.yarn.client.SCMAdmin;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.SCMAdminProtocol;
import org.apache.hadoop.yarn.server.api.protocolrecords.RunSharedCacheCleanerTaskRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RunSharedCacheCleanerTaskResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RunSharedCacheCleanerTaskResponsePBImpl;
import org.apache.hadoop.yarn.server.sharedcachemanager.store.SCMStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Basic unit tests for the SCM Admin Protocol Service and SCMAdmin.
 */
public class TestSCMAdminProtocolService {
    static SCMAdminProtocolService service;

    static SCMAdminProtocol SCMAdminProxy;

    static SCMAdminProtocol mockAdmin;

    static SCMAdmin adminCLI;

    static SCMStore store;

    static CleanerService cleaner;

    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    @Test
    public void testRunCleanerTask() throws Exception {
        Mockito.doNothing().when(TestSCMAdminProtocolService.cleaner).runCleanerTask();
        RunSharedCacheCleanerTaskRequest request = recordFactory.newRecordInstance(RunSharedCacheCleanerTaskRequest.class);
        RunSharedCacheCleanerTaskResponse response = TestSCMAdminProtocolService.SCMAdminProxy.runCleanerTask(request);
        Assert.assertTrue("cleaner task request isn't accepted", response.getAccepted());
        Mockito.verify(TestSCMAdminProtocolService.service, Mockito.times(1)).runCleanerTask(ArgumentMatchers.any(RunSharedCacheCleanerTaskRequest.class));
    }

    @Test
    public void testRunCleanerTaskCLI() throws Exception {
        String[] args = new String[]{ "-runCleanerTask" };
        RunSharedCacheCleanerTaskResponse rp = new RunSharedCacheCleanerTaskResponsePBImpl();
        rp.setAccepted(true);
        Mockito.when(TestSCMAdminProtocolService.mockAdmin.runCleanerTask(ArgumentMatchers.isA(RunSharedCacheCleanerTaskRequest.class))).thenReturn(rp);
        Assert.assertEquals(0, TestSCMAdminProtocolService.adminCLI.run(args));
        rp.setAccepted(false);
        Mockito.when(TestSCMAdminProtocolService.mockAdmin.runCleanerTask(ArgumentMatchers.isA(RunSharedCacheCleanerTaskRequest.class))).thenReturn(rp);
        Assert.assertEquals(1, TestSCMAdminProtocolService.adminCLI.run(args));
        Mockito.verify(TestSCMAdminProtocolService.mockAdmin, Mockito.times(2)).runCleanerTask(ArgumentMatchers.any(RunSharedCacheCleanerTaskRequest.class));
    }
}

