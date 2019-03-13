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
package org.apache.hadoop.hdfs.server.namenode;


import ExitUtil.EXIT_EXCEPTION_MESSAGE;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestMetadataVersionOutput {
    private MiniDFSCluster dfsCluster = null;

    private final Configuration conf = new Configuration();

    @Test(timeout = 30000)
    public void testMetadataVersionOutput() throws IOException {
        initConfig();
        dfsCluster = new MiniDFSCluster.Builder(conf).manageNameDfsDirs(false).numDataNodes(1).checkExitOnShutdown(false).build();
        dfsCluster.waitClusterUp();
        dfsCluster.shutdown(false);
        initConfig();
        final PrintStream origOut = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream stdOut = new PrintStream(baos);
        try {
            System.setOut(stdOut);
            try {
                NameNode.createNameNode(new String[]{ "-metadataVersion" }, conf);
            } catch (Exception e) {
                GenericTestUtils.assertExceptionContains(EXIT_EXCEPTION_MESSAGE, e);
            }
            /* Check if meta data version is printed correctly. */
            final String verNumStr = (HdfsServerConstants.NAMENODE_LAYOUT_VERSION) + "";
            Assert.assertTrue(baos.toString("UTF-8").contains(("HDFS Image Version: " + verNumStr)));
            Assert.assertTrue(baos.toString("UTF-8").contains(("Software format version: " + verNumStr)));
        } finally {
            System.setOut(origOut);
        }
    }
}

