/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config.sources;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.netflix.config.PollResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * User: gorzell
 * Date: 1/17/13
 * Time: 10:18 AM
 * You should write something useful here.
 */
public class DynamoDbConfigurationSourceTest {
    @Test
    public void testPoll() throws Exception {
        AmazonDynamoDB mockBasicDbClient = Mockito.mock(AmazonDynamoDB.class);
        Mockito.when(mockBasicDbClient.scan(ArgumentMatchers.any(ScanRequest.class))).thenReturn(DynamoDbMocks.basicScanResult1);
        DynamoDbConfigurationSource testConfigSource = new DynamoDbConfigurationSource(mockBasicDbClient);
        PollResult result = testConfigSource.poll(false, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals("bar", result.getComplete().get("foo"));
        Assert.assertEquals("goo", result.getComplete().get("goo"));
        Assert.assertEquals("who", result.getComplete().get("boo"));
    }

    @Test
    public void testUpdate() throws Exception {
        AmazonDynamoDB mockBasicDbClient = Mockito.mock(AmazonDynamoDB.class);
        Mockito.when(mockBasicDbClient.scan(ArgumentMatchers.any(ScanRequest.class))).thenReturn(DynamoDbMocks.basicScanResult1, DynamoDbMocks.basicScanResult2);
        DynamoDbConfigurationSource testConfigSource = new DynamoDbConfigurationSource(mockBasicDbClient);
        PollResult result = testConfigSource.poll(false, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals("bar", result.getComplete().get("foo"));
        Assert.assertEquals("goo", result.getComplete().get("goo"));
        Assert.assertEquals("who", result.getComplete().get("boo"));
        result = testConfigSource.poll(false, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals("bar", result.getComplete().get("foo"));
        Assert.assertEquals("foo", result.getComplete().get("goo"));
        Assert.assertEquals("who", result.getComplete().get("boo"));
    }
}

