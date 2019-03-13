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
package org.apache.nifi.processors.aws.dynamodb;


import AbstractDynamoDBProcessor.ACCESS_KEY;
import AbstractDynamoDBProcessor.BATCH_SIZE;
import AbstractDynamoDBProcessor.DYNAMODB_ERROR_EXCEPTION_MESSAGE;
import AbstractDynamoDBProcessor.DYNAMODB_HASH_KEY_VALUE_ERROR;
import AbstractDynamoDBProcessor.DYNAMODB_RANGE_KEY_VALUE_ERROR;
import AbstractDynamoDBProcessor.HASH_KEY_NAME;
import AbstractDynamoDBProcessor.HASH_KEY_VALUE;
import AbstractDynamoDBProcessor.JSON_DOCUMENT;
import AbstractDynamoDBProcessor.RANGE_KEY_NAME;
import AbstractDynamoDBProcessor.RANGE_KEY_VALUE;
import AbstractDynamoDBProcessor.REGION;
import AbstractDynamoDBProcessor.REL_FAILURE;
import AbstractDynamoDBProcessor.REL_UNPROCESSED;
import AbstractDynamoDBProcessor.SECRET_KEY;
import AbstractDynamoDBProcessor.TABLE;
import AbstractWriteDynamoDBProcessor.REL_SUCCESS;
import PutDynamoDB.AWS_DYNAMO_DB_ITEM_SIZE_ERROR;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static PutDynamoDB.DYNAMODB_MAX_ITEM_SIZE;


public class PutDynamoDBTest extends AbstractDynamoDBTest {
    protected PutDynamoDB putDynamoDB;

    protected BatchWriteItemResult result = new BatchWriteItemResult();

    BatchWriteItemOutcome outcome;

    @Test
    public void testStringHashStringRangePutOnlyHashFailure() {
        // Inject a mock DynamoDB to create the exception condition
        final DynamoDB mockDynamoDb = Mockito.mock(DynamoDB.class);
        // When writing, mock thrown service exception from AWS
        Mockito.when(mockDynamoDb.batchWriteItem(Matchers.<TableWriteItems>anyVararg())).thenThrow(getSampleAwsServiceException());
        putDynamoDB = new PutDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDb;
            }
        };
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(JSON_DOCUMENT, "document");
        String document = "{\"hello\": 2}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            ITAbstractDynamoDBTest.validateServiceExceptionAttribute(flowFile);
        }
    }

    @Test
    public void testStringHashStringRangePutNoHashValueFailure() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(JSON_DOCUMENT, "document");
        String document = "{\"hello\": 2}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_HASH_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangePutOnlyHashWithRangeValueNoRangeNameFailure() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(JSON_DOCUMENT, "document");
        putRunner.enqueue(new byte[]{  });
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_RANGE_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangePutOnlyHashWithRangeNameNoRangeValueFailure() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(JSON_DOCUMENT, "j1");
        putRunner.enqueue(new byte[]{  });
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_RANGE_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangePutSuccessfulWithMock() {
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
    }

    @Test
    public void testStringHashStringRangePutOneSuccessfulOneSizeFailureWithMockBatchSize1() {
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        byte[] item = new byte[(DYNAMODB_MAX_ITEM_SIZE) + 1];
        for (int i = 0; i < (item.length); i++) {
            item[i] = 'a';
        }
        String document2 = new String(item);
        putRunner.enqueue(document2.getBytes());
        putRunner.run(2, true, true);
        List<MockFlowFile> flowFilesFailed = putRunner.getFlowFilesForRelationship(AbstractWriteDynamoDBProcessor.REL_FAILURE);
        for (MockFlowFile flowFile : flowFilesFailed) {
            System.out.println(flowFile.getAttributes());
            flowFile.assertAttributeExists(AWS_DYNAMO_DB_ITEM_SIZE_ERROR);
            Assert.assertEquals(item.length, flowFile.getSize());
        }
        List<MockFlowFile> flowFilesSuccessful = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFilesSuccessful) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
    }

    @Test
    public void testStringHashStringRangePutOneSuccessfulOneSizeFailureWithMockBatchSize5() {
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(BATCH_SIZE, "5");
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        byte[] item = new byte[(DYNAMODB_MAX_ITEM_SIZE) + 1];
        for (int i = 0; i < (item.length); i++) {
            item[i] = 'a';
        }
        String document2 = new String(item);
        putRunner.enqueue(document2.getBytes());
        putRunner.run(1);
        List<MockFlowFile> flowFilesFailed = putRunner.getFlowFilesForRelationship(AbstractWriteDynamoDBProcessor.REL_FAILURE);
        for (MockFlowFile flowFile : flowFilesFailed) {
            System.out.println(flowFile.getAttributes());
            flowFile.assertAttributeExists(AWS_DYNAMO_DB_ITEM_SIZE_ERROR);
            Assert.assertEquals(item.length, flowFile.getSize());
        }
        List<MockFlowFile> flowFilesSuccessful = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFilesSuccessful) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
    }

    @Test
    public void testStringHashStringRangePutFailedWithItemSizeGreaterThan400Kb() {
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.JSON_DOCUMENT, "document");
        byte[] item = new byte[(DYNAMODB_MAX_ITEM_SIZE) + 1];
        for (int i = 0; i < (item.length); i++) {
            item[i] = 'a';
        }
        String document = new String(item);
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(AbstractWriteDynamoDBProcessor.REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(AbstractWriteDynamoDBProcessor.REL_FAILURE);
        Assert.assertEquals(1, flowFiles.size());
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            flowFile.assertAttributeExists(AWS_DYNAMO_DB_ITEM_SIZE_ERROR);
            Assert.assertEquals(item.length, flowFile.getSize());
        }
    }

    @Test
    public void testStringHashStringRangePutThrowsServiceException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchWriteItemOutcome batchWriteItem(TableWriteItems... tableWriteItems) {
                throw new AmazonServiceException("serviceException");
            }
        };
        putDynamoDB = new PutDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("serviceException (Service: null; Status Code: 0; Error Code: null; Request ID: null)", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangePutThrowsClientException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchWriteItemOutcome batchWriteItem(TableWriteItems... tableWriteItems) {
                throw new AmazonClientException("clientException");
            }
        };
        putDynamoDB = new PutDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("clientException", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangePutThrowsRuntimeException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchWriteItemOutcome batchWriteItem(TableWriteItems... tableWriteItems) {
                throw new RuntimeException("runtimeException");
            }
        };
        putDynamoDB = new PutDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("runtimeException", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangePutSuccessfulWithMockOneUnprocessed() {
        Map<String, List<WriteRequest>> unprocessed = new HashMap<String, List<WriteRequest>>();
        PutRequest put = new PutRequest();
        put.addItemEntry("hashS", new AttributeValue("h1"));
        put.addItemEntry("rangeS", new AttributeValue("r1"));
        WriteRequest write = new WriteRequest(put);
        List<WriteRequest> writes = new ArrayList<>();
        writes.add(write);
        unprocessed.put(ITAbstractDynamoDBTest.stringHashStringRangeTableName, writes);
        result.setUnprocessedItems(unprocessed);
        final TestRunner putRunner = TestRunners.newTestRunner(putDynamoDB);
        putRunner.setProperty(ACCESS_KEY, "abcd");
        putRunner.setProperty(SECRET_KEY, "cdef");
        putRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(JSON_DOCUMENT, "j2");
        putRunner.enqueue("{\"hello\":\"world\"}".getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_UNPROCESSED, 1);
    }
}

