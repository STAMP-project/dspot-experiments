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


public class ListenRemoveTest {
    private Listen<ListenRemoveTest.Person> listen;

    private MultiMap<Value, ListenRemoveTest.Person, List<ListenRemoveTest.Person>> map;

    private Collection<ListenRemoveTest.Person> all;

    private ListenRemoveTest.Person first;

    private ListenRemoveTest.Person last;

    private ListenRemoveTest.Person baby;

    private ListenRemoveTest.Person teenager;

    private ListenRemoveTest.Person grandpa;

    @Test
    public void testBeginning() throws Exception {
        map.remove(new Value(0));
        Assert.assertEquals(teenager, first);
        Assert.assertNull(last);
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testEnd() throws Exception {
        map.remove(new Value(100));
        Assert.assertNull(first);
        Assert.assertEquals(teenager, last);
        Assert.assertEquals(2, all.size());
    }

    @Test
    public void testMiddle() throws Exception {
        map.remove(new Value(15));
        Assert.assertNull(first);
        Assert.assertNull(last);
        Assert.assertEquals(2, all.size());
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

