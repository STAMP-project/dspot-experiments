/**
 * Copyright 2013 Goldman Sachs.
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
package com.gs.collections.impl.map.immutable.primitive;


import com.gs.collections.impl.map.mutable.primitive.ObjectByteHashMap;
import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class ImmutableObjectByteHashMapSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAHdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAuaW1tdXRhYmxlLnByaW1pdGl2ZS5B\n" + ("YnN0cmFjdEltbXV0YWJsZU9iamVjdEJ5dGVNYXAkSW1tdXRhYmxlT2JqZWN0Qnl0ZU1hcFNlcmlh\n" + "bGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHB3BAAAAAJ0AAEydwECdAABMXcBAXg=")), new ImmutableObjectByteHashMap<String>(ObjectByteHashMap.newWithKeysValues("1", ((byte) (1)), "2", ((byte) (2)))));
    }
}

