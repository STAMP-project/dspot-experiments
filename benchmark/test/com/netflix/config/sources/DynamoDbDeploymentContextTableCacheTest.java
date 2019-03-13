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


import DeploymentContext.ContextKey;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.netflix.config.PropertyWithDeploymentContext;
import java.util.Collection;
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
public class DynamoDbDeploymentContextTableCacheTest {
    private static final PropertyWithDeploymentContext test1 = new PropertyWithDeploymentContext(ContextKey.environment, "test", "foo", "bar");

    private static final PropertyWithDeploymentContext test2 = new PropertyWithDeploymentContext(ContextKey.environment, "test", "goo", "goo");

    private static final PropertyWithDeploymentContext test3 = new PropertyWithDeploymentContext(ContextKey.environment, "prod", "goo", "foo");

    private static final PropertyWithDeploymentContext test4 = new PropertyWithDeploymentContext(ContextKey.environment, "prod", "goo", "foo");

    private static final PropertyWithDeploymentContext test5 = new PropertyWithDeploymentContext(ContextKey.environment, "test", "boo", "who");

    private static final PropertyWithDeploymentContext test6 = new PropertyWithDeploymentContext(null, null, "foo", "bar");

    @Test
    public void testPoll() throws Exception {
        AmazonDynamoDB mockContextDbClient = Mockito.mock(AmazonDynamoDB.class);
        Mockito.when(mockContextDbClient.scan(ArgumentMatchers.any(ScanRequest.class))).thenReturn(DynamoDbMocks.contextScanResult1, DynamoDbMocks.contextScanResult2);
        DynamoDbDeploymentContextTableCache cache = new DynamoDbDeploymentContextTableCache(mockContextDbClient, 100, 100);
        Collection<PropertyWithDeploymentContext> props = cache.getProperties();
        Assert.assertEquals(4, props.size());
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test1));
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test2));
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test5));
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test6));
        Thread.sleep(150);
        props = cache.getProperties();
        Assert.assertEquals(5, props.size());
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test1));
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test3));
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test4));
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test5));
        Assert.assertTrue(props.contains(DynamoDbDeploymentContextTableCacheTest.test6));
    }
}

