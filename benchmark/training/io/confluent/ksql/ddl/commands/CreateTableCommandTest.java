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
package io.confluent.ksql.ddl.commands;


import DdlConfig.WINDOW_TYPE_PROPERTY;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.tree.BooleanLiteral;
import io.confluent.ksql.parser.tree.CreateTable;
import io.confluent.ksql.parser.tree.StringLiteral;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.MetaStoreFixture;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.WindowedSerdes;
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


@RunWith(MockitoJUnitRunner.class)
public class CreateTableCommandTest {
    @Mock
    private KafkaTopicClient topicClient;

    @Mock
    private CreateTable createTableStatement;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final MutableMetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());

    @Test
    public void shouldDefaultToStringKeySerde() {
        // When:
        final CreateTableCommand cmd = createCmd();
        // Then:
        MatcherAssert.assertThat(cmd.keySerde, Matchers.is(Matchers.instanceOf(Serdes.String().getClass())));
    }

    @Test
    public void shouldExtractSessionWindowType() {
        // Given:
        givenPropertiesWith(ImmutableMap.of(WINDOW_TYPE_PROPERTY, new StringLiteral("SeSSion")));
        // When:
        final CreateTableCommand cmd = createCmd();
        // Then:
        MatcherAssert.assertThat(cmd.keySerde, Matchers.is(Matchers.instanceOf(WindowedSerdes.sessionWindowedSerdeFrom(String.class).getClass())));
    }

    @Test
    public void shouldExtractHoppingWindowType() {
        // Given:
        givenPropertiesWith(ImmutableMap.of(WINDOW_TYPE_PROPERTY, new StringLiteral("HoPPing")));
        // When:
        final CreateTableCommand cmd = createCmd();
        // Then:
        MatcherAssert.assertThat(cmd.keySerde, Matchers.is(Matchers.instanceOf(WindowedSerdes.timeWindowedSerdeFrom(String.class).getClass())));
    }

    @Test
    public void shouldExtractTumblingWindowType() {
        // Given:
        givenPropertiesWith(ImmutableMap.of(WINDOW_TYPE_PROPERTY, new StringLiteral("Tumbling")));
        // When:
        final CreateTableCommand cmd = createCmd();
        // Then:
        MatcherAssert.assertThat(cmd.keySerde, Matchers.is(Matchers.instanceOf(WindowedSerdes.timeWindowedSerdeFrom(String.class).getClass())));
    }

    @Test
    public void shouldThrowOnUnknownWindowType() {
        // Given:
        givenPropertiesWith(ImmutableMap.of(WINDOW_TYPE_PROPERTY, new StringLiteral("Unknown")));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("WINDOW_TYPE property is not set correctly. " + "value: UNKNOWN, validValues: [SESSION, TUMBLING, HOPPING]"));
        // When:
        createCmd();
    }

    @Test
    public void shouldThrowOnOldWindowProperty() {
        // Given:
        givenPropertiesWith(ImmutableMap.of("WINDOWED", new BooleanLiteral("true")));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Invalid config variable in the WITH clause: WINDOWED");
        // When:
        createCmd();
    }

    @Test
    public void shouldThrowIfTopicDoesNotExist() {
        // Given:
        Mockito.when(topicClient.isTopicExists(ArgumentMatchers.any())).thenReturn(false);
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Kafka topic does not exist: some-topic");
        // When:
        createCmd();
    }

    @Test
    public void testCreateAlreadyRegisteredTableThrowsException() {
        // Given:
        final CreateTableCommand cmd = createCmd();
        cmd.run(metaStore);
        // Then:
        expectedException.expectMessage(("Cannot create table 'name': A table " + "with name 'name' already exists"));
        // When:
        cmd.run(metaStore);
    }
}

