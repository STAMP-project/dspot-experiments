/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.view.utils.hdfs;


import java.util.Arrays;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class HdfsApiTest {
    private FileSystem fs;

    private HdfsApi.HdfsApi hdfsApi;

    private Configuration conf;

    private MiniDFSCluster hdfsCluster;

    @Test
    public void testWith_EC_And_Encryption() {
        // Have to mock DummyFileStatus, because we cannot rely on internal class of hdfs
        DummyFileStatus fileStatus = Mockito.mock(DummyFileStatus.class);
        FsPermission fsPermission = new FsPermission(((short) (511)));
        String ecPolicyName = "Some-EC-Policy";
        ECSchema ecSchema = new ECSchema("someSchema", 1, 1);
        ErasureCodingPolicy erasureCodingPolicy = new ErasureCodingPolicy(ecPolicyName, ecSchema, 1024, ((byte) (0)));
        Mockito.when(getPermission()).thenReturn(fsPermission);
        Mockito.when(getPath()).thenReturn(new Path("/test/path"));
        Mockito.when(fileStatus.getErasureCodingPolicy()).thenReturn(erasureCodingPolicy);
        Mockito.when(isErasureCoded()).thenReturn(true);
        Mockito.when(isEncrypted()).thenReturn(true);
        Map<String, Object> json = hdfsApi.fileStatusToJSON(fileStatus);
        Assert.assertEquals(Boolean.TRUE, json.get(KeyIsErasureCoded));
        Assert.assertEquals(Boolean.TRUE, json.get(KeyIsEncrypted));
        Assert.assertEquals(json.get(KeyErasureCodingPolicyName), ecPolicyName);
    }

    @Test
    public void testWithout_EC_And_Encryption() {
        // Have to mock DummyFileStatus, because we cannot rely on internal class of hdfs
        FsPermission fsPermission = new FsPermission(((short) (511)));
        DummyFileStatus fileStatus = Mockito.mock(DummyFileStatus.class);
        Mockito.when(getPermission()).thenReturn(fsPermission);
        Mockito.when(getPath()).thenReturn(new Path("/test/path"));
        Mockito.when(fileStatus.getErasureCodingPolicy()).thenReturn(null);
        Mockito.when(isErasureCoded()).thenReturn(false);
        Mockito.when(isEncrypted()).thenReturn(false);
        Map<String, Object> json = hdfsApi.fileStatusToJSON(fileStatus);
        Assert.assertEquals(Boolean.FALSE, json.get(KeyIsErasureCoded));
        Assert.assertEquals(Boolean.FALSE, json.get(KeyIsEncrypted));
        Assert.assertNull(json.get(KeyErasureCodingPolicyName));
    }

    @Test
    public void testNonHdfsFileStatus() {
        // Have to mock DummyNonHdfsFileStatus, because we cannot rely on internal class of hdfs
        DummyNonHdfsFileStatus fileStatus = Mockito.mock(DummyNonHdfsFileStatus.class);
        FsPermission fsPermission = new FsPermission(((short) (511)));
        Mockito.when(getPermission()).thenReturn(fsPermission);
        Mockito.when(getPath()).thenReturn(new Path("/test/path"));
        Mockito.when(isErasureCoded()).thenReturn(false);
        Mockito.when(isEncrypted()).thenReturn(false);
        Map<String, Object> json = hdfsApi.fileStatusToJSON(fileStatus);
        Assert.assertEquals(Boolean.FALSE, json.get(KeyIsErasureCoded));
        Assert.assertEquals(Boolean.FALSE, json.get(KeyIsEncrypted));
        Assert.assertNull(json.get(KeyErasureCodingPolicyName));
    }

    @Test
    public void filterAndTruncateDirStatus() throws Exception {
        {
            // null fileStatuses
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus("", 0, null);
            Assert.assertEquals(new DirStatus(null, new DirListInfo(0, false, 0, "")), dirStatus);
        }
        {
            FileStatus[] fileStatuses = getFileStatuses(10);
            DirStatus dirStatus1 = hdfsApi.filterAndTruncateDirStatus("", 0, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[0], new DirListInfo(10, true, 0, "")), dirStatus1);
        }
        {
            int originalSize = 10;
            int maxAllowedSize = 5;
            String nameFilter = "";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus2 = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(Arrays.copyOf(fileStatuses, maxAllowedSize), new DirListInfo(originalSize, true, maxAllowedSize, nameFilter)), dirStatus2);
        }
        {
            int originalSize = 10;
            int maxAllowedSize = 10;
            String nameFilter = "";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus2 = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(Arrays.copyOf(fileStatuses, maxAllowedSize), new DirListInfo(originalSize, false, maxAllowedSize, nameFilter)), dirStatus2);
        }
        {
            int originalSize = 11;
            int maxAllowedSize = 2;
            String nameFilter = "1";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[]{ fileStatuses[1], fileStatuses[10] }, new DirListInfo(originalSize, false, 2, nameFilter)), dirStatus);
        }
        {
            int originalSize = 20;
            int maxAllowedSize = 3;
            String nameFilter = "1";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[]{ fileStatuses[1], fileStatuses[10], fileStatuses[11] }, new DirListInfo(originalSize, true, 3, nameFilter)), dirStatus);
        }
        {
            int originalSize = 12;
            int maxAllowedSize = 3;
            String nameFilter = "1";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[]{ fileStatuses[1], fileStatuses[10], fileStatuses[11] }, new DirListInfo(originalSize, false, 3, nameFilter)), dirStatus);
        }
        {
            int originalSize = 13;
            int maxAllowedSize = 3;
            String nameFilter = "1";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[]{ fileStatuses[1], fileStatuses[10], fileStatuses[11] }, new DirListInfo(originalSize, true, 3, nameFilter)), dirStatus);
        }
        {
            int originalSize = 0;
            int maxAllowedSize = 3;
            String nameFilter = "1";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[0], new DirListInfo(originalSize, false, originalSize, nameFilter)), dirStatus);
        }
        {
            int originalSize = 20;
            int maxAllowedSize = 3;
            String nameFilter = "";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[]{ fileStatuses[0], fileStatuses[1], fileStatuses[2] }, new DirListInfo(originalSize, true, maxAllowedSize, nameFilter)), dirStatus);
        }
        {
            int originalSize = 20;
            int maxAllowedSize = 3;
            String nameFilter = null;
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[]{ fileStatuses[0], fileStatuses[1], fileStatuses[2] }, new DirListInfo(originalSize, true, maxAllowedSize, nameFilter)), dirStatus);
        }
        {
            int originalSize = 3;
            int maxAllowedSize = 3;
            String nameFilter = null;
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[]{ fileStatuses[0], fileStatuses[1], fileStatuses[2] }, new DirListInfo(originalSize, false, maxAllowedSize, nameFilter)), dirStatus);
        }
        {
            int originalSize = 20;
            int maxAllowedSize = 3;
            String nameFilter = "a";
            FileStatus[] fileStatuses = getFileStatuses(originalSize);
            DirStatus dirStatus = hdfsApi.filterAndTruncateDirStatus(nameFilter, maxAllowedSize, fileStatuses);
            Assert.assertEquals(new DirStatus(new FileStatus[0], new DirListInfo(originalSize, false, 0, nameFilter)), dirStatus);
        }
    }
}

