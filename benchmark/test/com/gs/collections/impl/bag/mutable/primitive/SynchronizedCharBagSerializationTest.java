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


public class SynchronizedCharBagSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAEFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5iYWcubXV0YWJsZS5wcmltaXRpdmUuU3lu\n" + ((((("Y2hyb25pemVkQ2hhckJhZwAAAAAAAAABAgAAeHIAV2NvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmNv\n" + "bGxlY3Rpb24ubXV0YWJsZS5wcmltaXRpdmUuQWJzdHJhY3RTeW5jaHJvbml6ZWRDaGFyQ29sbGVj\n") + "dGlvbgAAAAAAAAABAgACTAAKY29sbGVjdGlvbnQAQ0xjb20vZ3MvY29sbGVjdGlvbnMvYXBpL2Nv\n") + "bGxlY3Rpb24vcHJpbWl0aXZlL011dGFibGVDaGFyQ29sbGVjdGlvbjtMAARsb2NrdAASTGphdmEv\n") + "bGFuZy9PYmplY3Q7eHBzcgA5Y29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmFnLm11dGFibGUucHJp\n") + "bWl0aXZlLkNoYXJIYXNoQmFnAAAAAAAAAAEMAAB4cHcEAAAAAHhxAH4ABA==")), new SynchronizedCharBag(new CharHashBag()));
    }
}

