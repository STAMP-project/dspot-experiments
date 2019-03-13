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
package org.apache.nifi.processors.aws.kinesis.stream;


import PutKinesisStream.AWS_KINESIS_ERROR_MESSAGE;
import PutKinesisStream.AWS_KINESIS_SEQUENCE_NUMBER;
import PutKinesisStream.AWS_KINESIS_SHARD_ID;
import PutKinesisStream.BATCH_SIZE;
import PutKinesisStream.CREDENTIALS_FILE;
import PutKinesisStream.KINESIS_PARTITION_KEY;
import PutKinesisStream.KINESIS_STREAM_NAME;
import PutKinesisStream.MAX_MESSAGE_BUFFER_SIZE_MB;
import PutKinesisStream.REL_FAILURE;
import PutKinesisStream.REL_SUCCESS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test contains both unit and integration test (integration tests are ignored by default).
 * Running integration tests may result in failures due to provisioned capacity of Kinesis stream based on number of shards.
 * The following integration tests run successfully with 10 shards.  If increasing shards is not a possiblity, please reduce the size and
 * number of messages in the integration tests based AWS Kinesis provisioning pages calculations.
 */
public class ITPutKinesisStream {
    private TestRunner runner;

    protected static final String CREDENTIALS_FILE = (System.getProperty("user.home")) + "/aws-credentials.properties";

    /**
     * Comment out ignore for integration tests (requires creds files)
     */
    @Test
    public void testIntegrationSuccess() throws Exception {
        runner.setProperty(PutKinesisStream.CREDENTIALS_FILE, ITPutKinesisStream.CREDENTIALS_FILE);
        runner.assertValid();
        runner.enqueue("test".getBytes());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> ffs = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final MockFlowFile out = ffs.iterator().next();
        out.assertContentEquals("test".getBytes());
    }

    @Test
    public void testIntegrationWithFixedPartitionSuccess() throws Exception {
        runner.setProperty(KINESIS_PARTITION_KEY, "pfixed");
        runner.assertValid();
        runner.enqueue("test".getBytes());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> ffs = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final MockFlowFile out = ffs.iterator().next();
        out.assertContentEquals("test".getBytes());
    }

    @Test
    public void testIntegrationWithDynamicPartitionSuccess() throws Exception {
        runner.setProperty(KINESIS_PARTITION_KEY, "${parition}");
        runner.assertValid();
        Map<String, String> properties = new HashMap<>();
        properties.put("partition", "px");
        runner.enqueue("test".getBytes(), properties);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> ffs = runner.getFlowFilesForRelationship(REL_SUCCESS);
        final MockFlowFile out = ffs.iterator().next();
        out.assertContentEquals("test".getBytes());
    }

    /**
     * Comment out ignore for integration tests (requires creds files)
     */
    @Test
    public void testIntegrationFailedBadStreamName() throws Exception {
        runner.setProperty(KINESIS_STREAM_NAME, "bad-kstream");
        runner.assertValid();
        runner.enqueue("test".getBytes());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testOneMessageWithMaxBufferSizeGreaterThan1MBOneSuccess() {
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[PutKinesisStream.MAX_MESSAGE_SIZE];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
    }

    @Test
    public void testOneMessageWithMaxBufferSizeGreaterThan1MBOneWithParameterPartitionSuccess() {
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.setProperty(KINESIS_PARTITION_KEY, "${partitionKey}");
        runner.assertValid();
        byte[] bytes = new byte[PutKinesisStream.MAX_MESSAGE_SIZE];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        Map<String, String> props = new HashMap<>();
        props.put("partitionKey", "p1");
        runner.enqueue(bytes, props);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
    }

    @Test
    public void testTwoMessageBatch5WithMaxBufferSize1MBRunOnceTwoMessageSent() {
        runner.setProperty(BATCH_SIZE, "5");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "2 MB");
        runner.assertValid();
        byte[] bytes = new byte[PutKinesisStream.MAX_MESSAGE_SIZE];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
    }

    @Test
    public void testThreeMessageWithBatch10MaxBufferSize1MBTRunOnceTwoMessageSent() {
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[PutKinesisStream.MAX_MESSAGE_SIZE];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.enqueue(bytes.clone());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
    }

    @Test
    public void testTwoMessageWithBatch10MaxBufferSize1MBTRunOnceTwoMessageSent() {
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[PutKinesisStream.MAX_MESSAGE_SIZE];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
    }

    @Test
    public void testThreeMessageWithBatch2MaxBufferSize1MBRunTwiceThreeMessageSent() {
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[PutKinesisStream.MAX_MESSAGE_SIZE];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.enqueue(bytes.clone());
        runner.run(2, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(3, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
    }

    @Test
    public void testThreeMessageHello2MBThereWithBatch10MaxBufferSize1MBRunOnceTwoMessageSuccessOneFailed() {
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[(PutKinesisStream.MAX_MESSAGE_SIZE) * 2];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue("hello".getBytes());
        runner.enqueue(bytes);
        runner.enqueue("there".getBytes());
        runner.run(1, true, true);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
        List<MockFlowFile> flowFilesFailed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, flowFilesFailed.size());
        for (MockFlowFile flowFileFailed : flowFilesFailed) {
            Assert.assertNotNull(flowFileFailed.getAttribute(AWS_KINESIS_ERROR_MESSAGE));
        }
    }

    @Test
    public void testTwoMessageHello2MBWithBatch10MaxBufferSize1MBRunOnceOneSuccessOneFailed() throws Exception {
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[(PutKinesisStream.MAX_MESSAGE_SIZE) * 2];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue("hello".getBytes());
        runner.enqueue(bytes);
        runner.run(1, true, true);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
            flowFile.assertContentEquals("hello".getBytes());
        }
        List<MockFlowFile> flowFilesFailed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, flowFilesFailed.size());
        for (MockFlowFile flowFileFailed : flowFilesFailed) {
            Assert.assertNotNull(flowFileFailed.getAttribute(AWS_KINESIS_ERROR_MESSAGE));
        }
    }

    @Test
    public void testTwoMessage2MBHelloWorldWithBatch10MaxBufferSize1MBRunOnceTwoMessageSent() throws Exception {
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[(PutKinesisStream.MAX_MESSAGE_SIZE) * 2];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue("HelloWorld".getBytes());
        runner.run(1, true, true);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
            flowFile.assertContentEquals("HelloWorld".getBytes());
        }
        List<MockFlowFile> flowFilesFailed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(1, flowFilesFailed.size());
        for (MockFlowFile flowFileFailed : flowFilesFailed) {
            Assert.assertNotNull(flowFileFailed.getAttribute(AWS_KINESIS_ERROR_MESSAGE));
        }
    }

    @Test
    public void testTwoMessageHelloWorldWithBatch10MaxBufferSize1MBRunOnceTwoMessageSent() throws Exception {
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        runner.enqueue("Hello".getBytes());
        runner.enqueue("World".getBytes());
        runner.run(1, true, true);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
        flowFiles.get(0).assertContentEquals("Hello".getBytes());
        flowFiles.get(1).assertContentEquals("World".getBytes());
        List<MockFlowFile> flowFilesFailed = runner.getFlowFilesForRelationship(REL_FAILURE);
        Assert.assertEquals(0, flowFilesFailed.size());
    }

    @Test
    public void testThreeMessageWithBatch5MaxBufferSize1MBRunOnceTwoMessageSent() {
        runner.setProperty(BATCH_SIZE, "5");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[PutKinesisStream.MAX_MESSAGE_SIZE];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.enqueue(bytes.clone());
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
    }

    @Test
    public void test5MessageWithBatch10MaxBufferSize10MBRunOnce5MessageSent() {
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[10];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.enqueue(bytes.clone());
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 5);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(5, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
    }

    @Test
    public void test5MessageWithBatch2MaxBufferSize10MBRunOnce2MessageSent() {
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(MAX_MESSAGE_BUFFER_SIZE_MB, "1 MB");
        runner.assertValid();
        byte[] bytes = new byte[10];
        for (int i = 0; i < (bytes.length); i++) {
            bytes[i] = 'a';
        }
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.enqueue(bytes.clone());
        runner.enqueue(bytes);
        runner.enqueue(bytes.clone());
        runner.run(1, true, true);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            flowFile.assertAttributeExists(AWS_KINESIS_SEQUENCE_NUMBER);
            flowFile.assertAttributeExists(AWS_KINESIS_SHARD_ID);
        }
    }
}

