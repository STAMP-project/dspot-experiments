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
package org.apache.hadoop;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.ipc.RefreshHandler;
import org.apache.hadoop.ipc.RefreshRegistry;
import org.apache.hadoop.ipc.RefreshResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Before all tests, a MiniDFSCluster is spun up.
 * Before each test, mock refresh handlers are created and registered.
 * After each test, the mock handlers are unregistered.
 * After all tests, the cluster is spun down.
 */
public class TestGenericRefresh {
    private static MiniDFSCluster cluster;

    private static Configuration config;

    private static RefreshHandler firstHandler;

    private static RefreshHandler secondHandler;

    @Test
    public void testInvalidCommand() throws Exception {
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "nn" };
        int exitCode = admin.run(args);
        Assert.assertEquals("DFSAdmin should fail due to bad args", (-1), exitCode);
    }

    @Test
    public void testInvalidIdentifier() throws Exception {
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "unregisteredIdentity" };
        int exitCode = admin.run(args);
        Assert.assertEquals("DFSAdmin should fail due to no handler registered", (-1), exitCode);
    }

    @Test
    public void testValidIdentifier() throws Exception {
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "firstHandler" };
        int exitCode = admin.run(args);
        Assert.assertEquals("DFSAdmin should succeed", 0, exitCode);
        Mockito.verify(TestGenericRefresh.firstHandler).handleRefresh("firstHandler", new String[]{  });
        // Second handler was never called
        Mockito.verify(TestGenericRefresh.secondHandler, Mockito.never()).handleRefresh(Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void testVariableArgs() throws Exception {
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "secondHandler", "one" };
        int exitCode = admin.run(args);
        Assert.assertEquals("DFSAdmin should return 2", 2, exitCode);
        exitCode = admin.run(new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "secondHandler", "one", "two" });
        Assert.assertEquals("DFSAdmin should now return 3", 3, exitCode);
        Mockito.verify(TestGenericRefresh.secondHandler).handleRefresh("secondHandler", new String[]{ "one" });
        Mockito.verify(TestGenericRefresh.secondHandler).handleRefresh("secondHandler", new String[]{ "one", "two" });
    }

    @Test
    public void testUnregistration() throws Exception {
        RefreshRegistry.defaultRegistry().unregisterAll("firstHandler");
        // And now this should fail
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "firstHandler" };
        int exitCode = admin.run(args);
        Assert.assertEquals("DFSAdmin should return -1", (-1), exitCode);
    }

    @Test
    public void testUnregistrationReturnValue() {
        RefreshHandler mockHandler = Mockito.mock(RefreshHandler.class);
        RefreshRegistry.defaultRegistry().register("test", mockHandler);
        boolean ret = RefreshRegistry.defaultRegistry().unregister("test", mockHandler);
        Assert.assertTrue(ret);
    }

    @Test
    public void testMultipleRegistration() throws Exception {
        RefreshRegistry.defaultRegistry().register("sharedId", TestGenericRefresh.firstHandler);
        RefreshRegistry.defaultRegistry().register("sharedId", TestGenericRefresh.secondHandler);
        // this should trigger both
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "sharedId", "one" };
        int exitCode = admin.run(args);
        Assert.assertEquals((-1), exitCode);// -1 because one of the responses is unregistered

        // verify we called both
        Mockito.verify(TestGenericRefresh.firstHandler).handleRefresh("sharedId", new String[]{ "one" });
        Mockito.verify(TestGenericRefresh.secondHandler).handleRefresh("sharedId", new String[]{ "one" });
        RefreshRegistry.defaultRegistry().unregisterAll("sharedId");
    }

    @Test
    public void testMultipleReturnCodeMerging() throws Exception {
        // Two handlers which return two non-zero values
        RefreshHandler handlerOne = Mockito.mock(RefreshHandler.class);
        Mockito.when(handlerOne.handleRefresh(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(new RefreshResponse(23, "Twenty Three"));
        RefreshHandler handlerTwo = Mockito.mock(RefreshHandler.class);
        Mockito.when(handlerTwo.handleRefresh(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(new RefreshResponse(10, "Ten"));
        // Then registered to the same ID
        RefreshRegistry.defaultRegistry().register("shared", handlerOne);
        RefreshRegistry.defaultRegistry().register("shared", handlerTwo);
        // We refresh both
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "shared" };
        int exitCode = admin.run(args);
        Assert.assertEquals((-1), exitCode);// We get -1 because of our logic for melding non-zero return codes

        // Verify we called both
        Mockito.verify(handlerOne).handleRefresh("shared", new String[]{  });
        Mockito.verify(handlerTwo).handleRefresh("shared", new String[]{  });
        RefreshRegistry.defaultRegistry().unregisterAll("shared");
    }

    @Test
    public void testExceptionResultsInNormalError() throws Exception {
        // In this test, we ensure that all handlers are called even if we throw an exception in one
        RefreshHandler exceptionalHandler = Mockito.mock(RefreshHandler.class);
        Mockito.when(exceptionalHandler.handleRefresh(Mockito.anyString(), Mockito.any(String[].class))).thenThrow(new RuntimeException("Exceptional Handler Throws Exception"));
        RefreshHandler otherExceptionalHandler = Mockito.mock(RefreshHandler.class);
        Mockito.when(otherExceptionalHandler.handleRefresh(Mockito.anyString(), Mockito.any(String[].class))).thenThrow(new RuntimeException("More Exceptions"));
        RefreshRegistry.defaultRegistry().register("exceptional", exceptionalHandler);
        RefreshRegistry.defaultRegistry().register("exceptional", otherExceptionalHandler);
        DFSAdmin admin = new DFSAdmin(TestGenericRefresh.config);
        String[] args = new String[]{ "-refresh", "localhost:" + (TestGenericRefresh.cluster.getNameNodePort()), "exceptional" };
        int exitCode = admin.run(args);
        Assert.assertEquals((-1), exitCode);// Exceptions result in a -1

        Mockito.verify(exceptionalHandler).handleRefresh("exceptional", new String[]{  });
        Mockito.verify(otherExceptionalHandler).handleRefresh("exceptional", new String[]{  });
        RefreshRegistry.defaultRegistry().unregisterAll("exceptional");
    }
}

