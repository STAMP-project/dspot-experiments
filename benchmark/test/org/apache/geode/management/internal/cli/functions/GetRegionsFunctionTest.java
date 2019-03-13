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
package org.apache.geode.management.internal.cli.functions;


import java.util.Collections;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.management.internal.cli.domain.RegionInformation;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Characterization unit tests for {@code GetRegionsFunction};
 */
public class GetRegionsFunctionTest {
    @Mock
    private RegionAttributes<Object, Object> regionAttributes;

    @Mock
    private org.apache.geode.cache.Region<Object, Object> region;

    @Mock
    private FunctionContext<Void> functionContext;

    @Mock
    private ResultSender<RegionInformation[]> resultSender;

    private final GetRegionsFunction getRegionsFunction = new GetRegionsFunction();

    @Test
    public void lastResultIsNullWhenThereAreNoRegions() {
        Cache cacheFromFunctionContext = Mockito.mock(Cache.class);
        Mockito.when(cacheFromFunctionContext.rootRegions()).thenReturn(Collections.emptySet());
        Mockito.when(functionContext.getCache()).thenReturn(cacheFromFunctionContext);
        getRegionsFunction.execute(functionContext);
        Mockito.verify(resultSender).lastResult(ArgumentMatchers.isNull());
    }

    @Test
    public void lastResultHasRegionInformationForRegion() {
        String regionNameInCacheFromFunctionContext = "MyRegion";
        Cache cacheFromFunctionContext = cacheWithOneRootRegion(regionNameInCacheFromFunctionContext);
        Mockito.when(functionContext.getCache()).thenReturn(cacheFromFunctionContext);
        getRegionsFunction.execute(functionContext);
        assertThat(lastResultFrom(resultSender).getValue()).extracting(( r) -> r.getPath()).containsExactly(regionNameInCacheFromFunctionContext);
    }

    @Test
    public void getsCacheFromFunctionContext() {
        Cache cacheFromFunctionContext = Mockito.mock(Cache.class);
        Mockito.when(cacheFromFunctionContext.rootRegions()).thenReturn(Collections.emptySet());
        Mockito.when(functionContext.getCache()).thenReturn(cacheFromFunctionContext);
        getRegionsFunction.execute(functionContext);
        Mockito.verify(functionContext).getCache();
    }
}

