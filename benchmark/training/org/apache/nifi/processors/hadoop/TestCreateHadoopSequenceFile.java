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
package org.apache.nifi.processors.hadoop;


import AbstractHadoopProcessor.COMPRESSION_CODEC;
import CompressionType.BZIP;
import CompressionType.DEFAULT;
import CompressionType.NONE;
import CoreAttributes.MIME_TYPE;
import CreateHadoopSequenceFile.COMPRESSION_TYPE;
import CreateHadoopSequenceFile.RELATIONSHIP_FAILURE;
import CreateHadoopSequenceFile.RELATIONSHIP_SUCCESS;
import SequenceFile.CompressionType.BLOCK;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.BZip2Codec;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.NiFiProperties;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;

import static CreateHadoopSequenceFile.COMPRESSION_TYPE;


public class TestCreateHadoopSequenceFile {
    private TestRunner controller;

    private final File testdata = new File("src/test/resources/testdata");

    private final File[] inFiles = new File[]{ new File(testdata, "randombytes-1"), new File(testdata, "randombytes-2"), new File(testdata, "randombytes-3") };

    private NiFiProperties mockNiFiProperties;

    private KerberosProperties kerberosProperties;

    @Test
    public void validateAllowableValuesForCompressionType() {
        PropertyDescriptor pd = COMPRESSION_TYPE;
        List<AllowableValue> allowableValues = pd.getAllowableValues();
        Assert.assertEquals("NONE", allowableValues.get(0).getValue());
        Assert.assertEquals("RECORD", allowableValues.get(1).getValue());
        Assert.assertEquals("BLOCK", allowableValues.get(2).getValue());
    }

    @Test
    public void testSimpleCase() throws IOException {
        for (File inFile : inFiles) {
            try (FileInputStream fin = new FileInputStream(inFile)) {
                controller.enqueue(fin);
            }
        }
        controller.run(3);
        List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
        List<MockFlowFile> failedFlowFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_FAILURE);
        Assert.assertEquals(0, failedFlowFiles.size());
        Assert.assertEquals(3, successSeqFiles.size());
    }

    @Test
    public void testSequenceFileSaysValueIsBytesWritable() throws IOException, UnsupportedEncodingException {
        for (File inFile : inFiles) {
            try (FileInputStream fin = new FileInputStream(inFile)) {
                controller.enqueue(fin);
            }
        }
        controller.run(3);
        List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
        List<MockFlowFile> failedFlowFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_FAILURE);
        Assert.assertEquals(0, failedFlowFiles.size());
        Assert.assertEquals(3, successSeqFiles.size());
        final byte[] data = successSeqFiles.iterator().next().toByteArray();
        final String magicHeader = new String(data, 0, 3, "UTF-8");
        Assert.assertEquals("SEQ", magicHeader);
        // Format of header is SEQ followed by the version (1 byte).
        // Then, the length of the Key type (1 byte), then the Key type
        // Then, the length of the Value type(1 byte), then the Value type
        final String keyType = Text.class.getCanonicalName();
        final int valueTypeStart = (((3 + 1) + 1) + (keyType.length())) + 1;
        final int valueTypeLength = data[(5 + (keyType.length()))];
        final String valueType = BytesWritable.class.getCanonicalName();
        Assert.assertEquals(valueType.length(), valueTypeLength);
        Assert.assertEquals(valueType, new String(data, valueTypeStart, valueType.length(), "UTF-8"));
        // FileOutputStream fos = new FileOutputStream("test.sf");
        // fos.write(data);
        // fos.flush();
        // fos.close();
    }

    @Test
    public void testMergedTarData() throws IOException {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/tar");
        try (final FileInputStream fin = new FileInputStream("src/test/resources/testdata/13545312236534130.tar")) {
            controller.enqueue(fin, attributes);
            controller.run();
            List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
            Assert.assertEquals(1, successSeqFiles.size());
            final byte[] data = successSeqFiles.iterator().next().toByteArray();
            // Data should be greater than 1000000 because that's the size of 2 of our input files,
            // and the file size should contain all of that plus headers, but the headers should only
            // be a couple hundred bytes.
            Assert.assertTrue(((data.length) > 1000000));
            Assert.assertTrue(((data.length) < 1501000));
        }
    }

    @Test
    public void testMergedZipData() throws IOException {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/zip");
        try (FileInputStream fin = new FileInputStream("src/test/resources/testdata/13545423550275052.zip")) {
            controller.enqueue(fin, attributes);
            controller.run();
            List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
            Assert.assertEquals(1, successSeqFiles.size());
            final byte[] data = successSeqFiles.iterator().next().toByteArray();
            // Data should be greater than 1000000 because that's the size of 2 of our input files,
            // and the file size should contain all of that plus headers, but the headers should only
            // be a couple hundred bytes.
            Assert.assertTrue(((data.length) > 1000000));
            Assert.assertTrue(((data.length) < 1501000));
        }
        // FileOutputStream fos = new FileOutputStream("zip-3-randoms.sf");
        // fos.write(data);
        // fos.flush();
        // fos.close();
    }

    @Test
    public void testMergedFlowfilePackagedData() throws IOException {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/flowfile-v3");
        try (final FileInputStream fin = new FileInputStream("src/test/resources/testdata/13545479542069498.pkg")) {
            controller.enqueue(fin, attributes);
            controller.run();
            List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
            Assert.assertEquals(1, successSeqFiles.size());
            final byte[] data = successSeqFiles.iterator().next().toByteArray();
            // Data should be greater than 1000000 because that's the size of 2 of our input files,
            // and the file size should contain all of that plus headers, but the headers should only
            // be a couple hundred bytes.
            Assert.assertTrue(((data.length) > 1000000));
            Assert.assertTrue(((data.length) < 1501000));
        }
        // FileOutputStream fos = new FileOutputStream("flowfilePkg-3-randoms.sf");
        // fos.write(data);
        // fos.flush();
        // fos.close();
    }

    @Test
    public void testSequenceFileBzipCompressionCodec() throws IOException, UnsupportedEncodingException {
        controller.setProperty(COMPRESSION_CODEC, BZIP.name());
        controller.setProperty(COMPRESSION_TYPE, BLOCK.name());
        File inFile = inFiles[0];
        try (FileInputStream fin = new FileInputStream(inFile)) {
            controller.enqueue(fin);
        }
        controller.run();
        List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
        List<MockFlowFile> failedFlowFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_FAILURE);
        Assert.assertEquals(0, failedFlowFiles.size());
        Assert.assertEquals(1, successSeqFiles.size());
        MockFlowFile ff = successSeqFiles.iterator().next();
        byte[] data = ff.toByteArray();
        final String magicHeader = new String(data, 0, 3, "UTF-8");
        Assert.assertEquals("SEQ", magicHeader);
        // Format of header is SEQ followed by the version (1 byte).
        // Then, the length of the Key type (1 byte), then the Key type
        // Then, the length of the Value type(1 byte), then the Value type
        final String keyType = Text.class.getCanonicalName();
        final int valueTypeStart = (((3 + 1) + 1) + (keyType.length())) + 1;
        final int valueTypeLength = data[(5 + (keyType.length()))];
        final String valueType = BytesWritable.class.getCanonicalName();
        Assert.assertEquals(valueType.length(), valueTypeLength);
        Assert.assertEquals(valueType, new String(data, valueTypeStart, valueType.length(), "UTF-8"));
        final int compressionIndex = ((((3 + 1) + 1) + (keyType.length())) + 1) + (valueType.length());
        final int blockCompressionIndex = compressionIndex + 1;
        Assert.assertEquals(1, data[compressionIndex]);
        Assert.assertEquals(1, data[blockCompressionIndex]);
        final int codecTypeSize = data[(blockCompressionIndex + 1)];
        final int codecTypeStartIndex = blockCompressionIndex + 2;
        Assert.assertEquals(BZip2Codec.class.getCanonicalName(), new String(data, codecTypeStartIndex, codecTypeSize, "UTF-8"));
    }

    @Test
    public void testSequenceFileDefaultCompressionCodec() throws IOException, UnsupportedEncodingException {
        controller.setProperty(COMPRESSION_CODEC, DEFAULT.name());
        controller.setProperty(COMPRESSION_TYPE, BLOCK.name());
        File inFile = inFiles[0];
        try (FileInputStream fin = new FileInputStream(inFile)) {
            controller.enqueue(fin);
        }
        controller.run();
        List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
        List<MockFlowFile> failedFlowFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_FAILURE);
        Assert.assertEquals(0, failedFlowFiles.size());
        Assert.assertEquals(1, successSeqFiles.size());
        MockFlowFile ff = successSeqFiles.iterator().next();
        byte[] data = ff.toByteArray();
        final String magicHeader = new String(data, 0, 3, "UTF-8");
        Assert.assertEquals("SEQ", magicHeader);
        // Format of header is SEQ followed by the version (1 byte).
        // Then, the length of the Key type (1 byte), then the Key type
        // Then, the length of the Value type(1 byte), then the Value type
        final String keyType = Text.class.getCanonicalName();
        final int valueTypeStart = (((3 + 1) + 1) + (keyType.length())) + 1;
        final int valueTypeLength = data[(5 + (keyType.length()))];
        final String valueType = BytesWritable.class.getCanonicalName();
        Assert.assertEquals(valueType.length(), valueTypeLength);
        Assert.assertEquals(valueType, new String(data, valueTypeStart, valueType.length(), "UTF-8"));
        final int compressionIndex = ((((3 + 1) + 1) + (keyType.length())) + 1) + (valueType.length());
        final int blockCompressionIndex = compressionIndex + 1;
        Assert.assertEquals(1, data[compressionIndex]);
        Assert.assertEquals(1, data[blockCompressionIndex]);
        final int codecTypeSize = data[(blockCompressionIndex + 1)];
        final int codecTypeStartIndex = blockCompressionIndex + 2;
        Assert.assertEquals(DefaultCodec.class.getCanonicalName(), new String(data, codecTypeStartIndex, codecTypeSize, "UTF-8"));
    }

    @Test
    public void testSequenceFileNoneCompressionCodec() throws IOException, UnsupportedEncodingException {
        controller.setProperty(COMPRESSION_CODEC, NONE.name());
        controller.setProperty(COMPRESSION_TYPE, BLOCK.name());
        File inFile = inFiles[0];
        try (FileInputStream fin = new FileInputStream(inFile)) {
            controller.enqueue(fin);
        }
        controller.run();
        List<MockFlowFile> successSeqFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_SUCCESS);
        List<MockFlowFile> failedFlowFiles = controller.getFlowFilesForRelationship(RELATIONSHIP_FAILURE);
        Assert.assertEquals(0, failedFlowFiles.size());
        Assert.assertEquals(1, successSeqFiles.size());
        MockFlowFile ff = successSeqFiles.iterator().next();
        byte[] data = ff.toByteArray();
        final String magicHeader = new String(data, 0, 3, "UTF-8");
        Assert.assertEquals("SEQ", magicHeader);
        // Format of header is SEQ followed by the version (1 byte).
        // Then, the length of the Key type (1 byte), then the Key type
        // Then, the length of the Value type(1 byte), then the Value type
        final String keyType = Text.class.getCanonicalName();
        final int valueTypeStart = (((3 + 1) + 1) + (keyType.length())) + 1;
        final int valueTypeLength = data[(5 + (keyType.length()))];
        final String valueType = BytesWritable.class.getCanonicalName();
        Assert.assertEquals(valueType.length(), valueTypeLength);
        Assert.assertEquals(valueType, new String(data, valueTypeStart, valueType.length(), "UTF-8"));
        final int compressionIndex = ((((3 + 1) + 1) + (keyType.length())) + 1) + (valueType.length());
        final int blockCompressionIndex = compressionIndex + 1;
        Assert.assertEquals(1, data[compressionIndex]);
        Assert.assertEquals(1, data[blockCompressionIndex]);
        final int codecTypeSize = data[(blockCompressionIndex + 1)];
        final int codecTypeStartIndex = blockCompressionIndex + 2;
        Assert.assertEquals(DefaultCodec.class.getCanonicalName(), new String(data, codecTypeStartIndex, codecTypeSize, "UTF-8"));
    }

    private static class TestableCreateHadoopSequenceFile extends CreateHadoopSequenceFile {
        private KerberosProperties testKerbersProperties;

        public TestableCreateHadoopSequenceFile(KerberosProperties testKerbersProperties) {
            this.testKerbersProperties = testKerbersProperties;
        }

        @Override
        protected KerberosProperties getKerberosProperties(File kerberosConfigFile) {
            return testKerbersProperties;
        }
    }
}

