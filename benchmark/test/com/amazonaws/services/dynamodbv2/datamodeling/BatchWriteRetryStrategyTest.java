/**
 * Copyright 2015-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.BatchWriteRetryStrategy;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import junit.framework.Assert;
import org.junit.Test;


public class BatchWriteRetryStrategyTest {
    private static final int MAX_RETRY = 10;

    private static final String TABLE_NAME = "tableName";

    private static final String HASH_ATTR = "hash";

    private static Map<String, List<WriteRequest>> unprocessedItems;

    private AmazonDynamoDB ddbMock;

    private DynamoDBMapper mapper;

    static {
        WriteRequest writeReq = new WriteRequest().withPutRequest(new PutRequest().withItem(Collections.singletonMap(BatchWriteRetryStrategyTest.HASH_ATTR, new AttributeValue("foo"))));
        BatchWriteRetryStrategyTest.unprocessedItems = Collections.singletonMap(BatchWriteRetryStrategyTest.TABLE_NAME, Arrays.asList(writeReq));
    }

    @Test
    public void testBatchWriteItemCallSuccess_NoRetry() {
        // BatchWriteItem is expected to be called only once
        expectBatchWriteItemSuccess().once();
        replay(ddbMock);
        List<FailedBatch> failedBatches = mapper.batchSave(new BatchWriteRetryStrategyTest.Item("foo"));
        verify(ddbMock);
        Assert.assertEquals(0, failedBatches.size());
    }

    @Test
    public void testUnprocessedItemReturned_BatchWriteItemCallNotExceedMaxRetry() {
        // BatchWriteItem is expected to be called exactly (MAX_RETRY + 1) times
        expectBatchWriteItemReturnUnprocessedItems().times(((BatchWriteRetryStrategyTest.MAX_RETRY) + 1));
        replay(ddbMock);
        List<FailedBatch> failedBatches = mapper.batchSave(new BatchWriteRetryStrategyTest.Item("foo"));
        verify(ddbMock);
        Assert.assertEquals(1, failedBatches.size());
        FailedBatch failedBatch = failedBatches.get(0);
        Assert.assertEquals("Failed batch should contain the same UnprocessedItems returned in the BatchWriteItem response.", BatchWriteRetryStrategyTest.unprocessedItems, failedBatch.getUnprocessedItems());
        Assert.assertNull("No exception should be set if the batch failed after max retry", failedBatch.getException());
    }

    @Test
    public void testExceptionThrown_NoRetry() {
        RuntimeException exception = new RuntimeException("BOOM");
        expectedBatchWriteItemThrowException(exception);
        replay(ddbMock);
        // put a random item
        BatchWriteRetryStrategyTest.Item item = new BatchWriteRetryStrategyTest.Item(UUID.randomUUID().toString());
        List<FailedBatch> failedBatches = mapper.batchSave(item);
        verify(ddbMock);
        Assert.assertEquals(1, failedBatches.size());
        FailedBatch failedBatch = failedBatches.get(0);
        Assert.assertEquals("Failed batch should contain all the input items for batchWrite", Collections.singletonMap(BatchWriteRetryStrategyTest.TABLE_NAME, Arrays.asList(item.toPutSaveRequest())), failedBatch.getUnprocessedItems());
        Assert.assertSame("The exception should be the same as one thrown by BatchWriteItem", exception, failedBatch.getException());
    }

    private static class BatchWriteRetryStrategyWithNoDelay implements BatchWriteRetryStrategy {
        private final int maxRetry;

        public BatchWriteRetryStrategyWithNoDelay(int maxRety) {
            this.maxRetry = maxRety;
        }

        @Override
        public int getMaxRetryOnUnprocessedItems(Map<String, List<WriteRequest>> batchWriteItemInput) {
            return maxRetry;
        }

        @Override
        public long getDelayBeforeRetryUnprocessedItems(Map<String, List<WriteRequest>> unprocessedItems, int retriesAttempted) {
            return 0;
        }
    }

    @DynamoDBTable(tableName = BatchWriteRetryStrategyTest.TABLE_NAME)
    public static class Item {
        private String hash;

        public Item(String hash) {
            this.hash = hash;
        }

        @DynamoDBHashKey
        @DynamoDBAttribute(attributeName = BatchWriteRetryStrategyTest.HASH_ATTR)
        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public WriteRequest toPutSaveRequest() {
            return new WriteRequest().withPutRequest(new PutRequest(Collections.singletonMap(BatchWriteRetryStrategyTest.HASH_ATTR, new AttributeValue(hash))));
        }
    }
}

