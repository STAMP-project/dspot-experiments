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
package com.gs.collections.impl.block.procedure;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class RejectProcedureSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcm9jZWR1cmUuUmVqZWN0UHJv\n" + (("Y2VkdXJlAAAAAAAAAAECAAJMAApjb2xsZWN0aW9udAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO0wA\n" + "CXByZWRpY2F0ZXQAMkxjb20vZ3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9QcmVk\n") + "aWNhdGU7eHBwcA==")), new RejectProcedure<Object>(null, null));
    }
}

