/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.debugger.ide.debug;


import AbstractDebugger.LOCAL_STORAGE_DEBUGGER_SESSION_KEY;
import com.google.common.base.Optional;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerConfigurator;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerManager;
import org.eclipse.che.api.core.jsonrpc.commons.RequestTransmitter;
import org.eclipse.che.api.debug.shared.dto.BreakpointDto;
import org.eclipse.che.api.debug.shared.dto.DebugSessionDto;
import org.eclipse.che.api.debug.shared.dto.LocationDto;
import org.eclipse.che.api.debug.shared.dto.SimpleValueDto;
import org.eclipse.che.api.debug.shared.dto.StackFrameDumpDto;
import org.eclipse.che.api.debug.shared.dto.VariableDto;
import org.eclipse.che.api.debug.shared.dto.VariablePathDto;
import org.eclipse.che.api.debug.shared.dto.action.ResumeActionDto;
import org.eclipse.che.api.debug.shared.dto.action.StepIntoActionDto;
import org.eclipse.che.api.debug.shared.dto.action.StepOutActionDto;
import org.eclipse.che.api.debug.shared.dto.action.StepOverActionDto;
import org.eclipse.che.api.debug.shared.dto.event.SuspendEventDto;
import org.eclipse.che.api.debug.shared.model.Breakpoint;
import org.eclipse.che.api.debug.shared.model.DebuggerInfo;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.api.debug.shared.model.SimpleValue;
import org.eclipse.che.api.debug.shared.model.StackFrameDump;
import org.eclipse.che.api.debug.shared.model.Variable;
import org.eclipse.che.api.debug.shared.model.VariablePath;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.debug.BreakpointManager;
import org.eclipse.che.ide.api.debug.DebuggerServiceClient;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.api.workspace.event.WorkspaceRunningEvent;
import org.eclipse.che.ide.api.workspace.model.WorkspaceImpl;
import org.eclipse.che.ide.debug.DebuggerDescriptor;
import org.eclipse.che.ide.debug.DebuggerManager;
import org.eclipse.che.ide.debug.DebuggerObserver;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.resource.Path;
import org.eclipse.che.ide.util.storage.LocalStorage;
import org.eclipse.che.ide.util.storage.LocalStorageProvider;
import org.eclipse.che.plugin.debugger.ide.BaseTest;
import org.eclipse.che.plugin.debugger.ide.DebuggerLocalizationConstant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Testing {@link AbstractDebugger} functionality.
 *
 * @author Artem Zatsarynnyi
 * @author Valeriy Svydenko
 * @author Dmytro Nochevnov
 */
@RunWith(GwtMockitoTestRunner.class)
public class DebuggerTest extends BaseTest {
    @Rule
    public MockitoRule mrule = MockitoJUnit.rule().silent();

    private static final String DEBUG_INFO = "debug_info";

    private static final String SESSION_ID = "debugger_id";

    private static final long THREAD_ID = 1;

    private static final int FRAME_INDEX = 0;

    public static final String PATH = "test/src/main/java/Test.java";

    @Mock
    private DebuggerServiceClient service;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private LocalStorageProvider localStorageProvider;

    @Mock
    private EventBus eventBus;

    @Mock
    private DebuggerLocationHandlerManager debuggerLocationHandlerManager;

    @Mock
    private DebuggerManager debuggerManager;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private BreakpointManager breakpointManager;

    @Mock
    private AppContext appContext;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RequestTransmitter transmitter;

    @Mock
    private RequestHandlerConfigurator configurator;

    @Mock
    private RequestHandlerManager requestHandlerManager;

    @Mock
    private Promise<Void> promiseVoid;

    @Mock
    private Promise<DebuggerInfo> promiseInfo;

    @Mock
    private PromiseError promiseError;

    @Mock
    private VirtualFile file;

    @Mock
    private LocalStorage localStorage;

    @Mock
    private DebuggerObserver observer;

    @Mock
    private LocationDto locationDto;

    @Mock
    private BreakpointDto breakpointDto;

    @Mock
    private Optional<Project> optional;

    @Mock
    private WorkspaceImpl workspace;

    @Mock
    private DebuggerLocalizationConstant constant;

    @Mock
    private PromiseProvider promiseProvider;

    @Mock
    private SuspendEventDto suspendEventDto;

    @Captor
    private ArgumentCaptor<WorkspaceRunningEvent.Handler> workspaceRunningHandlerCaptor;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> operationPromiseErrorCaptor;

    @Captor
    private ArgumentCaptor<Operation<Void>> operationVoidCaptor;

    @Captor
    private ArgumentCaptor<Breakpoint> breakpointCaptor;

    @Captor
    private ArgumentCaptor<Function<DebugSessionDto, Void>> argumentCaptorFunctionJavaDebugSessionVoid;

    public DebuggerDescriptor debuggerDescriptor;

    private AbstractDebugger debugger;

    @Test
    public void testAttachDebugger() throws Exception {
        Mockito.doNothing().when(debugger).subscribeToDebuggerEvents();
        Mockito.doNothing().when(debugger).startCheckingEvents();
        Mockito.doNothing().when(debugger).startDebugger(ArgumentMatchers.any(DebugSessionDto.class));
        debugger.setDebugSession(null);
        final String debugSessionJson = "debugSession";
        Mockito.doReturn(debugSessionJson).when(dtoFactory).toJson(debugSessionDto);
        Map<String, String> connectionProperties = Mockito.mock(Map.class);
        Promise<DebugSessionDto> promiseDebuggerInfo = Mockito.mock(Promise.class);
        Mockito.doReturn(promiseDebuggerInfo).when(service).connect("id", connectionProperties);
        Mockito.doReturn(promiseVoid).when(promiseDebuggerInfo).then(((Function<DebugSessionDto, Void>) (ArgumentMatchers.any())));
        Mockito.doReturn(promiseVoid).when(promiseVoid).catchError(((Operation<PromiseError>) (ArgumentMatchers.any())));
        Promise<Void> result = debugger.connect(connectionProperties);
        TestCase.assertEquals(promiseVoid, result);
        Mockito.verify(promiseDebuggerInfo).then(argumentCaptorFunctionJavaDebugSessionVoid.capture());
        argumentCaptorFunctionJavaDebugSessionVoid.getValue().apply(debugSessionDto);
        Mockito.verify(observer).onDebuggerAttached(debuggerDescriptor);
        Assert.assertTrue(debugger.isConnected());
        Mockito.verify(localStorage).setItem(ArgumentMatchers.eq(LOCAL_STORAGE_DEBUGGER_SESSION_KEY), ArgumentMatchers.eq(debugSessionJson));
    }

    @Test
    public void testAttachDebuggerWithConnection() throws Exception {
        Map<String, String> connectionProperties = Mockito.mock(Map.class);
        debugger.connect(connectionProperties);
        Mockito.verify(service, Mockito.never()).connect(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testDisconnectDebugger() throws Exception {
        Mockito.doReturn(promiseVoid).when(service).disconnect(DebuggerTest.SESSION_ID);
        Mockito.doReturn(promiseVoid).when(promiseVoid).then(((Operation<Void>) (ArgumentMatchers.any())));
        debugger.disconnect();
        Assert.assertFalse(debugger.isConnected());
        Mockito.verify(localStorage).setItem(ArgumentMatchers.eq(LOCAL_STORAGE_DEBUGGER_SESSION_KEY), ArgumentMatchers.eq(""));
        Mockito.verify(promiseVoid).then(operationVoidCaptor.capture());
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationVoidCaptor.getValue().apply(null);
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(observer, Mockito.times(2)).onDebuggerDisconnected();
        Mockito.verify(debuggerManager, Mockito.times(2)).setActiveDebugger(ArgumentMatchers.eq(null));
    }

    @Test
    public void testDisconnectDebuggerWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.disconnect();
        Mockito.verify(service, Mockito.never()).disconnect(ArgumentMatchers.any());
    }

    @Test
    public void testResume() throws Exception {
        ResumeActionDto resumeAction = Mockito.mock(ResumeActionDto.class);
        Mockito.doReturn(promiseVoid).when(service).resume(DebuggerTest.SESSION_ID, resumeAction);
        Mockito.doReturn(resumeAction).when(dtoFactory).createDto(ResumeActionDto.class);
        debugger.resume();
        Mockito.verify(observer).onPreResume();
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getCause();
        Assert.assertTrue(debugger.isConnected());
    }

    @Test
    public void testResumeWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.resume();
        Mockito.verify(service, Mockito.never()).resume(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testStepInto() throws Exception {
        StepIntoActionDto stepIntoAction = Mockito.mock(StepIntoActionDto.class);
        Mockito.doReturn(promiseVoid).when(service).stepInto(DebuggerTest.SESSION_ID, stepIntoAction);
        Mockito.doReturn(stepIntoAction).when(dtoFactory).createDto(StepIntoActionDto.class);
        debugger.setSuspendEvent(suspendEventDto);
        debugger.stepInto();
        Mockito.verify(observer).onPreStepInto();
        Assert.assertTrue(debugger.isConnected());
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getCause();
    }

    @Test
    public void testStepIntoWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.stepInto();
        Mockito.verify(service, Mockito.never()).stepInto(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testStepOver() throws Exception {
        StepOverActionDto stepOverAction = Mockito.mock(StepOverActionDto.class);
        Mockito.doReturn(promiseVoid).when(service).stepOver(DebuggerTest.SESSION_ID, stepOverAction);
        Mockito.doReturn(stepOverAction).when(dtoFactory).createDto(StepOverActionDto.class);
        debugger.setSuspendEvent(suspendEventDto);
        debugger.stepOver();
        Mockito.verify(observer).onPreStepOver();
        Assert.assertTrue(debugger.isConnected());
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getCause();
    }

    @Test
    public void testStepOverWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.stepOver();
        Mockito.verify(service, Mockito.never()).stepOver(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testStepOut() throws Exception {
        StepOutActionDto stepOutAction = Mockito.mock(StepOutActionDto.class);
        Mockito.doReturn(promiseVoid).when(service).stepOut(DebuggerTest.SESSION_ID, stepOutAction);
        Mockito.doReturn(stepOutAction).when(dtoFactory).createDto(StepOutActionDto.class);
        debugger.setSuspendEvent(suspendEventDto);
        debugger.stepOut();
        Mockito.verify(observer).onPreStepOut();
        Assert.assertTrue(debugger.isConnected());
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getCause();
    }

    @Test
    public void testStepOutWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.stepOut();
        Mockito.verify(service, Mockito.never()).stepOut(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testAddBreakpoint() throws Exception {
        MockSettings mockSettings = new MockSettingsImpl<>().defaultAnswer(Mockito.RETURNS_SMART_NULLS).extraInterfaces(Resource.class);
        Project project = Mockito.mock(Project.class);
        Mockito.when(optional.isPresent()).thenReturn(true);
        Mockito.when(optional.get()).thenReturn(project);
        Mockito.when(project.getPath()).thenReturn(DebuggerTest.PATH);
        VirtualFile virtualFile = Mockito.mock(VirtualFile.class, mockSettings);
        Path path = Mockito.mock(Path.class);
        Mockito.when(path.toString()).thenReturn(DebuggerTest.PATH);
        Mockito.when(virtualFile.getLocation()).thenReturn(path);
        Mockito.when(virtualFile.toString()).thenReturn(DebuggerTest.PATH);
        Resource resource = ((Resource) (virtualFile));
        Mockito.when(resource.getRelatedProject()).thenReturn(optional);
        Mockito.doReturn(promiseVoid).when(service).addBreakpoint(DebuggerTest.SESSION_ID, breakpointDto);
        Mockito.doReturn(promiseVoid).when(promiseVoid).then(((Operation<Void>) (ArgumentMatchers.any())));
        Mockito.doReturn(breakpointDto).when(debugger).toDto(ArgumentMatchers.any(Breakpoint.class));
        debugger.addBreakpoint(breakpointDto);
        Mockito.verify(service).addBreakpoint(DebuggerTest.SESSION_ID, breakpointDto);
        Mockito.verify(promiseVoid).then(operationVoidCaptor.capture());
        operationVoidCaptor.getValue().apply(null);
        Mockito.verify(observer).onBreakpointAdded(breakpointCaptor.capture());
        TestCase.assertEquals(breakpointCaptor.getValue(), breakpointDto);
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getMessage();
    }

    @Test
    public void testDeleteBreakpoint() throws Exception {
        Mockito.doReturn(promiseVoid).when(service).deleteBreakpoint(DebuggerTest.SESSION_ID, locationDto);
        Mockito.doReturn(promiseVoid).when(promiseVoid).then(((Operation<Void>) (ArgumentMatchers.any())));
        Mockito.doReturn(locationDto).when(debugger).toDto(ArgumentMatchers.any(Location.class));
        debugger.deleteBreakpoint(breakpointDto);
        Mockito.verify(promiseVoid).then(operationVoidCaptor.capture());
        operationVoidCaptor.getValue().apply(null);
        Mockito.verify(observer).onBreakpointDeleted(breakpointCaptor.capture());
        TestCase.assertEquals(breakpointDto, breakpointCaptor.getValue());
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getMessage();
    }

    @Test
    public void testDeleteBreakpointWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.deleteBreakpoint(breakpointDto);
        Mockito.verify(service, Mockito.never()).deleteBreakpoint(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testDeleteAllBreakpoints() throws Exception {
        Mockito.doReturn(promiseVoid).when(service).deleteAllBreakpoints(DebuggerTest.SESSION_ID);
        Mockito.doReturn(promiseVoid).when(promiseVoid).then(((Operation<Void>) (ArgumentMatchers.any())));
        debugger.deleteAllBreakpoints();
        Mockito.verify(promiseVoid).then(operationVoidCaptor.capture());
        operationVoidCaptor.getValue().apply(null);
        Mockito.verify(observer).onAllBreakpointsDeleted();
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getMessage();
    }

    @Test
    public void testDeleteAllBreakpointsWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.deleteAllBreakpoints();
        Mockito.verify(service, Mockito.never()).deleteAllBreakpoints(ArgumentMatchers.any());
    }

    @Test
    public void testGetValue() throws Exception {
        final VariableDto variableDto = Mockito.mock(VariableDto.class);
        final Variable variable = Mockito.mock(Variable.class);
        final Promise<SimpleValueDto> promiseValue = Mockito.mock(Promise.class);
        SimpleValueDto simpleValueDto = Mockito.mock(SimpleValueDto.class);
        Mockito.doReturn(simpleValueDto).when(dtoFactory).createDto(SimpleValueDto.class);
        Mockito.doReturn(simpleValueDto).when(simpleValueDto).withString(ArgumentMatchers.nullable(String.class));
        SimpleValue simpleValue = Mockito.mock(SimpleValue.class);
        Mockito.doReturn(simpleValue).when(variable).getValue();
        Mockito.doReturn(simpleValue).when(variable).getValue();
        Mockito.doReturn(variableDto).when(dtoFactory).createDto(VariableDto.class);
        Mockito.doReturn(Mockito.mock(VariablePathDto.class)).when(dtoFactory).createDto(VariablePathDto.class);
        Mockito.doReturn(Mockito.mock(VariablePathDto.class)).when(variable).getVariablePath();
        Mockito.doReturn(promiseValue).when(service).getValue(DebuggerTest.SESSION_ID, variableDto, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.doReturn(promiseValue).when(promiseValue).then(((Function<SimpleValueDto, Object>) (ArgumentMatchers.any())));
        Mockito.doReturn(promiseValue).when(promiseValue).catchError(((Operation<PromiseError>) (ArgumentMatchers.any())));
        Promise<? extends SimpleValue> result = debugger.getValue(variable, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        TestCase.assertEquals(promiseValue, result);
    }

    @Test
    public void testGetValueWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.getValue(null, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.verify(service, Mockito.never()).getValue(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(DebuggerTest.THREAD_ID), ArgumentMatchers.eq(DebuggerTest.FRAME_INDEX));
    }

    @Test
    public void testGetStackFrameDump() throws Exception {
        Promise<StackFrameDumpDto> promiseStackFrameDump = Mockito.mock(Promise.class);
        StackFrameDumpDto mockStackFrameDumpDto = Mockito.mock(StackFrameDumpDto.class);
        final String json = "json";
        Mockito.doReturn(json).when(dtoFactory).toJson(mockStackFrameDumpDto);
        Mockito.doReturn(promiseStackFrameDump).when(service).getStackFrameDump(DebuggerTest.SESSION_ID, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.doReturn(promiseStackFrameDump).when(promiseStackFrameDump).then(((Function<StackFrameDumpDto, Object>) (ArgumentMatchers.any())));
        Mockito.doReturn(promiseStackFrameDump).when(promiseStackFrameDump).catchError(((Operation<PromiseError>) (ArgumentMatchers.any())));
        Promise<? extends StackFrameDump> result = debugger.getStackFrameDump(DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        TestCase.assertEquals(promiseStackFrameDump, result);
    }

    @Test
    public void testGetStackFrameDumpWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.getStackFrameDump(DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.verify(service, Mockito.never()).getStackFrameDump(ArgumentMatchers.any(), ArgumentMatchers.eq(DebuggerTest.THREAD_ID), ArgumentMatchers.eq(DebuggerTest.FRAME_INDEX));
    }

    @Test
    public void testEvaluateExpression() throws Exception {
        final String expression = "a = 1";
        Promise<String> promiseString = Mockito.mock(Promise.class);
        Mockito.doReturn(promiseString).when(service).evaluate(DebuggerTest.SESSION_ID, expression, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Promise<String> result = debugger.evaluate(expression, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        TestCase.assertEquals(promiseString, result);
    }

    @Test
    public void testEvaluateExpressionWithoutConnection() throws Exception {
        debugger.setDebugSession(null);
        debugger.evaluate("any", DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.verify(service, Mockito.never()).evaluate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(DebuggerTest.THREAD_ID), ArgumentMatchers.eq(DebuggerTest.FRAME_INDEX));
    }

    @Test
    public void testChangeVariableValue() throws Exception {
        final List<String> path = Mockito.mock(List.class);
        final String newValue = "new-value";
        VariablePath variablePath = Mockito.mock(VariablePathDto.class);
        Mockito.doReturn(path).when(variablePath).getPath();
        VariableDto variableDto = Mockito.mock(VariableDto.class);
        Mockito.doReturn(variableDto).when(dtoFactory).createDto(VariableDto.class);
        SimpleValueDto simpleValueDto = Mockito.mock(SimpleValueDto.class);
        Mockito.doReturn(simpleValueDto).when(dtoFactory).createDto(SimpleValueDto.class);
        Mockito.doReturn(simpleValueDto).when(simpleValueDto).withString(ArgumentMatchers.anyString());
        Variable variable = Mockito.mock(Variable.class);
        Mockito.doReturn(Mockito.mock(VariablePathDto.class)).when(dtoFactory).createDto(VariablePathDto.class);
        Mockito.doReturn(variablePath).when(variable).getVariablePath();
        SimpleValue simpleValue = Mockito.mock(SimpleValue.class);
        Mockito.doReturn(newValue).when(simpleValue).getString();
        Mockito.doReturn(simpleValue).when(variable).getValue();
        Mockito.doReturn(promiseVoid).when(service).setValue(DebuggerTest.SESSION_ID, variableDto, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.doReturn(promiseVoid).when(promiseVoid).then(((Operation<Void>) (ArgumentMatchers.any())));
        debugger.setValue(variable, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.verify(promiseVoid).then(operationVoidCaptor.capture());
        operationVoidCaptor.getValue().apply(null);
        Mockito.verify(observer).onValueChanged(variable, DebuggerTest.THREAD_ID, DebuggerTest.FRAME_INDEX);
        Mockito.verify(promiseVoid).catchError(operationPromiseErrorCaptor.capture());
        operationPromiseErrorCaptor.getValue().apply(promiseError);
        Mockito.verify(promiseError).getMessage();
    }

    private class TestDebugger extends AbstractDebugger {
        public TestDebugger(DebuggerServiceClient service, RequestTransmitter transmitter, RequestHandlerConfigurator configurator, DtoFactory dtoFactory, LocalStorageProvider localStorageProvider, EventBus eventBus, DebuggerManager debuggerManager, NotificationManager notificationManager, AppContext appContext, DebuggerLocalizationConstant constant, String id, DebuggerLocationHandlerManager debuggerLocationHandlerManager, PromiseProvider promiseProvider) {
            super(service, transmitter, configurator, dtoFactory, localStorageProvider, eventBus, debuggerManager, notificationManager, appContext, breakpointManager, constant, requestHandlerManager, debuggerLocationHandlerManager, promiseProvider, id);
        }

        @Override
        protected DebuggerDescriptor toDescriptor(Map<String, String> connectionProperties) {
            return debuggerDescriptor;
        }
    }
}

