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


import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JLineReaderTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Mock
    private Predicate<String> cliLinePredicate;

    @Test
    public void shouldSaveCommandsWithLeadingSpacesToHistory() throws IOException {
        // Given:
        final String input = "  show streams;\n";
        final JLineReader reader = createReaderForInput(input);
        // When:
        reader.readLine();
        // Then:
        MatcherAssert.assertThat(JLineReaderTest.getHistory(reader), Matchers.contains(input.trim()));
    }

    @Test
    public void shouldExpandInlineMacro() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("csas\t* FROM Blah;\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("CREATE STREAM s AS SELECT * FROM Blah;"));
    }

    @Test
    public void shouldExpandHistoricalLine() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("foo;\n bar;\n  baz; \n!2\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("foo;", "bar;", "baz;", "bar;"));
    }

    @Test
    public void shouldExpandRelativeLine() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("foo;\n bar;\n  baz; \n!-3\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("foo;", "bar;", "baz;", "foo;"));
    }

    @Test
    public void shouldNotExpandHistoryUnlessAtStartOfLine() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("foo;\n bar;\n  baz; \n !2;\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("foo;", "bar;", "baz;", "!2;"));
    }

    @Test
    public void shouldExpandHistoricalSearch() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("foo;\n bar;\n  baz; \n!?ba\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("foo;", "bar;", "baz;", "baz;"));
    }

    @Test
    public void shouldExpandLastLine() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("foo;\n bar;\n  baz; \n!!\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("foo;", "bar;", "baz;", "baz;"));
    }

    @Test
    public void shouldExpandHistoricalLineWithReplacement() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("foo;\n select col1, col2 from d; \n^col2^xyz^\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("foo;", "select col1, col2 from d;", "select col1, xyz from d;"));
    }

    @Test
    public void shouldHandleSingleLine() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput("select * from foo;\n");
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("select * from foo;"));
    }

    @Test
    public void shouldHandleMultiLineUsingContinuationChar() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput(("select * \\\n" + "from foo;\n"));
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("select * from foo;"));
    }

    @Test
    public void shouldHandleMultiLineWithoutContinuationChar() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput(("select *\n\t" + "from foo;\n"));
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("select *\nfrom foo;"));
    }

    @Test
    public void shouldHandleMultiLineWithOpenQuotes() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput(("select * \'string that ends in termination char;\n" + "\' from foo;\n"));
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("select * \'string that ends in termination char;\n\' from foo;"));
    }

    @Test
    public void shouldHandleMultiLineWithComments() throws Exception {
        // Given:
        final JLineReader reader = createReaderForInput(("-- first inline comment\n" + ((("select * \'-- not comment\n" + "\' -- second inline comment\n") + "from foo; -- third inline comment\n") + "-- forth inline comment\n")));
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("-- first inline comment", "select * \'-- not comment\n\' -- second inline comment\nfrom foo; -- third inline comment", "-- forth inline comment"));
    }

    @Test
    public void shouldHandleCliCommandsWithInlineComments() throws Exception {
        // Given:
        Mockito.when(cliLinePredicate.test("Exit")).thenReturn(true);
        final JLineReader reader = createReaderForInput(("-- first inline comment\n" + ("Exit -- second inline comment\n" + " -- third inline comment\n")));
        // When:
        final List<String> commands = JLineReaderTest.readAllLines(reader);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.contains("-- first inline comment", "Exit -- second inline comment", "-- third inline comment"));
    }
}

