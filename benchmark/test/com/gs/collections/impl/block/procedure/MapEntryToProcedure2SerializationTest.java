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


public class MapEntryToProcedure2SerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcm9jZWR1cmUuTWFwRW50cnlU\n" + ("b1Byb2NlZHVyZTIAAAAAAAAAAQIAAUwACXByb2NlZHVyZXQAM0xjb20vZ3MvY29sbGVjdGlvbnMv\n" + "YXBpL2Jsb2NrL3Byb2NlZHVyZS9Qcm9jZWR1cmUyO3hwcA==")), new MapEntryToProcedure2<Object, Object>(null));
    }
}

