/**
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
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
package io.objectbox.query;


import TestEntity_.simpleInt;
import io.objectbox.AbstractObjectBoxTest;
import io.objectbox.TestEntity;
import io.objectbox.exception.DbException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class LazyListTest extends AbstractObjectBoxTest {
    @Test
    public void testSizeAndGetAndPeak() {
        List<TestEntity> list = putTestEntities(2);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazyCached();
        Assert.assertEquals(list.size(), listLazy.size());
        Assert.assertNull(listLazy.peek(0));
        Assert.assertNull(listLazy.peek(1));
        Assert.assertNotNull(listLazy.get(1));
        Assert.assertNull(listLazy.peek(0));
        Assert.assertNotNull(listLazy.peek(1));
        Assert.assertNotNull(listLazy.get(0));
        Assert.assertNotNull(listLazy.peek(0));
        Assert.assertNotNull(listLazy.peek(1));
    }

    @Test
    public void testGetAll100() {
        List<TestEntity> list = putTestEntities(100);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazyCached();
        assertIds(list, listLazy);
    }

    @Test
    public void testGetAll100Uncached() {
        List<TestEntity> list = putTestEntities(100);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazy();
        assertIds(list, listLazy);
    }

    @Test
    public void testSublist() {
        List<TestEntity> list = putTestEntities(10);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazyCached();
        assertIds(list.subList(2, 7), listLazy.subList(2, 7));
    }

    @Test
    public void testSublistUncached() {
        List<TestEntity> list = putTestEntities(10);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazy();
        try {
            assertIds(list.subList(2, 7), listLazy.subList(2, 7));
        } catch (DbException e) {
            Assert.assertEquals("This operation only works with cached lazy lists", e.getMessage());
        }
    }

    @Test
    public void testIterator() {
        List<TestEntity> list = putTestEntities(100);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazyCached();
        testIterator(list, listLazy, false);
    }

    @Test
    public void testIteratorUncached() {
        List<TestEntity> list = putTestEntities(100);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazy();
        testIterator(list, listLazy, true);
    }

    @Test
    public void testEmpty() {
        putTestEntities(1);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().equal(simpleInt, (-1)).build().findLazyCached();
        Assert.assertTrue(listLazy.isEmpty());
        try {
            listLazy.get(0);
            Assert.fail("Not empty");
        } catch (RuntimeException e) {
            // Expected, OK
        }
    }

    @Test
    public void testUncached() {
        putTestEntities(1);
        LazyList<TestEntity> listLazy = getTestEntityBox().query().build().findLazy();
        Assert.assertFalse(listLazy.isEmpty());
        TestEntity entity1 = listLazy.get(0);
        TestEntity entity2 = listLazy.get(0);
        Assert.assertEquals(entity1.getId(), entity2.getId());
        try {
            listLazy.loadRemaining();
            Assert.fail("Not empty");
        } catch (DbException expected) {
            // Expected, OK
        }
    }
}

