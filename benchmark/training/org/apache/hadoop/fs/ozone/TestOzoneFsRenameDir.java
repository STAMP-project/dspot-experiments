/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.ozone;


import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit Test for verifying directory rename operation through OzoneFS.
 */
public class TestOzoneFsRenameDir {
    public static final Logger LOG = LoggerFactory.getLogger(TestOzoneFsRenameDir.class);

    private MiniOzoneCluster cluster = null;

    private OzoneConfiguration conf = null;

    private static StorageHandler storageHandler;

    private static FileSystem fs;

    /**
     * Tests directory rename opertion through OzoneFS.
     */
    @Test(timeout = 300000)
    public void testRenameDir() throws IOException {
        final String dir = "/root_dir/dir1";
        final Path source = new Path(((TestOzoneFsRenameDir.fs.getUri().toString()) + dir));
        final Path dest = new Path(((source.toString()) + ".renamed"));
        // Add a sub-dir to the directory to be moved.
        final Path subdir = new Path(source, "sub_dir1");
        TestOzoneFsRenameDir.fs.mkdirs(subdir);
        TestOzoneFsRenameDir.LOG.info("Created dir {}", subdir);
        TestOzoneFsRenameDir.LOG.info("Will move {} to {}", source, dest);
        TestOzoneFsRenameDir.fs.rename(source, dest);
        Assert.assertTrue("Directory rename failed", TestOzoneFsRenameDir.fs.exists(dest));
        // Verify that the subdir is also renamed i.e. keys corresponding to the
        // sub-directories of the renamed directory have also been renamed.
        Assert.assertTrue("Keys under the renamed direcotry not renamed", TestOzoneFsRenameDir.fs.exists(new Path(dest, "sub_dir1")));
    }
}

