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
package com.gs.collections.impl.set.primitive;


import BooleanSets.immutable;
import com.gs.collections.api.set.primitive.ImmutableBooleanSet;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


public class ImmutableBooleanSetFactoryImplTest {
    @Test
    public void of() {
        Verify.assertEmpty(immutable.of());
        Assert.assertEquals(BooleanHashSet.newSetWith(true).toImmutable(), immutable.of(true));
    }

    @Test
    public void with() {
        Verify.assertEmpty(immutable.with(null));
        Assert.assertEquals(BooleanHashSet.newSetWith(false).toImmutable(), immutable.with(new boolean[]{ false }));
    }

    @Test
    public void ofAll() {
        ImmutableBooleanSet set = immutable.of(true, false);
        Assert.assertEquals(BooleanHashSet.newSet(set).toImmutable(), immutable.ofAll(set));
        Assert.assertEquals(BooleanHashSet.newSet(BooleanArrayList.newListWith(true, false, true)).toImmutable(), immutable.ofAll(BooleanArrayList.newListWith(true, false)));
    }
}

