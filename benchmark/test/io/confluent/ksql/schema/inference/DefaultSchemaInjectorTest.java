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
package io.confluent.ksql.schema.inference;


import Schema.BYTES_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_FLOAT32_SCHEMA;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import SqlType.BIGINT;
import SqlType.BOOLEAN;
import SqlType.DOUBLE;
import SqlType.INTEGER;
import SqlType.STRING;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.tree.Array;
import io.confluent.ksql.parser.tree.CreateStream;
import io.confluent.ksql.parser.tree.CreateTable;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.parser.tree.StringLiteral;
import io.confluent.ksql.parser.tree.Struct;
import io.confluent.ksql.parser.tree.TableElement;
import io.confluent.ksql.schema.inference.TopicSchemaSupplier.SchemaResult;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.KsqlStatementException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static SchemaAndId.schemaAndId;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSchemaInjectorTest {
    private static final List<TableElement> SOME_ELEMENTS = ImmutableList.of(new TableElement("bob", PrimitiveType.of(STRING)));

    private static final Map<String, Expression> UNSUPPORTED_PROPS = ImmutableMap.of("VALUE_FORMAT", new StringLiteral("json"));

    private static final String KAFKA_TOPIC = "some-topic";

    private static final Map<String, Expression> SUPPORTED_PROPS = ImmutableMap.of("VALUE_FORMAT", new StringLiteral("avro"), "KAFKA_TOPIC", new StringLiteral(DefaultSchemaInjectorTest.KAFKA_TOPIC));

    private static final String SQL_TEXT = "Some SQL";

    private static final List<Schema> UNSUPPORTED_SCHEMAS = ImmutableList.of(SchemaBuilder.struct().field("byte", INT8_SCHEMA).build(), SchemaBuilder.struct().field("short", INT16_SCHEMA).build(), SchemaBuilder.struct().field("bytes", BYTES_SCHEMA).build(), SchemaBuilder.struct().field("nonStringKeyMap", SchemaBuilder.map(OPTIONAL_INT64_SCHEMA, OPTIONAL_INT64_SCHEMA)).build());

    private static final Schema SUPPORTED_SCHEMA = SchemaBuilder.struct().field("intField", OPTIONAL_INT32_SCHEMA).field("bigIntField", OPTIONAL_INT64_SCHEMA).field("floatField", OPTIONAL_FLOAT32_SCHEMA).field("doubleField", OPTIONAL_FLOAT64_SCHEMA).field("stringField", OPTIONAL_STRING_SCHEMA).field("booleanField", OPTIONAL_BOOLEAN_SCHEMA).field("arrayField", SchemaBuilder.array(OPTIONAL_INT32_SCHEMA)).field("mapField", SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT64_SCHEMA)).field("structField", SchemaBuilder.struct().field("s0", OPTIONAL_INT64_SCHEMA).build()).build();

    private static final List<TableElement> EXPECTED_KSQL_SCHEMA = ImmutableList.<TableElement>builder().add(new TableElement("INTFIELD", PrimitiveType.of(INTEGER))).add(new TableElement("BIGINTFIELD", PrimitiveType.of(BIGINT))).add(new TableElement("FLOATFIELD", PrimitiveType.of(DOUBLE))).add(new TableElement("DOUBLEFIELD", PrimitiveType.of(DOUBLE))).add(new TableElement("STRINGFIELD", PrimitiveType.of(STRING))).add(new TableElement("BOOLEANFIELD", PrimitiveType.of(BOOLEAN))).add(new TableElement("ARRAYFIELD", Array.of(PrimitiveType.of(INTEGER)))).add(new TableElement("MAPFIELD", io.confluent.ksql.parser.tree.Map.of(PrimitiveType.of(BIGINT)))).add(new TableElement("STRUCTFIELD", Struct.builder().addField("s0", PrimitiveType.of(BIGINT)).build())).build();

    private static final int SCHEMA_ID = 5;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Statement statement;

    @Mock
    private CreateStream cs;

    @Mock
    private CreateTable ct;

    @Mock
    private TopicSchemaSupplier schemaSupplier;

    private PreparedStatement<CreateStream> csStatement;

    private PreparedStatement<CreateTable> ctStatement;

    private DefaultSchemaInjector injector;

    @Test
    public void shouldReturnStatementUnchangedIfNotCreateStatement() {
        // Given:
        final PreparedStatement<?> prepared = PreparedStatement.of("sql", statement);
        // When:
        final PreparedStatement<?> result = injector.forStatement(prepared);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.sameInstance(prepared)));
    }

    @Test
    public void shouldReturnStatementUnchangedIfCsAlreadyHasSchema() {
        // Given:
        Mockito.when(cs.getElements()).thenReturn(DefaultSchemaInjectorTest.SOME_ELEMENTS);
        // When:
        final PreparedStatement<?> result = injector.forStatement(csStatement);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.sameInstance(csStatement)));
    }

    @Test
    public void shouldReturnStatementUnchangedIfCtAlreadyHasSchema() {
        // Given:
        Mockito.when(ct.getElements()).thenReturn(DefaultSchemaInjectorTest.SOME_ELEMENTS);
        // When:
        final PreparedStatement<?> result = injector.forStatement(ctStatement);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.sameInstance(ctStatement)));
    }

    @Test
    public void shouldReturnStatementUnchangedIfCsFormatDoesNotSupportInference() {
        // Given:
        Mockito.when(cs.getProperties()).thenReturn(DefaultSchemaInjectorTest.UNSUPPORTED_PROPS);
        // When:
        final PreparedStatement<?> result = injector.forStatement(csStatement);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.sameInstance(csStatement)));
    }

    @Test
    public void shouldReturnStatementUnchangedIfCtFormatDoesNotSupportInference() {
        // Given:
        Mockito.when(ct.getProperties()).thenReturn(DefaultSchemaInjectorTest.UNSUPPORTED_PROPS);
        // When:
        final PreparedStatement<?> result = injector.forStatement(ctStatement);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(Matchers.sameInstance(ctStatement)));
    }

    @Test
    public void shouldThrowIfMissingValueFormat() {
        // Given:
        Mockito.when(cs.getProperties()).thenReturn(DefaultSchemaInjectorTest.supportedPropsWithout("VALUE_FORMAT"));
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expectMessage("VALUE_FORMAT should be set in WITH clause of CREATE STREAM/TABLE statement.");
        expectedException.expectMessage(DefaultSchemaInjectorTest.SQL_TEXT);
        // When:
        injector.forStatement(csStatement);
    }

    @Test
    public void shouldThrowIfMissingKafkaTopicProperty() {
        // Given:
        Mockito.when(ct.getProperties()).thenReturn(DefaultSchemaInjectorTest.supportedPropsWithout("KAFKA_TOPIC"));
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expectMessage("KAFKA_TOPIC should be set in WITH clause of CREATE STREAM/TABLE statement.");
        expectedException.expectMessage(DefaultSchemaInjectorTest.SQL_TEXT);
        // When:
        injector.forStatement(ctStatement);
    }

    @Test
    public void shouldThrowIfSchemaNotRegisteredOrNotCompatible() {
        // Given:
        Mockito.when(schemaSupplier.getValueSchema(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(SchemaResult.failure(new KsqlException("schema missing or incompatible")));
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expectMessage("schema missing or incompatible");
        // When:
        injector.forStatement(ctStatement);
    }

    @Test
    public void shouldAddElementsToCsStatement() {
        // Given:
        Mockito.when(schemaSupplier.getValueSchema(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(SchemaResult.success(schemaAndId(DefaultSchemaInjectorTest.SUPPORTED_SCHEMA, DefaultSchemaInjectorTest.SCHEMA_ID)));
        // When:
        final PreparedStatement<CreateStream> result = injector.forStatement(csStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatement().getElements(), Matchers.is(DefaultSchemaInjectorTest.EXPECTED_KSQL_SCHEMA));
    }

    @Test
    public void shouldAddElementsToCtStatement() {
        // Given:
        Mockito.when(schemaSupplier.getValueSchema(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(SchemaResult.success(schemaAndId(DefaultSchemaInjectorTest.SUPPORTED_SCHEMA, DefaultSchemaInjectorTest.SCHEMA_ID)));
        // When:
        final PreparedStatement<CreateTable> result = injector.forStatement(ctStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatement().getElements(), Matchers.is(DefaultSchemaInjectorTest.EXPECTED_KSQL_SCHEMA));
    }

    @Test
    public void shouldBuildNewCsStatementText() {
        // Given:
        Mockito.when(schemaSupplier.getValueSchema(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(SchemaResult.success(schemaAndId(DefaultSchemaInjectorTest.SUPPORTED_SCHEMA, DefaultSchemaInjectorTest.SCHEMA_ID)));
        // When:
        final PreparedStatement<CreateStream> result = injector.forStatement(csStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatementText(), Matchers.is(("CREATE STREAM cs (" + ((((((((("INTFIELD INTEGER, " + "BIGINTFIELD BIGINT, ") + "FLOATFIELD DOUBLE, ") + "DOUBLEFIELD DOUBLE, ") + "STRINGFIELD STRING, ") + "BOOLEANFIELD BOOLEAN, ") + "ARRAYFIELD ARRAY<INTEGER>, ") + "MAPFIELD MAP<VARCHAR, BIGINT>, ") + "STRUCTFIELD STRUCT<s0 BIGINT>) ") + "WITH (VALUE_FORMAT='avro', KAFKA_TOPIC='some-topic', AVRO_SCHEMA_ID='5');"))));
    }

    @Test
    public void shouldBuildNewCtStatementText() {
        // Given:
        Mockito.when(schemaSupplier.getValueSchema(DefaultSchemaInjectorTest.KAFKA_TOPIC, Optional.empty())).thenReturn(SchemaResult.success(schemaAndId(DefaultSchemaInjectorTest.SUPPORTED_SCHEMA, DefaultSchemaInjectorTest.SCHEMA_ID)));
        // When:
        final PreparedStatement<CreateTable> result = injector.forStatement(ctStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatementText(), Matchers.is(("CREATE TABLE ct (" + ((((((((("INTFIELD INTEGER, " + "BIGINTFIELD BIGINT, ") + "FLOATFIELD DOUBLE, ") + "DOUBLEFIELD DOUBLE, ") + "STRINGFIELD STRING, ") + "BOOLEANFIELD BOOLEAN, ") + "ARRAYFIELD ARRAY<INTEGER>, ") + "MAPFIELD MAP<VARCHAR, BIGINT>, ") + "STRUCTFIELD STRUCT<s0 BIGINT>) ") + "WITH (VALUE_FORMAT='avro', KAFKA_TOPIC='some-topic', AVRO_SCHEMA_ID='5');"))));
    }

    @Test
    public void shouldBuildNewCsStatementTextFromId() {
        // Given:
        Mockito.when(cs.getProperties()).thenReturn(DefaultSchemaInjectorTest.supportedPropsWith("AVRO_SCHEMA_ID", "42"));
        Mockito.when(schemaSupplier.getValueSchema(DefaultSchemaInjectorTest.KAFKA_TOPIC, Optional.of(42))).thenReturn(SchemaResult.success(schemaAndId(DefaultSchemaInjectorTest.SUPPORTED_SCHEMA, DefaultSchemaInjectorTest.SCHEMA_ID)));
        // When:
        final PreparedStatement<CreateStream> result = injector.forStatement(csStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatementText(), Matchers.is(("CREATE STREAM cs (" + ((((((((("INTFIELD INTEGER, " + "BIGINTFIELD BIGINT, ") + "FLOATFIELD DOUBLE, ") + "DOUBLEFIELD DOUBLE, ") + "STRINGFIELD STRING, ") + "BOOLEANFIELD BOOLEAN, ") + "ARRAYFIELD ARRAY<INTEGER>, ") + "MAPFIELD MAP<VARCHAR, BIGINT>, ") + "STRUCTFIELD STRUCT<s0 BIGINT>) ") + "WITH (VALUE_FORMAT='avro', KAFKA_TOPIC='some-topic', AVRO_SCHEMA_ID='42');"))));
    }

    @Test
    public void shouldBuildNewCtStatementTextFromId() {
        // Given:
        Mockito.when(ct.getProperties()).thenReturn(DefaultSchemaInjectorTest.supportedPropsWith("AVRO_SCHEMA_ID", "42"));
        Mockito.when(schemaSupplier.getValueSchema(DefaultSchemaInjectorTest.KAFKA_TOPIC, Optional.of(42))).thenReturn(SchemaResult.success(schemaAndId(DefaultSchemaInjectorTest.SUPPORTED_SCHEMA, DefaultSchemaInjectorTest.SCHEMA_ID)));
        // When:
        final PreparedStatement<CreateTable> result = injector.forStatement(ctStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatementText(), Matchers.is(("CREATE TABLE ct (" + ((((((((("INTFIELD INTEGER, " + "BIGINTFIELD BIGINT, ") + "FLOATFIELD DOUBLE, ") + "DOUBLEFIELD DOUBLE, ") + "STRINGFIELD STRING, ") + "BOOLEANFIELD BOOLEAN, ") + "ARRAYFIELD ARRAY<INTEGER>, ") + "MAPFIELD MAP<VARCHAR, BIGINT>, ") + "STRUCTFIELD STRUCT<s0 BIGINT>) ") + "WITH (VALUE_FORMAT='avro', KAFKA_TOPIC='some-topic', AVRO_SCHEMA_ID='42');"))));
    }

    @Test
    public void shouldAddSchemaIdIfNotPresentAlready() {
        // Given:
        Mockito.when(schemaSupplier.getValueSchema(DefaultSchemaInjectorTest.KAFKA_TOPIC, Optional.empty())).thenReturn(SchemaResult.success(schemaAndId(DefaultSchemaInjectorTest.SUPPORTED_SCHEMA, DefaultSchemaInjectorTest.SCHEMA_ID)));
        // When:
        final PreparedStatement<CreateStream> result = injector.forStatement(csStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatement().getProperties().get("AVRO_SCHEMA_ID"), Matchers.is(new StringLiteral(String.valueOf(DefaultSchemaInjectorTest.SCHEMA_ID))));
        MatcherAssert.assertThat(result.getStatementText(), Matchers.containsString("AVRO_SCHEMA_ID='5'"));
    }

    @Test
    public void shouldNotOverwriteExistingSchemaId() {
        // Given:
        Mockito.when(cs.getProperties()).thenReturn(DefaultSchemaInjectorTest.supportedPropsWith("AVRO_SCHEMA_ID", "42"));
        // When:
        final PreparedStatement<CreateStream> result = injector.forStatement(csStatement);
        // Then:
        MatcherAssert.assertThat(result.getStatement().getProperties().get("AVRO_SCHEMA_ID"), Matchers.is(new StringLiteral("42")));
        MatcherAssert.assertThat(result.getStatementText(), Matchers.containsString("AVRO_SCHEMA_ID='42'"));
    }

    @Test
    public void shouldThrowOnUnsupportedType() {
        for (final Schema unsupportedSchema : DefaultSchemaInjectorTest.UNSUPPORTED_SCHEMAS) {
            // Given:
            Mockito.when(schemaSupplier.getValueSchema(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(SchemaResult.success(schemaAndId(unsupportedSchema, DefaultSchemaInjectorTest.SCHEMA_ID)));
            try {
                // When:
                injector.forStatement(ctStatement);
                // Then:
                Assert.fail(("Expected KsqlStatementException. schema: " + unsupportedSchema));
            } catch (final KsqlStatementException e) {
                MatcherAssert.assertThat(e.getRawMessage(), Matchers.containsString("Failed to convert schema to KSQL model:"));
                MatcherAssert.assertThat(e.getSqlStatement(), Matchers.is(csStatement.getStatementText()));
            }
        }
    }

    @Test
    public void shouldThrowIfSchemaSupplierThrows() {
        // Given:
        Mockito.when(schemaSupplier.getValueSchema(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new KsqlException("Oh no!"));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expect(Matchers.not(Matchers.instanceOf(KsqlStatementException.class)));
        expectedException.expectMessage("Oh no");
        // When:
        injector.forStatement(csStatement);
    }
}

