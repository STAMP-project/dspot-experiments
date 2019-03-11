/**
 * Copyright (C) 2014-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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
package org.greenrobot.essentials.collections;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MultimapTest {
    @Parameterized.Parameter
    public AbstractMultimap<String, String, ? extends Collection<String>> multimap;

    @Test
    public void testPutElementAndGet() {
        Collection<String> collection = multimap.get("a");
        Assert.assertEquals(3, collection.size());
        if (collection instanceof List) {
            List<String> list = ((List<String>) (collection));
            Assert.assertEquals("1", list.get(0));
            Assert.assertEquals("2", list.get(1));
            Assert.assertEquals("3", list.get(2));
        }
    }

    @Test
    public void testContains() {
        Assert.assertTrue(multimap.containsElement("1"));
        Assert.assertFalse(multimap.containsElement("4"));
        Assert.assertTrue(multimap.containsElement("a", "1"));
        Assert.assertFalse(multimap.containsElement("a", "4"));
    }

    @Test
    public void testRemove() {
        Assert.assertTrue(multimap.removeElement("a", "2"));
        Assert.assertFalse(multimap.removeElement("a", "2"));
        Assert.assertTrue(multimap.removeElement("a", "1"));
        Assert.assertTrue(multimap.containsKey("a"));
        Assert.assertTrue(multimap.removeElement("a", "3"));
        Assert.assertFalse(multimap.containsKey("a"));
    }

    @Test
    public void testPutElements() {
        Collection<String> collection = new HashSet<>();
        collection.add("4");
        collection.add("5");
        Assert.assertTrue(multimap.putElements("a", collection));
        Assert.assertEquals(5, multimap.get("a").size());
        Assert.assertTrue(multimap.containsElement("a", "4"));
        Assert.assertTrue(multimap.containsElement("a", "5"));
    }

    @Test
    public void testValuesElements() {
        multimap.putElement("b", "10");
        multimap.putElement("b", "11");
        Collection<String> allStrings = multimap.valuesElements();
        Assert.assertEquals(5, allStrings.size());
        Assert.assertTrue(allStrings.contains("1"));
        Assert.assertTrue(allStrings.contains("10"));
    }

    @Test
    public void testCountElements() {
        multimap.putElement("b", "10");
        multimap.putElement("b", "11");
        Assert.assertEquals(5, multimap.countElements());
        Assert.assertEquals(3, multimap.countElements("a"));
        Assert.assertEquals(2, multimap.countElements("b"));
        Assert.assertEquals(0, multimap.countElements("c"));
    }
}

