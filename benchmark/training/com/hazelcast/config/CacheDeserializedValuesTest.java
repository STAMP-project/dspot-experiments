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


import CacheDeserializedValues.ALWAYS;
import CacheDeserializedValues.INDEX_ONLY;
import CacheDeserializedValues.NEVER;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheDeserializedValuesTest extends HazelcastTestSupport {
    @Test
    public void parseString_whenNEVER() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("NEVER");
        Assert.assertEquals(NEVER, cacheDeserializedValues);
    }

    @Test
    public void parseString_whenNever() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("never");
        Assert.assertEquals(NEVER, cacheDeserializedValues);
    }

    @Test
    public void parseString_whenINDEX_ONLY() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("INDEX_ONLY");
        Assert.assertEquals(INDEX_ONLY, cacheDeserializedValues);
    }

    @Test
    public void parseString_whenINDEX_ONLY_withDash() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("INDEX-ONLY");
        Assert.assertEquals(INDEX_ONLY, cacheDeserializedValues);
    }

    @Test
    public void parseString_whenIndex_only() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("index_only");
        Assert.assertEquals(INDEX_ONLY, cacheDeserializedValues);
    }

    @Test
    public void parseString_whenIndex_only_withDash() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("index-only");
        Assert.assertEquals(INDEX_ONLY, cacheDeserializedValues);
    }

    @Test
    public void parseString_whenALWAYS() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("ALWAYS");
        Assert.assertEquals(ALWAYS, cacheDeserializedValues);
    }

    @Test
    public void parseString_whenAlways() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("always");
        Assert.assertEquals(ALWAYS, cacheDeserializedValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseString_whenUnknownString() {
        CacheDeserializedValues cacheDeserializedValues = CacheDeserializedValues.parseString("does no exist");
    }
}

