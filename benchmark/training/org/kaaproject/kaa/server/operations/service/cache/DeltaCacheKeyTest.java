/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.operations.service.cache;


import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;


public class DeltaCacheKeyTest {
    private static final EndpointObjectHash HASH_1 = EndpointObjectHash.fromSha1("test1");

    private static final EndpointObjectHash HASH_2 = EndpointObjectHash.fromSha1("test2");

    @Test
    public void deltaCacheKeyEqualsTest() {
        EqualsVerifier.forClass(DeltaCacheKey.class).verify();
    }

    @Test
    public void deltaSameCacheKeyTest() {
        DeltaCacheKey key1 = new DeltaCacheKey(new AppVersionKey("appId1", 1), null, null, DeltaCacheKeyTest.HASH_1, null);
        DeltaCacheKey key2 = new DeltaCacheKey(new AppVersionKey("appId1", 1), null, null, DeltaCacheKeyTest.HASH_1, null);
        Assert.assertEquals(key1, key2);
        key1 = new DeltaCacheKey(new AppVersionKey("appId1", 1), null, null, null, null);
        key2 = new DeltaCacheKey(new AppVersionKey("appId1", 1), null, null, null, null);
        Assert.assertEquals(key1, key2);
        key1 = new DeltaCacheKey(null, null, null, null, null);
        key2 = new DeltaCacheKey(null, null, null, null, null);
        Assert.assertEquals(key1, key2);
        key1 = new DeltaCacheKey(null, null, null, null, DeltaCacheKeyTest.HASH_2);
        key2 = new DeltaCacheKey(null, null, null, null, DeltaCacheKeyTest.HASH_2);
        Assert.assertEquals(key1, key2);
    }

    @Test
    public void deltaDifferentCacheKeyTest() {
        List<EndpointGroupStateDto> egsList = new ArrayList<>();
        egsList.add(new EndpointGroupStateDto("eg1", "pf1", "cf1"));
        DeltaCacheKey key1 = new DeltaCacheKey(new AppVersionKey("appId1", 1), egsList, null, DeltaCacheKeyTest.HASH_1, null);
        DeltaCacheKey key2 = new DeltaCacheKey(new AppVersionKey("appId1", 1), egsList, null, DeltaCacheKeyTest.HASH_2, null);
        Assert.assertNotEquals(key1, key2);
        key1 = new DeltaCacheKey(new AppVersionKey("appId1", 1), egsList, null, DeltaCacheKeyTest.HASH_1, DeltaCacheKeyTest.HASH_2);
        key2 = new DeltaCacheKey(new AppVersionKey("appId1", 1), egsList, null, DeltaCacheKeyTest.HASH_1, DeltaCacheKeyTest.HASH_1);
        Assert.assertNotEquals(key1, key2);
        List<EndpointGroupStateDto> egsList2 = new ArrayList<>();
        egsList2.add(new EndpointGroupStateDto("eg1", "pf1", "cf2"));
        DeltaCacheKey key3 = new DeltaCacheKey(new AppVersionKey("appId1", 1), egsList2, null, EndpointObjectHash.fromSha1("test1"), null);
        Assert.assertNotEquals(key1, key3);
        DeltaCacheKey key4 = new DeltaCacheKey(new AppVersionKey("appId2", 1), egsList, null, EndpointObjectHash.fromSha1("test1"), null);
        Assert.assertNotEquals(key1, key4);
        DeltaCacheKey key5 = new DeltaCacheKey(new AppVersionKey("appId1", 2), egsList, null, EndpointObjectHash.fromSha1("test1"), null);
        Assert.assertNotEquals(key1, key5);
    }
}

