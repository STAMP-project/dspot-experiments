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


import InitialImageOperation.RequestImageMessage;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class InitialImageOperationTest {
    private ClusterDistributionManager dm;

    private String path;

    private LocalRegion region;

    private InternalCache cache;

    @Test
    public void getsRegionFromCacheFromDM() {
        LocalRegion value = InitialImageOperation.getGIIRegion(dm, path, false);
        assertThat(value).isSameAs(region);
    }

    @Test
    public void processRequestImageMessageWillSendFailureMessageIfGotCancelException() {
        InitialImageOperation.RequestImageMessage message = Mockito.spy(new InitialImageOperation.RequestImageMessage());
        message.regionPath = "regionPath";
        Mockito.when(dm.getExistingCache()).thenThrow(new CacheClosedException());
        message.process(dm);
        Mockito.verify(message).sendFailureMessage(ArgumentMatchers.eq(dm), ArgumentMatchers.eq(null));
    }
}

