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
package org.apache.hadoop.mapreduce;


import FileSystem.FS_DEFAULT_NAME_KEY;
import JTConfig.JT_IPC_ADDRESS;
import MRConfig.FRAMEWORK_NAME;
import MRConfig.YARN_FRAMEWORK_NAME;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.mapred.LocalJobRunner;
import org.apache.hadoop.mapred.YARNRunner;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestClientProtocolProviderImpls {
    @Test
    public void testClusterWithLocalClientProvider() throws Exception {
        Configuration conf = new Configuration();
        conf.set(FRAMEWORK_NAME, "local");
        Cluster cluster = new Cluster(conf);
        Assert.assertTrue(((cluster.getClient()) instanceof LocalJobRunner));
        cluster.close();
    }

    @Test
    public void testClusterWithJTClientProvider() throws Exception {
        Configuration conf = new Configuration();
        try {
            conf.set(FRAMEWORK_NAME, "classic");
            conf.set(JT_IPC_ADDRESS, "local");
            new Cluster(conf);
            Assert.fail(("Cluster with classic Framework name should not use " + "local JT address"));
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot initialize Cluster. Please check"));
        }
    }

    @Test
    public void testClusterWithYarnClientProvider() throws Exception {
        Configuration conf = new Configuration();
        conf.set(FRAMEWORK_NAME, "yarn");
        Cluster cluster = new Cluster(conf);
        Assert.assertTrue(((cluster.getClient()) instanceof YARNRunner));
        cluster.close();
    }

    @Test
    public void testClusterException() {
        Configuration conf = new Configuration();
        try {
            conf.set(FRAMEWORK_NAME, "incorrect");
            new Cluster(conf);
            Assert.fail("Cluster should not be initialized with incorrect framework name");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Cannot initialize Cluster. Please check"));
        }
    }

    @Test
    public void testClusterExceptionRootCause() throws Exception {
        final Configuration conf = new Configuration();
        conf.set(FRAMEWORK_NAME, YARN_FRAMEWORK_NAME);
        conf.set(FS_DEFAULT_NAME_KEY, "nosuchfs:///");
        conf.set(JT_IPC_ADDRESS, "local");
        try {
            new Cluster(conf);
            Assert.fail("Cluster init should fail because of non-existing FileSystem");
        } catch (IOException ioEx) {
            final String stackTrace = StringUtils.stringifyException(ioEx);
            Assert.assertTrue("No root cause detected", ((stackTrace.contains(UnsupportedFileSystemException.class.getName())) && (stackTrace.contains("nosuchfs"))));
        }
    }
}

