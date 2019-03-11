/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.ptest.api.server;


import PTest.Builder;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.apache.hive.ptest.api.Status;
import org.apache.hive.ptest.api.request.TestStartRequest;
import org.apache.hive.ptest.execution.PTest;
import org.apache.hive.ptest.execution.conf.ExecutionContextConfiguration;
import org.apache.hive.ptest.execution.context.ExecutionContext;
import org.apache.hive.ptest.execution.context.ExecutionContextProvider;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class TestTestExecutor {
    private static final String TEST_HANDLE = "myhandle";

    private static final String PRIVATE_KEY = "mykey";

    private static final String PROFILE = "myprole";

    @Rule
    public TemporaryFolder baseDir = new TemporaryFolder();

    private TestExecutor testExecutor;

    private BlockingQueue<Test> testQueue;

    private ExecutionContextConfiguration executionContextConfiguration;

    private ExecutionContextProvider executionContextProvider;

    private ExecutionContext executionContext;

    private Builder ptestBuilder;

    private PTest ptest;

    private Test test;

    private TestStartRequest startRequest;

    private File profileProperties;

    @org.junit.Test
    public void testSuccess() throws Exception {
        Assert.assertTrue(String.valueOf(test.getStatus()), Status.isPending(test.getStatus()));
        testQueue.add(test);
        TimeUnit.SECONDS.sleep(1);
        Assert.assertTrue(String.valueOf(test.getStatus()), Status.isOK(test.getStatus()));
    }

    @org.junit.Test
    public void testTestFailure() throws Exception {
        Assert.assertTrue(String.valueOf(test.getStatus()), Status.isPending(test.getStatus()));
        Mockito.when(ptest.run()).thenReturn(1);
        testQueue.add(test);
        TimeUnit.SECONDS.sleep(1);
        Assert.assertTrue(String.valueOf(test.getStatus()), Status.isFailed(test.getStatus()));
    }

    @org.junit.Test
    public void testNoProfileProperties() throws Exception {
        Assert.assertTrue(String.valueOf(test.getStatus()), Status.isPending(test.getStatus()));
        Assert.assertTrue(profileProperties.toString(), profileProperties.delete());
        testQueue.add(test);
        TimeUnit.SECONDS.sleep(1);
        Assert.assertTrue(String.valueOf(test.getStatus()), Status.isIllegalArgument(test.getStatus()));
    }
}

