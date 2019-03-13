/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import com.google.api.core.ApiFunction;
import com.google.common.testing.EqualsTester;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StringEnumTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class Letter extends StringEnumValue {
        private static final long serialVersionUID = -1717976087182628526L;

        private Letter(String constant) {
            super(constant);
        }

        private static final ApiFunction<String, StringEnumTest.Letter> CONSTRUCTOR = new ApiFunction<String, StringEnumTest.Letter>() {
            @Override
            public StringEnumTest.Letter apply(String constant) {
                return new StringEnumTest.Letter(constant);
            }
        };

        private static final StringEnumType<StringEnumTest.Letter> type = new StringEnumType(StringEnumTest.Letter.class, StringEnumTest.Letter.CONSTRUCTOR);

        public static final StringEnumTest.Letter A = StringEnumTest.Letter.type.createAndRegister("A");

        public static final StringEnumTest.Letter B = StringEnumTest.Letter.type.createAndRegister("B");

        public static final StringEnumTest.Letter C = StringEnumTest.Letter.type.createAndRegister("C");

        public static StringEnumTest.Letter valueOfStrict(String constant) {
            return StringEnumTest.Letter.type.valueOfStrict(constant);
        }

        /**
         * Get the StorageClass for the given String constant, and allow unrecognized values.
         */
        public static StringEnumTest.Letter valueOf(String constant) {
            return StringEnumTest.Letter.type.valueOf(constant);
        }

        /**
         * Return the known values for StorageClass.
         */
        public static StringEnumTest.Letter[] values() {
            return StringEnumTest.Letter.type.values();
        }
    }

    @Test
    public void testNullClass() {
        expectedException.expect(NullPointerException.class);
        new StringEnumType<StringEnumTest.Letter>(null, StringEnumTest.Letter.CONSTRUCTOR);
    }

    @Test
    public void testNullConstructor() {
        expectedException.expect(NullPointerException.class);
        new StringEnumType<StringEnumTest.Letter>(StringEnumTest.Letter.class, null);
    }

    @Test
    public void testEnumInstances() {
        assertThat(StringEnumTest.Letter.A.toString()).isEqualTo("A");
    }

    @Test
    public void testValueOf() {
        assertThat(StringEnumTest.Letter.valueOf("A")).isSameAs(StringEnumTest.Letter.A);
        assertThat(StringEnumTest.Letter.valueOf("B")).isSameAs(StringEnumTest.Letter.B);
        assertThat(StringEnumTest.Letter.valueOf("C")).isSameAs(StringEnumTest.Letter.C);
        assertThat(StringEnumTest.Letter.valueOf("NonExistentLetter").toString()).isEqualTo("NonExistentLetter");
    }

    @Test
    public void testValueOfStrict() {
        assertThat(StringEnumTest.Letter.valueOfStrict("A")).isSameAs(StringEnumTest.Letter.A);
        assertThat(StringEnumTest.Letter.valueOfStrict("B")).isSameAs(StringEnumTest.Letter.B);
        assertThat(StringEnumTest.Letter.valueOfStrict("C")).isSameAs(StringEnumTest.Letter.C);
    }

    @Test
    public void testEquals() {
        EqualsTester tester = new EqualsTester();
        tester.addEqualityGroup(StringEnumTest.Letter.A, StringEnumTest.Letter.valueOf("A"), StringEnumTest.Letter.valueOfStrict("A"));
        tester.addEqualityGroup(StringEnumTest.Letter.B, StringEnumTest.Letter.valueOf("B"), StringEnumTest.Letter.valueOfStrict("B"));
        tester.addEqualityGroup(StringEnumTest.Letter.C, StringEnumTest.Letter.valueOf("C"), StringEnumTest.Letter.valueOfStrict("C"));
        tester.addEqualityGroup(StringEnumTest.Letter.valueOf("NonExistentLetter"), StringEnumTest.Letter.valueOf("NonExistentLetter"));
    }

    @Test
    public void testValueOfStrict_invalid() {
        expectedException.expect(IllegalArgumentException.class);
        StringEnumTest.Letter.valueOfStrict("NonExistentLetter");
    }

    @Test
    public void testValues() {
        assertThat(Arrays.asList(StringEnumTest.Letter.values()).containsAll(Arrays.asList(StringEnumTest.Letter.A, StringEnumTest.Letter.B, StringEnumTest.Letter.C)));
    }
}

