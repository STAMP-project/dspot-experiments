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
package io.confluent.ksql.cli.console;


import CommandStatus.Status;
import DataSource.DataSourceSerDe;
import DataSource.DataSourceType.KTABLE;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.FakeException;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.TestTerminal;
import io.confluent.ksql.cli.console.Console.NoOpRowCaptor;
import io.confluent.ksql.cli.console.cmd.CliSpecificCommand;
import io.confluent.ksql.rest.entity.ArgumentInfo;
import io.confluent.ksql.rest.entity.EntityQueryId;
import io.confluent.ksql.rest.entity.ExecutionPlan;
import io.confluent.ksql.rest.entity.FunctionType;
import io.confluent.ksql.rest.entity.KafkaTopicInfo;
import io.confluent.ksql.rest.entity.KsqlEntityList;
import io.confluent.ksql.rest.entity.PropertiesList;
import io.confluent.ksql.rest.entity.RunningQuery;
import io.confluent.ksql.rest.entity.SourceDescriptionEntity;
import io.confluent.ksql.rest.entity.SourceInfo;
import io.confluent.ksql.rest.entity.StreamedRow;
import io.confluent.ksql.rest.entity.TopicDescription;
import io.confluent.ksql.rest.server.computation.CommandId;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static OutputFormat.JSON;


@RunWith(Parameterized.class)
public class ConsoleTest {
    private static final String CLI_CMD_NAME = "some command";

    private static final String WHITE_SPACE = " \t ";

    private final TestTerminal terminal;

    private final Console console;

    private final Supplier<String> lineSupplier;

    private final CliSpecificCommand cliCommand;

    @SuppressWarnings("unchecked")
    public ConsoleTest(final OutputFormat outputFormat) {
        this.lineSupplier = Mockito.mock(Supplier.class);
        this.cliCommand = Mockito.mock(CliSpecificCommand.class);
        this.terminal = new TestTerminal(lineSupplier);
        this.console = new Console(outputFormat, terminal, new NoOpRowCaptor());
        Mockito.when(cliCommand.getName()).thenReturn(ConsoleTest.CLI_CMD_NAME);
        console.registerCliSpecificCommand(cliCommand);
    }

    @Test
    public void testPrintGenericStreamedRow() throws IOException {
        final StreamedRow row = StreamedRow.row(new GenericRow(ImmutableList.of("col_1", "col_2")));
        console.printStreamedRow(row);
    }

    @Test
    public void testPrintErrorStreamedRow() throws IOException {
        final FakeException exception = new FakeException();
        console.printStreamedRow(StreamedRow.error(exception));
        MatcherAssert.assertThat(terminal.getOutputString(), Matchers.is(((exception.getMessage()) + "\n")));
    }

    @Test
    public void testPrintFinalMessageStreamedRow() throws IOException {
        console.printStreamedRow(StreamedRow.finalMessage("Some message"));
        MatcherAssert.assertThat(terminal.getOutputString(), Matchers.is("Some message\n"));
    }

    @Test
    public void testPrintKSqlEntityList() throws IOException {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("k1", 1);
        properties.put("k2", "v2");
        properties.put("k3", true);
        final List<RunningQuery> queries = new ArrayList<>();
        queries.add(new RunningQuery("select * from t1", Collections.singleton("Test"), new EntityQueryId("0")));
        for (int i = 0; i < 5; i++) {
            final KsqlEntityList entityList = new KsqlEntityList(ImmutableList.of(new io.confluent.ksql.rest.entity.CommandStatusEntity("e", CommandId.fromString("topic/1/create"), new io.confluent.ksql.rest.entity.CommandStatus(Status.SUCCESS, "Success Message"), 0L), new PropertiesList("e", properties, Collections.emptyList(), Collections.emptyList()), new io.confluent.ksql.rest.entity.Queries("e", queries), new SourceDescriptionEntity("e", new io.confluent.ksql.rest.entity.SourceDescription("TestSource", Collections.emptyList(), Collections.emptyList(), ConsoleTest.buildTestSchema(i), KTABLE.getKqlType(), "key", "2000-01-01", "stats", "errors", false, "avro", "kadka-topic", 1, 1)), new TopicDescription("e", "TestTopic", "TestKafkaTopic", "AVRO", "schemaString"), new io.confluent.ksql.rest.entity.StreamsList("e", ImmutableList.of(new SourceInfo.Stream("TestStream", "TestTopic", "AVRO"))), new io.confluent.ksql.rest.entity.TablesList("e", ImmutableList.of(new SourceInfo.Table("TestTable", "TestTopic", "JSON", false))), new io.confluent.ksql.rest.entity.KsqlTopicsList("e", ImmutableList.of(new io.confluent.ksql.rest.entity.KsqlTopicInfo("TestTopic", "TestKafkaTopic", DataSourceSerDe.JSON))), new io.confluent.ksql.rest.entity.KafkaTopicsList("e", ImmutableList.of(new KafkaTopicInfo("TestKafkaTopic", true, ImmutableList.of(1), 1, 1))), new ExecutionPlan("Test Execution Plan")));
            console.printKsqlEntityList(entityList);
        }
    }

    @Test
    public void shouldPrintTopicDescribeExtended() throws IOException {
        final KsqlEntityList entityList = new KsqlEntityList(ImmutableList.of(new SourceDescriptionEntity("e", new io.confluent.ksql.rest.entity.SourceDescription("TestSource", Collections.emptyList(), Collections.emptyList(), ConsoleTest.buildTestSchema(2), KTABLE.getKqlType(), "key", "2000-01-01", "stats", "errors", true, "avro", "kadka-topic", 2, 1))));
        console.printKsqlEntityList(entityList);
        final String output = terminal.getOutputString();
        if ((console.getOutputFormat()) == (JSON)) {
            MatcherAssert.assertThat(output, Matchers.containsString("\"topic\" : \"kadka-topic\""));
        } else {
            MatcherAssert.assertThat(output, Matchers.containsString("Kafka topic          : kadka-topic (partitions: 2, replication: 1)"));
        }
    }

    @Test
    public void shouldPrintFunctionDescription() throws IOException {
        final KsqlEntityList entityList = new KsqlEntityList(ImmutableList.of(new io.confluent.ksql.rest.entity.FunctionDescriptionList("DESCRIBE FUNCTION foo;", "FOO", ("Description that is very, very, very, very, very, very, very, very, very, " + ((("very, very, very, very, very, very, very, very, very, very, very, very long\n" + "and containing new lines\n") + "\tAND TABS\n") + "too!")), "Andy", "v1.1.0", "some.jar", ImmutableList.of(new io.confluent.ksql.rest.entity.FunctionInfo(ImmutableList.of(new ArgumentInfo("arg1", "INT", ("Another really, really, really, really, really, really, really," + (((("really, really, really, really, really, really, really, really " + " really, really, really, really, really, really, really, long\n") + "description\n") + "\tContaining Tabs\n") + "and stuff")))), "LONG", ("The function description, which too can be really, really, really, " + (("really, really, really, really, really, really, really, really, really, " + "really, really, really, really, really, really, really, really, long\n") + "and contains\n\ttabs and stuff")))), FunctionType.scalar)));
        console.printKsqlEntityList(entityList);
        final String output = terminal.getOutputString();
        if ((console.getOutputFormat()) == (JSON)) {
            MatcherAssert.assertThat(output, Matchers.containsString("\"name\" : \"FOO\""));
        } else {
            final String expected = "" + (((((((((((((((((((((((("Name        : FOO\n" + "Author      : Andy\n") + "Version     : v1.1.0\n") + "Overview    : Description that is very, very, very, very, very, very, very, very, very, very, very, \n") + "              very, very, very, very, very, very, very, very, very, very long\n") + "              and containing new lines\n") + "              \tAND TABS\n") + "              too!\n") + "Type        : scalar\n") + "Jar         : some.jar\n") + "Variations  : \n") + "\n") + "\tVariation   : FOO(arg1 INT)\n") + "\tReturns     : LONG\n") + "\tDescription : The function description, which too can be really, really, really, really, really, \n") + "                really, really, really, really, really, really, really, really, really, really, \n") + "                really, really, really, really, really, long\n") + "                and contains\n") + "                \ttabs and stuff\n") + "\targ1        : Another really, really, really, really, really, really, really,really, really, \n") + "                really, really, really, really, really, really  really, really, really, really, \n") + "                really, really, really, long\n") + "                description\n") + "                \tContaining Tabs\n") + "                and stuff");
            MatcherAssert.assertThat(output, Matchers.containsString(expected));
        }
    }

    @Test
    public void shouldExecuteCliCommands() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn(ConsoleTest.CLI_CMD_NAME).thenReturn("not a CLI command;");
        // When:
        console.readLine();
        // Then:
        Mockito.verify(cliCommand).execute(ArgumentMatchers.eq(ImmutableList.of()), ArgumentMatchers.any());
    }

    @Test
    public void shouldExecuteCliCommandWithArgsTrimmingWhiteSpace() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn(((((((ConsoleTest.CLI_CMD_NAME) + (ConsoleTest.WHITE_SPACE)) + "Arg0") + (ConsoleTest.WHITE_SPACE)) + "Arg1") + (ConsoleTest.WHITE_SPACE))).thenReturn("not a CLI command;");
        // When:
        console.readLine();
        // Then:
        Mockito.verify(cliCommand).execute(ArgumentMatchers.eq(ImmutableList.of("Arg0", "Arg1")), ArgumentMatchers.any());
    }

    @Test
    public void shouldExecuteCliCommandWithQuotedArgsContainingSpaces() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn((((((ConsoleTest.CLI_CMD_NAME) + (ConsoleTest.WHITE_SPACE)) + "Arg0") + (ConsoleTest.WHITE_SPACE)) + "'Arg 1'")).thenReturn("not a CLI command;");
        // When:
        console.readLine();
        // Then:
        Mockito.verify(cliCommand).execute(ArgumentMatchers.eq(ImmutableList.of("Arg0", "Arg 1")), ArgumentMatchers.any());
    }

    @Test
    public void shouldSupportOtherWhitespaceBetweenCliCommandAndArgs() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn(((((ConsoleTest.CLI_CMD_NAME) + "\tArg0") + (ConsoleTest.WHITE_SPACE)) + "'Arg 1'")).thenReturn("not a CLI command;");
        // When:
        console.readLine();
        // Then:
        Mockito.verify(cliCommand).execute(ArgumentMatchers.eq(ImmutableList.of("Arg0", "Arg 1")), ArgumentMatchers.any());
    }

    @Test
    public void shouldSupportCmdBeingTerminatedWithSemiColon() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn((((ConsoleTest.CLI_CMD_NAME) + (ConsoleTest.WHITE_SPACE)) + "Arg0;")).thenReturn("not a CLI command;");
        // When:
        console.readLine();
        // Then:
        Mockito.verify(cliCommand).execute(ArgumentMatchers.eq(ImmutableList.of("Arg0")), ArgumentMatchers.any());
    }

    @Test
    public void shouldSupportCmdWithQuotedArgBeingTerminatedWithSemiColon() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn((((ConsoleTest.CLI_CMD_NAME) + (ConsoleTest.WHITE_SPACE)) + "'Arg0';")).thenReturn("not a CLI command;");
        // When:
        console.readLine();
        // Then:
        Mockito.verify(cliCommand).execute(ArgumentMatchers.eq(ImmutableList.of("Arg0")), ArgumentMatchers.any());
    }

    @Test
    public void shouldFailIfCommandNameIsQuoted() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn(((("'some' 'command' " + "Arg0") + (ConsoleTest.WHITE_SPACE)) + "'Arg 1'")).thenReturn("not a CLI command;");
        // When:
        console.readLine();
        // Then:
        Mockito.verify(cliCommand, Mockito.never()).execute(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldSwallowCliCommandLines() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn(ConsoleTest.CLI_CMD_NAME).thenReturn("not a CLI command;");
        // When:
        final String result = console.readLine();
        // Then:
        MatcherAssert.assertThat(result, Matchers.is("not a CLI command;"));
    }

    @Test
    public void shouldSwallowCliCommandLinesEvenWithWhiteSpace() {
        // Given:
        Mockito.when(lineSupplier.get()).thenReturn((("   \t   " + (ConsoleTest.CLI_CMD_NAME)) + "   \t   ")).thenReturn("not a CLI command;");
        // When:
        final String result = console.readLine();
        // Then:
        MatcherAssert.assertThat(result, Matchers.is("not a CLI command;"));
    }
}

