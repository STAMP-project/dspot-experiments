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


import AbstractDynamoDBProcessor.DYNAMODB_KEY_ERROR_NOT_FOUND;
import AbstractDynamoDBProcessor.DYNAMODB_KEY_ERROR_NOT_FOUND_MESSAGE;
import AbstractWriteDynamoDBProcessor.ALLOWABLE_VALUE_NUMBER;
import AbstractWriteDynamoDBProcessor.CREDENTIALS_FILE;
import AbstractWriteDynamoDBProcessor.HASH_KEY_NAME;
import AbstractWriteDynamoDBProcessor.HASH_KEY_VALUE;
import AbstractWriteDynamoDBProcessor.HASH_KEY_VALUE_TYPE;
import AbstractWriteDynamoDBProcessor.JSON_DOCUMENT;
import AbstractWriteDynamoDBProcessor.RANGE_KEY_NAME;
import AbstractWriteDynamoDBProcessor.RANGE_KEY_VALUE;
import AbstractWriteDynamoDBProcessor.RANGE_KEY_VALUE_TYPE;
import AbstractWriteDynamoDBProcessor.REGION;
import AbstractWriteDynamoDBProcessor.REL_SUCCESS;
import AbstractWriteDynamoDBProcessor.TABLE;
import DeleteDynamoDB.REL_FAILURE;
import GetDynamoDB.REL_NOT_FOUND;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class ITPutGetDeleteGetDynamoDBTest extends ITAbstractDynamoDBTest {
    @Test
    public void testStringHashStringRangePutGetDeleteGetSuccess() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunner.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashS");
        getRunner.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "h1");
        getRunner.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(AbstractDynamoDBProcessor.REL_SUCCESS, 1);
        flowFiles = getRunner.getFlowFilesForRelationship(AbstractDynamoDBProcessor.REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner deleteRunner = TestRunners.newTestRunner(DeleteDynamoDB.class);
        deleteRunner.setProperty(DeleteDynamoDB.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        deleteRunner.setProperty(DeleteDynamoDB.REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(DeleteDynamoDB.TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(DeleteDynamoDB.RANGE_KEY_NAME, "rangeS");
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_VALUE, "h1");
        deleteRunner.setProperty(DeleteDynamoDB.RANGE_KEY_VALUE, "r1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(DeleteDynamoDB.REL_SUCCESS, 1);
        flowFiles = deleteRunner.getFlowFilesForRelationship(DeleteDynamoDB.REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals("", new String(flowFile.toByteArray()));
        }
        // Final check after delete
        final TestRunner getRunnerAfterDelete = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashS");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_NAME, "rangeS");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "h1");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_VALUE, "r1");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunnerAfterDelete.enqueue(new byte[]{  });
        getRunnerAfterDelete.run(1);
        getRunnerAfterDelete.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        flowFiles = getRunnerAfterDelete.getFlowFilesForRelationship(REL_NOT_FOUND);
        for (MockFlowFile flowFile : flowFiles) {
            String error = flowFile.getAttribute(DYNAMODB_KEY_ERROR_NOT_FOUND);
            Assert.assertTrue(error.startsWith(DYNAMODB_KEY_ERROR_NOT_FOUND_MESSAGE));
        }
    }

    @Test
    public void testStringHashStringRangePutDeleteWithHashOnlyFailure() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunner.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashS");
        getRunner.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_NAME, "rangeS");
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "h1");
        getRunner.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_VALUE, "r1");
        getRunner.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(AbstractDynamoDBProcessor.REL_SUCCESS, 1);
        flowFiles = getRunner.getFlowFilesForRelationship(AbstractDynamoDBProcessor.REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner deleteRunner = TestRunners.newTestRunner(DeleteDynamoDB.class);
        deleteRunner.setProperty(DeleteDynamoDB.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        deleteRunner.setProperty(DeleteDynamoDB.REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(DeleteDynamoDB.TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_NAME, "hashS");
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_VALUE, "h1");
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        flowFiles = deleteRunner.getFlowFilesForRelationship(REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            ITAbstractDynamoDBTest.validateServiceExceptionAttribute(flowFile);
            Assert.assertEquals("", new String(flowFile.toByteArray()));
        }
    }

    @Test
    public void testStringHashStringRangePutGetWithHashOnlyKeyFailure() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashS");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeS");
        putRunner.setProperty(HASH_KEY_VALUE, "h1");
        putRunner.setProperty(RANGE_KEY_VALUE, "r1");
        putRunner.setProperty(JSON_DOCUMENT, "document");
        String document = "{\"name\":\"john\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunner.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.stringHashStringRangeTableName);
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashS");
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "h1");
        getRunner.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(AbstractDynamoDBProcessor.REL_FAILURE, 1);
        flowFiles = getRunner.getFlowFilesForRelationship(AbstractDynamoDBProcessor.REL_FAILURE);
        for (MockFlowFile flowFile : flowFiles) {
            ITAbstractDynamoDBTest.validateServiceExceptionAttribute(flowFile);
            Assert.assertEquals("", new String(flowFile.toByteArray()));
        }
    }

    @Test
    public void testNumberHashOnlyPutGetDeleteGetSuccess() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.numberHashOnlyTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashN");
        putRunner.setProperty(HASH_KEY_VALUE, "40");
        putRunner.setProperty(HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        putRunner.setProperty(JSON_DOCUMENT, "document");
        String document = "{\"age\":40}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunner.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.numberHashOnlyTableName);
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashN");
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "40");
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        getRunner.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(AbstractDynamoDBProcessor.REL_SUCCESS, 1);
        flowFiles = getRunner.getFlowFilesForRelationship(AbstractDynamoDBProcessor.REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner deleteRunner = TestRunners.newTestRunner(DeleteDynamoDB.class);
        deleteRunner.setProperty(DeleteDynamoDB.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        deleteRunner.setProperty(DeleteDynamoDB.REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(DeleteDynamoDB.TABLE, ITAbstractDynamoDBTest.numberHashOnlyTableName);
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_NAME, "hashN");
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_VALUE, "40");
        deleteRunner.setProperty(HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(DeleteDynamoDB.REL_SUCCESS, 1);
        flowFiles = deleteRunner.getFlowFilesForRelationship(DeleteDynamoDB.REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals("", new String(flowFile.toByteArray()));
        }
        // Final check after delete
        final TestRunner getRunnerAfterDelete = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.numberHashOnlyTableName);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashN");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "40");
        getRunnerAfterDelete.setProperty(HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunnerAfterDelete.enqueue(new byte[]{  });
        getRunnerAfterDelete.run(1);
        getRunnerAfterDelete.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        flowFiles = getRunnerAfterDelete.getFlowFilesForRelationship(REL_NOT_FOUND);
        for (MockFlowFile flowFile : flowFiles) {
            String error = flowFile.getAttribute(DYNAMODB_KEY_ERROR_NOT_FOUND);
            Assert.assertTrue(error.startsWith(DYNAMODB_KEY_ERROR_NOT_FOUND_MESSAGE));
        }
    }

    @Test
    public void testNumberHashNumberRangePutGetDeleteGetSuccess() {
        final TestRunner putRunner = TestRunners.newTestRunner(PutDynamoDB.class);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        putRunner.setProperty(AbstractWriteDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        putRunner.setProperty(TABLE, ITAbstractDynamoDBTest.numberHashNumberRangeTableName);
        putRunner.setProperty(HASH_KEY_NAME, "hashN");
        putRunner.setProperty(RANGE_KEY_NAME, "rangeN");
        putRunner.setProperty(HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        putRunner.setProperty(RANGE_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        putRunner.setProperty(HASH_KEY_VALUE, "40");
        putRunner.setProperty(RANGE_KEY_VALUE, "50");
        putRunner.setProperty(JSON_DOCUMENT, "document");
        String document = "{\"40\":\"50\"}";
        putRunner.enqueue(document.getBytes());
        putRunner.run(1);
        putRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> flowFiles = putRunner.getFlowFilesForRelationship(REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner getRunner = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunner.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunner.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunner.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.numberHashNumberRangeTableName);
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashN");
        getRunner.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_NAME, "rangeN");
        getRunner.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "40");
        getRunner.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_VALUE, "50");
        getRunner.setProperty(HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        getRunner.setProperty(RANGE_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        getRunner.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunner.enqueue(new byte[]{  });
        getRunner.run(1);
        getRunner.assertAllFlowFilesTransferred(AbstractDynamoDBProcessor.REL_SUCCESS, 1);
        flowFiles = getRunner.getFlowFilesForRelationship(AbstractDynamoDBProcessor.REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            Assert.assertEquals(document, new String(flowFile.toByteArray()));
        }
        final TestRunner deleteRunner = TestRunners.newTestRunner(DeleteDynamoDB.class);
        deleteRunner.setProperty(DeleteDynamoDB.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        deleteRunner.setProperty(DeleteDynamoDB.REGION, ITAbstractDynamoDBTest.REGION);
        deleteRunner.setProperty(DeleteDynamoDB.TABLE, ITAbstractDynamoDBTest.numberHashNumberRangeTableName);
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_NAME, "hashN");
        deleteRunner.setProperty(DeleteDynamoDB.RANGE_KEY_NAME, "rangeN");
        deleteRunner.setProperty(DeleteDynamoDB.HASH_KEY_VALUE, "40");
        deleteRunner.setProperty(DeleteDynamoDB.RANGE_KEY_VALUE, "50");
        deleteRunner.setProperty(HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        deleteRunner.setProperty(RANGE_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        deleteRunner.enqueue(new byte[]{  });
        deleteRunner.run(1);
        deleteRunner.assertAllFlowFilesTransferred(DeleteDynamoDB.REL_SUCCESS, 1);
        flowFiles = deleteRunner.getFlowFilesForRelationship(DeleteDynamoDB.REL_SUCCESS);
        for (MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile.getAttributes());
            Assert.assertEquals("", new String(flowFile.toByteArray()));
        }
        // Final check after delete
        final TestRunner getRunnerAfterDelete = TestRunners.newTestRunner(GetDynamoDB.class);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.CREDENTIALS_FILE, ITAbstractDynamoDBTest.CREDENTIALS_FILE);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.REGION, ITAbstractDynamoDBTest.REGION);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.TABLE, ITAbstractDynamoDBTest.numberHashNumberRangeTableName);
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.HASH_KEY_NAME, "hashN");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_NAME, "rangeN");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.HASH_KEY_VALUE, "40");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.RANGE_KEY_VALUE, "50");
        getRunnerAfterDelete.setProperty(AbstractDynamoDBProcessor.JSON_DOCUMENT, "document");
        getRunnerAfterDelete.setProperty(HASH_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        getRunnerAfterDelete.setProperty(RANGE_KEY_VALUE_TYPE, ALLOWABLE_VALUE_NUMBER);
        getRunnerAfterDelete.enqueue(new byte[]{  });
        getRunnerAfterDelete.run(1);
        getRunnerAfterDelete.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        flowFiles = getRunnerAfterDelete.getFlowFilesForRelationship(REL_NOT_FOUND);
        for (MockFlowFile flowFile : flowFiles) {
            String error = flowFile.getAttribute(DYNAMODB_KEY_ERROR_NOT_FOUND);
            Assert.assertTrue(error.startsWith(DYNAMODB_KEY_ERROR_NOT_FOUND_MESSAGE));
        }
    }
}

