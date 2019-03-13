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
package com.gs.collections.impl.bag.mutable.primitive;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class UnmodifiableShortBagSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5iYWcubXV0YWJsZS5wcmltaXRpdmUuVW5t\n" + ((((("b2RpZmlhYmxlU2hvcnRCYWcAAAAAAAAAAQIAAHhyAFhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5j\n" + "b2xsZWN0aW9uLm11dGFibGUucHJpbWl0aXZlLkFic3RyYWN0VW5tb2RpZmlhYmxlU2hvcnRDb2xs\n") + "ZWN0aW9uAAAAAAAAAAECAAFMAApjb2xsZWN0aW9udABETGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkv\n") + "Y29sbGVjdGlvbi9wcmltaXRpdmUvTXV0YWJsZVNob3J0Q29sbGVjdGlvbjt4cHNyADpjb20uZ3Mu\n") + "Y29sbGVjdGlvbnMuaW1wbC5iYWcubXV0YWJsZS5wcmltaXRpdmUuU2hvcnRIYXNoQmFnAAAAAAAA\n") + "AAEMAAB4cHcEAAAAAHg=")), new UnmodifiableShortBag(new ShortHashBag()));
    }
}

