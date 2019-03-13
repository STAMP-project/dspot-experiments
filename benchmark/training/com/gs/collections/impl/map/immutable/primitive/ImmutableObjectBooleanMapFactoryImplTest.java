/**
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.map.immutable.primitive;


import ObjectBooleanMaps.immutable;
import org.junit.Assert;
import org.junit.Test;


public class ImmutableObjectBooleanMapFactoryImplTest {
    @Test
    public void of() {
        Assert.assertEquals(new com.gs.collections.impl.map.mutable.primitive.ObjectBooleanHashMap<String>().toImmutable(), immutable.of());
        Assert.assertEquals(com.gs.collections.impl.map.mutable.primitive.ObjectBooleanHashMap.newWithKeysValues("1", true).toImmutable(), immutable.of("1", true));
    }

    @Test
    public void with() {
        Assert.assertEquals(com.gs.collections.impl.map.mutable.primitive.ObjectBooleanHashMap.newWithKeysValues("1", true).toImmutable(), immutable.with("1", true));
    }

    @Test
    public void ofAll() {
        Assert.assertEquals(new com.gs.collections.impl.map.mutable.primitive.ObjectBooleanHashMap().toImmutable(), immutable.ofAll(immutable.of()));
    }

    @Test
    public void withAll() {
        Assert.assertEquals(new com.gs.collections.impl.map.mutable.primitive.ObjectBooleanHashMap().toImmutable(), immutable.withAll(immutable.of()));
    }
}

