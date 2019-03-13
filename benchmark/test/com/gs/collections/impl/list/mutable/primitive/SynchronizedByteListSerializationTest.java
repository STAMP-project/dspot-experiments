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
package com.gs.collections.impl.list.mutable.primitive;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class SynchronizedByteListSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAENjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0Lm11dGFibGUucHJpbWl0aXZlLlN5\n" + ((((("bmNocm9uaXplZEJ5dGVMaXN0AAAAAAAAAAECAAB4cgBXY29tLmdzLmNvbGxlY3Rpb25zLmltcGwu\n" + "Y29sbGVjdGlvbi5tdXRhYmxlLnByaW1pdGl2ZS5BYnN0cmFjdFN5bmNocm9uaXplZEJ5dGVDb2xs\n") + "ZWN0aW9uAAAAAAAAAAECAAJMAApjb2xsZWN0aW9udABDTGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkv\n") + "Y29sbGVjdGlvbi9wcmltaXRpdmUvTXV0YWJsZUJ5dGVDb2xsZWN0aW9uO0wABGxvY2t0ABJMamF2\n") + "YS9sYW5nL09iamVjdDt4cHNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0Lm11dGFibGUu\n") + "cHJpbWl0aXZlLkJ5dGVBcnJheUxpc3QAAAAAAAAAAQwAAHhwdwQAAAAAeHEAfgAE")), new SynchronizedByteList(new ByteArrayList()));
    }
}

