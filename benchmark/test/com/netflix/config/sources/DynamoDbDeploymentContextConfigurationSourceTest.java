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
import com.netflix.config.PollResult;
import com.netflix.config.PropertyWithDeploymentContext;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * User: gorzell
 * Date: 1/17/13
 * Time: 10:18 AM
 * You should write something useful here.
 */
public class DynamoDbDeploymentContextConfigurationSourceTest {
    private static Collection<PropertyWithDeploymentContext> propCollection1;

    private static Collection<PropertyWithDeploymentContext> propCollection2;

    @Test
    public void testPoll() throws Exception {
        DynamoDbDeploymentContextTableCache mockedCache = Mockito.mock(DynamoDbDeploymentContextTableCache.class);
        Mockito.when(mockedCache.getProperties()).thenReturn(DynamoDbDeploymentContextConfigurationSourceTest.propCollection1, DynamoDbDeploymentContextConfigurationSourceTest.propCollection2);
        DynamoDbDeploymentContextConfigurationSource testConfigSource = new DynamoDbDeploymentContextConfigurationSource(mockedCache, ContextKey.environment);
        PollResult result = testConfigSource.poll(false, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals(result.getComplete().get("foo"), "bar");
        Assert.assertEquals(result.getComplete().get("goo"), "goo");
        Assert.assertEquals(result.getComplete().get("boo"), "who");
        result = testConfigSource.poll(false, null);
        Assert.assertEquals(3, result.getComplete().size());
        Assert.assertEquals(result.getComplete().get("foo"), "bar");
        Assert.assertEquals(result.getComplete().get("goo"), "boo");
        Assert.assertEquals(result.getComplete().get("boo"), "who");
    }
}

