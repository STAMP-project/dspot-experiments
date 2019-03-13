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
package com.gs.collections.impl.test;


import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.MutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * JUnit test to make sure that the methods {@link Verify#assertListsEqual(String, List, List)},
 * {@link Verify#assertSetsEqual(String, Set, Set)},
 * and {@link Verify#assertMapsEqual(String, Map, Map)} really throw when they ought to.
 */
public class CollectionsEqualTest {
    private final MutableList<String> list = mList("asdf", "qwer");

    private final MutableList<String> list2 = mList("asdf", "zxcv");

    private final MutableList<String> list3 = mList("asdf");

    private final MutableSet<String> set = mSet("asdf", "qwer");

    private final MutableSet<String> set2 = mSet("asdf", "zxcv");

    private final MutableSet<String> set3 = mSet("asdf");

    private final MutableSet<String> bigSet1 = mSet("1", "2", "3", "4", "5", "6");

    private final MutableSet<String> bigSet2 = mSet("7", "8", "9", "10", "11", "12");

    private final Map<String, String> map = mMap("asdf", "asdf", "qwer", "qwer");

    private final Map<String, String> map2 = mMap("asdf", "zxcv", "qwer", "qwer");

    private final Map<String, String> map3 = mMap("zxcv", "asdf", "qwer", "qwer");

    private final Map<String, String> map4 = mMap("zxcv", "zxcv", "qwer", "qwer");

    private final Map<String, String> map5 = mMap("asdf", "asdf");

    @Test
    public void listsEqual() {
        List<?> nullList = null;
        Verify.assertListsEqual(nullList, nullList);
        Verify.assertListsEqual("assertListsEqual(nullList, nullList)", nullList, nullList);
        Verify.assertListsEqual(this.list, this.list);
        Verify.assertListsEqual("assertListsEqual(list, list)", this.list, this.list);
        try {
            Verify.assertListsEqual("assertListsEqual(nullList, list)", nullList, this.list);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertListsEqual(nullList, this.list);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertListsEqual("assertListsEqual(list, nullList)", this.list, nullList);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertListsEqual(this.list, nullList);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void setsEqual() {
        Set<?> nullSet = null;
        Verify.assertSetsEqual(nullSet, nullSet);
        Verify.assertSetsEqual("assertSetsEqual(nullSet, nullSet)", nullSet, nullSet);
        Verify.assertSetsEqual(this.set, this.set);
        Verify.assertSetsEqual("assertSetsEqual(set, set)", this.set, this.set);
        try {
            Verify.assertSetsEqual("assertSetsEqual(nullSet, set)", nullSet, this.set);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSetsEqual(nullSet, this.set);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSetsEqual("assertSetsEqual(set, nullSet)", this.set, nullSet);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSetsEqual(this.set, nullSet);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void mapsEqual() {
        Map<?, ?> nullMap = null;
        Verify.assertMapsEqual(nullMap, nullMap);
        Verify.assertMapsEqual("assertMapsEqual(nullMap, nullMap)", nullMap, nullMap);
        Verify.assertMapsEqual(this.map, this.map);
        Verify.assertMapsEqual("assertMapsEqual(map, map)", this.map, this.map);
        try {
            Verify.assertMapsEqual("assertMapsEqual(nullMap, map)", nullMap, this.map);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertMapsEqual(nullMap, this.map);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertMapsEqual("assertMapsEqual(map, nullMap)", this.map, nullMap);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertMapsEqual(this.map, nullMap);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void listsDifferentSizes() {
        try {
            Verify.assertListsEqual("assertListsEqual(list, list3)", this.list, this.list3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertListsEqual(this.list, this.list3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void differentLists() {
        try {
            Verify.assertListsEqual("assertListsEqual(list, list2)", this.list, this.list2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertListsEqual(this.list, this.list2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void setsDifferentSizes() {
        try {
            Verify.assertSetsEqual("assertSetsEqual(set, set2)", this.set, this.set2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSetsEqual(this.set, this.set2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void differentSets() {
        try {
            Verify.assertSetsEqual("assertSetsEqual(set, set3)", this.set, this.set3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertSetsEqual(this.set, this.set3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void mapsDifferentValue() {
        try {
            Verify.assertMapsEqual("assertMapsEqual(map, map2)", this.map, this.map2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertMapsEqual(this.map, this.map2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void mapsDifferentKey() {
        try {
            Verify.assertMapsEqual("assertMapsEqual(map, map3)", this.map, this.map3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertMapsEqual(this.map, this.map3);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void mapsDifferentEntry() {
        try {
            Verify.assertMapsEqual("assertMapsEqual(map, map4)", this.map, this.map4);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertMapsEqual(this.map, this.map4);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void mapsDifferentSize() {
        try {
            Verify.assertMapsEqual("assertMapsEqual(map, map5)", this.map, this.map5);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertMapsEqual(this.map, this.map5);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void bigSetsDiffer() {
        try {
            Verify.assertSetsEqual(this.bigSet1, this.bigSet2);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(CollectionsEqualTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }
}

