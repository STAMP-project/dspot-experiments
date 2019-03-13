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
package org.drools.verifier.core.maps;


import IndexKey.INDEX_ID;
import org.drools.verifier.core.index.keys.IndexKey;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.util.HasIndex;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Assert;
import org.junit.Test;


public class IndexedKeyTreeMapTest {
    private static final KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("name").build();

    private static final KeyDefinition AGE_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("age").updatable().build();

    private IndexedKeyTreeMap<IndexedKeyTreeMapTest.Person> map;

    private IndexedKeyTreeMapTest.Person toni;

    private IndexedKeyTreeMapTest.Person eder;

    private IndexedKeyTreeMapTest.Person michael;

    @Test
    public void testIndexOrder() throws Exception {
        Assert.assertEquals(toni, map.get(INDEX_ID).get(new Value(0)).iterator().next());
        Assert.assertEquals(eder, map.get(INDEX_ID).get(new Value(1)).iterator().next());
        Assert.assertEquals(michael, map.get(INDEX_ID).get(new Value(2)).iterator().next());
    }

    @Test
    public void testAddToMiddle() throws Exception {
        final IndexedKeyTreeMapTest.Person smurf = new IndexedKeyTreeMapTest.Person("Smurf", 55);
        map.put(smurf, 1);
        Assert.assertEquals(4, map.get(INDEX_ID).size());
        Assert.assertEquals(toni, map.get(INDEX_ID).get(new Value(0)).iterator().next());
        Assert.assertEquals(smurf, map.get(INDEX_ID).get(new Value(1)).iterator().next());
        Assert.assertEquals(eder, map.get(INDEX_ID).get(new Value(2)).iterator().next());
        Assert.assertEquals(michael, map.get(INDEX_ID).get(new Value(3)).iterator().next());
    }

    @Test
    public void testRemove() throws Exception {
        // Removing one by one to check the index stays on track.
        toni.uuidKey.retract();
        Assert.assertEquals(eder, map.get(INDEX_ID).get(new Value(0)).iterator().next());
        Assert.assertEquals(michael, map.get(INDEX_ID).get(new Value(1)).iterator().next());
        eder.uuidKey.retract();
        IndexedKeyTreeMapTest.Person next = map.get(INDEX_ID).get(new Value(0)).iterator().next();
        Assert.assertEquals(michael, next);
    }

    @Test
    public void testUpdateAge() throws Exception {
        toni.setAge(100);
        Assert.assertEquals(100, toni.getAge());
        final IndexedKeyTreeMapTest.Person person = map.get(IndexedKeyTreeMapTest.AGE_KEY_DEFINITION).get(new Value(100)).iterator().next();
        Assert.assertEquals(toni, person);
        Assert.assertEquals(100, person.getAge());
    }

    class Person implements HasIndex , HasKeys {
        private final UUIDKey uuidKey = getUUID(this);

        private UpdatableKey<IndexedKeyTreeMapTest.Person> indexKey;

        final String name;

        private UpdatableKey<IndexedKeyTreeMapTest.Person> ageKey;

        public Person(final String name, final int age) {
            this.name = name;
            ageKey = new UpdatableKey<IndexedKeyTreeMapTest.Person>(IndexedKeyTreeMapTest.AGE_KEY_DEFINITION, age);
        }

        public Key[] keys() {
            return new Key[]{ uuidKey, indexKey, new Key(IndexedKeyTreeMapTest.NAME_KEY_DEFINITION, name), ageKey };
        }

        @Override
        public int getIndex() {
            return ((int) (indexKey.getSingleValueComparator()));
        }

        @Override
        public void setIndex(final int index) {
            UpdatableKey<IndexedKeyTreeMapTest.Person> oldKey = indexKey;
            final UpdatableKey<IndexedKeyTreeMapTest.Person> newKey = new UpdatableKey(IndexKey.INDEX_ID, index);
            indexKey = newKey;
            if (oldKey != null) {
                oldKey.update(newKey, this);
            }
        }

        public int getAge() {
            return ((Integer) (ageKey.getSingleValueComparator()));
        }

        public void setAge(final int age) {
            if (ageKey.getSingleValue().equals(age)) {
                return;
            } else {
                final UpdatableKey<IndexedKeyTreeMapTest.Person> oldKey = ageKey;
                final UpdatableKey<IndexedKeyTreeMapTest.Person> newKey = new UpdatableKey(IndexedKeyTreeMapTest.AGE_KEY_DEFINITION, age);
                ageKey = newKey;
                oldKey.update(newKey, this);
            }
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}

