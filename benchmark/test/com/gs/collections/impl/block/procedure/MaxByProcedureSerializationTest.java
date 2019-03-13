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
package com.gs.collections.impl.block.procedure;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class MaxByProcedureSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyADZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcm9jZWR1cmUuTWF4QnlQcm9j\n" + ((("ZWR1cmUAAAAAAAAAAQIABFoAEnZpc2l0ZWRBdExlYXN0T25jZUwAEWNhY2hlZFJlc3VsdFZhbHVl\n" + "dAAWTGphdmEvbGFuZy9Db21wYXJhYmxlO0wACGZ1bmN0aW9udAAwTGNvbS9ncy9jb2xsZWN0aW9u\n") + "cy9hcGkvYmxvY2svZnVuY3Rpb24vRnVuY3Rpb247TAAGcmVzdWx0dAASTGphdmEvbGFuZy9PYmpl\n") + "Y3Q7eHAAcHBw")), new MaxByProcedure<Integer, Integer>(null));
    }
}

