/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.common.guava;


import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.guava.nary.BinaryFn;
import org.junit.Test;


public class ComplexSequenceTest {
    @Test
    public void testComplexSequence() {
        Sequence<Integer> complex;
        check("[3, 5]", (complex = concat(combine(simple(3)), combine(simple(5)))));
        check("[8]", (complex = combine(complex)));
        check("[8, 6, 3, 5]", (complex = concat(complex, concat(combine(simple(2, 4)), simple(3, 5)))));
        check("[22]", (complex = combine(complex)));
        check("[22]", concat(complex, simple()));
    }

    private final BinaryFn<Integer, Integer, Integer> plus = new BinaryFn<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer arg1, Integer arg2) {
            if (arg1 == null) {
                return arg2;
            }
            if (arg2 == null) {
                return arg1;
            }
            return arg1 + arg2;
        }
    };
}

