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
package org.apache.hadoop.hive.io;


import AclEntryScope.ACCESS;
import AclEntryType.GROUP;
import AclEntryType.OTHER;
import AclEntryType.USER;
import FsAction.ALL;
import FsAction.NONE;
import FsAction.READ_EXECUTE;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.io.HdfsUtils.HadoopFileStatus;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestHadoopFileStatus {
    private static HiveConf hiveConf;

    private static HadoopFileStatus sourceStatus;

    /* HdfsUtils.setFullFileStatus(..) is called from multiple parallel threads. If AclEntries
    is modifiable the method will not be thread safe and could cause random concurrency issues
    This test case checks if the aclEntries returned from HadoopFileStatus is thread-safe or not
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testHadoopFileStatusAclEntries() throws IOException {
        FileSystem mockDfs = Mockito.mock(DistributedFileSystem.class);
        Path mockPath = Mockito.mock(Path.class);
        List<AclEntry> aclEntries = Lists.newArrayList();
        aclEntries.add(TestHadoopFileStatus.newAclEntry(ACCESS, USER, ALL));
        aclEntries.add(TestHadoopFileStatus.newAclEntry(ACCESS, GROUP, READ_EXECUTE));
        aclEntries.add(TestHadoopFileStatus.newAclEntry(ACCESS, OTHER, NONE));
        AclStatus aclStatus = new AclStatus.Builder().owner("dummyOwner").group("dummyGroup").stickyBit(true).addEntries(aclEntries).build();
        FileStatus mockFileStatus = Mockito.mock(FileStatus.class);
        Mockito.when(mockDfs.getAclStatus(mockPath)).thenReturn(aclStatus);
        Mockito.when(mockDfs.getFileStatus(mockPath)).thenReturn(mockFileStatus);
        TestHadoopFileStatus.sourceStatus = new HadoopFileStatus(TestHadoopFileStatus.hiveConf, mockDfs, mockPath);
        Assert.assertNotNull(TestHadoopFileStatus.sourceStatus.getAclEntries());
        Assert.assertTrue(((TestHadoopFileStatus.sourceStatus.getAclEntries().size()) == 3));
        Iterables.removeIf(TestHadoopFileStatus.sourceStatus.getAclEntries(), new Predicate<AclEntry>() {
            @Override
            public boolean apply(AclEntry input) {
                if ((input.getName()) == null) {
                    return true;
                }
                return false;
            }
        });
    }
}

