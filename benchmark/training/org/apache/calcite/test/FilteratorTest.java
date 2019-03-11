/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.calcite.util.Filterator;
import org.apache.calcite.util.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link Filterator}.
 */
public class FilteratorTest {
    // ~ Methods ----------------------------------------------------------------
    @Test
    public void testOne() {
        final List<String> tomDickHarry = Arrays.asList("tom", "dick", "harry");
        final Filterator<String> filterator = new Filterator<String>(tomDickHarry.iterator(), String.class);
        // call hasNext twice
        Assert.assertTrue(filterator.hasNext());
        Assert.assertTrue(filterator.hasNext());
        Assert.assertEquals("tom", filterator.next());
        // call next without calling hasNext
        Assert.assertEquals("dick", filterator.next());
        Assert.assertTrue(filterator.hasNext());
        Assert.assertEquals("harry", filterator.next());
        Assert.assertFalse(filterator.hasNext());
        Assert.assertFalse(filterator.hasNext());
    }

    @Test
    public void testNulls() {
        // Nulls don't cause an error - but are not emitted, because they
        // fail the instanceof test.
        final List<String> tomDickHarry = Arrays.asList("paul", null, "ringo");
        final Filterator<String> filterator = new Filterator<String>(tomDickHarry.iterator(), String.class);
        Assert.assertEquals("paul", filterator.next());
        Assert.assertEquals("ringo", filterator.next());
        Assert.assertFalse(filterator.hasNext());
    }

    @Test
    public void testSubtypes() {
        final ArrayList arrayList = new ArrayList();
        final HashSet hashSet = new HashSet();
        final LinkedList linkedList = new LinkedList();
        Collection[] collections = new Collection[]{ null, arrayList, hashSet, linkedList, null };
        final Filterator<List> filterator = new Filterator<List>(Arrays.asList(collections).iterator(), List.class);
        Assert.assertTrue(filterator.hasNext());
        // skips null
        Assert.assertTrue((arrayList == (filterator.next())));
        // skips the HashSet
        Assert.assertTrue((linkedList == (filterator.next())));
        Assert.assertFalse(filterator.hasNext());
    }

    @Test
    public void testBox() {
        final Number[] numbers = new Number[]{ 1, 2, 3.14, 4, null, 6.0E23 };
        List<Integer> result = new ArrayList<Integer>();
        for (int i : Util.filter(Arrays.asList(numbers), Integer.class)) {
            result.add(i);
        }
        Assert.assertEquals("[1, 2, 4]", result.toString());
    }
}

/**
 * End FilteratorTest.java
 */
