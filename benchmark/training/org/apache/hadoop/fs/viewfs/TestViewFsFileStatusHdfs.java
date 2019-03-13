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
/**
 * The FileStatus is being serialized in MR as jobs are submitted.
 * Since viewfs has overlayed ViewFsFileStatus, we ran into
 * serialization problems. THis test is test the fix.
 */
package org.apache.hadoop.fs.viewfs;


import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.junit.Assert;
import org.junit.Test;


public class TestViewFsFileStatusHdfs {
    static final String testfilename = "/tmp/testFileStatusSerialziation";

    static final String someFile = "/hdfstmp/someFileForTestGetFileChecksum";

    private static final FileSystemTestHelper fileSystemTestHelper = new FileSystemTestHelper();

    private static MiniDFSCluster cluster;

    private static Path defaultWorkingDirectory;

    private static final Configuration CONF = new Configuration();

    private static FileSystem fHdfs;

    private static FileSystem vfs;

    @Test
    public void testFileStatusSerialziation() throws IOException, URISyntaxException {
        long len = TestViewFsFileStatusHdfs.fileSystemTestHelper.createFile(TestViewFsFileStatusHdfs.fHdfs, TestViewFsFileStatusHdfs.testfilename);
        FileStatus stat = TestViewFsFileStatusHdfs.vfs.getFileStatus(new Path(TestViewFsFileStatusHdfs.testfilename));
        Assert.assertEquals(len, stat.getLen());
        // check serialization/deserialization
        DataOutputBuffer dob = new DataOutputBuffer();
        stat.write(dob);
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(dob.getData(), 0, dob.getLength());
        FileStatus deSer = new FileStatus();
        deSer.readFields(dib);
        Assert.assertEquals(len, deSer.getLen());
    }

    @Test
    public void testGetFileChecksum() throws IOException, URISyntaxException {
        // Create two different files in HDFS
        TestViewFsFileStatusHdfs.fileSystemTestHelper.createFile(TestViewFsFileStatusHdfs.fHdfs, TestViewFsFileStatusHdfs.someFile);
        TestViewFsFileStatusHdfs.fileSystemTestHelper.createFile(TestViewFsFileStatusHdfs.fHdfs, TestViewFsFileStatusHdfs.fileSystemTestHelper.getTestRootPath(TestViewFsFileStatusHdfs.fHdfs, ((TestViewFsFileStatusHdfs.someFile) + "other")), 1, 512);
        // Get checksum through ViewFS
        FileChecksum viewFSCheckSum = TestViewFsFileStatusHdfs.vfs.getFileChecksum(new Path("/vfstmp/someFileForTestGetFileChecksum"));
        // Get checksum through HDFS.
        FileChecksum hdfsCheckSum = TestViewFsFileStatusHdfs.fHdfs.getFileChecksum(new Path(TestViewFsFileStatusHdfs.someFile));
        // Get checksum of different file in HDFS
        FileChecksum otherHdfsFileCheckSum = TestViewFsFileStatusHdfs.fHdfs.getFileChecksum(new Path(((TestViewFsFileStatusHdfs.someFile) + "other")));
        // Checksums of the same file (got through HDFS and ViewFS should be same)
        Assert.assertEquals("HDFS and ViewFS checksums were not the same", viewFSCheckSum, hdfsCheckSum);
        // Checksum of different files should be different.
        Assert.assertFalse(("Some other HDFS file which should not have had the same " + "checksum as viewFS did!"), viewFSCheckSum.equals(otherHdfsFileCheckSum));
    }
}

