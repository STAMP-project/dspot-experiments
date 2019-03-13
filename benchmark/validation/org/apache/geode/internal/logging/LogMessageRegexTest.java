/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.logging;


import java.util.regex.Matcher;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static Group.values;
import static java.util.regex.Pattern.compile;


/**
 * Unit tests for {@link LogMessageRegex}.
 */
@Category(LoggingTest.class)
public class LogMessageRegexTest {
    private final String logLevel = "info";

    private final String date = "2018/09/24";

    private final String time = "12:35:59.515";

    private final String timeZone = "PDT";

    private final String memberName = "logMessageRegexTest";

    private final String threadName = "<main>";

    private final String threadId = "tid=0x1";

    private final String message = "this is a log statement";

    private String logLine;

    @Test
    public void regexMatchesLogLine() {
        assertThat(logLine).matches(LogMessageRegex.getRegex());
    }

    @Test
    public void patternMatcherMatchesLogLine() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void patternMatcherGroupZeroMatchesLogLine() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(0)).isEqualTo(logLine);
    }

    @Test
    public void patternMatcherGroupCountEqualsGroupsLength() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.groupCount()).isEqualTo(values().length);
    }

    @Test
    public void logLevelGroupIndexCapturesLogLevel() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.LOG_LEVEL.getIndex())).isEqualTo(logLevel);
    }

    @Test
    public void dateGroupIndexCapturesDate() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.DATE.getIndex())).isEqualTo(date);
    }

    @Test
    public void timeGroupIndexCapturesTime() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME.getIndex())).isEqualTo(time);
    }

    @Test
    public void timeZoneGroupIndexCapturesTimeZone() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME_ZONE.getIndex())).isEqualTo(timeZone);
    }

    @Test
    public void logLevelGroupNameCapturesLogLevel() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.LOG_LEVEL.getName())).isEqualTo(logLevel);
    }

    @Test
    public void dateGroupNameCapturesDate() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.DATE.getName())).isEqualTo(date);
    }

    @Test
    public void timeGroupNameCapturesTime() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME.getName())).isEqualTo(time);
    }

    @Test
    public void timeZoneGroupNameCapturesTimeZone() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME_ZONE.getName())).isEqualTo(timeZone);
    }

    @Test
    public void memberNameGroupNameCapturesMemberName() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.MEMBER_NAME.getName())).isEqualTo(memberName);
    }

    @Test
    public void threadNameGroupNameCapturesThreadName() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.THREAD_NAME.getName())).isEqualTo(threadName);
    }

    @Test
    public void threadIdGroupNameCapturesThreadId() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.THREAD_ID.getName())).isEqualTo(threadId);
    }

    @Test
    public void messageGroupNameCapturesMessage() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.MESSAGE.getName())).isEqualTo(message);
    }

    @Test
    public void logLevelRegexMatchesLogLevel() {
        Matcher matcher = compile(Group.LOG_LEVEL.getRegex()).matcher(logLevel);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void dateRegexMatchesDate() {
        Matcher matcher = compile(Group.DATE.getRegex()).matcher(date);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void timeRegexMatchesTime() {
        Matcher matcher = compile(Group.TIME.getRegex()).matcher(time);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void timeZoneRegexMatchesTimeZone() {
        Matcher matcher = compile(Group.TIME_ZONE.getRegex()).matcher(timeZone);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void memberNameRegexMatchesMemberName() {
        Matcher matcher = compile(Group.MEMBER_NAME.getRegex()).matcher(memberName);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void threadNameRegexMatchesThreadName() {
        Matcher matcher = compile(Group.THREAD_NAME.getRegex()).matcher(threadName);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void threadIdRegexMatchesThreadId() {
        Matcher matcher = compile(Group.THREAD_ID.getRegex()).matcher(threadId);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void messageRegexMatchesMessage() {
        Matcher matcher = compile(Group.MESSAGE.getRegex()).matcher(message);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void memberNameRegexMatchesMissingMemberName() {
        Matcher matcher = compile(Group.MEMBER_NAME.getRegex()).matcher("");
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void memberNameRegexMatchesMemberNameWithNoSpaces() {
        Matcher matcher = compile(Group.MEMBER_NAME.getRegex()).matcher("");
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void memberNameRegexMatchesMemberNameWithOneSpace() {
        Matcher matcher = compile(Group.MEMBER_NAME.getRegex()).matcher("hello world");
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void memberNameRegexMatchesMemberNameWithMultipleSpaces() {
        Matcher matcher = compile(Group.MEMBER_NAME.getRegex()).matcher("this is a name");
        assertThat(matcher.matches()).isTrue();
    }
}

