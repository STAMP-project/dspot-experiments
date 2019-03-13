/**
 * Copyright 2011 Goldman Sachs.
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
package com.gs.collections.impl.map.sorted.mutable;


import com.gs.collections.impl.test.Verify;
import java.util.TreeMap;
import org.junit.Test;


public class SortedMapAdapterSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAuc29ydGVkLm11dGFibGUuU29ydGVk\n" + (("TWFwQWRhcHRlcgAAAAAAAAABAgABTAAIZGVsZWdhdGV0ABVMamF2YS91dGlsL1NvcnRlZE1hcDt4\n" + "cHNyABFqYXZhLnV0aWwuVHJlZU1hcAzB9j4tJWrmAwABTAAKY29tcGFyYXRvcnQAFkxqYXZhL3V0\n") + "aWwvQ29tcGFyYXRvcjt4cHB3BAAAAAB4")), new SortedMapAdapter<Object, Object>(new TreeMap<Object, Object>()));
    }
}

