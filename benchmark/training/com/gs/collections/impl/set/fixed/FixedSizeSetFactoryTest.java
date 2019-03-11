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
package com.gs.collections.impl.set.fixed;


import Lists.mutable;
import Sets.fixedSize;
import com.gs.collections.api.factory.set.FixedSizeSetFactory;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.FixedSizeSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.Procedures2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Key;
import org.junit.Assert;
import org.junit.Test;


public class FixedSizeSetFactoryTest {
    private FixedSizeSetFactory setFactory;

    @Test
    public void testCreateWith3Args() {
        this.assertCreateSet(this.setFactory.of("a", "a"), "a");
        this.assertCreateSet(this.setFactory.of("a", "a", "c"), "a", "c");
        this.assertCreateSet(this.setFactory.of("a", "b", "a"), "a", "b");
        this.assertCreateSet(this.setFactory.of("a", "b", "b"), "a", "b");
    }

    @Test
    public void testCreateWith4Args() {
        this.assertCreateSet(this.setFactory.of("a", "a", "c", "d"), "a", "c", "d");
        this.assertCreateSet(this.setFactory.of("a", "b", "a", "d"), "a", "b", "d");
        this.assertCreateSet(this.setFactory.of("a", "b", "c", "a"), "a", "b", "c");
        this.assertCreateSet(this.setFactory.of("a", "b", "b", "d"), "a", "b", "d");
        this.assertCreateSet(this.setFactory.of("a", "b", "c", "b"), "a", "b", "c");
        this.assertCreateSet(this.setFactory.of("a", "b", "c", "c"), "a", "b", "c");
    }

    @Test
    public void keyPreservation() {
        Key key = new Key("key");
        Key duplicateKey1 = new Key("key");
        MutableSet<Key> set1 = this.setFactory.of(key, duplicateKey1);
        Verify.assertSize(1, set1);
        Verify.assertContains(key, set1);
        Assert.assertSame(key, set1.getFirst());
        Key duplicateKey2 = new Key("key");
        MutableSet<Key> set2 = this.setFactory.of(key, duplicateKey1, duplicateKey2);
        Verify.assertSize(1, set2);
        Verify.assertContains(key, set2);
        Assert.assertSame(key, set1.getFirst());
        Key duplicateKey3 = new Key("key");
        MutableSet<Key> set3 = this.setFactory.of(key, new Key("not a dupe"), duplicateKey3);
        Verify.assertSize(2, set3);
        Verify.assertContainsAll(set3, key, new Key("not a dupe"));
        Assert.assertSame(key, set3.detect(key::equals));
        Key duplicateKey4 = new Key("key");
        MutableSet<Key> set4 = this.setFactory.of(key, new Key("not a dupe"), duplicateKey3, duplicateKey4);
        Verify.assertSize(2, set4);
        Verify.assertContainsAll(set4, key, new Key("not a dupe"));
        Assert.assertSame(key, set4.detect(key::equals));
        MutableSet<Key> set5 = this.setFactory.of(key, new Key("not a dupe"), new Key("me neither"), duplicateKey4);
        Verify.assertSize(3, set5);
        Verify.assertContainsAll(set5, key, new Key("not a dupe"), new Key("me neither"));
        Assert.assertSame(key, set5.detect(key::equals));
        MutableSet<Key> set6 = this.setFactory.of(key, duplicateKey2, duplicateKey3, duplicateKey4);
        Verify.assertSize(1, set6);
        Verify.assertContains(key, set6);
        Assert.assertSame(key, set6.detect(key::equals));
    }

    @Test
    public void create1() {
        FixedSizeSet<String> set = fixedSize.of("1");
        Verify.assertSize(1, set);
        Verify.assertContains("1", set);
    }

    @Test
    public void create2() {
        FixedSizeSet<String> set = fixedSize.of("1", "2");
        Assert.assertEquals(UnifiedSet.newSetWith("1", "2"), set);
    }

    @Test
    public void create3() {
        FixedSizeSet<String> set = fixedSize.of("1", "2", "3");
        Assert.assertEquals(UnifiedSet.newSetWith("1", "2", "3"), set);
    }

    @Test
    public void create4() {
        FixedSizeSet<String> set = fixedSize.of("1", "2", "3", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("1", "2", "3", "4"), set);
    }

    @Test
    public void createWithDuplicates() {
        FixedSizeSet<String> set1 = fixedSize.of("1", "1");
        Assert.assertEquals(UnifiedSet.newSetWith("1"), set1);
        FixedSizeSet<String> set2 = fixedSize.of("1", "1", "1");
        Assert.assertEquals(UnifiedSet.newSetWith("1"), set2);
        FixedSizeSet<String> set3 = fixedSize.of("2", "3", "2");
        Assert.assertEquals(UnifiedSet.newSetWith("2", "3"), set3);
        FixedSizeSet<String> set4 = fixedSize.of("3", "4", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("3", "4"), set4);
        FixedSizeSet<String> set5 = fixedSize.of("4", "4", "4", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("4"), set5);
        FixedSizeSet<String> set6 = fixedSize.of("4", "3", "4", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("4", "3"), set6);
        FixedSizeSet<String> set7 = fixedSize.of("4", "2", "3", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set7);
        FixedSizeSet<String> set8 = fixedSize.of("2", "3", "4", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set8);
        FixedSizeSet<String> set9 = fixedSize.of("2", "4", "3", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set9);
        FixedSizeSet<String> set10 = fixedSize.of("2", "4", "3", "4");
        Assert.assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set10);
        FixedSizeSet<String> set11 = fixedSize.of("4", "3", "4", "2");
        Assert.assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set11);
        FixedSizeSet<String> set12 = fixedSize.of("3", "4", "4", "2");
        Assert.assertEquals(UnifiedSet.newSetWith("4", "3", "2"), set12);
    }

    @Test
    public void createSet() {
        MutableSet<String> set1 = fixedSize.of();
        Verify.assertEmpty(set1);
        MutableSet<String> set2 = fixedSize.of();
        Verify.assertEmpty(set2);
        Assert.assertSame(fixedSize.of(), fixedSize.of());
    }

    @Test
    public void forEach() {
        MutableList<String> result = mutable.of();
        MutableSet<String> source = fixedSize.of("1", "2", "3", "4");
        source.forEach(CollectionAddProcedure.on(result));
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void forEachWithIndex() {
        int[] indexSum = new int[1];
        MutableList<String> result = mutable.of();
        MutableSet<String> source = fixedSize.of("1", "2", "3", "4");
        source.forEachWithIndex(( each, index) -> {
            result.add(each);
            indexSum[0] += index;
        });
        Assert.assertEquals(6, indexSum[0]);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void forEachWith() {
        MutableList<String> result = mutable.of();
        MutableSet<String> source = fixedSize.of("1", "2", "3", "4");
        source.forEachWith(Procedures2.fromProcedure(CollectionAddProcedure.on(result)), null);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4"), result);
    }

    @Test
    public void ofAllSizeZero() {
        MutableSet<Integer> set = fixedSize.ofAll(FastList.<Integer>newList());
        Assert.assertEquals(UnifiedSet.<Integer>newSetWith(), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeOne() {
        MutableSet<Integer> set = fixedSize.ofAll(FastList.newListWith(1));
        Assert.assertEquals(UnifiedSet.newSetWith(1), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeTwo() {
        MutableSet<Integer> set = fixedSize.ofAll(FastList.newListWith(1, 2));
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeThree() {
        MutableSet<Integer> set = fixedSize.ofAll(FastList.newListWith(1, 2, 3));
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeFour() {
        MutableSet<Integer> set = fixedSize.ofAll(FastList.newListWith(1, 2, 3, 4));
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), set);
        Verify.assertInstanceOf(FixedSizeSet.class, set);
    }

    @Test
    public void ofAllSizeFive() {
        MutableSet<Integer> set = fixedSize.ofAll(FastList.newListWith(1, 2, 3, 4, 5));
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5), set);
        Verify.assertInstanceOf(UnifiedSet.class, set);
    }
}

