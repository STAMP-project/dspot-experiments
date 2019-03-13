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
package org.apache.flink.hdfstests;


import BlobServerOptions.STORAGE_DIRECTORY;
import ConfigConstants.DEFAULT_CHARSET;
import FileOutputFormat.OutputDirectoryMode.ALWAYS;
import FileSystem.WriteMode.NO_OVERWRITE;
import FileSystem.WriteMode.OVERWRITE;
import HighAvailabilityOptions.HA_MODE;
import HighAvailabilityOptions.HA_STORAGE_PATH;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.ExecutionEnvironmentFactory;
import org.apache.flink.api.java.LocalEnvironment;
import org.apache.flink.api.java.io.TextOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.examples.java.wordcount.WordCount;
import org.apache.flink.runtime.blob.BlobCacheCorruptionTest;
import org.apache.flink.runtime.blob.BlobCacheRecoveryTest;
import org.apache.flink.runtime.blob.BlobServerCorruptionTest;
import org.apache.flink.runtime.blob.BlobServerRecoveryTest;
import org.apache.flink.runtime.blob.BlobStoreService;
import org.apache.flink.runtime.blob.BlobUtils;
import org.apache.flink.runtime.fs.hdfs.HadoopFileSystem;
import org.apache.flink.util.FileUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * This test should logically be located in the 'flink-runtime' tests. However, this project
 * has already all dependencies required (flink-java-examples). Also, the ParallelismOneExecEnv is here.
 */
public class HDFSTest {
    protected String hdfsURI;

    private MiniDFSCluster hdfsCluster;

    private Path hdPath;

    protected FileSystem hdfs;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testHDFS() {
        Path file = new Path(((hdfsURI) + (hdPath)));
        org.apache.hadoop.fs.Path result = new org.apache.hadoop.fs.Path(((hdfsURI) + "/result"));
        try {
            FileSystem fs = file.getFileSystem();
            Assert.assertTrue("Must be HadoopFileSystem", (fs instanceof HadoopFileSystem));
            HDFSTest.DopOneTestEnvironment.setAsContext();
            try {
                WordCount.main(new String[]{ "--input", file.toString(), "--output", result.toString() });
            } catch (Throwable t) {
                t.printStackTrace();
                Assert.fail(("Test failed with " + (t.getMessage())));
            } finally {
                HDFSTest.DopOneTestEnvironment.unsetAsContext();
            }
            Assert.assertTrue("No result file present", hdfs.exists(result));
            // validate output:
            FSDataInputStream inStream = hdfs.open(result);
            StringWriter writer = new StringWriter();
            IOUtils.copy(inStream, writer);
            String resultString = writer.toString();
            Assert.assertEquals(("hdfs 10\n" + "hello 10\n"), resultString);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(("Error in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testChangingFileNames() {
        org.apache.hadoop.fs.Path hdfsPath = new org.apache.hadoop.fs.Path(((hdfsURI) + "/hdfsTest"));
        Path path = new Path(hdfsPath.toString());
        String type = "one";
        TextOutputFormat<String> outputFormat = new TextOutputFormat(path);
        outputFormat.setWriteMode(NO_OVERWRITE);
        outputFormat.setOutputDirectoryMode(ALWAYS);
        try {
            outputFormat.open(0, 2);
            outputFormat.writeRecord(type);
            outputFormat.close();
            outputFormat.open(1, 2);
            outputFormat.writeRecord(type);
            outputFormat.close();
            Assert.assertTrue("No result file present", hdfs.exists(hdfsPath));
            FileStatus[] files = hdfs.listStatus(hdfsPath);
            Assert.assertEquals(2, files.length);
            for (FileStatus file : files) {
                Assert.assertTrue((("1".equals(file.getPath().getName())) || ("2".equals(file.getPath().getName()))));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test that {@link FileUtils#deletePathIfEmpty(FileSystem, Path)} deletes the path if it is
     * empty. A path can only be empty if it is a directory which does not contain any
     * files/directories.
     */
    @Test
    public void testDeletePathIfEmpty() throws IOException {
        final Path basePath = new Path(hdfsURI);
        final Path directory = new Path(basePath, UUID.randomUUID().toString());
        final Path directoryFile = new Path(directory, UUID.randomUUID().toString());
        final Path singleFile = new Path(basePath, UUID.randomUUID().toString());
        FileSystem fs = basePath.getFileSystem();
        fs.mkdirs(directory);
        byte[] data = "HDFSTest#testDeletePathIfEmpty".getBytes(DEFAULT_CHARSET);
        for (Path file : Arrays.asList(singleFile, directoryFile)) {
            FSDataOutputStream outputStream = fs.create(file, OVERWRITE);
            outputStream.write(data);
            outputStream.close();
        }
        // verify that the files have been created
        Assert.assertTrue(fs.exists(singleFile));
        Assert.assertTrue(fs.exists(directoryFile));
        // delete the single file
        Assert.assertFalse(FileUtils.deletePathIfEmpty(fs, singleFile));
        Assert.assertTrue(fs.exists(singleFile));
        // try to delete the non-empty directory
        Assert.assertFalse(FileUtils.deletePathIfEmpty(fs, directory));
        Assert.assertTrue(fs.exists(directory));
        // delete the file contained in the directory
        Assert.assertTrue(fs.delete(directoryFile, false));
        // now the deletion should work
        Assert.assertTrue(FileUtils.deletePathIfEmpty(fs, directory));
        Assert.assertFalse(fs.exists(directory));
    }

    /**
     * Tests that with {@link HighAvailabilityMode#ZOOKEEPER} distributed JARs are recoverable from any
     * participating BlobServer when talking to the {@link org.apache.flink.runtime.blob.BlobServer} directly.
     */
    @Test
    public void testBlobServerRecovery() throws Exception {
        Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, hdfsURI);
        BlobStoreService blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
        try {
            BlobServerRecoveryTest.testBlobServerRecovery(config, blobStoreService);
        } finally {
            blobStoreService.closeAndCleanupAllData();
        }
    }

    /**
     * Tests that with {@link HighAvailabilityMode#ZOOKEEPER} distributed corrupted JARs are
     * recognised during the download via a {@link org.apache.flink.runtime.blob.BlobServer}.
     */
    @Test
    public void testBlobServerCorruptedFile() throws Exception {
        Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, hdfsURI);
        BlobStoreService blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
        try {
            BlobServerCorruptionTest.testGetFailsFromCorruptFile(config, blobStoreService, exception);
        } finally {
            blobStoreService.closeAndCleanupAllData();
        }
    }

    /**
     * Tests that with {@link HighAvailabilityMode#ZOOKEEPER} distributed JARs are recoverable from any
     * participating BlobServer when uploaded via a BLOB cache.
     */
    @Test
    public void testBlobCacheRecovery() throws Exception {
        Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, hdfsURI);
        BlobStoreService blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
        try {
            BlobCacheRecoveryTest.testBlobCacheRecovery(config, blobStoreService);
        } finally {
            blobStoreService.closeAndCleanupAllData();
        }
    }

    /**
     * Tests that with {@link HighAvailabilityMode#ZOOKEEPER} distributed corrupted JARs are
     * recognised during the download via a BLOB cache.
     */
    @Test
    public void testBlobCacheCorruptedFile() throws Exception {
        Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, hdfsURI);
        BlobStoreService blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
        try {
            BlobCacheCorruptionTest.testGetFailsFromCorruptFile(new JobID(), config, blobStoreService, exception);
        } finally {
            blobStoreService.closeAndCleanupAllData();
        }
    }

    abstract static class DopOneTestEnvironment extends ExecutionEnvironment {
        public static void setAsContext() {
            final LocalEnvironment le = new LocalEnvironment();
            le.setParallelism(1);
            initializeContextEnvironment(new ExecutionEnvironmentFactory() {
                @Override
                public ExecutionEnvironment createExecutionEnvironment() {
                    return le;
                }
            });
        }

        public static void unsetAsContext() {
            resetContextEnvironment();
        }
    }
}

