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
package org.apache.hadoop.util;


import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.DiskChecker.FileIoProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDiskChecker {
    public static final Logger LOG = LoggerFactory.getLogger(TestDiskChecker.class);

    private final FsPermission defaultPerm = new FsPermission("755");

    private final FsPermission invalidPerm = new FsPermission("000");

    private FileIoProvider fileIoProvider = null;

    @Test(timeout = 30000)
    public void testMkdirs_dirExists() throws Throwable {
        _mkdirs(true, defaultPerm, defaultPerm);
    }

    @Test(timeout = 30000)
    public void testMkdirs_noDir() throws Throwable {
        _mkdirs(false, defaultPerm, defaultPerm);
    }

    @Test(timeout = 30000)
    public void testMkdirs_dirExists_badUmask() throws Throwable {
        _mkdirs(true, defaultPerm, invalidPerm);
    }

    @Test(timeout = 30000)
    public void testMkdirs_noDir_badUmask() throws Throwable {
        _mkdirs(false, defaultPerm, invalidPerm);
    }

    @Test(timeout = 30000)
    public void testCheckDir_normal() throws Throwable {
        _checkDirs(true, new FsPermission("755"), true);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notDir() throws Throwable {
        _checkDirs(false, new FsPermission("000"), false);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notReadable() throws Throwable {
        _checkDirs(true, new FsPermission("000"), false);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notWritable() throws Throwable {
        _checkDirs(true, new FsPermission("444"), false);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notListable() throws Throwable {
        _checkDirs(true, new FsPermission("666"), false);// not listable

    }

    /**
     * These test cases test to test the creation of a local folder with correct
     * permission for result of mapper.
     */
    @Test(timeout = 30000)
    public void testCheckDir_normal_local() throws Throwable {
        checkDirs(true, "755", true);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notDir_local() throws Throwable {
        checkDirs(false, "000", false);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notReadable_local() throws Throwable {
        checkDirs(true, "000", false);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notWritable_local() throws Throwable {
        checkDirs(true, "444", false);
    }

    @Test(timeout = 30000)
    public void testCheckDir_notListable_local() throws Throwable {
        checkDirs(true, "666", false);
    }
}

