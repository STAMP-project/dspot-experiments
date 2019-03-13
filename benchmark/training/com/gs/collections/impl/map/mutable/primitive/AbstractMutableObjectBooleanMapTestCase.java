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
package com.gs.collections.impl.map.mutable.primitive;


import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.BooleanFunction0;
import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.api.map.primitive.MutableObjectBooleanMap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractMutableObjectBooleanMapTestCase extends AbstractObjectBooleanMapTestCase {
    protected final MutableObjectBooleanMap<String> map = this.classUnderTest();

    @Test
    public void clear() {
        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        hashMap.put("0", true);
        hashMap.clear();
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), hashMap);
        hashMap.put("1", false);
        hashMap.clear();
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), hashMap);
        hashMap.put(null, true);
        hashMap.clear();
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), hashMap);
    }

    @Test
    public void removeKey() {
        MutableObjectBooleanMap<String> map0 = this.newWithKeysValues("0", true, "1", false);
        map0.removeKey("1");
        Assert.assertEquals(this.newWithKeysValues("0", true), map0);
        map0.removeKey("0");
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), map0);
        MutableObjectBooleanMap<String> map1 = this.newWithKeysValues("0", false, "1", true);
        map1.removeKey("0");
        Assert.assertEquals(this.newWithKeysValues("1", true), map1);
        map1.removeKey("1");
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), map1);
        this.map.removeKey("5");
        Assert.assertEquals(this.newWithKeysValues("0", true, "1", true, "2", false), this.map);
        this.map.removeKey("0");
        Assert.assertEquals(this.newWithKeysValues("1", true, "2", false), this.map);
        this.map.removeKey("1");
        Assert.assertEquals(this.newWithKeysValues("2", false), this.map);
        this.map.removeKey("2");
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        this.map.removeKey("0");
        this.map.removeKey("1");
        this.map.removeKey("2");
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        Verify.assertEmpty(this.map);
        this.map.put(null, true);
        Assert.assertTrue(this.map.get(null));
        this.map.removeKey(null);
        Assert.assertFalse(this.map.get(null));
    }

    @Test
    public void put() {
        this.map.put("0", false);
        this.map.put("1", false);
        this.map.put("2", true);
        ObjectBooleanHashMap<String> expected = ObjectBooleanHashMap.newWithKeysValues("0", false, "1", false, "2", true);
        Assert.assertEquals(expected, this.map);
        this.map.put("5", true);
        expected.put("5", true);
        Assert.assertEquals(expected, this.map);
        this.map.put(null, false);
        expected.put(null, false);
        Assert.assertEquals(expected, this.map);
    }

    @Test
    public void putDuplicateWithRemovedSlot() {
        String collision1 = AbstractMutableObjectBooleanMapTestCase.generateCollisions().getFirst();
        String collision2 = AbstractMutableObjectBooleanMapTestCase.generateCollisions().get(1);
        String collision3 = AbstractMutableObjectBooleanMapTestCase.generateCollisions().get(2);
        String collision4 = AbstractMutableObjectBooleanMapTestCase.generateCollisions().get(3);
        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        hashMap.put(collision1, true);
        hashMap.put(collision2, false);
        hashMap.put(collision3, true);
        Assert.assertFalse(hashMap.get(collision2));
        hashMap.removeKey(collision2);
        hashMap.put(collision4, false);
        Assert.assertEquals(this.newWithKeysValues(collision1, true, collision3, true, collision4, false), hashMap);
        MutableObjectBooleanMap<String> hashMap1 = this.getEmptyMap();
        hashMap1.put(collision1, false);
        hashMap1.put(collision2, false);
        hashMap1.put(collision3, true);
        Assert.assertFalse(hashMap1.get(collision1));
        hashMap1.removeKey(collision1);
        hashMap1.put(collision4, true);
        Assert.assertEquals(this.newWithKeysValues(collision2, false, collision3, true, collision4, true), hashMap1);
        MutableObjectBooleanMap<String> hashMap2 = this.getEmptyMap();
        hashMap2.put(collision1, true);
        hashMap2.put(collision2, true);
        hashMap2.put(collision3, false);
        Assert.assertFalse(hashMap2.get(collision3));
        hashMap2.removeKey(collision3);
        hashMap2.put(collision4, false);
        Assert.assertEquals(this.newWithKeysValues(collision1, true, collision2, true, collision4, false), hashMap2);
        MutableObjectBooleanMap<String> hashMap3 = this.getEmptyMap();
        hashMap3.put(collision1, true);
        hashMap3.put(collision2, true);
        hashMap3.put(collision3, false);
        Assert.assertTrue(hashMap3.get(collision2));
        Assert.assertFalse(hashMap3.get(collision3));
        hashMap3.removeKey(collision2);
        hashMap3.removeKey(collision3);
        hashMap3.put(collision4, false);
        Assert.assertEquals(this.newWithKeysValues(collision1, true, collision4, false), hashMap3);
        MutableObjectBooleanMap<String> hashMap4 = this.getEmptyMap();
        hashMap4.put(null, false);
        Assert.assertEquals(this.newWithKeysValues(null, false), hashMap4);
        hashMap4.put(null, true);
        Assert.assertEquals(this.newWithKeysValues(null, true), hashMap4);
    }

    @Test
    public void getIfAbsentPut_Function() {
        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        Assert.assertTrue(map1.getIfAbsentPut(0, () -> true));
        BooleanFunction0 factoryThrows = () -> {
            throw new AssertionError();
        };
        Assert.assertTrue(map1.getIfAbsentPut(0, factoryThrows));
        Assert.assertEquals(this.newWithKeysValues(0, true), map1);
        Assert.assertTrue(map1.getIfAbsentPut(1, () -> true));
        Assert.assertTrue(map1.getIfAbsentPut(1, factoryThrows));
        Assert.assertEquals(this.newWithKeysValues(0, true, 1, true), map1);
        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        Assert.assertFalse(map2.getIfAbsentPut(1, () -> false));
        Assert.assertFalse(map2.getIfAbsentPut(1, factoryThrows));
        Assert.assertEquals(this.newWithKeysValues(1, false), map2);
        Assert.assertFalse(map2.getIfAbsentPut(0, () -> false));
        Assert.assertFalse(map2.getIfAbsentPut(0, factoryThrows));
        Assert.assertEquals(this.newWithKeysValues(0, false, 1, false), map2);
        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        Assert.assertTrue(map3.getIfAbsentPut(null, () -> true));
        Assert.assertTrue(map3.getIfAbsentPut(null, factoryThrows));
        Assert.assertEquals(this.newWithKeysValues(null, true), map3);
    }

    @Test
    public void getIfAbsentPutWith() {
        BooleanFunction<String> functionLengthEven = ( string) -> ((string.length()) & 1) == 0;
        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        Assert.assertFalse(map1.getIfAbsentPutWith(0, functionLengthEven, "123456789"));
        BooleanFunction<String> functionThrows = ( string) -> {
            throw new AssertionError();
        };
        Assert.assertFalse(map1.getIfAbsentPutWith(0, functionThrows, "unused"));
        Assert.assertEquals(this.newWithKeysValues(0, false), map1);
        Assert.assertFalse(map1.getIfAbsentPutWith(1, functionLengthEven, "123456789"));
        Assert.assertFalse(map1.getIfAbsentPutWith(1, functionThrows, "unused"));
        Assert.assertEquals(this.newWithKeysValues(0, false, 1, false), map1);
        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        Assert.assertTrue(map2.getIfAbsentPutWith(1, functionLengthEven, "1234567890"));
        Assert.assertTrue(map2.getIfAbsentPutWith(1, functionThrows, "unused0"));
        Assert.assertEquals(this.newWithKeysValues(1, true), map2);
        Assert.assertTrue(map2.getIfAbsentPutWith(0, functionLengthEven, "1234567890"));
        Assert.assertTrue(map2.getIfAbsentPutWith(0, functionThrows, "unused0"));
        Assert.assertEquals(this.newWithKeysValues(0, true, 1, true), map2);
        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        Assert.assertFalse(map3.getIfAbsentPutWith(null, functionLengthEven, "123456789"));
        Assert.assertFalse(map3.getIfAbsentPutWith(null, functionThrows, "unused"));
        Assert.assertEquals(this.newWithKeysValues(null, false), map3);
    }

    @Test
    public void getIfAbsentPutWithKey() {
        BooleanFunction<Integer> function = ( anObject) -> (anObject == null) || ((anObject & 1) == 0);
        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        Assert.assertTrue(map1.getIfAbsentPutWithKey(0, function));
        BooleanFunction<Integer> functionThrows = ( anObject) -> {
            throw new AssertionError();
        };
        Assert.assertTrue(map1.getIfAbsentPutWithKey(0, functionThrows));
        Assert.assertEquals(this.newWithKeysValues(0, true), map1);
        Assert.assertFalse(map1.getIfAbsentPutWithKey(1, function));
        Assert.assertFalse(map1.getIfAbsentPutWithKey(1, functionThrows));
        Assert.assertEquals(this.newWithKeysValues(0, true, 1, false), map1);
        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        Assert.assertFalse(map2.getIfAbsentPutWithKey(1, function));
        Assert.assertFalse(map2.getIfAbsentPutWithKey(1, functionThrows));
        Assert.assertEquals(this.newWithKeysValues(1, false), map2);
        Assert.assertTrue(map2.getIfAbsentPutWithKey(0, function));
        Assert.assertTrue(map2.getIfAbsentPutWithKey(0, functionThrows));
        Assert.assertEquals(this.newWithKeysValues(0, true, 1, false), map2);
        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        Assert.assertTrue(map3.getIfAbsentPutWithKey(null, function));
        Assert.assertTrue(map3.getIfAbsentPutWithKey(null, functionThrows));
        Assert.assertEquals(this.newWithKeysValues(null, true), map3);
    }

    @Test
    public void withKeysValues() {
        MutableObjectBooleanMap<Integer> emptyMap = this.getEmptyMap();
        MutableObjectBooleanMap<Integer> hashMap = emptyMap.withKeyValue(1, true);
        Assert.assertEquals(this.newWithKeysValues(1, true), hashMap);
        Assert.assertSame(emptyMap, hashMap);
    }

    @Test
    public void withoutKey() {
        MutableObjectBooleanMap<Integer> hashMap = this.newWithKeysValues(1, true, 2, true, 3, false, 4, false);
        MutableObjectBooleanMap<Integer> actual = hashMap.withoutKey(5);
        Assert.assertSame(hashMap, actual);
        Assert.assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false, 4, false), actual);
        Assert.assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false), hashMap.withoutKey(4));
        Assert.assertEquals(this.newWithKeysValues(1, true, 2, true), hashMap.withoutKey(3));
        Assert.assertEquals(this.newWithKeysValues(1, true), hashMap.withoutKey(2));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutKey(1));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutKey(1));
    }

    @Test
    public void withoutAllKeys() {
        MutableObjectBooleanMap<Integer> hashMap = this.newWithKeysValues(1, true, 2, true, 3, false, 4, false);
        MutableObjectBooleanMap<Integer> actual = hashMap.withoutAllKeys(FastList.newListWith(5, 6, 7));
        Assert.assertSame(hashMap, actual);
        Assert.assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false, 4, false), actual);
        Assert.assertEquals(this.newWithKeysValues(1, true, 2, true), hashMap.withoutAllKeys(FastList.newListWith(5, 4, 3)));
        Assert.assertEquals(this.newWithKeysValues(1, true), hashMap.withoutAllKeys(FastList.newListWith(2)));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutAllKeys(FastList.newListWith(1)));
        Assert.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutAllKeys(FastList.newListWith(5, 6)));
    }

    @Test
    public void asUnmodifiable() {
        Verify.assertInstanceOf(UnmodifiableObjectBooleanMap.class, this.map.asUnmodifiable());
        Assert.assertEquals(new UnmodifiableObjectBooleanMap(this.map), this.map.asUnmodifiable());
    }

    @Test
    public void asSynchronized() {
        Verify.assertInstanceOf(SynchronizedObjectBooleanMap.class, this.map.asSynchronized());
        Assert.assertEquals(new SynchronizedObjectBooleanMap(this.map), this.map.asSynchronized());
    }

    @Test
    public void iterator_remove() {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        Verify.assertNotEmpty(map);
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        while (booleanIterator.hasNext()) {
            booleanIterator.next();
            booleanIterator.remove();
        } 
        Verify.assertEmpty(map);
    }

    @Test
    public void iterator_throws_on_consecutive_invocation_of_remove() {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        Verify.assertNotEmpty(map);
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        Assert.assertTrue(booleanIterator.hasNext());
        booleanIterator.next();
        booleanIterator.remove();
        Verify.assertThrows(IllegalStateException.class, booleanIterator::remove);
    }

    @Test
    public void iterator_throws_on_invocation_of_remove_before_next() {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        Assert.assertTrue(booleanIterator.hasNext());
        Verify.assertThrows(IllegalStateException.class, booleanIterator::remove);
    }
}

