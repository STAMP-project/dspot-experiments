/**
 * Copyright 2015 Goldman Sachs.
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
package com.gs.collections.impl.bimap.mutable;


import com.gs.collections.api.bimap.MutableBiMap;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Key;
import org.junit.Test;


public class HashBiMapInverseTest extends AbstractMutableBiMapTestCase {
    @Override
    @Test
    public void keyPreservation() {
        Key key = new Key("key");
        Key duplicateKey1 = new Key("key");
        MutableBiMap<Integer, Key> map1 = HashBiMap.newWithKeysValues(key, 1, duplicateKey1, 2).inverse();
        Verify.assertSize(1, map1);
        Verify.assertContainsKeyValue(2, key, map1);
        Key duplicateKey2 = new Key("key");
        MutableBiMap<Integer, Key> map2 = HashBiMap.newWithKeysValues(key, 1, duplicateKey1, 2, duplicateKey2, 3).inverse();
        Verify.assertSize(1, map2);
        Verify.assertContainsKeyValue(3, key, map2);
    }
}

