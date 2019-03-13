/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WanReplicationRefTest {
    private WanReplicationRef ref = new WanReplicationRef();

    @Test
    public void testConstructor_withParameters() {
        ref = new WanReplicationRef("myRef", "myMergePolicy", Collections.singletonList("myFilter"), true);
        Assert.assertEquals("myRef", ref.getName());
        Assert.assertEquals("myMergePolicy", ref.getMergePolicy());
        Assert.assertEquals(1, ref.getFilters().size());
        Assert.assertEquals("myFilter", ref.getFilters().get(0));
        Assert.assertTrue(ref.isRepublishingEnabled());
    }

    @Test
    public void testConstructor_withWanReplicationRef() {
        WanReplicationRef original = new WanReplicationRef("myRef", "myMergePolicy", Collections.singletonList("myFilter"), true);
        ref = new WanReplicationRef(original);
        Assert.assertEquals(original.getName(), ref.getName());
        Assert.assertEquals(original.getMergePolicy(), ref.getMergePolicy());
        Assert.assertEquals(original.getFilters(), ref.getFilters());
        Assert.assertEquals(original.isRepublishingEnabled(), ref.isRepublishingEnabled());
    }

    @Test
    public void testSetFilters() {
        List<String> filters = new ArrayList<String>();
        filters.add("myFilter1");
        filters.add("myFilter2");
        ref.setFilters(filters);
        Assert.assertEquals(2, ref.getFilters().size());
        Assert.assertEquals("myFilter1", ref.getFilters().get(0));
        Assert.assertEquals("myFilter2", ref.getFilters().get(1));
    }

    @Test
    public void testSerialization() {
        ref.setName("myWanReplicationRef");
        ref.setMergePolicy("myMergePolicy");
        ref.setRepublishingEnabled(true);
        ref.addFilter("myFilter");
        SerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        Data serialized = serializationService.toData(ref);
        WanReplicationRef deserialized = serializationService.toObject(serialized);
        Assert.assertEquals(ref.getName(), deserialized.getName());
        Assert.assertEquals(ref.getMergePolicy(), deserialized.getMergePolicy());
        Assert.assertEquals(ref.isRepublishingEnabled(), deserialized.isRepublishingEnabled());
        Assert.assertEquals(ref.getFilters(), deserialized.getFilters());
        Assert.assertEquals(ref.toString(), deserialized.toString());
    }
}

