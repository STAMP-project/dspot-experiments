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


import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Test;


public class KeyTreeMapUUIDKeyTest {
    AnalyzerConfiguration configuration;

    @Test(expected = IllegalArgumentException.class)
    public void testNoKey() throws Exception {
        final KeyTreeMap<KeyTreeMapUUIDKeyTest.NoKey> map = new KeyTreeMap(UUIDKey.UNIQUE_UUID);
        map.put(new KeyTreeMapUUIDKeyTest.NoKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTwoKeys() throws Exception {
        final KeyTreeMap<KeyTreeMapUUIDKeyTest.TwoKeys> map = new KeyTreeMap(UUIDKey.UNIQUE_UUID);
        map.put(new KeyTreeMapUUIDKeyTest.TwoKeys());
    }

    private class NoKey implements HasKeys {
        @Override
        public Key[] keys() {
            return new Key[0];
        }

        @Override
        public UUIDKey getUuidKey() {
            return null;
        }
    }

    private class TwoKeys implements HasKeys {
        @Override
        public Key[] keys() {
            return new Key[]{ configuration.getUUID(this), configuration.getUUID(this) };
        }

        @Override
        public UUIDKey getUuidKey() {
            return null;
        }
    }
}

