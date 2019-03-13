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
package com.hazelcast.query.impl;


import IndexMatchHint.NONE;
import IndexMatchHint.PREFER_ORDERED;
import IndexMatchHint.PREFER_UNORDERED;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AttributeIndexRegistryTest {
    private AttributeIndexRegistry registry;

    @Test
    public void testNonCompositeIndexes() {
        InternalIndex orderedA = AttributeIndexRegistryTest.index(true, "a");
        registry.register(orderedA);
        Assert.assertSame(orderedA, registry.match("a", NONE));
        Assert.assertSame(orderedA, registry.match("a", PREFER_ORDERED));
        Assert.assertSame(orderedA, registry.match("a", PREFER_UNORDERED));
        Assert.assertNull(registry.match("unknown", NONE));
        InternalIndex unorderedB = AttributeIndexRegistryTest.index(false, "b");
        registry.register(unorderedB);
        Assert.assertSame(orderedA, registry.match("a", NONE));
        Assert.assertSame(orderedA, registry.match("a", PREFER_ORDERED));
        Assert.assertSame(orderedA, registry.match("a", PREFER_UNORDERED));
        Assert.assertSame(unorderedB, registry.match("b", NONE));
        Assert.assertSame(unorderedB, registry.match("b", PREFER_ORDERED));
        Assert.assertSame(unorderedB, registry.match("b", PREFER_UNORDERED));
        Assert.assertNull(registry.match("unknown", NONE));
        InternalIndex unorderedA = AttributeIndexRegistryTest.index(false, "a");
        registry.register(unorderedA);
        MatcherAssert.assertThat(registry.match("a", NONE), Matchers.anyOf(Matchers.sameInstance(orderedA), Matchers.sameInstance(unorderedA)));
        Assert.assertSame(orderedA, registry.match("a", PREFER_ORDERED));
        Assert.assertSame(unorderedA, registry.match("a", PREFER_UNORDERED));
        Assert.assertSame(unorderedB, registry.match("b", NONE));
        Assert.assertSame(unorderedB, registry.match("b", PREFER_ORDERED));
        Assert.assertSame(unorderedB, registry.match("b", PREFER_UNORDERED));
        Assert.assertNull(registry.match("unknown", NONE));
        registry.clear();
        Assert.assertNull(registry.match("a", NONE));
        Assert.assertNull(registry.match("b", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
    }

    @Test
    public void testCompositeIndexes() {
        InternalIndex orderedA12 = AttributeIndexRegistryTest.index(true, "a1", "a2");
        registry.register(orderedA12);
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", NONE)));
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", PREFER_ORDERED)));
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", PREFER_UNORDERED)));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
        InternalIndex unorderedB12 = AttributeIndexRegistryTest.index(false, "b1", "b2");
        registry.register(unorderedB12);
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", NONE)));
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", PREFER_ORDERED)));
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", PREFER_UNORDERED)));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("b1", NONE));
        Assert.assertNull(registry.match("b1", PREFER_ORDERED));
        Assert.assertNull(registry.match("b1", PREFER_UNORDERED));
        Assert.assertNull(registry.match("b2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
        InternalIndex unorderedA12 = AttributeIndexRegistryTest.index(false, "a1", "a2");
        registry.register(unorderedA12);
        MatcherAssert.assertThat(AttributeIndexRegistryTest.undecorated(registry.match("a1", NONE)), Matchers.anyOf(Matchers.sameInstance(orderedA12), Matchers.sameInstance(unorderedA12)));
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", PREFER_ORDERED)));
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", PREFER_UNORDERED)));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("b1", NONE));
        Assert.assertNull(registry.match("b1", PREFER_ORDERED));
        Assert.assertNull(registry.match("b1", PREFER_UNORDERED));
        Assert.assertNull(registry.match("b2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
        registry.clear();
        Assert.assertNull(registry.match("a1", NONE));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("b1", NONE));
        Assert.assertNull(registry.match("b2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
    }

    @Test
    public void testCompositeAndNonCompositeIndexes() {
        InternalIndex unorderedA1 = AttributeIndexRegistryTest.index(false, "a1");
        registry.register(unorderedA1);
        Assert.assertSame(unorderedA1, registry.match("a1", NONE));
        Assert.assertSame(unorderedA1, registry.match("a1", PREFER_ORDERED));
        Assert.assertSame(unorderedA1, registry.match("a1", PREFER_UNORDERED));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
        InternalIndex unorderedB12 = AttributeIndexRegistryTest.index(false, "b1", "b2");
        registry.register(unorderedB12);
        Assert.assertSame(unorderedA1, registry.match("a1", NONE));
        Assert.assertSame(unorderedA1, registry.match("a1", PREFER_ORDERED));
        Assert.assertSame(unorderedA1, registry.match("a1", PREFER_UNORDERED));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("b1", NONE));
        Assert.assertNull(registry.match("b1", PREFER_ORDERED));
        Assert.assertNull(registry.match("b1", PREFER_UNORDERED));
        Assert.assertNull(registry.match("b2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
        InternalIndex orderedA12 = AttributeIndexRegistryTest.index(true, "a1", "a2");
        registry.register(orderedA12);
        MatcherAssert.assertThat(AttributeIndexRegistryTest.undecorated(registry.match("a1", NONE)), Matchers.anyOf(Matchers.sameInstance(unorderedA1), Matchers.sameInstance(orderedA12)));
        Assert.assertSame(orderedA12, AttributeIndexRegistryTest.undecorated(registry.match("a1", PREFER_ORDERED)));
        Assert.assertSame(unorderedA1, registry.match("a1", PREFER_UNORDERED));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("b1", NONE));
        Assert.assertNull(registry.match("b1", PREFER_ORDERED));
        Assert.assertNull(registry.match("b1", PREFER_UNORDERED));
        Assert.assertNull(registry.match("b2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
        registry.clear();
        Assert.assertNull(registry.match("a1", NONE));
        Assert.assertNull(registry.match("a2", NONE));
        Assert.assertNull(registry.match("b1", NONE));
        Assert.assertNull(registry.match("b2", NONE));
        Assert.assertNull(registry.match("unknown", NONE));
    }

    @Test
    public void testNonCompositeIndexesArePreferredOverComposite() {
        InternalIndex a12 = AttributeIndexRegistryTest.index(true, "a1", "a2");
        registry.register(a12);
        Assert.assertSame(a12, AttributeIndexRegistryTest.undecorated(registry.match("a1", NONE)));
        InternalIndex a1 = AttributeIndexRegistryTest.index(true, "a1");
        registry.register(a1);
        Assert.assertSame(a1, registry.match("a1", NONE));
    }

    @Test
    public void testShorterCompositeIndexesArePreferredOverLonger() {
        InternalIndex a123 = AttributeIndexRegistryTest.index(true, "a1", "a2", "a3");
        registry.register(a123);
        Assert.assertSame(a123, AttributeIndexRegistryTest.undecorated(registry.match("a1", NONE)));
        InternalIndex a12 = AttributeIndexRegistryTest.index(true, "a1", "a2");
        registry.register(a12);
        Assert.assertSame(a12, AttributeIndexRegistryTest.undecorated(registry.match("a1", NONE)));
    }
}

