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
package org.apache.hadoop.hdfs.qjournal;


import java.io.File;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.minikdc.MiniKdc;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestSecureNNWithQJM {
    private static final Path TEST_PATH = new Path("/test-dir");

    private static final Path TEST_PATH_2 = new Path("/test-dir-2");

    private static HdfsConfiguration baseConf;

    private static File baseDir;

    private static String keystoresDir;

    private static String sslConfDir;

    private static MiniKdc kdc;

    private MiniDFSCluster cluster;

    private HdfsConfiguration conf;

    private FileSystem fs;

    private MiniJournalCluster mjc;

    @Rule
    public Timeout timeout = new Timeout(180000);

    @Test
    public void testSecureMode() throws Exception {
        doNNWithQJMTest();
    }

    @Test
    public void testSecondaryNameNodeHttpAddressNotNeeded() throws Exception {
        conf.set(DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, "null");
        doNNWithQJMTest();
    }
}

