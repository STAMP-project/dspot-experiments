/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.util;


import io.undertow.testutils.category.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static Headers.DEFLATE;
import static Headers.HOST;


/**
 *
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Category(UnitTest.class)
public final class HeaderValuesTestCase {
    @Test
    public void testBasic() {
        final HeaderValues headerValues = new HeaderValues(DEFLATE);
        Assert.assertEquals(0, headerValues.size());
        Assert.assertTrue(headerValues.isEmpty());
        Assert.assertFalse(headerValues.iterator().hasNext());
        Assert.assertFalse(headerValues.descendingIterator().hasNext());
        Assert.assertFalse(headerValues.listIterator().hasNext());
        Assert.assertFalse(headerValues.listIterator(0).hasNext());
        Assert.assertNull(headerValues.peek());
        Assert.assertNull(headerValues.peekFirst());
        Assert.assertNull(headerValues.peekLast());
    }

    @Test
    public void testAdd() {
        HeaderValues headerValues = new HeaderValues(HOST);
        Assert.assertTrue(headerValues.add("Foo"));
        Assert.assertTrue(headerValues.contains("Foo"));
        Assert.assertTrue(headerValues.contains(new String("Foo")));
        Assert.assertFalse(headerValues.contains("Bar"));
        Assert.assertFalse(headerValues.isEmpty());
        Assert.assertEquals(1, headerValues.size());
        Assert.assertEquals("Foo", headerValues.peek());
        Assert.assertEquals("Foo", headerValues.peekFirst());
        Assert.assertEquals("Foo", headerValues.peekLast());
        Assert.assertEquals("Foo", headerValues.get(0));
        Assert.assertTrue(headerValues.offerFirst("First!"));
        Assert.assertTrue(headerValues.contains("First!"));
        Assert.assertTrue(headerValues.contains("Foo"));
        Assert.assertEquals(2, headerValues.size());
        Assert.assertEquals("First!", headerValues.peek());
        Assert.assertEquals("First!", headerValues.peekFirst());
        Assert.assertEquals("First!", headerValues.get(0));
        Assert.assertEquals("Foo", headerValues.peekLast());
        Assert.assertEquals("Foo", headerValues.get(1));
        Assert.assertTrue(headerValues.offerLast("Last!"));
        Assert.assertTrue(headerValues.contains("Last!"));
        Assert.assertTrue(headerValues.contains("Foo"));
        Assert.assertTrue(headerValues.contains("First!"));
        Assert.assertEquals(3, headerValues.size());
        Assert.assertEquals("First!", headerValues.peek());
        Assert.assertEquals("First!", headerValues.peekFirst());
        Assert.assertEquals("First!", headerValues.get(0));
        Assert.assertEquals("Foo", headerValues.get(1));
        Assert.assertEquals("Last!", headerValues.peekLast());
        Assert.assertEquals("Last!", headerValues.get(2));
    }
}

