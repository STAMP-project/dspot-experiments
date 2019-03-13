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
package com.gs.collections.impl.stack.mutable.primitive;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class UnmodifiableFloatStackSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAEZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zdGFjay5tdXRhYmxlLnByaW1pdGl2ZS5V\n" + ((("bm1vZGlmaWFibGVGbG9hdFN0YWNrAAAAAAAAAAECAAFMAAVzdGFja3QAOkxjb20vZ3MvY29sbGVj\n" + "dGlvbnMvYXBpL3N0YWNrL3ByaW1pdGl2ZS9NdXRhYmxlRmxvYXRTdGFjazt4cHNyAD9jb20uZ3Mu\n") + "Y29sbGVjdGlvbnMuaW1wbC5zdGFjay5tdXRhYmxlLnByaW1pdGl2ZS5GbG9hdEFycmF5U3RhY2sA\n") + "AAAAAAAAAQwAAHhwdwQAAAAAeA==")), new UnmodifiableFloatStack(new FloatArrayStack()));
    }
}

