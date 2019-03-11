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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;


/**
 * 1) save xattrs, restart NN, assert xattrs reloaded from edit log,
 * 2) save xattrs, create new checkpoint, restart NN, assert xattrs
 * reloaded from fsimage
 */
public class TestFSImageWithXAttr {
    private static Configuration conf;

    private static MiniDFSCluster cluster;

    // xattrs
    private static final String name1 = "user.a1";

    private static final byte[] value1 = new byte[]{ 49, 50, 51 };

    private static final byte[] newValue1 = new byte[]{ 49, 49, 49 };

    private static final String name2 = "user.a2";

    private static final byte[] value2 = new byte[]{ 55, 56, 57 };

    private static final String name3 = "user.a3";

    private static final byte[] value3 = new byte[]{  };

    @Test
    public void testPersistXAttr() throws IOException {
        testXAttr(true);
    }

    @Test
    public void testXAttrEditLog() throws IOException {
        testXAttr(false);
    }
}

