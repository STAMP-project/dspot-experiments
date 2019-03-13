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


import BasicTypes.EncodedValue;
import BasicTypes.Entry;
import RegionAPI.GetAllRequest;
import RegionAPI.GetAllResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.Region;
import org.apache.geode.internal.protocol.TestExecutionContext;
import org.apache.geode.internal.protocol.protobuf.v1.BasicTypes;
import org.apache.geode.internal.protocol.protobuf.v1.ProtobufRequestUtilities;
import org.apache.geode.internal.protocol.protobuf.v1.ProtobufSerializationService;
import org.apache.geode.internal.protocol.protobuf.v1.RegionAPI;
import org.apache.geode.internal.protocol.protobuf.v1.Result;
import org.apache.geode.internal.protocol.protobuf.v1.Success;
import org.apache.geode.internal.protocol.protobuf.v1.serialization.exception.DecodingException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class GetAllRequestOperationHandlerJUnitTest extends OperationHandlerJUnitTest {
    private static final String TEST_KEY1 = "my key1";

    private static final String TEST_VALUE1 = "my value1";

    private static final String TEST_KEY2 = "my key2";

    private static final String TEST_VALUE2 = "my value2";

    private static final String TEST_KEY3 = "my key3";

    private static final String TEST_VALUE3 = "my value3";

    private static final String TEST_REGION = "test region";

    private static final String TEST_INVALID_KEY = "I'm a naughty key!";

    private static final String NO_VALUE_PRESENT_FOR_THIS_KEY = "no value present for this key";

    private Region regionStub;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void processReturnsErrorUnableToDecodeRequest() throws Exception {
        Exception exception = new DecodingException("error finding codec for type");
        ProtobufSerializationService serializationServiceStub = Mockito.mock(ProtobufSerializationService.class);
        Mockito.when(serializationServiceStub.decodeList(ArgumentMatchers.any())).thenThrow(exception);
        BasicTypes.EncodedValue encodedKey1 = EncodedValue.newBuilder().setStringResult(GetAllRequestOperationHandlerJUnitTest.TEST_KEY1).build();
        BasicTypes.EncodedValue encodedKey2 = EncodedValue.newBuilder().setStringResult(GetAllRequestOperationHandlerJUnitTest.TEST_KEY2).build();
        Set<BasicTypes.EncodedValue> keys = new HashSet<>();
        keys.add(encodedKey1);
        keys.add(encodedKey2);
        RegionAPI.GetAllRequest getRequest = ProtobufRequestUtilities.createGetAllRequest(GetAllRequestOperationHandlerJUnitTest.TEST_REGION, keys);
        expectedException.expect(DecodingException.class);
        operationHandler.process(serializationServiceStub, getRequest, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
    }

    @Test
    public void processReturnsExpectedValuesForValidKeys() throws Exception {
        Result result = operationHandler.process(serializationService, generateTestRequest(true, false), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        RegionAPI.GetAllResponse response = ((RegionAPI.GetAllResponse) (result.getMessage()));
        Assert.assertEquals(3, response.getEntriesCount());
        List<BasicTypes.Entry> entriesList = response.getEntriesList();
        Map<String, String> responseEntries = convertEntryListToMap(entriesList);
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.TEST_VALUE1, responseEntries.get(GetAllRequestOperationHandlerJUnitTest.TEST_KEY1));
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.TEST_VALUE2, responseEntries.get(GetAllRequestOperationHandlerJUnitTest.TEST_KEY2));
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.TEST_VALUE3, responseEntries.get(GetAllRequestOperationHandlerJUnitTest.TEST_KEY3));
    }

    @Test
    public void processReturnsNoEntriesForNoKeysRequested() throws Exception {
        Result result = operationHandler.process(serializationService, generateTestRequest(false, false), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        RegionAPI.GetAllResponse response = ((RegionAPI.GetAllResponse) (result.getMessage()));
        List<BasicTypes.Entry> entriesList = response.getEntriesList();
        Map<String, String> responseEntries = convertEntryListToMap(entriesList);
        Assert.assertEquals(0, responseEntries.size());
    }

    @Test
    public void singleNullKey() throws Exception {
        HashSet<BasicTypes.EncodedValue> testKeys = new HashSet<>();
        testKeys.add(serializationService.encode(GetAllRequestOperationHandlerJUnitTest.NO_VALUE_PRESENT_FOR_THIS_KEY));
        RegionAPI.GetAllRequest getAllRequest = ProtobufRequestUtilities.createGetAllRequest(GetAllRequestOperationHandlerJUnitTest.TEST_REGION, testKeys);
        Result result = operationHandler.process(serializationService, getAllRequest, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        RegionAPI.GetAllResponse message = ((RegionAPI.GetAllResponse) (result.getMessage()));
        Assert.assertEquals(1, message.getEntriesCount());
        Assert.assertEquals(null, serializationService.decode(message.getEntries(0).getValue()));
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.NO_VALUE_PRESENT_FOR_THIS_KEY, message.getEntries(0).getKey().getStringResult());
        Mockito.verify(regionStub, Mockito.times(1)).get(GetAllRequestOperationHandlerJUnitTest.NO_VALUE_PRESENT_FOR_THIS_KEY);
    }

    @Test
    public void multipleKeysWhereOneThrows() throws Exception {
        Result result = operationHandler.process(serializationService, generateTestRequest(true, true), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        RegionAPI.GetAllResponse response = ((RegionAPI.GetAllResponse) (result.getMessage()));
        Assert.assertEquals(3, response.getEntriesCount());
        List<BasicTypes.Entry> entriesList = response.getEntriesList();
        Map<String, String> responseEntries = convertEntryListToMap(entriesList);
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.TEST_VALUE1, responseEntries.get(GetAllRequestOperationHandlerJUnitTest.TEST_KEY1));
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.TEST_VALUE2, responseEntries.get(GetAllRequestOperationHandlerJUnitTest.TEST_KEY2));
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.TEST_VALUE3, responseEntries.get(GetAllRequestOperationHandlerJUnitTest.TEST_KEY3));
        Assert.assertEquals(1, response.getFailuresCount());
        Assert.assertEquals(GetAllRequestOperationHandlerJUnitTest.TEST_INVALID_KEY, response.getFailures(0).getKey().getStringResult());
    }
}

