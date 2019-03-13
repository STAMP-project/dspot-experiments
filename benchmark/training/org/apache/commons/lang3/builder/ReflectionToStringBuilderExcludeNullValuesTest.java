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


public class ReflectionToStringBuilderExcludeNullValuesTest {
    static class TestFixture {
        @SuppressWarnings("unused")
        private final Integer testIntegerField;

        @SuppressWarnings("unused")
        private final String testStringField;

        TestFixture(final Integer a, final String b) {
            this.testIntegerField = a;
            this.testStringField = b;
        }
    }

    private static final String INTEGER_FIELD_NAME = "testIntegerField";

    private static final String STRING_FIELD_NAME = "testStringField";

    private final ReflectionToStringBuilderExcludeNullValuesTest.TestFixture BOTH_NON_NULL = new ReflectionToStringBuilderExcludeNullValuesTest.TestFixture(0, "str");

    private final ReflectionToStringBuilderExcludeNullValuesTest.TestFixture FIRST_NULL = new ReflectionToStringBuilderExcludeNullValuesTest.TestFixture(null, "str");

    private final ReflectionToStringBuilderExcludeNullValuesTest.TestFixture SECOND_NULL = new ReflectionToStringBuilderExcludeNullValuesTest.TestFixture(0, null);

    private final ReflectionToStringBuilderExcludeNullValuesTest.TestFixture BOTH_NULL = new ReflectionToStringBuilderExcludeNullValuesTest.TestFixture(null, null);

    @Test
    public void test_NonExclude() {
        // normal case=
        String toString = ReflectionToStringBuilder.toString(BOTH_NON_NULL, null, false, false, false, null);
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        // make one null
        toString = ReflectionToStringBuilder.toString(FIRST_NULL, null, false, false, false, null);
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        // other one null
        toString = ReflectionToStringBuilder.toString(SECOND_NULL, null, false, false, false, null);
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        // make the both null
        toString = ReflectionToStringBuilder.toString(BOTH_NULL, null, false, false, false, null);
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
    }

    @Test
    public void test_excludeNull() {
        // test normal case
        String toString = ReflectionToStringBuilder.toString(BOTH_NON_NULL, null, false, false, true, null);
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        // make one null
        toString = ReflectionToStringBuilder.toString(FIRST_NULL, null, false, false, true, null);
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        // other one null
        toString = ReflectionToStringBuilder.toString(SECOND_NULL, null, false, false, true, null);
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        // both null
        toString = ReflectionToStringBuilder.toString(BOTH_NULL, null, false, false, true, null);
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
    }

    @Test
    public void test_ConstructorOption() {
        ReflectionToStringBuilder builder = new ReflectionToStringBuilder(BOTH_NON_NULL, null, null, null, false, false, true);
        Assertions.assertTrue(builder.isExcludeNullValues());
        String toString = builder.toString();
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        builder = new ReflectionToStringBuilder(FIRST_NULL, null, null, null, false, false, true);
        toString = builder.toString();
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        builder = new ReflectionToStringBuilder(SECOND_NULL, null, null, null, false, false, true);
        toString = builder.toString();
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        builder = new ReflectionToStringBuilder(BOTH_NULL, null, null, null, false, false, true);
        toString = builder.toString();
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
    }

    @Test
    public void test_ConstructorOptionNormal() {
        final ReflectionToStringBuilder builder = new ReflectionToStringBuilder(BOTH_NULL, null, null, null, false, false, false);
        Assertions.assertFalse(builder.isExcludeNullValues());
        String toString = builder.toString();
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        // regression test older constructors
        ReflectionToStringBuilder oldBuilder = new ReflectionToStringBuilder(BOTH_NULL);
        toString = oldBuilder.toString();
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        oldBuilder = new ReflectionToStringBuilder(BOTH_NULL, null, null, null, false, false);
        toString = oldBuilder.toString();
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        oldBuilder = new ReflectionToStringBuilder(BOTH_NULL, null, null);
        toString = oldBuilder.toString();
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        Assertions.assertTrue(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
    }

    @Test
    public void test_ConstructorOption_ExcludeNull() {
        ReflectionToStringBuilder builder = new ReflectionToStringBuilder(BOTH_NULL, null, null, null, false, false, false);
        builder.setExcludeNullValues(true);
        Assertions.assertTrue(builder.isExcludeNullValues());
        String toString = builder.toString();
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        builder = new ReflectionToStringBuilder(BOTH_NULL, null, null, null, false, false, true);
        toString = builder.toString();
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
        final ReflectionToStringBuilder oldBuilder = new ReflectionToStringBuilder(BOTH_NULL);
        oldBuilder.setExcludeNullValues(true);
        Assertions.assertTrue(oldBuilder.isExcludeNullValues());
        toString = oldBuilder.toString();
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.STRING_FIELD_NAME));
        Assertions.assertFalse(toString.contains(ReflectionToStringBuilderExcludeNullValuesTest.INTEGER_FIELD_NAME));
    }
}

