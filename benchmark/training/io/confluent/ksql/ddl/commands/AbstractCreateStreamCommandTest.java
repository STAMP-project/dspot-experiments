/**
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License; you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.ddl.commands;


import DdlConfig.KAFKA_TOPIC_NAME_PROPERTY;
import DdlConfig.VALUE_FORMAT_PROPERTY;
import SqlType.STRING;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.tree.AbstractStreamCreateStatement;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.TableElement;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.util.KsqlException;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AbstractCreateStreamCommandTest {
    private static final String TOPIC_NAME = "some topic";

    private static final List<TableElement> SOME_ELEMENTS = ImmutableList.of(new TableElement("bob", PrimitiveType.of(STRING)));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private AbstractStreamCreateStatement statement;

    @Mock
    private KafkaTopicClient kafkaTopicClient;

    @Test
    public void shouldThrowOnNoElements() {
        // Given:
        Mockito.when(statement.getElements()).thenReturn(Collections.emptyList());
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("The statement does not define any columns.");
        // When:
        new AbstractCreateStreamCommandTest.TestCmd("look mum, no columns", statement, kafkaTopicClient);
    }

    @Test
    public void shouldNotThrowWhenThereAreElements() {
        // Given:
        Mockito.when(statement.getElements()).thenReturn(AbstractCreateStreamCommandTest.SOME_ELEMENTS);
        // When:
        new AbstractCreateStreamCommandTest.TestCmd("look mum, columns", statement, kafkaTopicClient);
        // Then: not exception thrown
    }

    @Test
    public void shouldThrowIfValueFormatNotSupplied() {
        // Given:
        Mockito.when(statement.getProperties()).thenReturn(AbstractCreateStreamCommandTest.propsWithout(VALUE_FORMAT_PROPERTY));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Topic format(VALUE_FORMAT) should be set in WITH clause.");
        // When:
        new AbstractCreateStreamCommandTest.TestCmd("what, no value format?", statement, kafkaTopicClient);
    }

    @Test
    public void shouldThrowIfTopicNameNotSupplied() {
        // Given:
        Mockito.when(statement.getProperties()).thenReturn(AbstractCreateStreamCommandTest.propsWithout(KAFKA_TOPIC_NAME_PROPERTY));
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Corresponding Kafka topic (KAFKA_TOPIC) should be set in WITH clause.");
        // When:
        new AbstractCreateStreamCommandTest.TestCmd("what, no value topic?", statement, kafkaTopicClient);
    }

    @Test
    public void shouldThrowIfTopicDoesNotExist() {
        // Given:
        Mockito.when(kafkaTopicClient.isTopicExists(ArgumentMatchers.any())).thenReturn(false);
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage(("Kafka topic does not exist: " + (AbstractCreateStreamCommandTest.TOPIC_NAME)));
        // When:
        new AbstractCreateStreamCommandTest.TestCmd("what, no value topic?", statement, kafkaTopicClient);
    }

    @Test
    public void shouldNotThrowIfTopicDoesExist() {
        // Given:
        Mockito.when(kafkaTopicClient.isTopicExists(AbstractCreateStreamCommandTest.TOPIC_NAME)).thenReturn(true);
        // When:
        new AbstractCreateStreamCommandTest.TestCmd("what, no value topic?", statement, kafkaTopicClient);
        // Then:
        Mockito.verify(kafkaTopicClient).isTopicExists(AbstractCreateStreamCommandTest.TOPIC_NAME);
    }

    private static final class TestCmd extends AbstractCreateStreamCommand {
        private TestCmd(final String sqlExpression, final AbstractStreamCreateStatement statement, final KafkaTopicClient kafkaTopicClient) {
            super(sqlExpression, statement, kafkaTopicClient);
        }

        @Override
        public DdlCommandResult run(final MutableMetaStore metaStore) {
            return null;
        }
    }
}

