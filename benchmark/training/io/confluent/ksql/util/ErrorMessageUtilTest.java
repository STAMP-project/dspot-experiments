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


import java.net.ConnectException;
import java.sql.SQLDataException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ErrorMessageUtilTest {
    @Test
    public void shouldBuildSimpleErrorMessage() {
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(new SQLDataException("some message", "some state", 422)), CoreMatchers.is("some message"));
    }

    @Test
    public void shouldUseCustomMsgForConnectException() {
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(new ConnectException("asdf")), CoreMatchers.is("Could not connect to the server."));
    }

    @Test
    public void shouldBuildErrorMessageFromExceptionWithNoMessage() {
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(new NullPointerException()), CoreMatchers.is("java.lang.NullPointerException"));
    }

    @Test
    public void shouldBuildErrorMessageFromExceptionChain() {
        final Throwable cause = new ErrorMessageUtilTest.TestException("Something went wrong");
        final Throwable subLevel2 = new ErrorMessageUtilTest.TestException("Intermediate message 2", cause);
        final Throwable subLevel1 = new ErrorMessageUtilTest.TestException("Intermediate message 1", subLevel2);
        final Throwable e = new ErrorMessageUtilTest.TestException("Top level", subLevel1);
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(e), CoreMatchers.is((((((("Top level" + (System.lineSeparator())) + "Caused by: Intermediate message 1") + (System.lineSeparator())) + "Caused by: Intermediate message 2") + (System.lineSeparator())) + "Caused by: Something went wrong")));
    }

    @Test
    public void shouldDeduplicateMessage() {
        final Throwable cause = new ErrorMessageUtilTest.TestException("Something went wrong");
        final Throwable subLevel2 = new ErrorMessageUtilTest.TestException("Msg that matches", cause);
        final Throwable subLevel1 = new ErrorMessageUtilTest.TestException("Msg that matches", subLevel2);
        final Throwable e = new ErrorMessageUtilTest.TestException("Msg that matches", subLevel1);
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(e), CoreMatchers.is((("Msg that matches" + (System.lineSeparator())) + "Caused by: Something went wrong")));
    }

    @Test
    public void shouldNotDeduplicateMessageIfNextMessageIsLonger() {
        final Throwable cause = new ErrorMessageUtilTest.TestException("Something went wrong");
        final Throwable subLevel1 = new ErrorMessageUtilTest.TestException("Some Message with more detail", cause);
        final Throwable e = new ErrorMessageUtilTest.TestException("Some Message", subLevel1);
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(e), CoreMatchers.is((((("Some Message" + (System.lineSeparator())) + "Caused by: Some Message with more detail") + (System.lineSeparator())) + "Caused by: Something went wrong")));
    }

    @Test
    public void shouldHandleRecursiveExceptions() {
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(new ErrorMessageUtilTest.RecursiveException("It went boom")), CoreMatchers.is("It went boom"));
    }

    @Test
    public void shouldHandleRecursiveExceptionChain() {
        final Exception cause = new ErrorMessageUtilTest.TestException("Something went wrong");
        final Throwable e = new ErrorMessageUtilTest.TestException("Top level", cause);
        cause.initCause(e);
        MatcherAssert.assertThat(ErrorMessageUtil.buildErrorMessage(e), CoreMatchers.is((("Top level" + (System.lineSeparator())) + "Caused by: Something went wrong")));
    }

    private static class TestException extends Exception {
        private TestException(final String msg) {
            super(msg);
        }

        private TestException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }

    private static class RecursiveException extends Exception {
        private RecursiveException(final String message) {
            super(message);
        }

        public synchronized Throwable getCause() {
            return this;
        }
    }
}

