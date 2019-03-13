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
package org.apache.hadoop.hdfs;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Test;


public class TestDFSShellGenericOptions {
    @Test
    public void testDFSCommand() throws IOException {
        String namenode = null;
        MiniDFSCluster cluster = null;
        try {
            Configuration conf = new HdfsConfiguration();
            cluster = new MiniDFSCluster.Builder(conf).build();
            namenode = FileSystem.getDefaultUri(conf).toString();
            String[] args = new String[4];
            args[2] = "-mkdir";
            args[3] = "/data";
            testFsOption(args, namenode);
            testConfOption(args, namenode);
            testPropertyOption(args, namenode);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

