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


import java.io.File;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.junit.Test;


public class TestFetchImage {
    private static final File FETCHED_IMAGE_FILE = GenericTestUtils.getTestDir("target/fetched-image-dir");

    // Shamelessly stolen from NNStorage.
    private static final Pattern IMAGE_REGEX = Pattern.compile("fsimage_(\\d+)");

    private MiniDFSCluster cluster;

    private NameNode nn0 = null;

    private NameNode nn1 = null;

    private Configuration conf = null;

    /**
     * Download a few fsimages using `hdfs dfsadmin -fetchImage ...' and verify
     * the results.
     */
    @Test(timeout = 30000)
    public void testFetchImageHA() throws Exception {
        final Path parent = new Path(PathUtils.getTestPath(getClass()), GenericTestUtils.getMethodName());
        int nnIndex = 0;
        /* run on nn0 as active */
        cluster.transitionToActive(nnIndex);
        testFetchImageInternal(nnIndex, new Path(parent, "dir1"), new Path(parent, "dir2"));
        /* run on nn1 as active */
        nnIndex = 1;
        HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
        cluster.transitionToActive(nnIndex);
        testFetchImageInternal(nnIndex, new Path(parent, "dir3"), new Path(parent, "dir4"));
    }
}

