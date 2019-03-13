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


import java.util.List;
import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Assert;
import org.junit.Test;


public class KeyTreeMapMergeTest {
    private static final KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("name").build();

    private static final KeyDefinition AGE_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("age").build();

    private KeyTreeMap<KeyTreeMapMergeTest.Person> treeMap;

    private KeyTreeMapMergeTest.Person pat;

    private KeyTreeMap<KeyTreeMapMergeTest.Person> otherKeyTreeMap;

    private KeyTreeMapMergeTest.Person mat;

    private Matcher nameMatcher = new Matcher(KeyTreeMapMergeTest.NAME_KEY_DEFINITION);

    private AnalyzerConfigurationMock configuration;

    @Test
    public void testMergeToEmptyMap() throws Exception {
        final KeyTreeMap<KeyTreeMapMergeTest.Person> empty = new KeyTreeMap(UUIDKey.UNIQUE_UUID, KeyTreeMapMergeTest.NAME_KEY_DEFINITION, KeyTreeMapMergeTest.AGE_KEY_DEFINITION);
        empty.merge(otherKeyTreeMap);
        Assert.assertEquals(1, empty.get(nameMatcher.getKeyDefinition()).allValues().size());
    }

    @Test
    public void testNames() throws Exception {
        treeMap.merge(otherKeyTreeMap);
        final MultiMap<Value, KeyTreeMapMergeTest.Person, List<KeyTreeMapMergeTest.Person>> multiMap = treeMap.get(nameMatcher.getKeyDefinition());
        Assert.assertEquals(2, multiMap.allValues().size());
    }

    @Test
    public void testAge() throws Exception {
        treeMap.merge(otherKeyTreeMap);
        Assert.assertEquals(2, allPersons(treeMap).size());
    }

    @Test
    public void testRetract() throws Exception {
        KeyTreeMap<KeyTreeMapMergeTest.Person> thirdKeyTreeMap = new KeyTreeMap(KeyTreeMapMergeTest.NAME_KEY_DEFINITION, KeyTreeMapMergeTest.AGE_KEY_DEFINITION);
        thirdKeyTreeMap.merge(treeMap);
        thirdKeyTreeMap.merge(otherKeyTreeMap);
        Assert.assertEquals(2, allPersons(thirdKeyTreeMap).size());
        Assert.assertEquals(1, allPersons(treeMap).size());
        Assert.assertEquals(1, allPersons(otherKeyTreeMap).size());
        pat.uuidKey.retract();
        Assert.assertEquals(1, allPersons(thirdKeyTreeMap).size());
        Assert.assertEquals(0, allPersons(treeMap).size());
        Assert.assertEquals(1, allPersons(otherKeyTreeMap).size());
    }

    private class Person implements HasKeys {
        private String name;

        private Integer age;

        private UUIDKey uuidKey = getUUID(this);

        public Person(final String name, final Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        @Override
        public Key[] keys() {
            return new Key[]{ uuidKey, new Key(KeyTreeMapMergeTest.NAME_KEY_DEFINITION, name), new Key(KeyTreeMapMergeTest.AGE_KEY_DEFINITION, age) };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}

