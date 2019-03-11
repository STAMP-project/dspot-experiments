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
package io.confluent.ksql.schema.ksql;


import SqlType.BIGINT;
import SqlType.BOOLEAN;
import SqlType.DOUBLE;
import SqlType.INTEGER;
import SqlType.STRING;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.Type;
import io.confluent.ksql.parser.tree.Type.SqlType;
import io.confluent.ksql.util.KsqlException;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LogicalSchemasTest {
    private static final Schema LOGICAL_BOOLEAN_SCHEMA = SchemaBuilder.bool().optional().build();

    private static final Schema LOGICAL_INT_SCHEMA = SchemaBuilder.int32().optional().build();

    private static final Schema LOGICAL_BIGINT_SCHEMA = SchemaBuilder.int64().optional().build();

    private static final Schema LOGICAL_DOUBLE_SCHEMA = SchemaBuilder.float64().optional().build();

    private static final Schema LOGICAL_STRING_SCHEMA = SchemaBuilder.string().optional().build();

    private static final BiMap<Type, Schema> SQL_TO_LOGICAL = ImmutableBiMap.<Type, Schema>builder().put(PrimitiveType.of(BOOLEAN), LogicalSchemasTest.LOGICAL_BOOLEAN_SCHEMA).put(PrimitiveType.of(INTEGER), LogicalSchemasTest.LOGICAL_INT_SCHEMA).put(PrimitiveType.of(BIGINT), LogicalSchemasTest.LOGICAL_BIGINT_SCHEMA).put(PrimitiveType.of(DOUBLE), LogicalSchemasTest.LOGICAL_DOUBLE_SCHEMA).put(PrimitiveType.of(STRING), LogicalSchemasTest.LOGICAL_STRING_SCHEMA).put(io.confluent.ksql.parser.tree.Array.of(PrimitiveType.of(INTEGER)), SchemaBuilder.array(LogicalSchemas.INTEGER).optional().build()).put(io.confluent.ksql.parser.tree.Map.of(PrimitiveType.of(INTEGER)), SchemaBuilder.map(LogicalSchemas.STRING, LogicalSchemas.INTEGER).optional().build()).put(io.confluent.ksql.parser.tree.Struct.builder().addField("f0", PrimitiveType.of(INTEGER)).build(), SchemaBuilder.struct().field("f0", LogicalSchemas.INTEGER).optional().build()).build();

    private static final Schema STRUCT_LOGICAL_TYPE = SchemaBuilder.struct().field("F0", SchemaBuilder.int32().optional().build()).optional().build();

    private static final Schema NESTED_LOGICAL_TYPE = SchemaBuilder.struct().field("ARRAY", SchemaBuilder.array(LogicalSchemasTest.STRUCT_LOGICAL_TYPE).optional().build()).field("MAP", SchemaBuilder.map(LogicalSchemasTest.LOGICAL_STRING_SCHEMA, LogicalSchemasTest.STRUCT_LOGICAL_TYPE).optional().build()).field("STRUCT", LogicalSchemasTest.STRUCT_LOGICAL_TYPE).optional().build();

    private static final Type STRUCT_SQL_TYPE = io.confluent.ksql.parser.tree.Struct.builder().addField("F0", PrimitiveType.of(INTEGER)).build();

    private static final Type NESTED_SQL_TYPE = io.confluent.ksql.parser.tree.Struct.builder().addField("ARRAY", io.confluent.ksql.parser.tree.Array.of(LogicalSchemasTest.STRUCT_SQL_TYPE)).addField("MAP", io.confluent.ksql.parser.tree.Map.of(LogicalSchemasTest.STRUCT_SQL_TYPE)).addField("STRUCT", LogicalSchemasTest.STRUCT_SQL_TYPE).build();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldHaveTestsForAllTypes() {
        final Set<SqlType> tested = LogicalSchemasTest.SQL_TO_LOGICAL.keySet().stream().map(Type::getSqlType).collect(Collectors.toSet());
        final ImmutableSet<SqlType> allTypes = ImmutableSet.copyOf(SqlType.values());
        MatcherAssert.assertThat(("If this test fails then there has been a new SQL type added and this test " + "file needs updating to cover that new type"), tested, Matchers.is(allTypes));
    }

    @Test
    public void shouldGetLogicalForEverySqlType() {
        LogicalSchemasTest.SQL_TO_LOGICAL.forEach(( sqlType, logical) -> assertThat(LogicalSchemas.fromSqlTypeConverter().fromSqlType(sqlType), is(logical)));
    }

    @Test
    public void shouldGetSqlTypeForEveryLogicalType() {
        LogicalSchemasTest.SQL_TO_LOGICAL.inverse().forEach(( logical, sqlType) -> assertThat(LogicalSchemas.toSqlTypeConverter().toSqlType(logical), is(sqlType)));
    }

    @Test
    public void shouldConvertNestedComplexToSql() {
        MatcherAssert.assertThat(LogicalSchemas.toSqlTypeConverter().toSqlType(LogicalSchemasTest.NESTED_LOGICAL_TYPE), Matchers.is(LogicalSchemasTest.NESTED_SQL_TYPE));
    }

    @Test
    public void shouldConvertNestedComplexFromSql() {
        MatcherAssert.assertThat(LogicalSchemas.fromSqlTypeConverter().fromSqlType(LogicalSchemasTest.NESTED_SQL_TYPE), Matchers.is(LogicalSchemasTest.NESTED_LOGICAL_TYPE));
    }

    @Test
    public void shouldThrowOnNonStringKeyedMap() {
        // Given:
        final Schema mapSchema = SchemaBuilder.map(LogicalSchemasTest.LOGICAL_BIGINT_SCHEMA, LogicalSchemasTest.LOGICAL_DOUBLE_SCHEMA).optional().build();
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Unsupported map key type: Schema{INT64}");
        // When:
        LogicalSchemas.toSqlTypeConverter().toSqlType(mapSchema);
    }

    @Test
    public void shouldThrowOnUnsupportedConnectSchemaType() {
        // Given:
        final Schema unsupported = SchemaBuilder.int8().build();
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Unexpected logical type: Schema{INT8}");
        // When:
        LogicalSchemas.toSqlTypeConverter().toSqlType(unsupported);
    }
}

