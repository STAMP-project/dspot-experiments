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
package org.apache.geode.distributed;


import org.apache.geode.distributed.LocatorLauncher.LocatorState;
import org.apache.geode.management.internal.cli.json.GfJsonException;
import org.junit.Test;


/**
 * Unit tests for {@link LocatorLauncher.LocatorState}.
 */
public class LocatorStateTest {
    private String classpath;

    private String gemFireVersion;

    private String host;

    private String javaVersion;

    private String jvmArguments;

    private String serviceLocation;

    private String logFile;

    private String memberName;

    private Integer pid;

    private String port;

    private String statusDescription;

    private String statusMessage;

    private Long timestampTime;

    private Long uptime;

    private String workingDirectory;

    @Test
    public void fromJsonWithEmptyStringThrowsIllegalArgumentException() {
        // given: empty string
        String emptyString = "";
        // when: passed to fromJson
        Throwable thrown = catchThrowable(() -> fromJson(emptyString));
        // then: throws IllegalArgumentException with cause of GfJsonException
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasCauseInstanceOf(GfJsonException.class);
        assertThat(thrown.getCause()).isInstanceOf(GfJsonException.class).hasNoCause();
    }

    @Test
    public void fromJsonWithWhiteSpaceStringThrowsIllegalArgumentException() {
        // given: white space string
        String whiteSpaceString = "      ";
        // when: passed to fromJson
        Throwable thrown = catchThrowable(() -> fromJson(whiteSpaceString));
        // then: throws IllegalArgumentException with cause of GfJsonException
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasCauseInstanceOf(GfJsonException.class);
        assertThat(thrown.getCause()).isInstanceOf(GfJsonException.class).hasNoCause();
    }

    @Test
    public void fromJsonWithNullStringThrowsNullPointerException() {
        // given: null string
        String nullString = null;
        // when: passed to fromJson
        Throwable thrown = catchThrowable(() -> fromJson(nullString));
        // then: throws NullPointerException
        assertThat(thrown).isInstanceOf(NullPointerException.class).hasNoCause();
    }

    @Test
    public void fromJsonWithValidJsonStringReturnsLocatorState() {
        // given: valid json string
        String jsonString = createStatusJson();
        // when: passed to fromJson
        LocatorState value = fromJson(jsonString);
        // then: return valid instance of LocatorState
        assertThat(value).isInstanceOf(LocatorState.class);
        assertThat(value.getClasspath()).isEqualTo(classpath);
        assertThat(value.getGemFireVersion()).isEqualTo(gemFireVersion);
        assertThat(value.getHost()).isEqualTo(host);
        assertThat(value.getJavaVersion()).isEqualTo(javaVersion);
        assertThat(value.getJvmArguments()).isEqualTo(getJvmArguments());
        assertThat(value.getLogFile()).isEqualTo(logFile);
        assertThat(value.getMemberName()).isEqualTo(memberName);
        assertThat(value.getPid()).isEqualTo(pid);
        assertThat(value.getPort()).isEqualTo(port);
        assertThat(value.getServiceLocation()).isEqualTo(serviceLocation);
        assertThat(value.getStatus().getDescription()).isEqualTo(statusDescription);
        assertThat(value.getStatusMessage()).isEqualTo(statusMessage);
        assertThat(value.getTimestamp().getTime()).isEqualTo(timestampTime);
        assertThat(value.getUptime()).isEqualTo(uptime);
        assertThat(value.getWorkingDirectory()).isEqualTo(workingDirectory);
    }
}

