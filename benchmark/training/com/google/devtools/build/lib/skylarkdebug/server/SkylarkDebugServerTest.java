/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skylarkdebug.server;


import PauseReason.CONDITIONAL_BREAKPOINT_ERROR;
import PauseReason.HIT_BREAKPOINT;
import PauseReason.INITIALIZING;
import PauseReason.STEPPING;
import SkylarkDebuggingProtos.Error;
import SkylarkDebuggingProtos.PausedThread;
import Stepping.INTO;
import Stepping.OUT;
import Stepping.OVER;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.events.EventKind;
import com.google.devtools.build.lib.events.util.EventCollectionApparatus;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.Breakpoint;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.ContinueExecutionRequest;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.DebugEvent;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.DebugRequest;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.EvaluateRequest;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.Frame;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.ListFramesRequest;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.ListFramesResponse;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.Location;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.Scope;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.StartDebuggingRequest;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.StartDebuggingResponse;
import com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.Value;
import com.google.devtools.build.lib.syntax.BuildFileAST;
import com.google.devtools.build.lib.syntax.Environment;
import com.google.devtools.build.lib.syntax.SkylarkList;
import com.google.devtools.build.lib.testutil.Scratch;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests for {@link SkylarkDebugServer}.
 */
@RunWith(JUnit4.class)
public class SkylarkDebugServerTest {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private final Scratch scratch = new Scratch();

    private final EventCollectionApparatus events = new EventCollectionApparatus(EventKind.ALL_EVENTS);

    private final ThreadObjectMap dummyObjectMap = new ThreadObjectMap();

    private MockDebugClient client;

    private SkylarkDebugServer server;

    @Test
    public void testStartDebuggingResponseReceived() throws Exception {
        DebugEvent response = client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(1).setStartDebugging(StartDebuggingRequest.newBuilder()).build());
        assertThat(response).isEqualTo(DebugEvent.newBuilder().setSequenceNumber(1).setStartDebugging(StartDebuggingResponse.newBuilder().build()).build());
    }

    @Test
    public void testPausedUntilStartDebuggingRequestReceived() throws Exception {
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        String threadName = evaluationThread.getName();
        long threadId = evaluationThread.getId();
        // wait for BUILD evaluation to start
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        Location expectedLocation = DebugEventHelper.getLocationProto(buildFile.getStatements().get(0).getLocation());
        assertThat(event).isEqualTo(DebugEventHelper.threadPausedEvent(PausedThread.newBuilder().setId(threadId).setName(threadName).setPauseReason(INITIALIZING).setLocation(expectedLocation).build()));
        sendStartDebuggingRequest();
        event = client.waitForEvent(DebugEvent::hasThreadContinued, Duration.ofSeconds(5));
        assertThat(event).isEqualTo(DebugEventHelper.threadContinuedEvent(threadId));
    }

    @Test
    public void testResumeAllThreads() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        // evaluate in two separate worker threads
        execInWorkerThread(buildFile, SkylarkDebugServerTest.newEnvironment());
        execInWorkerThread(buildFile, SkylarkDebugServerTest.newEnvironment());
        // wait for both breakpoints to be hit
        boolean paused = client.waitForEvents(( list) -> (list.stream().filter(DebugEvent::hasThreadPaused).count()) == 2, Duration.ofSeconds(5));
        assertThat(paused).isTrue();
        client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(45).setContinueExecution(ContinueExecutionRequest.newBuilder()).build());
        boolean resumed = client.waitForEvents(( list) -> (list.stream().filter(DebugEvent::hasThreadContinued).count()) == 2, Duration.ofSeconds(5));
        assertThat(resumed).isTrue();
    }

    @Test
    public void testPauseAtBreakpoint() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        String threadName = evaluationThread.getName();
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        SkylarkDebuggingProtos.PausedThread expectedThreadState = PausedThread.newBuilder().setName(threadName).setId(threadId).setPauseReason(HIT_BREAKPOINT).setLocation(breakpoint.toBuilder().setColumnNumber(1)).build();
        assertThat(event).isEqualTo(DebugEventHelper.threadPausedEvent(expectedThreadState));
    }

    @Test
    public void testDoNotPauseAtUnsatisfiedConditionalBreakpoint() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]", "z = 1");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        ImmutableList<Breakpoint> breakpoints = ImmutableList.of(Breakpoint.newBuilder().setLocation(Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD")).setExpression("x[0] == 2").build(), Breakpoint.newBuilder().setLocation(Location.newBuilder().setLineNumber(3).setPath("/a/build/file/BUILD")).setExpression("x[0] == 1").build());
        setBreakpoints(breakpoints);
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        String threadName = evaluationThread.getName();
        long threadId = evaluationThread.getId();
        Breakpoint expectedBreakpoint = breakpoints.get(1);
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        assertThat(event).isEqualTo(DebugEventHelper.threadPausedEvent(PausedThread.newBuilder().setName(threadName).setId(threadId).setLocation(expectedBreakpoint.getLocation().toBuilder().setColumnNumber(1)).setPauseReason(HIT_BREAKPOINT).build()));
    }

    @Test
    public void testPauseAtSatisfiedConditionalBreakpoint() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location location = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        Breakpoint breakpoint = Breakpoint.newBuilder().setLocation(location).setExpression("x[0] == 1").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        String threadName = evaluationThread.getName();
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        SkylarkDebuggingProtos.PausedThread expectedThreadState = PausedThread.newBuilder().setName(threadName).setId(threadId).setPauseReason(HIT_BREAKPOINT).setLocation(location.toBuilder().setColumnNumber(1)).build();
        assertThat(event).isEqualTo(DebugEventHelper.threadPausedEvent(expectedThreadState));
    }

    @Test
    public void testPauseAtInvalidConditionBreakpointWithError() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location location = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        Breakpoint breakpoint = Breakpoint.newBuilder().setLocation(location).setExpression("z[0] == 1").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        String threadName = evaluationThread.getName();
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        SkylarkDebuggingProtos.PausedThread expectedThreadState = PausedThread.newBuilder().setName(threadName).setId(threadId).setPauseReason(CONDITIONAL_BREAKPOINT_ERROR).setLocation(location.toBuilder().setColumnNumber(1)).setConditionalBreakpointError(Error.newBuilder().setMessage("name \'z\' is not defined")).build();
        assertThat(event).isEqualTo(DebugEventHelper.threadPausedEvent(expectedThreadState));
    }

    @Test
    public void testListFramesForInvalidThread() throws Exception {
        sendStartDebuggingRequest();
        DebugEvent event = client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(1).setListFrames(ListFramesRequest.newBuilder().setThreadId(20).build()).build());
        assertThat(event.hasError()).isTrue();
        assertThat(event.getError().getMessage()).contains("Thread 20 is not paused");
    }

    @Test
    public void testSimpleListFramesRequest() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        ListFramesResponse frames = listFrames(threadId);
        assertThat(frames.getFrameCount()).isEqualTo(1);
        assertFramesEqualIgnoringValueIdentifiers(frames.getFrame(0), Frame.newBuilder().setFunctionName("<top level>").setLocation(breakpoint.toBuilder().setColumnNumber(1)).addScope(Scope.newBuilder().setName("global").addBinding(getValueProto("x", SkylarkList.createImmutable(ImmutableList.of(1, 2, 3))))).build());
    }

    @Test
    public void testGetChildrenRequest() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        ListFramesResponse frames = listFrames(threadId);
        Value xValue = frames.getFrame(0).getScope(0).getBinding(0);
        assertValuesEqualIgnoringId(xValue, getValueProto("x", SkylarkList.createImmutable(ImmutableList.of(1, 2, 3))));
        List<Value> children = getChildren(xValue);
        assertThat(children).isEqualTo(ImmutableList.of(getValueProto("[0]", 1), getValueProto("[1]", 2), getValueProto("[2]", 3)));
    }

    @Test
    public void testListFramesShadowedBinding() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST bzlFile = parseSkylarkFile("/a/build/file/test.bzl", "a = 1", "c = 3", "def fn():", "  a = 2", "  b = 1", "  b + 1", "fn()");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setPath("/a/build/file/test.bzl").setLineNumber(6).build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(bzlFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        ListFramesResponse frames = listFrames(threadId);
        assertThat(frames.getFrameCount()).isEqualTo(2);
        assertFramesEqualIgnoringValueIdentifiers(frames.getFrame(0), Frame.newBuilder().setFunctionName("fn").setLocation(breakpoint.toBuilder().setColumnNumber(3)).addScope(Scope.newBuilder().setName("local").addBinding(getValueProto("a", 2)).addBinding(getValueProto("b", 1))).addScope(Scope.newBuilder().setName("global").addBinding(getValueProto("c", 3)).addBinding(getValueProto("fn", env.moduleLookup("fn")))).build());
        assertFramesEqualIgnoringValueIdentifiers(frames.getFrame(1), Frame.newBuilder().setFunctionName("<top level>").setLocation(Location.newBuilder().setPath("/a/build/file/test.bzl").setLineNumber(7).setColumnNumber(1)).addScope(Scope.newBuilder().setName("global").addBinding(getValueProto("a", 1)).addBinding(getValueProto("c", 3)).addBinding(getValueProto("fn", env.moduleLookup("fn")))).build());
    }

    @Test
    public void testEvaluateRequestWithExpression() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        DebugEvent response = client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(123).setEvaluate(EvaluateRequest.newBuilder().setThreadId(threadId).setStatement("x[1]").build()).build());
        assertThat(response.hasEvaluate()).isTrue();
        assertThat(response.getEvaluate().getResult()).isEqualTo(getValueProto("Evaluation result", 2));
    }

    @Test
    public void testEvaluateRequestWithAssignmentStatement() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        DebugEvent response = client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(123).setEvaluate(EvaluateRequest.newBuilder().setThreadId(threadId).setStatement("x = [5,6]").build()).build());
        assertThat(response.getEvaluate().getResult()).isEqualTo(getValueProto("Evaluation result", SkylarkList.createImmutable(ImmutableList.of(5, 6))));
        ListFramesResponse frames = listFrames(threadId);
        assertThat(frames.getFrame(0).getScope(0).getBindingList()).contains(getValueProto("x", SkylarkList.createImmutable(ImmutableList.of(5, 6))));
    }

    @Test
    public void testEvaluateRequestWithExpressionStatementMutatingState() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        DebugEvent response = client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(123).setEvaluate(EvaluateRequest.newBuilder().setThreadId(threadId).setStatement("x.append(4)").build()).build());
        assertThat(response.getEvaluate().getResult()).isEqualTo(getValueProto("Evaluation result", NONE));
        ListFramesResponse frames = listFrames(threadId);
        assertThat(frames.getFrame(0).getScope(0).getBindingList()).contains(getValueProto("x", SkylarkList.createImmutable(ImmutableList.of(1, 2, 3, 4))));
    }

    @Test
    public void testEvaluateRequestThrowingException() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST buildFile = parseBuildFile("/a/build/file/BUILD", "x = [1,2,3]", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/BUILD").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(buildFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        DebugEvent response = client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(123).setEvaluate(EvaluateRequest.newBuilder().setThreadId(threadId).setStatement("z[0]").build()).build());
        assertThat(response.hasError()).isTrue();
        assertThat(response.getError().getMessage()).isEqualTo("name 'z' is not defined");
    }

    @Test
    public void testStepIntoFunction() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST bzlFile = parseSkylarkFile("/a/build/file/test.bzl", "def fn():", "  a = 2", "  return a", "x = fn()", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(4).setPath("/a/build/file/test.bzl").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(bzlFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        assertThat(event.getThreadPaused().getThread().getLocation().getLineNumber()).isEqualTo(4);
        client.unnumberedEvents.clear();
        client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(2).setContinueExecution(ContinueExecutionRequest.newBuilder().setThreadId(threadId).setStepping(INTO).build()).build());
        event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        // check we're paused inside the function
        assertThat(listFrames(threadId).getFrameCount()).isEqualTo(2);
        // and verify the location and pause reason as well
        Location expectedLocation = breakpoint.toBuilder().setLineNumber(2).setColumnNumber(3).build();
        SkylarkDebuggingProtos.PausedThread pausedThread = event.getThreadPaused().getThread();
        assertThat(pausedThread.getPauseReason()).isEqualTo(STEPPING);
        assertThat(pausedThread.getLocation()).isEqualTo(expectedLocation);
    }

    @Test
    public void testStepOverFunction() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST bzlFile = parseSkylarkFile("/a/build/file/test.bzl", "def fn():", "  a = 2", "  return a", "x = fn()", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(4).setPath("/a/build/file/test.bzl").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(bzlFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        assertThat(event.getThreadPaused().getThread().getLocation().getLineNumber()).isEqualTo(4);
        client.unnumberedEvents.clear();
        client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(2).setContinueExecution(ContinueExecutionRequest.newBuilder().setThreadId(threadId).setStepping(OVER).build()).build());
        event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        Location expectedLocation = breakpoint.toBuilder().setLineNumber(5).setColumnNumber(1).build();
        com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.PausedThread pausedThread = event.getThreadPaused().getThread();
        assertThat(pausedThread.getPauseReason()).isEqualTo(STEPPING);
        assertThat(pausedThread.getLocation()).isEqualTo(expectedLocation);
    }

    @Test
    public void testStepOutOfFunction() throws Exception {
        sendStartDebuggingRequest();
        BuildFileAST bzlFile = parseSkylarkFile("/a/build/file/test.bzl", "def fn():", "  a = 2", "  return a", "x = fn()", "y = [2,3,4]");
        Environment env = SkylarkDebugServerTest.newEnvironment();
        Location breakpoint = Location.newBuilder().setLineNumber(2).setPath("/a/build/file/test.bzl").build();
        setBreakpoints(ImmutableList.of(breakpoint));
        Thread evaluationThread = execInWorkerThread(bzlFile, env);
        long threadId = evaluationThread.getId();
        // wait for breakpoint to be hit
        client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        assertThat(listFrames(threadId).getFrameCount()).isEqualTo(2);
        client.unnumberedEvents.clear();
        client.sendRequestAndWaitForResponse(DebugRequest.newBuilder().setSequenceNumber(2).setContinueExecution(ContinueExecutionRequest.newBuilder().setThreadId(threadId).setStepping(OUT).build()).build());
        DebugEvent event = client.waitForEvent(DebugEvent::hasThreadPaused, Duration.ofSeconds(5));
        com.google.devtools.build.lib.skylarkdebugging.SkylarkDebuggingProtos.PausedThread pausedThread = event.getThreadPaused().getThread();
        Location expectedLocation = breakpoint.toBuilder().setLineNumber(5).setColumnNumber(1).build();
        assertThat(pausedThread.getPauseReason()).isEqualTo(STEPPING);
        assertThat(pausedThread.getLocation()).isEqualTo(expectedLocation);
    }
}

