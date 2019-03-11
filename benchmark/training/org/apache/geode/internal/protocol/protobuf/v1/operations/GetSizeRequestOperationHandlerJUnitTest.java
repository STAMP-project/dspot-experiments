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


import DataPolicy.PERSISTENT_REPLICATE;
import RegionAPI.GetSizeResponse;
import Scope.DISTRIBUTED_ACK;
import java.util.Collections;
import java.util.HashSet;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.InternalCacheForClientAccess;
import org.apache.geode.internal.protocol.TestExecutionContext;
import org.apache.geode.internal.protocol.protobuf.v1.MessageUtil;
import org.apache.geode.internal.protocol.protobuf.v1.RegionAPI;
import org.apache.geode.internal.protocol.protobuf.v1.Result;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class GetSizeRequestOperationHandlerJUnitTest extends OperationHandlerJUnitTest {
    private final String TEST_REGION1 = "test region 1";

    private Region region1Stub;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void processReturnsCacheRegions() throws Exception {
        RegionAttributes regionAttributesStub = Mockito.mock(RegionAttributes.class);
        Mockito.when(cacheStub.getRegion(TEST_REGION1)).thenReturn(region1Stub);
        Mockito.when(region1Stub.getName()).thenReturn(TEST_REGION1);
        Mockito.when(region1Stub.size()).thenReturn(10);
        Mockito.when(region1Stub.getAttributes()).thenReturn(regionAttributesStub);
        Mockito.when(regionAttributesStub.getDataPolicy()).thenReturn(PERSISTENT_REPLICATE);
        Mockito.when(regionAttributesStub.getKeyConstraint()).thenReturn(String.class);
        Mockito.when(regionAttributesStub.getValueConstraint()).thenReturn(Integer.class);
        Mockito.when(regionAttributesStub.getScope()).thenReturn(DISTRIBUTED_ACK);
        Result result = operationHandler.process(serializationService, MessageUtil.makeGetSizeRequest(TEST_REGION1), TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        RegionAPI.GetSizeResponse response = ((RegionAPI.GetSizeResponse) (result.getMessage()));
        Assert.assertEquals(10, response.getSize());
    }

    @Test
    public void processReturnsNoCacheRegions() throws Exception {
        InternalCache emptyCache = Mockito.mock(InternalCacheForClientAccess.class);
        Mockito.doReturn(emptyCache).when(emptyCache).getCacheForProcessingClientRequests();
        Mockito.when(emptyCache.rootRegions()).thenReturn(Collections.unmodifiableSet(new HashSet<Region<String, String>>()));
        String unknownRegionName = "UNKNOWN_REGION";
        expectedException.expect(RegionDestroyedException.class);
        operationHandler.process(serializationService, MessageUtil.makeGetSizeRequest(unknownRegionName), TestExecutionContext.getNoAuthCacheExecutionContext(emptyCache));
    }
}

