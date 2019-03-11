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
package com.gs.collections.impl.list.immutable.primitive;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class ImmutableFloatArrayListSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAEhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0LmltbXV0YWJsZS5wcmltaXRpdmUu\n" + (("SW1tdXRhYmxlRmxvYXRBcnJheUxpc3QAAAAAAAAAAQIAAVsABWl0ZW1zdAACW0Z4cHVyAAJbRguc\n" + "gYki4AxCAgAAeHAAAAALP4AAAEAAAABAQAAAQIAAAECgAABAwAAAQOAAAEEAAABBEAAAQSAAAEEw\n") + "AAA=")), ImmutableFloatArrayList.newListWith(1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F, 8.0F, 9.0F, 10.0F, 11.0F));
    }
}

