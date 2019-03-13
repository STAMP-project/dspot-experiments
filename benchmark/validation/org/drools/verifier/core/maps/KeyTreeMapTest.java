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


import UUIDKey.UNIQUE_UUID;
import java.util.List;
import org.drools.verifier.core.Util;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class KeyTreeMapTest {
    private final KeyDefinition AGE = KeyDefinition.newKeyDefinition().updatable().withId("age").build();

    private final KeyDefinition NAME = KeyDefinition.newKeyDefinition().withId("name").build();

    private KeyTreeMap<KeyTreeMapTest.Person> map;

    private KeyTreeMapTest.Person toni;

    private KeyTreeMapTest.Person eder;

    private KeyTreeMapTest.Person michael;

    private AnalyzerConfiguration configuration;

    @Test
    public void testFindByUUID() throws Exception {
        Util.assertMapContent(map.get(UNIQUE_UUID), toni.uuidKey, eder.uuidKey, michael.uuidKey);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReAdd() throws Exception {
        put(toni);
    }

    @Test
    public void testFindByName() throws Exception {
        Util.assertMapContent(map.get(KeyDefinition.newKeyDefinition().withId("name").build()), "Toni", "Eder", "Michael");
    }

    @Test
    public void testFindByAge() throws Exception {
        final MultiMap<Value, KeyTreeMapTest.Person, List<KeyTreeMapTest.Person>> age = map.get(KeyDefinition.newKeyDefinition().withId("age").build());
        Util.assertMapContent(age, 20, 20, 30);
        Assert.assertTrue(age.get(new Value(20)).contains(toni));
        Assert.assertTrue(age.get(new Value(20)).contains(eder));
    }

    @Test
    public void testUpdateAge() throws Exception {
        final MultiMapChangeHandler changeHandler = Mockito.mock(MultiMapChangeHandler.class);
        ((ChangeHandledMultiMap) (map.get(AGE))).addChangeListener(changeHandler);
        toni.setAge(10);
        final MultiMap<Value, KeyTreeMapTest.Person, List<KeyTreeMapTest.Person>> age = map.get(AGE);
        Assert.assertFalse(age.get(new Value(20)).contains(toni));
        Assert.assertTrue(age.get(new Value(10)).contains(toni));
    }

    @Test
    public void testRetract() throws Exception {
        toni.uuidKey.retract();
        Util.assertMapContent(map.get(UNIQUE_UUID), eder.uuidKey, michael.uuidKey);
        Util.assertMapContent(map.get(KeyDefinition.newKeyDefinition().withId("name").build()), "Eder", "Michael");
        Util.assertMapContent(map.get(KeyDefinition.newKeyDefinition().withId("age").build()), 20, 30);
    }

    @Test
    public void testRemoveWhenItemDoesNotExist() throws Exception {
        final UUIDKey uuidKey = Mockito.mock(UUIDKey.class);
        Mockito.when(uuidKey.getKeyDefinition()).thenReturn(UNIQUE_UUID);
        Mockito.when(uuidKey.getSingleValue()).thenReturn(new Value("DoesNotExist"));
        Assert.assertNull(map.remove(uuidKey));
        Assert.assertEquals(3, map.get(UNIQUE_UUID).size());
    }

    class Person implements HasKeys {
        final String name;

        private final UUIDKey uuidKey = configuration.getUUID(this);

        private UpdatableKey ageKey;

        public Person(final String name, final int age) {
            this.name = name;
            this.ageKey = new UpdatableKey(AGE, age);
        }

        @Override
        public Key[] keys() {
            return new Key[]{ uuidKey, new Key(NAME, name), ageKey };
        }

        public void setAge(final int age) {
            final UpdatableKey oldKey = ageKey;
            final UpdatableKey<KeyTreeMapTest.Person> newKey = new UpdatableKey(AGE, age);
            ageKey = newKey;
            oldKey.update(newKey, this);
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}

