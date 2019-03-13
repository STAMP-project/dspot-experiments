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
import AbstractDynamoDBProcessor.DYNAMODB_ERROR_EXCEPTION_MESSAGE;
import AbstractDynamoDBProcessor.DYNAMODB_HASH_KEY_VALUE_ERROR;
import AbstractDynamoDBProcessor.DYNAMODB_RANGE_KEY_VALUE_ERROR;
import AbstractDynamoDBProcessor.HASH_KEY_NAME;
import AbstractDynamoDBProcessor.HASH_KEY_VALUE;
import AbstractDynamoDBProcessor.RANGE_KEY_NAME;
import AbstractDynamoDBProcessor.RANGE_KEY_VALUE;
import AbstractDynamoDBProcessor.REGION;
import AbstractDynamoDBProcessor.REL_FAILURE;
import AbstractDynamoDBProcessor.REL_SUCCESS;
import AbstractDynamoDBProcessor.REL_UNPROCESSED;
import AbstractDynamoDBProcessor.SECRET_KEY;
import AbstractDynamoDBProcessor.TABLE;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
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


public class DeleteDynamoDBTest extends AbstractDynamoDBTest {
    protected DeleteDynamoDB deleteDynamoDB;

    protected BatchWriteItemResult result = new BatchWriteItemResult();

    BatchWriteItemOutcome outcome;

    @Test
    public void testStringHashStringRangeDeleteOnlyHashFailure() {
        // Inject a mock DynamoDB to create the exception condition
        final DynamoDB mockDynamoDb = Mockito.mock(DynamoDB.class);
        // When writing, mock thrown service exception from AWS
        Mockito.when(mockDynamoDb.batchWriteItem(Matchers.<TableWriteItems>anyVararg())).thenThrow(getSampleAwsServiceException());
        deleteDynamoDB = new DeleteDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDb;
            }
        };
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            ITAbstractDynamoDBTest.validateServiceExceptionAttribute(flowFile);
        }
    }

    @Test
    public void testStringHashStringRangeDeleteSuccessfulWithMock() {
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testStringHashStringRangeDeleteSuccessfulWithMockOneUnprocessed() {
        Map<String, List<WriteRequest>> unprocessed = new HashMap<String, List<WriteRequest>>();
        DeleteRequest delete = new DeleteRequest();
        delete.addKeyEntry("hashS", new AttributeValue("h1"));
        delete.addKeyEntry("rangeS", new AttributeValue("r1"));
        WriteRequest write = new WriteRequest(delete);
        List<WriteRequest> writes = new ArrayList<>();
        writes.add(write);
        unprocessed.put(ITAbstractDynamoDBTest.stringHashStringRangeTableName, writes);
        result.setUnprocessedItems(unprocessed);
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_UNPROCESSED, 1);
    }

    @Test
    public void testStringHashStringRangeDeleteNoHashValueFailure() {
        final TestRunner deleteRunner = TestRunners.newTestRunner(DeleteDynamoDB.class);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_HASH_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangeDeleteOnlyHashWithRangeValueNoRangeNameFailure() {
        final TestRunner deleteRunner = TestRunners.newTestRunner(DeleteDynamoDB.class);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_RANGE_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangeDeleteOnlyHashWithRangeNameNoRangeValueFailure() {
        final TestRunner deleteRunner = TestRunners.newTestRunner(DeleteDynamoDB.class);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_RANGE_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangeDeleteNonExistentHashSuccess() {
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "nonexistent");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testStringHashStringRangeDeleteNonExistentRangeSuccess() {
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "nonexistent");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testStringHashStringRangeDeleteThrowsServiceException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchWriteItemOutcome batchWriteItem(TableWriteItems... tableWriteItems) {
                throw new AmazonServiceException("serviceException");
            }
        };
        deleteDynamoDB = new DeleteDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("serviceException (Service: null; Status Code: 0; Error Code: null; Request ID: null)", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangeDeleteThrowsClientException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchWriteItemOutcome batchWriteItem(TableWriteItems... tableWriteItems) {
                throw new AmazonClientException("clientException");
            }
        };
        deleteDynamoDB = new DeleteDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("clientException", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangeDeleteThrowsRuntimeException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchWriteItemOutcome batchWriteItem(TableWriteItems... tableWriteItems) {
                throw new RuntimeException("runtimeException");
            }
        };
        deleteDynamoDB = new DeleteDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner deleteRunner = TestRunners.newTestRunner(deleteDynamoDB);
        deleteRunner.setProperty(ACCESS_KEY, "abcd");
        deleteRunner.setProperty(SECRET_KEY, "cdef");
        deleteRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("runtimeException", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }
}

