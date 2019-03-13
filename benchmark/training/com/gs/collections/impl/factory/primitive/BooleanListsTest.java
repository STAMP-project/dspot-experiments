/**
 * Copyright 2013 Goldman Sachs.
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
package com.gs.collections.impl.factory.primitive;


import BooleanLists.immutable;
import com.gs.collections.api.factory.list.primitive.ImmutableBooleanListFactory;
import com.gs.collections.api.list.primitive.ImmutableBooleanList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

import static BooleanLists.immutable;


public class BooleanListsTest {
    @Test
    public void immutables() {
        ImmutableBooleanListFactory listFactory = immutable;
        Assert.assertEquals(new BooleanArrayList(), listFactory.of());
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of());
        Assert.assertEquals(BooleanArrayList.newListWith(true), listFactory.of(true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false), listFactory.of(true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true), listFactory.of(true, false, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true, false), listFactory.of(true, false, true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true), listFactory.of(true, false, true, false, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false), listFactory.of(true, false, true, false, true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true), listFactory.of(true, false, true, false, true, false, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true, true), listFactory.of(true, false, true, false, true, false, true, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true, true));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true, true, true), listFactory.of(true, false, true, false, true, false, true, true, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true, true, true));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true, true, true, false), listFactory.of(true, false, true, false, true, false, true, true, true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true, true, true, false));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true), listFactory.ofAll(BooleanArrayList.newListWith(true, false, true)));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.ofAll(BooleanArrayList.newListWith(true, false, true)));
    }

    @Test
    public void emptyList() {
        Verify.assertEmpty(immutable.of());
        Assert.assertSame(immutable.of(), immutable.of());
        Verify.assertPostSerializedIdentity(immutable.of());
    }

    @Test
    public void newListWith() {
        ImmutableBooleanList list = immutable.of();
        Assert.assertEquals(list, immutable.of(list.toArray()));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(true));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(true, false));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(true, false, true));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(true, false, true, false));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(true, false, true, false, true));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(true, false, true, false, true, false));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(true, false, true, false, true, false, true));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(true, false, true, false, true, false, true, true));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(true, false, true, false, true, false, true, true, true));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(true, false, true, false, true, false, true, true, true, false));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(true, false, true, false, true, false, true, true, true, false, true));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(true, false, true, false, true, false, true, true, true, false, true, false));
    }

    @SuppressWarnings("RedundantArrayCreation")
    @Test
    public void newListWithArray() {
        ImmutableBooleanList list = immutable.of();
        Assert.assertEquals((list = list.newWith(true)), immutable.of(new boolean[]{ true }));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(new boolean[]{ true, false }));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(new boolean[]{ true, false, true }));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(new boolean[]{ true, false, true, false }));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(new boolean[]{ true, false, true, false, true }));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(new boolean[]{ true, false, true, false, true, false }));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(new boolean[]{ true, false, true, false, true, false, true }));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(new boolean[]{ true, false, true, false, true, false, true, true }));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(new boolean[]{ true, false, true, false, true, false, true, true, true }));
        Assert.assertEquals((list = list.newWith(false)), immutable.of(new boolean[]{ true, false, true, false, true, false, true, true, true, false }));
        Assert.assertEquals((list = list.newWith(true)), immutable.of(new boolean[]{ true, false, true, false, true, false, true, true, true, false, true }));
    }

    @Test
    public void newListWithList() {
        ImmutableBooleanList list = immutable.of();
        BooleanArrayList booleanArrayList = BooleanArrayList.newListWith(true);
        Assert.assertEquals((list = list.newWith(true)), booleanArrayList.toImmutable());
        Assert.assertEquals((list = list.newWith(false)), booleanArrayList.with(false).toImmutable());
        Assert.assertEquals((list = list.newWith(true)), booleanArrayList.with(true).toImmutable());
        Assert.assertEquals((list = list.newWith(false)), booleanArrayList.with(false).toImmutable());
        Assert.assertEquals((list = list.newWith(true)), booleanArrayList.with(true).toImmutable());
        Assert.assertEquals((list = list.newWith(false)), booleanArrayList.with(false).toImmutable());
        Assert.assertEquals((list = list.newWith(true)), booleanArrayList.with(true).toImmutable());
        Assert.assertEquals((list = list.newWith(true)), booleanArrayList.with(true).toImmutable());
        Assert.assertEquals((list = list.newWith(true)), booleanArrayList.with(true).toImmutable());
        Assert.assertEquals((list = list.newWith(false)), booleanArrayList.with(false).toImmutable());
        Assert.assertEquals((list = list.newWith(true)), booleanArrayList.with(true).toImmutable());
    }

    @Test
    public void newListWithWithList() {
        Assert.assertEquals(new BooleanArrayList(), immutable.ofAll(new BooleanArrayList()));
        Assert.assertEquals(BooleanArrayList.newListWith(true), immutable.ofAll(BooleanArrayList.newListWith(true)));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false), immutable.ofAll(BooleanArrayList.newListWith(true, false)));
        Assert.assertEquals(BooleanArrayList.newListWith(true, false, true), immutable.ofAll(BooleanArrayList.newListWith(true, false, true)));
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(BooleanLists.class);
    }
}

