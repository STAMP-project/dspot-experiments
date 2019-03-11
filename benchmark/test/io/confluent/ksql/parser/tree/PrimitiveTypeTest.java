/**
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.parser.tree;


import SqlType.ARRAY;
import SqlType.BIGINT;
import SqlType.BOOLEAN;
import SqlType.DOUBLE;
import SqlType.INTEGER;
import SqlType.MAP;
import SqlType.STRING;
import SqlType.STRUCT;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import io.confluent.ksql.parser.tree.Type.SqlType;
import io.confluent.ksql.util.KsqlException;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PrimitiveTypeTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldImplementHashCodeAndEqualsProperly() {
        new EqualsTester().addEqualityGroup(PrimitiveType.of(BOOLEAN), PrimitiveType.of(BOOLEAN)).addEqualityGroup(PrimitiveType.of(INTEGER), PrimitiveType.of(INTEGER)).addEqualityGroup(PrimitiveType.of(BIGINT), PrimitiveType.of(BIGINT)).addEqualityGroup(PrimitiveType.of(DOUBLE), PrimitiveType.of(DOUBLE)).addEqualityGroup(PrimitiveType.of(STRING), PrimitiveType.of(STRING)).addEqualityGroup(Array.of(PrimitiveType.of(STRING))).testEquals();
    }

    @Test
    public void shouldReturnSqlType() {
        MatcherAssert.assertThat(PrimitiveType.of(INTEGER).getSqlType(), Matchers.is(INTEGER));
    }

    @Test
    public void shouldThrowOnUnknownTypeString() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Unknown primitive type: WHAT_IS_THIS?");
        // When:
        PrimitiveType.of("WHAT_IS_THIS?");
    }

    @Test
    public void shouldThrowOnArrayType() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Invalid primitive type: ARRAY");
        // When:
        PrimitiveType.of(ARRAY);
    }

    @Test
    public void shouldThrowOnMapType() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Invalid primitive type: MAP");
        // When:
        PrimitiveType.of(MAP);
    }

    @Test
    public void shouldThrowOnStructType() {
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Invalid primitive type: STRUCT");
        // When:
        PrimitiveType.of(STRUCT);
    }

    @Test
    public void shouldSupportPrimitiveTypes() {
        // Given:
        final Map<String, SqlType> primitives = ImmutableMap.of("BooleaN", BOOLEAN, "IntegeR", INTEGER, "BigInT", BIGINT, "DoublE", DOUBLE, "StrinG", STRING);
        primitives.forEach(( string, expected) -> assertThat(PrimitiveType.of(string).getSqlType(), is(expected)));
    }

    @Test
    public void shouldSupportAlternativePrimitiveTypeNames() {
        // Given:
        final Map<String, SqlType> primitives = ImmutableMap.of("InT", INTEGER, "VarchaR", STRING);
        primitives.forEach(( string, expected) -> assertThat(PrimitiveType.of(string).getSqlType(), is(expected)));
    }
}

