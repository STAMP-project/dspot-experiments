/**
 * -
 * -\-\-
 * Helios Testing Library
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.testing;


import Level.INFO;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.common.base.Charsets;
import com.spotify.docker.client.LogMessage;
import com.spotify.helios.common.descriptors.JobId;
import java.util.Arrays;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


@SuppressWarnings("AvoidEscapedUnicodeCharacters")
public class LoggingLogStreamFollowerTest {
    private final LoggerContext context = new LoggerContext();

    private final Logger log = context.getLogger("test");

    private final CapturingAppender appender = CapturingAppender.create();

    @Test
    public void testSingleLine() throws Exception {
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout("abc123\n"));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 abc123")));
    }

    @Test
    public void testSingleLineNoNewline() throws Exception {
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout("abc123"));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 abc123")));
    }

    @Test
    public void testTwoLines() throws Exception {
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout("abc123\n123abc\n"));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 abc123"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 123abc")));
    }

    @Test
    public void testTwoLinesNoNewline() throws Exception {
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout("abc123\n123abc"));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 abc123"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 123abc")));
    }

    @Test
    public void testInterleavedStreams() throws Exception {
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout("abc"), LoggingLogStreamFollowerTest.stderr("123"), LoggingLogStreamFollowerTest.stdout("123"), LoggingLogStreamFollowerTest.stderr("abc"));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 abc"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 123"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 123"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 abc")));
    }

    @Test
    public void testInterleavedStreamsNewline() throws Exception {
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout("abc\n"), LoggingLogStreamFollowerTest.stderr("123\n"), LoggingLogStreamFollowerTest.stdout("123\n"), LoggingLogStreamFollowerTest.stderr("abc\n"));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 abc"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 123"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 123"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 abc")));
    }

    @Test
    public void testPartialUtf8() throws Exception {
        final byte[] annoyingData = "foo\u0000\uffff\u0bf5\ud808\udc30".getBytes(Charsets.UTF_8);
        MatcherAssert.assertThat(annoyingData.length, Matchers.is(14));
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 0, 1)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 1, 2)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 2, 3)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 3, 4)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 4, 5)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 5, 6)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 6, 7)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 7, 8)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 8, 9)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 9, 10)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 10, 11)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 11, 12)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 12, 13)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 13, 14)));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 foo\u0000\uffff\u0bf5\ud808\udc30")));
    }

    @Test
    public void testPartialUtf8Interleaved() throws Exception {
        final byte[] annoyingData = "foo\u0000\uffff\u0bf5\ud808\udc30".getBytes(Charsets.UTF_8);
        MatcherAssert.assertThat(annoyingData.length, Matchers.is(14));
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 0, 1)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 0, 1)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 1, 2)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 1, 2)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 2, 3)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 2, 3)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 3, 4)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 3, 4)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 4, 5)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 4, 5)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 5, 6)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 5, 6)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 6, 7)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 6, 7)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 7, 8)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 7, 8)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 8, 9)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 8, 9)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 9, 10)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 9, 10)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 10, 11)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 10, 11)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 11, 12)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 11, 12)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 12, 13)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 12, 13)), LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 13, 14)), LoggingLogStreamFollowerTest.stderr(Arrays.copyOfRange(annoyingData, 13, 14)));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 f"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 f"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 o"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 o"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 o"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 o"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 \u0000"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 \u0000"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 \uffff"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 \uffff"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 ?"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 ?"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 1 \ud808\udc30"), LoggingLogStreamFollowerTest.event(INFO, "[a] [d] 2 \ud808\udc30")));
    }

    @Test
    public void testDropPartialUtf8Sequence() throws Exception {
        final byte[] annoyingData = "?".getBytes(Charsets.UTF_8);
        MatcherAssert.assertThat(annoyingData.length, Matchers.is(3));
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout(Arrays.copyOfRange(annoyingData, 0, 1)));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "d", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.is(Matchers.emptyCollectionOf(ILoggingEvent.class)));
    }

    @Test
    public void testTruncateContainerId() throws Exception {
        final Iterator<LogMessage> stream = LoggingLogStreamFollowerTest.stream(LoggingLogStreamFollowerTest.stdout("x"));
        final LoggingLogStreamFollower sut = LoggingLogStreamFollower.create(log);
        sut.followLog(JobId.fromString("a:b:c"), "0123456789abcdef", stream);
        MatcherAssert.assertThat(appender.events(), Matchers.contains(LoggingLogStreamFollowerTest.event(INFO, "[a] [0123456] 1 x")));
    }
}

