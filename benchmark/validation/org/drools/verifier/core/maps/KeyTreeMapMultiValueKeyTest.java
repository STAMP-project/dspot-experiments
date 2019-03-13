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
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Assert;
import org.junit.Test;


public class KeyTreeMapMultiValueKeyTest {
    private final KeyDefinition NAME = KeyDefinition.newKeyDefinition().withId("name").build();

    private final KeyDefinition AREA_CODE = KeyDefinition.newKeyDefinition().withId("areaCode").build();

    private KeyTreeMap<KeyTreeMapMultiValueKeyTest.Country> map;

    private KeyTreeMapMultiValueKeyTest.Country norway;

    private KeyTreeMapMultiValueKeyTest.Country finland;

    private KeyTreeMapMultiValueKeyTest.Country sweden;

    private AnalyzerConfiguration configuration;

    @Test
    public void testFindByUUID() throws Exception {
        Util.assertMapContent(map.get(UNIQUE_UUID), finland.uuidKey, sweden.uuidKey, norway.uuidKey);
    }

    @Test
    public void testFindByAreaCodeKey() throws Exception {
        Util.assertMapContent(map.get(AREA_CODE), 48100, 12345, 51000, 0);
    }

    @Test
    public void testFindByAreaCode() throws Exception {
        final MultiMap<Value, KeyTreeMapMultiValueKeyTest.Country, List<KeyTreeMapMultiValueKeyTest.Country>> areaCode = map.get(AREA_CODE);
        Assert.assertEquals(1, areaCode.get(new Value(48100)).size());
        Assert.assertTrue(areaCode.get(new Value(48100)).contains(finland));
        Assert.assertEquals(1, areaCode.get(new Value(12345)).size());
        Assert.assertTrue(areaCode.get(new Value(12345)).contains(sweden));
        Assert.assertEquals(2, areaCode.get(new Value(51000)).size());
        Assert.assertTrue(areaCode.get(new Value(51000)).contains(sweden));
        Assert.assertTrue(areaCode.get(new Value(51000)).contains(norway));
    }

    class Country implements HasKeys {
        final String name;

        private final UUIDKey uuidKey = configuration.getUUID(this);

        private UpdatableKey areaCode;

        public Country(final String name, final Integer... areaCodes) {
            this.name = name;
            this.areaCode = new UpdatableKey(AREA_CODE, new Values(areaCodes));
        }

        @Override
        public Key[] keys() {
            return new Key[]{ uuidKey, new Key(NAME, name), areaCode };
        }

        public void setAge(final Integer... areaCodes) {
            final UpdatableKey oldKey = areaCode;
            final UpdatableKey<KeyTreeMapMultiValueKeyTest.Country> newKey = new UpdatableKey(AREA_CODE, new Values(areaCodes));
            areaCode = newKey;
            oldKey.update(newKey, this);
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}

