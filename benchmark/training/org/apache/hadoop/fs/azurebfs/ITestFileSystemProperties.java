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
package org.apache.hadoop.fs.azurebfs;


import java.util.Hashtable;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test FileSystemProperties.
 */
public class ITestFileSystemProperties extends AbstractAbfsIntegrationTest {
    private static final int TEST_DATA = 100;

    private static final Path TEST_PATH = new Path("/testfile");

    public ITestFileSystemProperties() throws Exception {
    }

    @Test
    public void testReadWriteBytesToFileAndEnsureThreadPoolCleanup() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        testWriteOneByteToFileAndEnsureThreadPoolCleanup();
        try (FSDataInputStream inputStream = fs.open(ITestFileSystemProperties.TEST_PATH, ((4 * 1024) * 1024))) {
            int i = inputStream.read();
            Assert.assertEquals(ITestFileSystemProperties.TEST_DATA, i);
        }
    }

    @Test
    public void testWriteOneByteToFileAndEnsureThreadPoolCleanup() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        try (FSDataOutputStream stream = fs.create(ITestFileSystemProperties.TEST_PATH)) {
            stream.write(ITestFileSystemProperties.TEST_DATA);
        }
        FileStatus fileStatus = fs.getFileStatus(ITestFileSystemProperties.TEST_PATH);
        Assert.assertEquals(1, fileStatus.getLen());
    }

    @Test
    public void testBase64FileSystemProperties() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put("key", "{ value: value }");
        fs.getAbfsStore().setFilesystemProperties(properties);
        Hashtable<String, String> fetchedProperties = fs.getAbfsStore().getFilesystemProperties();
        Assert.assertEquals(properties, fetchedProperties);
    }

    @Test
    public void testBase64PathProperties() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put("key", "{ value: valueTest }");
        touch(ITestFileSystemProperties.TEST_PATH);
        fs.getAbfsStore().setPathProperties(ITestFileSystemProperties.TEST_PATH, properties);
        Hashtable<String, String> fetchedProperties = fs.getAbfsStore().getPathStatus(ITestFileSystemProperties.TEST_PATH);
        Assert.assertEquals(properties, fetchedProperties);
    }

    @Test(expected = Exception.class)
    public void testBase64InvalidFileSystemProperties() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put("key", "{ value: value? }");
        fs.getAbfsStore().setFilesystemProperties(properties);
        Hashtable<String, String> fetchedProperties = fs.getAbfsStore().getFilesystemProperties();
        Assert.assertEquals(properties, fetchedProperties);
    }

    @Test(expected = Exception.class)
    public void testBase64InvalidPathProperties() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put("key", "{ value: valueTest? }");
        touch(ITestFileSystemProperties.TEST_PATH);
        fs.getAbfsStore().setPathProperties(ITestFileSystemProperties.TEST_PATH, properties);
        Hashtable<String, String> fetchedProperties = fs.getAbfsStore().getPathStatus(ITestFileSystemProperties.TEST_PATH);
        Assert.assertEquals(properties, fetchedProperties);
    }

    @Test
    public void testSetFileSystemProperties() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put("containerForDevTest", "true");
        fs.getAbfsStore().setFilesystemProperties(properties);
        Hashtable<String, String> fetchedProperties = fs.getAbfsStore().getFilesystemProperties();
        Assert.assertEquals(properties, fetchedProperties);
    }
}

