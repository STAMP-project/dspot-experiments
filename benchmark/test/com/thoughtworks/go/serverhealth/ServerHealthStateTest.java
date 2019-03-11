/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.serverhealth;


import ServerHealthState.TIMESTAMP_FORMAT;
import Timeout.FIVE_MINUTES;
import Timeout.TEN_SECONDS;
import Timeout.THREE_MINUTES;
import Timeout.TWO_MINUTES;
import com.thoughtworks.go.util.TestingClock;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ServerHealthStateTest {
    private static final HealthStateType HEALTH_STATE_TYPE_IDENTIFIER = HealthStateType.invalidConfig();

    static final ServerHealthState ERROR_SERVER_HEALTH_STATE = ServerHealthState.error("error", "royally screwed", ServerHealthStateTest.HEALTH_STATE_TYPE_IDENTIFIER);

    static final ServerHealthState WARNING_SERVER_HEALTH_STATE = ServerHealthState.warning("warning", "warning", ServerHealthStateTest.HEALTH_STATE_TYPE_IDENTIFIER);

    static final ServerHealthState ANOTHER_ERROR_SERVER_HEALTH_STATE = ServerHealthState.error("Second", "Hi World", ServerHealthStateTest.HEALTH_STATE_TYPE_IDENTIFIER);

    static final ServerHealthState SUCCESS_SERVER_HEALTH_STATE = ServerHealthState.success(ServerHealthStateTest.HEALTH_STATE_TYPE_IDENTIFIER);

    static final ServerHealthState ANOTHER_SUCCESS_SERVER_HEALTH_STATE = ServerHealthState.success(ServerHealthStateTest.HEALTH_STATE_TYPE_IDENTIFIER);

    static final ServerHealthState ANOTHER_WARNING_SERVER_HEALTH_STATE = ServerHealthState.warning("different warning", "my friend warning", ServerHealthStateTest.HEALTH_STATE_TYPE_IDENTIFIER);

    private TestingClock testingClock;

    @Test
    public void shouldTrumpSuccessIfCurrentIsWarning() {
        Assert.assertThat(ServerHealthStateTest.SUCCESS_SERVER_HEALTH_STATE.trump(ServerHealthStateTest.WARNING_SERVER_HEALTH_STATE), Matchers.is(ServerHealthStateTest.WARNING_SERVER_HEALTH_STATE));
    }

    @Test
    public void shouldTrumpSuccessIfCurrentIsSuccess() {
        Assert.assertThat(ServerHealthStateTest.SUCCESS_SERVER_HEALTH_STATE.trump(ServerHealthStateTest.ANOTHER_SUCCESS_SERVER_HEALTH_STATE), Matchers.is(ServerHealthStateTest.ANOTHER_SUCCESS_SERVER_HEALTH_STATE));
    }

    @Test
    public void shouldTrumpWarningIfCurrentIsWarning() {
        Assert.assertThat(ServerHealthStateTest.ANOTHER_WARNING_SERVER_HEALTH_STATE.trump(ServerHealthStateTest.WARNING_SERVER_HEALTH_STATE), Matchers.is(ServerHealthStateTest.WARNING_SERVER_HEALTH_STATE));
    }

    @Test
    public void shouldNotTrumpWarningIfCurrentIsSuccess() {
        Assert.assertThat(ServerHealthStateTest.WARNING_SERVER_HEALTH_STATE.trump(ServerHealthStateTest.SUCCESS_SERVER_HEALTH_STATE), Matchers.is(ServerHealthStateTest.WARNING_SERVER_HEALTH_STATE));
    }

    @Test
    public void shouldNotTrumpErrorIfCurrentIsSuccess() {
        Assert.assertThat(ServerHealthStateTest.ERROR_SERVER_HEALTH_STATE.trump(ServerHealthStateTest.SUCCESS_SERVER_HEALTH_STATE), Matchers.is(ServerHealthStateTest.ERROR_SERVER_HEALTH_STATE));
    }

    @Test
    public void shouldtNotTrumpErrorIfCurrentIsWarning() {
        Assert.assertThat(ServerHealthStateTest.ERROR_SERVER_HEALTH_STATE.trump(ServerHealthStateTest.WARNING_SERVER_HEALTH_STATE), Matchers.is(ServerHealthStateTest.ERROR_SERVER_HEALTH_STATE));
    }

    @Test
    public void shouldExpireAfterTheExpiryTime() throws Exception {
        testingClock.setTime(new Date());
        ServerHealthState expireInFiveMins = ServerHealthState.warning("message", "desc", HealthStateType.databaseDiskFull(), FIVE_MINUTES);
        ServerHealthState expireNever = ServerHealthState.warning("message", "desc", HealthStateType.databaseDiskFull());
        Assert.assertThat(expireInFiveMins.hasExpired(), Matchers.is(false));
        testingClock.addMillis(((int) (TWO_MINUTES.inMillis())));
        Assert.assertThat(expireInFiveMins.hasExpired(), Matchers.is(false));
        testingClock.addMillis(((int) (THREE_MINUTES.inMillis())));
        Assert.assertThat(expireInFiveMins.hasExpired(), Matchers.is(false));
        testingClock.addMillis(10);
        Assert.assertThat(expireInFiveMins.hasExpired(), Matchers.is(true));
        testingClock.addMillis(999999999);
        Assert.assertThat(expireNever.hasExpired(), Matchers.is(false));
    }

    @Test
    public void shouldUnderstandEquality() {
        ServerHealthState fooError = ServerHealthState.error("my message", "my description", HealthStateType.general(HealthStateScope.forPipeline("foo")));
        ServerHealthState fooErrorCopy = ServerHealthState.error("my message", "my description", HealthStateType.general(HealthStateScope.forPipeline("foo")));
        Assert.assertThat(fooError, Matchers.is(fooErrorCopy));
    }

    @Test
    public void shouldNotAllowNullMessage() {
        ServerHealthState nullError = null;
        try {
            nullError = ServerHealthState.error(null, "some desc", HealthStateType.general(HealthStateScope.forPipeline("foo")));
            Assert.fail("should have bombed as message given is null");
        } catch (Exception e) {
            Assert.assertThat(nullError, Matchers.is(Matchers.nullValue()));
            Assert.assertThat(e.getMessage(), Matchers.is("message cannot be null"));
        }
    }

    @Test
    public void shouldGetMessageWithTimestamp() {
        ServerHealthState errorState = ServerHealthState.error("my message", "my description", HealthStateType.general(HealthStateScope.forPipeline("foo")));
        Assert.assertThat(errorState.getMessageWithTimestamp(), Matchers.is(((("my message" + " [") + (TIMESTAMP_FORMAT.format(errorState.getTimestamp()))) + "]")));
    }

    @Test
    public void shouldEscapeErrorMessageAndDescriptionByDefault() {
        ServerHealthState errorState = ServerHealthState.error("\"<message1 & message2>\"", "\"<message1 & message2>\"", HealthStateType.general(HealthStateScope.forPipeline("foo")));
        Assert.assertThat(errorState.getMessage(), Matchers.is("&quot;&lt;message1 &amp; message2&gt;&quot;"));
        Assert.assertThat(errorState.getDescription(), Matchers.is("&quot;&lt;message1 &amp; message2&gt;&quot;"));
    }

    @Test
    public void shouldEscapeWarningMessageAndDescriptionByDefault() {
        ServerHealthState warningStateWithoutTimeout = ServerHealthState.warning("\"<message1 & message2>\"", "\"<message1 & message2>\"", HealthStateType.general(HealthStateScope.forPipeline("foo")));
        ServerHealthState warningStateWithTimeout = ServerHealthState.warning("\"<message1 & message2>\"", "\"<message1 & message2>\"", HealthStateType.general(HealthStateScope.forPipeline("foo")), TEN_SECONDS);
        ServerHealthState warningState = ServerHealthState.warning("\"<message1 & message2>\"", "\"<message1 & message2>\"", HealthStateType.general(HealthStateScope.forPipeline("foo")), 15L);
        Assert.assertThat(warningStateWithoutTimeout.getMessage(), Matchers.is("&quot;&lt;message1 &amp; message2&gt;&quot;"));
        Assert.assertThat(warningStateWithoutTimeout.getDescription(), Matchers.is("&quot;&lt;message1 &amp; message2&gt;&quot;"));
        Assert.assertThat(warningStateWithTimeout.getMessage(), Matchers.is("\"<message1 & message2>\""));
        Assert.assertThat(warningStateWithTimeout.getDescription(), Matchers.is("\"<message1 & message2>\""));
        Assert.assertThat(warningState.getMessage(), Matchers.is("&quot;&lt;message1 &amp; message2&gt;&quot;"));
        Assert.assertThat(warningState.getDescription(), Matchers.is("&quot;&lt;message1 &amp; message2&gt;&quot;"));
    }

    @Test
    public void shouldPreserverHtmlInWarningMessageAndDescription() {
        ServerHealthState warningState = ServerHealthState.warningWithHtml("\"<message1 & message2>\"", "\"<message1 & message2>\"", HealthStateType.general(HealthStateScope.forPipeline("foo")));
        ServerHealthState warningStateWithTime = ServerHealthState.warningWithHtml("\"<message1 & message2>\"", "\"<message1 & message2>\"", HealthStateType.general(HealthStateScope.forPipeline("foo")), 15L);
        Assert.assertThat(warningState.getMessage(), Matchers.is("\"<message1 & message2>\""));
        Assert.assertThat(warningState.getDescription(), Matchers.is("\"<message1 & message2>\""));
        Assert.assertThat(warningStateWithTime.getMessage(), Matchers.is("\"<message1 & message2>\""));
        Assert.assertThat(warningStateWithTime.getDescription(), Matchers.is("\"<message1 & message2>\""));
    }
}

