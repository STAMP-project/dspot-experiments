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


import ParseContext.ACCEPT_LINE;
import ParseContext.COMPLETE;
import java.util.function.Predicate;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.hamcrest.Matchers;
import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class KsqlLineParserTest {
    private static final String UNTERMINATED_LINE = "an unterminated line";

    private static final String TERMINATED_LINE = "a terminated line;";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Parser delegate;

    @Mock
    private ParsedLine parsedLine;

    @Mock
    private Predicate<String> cliLinePredicate;

    private KsqlLineParser parser;

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnNullParam() {
        new KsqlLineParser(null, cliLinePredicate);
    }

    @Test
    public void shouldCallDelegateWithCorrectParams() {
        // Given:
        EasyMock.expect(parsedLine.line()).andReturn(KsqlLineParserTest.TERMINATED_LINE).anyTimes();
        EasyMock.expect(delegate.parse("some-string", 55, ACCEPT_LINE)).andReturn(parsedLine);
        EasyMock.replay(delegate, parsedLine);
        // When:
        parser.parse("some-string", 55, ACCEPT_LINE);
        // Then:
        EasyMock.verify(delegate);
    }

    @Test
    public void shouldReturnResultIfNotAcceptLine() {
        // Given:
        givenDelegateWillReturn(KsqlLineParserTest.UNTERMINATED_LINE);
        // When:
        final ParsedLine result = parser.parse("what ever", 0, COMPLETE);
        // Then:
        Assert.assertThat(result, Matchers.is(parsedLine));
    }

    @Test
    public void shouldAcceptIfEmptyLine() {
        // Given:
        givenDelegateWillReturn("");
        // When:
        final ParsedLine result = parser.parse("what ever", 0, ACCEPT_LINE);
        // Then:
        Assert.assertThat(result, Matchers.is(parsedLine));
    }

    @Test
    public void shouldAcceptIfLineTerminated() {
        // Given:
        givenDelegateWillReturn(KsqlLineParserTest.TERMINATED_LINE);
        // When:
        final ParsedLine result = parser.parse("what ever", 0, ACCEPT_LINE);
        // Then:
        Assert.assertThat(result, Matchers.is(parsedLine));
    }

    @Test
    public void shouldAcceptIfPredicateReturnsTrue() {
        // Given:
        givenPredicateWillReturnTrue();
        givenDelegateWillReturn(KsqlLineParserTest.UNTERMINATED_LINE);
        // When:
        final ParsedLine result = parser.parse("what ever", 0, ACCEPT_LINE);
        // Then:
        Assert.assertThat(result, Matchers.is(parsedLine));
    }

    @Test
    public void shouldNotAcceptUnterminatedAcceptLine() {
        // Given:
        expectedException.expect(EOFError.class);
        expectedException.expectMessage("Missing termination char");
        givenDelegateWillReturn(KsqlLineParserTest.UNTERMINATED_LINE);
        // When:
        parser.parse("what ever", 0, ACCEPT_LINE);
    }

    @Test
    public void shouldAlwaysAcceptCommentLines() {
        // Given:
        givenDelegateWillReturn(" -- this is a comment");
        // When:
        final ParsedLine result = parser.parse("what ever", 0, ACCEPT_LINE);
        // Then:
        Assert.assertThat(result, Matchers.is(parsedLine));
    }

    @Test
    public void shouldAcceptTerminatedLineEndingInComment() {
        // Given:
        givenDelegateWillReturn(((KsqlLineParserTest.TERMINATED_LINE) + " -- this is a comment"));
        // When:
        final ParsedLine result = parser.parse("what ever", 0, ACCEPT_LINE);
        // Then:
        Assert.assertThat(result, Matchers.is(parsedLine));
    }

    @Test
    public void shouldNotAcceptUnterminatedLineEndingInComment() {
        // Given:
        givenDelegateWillReturn(((KsqlLineParserTest.UNTERMINATED_LINE) + " -- this is a comment"));
        expectedException.expect(EOFError.class);
        expectedException.expectMessage("Missing termination char");
        // When:
        parser.parse("what ever", 0, ACCEPT_LINE);
    }
}

