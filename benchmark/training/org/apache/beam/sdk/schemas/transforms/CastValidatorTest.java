/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.schemas.transforms;


import Cast.CompatibilityError;
import Cast.Narrowing;
import Cast.Widening;
import Schema.Field;
import TypeName.BYTE;
import TypeName.DECIMAL;
import TypeName.DOUBLE;
import TypeName.FLOAT;
import TypeName.INT16;
import TypeName.INT32;
import TypeName.INT64;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.TypeName;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Cast.Widening}, {@link Cast.Narrowing}.
 */
public class CastValidatorTest {
    public static final Map<TypeName, Number> NUMERICS = ImmutableMap.<TypeName, Number>builder().put(BYTE, Byte.valueOf(((byte) (42)))).put(INT16, Short.valueOf(((short) (42)))).put(INT32, Integer.valueOf(42)).put(INT64, Long.valueOf(42)).put(FLOAT, Float.valueOf(42)).put(DOUBLE, Double.valueOf(42)).put(DECIMAL, BigDecimal.valueOf(42)).build();

    public static final List<TypeName> NUMERIC_ORDER = ImmutableList.of(BYTE, INT16, INT32, INT64, FLOAT, DOUBLE, DECIMAL);

    @Test
    public void testWideningOrder() {
        CastValidatorTest.NUMERICS.keySet().forEach(( input) -> NUMERICS.keySet().forEach(( output) -> testWideningOrder(input, output)));
    }

    @Test
    public void testCasting() {
        CastValidatorTest.NUMERICS.keySet().forEach(( input) -> NUMERICS.keySet().forEach(( output) -> testCasting(input, output)));
    }

    @Test
    public void testCastingCompleteness() {
        boolean all = CastValidatorTest.NUMERIC_ORDER.stream().filter(TypeName::isNumericType).allMatch(CastValidatorTest.NUMERIC_ORDER::contains);
        Assert.assertTrue(all);
    }

    @Test
    public void testWideningNullableToNotNullable() {
        Schema input = Schema.of(Field.nullable("f0", FieldType.INT32));
        Schema output = Schema.of(Field.of("f0", FieldType.INT32));
        List<Cast.CompatibilityError> errors = Widening.of().apply(input, output);
        Cast.CompatibilityError expected = CompatibilityError.create(Arrays.asList("f0"), "Can't cast nullable field to non-nullable field");
        Assert.assertThat(errors, Matchers.containsInAnyOrder(expected));
    }

    @Test
    public void testNarrowingNullableToNotNullable() {
        Schema input = Schema.of(Field.nullable("f0", FieldType.INT32));
        Schema output = Schema.of(Field.of("f0", FieldType.INT32));
        List<Cast.CompatibilityError> errors = Narrowing.of().apply(input, output);
        Assert.assertThat(errors, Matchers.empty());
    }
}

