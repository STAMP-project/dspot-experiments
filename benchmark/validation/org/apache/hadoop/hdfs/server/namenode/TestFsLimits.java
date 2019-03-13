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


import DFSConfigKeys.DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY;
import DFSConfigKeys.DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.protocol.FSLimitException.MaxDirectoryItemsExceededException;
import org.apache.hadoop.hdfs.protocol.FSLimitException.PathComponentTooLongException;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


public class TestFsLimits {
    static Configuration conf;

    static FSNamesystem fs;

    static boolean fsIsReady;

    static final PermissionStatus perms = new PermissionStatus("admin", "admin", FsPermission.getDefault());

    @Test
    public void testNoLimits() throws Exception {
        mkdirs("/1", null);
        mkdirs("/22", null);
        mkdirs("/333", null);
        mkdirs("/4444", null);
        mkdirs("/55555", null);
        mkdirs(("/1/" + (HdfsConstants.DOT_SNAPSHOT_DIR)), HadoopIllegalArgumentException.class);
    }

    @Test
    public void testMaxComponentLength() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY, 2);
        mkdirs("/1", null);
        mkdirs("/22", null);
        mkdirs("/333", PathComponentTooLongException.class);
        mkdirs("/4444", PathComponentTooLongException.class);
    }

    @Test
    public void testMaxComponentLengthRename() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY, 2);
        mkdirs("/5", null);
        rename("/5", "/555", PathComponentTooLongException.class);
        rename("/5", "/55", null);
        mkdirs("/6", null);
        deprecatedRename("/6", "/666", PathComponentTooLongException.class);
        deprecatedRename("/6", "/66", null);
    }

    @Test
    public void testMaxDirItems() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY, 2);
        mkdirs("/1", null);
        mkdirs("/22", null);
        mkdirs("/333", MaxDirectoryItemsExceededException.class);
        mkdirs("/4444", MaxDirectoryItemsExceededException.class);
    }

    @Test
    public void testMaxDirItemsRename() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY, 2);
        mkdirs("/1", null);
        mkdirs("/2", null);
        mkdirs("/2/A", null);
        rename("/2/A", "/A", MaxDirectoryItemsExceededException.class);
        rename("/2/A", "/1/A", null);
        mkdirs("/2/B", null);
        deprecatedRename("/2/B", "/B", MaxDirectoryItemsExceededException.class);
        deprecatedRename("/2/B", "/1/B", null);
        rename("/1", "/3", null);
        deprecatedRename("/2", "/4", null);
    }

    @Test
    public void testMaxDirItemsLimits() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY, 0);
        try {
            mkdirs("1", null);
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Cannot set dfs", e);
        }
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY, ((64 * 100) * 1024));
        try {
            mkdirs("1", null);
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Cannot set dfs", e);
        }
    }

    @Test
    public void testMaxComponentsAndMaxDirItems() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY, 3);
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY, 2);
        mkdirs("/1", null);
        mkdirs("/22", null);
        mkdirs("/333", MaxDirectoryItemsExceededException.class);
        mkdirs("/4444", PathComponentTooLongException.class);
    }

    @Test
    public void testDuringEditLogs() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY, 3);
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY, 2);
        TestFsLimits.fsIsReady = false;
        mkdirs("/1", null);
        mkdirs("/22", null);
        mkdirs("/333", null);
        mkdirs("/4444", null);
        mkdirs(("/1/" + (HdfsConstants.DOT_SNAPSHOT_DIR)), HadoopIllegalArgumentException.class);
    }

    /**
     * This test verifies that error string contains the
     * right parent directory name if the operation fails with
     * PathComponentTooLongException
     */
    @Test
    public void testParentDirectoryNameIsCorrect() throws Exception {
        TestFsLimits.conf.setInt(DFS_NAMENODE_MAX_COMPONENT_LENGTH_KEY, 20);
        mkdirs("/user", null);
        mkdirs("/user/testHome", null);
        mkdirs("/user/testHome/FileNameLength", null);
        mkdirCheckParentDirectory("/user/testHome/FileNameLength/really_big_name_0003_fail", "/user/testHome/FileNameLength", PathComponentTooLongException.class);
        renameCheckParentDirectory("/user/testHome/FileNameLength", "/user/testHome/really_big_name_0003_fail", "/user/testHome", PathComponentTooLongException.class);
    }
}

