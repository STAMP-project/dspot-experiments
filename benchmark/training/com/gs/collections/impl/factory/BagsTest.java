/**
 * Copyright 2011 Goldman Sachs.
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
package com.gs.collections.impl.factory;


import Bags.immutable;
import com.gs.collections.api.bag.ImmutableBag;
import com.gs.collections.api.factory.bag.ImmutableBagFactory;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

import static Bags.immutable;


public class BagsTest {
    @Test
    public void immutables() {
        ImmutableBagFactory bagFactory = immutable;
        Assert.assertEquals(HashBag.newBag(), bagFactory.of());
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of());
        Assert.assertEquals(HashBag.newBagWith(1), bagFactory.of(1));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1));
        Assert.assertEquals(HashBag.newBagWith(1, 2), bagFactory.of(1, 2));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3), bagFactory.of(1, 2, 3));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4), bagFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5), bagFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6), bagFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7), bagFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8), bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8, 9), bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assert.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assert.assertEquals(HashBag.newBagWith(3, 2, 1), bagFactory.ofAll(HashBag.newBagWith(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.ofAll(HashBag.newBagWith(1, 2, 3)));
    }

    @Test
    public void emptyBag() {
        Assert.assertTrue(immutable.of().isEmpty());
    }

    @Test
    public void newBagWith() {
        ImmutableBag<String> bag = immutable.of();
        Assert.assertEquals(bag, immutable.of(bag.toArray()));
        Assert.assertEquals((bag = bag.newWith("1")), immutable.of("1"));
        Assert.assertEquals((bag = bag.newWith("2")), immutable.of("1", "2"));
        Assert.assertEquals((bag = bag.newWith("3")), immutable.of("1", "2", "3"));
        Assert.assertEquals((bag = bag.newWith("4")), immutable.of("1", "2", "3", "4"));
        Assert.assertEquals((bag = bag.newWith("5")), immutable.of("1", "2", "3", "4", "5"));
        Assert.assertEquals((bag = bag.newWith("6")), immutable.of("1", "2", "3", "4", "5", "6"));
        Assert.assertEquals((bag = bag.newWith("7")), immutable.of("1", "2", "3", "4", "5", "6", "7"));
        Assert.assertEquals((bag = bag.newWith("8")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8"));
        Assert.assertEquals((bag = bag.newWith("9")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        Assert.assertEquals((bag = bag.newWith("10")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        Assert.assertEquals((bag = bag.newWith("11")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        Assert.assertEquals((bag = bag.newWith("12")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
    }

    @SuppressWarnings("RedundantArrayCreation")
    @Test
    public void newBagWithArray() {
        ImmutableBag<String> bag = immutable.of();
        Assert.assertEquals((bag = bag.newWith("1")), immutable.of(new String[]{ "1" }));
        Assert.assertEquals((bag = bag.newWith("2")), immutable.of(new String[]{ "1", "2" }));
        Assert.assertEquals((bag = bag.newWith("3")), immutable.of(new String[]{ "1", "2", "3" }));
        Assert.assertEquals((bag = bag.newWith("4")), immutable.of(new String[]{ "1", "2", "3", "4" }));
        Assert.assertEquals((bag = bag.newWith("5")), immutable.of(new String[]{ "1", "2", "3", "4", "5" }));
        Assert.assertEquals((bag = bag.newWith("6")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6" }));
        Assert.assertEquals((bag = bag.newWith("7")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7" }));
        Assert.assertEquals((bag = bag.newWith("8")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8" }));
        Assert.assertEquals((bag = bag.newWith("9")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        Assert.assertEquals((bag = bag.newWith("10")), immutable.of(new String[]{ "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        Assert.assertEquals((bag = bag.newWith("11")), immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
    }

    @Test
    public void newBagWithBag() {
        ImmutableBag<String> bag = immutable.of();
        HashBag<String> hashBag = HashBag.newBagWith("1");
        Assert.assertEquals((bag = bag.newWith("1")), hashBag.toImmutable());
        hashBag.add("2");
        Assert.assertEquals((bag = bag.newWith("2")), hashBag.toImmutable());
        hashBag.add("3");
        Assert.assertEquals((bag = bag.newWith("3")), hashBag.toImmutable());
        hashBag.add("4");
        Assert.assertEquals((bag = bag.newWith("4")), hashBag.toImmutable());
        hashBag.add("5");
        Assert.assertEquals((bag = bag.newWith("5")), hashBag.toImmutable());
        hashBag.add("6");
        Assert.assertEquals((bag = bag.newWith("6")), hashBag.toImmutable());
        hashBag.add("7");
        Assert.assertEquals((bag = bag.newWith("7")), hashBag.toImmutable());
        hashBag.add("8");
        Assert.assertEquals((bag = bag.newWith("8")), hashBag.toImmutable());
        hashBag.add("9");
        Assert.assertEquals((bag = bag.newWith("9")), hashBag.toImmutable());
        hashBag.add("10");
        Assert.assertEquals((bag = bag.newWith("10")), hashBag.toImmutable());
        hashBag.add("11");
        Assert.assertEquals((bag = bag.newWith("11")), hashBag.toImmutable());
    }

    @Test
    public void classIsNonInstantiable() {
        Verify.assertClassNonInstantiable(Bags.class);
    }
}

