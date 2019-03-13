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


import DdlConfig.KEY_NAME_PROPERTY;
import DdlConfig.TIMESTAMP_NAME_PROPERTY;
import SqlType.STRING;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.parser.tree.ExecutableDdlStatement;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.QualifiedName;
import io.confluent.ksql.parser.tree.StringLiteral;
import io.confluent.ksql.parser.tree.TableElement;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CommandFactoriesTest {
    private static final Map<String, Object> NO_PROPS = Collections.emptyMap();

    private static final String sqlExpression = "sqlExpression";

    private static final List<TableElement> SOME_ELEMENTS = ImmutableList.of(new TableElement("bob", PrimitiveType.of(STRING)));

    private final KafkaTopicClient topicClient = EasyMock.createNiceMock(KafkaTopicClient.class);

    private final ServiceContext serviceContext = EasyMock.createNiceMock(ServiceContext.class);

    private final CommandFactories commandFactories = new CommandFactories(serviceContext);

    private final HashMap<String, Expression> properties = new HashMap<>();

    @Test
    public void shouldCreateDDLCommandForRegisterTopic() {
        final DdlCommand result = commandFactories.create(CommandFactoriesTest.sqlExpression, new io.confluent.ksql.parser.tree.RegisterTopic(QualifiedName.of("blah"), true, properties), CommandFactoriesTest.NO_PROPS);
        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(RegisterTopicCommand.class));
    }

    @Test
    public void shouldCreateCommandForCreateStream() {
        final DdlCommand result = commandFactories.create(CommandFactoriesTest.sqlExpression, new io.confluent.ksql.parser.tree.CreateStream(QualifiedName.of("foo"), CommandFactoriesTest.SOME_ELEMENTS, true, properties), CommandFactoriesTest.NO_PROPS);
        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(CreateStreamCommand.class));
    }

    @Test
    public void shouldCreateCommandForCreateTable() {
        final HashMap<String, Expression> tableProperties = validTableProps();
        final DdlCommand result = commandFactories.create(CommandFactoriesTest.sqlExpression, CommandFactoriesTest.createTable(tableProperties), CommandFactoriesTest.NO_PROPS);
        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(CreateTableCommand.class));
    }

    @Test
    public void shouldFailCreateTableIfKeyNameIsIncorrect() {
        final HashMap<String, Expression> tableProperties = validTableProps();
        tableProperties.put(KEY_NAME_PROPERTY, new StringLiteral("COL3"));
        try {
            commandFactories.create(CommandFactoriesTest.sqlExpression, CommandFactoriesTest.createTable(tableProperties), CommandFactoriesTest.NO_PROPS);
        } catch (final KsqlException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo(("No column with the provided key column name in the " + "WITH clause, COL3, exists in the defined schema.")));
        }
    }

    @Test
    public void shouldFailCreateTableIfTimestampColumnNameIsIncorrect() {
        final HashMap<String, Expression> tableProperties = validTableProps();
        tableProperties.put(TIMESTAMP_NAME_PROPERTY, new StringLiteral("COL3"));
        try {
            commandFactories.create(CommandFactoriesTest.sqlExpression, CommandFactoriesTest.createTable(tableProperties), CommandFactoriesTest.NO_PROPS);
        } catch (final KsqlException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo("No column with the provided timestamp column name in the WITH clause, COL3, exists in the defined schema."));
        }
    }

    @Test
    public void shouldFailCreateTableIfKeyIsNotProvided() {
        final HashMap<String, Expression> tableProperties = validTableProps();
        tableProperties.remove(KEY_NAME_PROPERTY);
        try {
            commandFactories.create(CommandFactoriesTest.sqlExpression, CommandFactoriesTest.createTable(properties), CommandFactoriesTest.NO_PROPS);
        } catch (final KsqlException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo("Cannot define a TABLE without providing the KEY column name in the WITH clause."));
        }
    }

    @Test
    public void shouldFailCreateTableIfTopicNotExist() {
        final HashMap<String, Expression> tableProperties = validTableProps();
        givenTopicsDoNotExist();
        try {
            commandFactories.create(CommandFactoriesTest.sqlExpression, CommandFactoriesTest.createTable(tableProperties), CommandFactoriesTest.NO_PROPS);
        } catch (final KsqlException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo("Kafka topic does not exist: topic"));
        }
    }

    @Test
    public void shouldCreateCommandForDropStream() {
        final DdlCommand result = commandFactories.create(CommandFactoriesTest.sqlExpression, new io.confluent.ksql.parser.tree.DropStream(QualifiedName.of("foo"), true, true), CommandFactoriesTest.NO_PROPS);
        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(DropSourceCommand.class));
    }

    @Test
    public void shouldCreateCommandForDropTable() {
        final DdlCommand result = commandFactories.create(CommandFactoriesTest.sqlExpression, new io.confluent.ksql.parser.tree.DropTable(QualifiedName.of("foo"), true, true), CommandFactoriesTest.NO_PROPS);
        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(DropSourceCommand.class));
    }

    @Test
    public void shouldCreateCommandForDropTopic() {
        final DdlCommand result = commandFactories.create(CommandFactoriesTest.sqlExpression, new io.confluent.ksql.parser.tree.DropTopic(QualifiedName.of("foo"), true), CommandFactoriesTest.NO_PROPS);
        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(DropTopicCommand.class));
    }

    @Test(expected = KsqlException.class)
    public void shouldThowKsqlExceptionIfCommandFactoryNotFound() {
        commandFactories.create(CommandFactoriesTest.sqlExpression, new ExecutableDdlStatement() {}, CommandFactoriesTest.NO_PROPS);
    }
}

