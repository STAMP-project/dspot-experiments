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
package com.gs.collections.impl.block.predicate;


import com.gs.collections.impl.test.Verify;
import org.junit.Test;


public class PairPredicateSerializationTest {
    private static final PairPredicate<Object, Object> PAIR_PREDICATE = new PairPredicate<Object, Object>() {
        private static final long serialVersionUID = 1L;

        public boolean accept(Object argument1, Object argument2) {
            return false;
        }
    };

    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyAEhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcmVkaWNhdGUuUGFpclByZWRp\n" + ("Y2F0ZVNlcmlhbGl6YXRpb25UZXN0JDEAAAAAAAAAAQIAAHhyADVjb20uZ3MuY29sbGVjdGlvbnMu\n" + "aW1wbC5ibG9jay5wcmVkaWNhdGUuUGFpclByZWRpY2F0ZQAAAAAAAAABAgAAeHA=")), PairPredicateSerializationTest.PAIR_PREDICATE);
    }
}

