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
 * Unit tests for {@link LogMessageRegex} matching startup configuration with optional
 * {@code memberName} missing.
 *
 * <p>
 * Example log message containing startup configuration:
 *
 * <pre>
 * [info 2018/10/19 16:03:18.069 PDT <main> tid=0x1] Startup Configuration: ### GemFire Properties defined with api ###
 * </pre>
 */
@Category(LoggingTest.class)
public class LogMessageRegexMatchesStartupConfigurationTest {
    private final String logLevel = "info";

    private final String date = "2018/10/19";

    private final String time = "16:03:18.069";

    private final String timeZone = "PDT";

    private final String memberName = "";

    private final String threadName = "<main>";

    private final String threadId = "tid=0x1";

    private final String message = "Startup Configuration: ### GemFire Properties defined with api ###";

    private String logLine;

    @Test
    public void regexMatchesStartupConfigurationLogLine() {
        assertThat(logLine).matches(LogMessageRegex.getRegex());
    }

    @Test
    public void patternMatcherMatchesStartupConfigurationLogLine() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void patternMatcherGroupZeroMatchesStartupConfigurationLogLine() {
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
    public void logLevelGroupIndexCapturesStartupConfigurationLogLevel() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.LOG_LEVEL.getIndex())).isEqualTo(logLevel);
    }

    @Test
    public void dateGroupIndexCapturesStartupConfigurationDate() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.DATE.getIndex())).isEqualTo(date);
    }

    @Test
    public void timeGroupIndexCapturesStartupConfigurationTime() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME.getIndex())).isEqualTo(time);
    }

    @Test
    public void timeZoneGroupIndexCapturesStartupConfigurationTimeZone() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME_ZONE.getIndex())).isEqualTo(timeZone);
    }

    @Test
    public void logLevelGroupNameCapturesStartupConfigurationLogLevel() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.LOG_LEVEL.getName())).isEqualTo(logLevel);
    }

    @Test
    public void dateGroupNameCapturesStartupConfigurationDate() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.DATE.getName())).isEqualTo(date);
    }

    @Test
    public void timeGroupNameCapturesStartupConfigurationTime() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME.getName())).isEqualTo(time);
    }

    @Test
    public void timeZoneGroupNameCapturesStartupConfigurationTimeZone() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.TIME_ZONE.getName())).isEqualTo(timeZone);
    }

    @Test
    public void memberNameGroupNameCapturesStartupConfigurationMemberName() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.MEMBER_NAME.getName())).isEqualTo(memberName);
    }

    @Test
    public void threadNameGroupNameCapturesStartupConfigurationThreadName() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.THREAD_NAME.getName())).isEqualTo(threadName);
    }

    @Test
    public void threadIdGroupNameCapturesStartupConfigurationThreadId() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.THREAD_ID.getName())).isEqualTo(threadId);
    }

    @Test
    public void messageGroupNameCapturesStartupConfigurationMessage() {
        Matcher matcher = LogMessageRegex.getPattern().matcher(logLine);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(Group.MESSAGE.getName())).isEqualTo(message);
    }

    @Test
    public void logLevelRegexMatchesStartupConfigurationLogLevel() {
        Matcher matcher = compile(Group.LOG_LEVEL.getRegex()).matcher(logLevel);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void dateRegexMatchesStartupConfigurationDate() {
        Matcher matcher = compile(Group.DATE.getRegex()).matcher(date);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void timeRegexMatchesStartupConfigurationTime() {
        Matcher matcher = compile(Group.TIME.getRegex()).matcher(time);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void timeZoneRegexMatchesStartupConfigurationTimeZone() {
        Matcher matcher = compile(Group.TIME_ZONE.getRegex()).matcher(timeZone);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void memberNameRegexMatchesStartupConfigurationMemberName() {
        Matcher matcher = compile(Group.MEMBER_NAME.getRegex()).matcher(memberName);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void threadNameRegexMatchesStartupConfigurationThreadName() {
        Matcher matcher = compile(Group.THREAD_NAME.getRegex()).matcher(threadName);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void threadIdRegexMatchesStartupConfigurationThreadId() {
        Matcher matcher = compile(Group.THREAD_ID.getRegex()).matcher(threadId);
        assertThat(matcher.matches()).isTrue();
    }

    @Test
    public void messageRegexMatchesStartupConfigurationMessage() {
        Matcher matcher = compile(Group.MESSAGE.getRegex()).matcher(message);
        assertThat(matcher.matches()).isTrue();
    }
}

