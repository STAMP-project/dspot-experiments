/**
 * Copyright 2018 Confluent Inc.
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
package io.confluent.ksql.util;


import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.ksql.metastore.KsqlTopic;
import java.io.IOException;
import org.apache.kafka.connect.data.Schema;
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

import static KsqlConstants.DEFAULT_AVRO_SCHEMA_FULL_NAME;


@RunWith(MockitoJUnitRunner.class)
public class AvroUtilTest {
    private static final String AVRO_SCHEMA_STRING = "{" + ((((((((((("\"namespace\": \"some.namespace\"," + " \"name\": \"orders\",") + " \"type\": \"record\",") + " \"fields\": [") + "     {\"name\": \"ordertime\", \"type\": \"long\"},") + "     {\"name\": \"orderid\",  \"type\": \"long\"},") + "     {\"name\": \"itemid\", \"type\": \"string\"},") + "     {\"name\": \"orderunits\", \"type\": \"double\"},") + "     {\"name\": \"arraycol\", \"type\": {\"type\": \"array\", \"items\": \"double\"}},") + "     {\"name\": \"mapcol\", \"type\": {\"type\": \"map\", \"values\": \"double\"}}") + " ]") + "}");

    private static final Schema RESULT_SCHEMA = AvroUtilTest.toKsqlSchema(AvroUtilTest.AVRO_SCHEMA_STRING);

    private static final KsqlTopic RESULT_TOPIC = new KsqlTopic("registered-name", "actual-name", new io.confluent.ksql.serde.avro.KsqlAvroTopicSerDe(DEFAULT_AVRO_SCHEMA_FULL_NAME), false);

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SchemaRegistryClient srClient;

    @Mock
    private PersistentQueryMetadata persistentQuery;

    @Test
    public void shouldValidateSchemaEvolutionWithCorrectSubject() throws Exception {
        // Given:
        Mockito.when(persistentQuery.getResultTopic()).thenReturn(AvroUtilTest.RESULT_TOPIC);
        // When:
        AvroUtil.isValidSchemaEvolution(persistentQuery, srClient);
        // Then:
        Mockito.verify(srClient).testCompatibility(ArgumentMatchers.eq(((AvroUtilTest.RESULT_TOPIC.getKafkaTopicName()) + "-value")), ArgumentMatchers.any());
    }

    @Test
    public void shouldValidateSchemaEvolutionWithCorrectSchema() throws Exception {
        // Given:
        Mockito.when(persistentQuery.getResultSchema()).thenReturn(AvroUtilTest.RESULT_SCHEMA);
        final org.apache.avro.Schema expectedAvroSchema = SchemaUtil.buildAvroSchema(AvroUtilTest.RESULT_SCHEMA, AvroUtilTest.RESULT_TOPIC.getName());
        // When:
        AvroUtil.isValidSchemaEvolution(persistentQuery, srClient);
        // Then:
        Mockito.verify(srClient).testCompatibility(ArgumentMatchers.any(), ArgumentMatchers.eq(expectedAvroSchema));
    }

    @Test
    public void shouldReturnValidEvolution() throws Exception {
        // Given:
        Mockito.when(srClient.testCompatibility(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        // When:
        final boolean result = AvroUtil.isValidSchemaEvolution(persistentQuery, srClient);
        // Then:
        Assert.assertThat(result, Matchers.is(true));
    }

    @Test
    public void shouldReturnInvalidEvolution() throws Exception {
        // Given:
        Mockito.when(srClient.testCompatibility(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(false);
        // When:
        final boolean result = AvroUtil.isValidSchemaEvolution(persistentQuery, srClient);
        // Then:
        Assert.assertThat(result, Matchers.is(false));
    }

    @Test
    public void shouldReturnValidEvolutionIfSubjectNotRegistered() throws Exception {
        // Given:
        Mockito.when(srClient.testCompatibility(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new RestClientException("Unknown subject", 404, 40401));
        // When:
        final boolean result = AvroUtil.isValidSchemaEvolution(persistentQuery, srClient);
        // Then:
        Assert.assertThat(result, Matchers.is(true));
    }

    @Test
    public void shouldThrowOnAnyOtherEvolutionSrException() throws Exception {
        // Given:
        Mockito.when(srClient.testCompatibility(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new RestClientException("Unknown subject", 403, 40401));
        // Expect:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Could not connect to Schema Registry service");
        // When:
        AvroUtil.isValidSchemaEvolution(persistentQuery, srClient);
    }

    @Test
    public void shouldThrowOnAnyOtherEvolutionIOException() throws Exception {
        // Given:
        Mockito.when(srClient.testCompatibility(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new IOException("something"));
        // Expect:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Could not check Schema compatibility");
        // When:
        AvroUtil.isValidSchemaEvolution(persistentQuery, srClient);
    }
}

