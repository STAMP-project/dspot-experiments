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
import BasicTypes.KeyedError;
import RegionAPI.PutAllRequest;
import RegionAPI.PutAllResponse;
import java.util.HashSet;
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
import org.apache.geode.internal.protocol.protobuf.v1.utilities.ProtobufUtilities;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class PutAllRequestOperationHandlerJUnitTest extends OperationHandlerJUnitTest {
    private final String TEST_KEY1 = "my key1";

    private final String TEST_KEY2 = "my key2";

    private final String TEST_KEY3 = "my key3";

    private final String TEST_INVALID_KEY = "invalid key";

    private final String TEST_VALUE1 = "my value1";

    private final String TEST_VALUE2 = "my value2";

    private final String TEST_VALUE3 = "my value3";

    private final Integer TEST_INVALID_VALUE = 732;

    private final String TEST_REGION = "test region";

    private final String EXCEPTION_TEXT = "Simulating put failure";

    private Region regionMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void processReturnsErrorUnableToDecodeRequest() throws Exception {
        Exception exception = new DecodingException("error finding codec for type");
        ProtobufSerializationService serializationServiceStub = Mockito.mock(ProtobufSerializationService.class);
        Mockito.when(serializationServiceStub.decode(ArgumentMatchers.any())).thenReturn(TEST_KEY1, TEST_VALUE1).thenThrow(exception);
        Mockito.when(serializationServiceStub.encode(ArgumentMatchers.any())).thenReturn(EncodedValue.newBuilder().setStringResult("some string").build());
        BasicTypes.EncodedValue encodedObject1 = EncodedValue.newBuilder().setStringResult(TEST_KEY1).build();
        BasicTypes.EncodedValue encodedObject2 = EncodedValue.newBuilder().setStringResult(TEST_KEY2).build();
        Set<BasicTypes.Entry> entries = new HashSet<>();
        entries.add(ProtobufUtilities.createEntry(encodedObject1, encodedObject1));
        entries.add(ProtobufUtilities.createEntry(encodedObject2, encodedObject2));
        RegionAPI.PutAllRequest putAllRequest = ProtobufRequestUtilities.createPutAllRequest(TEST_REGION, entries).getPutAllRequest();
        expectedException.expect(DecodingException.class);
        Result response = operationHandler.process(serializationServiceStub, putAllRequest, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
    }

    @Test
    public void processInsertsMultipleValidEntriesInCache() throws Exception {
        Result result = operationHandler.process(serializationService, generateTestRequest(false, true), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        Mockito.verify(regionMock).put(TEST_KEY1, TEST_VALUE1);
        Mockito.verify(regionMock).put(TEST_KEY2, TEST_VALUE2);
        Mockito.verify(regionMock).put(TEST_KEY3, TEST_VALUE3);
    }

    @Test
    public void processWithInvalidEntrySucceedsAndReturnsFailedKey() throws Exception {
        Result result = operationHandler.process(serializationService, generateTestRequest(true, true), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        Mockito.verify(regionMock).put(TEST_KEY1, TEST_VALUE1);
        Mockito.verify(regionMock).put(TEST_KEY2, TEST_VALUE2);
        Mockito.verify(regionMock).put(TEST_KEY3, TEST_VALUE3);
        RegionAPI.PutAllResponse putAllResponse = ((RegionAPI.PutAllResponse) (result.getMessage()));
        Assert.assertEquals(1, putAllResponse.getFailedKeysCount());
        BasicTypes.KeyedError error = putAllResponse.getFailedKeys(0);
        Assert.assertEquals(TEST_INVALID_KEY, serializationService.decode(error.getKey()));
    }

    @Test
    public void processWithNoEntriesPasses() throws Exception {
        Result result = operationHandler.process(serializationService, generateTestRequest(false, false), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertTrue((result instanceof Success));
        Mockito.verify(regionMock, Mockito.times(0)).put(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

