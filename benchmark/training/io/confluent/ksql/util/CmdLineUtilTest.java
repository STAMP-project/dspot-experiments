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


import org.junit.Test;


public class CmdLineUtilTest {
    @Test
    public void shouldSplitAndRemoveQuotesOnEmptyString() {
        CmdLineUtilTest.assertSplit("");
    }

    @Test
    public void shouldSplitAndRemoveQuotesOnSimpleArgs() {
        CmdLineUtilTest.assertSplit("arg0 arg1", "arg0", "arg1");
    }

    @Test
    public void shouldSplitSimpleQuotedArgs() {
        CmdLineUtilTest.assertSplit("'arg0' 'arg1'", "'arg0'", "'arg1'");
    }

    @Test
    public void shouldSplitArgsWithTabs() {
        CmdLineUtilTest.assertSplit("Arg \t Arg0 \t Arg1 \t ", "Arg", "Arg0", "Arg1");
    }

    @Test
    public void shouldSplitQuotedArgsWithLeadingTrailingWhiteSpace() {
        CmdLineUtilTest.assertSplit("\' \t arg0 \t \' \' arg1 \'", "\' \t arg0 \t \'", "' arg1 '");
    }

    @Test
    public void shouldNotSplitUnterminatedQuotes() {
        CmdLineUtilTest.assertSplit("'some unterminated string", "'some unterminated string");
    }

    @Test
    public void shouldNotSplitJustUnterminatedQuote() {
        CmdLineUtilTest.assertSplit("'", "'");
    }

    @Test
    public void shouldSplitEmptyQuotedString() {
        CmdLineUtilTest.assertSplit("''", "''");
    }

    @Test
    public void shouldSplitThreeSingleQuotes() {
        CmdLineUtilTest.assertSplit("'''", "'''");
    }

    @Test
    public void shouldSplitSingleQuotedEscapedQuote() {
        CmdLineUtilTest.assertSplit("''''", "''''");
    }

    @Test
    public void shouldSplitStringWithEmbeddedQuoted() {
        CmdLineUtilTest.assertSplit("'string''with''embedded''quotes'", "'string''with''embedded''quotes'");
    }

    @Test
    public void shouldSplitStringWithEmbeddedDoubleQuoted() {
        CmdLineUtilTest.assertSplit("string\"with\"double\"quotes", "string\"with\"double\"quotes");
    }

    @Test
    public void shouldSplitQuotedStringWithEmbeddedDoubleQuoted() {
        CmdLineUtilTest.assertSplit("\'string\"with\"double\"quotes\'", "\'string\"with\"double\"quotes\'");
    }

    @Test
    public void shouldIgnoreLeadingWhiteSpace() {
        CmdLineUtilTest.assertSplit(" some thing", "some", "thing");
    }

    @Test
    public void shouldHandleCharactersTrailingAfterQuoteEnds() {
        CmdLineUtilTest.assertSplit("'thing'one 'thing'two", "'thing'one", "'thing'two");
    }

    @Test
    public void shouldCheckSplitJavaDocExamplesAreRight() {
        CmdLineUtilTest.assertSplit("t0 t1", "t0", "t1");
        CmdLineUtilTest.assertSplit("'quoted string'", "'quoted string'");
        CmdLineUtilTest.assertSplit("'quoted 'connected' quoted'", "'quoted 'connected' quoted'");
        CmdLineUtilTest.assertSplit("'escaped '' quote'", "'escaped '' quote'");
    }

    @Test
    public void shouldRemoveQuotesFromUnQuotedString() {
        CmdLineUtilTest.assertMatchedQuotesRemoved(" some input ", " some input ");
    }

    @Test
    public void shouldRemoveSingleQuotes() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("' some input '", " some input ");
    }

    @Test
    public void shouldNotRemoveUnterminatedQuote() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("' some input ", "' some input ");
    }

    @Test
    public void shouldNotRemoveIfOnlyTrailingQuote() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("something'", "something'");
    }

    @Test
    public void shouldRemoveMultipleMatchingQuotes() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("a'quoted1'connnected'quoted2'a", "aquoted1connnectedquoted2a");
    }

    @Test
    public void shouldCorrectlyHandleEscapedQuotesWhenRemovingMatchedQuotes() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("'a '''' b ''' c '''' d '' e '' f '''", "a '' b ' c ' d  e  f '''");
    }

    @Test
    public void shouldStripSingleQuotedEscapedQuote() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("''''", "'");
    }

    @Test
    public void shouldStripThreeSingleQuotes() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("'''", "'''");
    }

    @Test
    public void shouldCheckRemoveQuotesJavaDocExamplesAreRight() {
        CmdLineUtilTest.assertMatchedQuotesRemoved("unquoted", "unquoted");
        CmdLineUtilTest.assertMatchedQuotesRemoved("'quoted string'", "quoted string");
        CmdLineUtilTest.assertMatchedQuotesRemoved("'quoted'connected'quoted'", "quotedconnectedquoted");
        CmdLineUtilTest.assertMatchedQuotesRemoved("escaped ' quote", "escaped ' quote");
    }
}

