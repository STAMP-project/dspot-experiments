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


public class ListenAddToEmptyTest {
    private Listen<ListenAddToEmptyTest.Person> listen;

    private MultiMap<Value, ListenAddToEmptyTest.Person, List<ListenAddToEmptyTest.Person>> map;

    private Collection<ListenAddToEmptyTest.Person> all;

    private ListenAddToEmptyTest.Person first;

    private ListenAddToEmptyTest.Person last;

    @Test
    public void testEmpty() throws Exception {
        Assert.assertNull(all);
        Assert.assertNull(first);
        Assert.assertNull(last);
    }

    @Test
    public void testBeginning() throws Exception {
        final ListenAddToEmptyTest.Person baby = new ListenAddToEmptyTest.Person(0, "baby");
        map.put(new Value(0), baby);
        Assert.assertEquals(baby, first);
        Assert.assertEquals(baby, last);
        Assert.assertEquals(1, all.size());
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

