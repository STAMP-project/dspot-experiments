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
package com.gs.collections.impl.block.function.primitive;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public final class DoubleFunctionImplSerializationTest {
    private static final DoubleFunctionImpl<?> DOUBLE_FUNCTION = new DoubleFunctionImpl<Object>() {
        private static final long serialVersionUID = 1L;

        public double doubleValueOf(Object anObject) {
            return 0;
        }
    };

    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAFZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mdW5jdGlvbi5wcmltaXRpdmUu\n" + (("RG91YmxlRnVuY3Rpb25JbXBsU2VyaWFsaXphdGlvblRlc3QkMQAAAAAAAAABAgAAeHIAQ2NvbS5n\n" + "cy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZ1bmN0aW9uLnByaW1pdGl2ZS5Eb3VibGVGdW5jdGlv\n") + "bkltcGwAAAAAAAAAAQIAAHhw")), DoubleFunctionImplSerializationTest.DOUBLE_FUNCTION);
    }
}

