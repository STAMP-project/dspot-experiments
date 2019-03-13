/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.protocol.protobuf.v1.operations;


import RegionAPI.KeySetRequest;
import RegionAPI.KeySetResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.geode.internal.protocol.TestExecutionContext;
import org.apache.geode.internal.protocol.protobuf.v1.RegionAPI;
import org.apache.geode.internal.protocol.protobuf.v1.Result;
import org.apache.geode.internal.protocol.protobuf.v1.Success;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientServerTest.class })
public class KeySetOperationHandlerJUnitTest extends OperationHandlerJUnitTest {
    private final String TEST_KEY1 = "Key1";

    private final String TEST_KEY2 = "Key2";

    private final String TEST_KEY3 = "Key3";

    private final String TEST_REGION = "test region";

    @Test
    public void verifyKeySetReturnsExpectedKeys() throws Exception {
        RegionAPI.KeySetRequest request = KeySetRequest.newBuilder().setRegionName(TEST_REGION).build();
        Result result = operationHandler.process(serializationService, request, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        RegionAPI.KeySetResponse response = ((RegionAPI.KeySetResponse) (result.getMessage()));
        List<Object> results = response.getKeysList().stream().map(serializationService::decode).collect(Collectors.toList());
        Assert.assertEquals(3, results.size());
        Assert.assertTrue(results.contains(TEST_KEY1));
        Assert.assertTrue(results.contains(TEST_KEY2));
        Assert.assertTrue(results.contains(TEST_KEY3));
    }
}

