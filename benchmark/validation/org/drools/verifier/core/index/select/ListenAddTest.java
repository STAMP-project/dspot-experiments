/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.index.select;


import java.util.Collection;
import java.util.List;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.MultiMap;
import org.junit.Assert;
import org.junit.Test;


public class ListenAddTest {
    private Listen<ListenAddTest.Person> listen;

    private MultiMap<Value, ListenAddTest.Person, List<ListenAddTest.Person>> map;

    private Collection<ListenAddTest.Person> all;

    private ListenAddTest.Person first;

    private ListenAddTest.Person last;

    @Test
    public void testEmpty() throws Exception {
        Assert.assertNull(all);
        Assert.assertNull(first);
        Assert.assertNull(last);
    }

    @Test
    public void testBeginning() throws Exception {
        final ListenAddTest.Person baby = new ListenAddTest.Person(0, "baby");
        map.put(new Value(0), baby);
        Assert.assertEquals(baby, first);
        Assert.assertNull(last);
        Assert.assertEquals(3, all.size());
    }

    @Test
    public void testEnd() throws Exception {
        final ListenAddTest.Person grandpa = new ListenAddTest.Person(100, "grandpa");
        map.put(new Value(100), grandpa);
        Assert.assertNull(first);
        Assert.assertEquals(grandpa, last);
        Assert.assertEquals(3, all.size());
    }

    @Test
    public void testMiddle() throws Exception {
        map.put(new Value(15), new ListenAddTest.Person(15, "teenager"));
        Assert.assertNull(first);
        Assert.assertNull(last);
        Assert.assertEquals(3, all.size());
    }

    class Person {
        int age;

        String name;

        public Person(final int age, final String name) {
            this.age = age;
            this.name = name;
        }
    }
}

