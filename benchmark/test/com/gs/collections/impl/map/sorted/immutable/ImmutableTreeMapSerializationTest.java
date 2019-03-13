/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.map.sorted.immutable;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class ImmutableTreeMapSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(2L, ImmutableEmptySortedMapSerializationTest.EXPECTED_BASE_64_FORM, ImmutableTreeMap.newMap(new com.gs.collections.impl.map.sorted.mutable.TreeSortedMap<Object, Object>()));
    }

    @Test
    public void keySetSerializedForm() {
        Verify.assertSerializedForm(2L, ("rO0ABXNyAFFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zZXQuc29ydGVkLmltbXV0YWJsZS5JbW11\n" + "dGFibGVTb3J0ZWRTZXRTZXJpYWxpemF0aW9uUHJveHkAAAAAAAAAAQwAAHhwcHcEAAAAAHg="), new ImmutableTreeMap<Object, Object>(new com.gs.collections.impl.map.sorted.mutable.TreeSortedMap<Object, Object>()).keySet());
    }
}

