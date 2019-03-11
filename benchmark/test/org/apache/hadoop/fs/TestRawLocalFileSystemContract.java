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
package org.apache.hadoop.fs;


import RawLocalFileSystem.DeprecatedRawLocalFileStatus;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.StatUtils;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test filesystem contracts with {@link RawLocalFileSystem}.
 * Root directory related tests from super class will work into target
 * directory since we have no permission to write / on local filesystem.
 */
public class TestRawLocalFileSystemContract extends FileSystemContractBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestRawLocalFileSystemContract.class);

    private static final Path TEST_BASE_DIR = new Path(GenericTestUtils.getRandomizedTestDir().getAbsolutePath());

    // cross-check getPermission using both native/non-native
    @Test
    @SuppressWarnings("deprecation")
    public void testPermission() throws Exception {
        Path testDir = getTestBaseDir();
        String testFilename = "teststat2File";
        Path path = new Path(testDir, testFilename);
        RawLocalFileSystem rfs = new RawLocalFileSystem();
        Configuration conf = new Configuration();
        rfs.initialize(rfs.getUri(), conf);
        rfs.createNewFile(path);
        File file = rfs.pathToFile(path);
        long defaultBlockSize = rfs.getDefaultBlockSize(path);
        // 
        // test initial permission
        // 
        RawLocalFileSystem.DeprecatedRawLocalFileStatus fsNIO = new RawLocalFileSystem.DeprecatedRawLocalFileStatus(file, defaultBlockSize, rfs);
        fsNIO.loadPermissionInfoByNativeIO();
        RawLocalFileSystem.DeprecatedRawLocalFileStatus fsnonNIO = new RawLocalFileSystem.DeprecatedRawLocalFileStatus(file, defaultBlockSize, rfs);
        fsnonNIO.loadPermissionInfoByNonNativeIO();
        Assert.assertEquals(fsNIO.getOwner(), fsnonNIO.getOwner());
        Assert.assertEquals(fsNIO.getGroup(), fsnonNIO.getGroup());
        Assert.assertEquals(fsNIO.getPermission(), fsnonNIO.getPermission());
        TestRawLocalFileSystemContract.LOG.info("owner: {}, group: {}, permission: {}, isSticky: {}", fsNIO.getOwner(), fsNIO.getGroup(), fsNIO.getPermission(), fsNIO.getPermission().getStickyBit());
        // 
        // test normal chmod - no sticky bit
        // 
        StatUtils.setPermissionFromProcess("644", file.getPath());
        fsNIO.loadPermissionInfoByNativeIO();
        fsnonNIO.loadPermissionInfoByNonNativeIO();
        Assert.assertEquals(fsNIO.getPermission(), fsnonNIO.getPermission());
        Assert.assertEquals(644, fsNIO.getPermission().toOctal());
        Assert.assertFalse(fsNIO.getPermission().getStickyBit());
        Assert.assertFalse(fsnonNIO.getPermission().getStickyBit());
        // 
        // test sticky bit
        // unfortunately, cannot be done in Windows environments
        // 
        if (!(Shell.WINDOWS)) {
            // 
            // add sticky bit
            // 
            StatUtils.setPermissionFromProcess("1644", file.getPath());
            fsNIO.loadPermissionInfoByNativeIO();
            fsnonNIO.loadPermissionInfoByNonNativeIO();
            Assert.assertEquals(fsNIO.getPermission(), fsnonNIO.getPermission());
            Assert.assertEquals(1644, fsNIO.getPermission().toOctal());
            Assert.assertEquals(true, fsNIO.getPermission().getStickyBit());
            Assert.assertEquals(true, fsnonNIO.getPermission().getStickyBit());
            // 
            // remove sticky bit
            // 
            StatUtils.setPermissionFromProcess("-t", file.getPath());
            fsNIO.loadPermissionInfoByNativeIO();
            fsnonNIO.loadPermissionInfoByNonNativeIO();
            Assert.assertEquals(fsNIO.getPermission(), fsnonNIO.getPermission());
            Assert.assertEquals(644, fsNIO.getPermission().toOctal());
            Assert.assertEquals(false, fsNIO.getPermission().getStickyBit());
            Assert.assertEquals(false, fsnonNIO.getPermission().getStickyBit());
        }
    }
}

