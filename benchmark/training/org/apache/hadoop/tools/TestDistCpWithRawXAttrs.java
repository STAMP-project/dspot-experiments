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


import DistCpConstants.SUCCESS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;


/**
 * Tests distcp in combination with HDFS raw.* XAttrs.
 */
public class TestDistCpWithRawXAttrs {
    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static FileSystem fs;

    private static final String rawName1 = "raw.a1";

    private static final byte[] rawValue1 = new byte[]{ 55, 56, 57 };

    private static final String userName1 = "user.a1";

    private static final byte[] userValue1 = new byte[]{ 56, 56, 56 };

    private static final Path dir1 = new Path("/src/dir1");

    private static final Path subDir1 = new Path(TestDistCpWithRawXAttrs.dir1, "subdir1");

    private static final Path file1 = new Path("/src/file1");

    private static final String rawRootName = "/.reserved/raw";

    private static final String rootedDestName = "/dest";

    private static final String rootedSrcName = "/src";

    private static final String rawDestName = "/.reserved/raw/dest";

    private static final String rawSrcName = "/.reserved/raw/src";

    /* Test that XAttrs and raw.* XAttrs are preserved when appropriate. */
    @Test
    public void testPreserveRawXAttrs1() throws Exception {
        final String relSrc = "/./.reserved/../.reserved/raw/../raw/src/../src";
        final String relDst = "/./.reserved/../.reserved/raw/../raw/dest/../dest";
        doTestPreserveRawXAttrs(relSrc, relDst, "-px", true, true, SUCCESS);
        doTestStandardPreserveRawXAttrs("-px", true);
        final Path savedWd = TestDistCpWithRawXAttrs.fs.getWorkingDirectory();
        try {
            TestDistCpWithRawXAttrs.fs.setWorkingDirectory(new Path("/.reserved/raw"));
            doTestPreserveRawXAttrs(("../.." + (TestDistCpWithRawXAttrs.rawSrcName)), ("../.." + (TestDistCpWithRawXAttrs.rawDestName)), "-px", true, true, SUCCESS);
        } finally {
            TestDistCpWithRawXAttrs.fs.setWorkingDirectory(savedWd);
        }
    }

    /* Test that XAttrs are not preserved and raw.* are when appropriate. */
    @Test
    public void testPreserveRawXAttrs2() throws Exception {
        doTestStandardPreserveRawXAttrs("-p", false);
    }

    /* Test that XAttrs are not preserved and raw.* are when appropriate. */
    @Test
    public void testPreserveRawXAttrs3() throws Exception {
        doTestStandardPreserveRawXAttrs(null, false);
    }

    @Test
    public void testPreserveRawXAttrs4() throws Exception {
        doTestStandardPreserveRawXAttrs("-update -delete", false);
    }

    private static Path[] pathnames = new Path[]{ new Path("dir1"), new Path("dir1/subdir1"), new Path("file1") };
}

