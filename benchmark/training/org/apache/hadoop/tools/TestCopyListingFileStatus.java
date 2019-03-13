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


import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify CopyListingFileStatus serialization and requirements for distcp.
 */
public class TestCopyListingFileStatus {
    @Test
    public void testToString() {
        CopyListingFileStatus src = new CopyListingFileStatus(4344L, false, 2, (512 << 20), 1234L, 5678L, new FsPermission(((short) (330))), "dingo", "yaks", new Path("hdfs://localhost:4344"));
        src.toString();
        src = new CopyListingFileStatus();
        src.toString();
    }

    @Test
    public void testCopyListingFileStatusSerialization() throws Exception {
        CopyListingFileStatus src = new CopyListingFileStatus(4344L, false, 2, (512 << 20), 1234L, 5678L, new FsPermission(((short) (330))), "dingo", "yaks", new Path("hdfs://localhost:4344"));
        DataOutputBuffer dob = new DataOutputBuffer();
        src.write(dob);
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(dob.getData(), 0, dob.getLength());
        CopyListingFileStatus dst = new CopyListingFileStatus();
        dst.readFields(dib);
        Assert.assertEquals(src, dst);
    }

    @Test
    public void testFileStatusEquality() throws Exception {
        FileStatus stat = new FileStatus(4344L, false, 2, (512 << 20), 1234L, 5678L, new FsPermission(((short) (330))), "dingo", "yaks", new Path("hdfs://localhost:4344/foo/bar/baz"));
        CopyListingFileStatus clfs = new CopyListingFileStatus(stat);
        Assert.assertEquals(stat.getLen(), clfs.getLen());
        Assert.assertEquals(stat.isDirectory(), clfs.isDirectory());
        Assert.assertEquals(stat.getReplication(), clfs.getReplication());
        Assert.assertEquals(stat.getBlockSize(), clfs.getBlockSize());
        Assert.assertEquals(stat.getAccessTime(), clfs.getAccessTime());
        Assert.assertEquals(stat.getModificationTime(), clfs.getModificationTime());
        Assert.assertEquals(stat.getPermission(), clfs.getPermission());
        Assert.assertEquals(stat.getOwner(), clfs.getOwner());
        Assert.assertEquals(stat.getGroup(), clfs.getGroup());
        Assert.assertEquals(stat.getPath(), clfs.getPath());
        Assert.assertEquals(stat.isErasureCoded(), clfs.isErasureCoded());
    }
}

