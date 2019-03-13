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
import AbstractDynamoDBProcessor.DYNAMODB_KEY_ERROR_NOT_FOUND;
import AbstractDynamoDBProcessor.DYNAMODB_KEY_ERROR_UNPROCESSED;
import AbstractDynamoDBProcessor.DYNAMODB_RANGE_KEY_VALUE_ERROR;
import AbstractDynamoDBProcessor.HASH_KEY_NAME;
import AbstractDynamoDBProcessor.HASH_KEY_VALUE;
import AbstractDynamoDBProcessor.JSON_DOCUMENT;
import AbstractDynamoDBProcessor.RANGE_KEY_NAME;
import AbstractDynamoDBProcessor.RANGE_KEY_VALUE;
import AbstractDynamoDBProcessor.REGION;
import AbstractDynamoDBProcessor.REL_FAILURE;
import AbstractDynamoDBProcessor.REL_SUCCESS;
import AbstractDynamoDBProcessor.REL_UNPROCESSED;
import AbstractDynamoDBProcessor.SECRET_KEY;
import AbstractDynamoDBProcessor.TABLE;
import GetDynamoDB.REL_NOT_FOUND;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import java.io.IOException;
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


public class GetDynamoDBTest extends AbstractDynamoDBTest {
    protected GetDynamoDB getDynamoDB;

    protected BatchGetItemOutcome outcome;

    protected BatchGetItemResult result = new BatchGetItemResult();

    private HashMap unprocessed;

    @Test
    public void testStringHashStringRangeGetUnprocessed() {
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_UNPROCESSED, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_UNPROCESSED);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_KEY_ERROR_UNPROCESSED));
        }
    }

    @Test
    public void testStringHashStringRangeGetJsonObjectNull() {
        outcome = new BatchGetItemOutcome(result);
        KeysAndAttributes kaa = new KeysAndAttributes();
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("hashS", new AttributeValue("h1"));
        map.put("rangeS", new AttributeValue("r1"));
        kaa.withKeys(map);
        unprocessed = new HashMap<>();
        result.withUnprocessedKeys(unprocessed);
        Map<String, List<Map<String, AttributeValue>>> responses = new HashMap<>();
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("j1", null);
        item.put("hashS", new AttributeValue("h1"));
        item.put("rangeS", new AttributeValue("r1"));
        items.add(item);
        responses.put("StringHashStringRangeTable", items);
        result.withResponses(responses);
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchGetItemOutcome batchGetItem(TableKeysAndAttributes... tableKeysAndAttributes) {
                return outcome;
            }
        };
        getDynamoDB = new GetDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNull(flowFile.getContentClaim());
        }
    }

    @Test
    public void testStringHashStringRangeGetJsonObjectValid() throws IOException {
        outcome = new BatchGetItemOutcome(result);
        KeysAndAttributes kaa = new KeysAndAttributes();
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("hashS", new AttributeValue("h1"));
        map.put("rangeS", new AttributeValue("r1"));
        kaa.withKeys(map);
        unprocessed = new HashMap<>();
        result.withUnprocessedKeys(unprocessed);
        Map<String, List<Map<String, AttributeValue>>> responses = new HashMap<>();
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        String jsonDocument = "{\"name\": \"john\"}";
        item.put("j1", new AttributeValue(jsonDocument));
        item.put("hashS", new AttributeValue("h1"));
        item.put("rangeS", new AttributeValue("r1"));
        items.add(item);
        responses.put("StringHashStringRangeTable", items);
        result.withResponses(responses);
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchGetItemOutcome batchGetItem(TableKeysAndAttributes... tableKeysAndAttributes) {
                return outcome;
            }
        };
        getDynamoDB = new GetDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testStringHashStringRangeGetThrowsServiceException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchGetItemOutcome batchGetItem(TableKeysAndAttributes... tableKeysAndAttributes) {
                throw new AmazonServiceException("serviceException");
            }
        };
        final GetDynamoDB getDynamoDB = new GetDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("serviceException (Service: null; Status Code: 0; Error Code: null; Request ID: null)", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangeGetThrowsRuntimeException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchGetItemOutcome batchGetItem(TableKeysAndAttributes... tableKeysAndAttributes) {
                throw new RuntimeException("runtimeException");
            }
        };
        final GetDynamoDB getDynamoDB = new GetDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("runtimeException", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangeGetThrowsClientException() {
        final DynamoDB mockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchGetItemOutcome batchGetItem(TableKeysAndAttributes... tableKeysAndAttributes) {
                throw new AmazonClientException("clientException");
            }
        };
        final GetDynamoDB getDynamoDB = new GetDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDB;
            }
        };
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals("clientException", flowFile.getAttribute(DYNAMODB_ERROR_EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangeGetNotFound() {
        result.clearResponsesEntries();
        result.clearUnprocessedKeysEntries();
        final BatchGetItemOutcome notFoundOutcome = new BatchGetItemOutcome(result);
        Map<String, List<Map<String, AttributeValue>>> responses = new HashMap<>();
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        responses.put(ITAbstractDynamoDBTest.stringHashStringRangeTableName, items);
        result.withResponses(responses);
        final DynamoDB notFoundMockDynamoDB = new DynamoDB(Regions.AP_NORTHEAST_1) {
            @Override
            public BatchGetItemOutcome batchGetItem(TableKeysAndAttributes... tableKeysAndAttributes) {
                return notFoundOutcome;
            }
        };
        final GetDynamoDB getDynamoDB = new GetDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return notFoundMockDynamoDB;
            }
        };
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_UNPROCESSED);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_KEY_ERROR_NOT_FOUND));
        }
    }

    @Test
    public void testStringHashStringRangeGetOnlyHashFailure() {
        // Inject a mock DynamoDB to create the exception condition
        final DynamoDB mockDynamoDb = Mockito.mock(DynamoDB.class);
        // When writing, mock thrown service exception from AWS
        Mockito.when(mockDynamoDb.batchGetItem(Matchers.<TableKeysAndAttributes>anyVararg())).thenThrow(getSampleAwsServiceException());
        getDynamoDB = new GetDynamoDB() {
            @Override
            protected DynamoDB getDynamoDB() {
                return mockDynamoDb;
            }
        };
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            ITAbstractDynamoDBTest.validateServiceExceptionAttribute(flowFile);
        }
    }

    @Test
    public void testStringHashStringRangeGetNoHashValueFailure() {
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_HASH_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangeGetOnlyHashWithRangeValueNoRangeNameFailure() {
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_RANGE_KEY_VALUE_ERROR));
        }
    }

    @Test
    public void testStringHashStringRangeGetOnlyHashWithRangeNameNoRangeValueFailure() {
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_RANGE_KEY_VALUE_ERROR));
        }
    }

    // Incorporated test from James W
    @Test
    public void testStringHashStringNoRangeGetUnprocessed() {
        unprocessed.clear();
        KeysAndAttributes kaa = new KeysAndAttributes();
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("hashS", new AttributeValue("h1"));
        kaa.withKeys(map);
        unprocessed.put(ITAbstractDynamoDBTest.stringHashStringRangeTableName, kaa);
        final TestRunner getRunner = TestRunners.newTestRunner(getDynamoDB);
        getRunner.setProperty(ACCESS_KEY, "abcd");
        getRunner.setProperty(SECRET_KEY, "cdef");
        getRunner.setProperty(REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(HASH_KEY_NAME, "hashS");
        getRunner.setProperty(HASH_KEY_VALUE, "h1");
        getRunner.setProperty(JSON_DOCUMENT, "j1");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(REL_UNPROCESSED, 1);
        List<MockFlowFile> flowFiles = getRunner.getFlowFilesForRelationship(REL_UNPROCESSED);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertNotNull(flowFile.getAttribute(DYNAMODB_KEY_ERROR_UNPROCESSED));
        }
    }
}

