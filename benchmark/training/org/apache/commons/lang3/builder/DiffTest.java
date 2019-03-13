/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.builder;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link Diff}.
 */
public class DiffTest {
    private static final String FIELD_NAME = "field";

    private static final Diff<Boolean> booleanDiff = new DiffTest.BooleanDiff(DiffTest.FIELD_NAME);

    private static class BooleanDiff extends Diff<Boolean> {
        private static final long serialVersionUID = 1L;

        protected BooleanDiff(final String fieldName) {
            super(fieldName);
        }

        @Override
        public Boolean getLeft() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean getRight() {
            return Boolean.FALSE;
        }
    }

    @Test
    public void testCannotModify() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> DiffTest.booleanDiff.setValue(Boolean.FALSE));
    }

    @Test
    public void testGetFieldName() {
        Assertions.assertEquals(DiffTest.FIELD_NAME, DiffTest.booleanDiff.getFieldName());
    }

    @Test
    public void testGetType() {
        Assertions.assertEquals(Boolean.class, DiffTest.booleanDiff.getType());
    }

    @Test
    public void testToString() {
        Assertions.assertEquals(String.format("[%s: %s, %s]", DiffTest.FIELD_NAME, DiffTest.booleanDiff.getLeft(), DiffTest.booleanDiff.getRight()), DiffTest.booleanDiff.toString());
    }
}

