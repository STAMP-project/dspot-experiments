/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.service;


import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.zeppelin.rest.AbstractTestRestApi;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ConfigurationServiceTest extends AbstractTestRestApi {
    private static ConfigurationService configurationService;

    private ServiceContext context = new ServiceContext(AuthenticationInfo.ANONYMOUS, new HashSet());

    private ServiceCallback callback = Mockito.mock(ServiceCallback.class);

    @Test
    public void testFetchConfiguration() throws IOException {
        Map<String, String> properties = ConfigurationServiceTest.configurationService.getAllProperties(context, callback);
        Mockito.verify(callback).onSuccess(properties, context);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Assert.assertFalse(entry.getKey().contains("password"));
        }
        Mockito.reset(callback);
        properties = ConfigurationServiceTest.configurationService.getPropertiesWithPrefix("zeppelin.server", context, callback);
        Mockito.verify(callback).onSuccess(properties, context);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Assert.assertFalse(entry.getKey().contains("password"));
            TestCase.assertTrue(entry.getKey().startsWith("zeppelin.server"));
        }
    }
}

