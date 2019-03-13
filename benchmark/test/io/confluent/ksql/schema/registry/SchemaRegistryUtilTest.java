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
package io.confluent.ksql.schema.registry;


import com.google.common.collect.ImmutableList;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SchemaRegistryUtilTest {
    private static final String APP_ID = "_my_app_id";

    @Mock
    private SchemaRegistryClient schemaRegistryClient;

    @Test
    public void shouldDeleteChangeLogTopicSchema() throws Exception {
        // Given:
        Mockito.when(schemaRegistryClient.getAllSubjects()).thenReturn(ImmutableList.of(((SchemaRegistryUtilTest.APP_ID) + "SOME-changelog-value")));
        // When:
        SchemaRegistryUtil.cleanUpInternalTopicAvroSchemas(SchemaRegistryUtilTest.APP_ID, schemaRegistryClient);
        // Then not exception:
        Mockito.verify(schemaRegistryClient).deleteSubject(((SchemaRegistryUtilTest.APP_ID) + "SOME-changelog-value"));
    }

    @Test
    public void shouldDeleteRepartitionTopicSchema() throws Exception {
        // Given:
        Mockito.when(schemaRegistryClient.getAllSubjects()).thenReturn(ImmutableList.of(((SchemaRegistryUtilTest.APP_ID) + "SOME-repartition-value")));
        // When:
        SchemaRegistryUtil.cleanUpInternalTopicAvroSchemas(SchemaRegistryUtilTest.APP_ID, schemaRegistryClient);
        // Then not exception:
        Mockito.verify(schemaRegistryClient).deleteSubject(((SchemaRegistryUtilTest.APP_ID) + "SOME-repartition-value"));
    }

    @Test
    public void shouldNotDeleteOtherSchemasForThisApplicationId() throws Exception {
        // Given:
        Mockito.when(schemaRegistryClient.getAllSubjects()).thenReturn(ImmutableList.of(((SchemaRegistryUtilTest.APP_ID) + "SOME-other-value")));
        // When:
        SchemaRegistryUtil.cleanUpInternalTopicAvroSchemas(SchemaRegistryUtilTest.APP_ID, schemaRegistryClient);
        // Then not exception:
        Mockito.verify(schemaRegistryClient, Mockito.never()).deleteSubject(ArgumentMatchers.any());
    }

    @Test
    public void shouldNotDeleteOtherSchemas() throws Exception {
        // Given:
        Mockito.when(schemaRegistryClient.getAllSubjects()).thenReturn(ImmutableList.of("SOME-other-value"));
        // When:
        SchemaRegistryUtil.cleanUpInternalTopicAvroSchemas(SchemaRegistryUtilTest.APP_ID, schemaRegistryClient);
        // Then not exception:
        Mockito.verify(schemaRegistryClient, Mockito.never()).deleteSubject(ArgumentMatchers.any());
    }

    @Test
    public void shouldNotThrowIfAllSubjectsThrows() throws Exception {
        // Given:
        Mockito.when(schemaRegistryClient.getAllSubjects()).thenThrow(new RuntimeException("Boom!"));
        // When:
        SchemaRegistryUtil.cleanUpInternalTopicAvroSchemas(SchemaRegistryUtilTest.APP_ID, schemaRegistryClient);
        // Then not exception:
        Mockito.verify(schemaRegistryClient).getAllSubjects();
    }

    @Test
    public void shouldNotThrowIfDeleteSubjectThrows() throws Exception {
        // Given:
        Mockito.when(schemaRegistryClient.getAllSubjects()).thenReturn(ImmutableList.of(((SchemaRegistryUtilTest.APP_ID) + "SOME-changelog-value")));
        Mockito.when(schemaRegistryClient.deleteSubject(ArgumentMatchers.any())).thenThrow(new RuntimeException("Boom!"));
        // When:
        SchemaRegistryUtil.cleanUpInternalTopicAvroSchemas(SchemaRegistryUtilTest.APP_ID, schemaRegistryClient);
        // Then not exception:
        Mockito.verify(schemaRegistryClient, Mockito.times(5)).deleteSubject(ArgumentMatchers.any());
    }
}

