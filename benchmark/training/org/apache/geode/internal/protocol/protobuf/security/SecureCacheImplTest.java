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
package org.apache.geode.internal.protocol.protobuf.security;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.Struct;
import org.apache.geode.cache.query.internal.DefaultQuery;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.apache.geode.cache.query.internal.types.ObjectTypeImpl;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class SecureCacheImplTest {
    public static final String REGION = "TestRegion";

    private SecureCacheImpl authorizingCache;

    private InternalCache cache;

    private Security security;

    private Region region;

    @Test
    public void getAllSuccesses() {
        authorize(DATA, READ, SecureCacheImplTest.REGION, "a");
        authorize(DATA, READ, SecureCacheImplTest.REGION, "b");
        Map<Object, Object> okValues = new HashMap<>();
        Map<Object, Exception> exceptionValues = new HashMap<>();
        Mockito.when(region.get("b")).thenReturn("existing value");
        authorizingCache.getAll(SecureCacheImplTest.REGION, Arrays.asList("a", "b"), okValues::put, exceptionValues::put);
        Mockito.verify(region).get("a");
        Mockito.verify(region).get("b");
        assertThat(okValues).containsOnly(entry("a", null), entry("b", "existing value"));
        assertThat(exceptionValues).isEmpty();
    }

    @Test
    public void getAllWithRegionLevelAuthorizationSucceeds() {
        authorize(DATA, READ, SecureCacheImplTest.REGION, ALL);
        Map<Object, Object> okValues = new HashMap<>();
        Map<Object, Exception> exceptionValues = new HashMap<>();
        Mockito.when(region.get("b")).thenReturn("existing value");
        authorizingCache.getAll(SecureCacheImplTest.REGION, Arrays.asList("a", "b"), okValues::put, exceptionValues::put);
        Mockito.verify(region).get("a");
        Mockito.verify(region).get("b");
        assertThat(okValues).containsOnly(entry("a", null), entry("b", "existing value"));
        assertThat(exceptionValues).isEmpty();
    }

    @Test
    public void getAllWithFailure() {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, "b");
        Map<Object, Object> okValues = new HashMap<>();
        Map<Object, Exception> exceptionValues = new HashMap<>();
        Mockito.when(region.get("b")).thenReturn("existing value");
        authorizingCache.getAll(SecureCacheImplTest.REGION, Arrays.asList("a", "b"), okValues::put, exceptionValues::put);
        Mockito.verify(region).get("b");
        Mockito.verifyNoMoreInteractions(region);
        assertThat(okValues).containsOnly(entry("b", "existing value"));
        assertThat(exceptionValues).containsOnlyKeys("a");
        assertThat(exceptionValues.values().iterator().next()).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getAllIsPostProcessed() {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, "a");
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, "b");
        Mockito.when(security.postProcess(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("spam");
        Map<Object, Object> okValues = new HashMap<>();
        Map<Object, Exception> exceptionValues = new HashMap<>();
        Mockito.when(region.get("b")).thenReturn("existing value");
        authorizingCache.getAll(SecureCacheImplTest.REGION, Arrays.asList("a", "b"), okValues::put, exceptionValues::put);
        Mockito.verify(region).get("a");
        Mockito.verify(region).get("b");
        assertThat(okValues).containsOnly(entry("a", "spam"), entry("b", "spam"));
        assertThat(exceptionValues).isEmpty();
    }

    @Test
    public void get() {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, "a");
        Mockito.when(region.get("a")).thenReturn("value");
        Assert.assertEquals("value", authorizingCache.get(SecureCacheImplTest.REGION, "a"));
    }

    @Test
    public void getWithFailure() {
        assertThatThrownBy(() -> authorizingCache.get(REGION, "a")).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getIsPostProcessed() {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, "a");
        Mockito.when(security.postProcess(SecureCacheImplTest.REGION, "a", "value")).thenReturn("spam");
        Mockito.when(region.get("a")).thenReturn("value");
        Assert.assertEquals("spam", authorizingCache.get(SecureCacheImplTest.REGION, "a"));
    }

    @Test
    public void put() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        authorizingCache.put(SecureCacheImplTest.REGION, "a", "value");
        Mockito.verify(region).put("a", "value");
    }

    @Test
    public void putWithFailure() {
        assertThatThrownBy(() -> authorizingCache.put(REGION, "a", "value")).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void putAll() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "c");
        Map<Object, Object> entries = new HashMap<>();
        entries.put("a", "b");
        entries.put("c", "d");
        Map<Object, Exception> exceptionValues = new HashMap<>();
        authorizingCache.putAll(SecureCacheImplTest.REGION, entries, exceptionValues::put);
        Mockito.verify(region).put("a", "b");
        Mockito.verify(region).put("c", "d");
        assertThat(exceptionValues).isEmpty();
    }

    @Test
    public void putAllWithRegionLevelAuthorizationSucceeds() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, ALL);
        Map<Object, Object> entries = new HashMap<>();
        entries.put("a", "b");
        entries.put("c", "d");
        Map<Object, Exception> exceptionValues = new HashMap<>();
        authorizingCache.putAll(SecureCacheImplTest.REGION, entries, exceptionValues::put);
        Mockito.verify(region).put("a", "b");
        Mockito.verify(region).put("c", "d");
        assertThat(exceptionValues).isEmpty();
    }

    @Test
    public void putAllWithFailure() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        Map<Object, Object> entries = new HashMap<>();
        entries.put("a", "b");
        entries.put("c", "d");
        Map<Object, Exception> exceptionValues = new HashMap<>();
        authorizingCache.putAll(SecureCacheImplTest.REGION, entries, exceptionValues::put);
        Mockito.verify(security).authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        Mockito.verify(security).authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "c");
        Mockito.verify(region).put("a", "b");
        Mockito.verifyNoMoreInteractions(region);
        assertThat(exceptionValues).containsOnlyKeys("c");
    }

    @Test
    public void remove() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        authorizingCache.remove(SecureCacheImplTest.REGION, "a");
        Mockito.verify(region).remove("a");
    }

    @Test
    public void removeWithoutAuthorization() {
        assertThatThrownBy(() -> authorizingCache.remove(REGION, "a")).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void removeIsPostProcessed() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        Mockito.when(region.remove("a")).thenReturn("value");
        Mockito.when(security.postProcess(SecureCacheImplTest.REGION, "a", "value")).thenReturn("spam");
        Object value = authorizingCache.remove(SecureCacheImplTest.REGION, "a");
        Mockito.verify(region).remove("a");
        Assert.assertEquals("spam", value);
    }

    @Test
    public void getRegionNames() {
        authorize(Resource.DATA, Operation.READ, ALL, ALL);
        Set<Region<?, ?>> regions = new HashSet<>();
        regions.add(region);
        Mockito.when(cache.rootRegions()).thenReturn(regions);
        Set subregions = new HashSet<>();
        Region region2 = Mockito.mock(Region.class);
        subregions.add(region2);
        Region region3 = Mockito.mock(Region.class);
        subregions.add(region3);
        Mockito.when(region.getFullPath()).thenReturn("region1");
        Mockito.when(region2.getFullPath()).thenReturn("region2");
        Mockito.when(region3.getFullPath()).thenReturn("region3");
        Mockito.when(region.subregions(true)).thenReturn(subregions);
        Collection<String> regionNames = authorizingCache.getRegionNames();
        assertThat(regionNames).containsExactly("region1", "region2", "region3");
        Mockito.verify(cache).rootRegions();
    }

    @Test
    public void getRegionNamesWithoutAuthorization() {
        assertThatThrownBy(() -> authorizingCache.getRegionNames()).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void getSize() {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, ALL);
        authorizingCache.getSize(SecureCacheImplTest.REGION);
        Mockito.verify(region).size();
    }

    @Test
    public void getSizeWithoutAuthorization() {
        assertThatThrownBy(() -> authorizingCache.getSize(REGION)).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void keySet() {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, ALL);
        authorizingCache.keySet(SecureCacheImplTest.REGION);
        Mockito.verify(region).keySet();
    }

    @Test
    public void keySetWithoutAuthorization() {
        assertThatThrownBy(() -> authorizingCache.keySet(REGION)).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void clear() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, ALL);
        authorizingCache.clear(SecureCacheImplTest.REGION);
        Mockito.verify(region).clear();
    }

    @Test
    public void clearWithoutAuthorization() {
        assertThatThrownBy(() -> authorizingCache.clear(REGION)).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void putIfAbsent() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        String oldValue = authorizingCache.putIfAbsent(SecureCacheImplTest.REGION, "a", "b");
        Mockito.verify(region).putIfAbsent("a", "b");
    }

    @Test
    public void putIfAbsentIsPostProcessed() {
        authorize(Resource.DATA, Operation.WRITE, SecureCacheImplTest.REGION, "a");
        Mockito.when(region.putIfAbsent(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("value");
        Mockito.when(security.postProcess(SecureCacheImplTest.REGION, "a", "value")).thenReturn("spam");
        String oldValue = authorizingCache.putIfAbsent(SecureCacheImplTest.REGION, "a", "b");
        Mockito.verify(region).putIfAbsent("a", "b");
        Assert.assertEquals("spam", oldValue);
    }

    @Test
    public void putIfAbsentWithoutAuthorization() {
        assertThatThrownBy(() -> authorizingCache.putIfAbsent(REGION, "a", "b")).isInstanceOf(NotAuthorizedException.class);
    }

    @Test
    public void query() throws Exception {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, ALL);
        mockQuery();
        String queryString = "select * from /region";
        Object[] bindParameters = new Object[]{ "a" };
        authorizingCache.query(queryString, bindParameters);
    }

    @Test
    public void queryIsPostProcessedWithSingleResultValue() throws Exception {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, ALL);
        Mockito.when(security.postProcess(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("spam");
        DefaultQuery query = mockQuery();
        String queryString = "select * from /region";
        Object[] bindParameters = new Object[]{ "a" };
        Mockito.when(query.execute(bindParameters)).thenReturn("value");
        Object result = authorizingCache.query(queryString, bindParameters);
        Assert.assertEquals("spam", result);
    }

    @Test
    public void queryIsPostProcessedWithListOfObjectValues() throws Exception {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, ALL);
        Mockito.when(security.postProcess(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("spam");
        DefaultQuery query = mockQuery();
        String queryString = "select * from /region";
        Object[] bindParameters = new Object[]{ "a" };
        SelectResults results = new ResultsBag();
        results.setElementType(new ObjectTypeImpl(Object.class));
        results.add("value1");
        results.add("value2");
        Mockito.when(query.execute(((Object[]) (ArgumentMatchers.any())))).thenReturn(results);
        SelectResults<String> result = ((SelectResults<String>) (authorizingCache.query(queryString, bindParameters)));
        Assert.assertEquals(Arrays.asList("spam", "spam"), result.asList());
    }

    @Test
    public void queryIsPostProcessedWithListOfStructValues() throws Exception {
        authorize(Resource.DATA, Operation.READ, SecureCacheImplTest.REGION, ALL);
        Mockito.when(security.postProcess(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("spam");
        SelectResults<Struct> results = buildListOfStructs("value1", "value2");
        DefaultQuery query = mockQuery();
        String queryString = "select * from /region";
        Object[] bindParameters = new Object[]{ "a" };
        Mockito.when(query.execute(((Object[]) (ArgumentMatchers.any())))).thenReturn(results);
        SelectResults<String> result = ((SelectResults<String>) (authorizingCache.query(queryString, bindParameters)));
        Assert.assertEquals(buildListOfStructs("spam", "spam").asList(), result.asList());
    }
}

