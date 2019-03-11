/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import CompressContent.COMPRESSION_FORMAT;
import CompressContent.COMPRESSION_FORMAT_ATTRIBUTE;
import CompressContent.COMPRESSION_FORMAT_SNAPPY;
import CompressContent.COMPRESSION_FORMAT_SNAPPY_FRAMED;
import CompressContent.MODE;
import CompressContent.MODE_COMPRESS;
import CompressContent.MODE_DECOMPRESS;
import CompressContent.REL_FAILURE;
import CompressContent.REL_SUCCESS;
import CompressContent.UPDATE_FILENAME;
import CoreAttributes.MIME_TYPE;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestCompressContent {
    @Test
    public void testSnappyCompress() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, MODE_COMPRESS);
        runner.setProperty(COMPRESSION_FORMAT, COMPRESSION_FORMAT_SNAPPY);
        runner.setProperty(UPDATE_FILENAME, "true");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(MIME_TYPE.key(), "application/x-snappy");
        flowFile.assertAttributeEquals("filename", "SampleFile.txt.snappy");
    }

    @Test
    public void testSnappyDecompress() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, MODE_DECOMPRESS);
        runner.setProperty(COMPRESSION_FORMAT, COMPRESSION_FORMAT_SNAPPY);
        runner.setProperty(UPDATE_FILENAME, "true");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt.snappy"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile.txt");
    }

    @Test
    public void testSnappyFramedCompress() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, MODE_COMPRESS);
        runner.setProperty(COMPRESSION_FORMAT, COMPRESSION_FORMAT_SNAPPY_FRAMED);
        runner.setProperty(UPDATE_FILENAME, "true");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(MIME_TYPE.key(), "application/x-snappy-framed");
        flowFile.assertAttributeEquals("filename", "SampleFile.txt.sz");
    }

    @Test
    public void testSnappyFramedDecompress() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, MODE_DECOMPRESS);
        runner.setProperty(COMPRESSION_FORMAT, COMPRESSION_FORMAT_SNAPPY_FRAMED);
        runner.setProperty(UPDATE_FILENAME, "true");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt.sz"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile.txt");
    }

    @Test
    public void testBzip2DecompressConcatenated() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, "decompress");
        runner.setProperty(COMPRESSION_FORMAT, "bzip2");
        runner.setProperty(UPDATE_FILENAME, "false");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFileConcat.txt.bz2"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFileConcat.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFileConcat.txt.bz2");// not updating filename

    }

    @Test
    public void testBzip2Decompress() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, "decompress");
        runner.setProperty(COMPRESSION_FORMAT, "bzip2");
        runner.setProperty(UPDATE_FILENAME, "true");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt.bz2"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile.txt");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile1.txt.bz2"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile1.txt");
    }

    @Test
    public void testProperMimeTypeFromBzip2() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, "compress");
        runner.setProperty(COMPRESSION_FORMAT, "bzip2");
        runner.setProperty(UPDATE_FILENAME, "false");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals("mime.type", "application/x-bzip2");
    }

    @Test
    public void testBzip2DecompressWithBothMimeTypes() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, "decompress");
        runner.setProperty(COMPRESSION_FORMAT, COMPRESSION_FORMAT_ATTRIBUTE);
        runner.setProperty(UPDATE_FILENAME, "true");
        // ensure that we can decompress with a mime type of application/x-bzip2
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("mime.type", "application/x-bzip2");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt.bz2"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile.txt");
        // ensure that we can decompress with a mime type of application/bzip2. The appropriate mime type is
        // application/x-bzip2, but we used to use application/bzip2. We want to ensure that we are still
        // backward compatible.
        runner.clearTransferState();
        attributes.put("mime.type", "application/bzip2");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile1.txt.bz2"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile1.txt");
    }

    @Test
    public void testGzipDecompress() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, "decompress");
        runner.setProperty(COMPRESSION_FORMAT, "gzip");
        Assert.assertTrue(runner.setProperty(UPDATE_FILENAME, "true").isValid());
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt.gz"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile.txt");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile1.txt.gz"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile1.txt");
        runner.clearTransferState();
        runner.setProperty(COMPRESSION_FORMAT, COMPRESSION_FORMAT_ATTRIBUTE);
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(MIME_TYPE.key(), "application/x-gzip");
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt.gz"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        flowFile.assertAttributeEquals("filename", "SampleFile.txt");
    }

    @Test
    public void testFilenameUpdatedOnCompress() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, "compress");
        runner.setProperty(COMPRESSION_FORMAT, "gzip");
        Assert.assertTrue(runner.setProperty(UPDATE_FILENAME, "true").isValid());
        runner.enqueue(Paths.get("src/test/resources/CompressedData/SampleFile.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals("filename", "SampleFile.txt.gz");
    }

    @Test
    public void testDecompressFailure() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(CompressContent.class);
        runner.setProperty(MODE, "decompress");
        runner.setProperty(COMPRESSION_FORMAT, "gzip");
        byte[] data = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        runner.enqueue(data);
        Assert.assertTrue(runner.setProperty(UPDATE_FILENAME, "true").isValid());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.getFlowFilesForRelationship(REL_FAILURE).get(0).assertContentEquals(data);
    }
}

