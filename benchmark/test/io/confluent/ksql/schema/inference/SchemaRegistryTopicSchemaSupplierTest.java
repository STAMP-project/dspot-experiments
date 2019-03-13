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


import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.ksql.schema.inference.TopicSchemaSupplier.SchemaResult;
import io.confluent.ksql.util.KsqlException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import org.apache.avro.Schema;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("ConstantConditions")
@RunWith(MockitoJUnitRunner.class)
public class SchemaRegistryTopicSchemaSupplierTest {
    private static final String TOPIC_NAME = "some-topic";

    private static final int SCHEMA_ID = 12;

    private static final String AVRO_SCHEMA = "{use your imagination}";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SchemaRegistryClient srClient;

    @Mock
    private Function<String, Schema> toAvroTranslator;

    @Mock
    private Function<Schema, org.apache.kafka.connect.data.Schema> toConnectTranslator;

    @Mock
    private Function<org.apache.kafka.connect.data.Schema, org.apache.kafka.connect.data.Schema> toKsqlTranslator;

    @Mock
    private org.apache.kafka.connect.data.Schema avroSchema;

    @Mock
    private org.apache.kafka.connect.data.Schema connectSchema;

    @Mock
    private org.apache.kafka.connect.data.Schema ksqlSchema;

    private SchemaRegistryTopicSchemaSupplier supplier;

    @Test
    public void shouldReturnErrorFromGetValueSchemaIfNotFound() throws Exception {
        // Given:
        Mockito.when(srClient.getLatestSchemaMetadata(ArgumentMatchers.any())).thenThrow(SchemaRegistryTopicSchemaSupplierTest.notFoundException());
        // When:
        final SchemaResult result = supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        MatcherAssert.assertThat(result.schemaAndId, Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(result.failureReason, Matchers.is(Matchers.not(Optional.empty())));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString((("Avro schema for message values on topic " + (SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME)) + " does not exist in the Schema Registry.")));
    }

    @Test
    public void shouldReturnErrorFromGetValueWithIdSchemaIfNotFound() throws Exception {
        // Given:
        Mockito.when(srClient.getSchemaMetadata(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenThrow(SchemaRegistryTopicSchemaSupplierTest.notFoundException());
        // When:
        final SchemaResult result = supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.of(42));
        // Then:
        MatcherAssert.assertThat(result.schemaAndId, Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(result.failureReason, Matchers.is(Matchers.not(Optional.empty())));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString((("Avro schema for message values on topic " + (SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME)) + " does not exist in the Schema Registry.")));
    }

    @Test
    public void shouldThrowFromGetValueSchemaOnOtherRestExceptions() throws Exception {
        // Given:
        Mockito.when(srClient.getLatestSchemaMetadata(ArgumentMatchers.any())).thenThrow(new RestClientException("failure", 1, 1));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage((("Schema registry fetch for topic " + (SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME)) + " request failed."));
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
    }

    @Test
    public void shouldThrowFromGetValueWithIdSchemaOnOtherRestExceptions() throws Exception {
        // Given:
        Mockito.when(srClient.getSchemaMetadata(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenThrow(new RestClientException("failure", 1, 1));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage((("Schema registry fetch for topic " + (SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME)) + " request failed."));
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.of(42));
    }

    @Test
    public void shouldThrowFromGetValueSchemaOnOtherException() throws Exception {
        // Given:
        Mockito.when(srClient.getLatestSchemaMetadata(ArgumentMatchers.any())).thenThrow(new IOException("boom"));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage((("Schema registry fetch for topic " + (SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME)) + " request failed."));
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
    }

    @Test
    public void shouldThrowFromGetValueWithIdSchemaOnOtherException() throws Exception {
        // Given:
        Mockito.when(srClient.getSchemaMetadata(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenThrow(new IOException("boom"));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage((("Schema registry fetch for topic " + (SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME)) + " request failed."));
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.of(42));
    }

    @Test
    public void shouldReturnErrorFromGetValueSchemaIfCanNotConvertToAvroSchema() {
        // Given:
        Mockito.when(toAvroTranslator.apply(ArgumentMatchers.any())).thenThrow(new RuntimeException("it went boom"));
        // When:
        final SchemaResult result = supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        MatcherAssert.assertThat(result.schemaAndId, Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString("Unable to verify if the schema for topic some-topic is compatible with KSQL."));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString("it went boom"));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString(SchemaRegistryTopicSchemaSupplierTest.AVRO_SCHEMA));
    }

    @Test
    public void shouldReturnErrorFromGetValueSchemaIfCanNotConvertToConnectSchema() {
        // Given:
        Mockito.when(toConnectTranslator.apply(ArgumentMatchers.any())).thenThrow(new RuntimeException("it went boom"));
        // When:
        final SchemaResult result = supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        MatcherAssert.assertThat(result.schemaAndId, Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString("Unable to verify if the schema for topic some-topic is compatible with KSQL."));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString("it went boom"));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString(SchemaRegistryTopicSchemaSupplierTest.AVRO_SCHEMA));
    }

    @Test
    public void shouldReturnErrorFromGetValueSchemaIfCanNotConvertToKsqlSchema() {
        // Given:
        Mockito.when(toKsqlTranslator.apply(ArgumentMatchers.any())).thenThrow(new RuntimeException("big badda boom"));
        // When:
        final SchemaResult result = supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        MatcherAssert.assertThat(result.schemaAndId, Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString("Unable to verify if the schema for topic some-topic is compatible with KSQL."));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString("big badda boom"));
        MatcherAssert.assertThat(result.failureReason.get().getMessage(), Matchers.containsString(SchemaRegistryTopicSchemaSupplierTest.AVRO_SCHEMA));
    }

    @Test
    public void shouldRequestCorrectSchemaOnGetValueSchema() throws Exception {
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        Mockito.verify(srClient).getLatestSchemaMetadata(((SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME) + "-value"));
    }

    @Test
    public void shouldRequestCorrectSchemaOnGetValueSchemaWithId() throws Exception {
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.of(42));
        // Then:
        Mockito.verify(srClient).getSchemaMetadata(((SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME) + "-value"), 42);
    }

    @Test
    public void shouldPassWriteSchemaToAvroTranslator() {
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        Mockito.verify(toAvroTranslator).apply(SchemaRegistryTopicSchemaSupplierTest.AVRO_SCHEMA);
    }

    @Test
    public void shouldPassWriteSchemaToConnectTranslator() {
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        Mockito.verify(toConnectTranslator).apply(avroSchema);
    }

    @Test
    public void shouldPassWriteSchemaToKsqlTranslator() {
        // When:
        supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        Mockito.verify(toKsqlTranslator).apply(connectSchema);
    }

    @Test
    public void shouldReturnSchemaFromGetValueSchemaIfFound() {
        // When:
        final SchemaResult result = supplier.getValueSchema(SchemaRegistryTopicSchemaSupplierTest.TOPIC_NAME, Optional.empty());
        // Then:
        MatcherAssert.assertThat(result.schemaAndId, Matchers.is(Matchers.not(Optional.empty())));
        MatcherAssert.assertThat(result.schemaAndId.get().id, Matchers.is(SchemaRegistryTopicSchemaSupplierTest.SCHEMA_ID));
        MatcherAssert.assertThat(result.schemaAndId.get().schema, Matchers.is(ksqlSchema));
    }
}

