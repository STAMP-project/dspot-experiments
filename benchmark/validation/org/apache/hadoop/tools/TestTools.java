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
package org.apache.hadoop.tools;


import com.google.common.collect.ImmutableSet;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.hdfs.tools.DelegationTokenFetcher;
import org.apache.hadoop.hdfs.tools.JMXGet;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestTools {
    private static final int PIPE_BUFFER_SIZE = 1024 * 5;

    private static final String INVALID_OPTION = "-invalidOption";

    private static final String[] OPTIONS = new String[2];

    @Test
    public void testDelegationTokenFetcherPrintUsage() {
        String pattern = "Options:";
        checkOutput(new String[]{ "-help" }, pattern, System.out, DelegationTokenFetcher.class);
    }

    @Test
    public void testDelegationTokenFetcherErrorOption() {
        String pattern = "ERROR: Only specify cancel, renew or print.";
        checkOutput(new String[]{ "-cancel", "-renew" }, pattern, System.err, DelegationTokenFetcher.class);
    }

    @Test
    public void testJMXToolHelp() {
        String pattern = "usage: jmxget options are:";
        checkOutput(new String[]{ "-help" }, pattern, System.out, JMXGet.class);
    }

    @Test
    public void testJMXToolAdditionParameter() {
        String pattern = "key = -addition";
        checkOutput(new String[]{ "-service=NameNode", "-server=localhost", "-addition" }, pattern, System.err, JMXGet.class);
    }

    @Test
    public void testDFSAdminInvalidUsageHelp() {
        ImmutableSet<String> args = ImmutableSet.of("-report", "-saveNamespace", "-rollEdits", "-restoreFailedStorage", "-refreshNodes", "-finalizeUpgrade", "-metasave", "-refreshUserToGroupsMappings", "-printTopology", "-refreshNamenodes", "-deleteBlockPool", "-setBalancerBandwidth", "-fetchImage");
        try {
            for (String arg : args)
                Assert.assertTrue(((ToolRunner.run(new DFSAdmin(), TestTools.fillArgs(arg))) == (-1)));

            Assert.assertTrue(((ToolRunner.run(new DFSAdmin(), new String[]{ "-help", "-some" })) == 0));
        } catch (Exception e) {
            Assert.fail(("testDFSAdminHelp error" + e));
        }
        String pattern = "Usage: hdfs dfsadmin";
        checkOutput(new String[]{ "-cancel", "-renew" }, pattern, System.err, DFSAdmin.class);
    }
}

