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
package org.apache.geode.internal.cache;


import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.internal.cache.InitialImageOperation.RequestFilterInfoMessage;
import org.junit.Test;
import org.mockito.Mockito;


public class RequestFilterInfoMessageTest {
    private ClusterDistributionManager dm;

    private InternalCache cache;

    private String path;

    private LocalRegion region;

    @Test
    public void shouldBeMockable() throws Exception {
        RequestFilterInfoMessage mockRequestFilterInfoMessage = Mockito.mock(RequestFilterInfoMessage.class);
        Mockito.when(mockRequestFilterInfoMessage.getProcessorType()).thenReturn(1);
        assertThat(mockRequestFilterInfoMessage.getProcessorType()).isEqualTo(1);
    }

    @Test
    public void getsRegionFromCacheFromDM() {
        RequestFilterInfoMessage message = new RequestFilterInfoMessage();
        message.regionPath = path;
        message.process(dm);
        Mockito.verify(dm, Mockito.times(1)).getCache();
        Mockito.verify(cache, Mockito.times(1)).getRegionByPath(path);
    }
}

