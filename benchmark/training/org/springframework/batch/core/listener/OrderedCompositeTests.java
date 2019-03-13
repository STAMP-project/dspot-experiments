/**
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.listener;


import java.util.Arrays;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;


/**
 *
 *
 * @author Dave Syer
 */
public class OrderedCompositeTests {
    private OrderedComposite<Object> list = new OrderedComposite();

    @Test
    public void testSetItems() {
        list.setItems(Arrays.asList(new Object[]{ "1", "2" }));
        Iterator<Object> iterator = list.iterator();
        Assert.assertEquals("1", iterator.next());
        Assert.assertEquals("2", iterator.next());
    }

    @Test
    public void testSetSameObject() {
        list.setItems(Arrays.asList(new Object[]{ "1", "1" }));
        Iterator<Object> iterator = list.iterator();
        Assert.assertEquals("1", iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testAdd() {
        list.setItems(Arrays.asList(((Object) ("1"))));
        list.add("3");
        Iterator<Object> iterator = list.iterator();
        Assert.assertEquals("1", iterator.next());
        Assert.assertEquals("3", iterator.next());
    }

    @Test
    public void testAddOrdered() {
        list.setItems(Arrays.asList(((Object) ("1"))));
        list.add(new Ordered() {
            @Override
            public int getOrder() {
                return 0;
            }
        });
        Iterator<Object> iterator = list.iterator();
        iterator.next();
        Assert.assertEquals("1", iterator.next());
    }

    @Test
    public void testAddMultipleOrdered() {
        list.setItems(Arrays.asList(((Object) ("1"))));
        list.add(new Ordered() {
            @Override
            public int getOrder() {
                return 1;
            }
        });
        list.add(new Ordered() {
            @Override
            public int getOrder() {
                return 0;
            }
        });
        Iterator<Object> iterator = list.iterator();
        Assert.assertEquals(0, getOrder());
        Assert.assertEquals(1, getOrder());
        Assert.assertEquals("1", iterator.next());
    }

    @Test
    public void testAddDuplicateOrdered() {
        list.setItems(Arrays.asList(((Object) ("1"))));
        list.add(new Ordered() {
            @Override
            public int getOrder() {
                return 1;
            }
        });
        list.add(new Ordered() {
            @Override
            public int getOrder() {
                return 1;
            }
        });
        Iterator<Object> iterator = list.iterator();
        Assert.assertEquals(1, getOrder());
        Assert.assertEquals(1, getOrder());
        Assert.assertEquals("1", iterator.next());
    }

    @Test
    public void testAddAnnotationOrdered() {
        list.add(new Ordered() {
            @Override
            public int getOrder() {
                return 1;
            }
        });
        OrderedCompositeTests.OrderedObject item = new OrderedCompositeTests.OrderedObject();
        list.add(item);
        Iterator<Object> iterator = list.iterator();
        Assert.assertEquals(item, iterator.next());
    }

    @Order(0)
    private static class OrderedObject {}
}

