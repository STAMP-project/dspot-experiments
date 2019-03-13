/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.buildeventservice;


import BuildComponent.CONTROLLER;
import BuildComponent.TOOL;
import FinishType.FINISHED;
import Result.COMMAND_SUCCEEDED;
import ServiceLevel.INTERACTIVE;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.v1.BuildEvent;
import com.google.devtools.build.v1.BuildEvent.BuildComponentStreamFinished;
import com.google.devtools.build.v1.BuildEvent.BuildEnqueued;
import com.google.devtools.build.v1.BuildEvent.BuildFinished;
import com.google.devtools.build.v1.BuildEvent.InvocationAttemptFinished;
import com.google.devtools.build.v1.BuildEvent.InvocationAttemptStarted;
import com.google.devtools.build.v1.BuildStatus;
import com.google.devtools.build.v1.OrderedBuildEvent;
import com.google.devtools.build.v1.PublishBuildToolEventStreamRequest;
import com.google.devtools.build.v1.PublishLifecycleEventRequest;
import com.google.devtools.build.v1.StreamId;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link BuildEventServiceProtoUtil}. *
 */
@RunWith(JUnit4.class)
public class BuildEventServiceProtoUtilTest {
    private static final String BUILD_REQUEST_ID = "feedbeef-dead-4321-beef-deaddeaddead";

    private static final String BUILD_INVOCATION_ID = "feedbeef-dead-4444-beef-deaddeaddead";

    private static final String PROJECT_ID = "my_project";

    private static final String COMMAND_NAME = "test";

    private static final String ADDITIONAL_KEYWORD = "keyword=foo";

    private static final ImmutableList<String> EXPECTED_KEYWORDS = ImmutableList.of(("command_name=" + (BuildEventServiceProtoUtilTest.COMMAND_NAME)), "protocol_name=BEP", BuildEventServiceProtoUtilTest.ADDITIONAL_KEYWORD);

    private static final BuildEventServiceProtoUtil BES_PROTO_UTIL = new BuildEventServiceProtoUtil.Builder().buildRequestId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).invocationId(BuildEventServiceProtoUtilTest.BUILD_INVOCATION_ID).projectId(BuildEventServiceProtoUtilTest.PROJECT_ID).commandName(BuildEventServiceProtoUtilTest.COMMAND_NAME).keywords(ImmutableSet.of(BuildEventServiceProtoUtilTest.ADDITIONAL_KEYWORD)).build();

    private final ManualClock clock = new ManualClock();

    @Test
    public void testBuildEnqueued() {
        Timestamp expected = Timestamps.fromMillis(clock.advanceMillis(100));
        assertThat(BuildEventServiceProtoUtilTest.BES_PROTO_UTIL.buildEnqueued(expected)).isEqualTo(PublishLifecycleEventRequest.newBuilder().setServiceLevel(INTERACTIVE).setProjectId(BuildEventServiceProtoUtilTest.PROJECT_ID).setBuildEvent(OrderedBuildEvent.newBuilder().setStreamId(StreamId.newBuilder().setBuildId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).setComponent(CONTROLLER)).setSequenceNumber(1).setEvent(BuildEvent.newBuilder().setEventTime(expected).setBuildEnqueued(BuildEnqueued.newBuilder()))).build());
    }

    @Test
    public void testInvocationAttemptStarted() {
        Timestamp expected = Timestamps.fromMillis(clock.advanceMillis(100));
        assertThat(BuildEventServiceProtoUtilTest.BES_PROTO_UTIL.invocationStarted(expected)).isEqualTo(PublishLifecycleEventRequest.newBuilder().setServiceLevel(INTERACTIVE).setProjectId(BuildEventServiceProtoUtilTest.PROJECT_ID).setBuildEvent(OrderedBuildEvent.newBuilder().setStreamId(StreamId.newBuilder().setBuildId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).setInvocationId(BuildEventServiceProtoUtilTest.BUILD_INVOCATION_ID).setComponent(CONTROLLER)).setSequenceNumber(1).setEvent(BuildEvent.newBuilder().setEventTime(expected).setInvocationAttemptStarted(InvocationAttemptStarted.newBuilder().setAttemptNumber(1)))).build());
    }

    @Test
    public void testInvocationAttemptFinished() {
        Timestamp expected = Timestamps.fromMillis(clock.advanceMillis(100));
        assertThat(BuildEventServiceProtoUtilTest.BES_PROTO_UTIL.invocationFinished(expected, COMMAND_SUCCEEDED)).isEqualTo(PublishLifecycleEventRequest.newBuilder().setServiceLevel(INTERACTIVE).setProjectId(BuildEventServiceProtoUtilTest.PROJECT_ID).setBuildEvent(OrderedBuildEvent.newBuilder().setStreamId(StreamId.newBuilder().setBuildId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).setInvocationId(BuildEventServiceProtoUtilTest.BUILD_INVOCATION_ID).setComponent(CONTROLLER)).setSequenceNumber(2).setEvent(BuildEvent.newBuilder().setEventTime(expected).setInvocationAttemptFinished(InvocationAttemptFinished.newBuilder().setInvocationStatus(BuildStatus.newBuilder().setResult(COMMAND_SUCCEEDED))))).build());
    }

    @Test
    public void testBuildFinished() {
        Timestamp expected = Timestamps.fromMillis(clock.advanceMillis(100));
        assertThat(BuildEventServiceProtoUtilTest.BES_PROTO_UTIL.buildFinished(expected, COMMAND_SUCCEEDED)).isEqualTo(PublishLifecycleEventRequest.newBuilder().setServiceLevel(INTERACTIVE).setProjectId(BuildEventServiceProtoUtilTest.PROJECT_ID).setBuildEvent(OrderedBuildEvent.newBuilder().setStreamId(StreamId.newBuilder().setBuildId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).setComponent(CONTROLLER)).setSequenceNumber(2).setEvent(BuildEvent.newBuilder().setEventTime(expected).setBuildFinished(BuildFinished.newBuilder().setStatus(BuildStatus.newBuilder().setResult(COMMAND_SUCCEEDED))))).build());
    }

    @Test
    public void testStreamEvents() {
        Timestamp firstEventTimestamp = Timestamps.fromMillis(clock.advanceMillis(100));
        Any anything = Any.getDefaultInstance();
        assertThat(BuildEventServiceProtoUtilTest.BES_PROTO_UTIL.bazelEvent(1, firstEventTimestamp, anything)).isEqualTo(PublishBuildToolEventStreamRequest.newBuilder().addAllNotificationKeywords(BuildEventServiceProtoUtilTest.EXPECTED_KEYWORDS).setProjectId(BuildEventServiceProtoUtilTest.PROJECT_ID).setOrderedBuildEvent(OrderedBuildEvent.newBuilder().setStreamId(StreamId.newBuilder().setBuildId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).setInvocationId(BuildEventServiceProtoUtilTest.BUILD_INVOCATION_ID).setComponent(TOOL)).setSequenceNumber(1).setEvent(BuildEvent.newBuilder().setEventTime(firstEventTimestamp).setBazelEvent(anything)).build()).build());
        Timestamp secondEventTimestamp = Timestamps.fromMillis(clock.advanceMillis(100));
        assertThat(BuildEventServiceProtoUtilTest.BES_PROTO_UTIL.bazelEvent(2, secondEventTimestamp, anything)).isEqualTo(PublishBuildToolEventStreamRequest.newBuilder().setProjectId(BuildEventServiceProtoUtilTest.PROJECT_ID).setOrderedBuildEvent(OrderedBuildEvent.newBuilder().setStreamId(StreamId.newBuilder().setBuildId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).setInvocationId(BuildEventServiceProtoUtilTest.BUILD_INVOCATION_ID).setComponent(TOOL)).setSequenceNumber(2).setEvent(BuildEvent.newBuilder().setEventTime(secondEventTimestamp).setBazelEvent(anything)).build()).build());
        Timestamp thirdEventTimestamp = Timestamps.fromMillis(clock.advanceMillis(100));
        assertThat(BuildEventServiceProtoUtilTest.BES_PROTO_UTIL.streamFinished(3, thirdEventTimestamp)).isEqualTo(PublishBuildToolEventStreamRequest.newBuilder().setProjectId(BuildEventServiceProtoUtilTest.PROJECT_ID).setOrderedBuildEvent(OrderedBuildEvent.newBuilder().setStreamId(StreamId.newBuilder().setBuildId(BuildEventServiceProtoUtilTest.BUILD_REQUEST_ID).setInvocationId(BuildEventServiceProtoUtilTest.BUILD_INVOCATION_ID).setComponent(TOOL)).setSequenceNumber(3).setEvent(BuildEvent.newBuilder().setEventTime(thirdEventTimestamp).setComponentStreamFinished(BuildComponentStreamFinished.newBuilder().setType(FINISHED))).build()).build());
    }
}

