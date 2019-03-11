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
package org.apache.hadoop.fs.s3a;


import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Date;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * S3A tests for getFileStatus using mock S3 client.
 */
public class TestS3AGetFileStatus extends AbstractS3AMockTest {
    @Test
    public void testFile() throws Exception {
        Path path = new Path("/file");
        String key = path.toUri().getPath().substring(1);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(1L);
        meta.setLastModified(new Date(2L));
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, key)))).thenReturn(meta);
        FileStatus stat = fs.getFileStatus(path);
        Assert.assertNotNull(stat);
        Assert.assertEquals(fs.makeQualified(path), stat.getPath());
        Assert.assertTrue(stat.isFile());
        Assert.assertEquals(meta.getContentLength(), stat.getLen());
        Assert.assertEquals(meta.getLastModified().getTime(), stat.getModificationTime());
        ContractTestUtils.assertNotErasureCoded(fs, path);
        Assert.assertTrue((((path + " should have erasure coding unset in ") + "FileStatus#toString(): ") + stat), stat.toString().contains("isErasureCoded=false"));
    }

    @Test
    public void testFakeDirectory() throws Exception {
        Path path = new Path("/dir");
        String key = path.toUri().getPath().substring(1);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, key)))).thenThrow(AbstractS3AMockTest.NOT_FOUND);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(0L);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, (key + "/"))))).thenReturn(meta);
        FileStatus stat = fs.getFileStatus(path);
        Assert.assertNotNull(stat);
        Assert.assertEquals(fs.makeQualified(path), stat.getPath());
        Assert.assertTrue(stat.isDirectory());
    }

    @Test
    public void testImplicitDirectory() throws Exception {
        Path path = new Path("/dir");
        String key = path.toUri().getPath().substring(1);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, key)))).thenThrow(AbstractS3AMockTest.NOT_FOUND);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, (key + "/"))))).thenThrow(AbstractS3AMockTest.NOT_FOUND);
        setupListMocks(Collections.singletonList("dir/"), Collections.emptyList());
        FileStatus stat = fs.getFileStatus(path);
        Assert.assertNotNull(stat);
        Assert.assertEquals(fs.makeQualified(path), stat.getPath());
        Assert.assertTrue(stat.isDirectory());
        ContractTestUtils.assertNotErasureCoded(fs, path);
        Assert.assertTrue((((path + " should have erasure coding unset in ") + "FileStatus#toString(): ") + stat), stat.toString().contains("isErasureCoded=false"));
    }

    @Test
    public void testRoot() throws Exception {
        Path path = new Path("/");
        String key = path.toUri().getPath().substring(1);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, key)))).thenThrow(AbstractS3AMockTest.NOT_FOUND);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, (key + "/"))))).thenThrow(AbstractS3AMockTest.NOT_FOUND);
        setupListMocks(Collections.emptyList(), Collections.emptyList());
        FileStatus stat = fs.getFileStatus(path);
        Assert.assertNotNull(stat);
        Assert.assertEquals(fs.makeQualified(path), stat.getPath());
        Assert.assertTrue(stat.isDirectory());
        Assert.assertTrue(stat.getPath().isRoot());
    }

    @Test
    public void testNotFound() throws Exception {
        Path path = new Path("/dir");
        String key = path.toUri().getPath().substring(1);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, key)))).thenThrow(AbstractS3AMockTest.NOT_FOUND);
        Mockito.when(s3.getObjectMetadata(ArgumentMatchers.argThat(correctGetMetadataRequest(AbstractS3AMockTest.BUCKET, (key + "/"))))).thenThrow(AbstractS3AMockTest.NOT_FOUND);
        setupListMocks(Collections.emptyList(), Collections.emptyList());
        exception.expect(FileNotFoundException.class);
        fs.getFileStatus(path);
    }
}

