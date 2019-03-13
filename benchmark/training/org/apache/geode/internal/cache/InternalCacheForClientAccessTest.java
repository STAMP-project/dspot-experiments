/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geode.internal.cache;


import java.util.Set;
import org.apache.geode.cache.Cache;
import org.apache.geode.security.NotAuthorizedException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class InternalCacheForClientAccessTest {
    private InternalCache delegate;

    private InternalCacheForClientAccess cache;

    private DistributedRegion secretRegion;

    private DistributedRegion applicationRegion;

    private Set applicationRegions;

    private Set secretRegions;

    @Test
    public void getRegionWithApplicationWorks() {
        Mockito.when(delegate.getRegion("application")).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.getRegion("application");
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void getRegionWithSecretThrows() {
        Mockito.when(delegate.getRegion("secret")).thenReturn(secretRegion);
        assertThatThrownBy(() -> {
            cache.getRegion("secret");
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getFilterProfileWithApplicationWorks() {
        Mockito.when(delegate.getRegion("application", true)).thenReturn(applicationRegion);
        FilterProfile filterProfile = Mockito.mock(FilterProfile.class);
        Mockito.when(applicationRegion.getFilterProfile()).thenReturn(filterProfile);
        FilterProfile result = cache.getFilterProfile("application");
        assertThat(result).isSameAs(filterProfile);
    }

    @Test
    public void getFilterProfileWithSecretThrows() {
        Mockito.when(delegate.getRegion("secret", true)).thenReturn(secretRegion);
        assertThatThrownBy(() -> {
            cache.getFilterProfile("secret");
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getRegionBooleanWithApplicationWorks() {
        Mockito.when(delegate.getRegion("application", true)).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.getRegion("application", true);
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void getRegionBooleanWithSecretThrows() {
        Mockito.when(delegate.getRegion("secret", false)).thenReturn(secretRegion);
        assertThatThrownBy(() -> {
            cache.getRegion("secret", false);
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getReinitializingRegionWithApplicationWorks() {
        Mockito.when(delegate.getReinitializingRegion("application")).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.getReinitializingRegion("application");
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void getReinitializingRegionWithSecretThrows() {
        Mockito.when(delegate.getReinitializingRegion("secret")).thenReturn(secretRegion);
        assertThatThrownBy(() -> {
            cache.getReinitializingRegion("secret");
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getRegionByPathWithApplicationWorks() {
        Mockito.when(delegate.getRegionByPath("application")).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.getRegionByPath("application");
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void getRegionByPathWithSecretThrows() {
        Mockito.when(delegate.getRegionByPath("secret")).thenReturn(secretRegion);
        assertThatThrownBy(() -> {
            cache.getRegionByPath("secret");
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getRegionByPathForProcessingWithApplicationWorks() {
        Mockito.when(delegate.getRegionByPathForProcessing("application")).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.getRegionByPathForProcessing("application");
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void getRegionByPathForProcessingWithSecretThrows() {
        Mockito.when(delegate.getRegionByPathForProcessing("secret")).thenReturn(secretRegion);
        assertThatThrownBy(() -> {
            cache.getRegionByPathForProcessing("secret");
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getRegionInDestroyWithApplicationWorks() {
        Mockito.when(delegate.getRegionInDestroy("application")).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.getRegionInDestroy("application");
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void getRegionInDestroyWithSecretThrows() {
        Mockito.when(delegate.getRegionInDestroy("secret")).thenReturn(secretRegion);
        assertThatThrownBy(() -> {
            cache.getRegionInDestroy("secret");
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getPartitionedRegionsWithApplicationWorks() {
        Mockito.when(delegate.getPartitionedRegions()).thenReturn(applicationRegions);
        Set result = cache.getPartitionedRegions();
        assertThat(result).isSameAs(applicationRegions);
    }

    @Test
    public void getPartitionedRegionsWithSecretThrows() {
        Mockito.when(delegate.getPartitionedRegions()).thenReturn(secretRegions);
        assertThatThrownBy(() -> {
            cache.getPartitionedRegions();
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void rootRegionsWithApplicationWorks() {
        Mockito.when(delegate.rootRegions()).thenReturn(applicationRegions);
        Set result = cache.rootRegions();
        assertThat(result).isSameAs(applicationRegions);
    }

    @Test
    public void rootRegionsWithSecretThrows() {
        Mockito.when(delegate.rootRegions()).thenReturn(secretRegions);
        assertThatThrownBy(() -> {
            cache.rootRegions();
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void rootRegionsWithParameterWithApplicationWorks() {
        Mockito.when(delegate.rootRegions(true)).thenReturn(applicationRegions);
        Set result = cache.rootRegions(true);
        assertThat(result).isSameAs(applicationRegions);
    }

    @Test
    public void rootRegionsWithParameterWithSecretThrows() {
        Mockito.when(delegate.rootRegions(true)).thenReturn(secretRegions);
        assertThatThrownBy(() -> {
            cache.rootRegions(true);
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getAllRegionsWithApplicationWorks() {
        Mockito.when(delegate.getAllRegions()).thenReturn(applicationRegions);
        Set result = cache.getAllRegions();
        assertThat(result).isSameAs(applicationRegions);
    }

    @Test
    public void getAllRegionsWithSecretThrows() {
        Mockito.when(delegate.getAllRegions()).thenReturn(secretRegions);
        assertThatThrownBy(() -> {
            cache.getAllRegions();
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void createVMRegionWithNullArgsApplicationWorks() throws Exception {
        Mockito.when(delegate.createVMRegion(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.createVMRegion(null, null, null);
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void createVMRegionWithDefaultArgsApplicationWorks() throws Exception {
        Mockito.when(delegate.createVMRegion(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(applicationRegion);
        org.apache.geode.cache.Region result = cache.createVMRegion(null, null, new InternalRegionArguments());
        assertThat(result).isSameAs(applicationRegion);
    }

    @Test
    public void createVMRegionWithInternalRegionThrows() throws Exception {
        assertThatThrownBy(() -> {
            cache.createVMRegion(null, null, new InternalRegionArguments().setInternalRegion(true));
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void createVMRegionWithUsedForPartitionedRegionBucketThrows() throws Exception {
        assertThatThrownBy(() -> {
            cache.createVMRegion(null, null, new InternalRegionArguments().setPartitionedRegionBucketRedundancy(0));
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void createVMRegionWithUsedForMetaRegionThrows() throws Exception {
        assertThatThrownBy(() -> {
            cache.createVMRegion(null, null, new InternalRegionArguments().setIsUsedForMetaRegion(true));
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void createVMRegionWithUsedForSerialGatewaySenderQueueThrows() throws Exception {
        assertThatThrownBy(() -> {
            cache.createVMRegion(null, null, new InternalRegionArguments().setIsUsedForSerialGatewaySenderQueue(true));
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void createVMRegionWithUsedForParallelGatewaySenderQueueThrows() throws Exception {
        assertThatThrownBy(() -> {
            cache.createVMRegion(null, null, new InternalRegionArguments().setIsUsedForParallelGatewaySenderQueue(true));
        }).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getReconnectedCacheReturnsNull() throws Exception {
        Mockito.when(delegate.getReconnectedCache()).thenReturn(null);
        Cache result = cache.getReconnectedCache();
        assertThat(result).isNull();
    }

    @Test
    public void getReconnectedCacheReturnsWrappedCache() throws Exception {
        Mockito.when(delegate.getReconnectedCache()).thenReturn(delegate);
        Cache result = cache.getReconnectedCache();
        assertThat(result).isInstanceOf(InternalCacheForClientAccess.class);
    }
}

