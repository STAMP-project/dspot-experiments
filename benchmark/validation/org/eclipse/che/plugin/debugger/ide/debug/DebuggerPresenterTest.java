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


import StatusNotification.DisplayMode;
import StatusNotification.Status;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Collections;
import java.util.List;
import org.eclipse.che.api.debug.shared.dto.SimpleValueDto;
import org.eclipse.che.api.debug.shared.model.Location;
import org.eclipse.che.api.debug.shared.model.MutableVariable;
import org.eclipse.che.api.debug.shared.model.StackFrameDump;
import org.eclipse.che.api.debug.shared.model.ThreadState;
import org.eclipse.che.api.debug.shared.model.Variable;
import org.eclipse.che.api.debug.shared.model.WatchExpression;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.debug.BreakpointManager;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.workspace.event.WorkspaceStoppedEvent;
import org.eclipse.che.ide.debug.Debugger;
import org.eclipse.che.ide.debug.DebuggerDescriptor;
import org.eclipse.che.ide.debug.DebuggerManager;
import org.eclipse.che.ide.ui.toolbar.ToolbarPresenter;
import org.eclipse.che.plugin.debugger.ide.BaseTest;
import org.eclipse.che.plugin.debugger.ide.DebuggerLocalizationConstant;
import org.eclipse.che.plugin.debugger.ide.DebuggerResources;
import org.eclipse.che.plugin.debugger.ide.debug.breakpoint.BreakpointContextMenuFactory;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Testing {@link DebuggerPresenter} functionality.
 *
 * @author Dmytro Nochevnov
 * @author Oleksandr Andriienko
 */
public class DebuggerPresenterTest extends BaseTest {
    @Rule
    public MockitoRule mrule = MockitoJUnit.rule().silent();

    private static final long THREAD_ID = 1;

    private static final int FRAME_INDEX = 0;

    @Mock
    private DebuggerView view;

    @Mock
    private DebuggerLocalizationConstant constant;

    @Mock
    private BreakpointManager breakpointManager;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private DebuggerResources debuggerResources;

    @Mock
    @DebuggerToolbar
    private ToolbarPresenter debuggerToolbar;

    @Mock
    @DebuggerWatchToolBar
    private ToolbarPresenter watchToolbar;

    @Mock
    private DebuggerManager debuggerManager;

    @Mock
    private WorkspaceAgent workspaceAgent;

    @Mock
    private DebuggerLocationHandlerManager debuggerLocationHandlerManager;

    @Mock
    private BreakpointContextMenuFactory breakpointContextMenuFactory;

    @Mock
    private Debugger debugger;

    @Mock
    private MutableVariable selectedVariable;

    @Mock
    private List<ThreadState> threadDump;

    @Mock
    private StackFrameDump stackFrame;

    @Mock
    private Promise<SimpleValueDto> promiseValue;

    @Mock
    private Promise<List<ThreadState>> promiseThreadDump;

    @Mock
    private Promise<StackFrameDump> promiseStackFrame;

    @Mock
    private Promise<Void> promiseVoid;

    @Mock
    private EventBus eventBus;

    @Captor
    private ArgumentCaptor<Operation<Void>> operationVoidCaptor;

    @Captor
    private ArgumentCaptor<Operation<List<ThreadState>>> operationThreadDumpCaptor;

    @Captor
    private ArgumentCaptor<Operation<StackFrameDump>> operationStackFrameCaptor;

    @Captor
    private ArgumentCaptor<Operation<SimpleValueDto>> operationValueCaptor;

    private DebuggerPresenter presenter;

    @Test
    public void shouldSetNestedVariablesWhenNodeIsExpended() throws OperationException {
        SimpleValueDto valueDto = Mockito.mock(SimpleValueDto.class);
        Mockito.doReturn(promiseValue).when(debugger).getValue(ArgumentMatchers.eq(selectedVariable), ArgumentMatchers.eq(DebuggerPresenterTest.THREAD_ID), ArgumentMatchers.eq(DebuggerPresenterTest.FRAME_INDEX));
        Mockito.doReturn(promiseValue).when(promiseValue).then(((Operation<SimpleValueDto>) (ArgumentMatchers.any())));
        presenter.onExpandVariable(selectedVariable);
        Mockito.verify(promiseValue).then(operationValueCaptor.capture());
        operationValueCaptor.getValue().apply(valueDto);
        Mockito.verify(debugger).getValue(ArgumentMatchers.eq(selectedVariable), ArgumentMatchers.eq(DebuggerPresenterTest.THREAD_ID), ArgumentMatchers.eq(DebuggerPresenterTest.FRAME_INDEX));
        Mockito.verify(view).expandVariable(ArgumentMatchers.any(Variable.class));
    }

    @Test
    public void shouldUpdateStackFrameDumpAndVariablesOnNewSelectedThread() throws Exception {
        Mockito.doNothing().when(presenter).refreshVariables(DebuggerPresenterTest.THREAD_ID, 0);
        presenter.onSelectedThread(DebuggerPresenterTest.THREAD_ID);
        Mockito.verify(presenter).refreshView(DebuggerPresenterTest.THREAD_ID);
    }

    @Test
    public void shouldUpdateVariablesOnSelectedFrame() throws Exception {
        Mockito.doNothing().when(presenter).refreshVariables(DebuggerPresenterTest.THREAD_ID, DebuggerPresenterTest.FRAME_INDEX);
        presenter.onSelectedFrame(DebuggerPresenterTest.FRAME_INDEX);
        Mockito.verify(presenter).refreshVariables(DebuggerPresenterTest.THREAD_ID, DebuggerPresenterTest.FRAME_INDEX);
    }

    @Test
    public void whenDebuggerStoppedThenPresenterShouldUpdateFramesAndVariables() throws Exception {
        Location executionPoint = Mockito.mock(Location.class);
        Mockito.doReturn(DebuggerPresenterTest.THREAD_ID).when(executionPoint).getThreadId();
        Mockito.doReturn(promiseThreadDump).when(debugger).getThreadDump();
        Mockito.doReturn(promiseThreadDump).when(promiseThreadDump).then(((Operation<List<ThreadState>>) (ArgumentMatchers.any())));
        presenter.onBreakpointStopped(null, executionPoint);
        Mockito.verify(promiseThreadDump).then(operationThreadDumpCaptor.capture());
        operationThreadDumpCaptor.getValue().apply(threadDump);
        Mockito.verify(presenter).refreshView();
        Mockito.verify(view).setThreadDump(ArgumentMatchers.eq(threadDump), ArgumentMatchers.anyLong());
    }

    @Test
    public void updateVariablesShouldUpdateView() throws Exception {
        Mockito.doReturn(promiseStackFrame).when(debugger).getStackFrameDump(DebuggerPresenterTest.THREAD_ID, DebuggerPresenterTest.FRAME_INDEX);
        Mockito.doReturn(promiseStackFrame).when(promiseStackFrame).then(((Operation<StackFrameDump>) (ArgumentMatchers.any())));
        presenter.refreshVariables(DebuggerPresenterTest.THREAD_ID, DebuggerPresenterTest.FRAME_INDEX);
        Mockito.verify(promiseStackFrame).then(operationStackFrameCaptor.capture());
        operationStackFrameCaptor.getValue().apply(stackFrame);
        Mockito.verify(view).setVariables(stackFrame.getVariables());
    }

    @Test
    public void showDebuggerPanelAndSetVMNameOnDebuggerAttached() throws Exception {
        DebuggerDescriptor debuggerDescriptor = Mockito.mock(DebuggerDescriptor.class);
        Mockito.when(debuggerDescriptor.getAddress()).thenReturn("address");
        Mockito.when(debuggerDescriptor.getInfo()).thenReturn("info");
        Mockito.doReturn(promiseVoid).when(promiseVoid).then(((Operation<Void>) (ArgumentMatchers.any())));
        Mockito.doNothing().when(presenter).showDebuggerPanel();
        Mockito.when(notificationManager.notify(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(Status.class), ArgumentMatchers.nullable(DisplayMode.class))).thenReturn(Mockito.mock(StatusNotification.class));
        presenter.onDebuggerAttached(debuggerDescriptor);
        Mockito.verify(presenter).showDebuggerPanel();
        Mockito.verify(view).setVMName("info");
    }

    @Test
    public void testOnDebuggerDisconnected() {
        final String address = "";
        String title = "title";
        Mockito.doReturn(title).when(this.constant).debuggerDisconnectedTitle();
        String description = "description";
        Mockito.doReturn(description).when(this.constant).debuggerDisconnectedDescription(address);
        presenter.onDebuggerDisconnected();
        notificationManager.notify(ArgumentMatchers.eq(title), ArgumentMatchers.eq(description), ArgumentMatchers.eq(SUCCESS), ArgumentMatchers.eq(NOT_EMERGE_MODE));
    }

    @Test
    public void shouldSetNewValueOnValueChanged() throws Exception {
        SimpleValueDto valueDto = Mockito.mock(SimpleValueDto.class);
        Mockito.doReturn(promiseValue).when(debugger).getValue(ArgumentMatchers.eq(selectedVariable), ArgumentMatchers.eq(DebuggerPresenterTest.THREAD_ID), ArgumentMatchers.eq(DebuggerPresenterTest.FRAME_INDEX));
        Mockito.doReturn(promiseValue).when(promiseValue).then(((Operation<SimpleValueDto>) (ArgumentMatchers.any())));
        presenter.onValueChanged(selectedVariable, DebuggerPresenterTest.THREAD_ID, DebuggerPresenterTest.FRAME_INDEX);
        Mockito.verify(promiseValue).then(operationValueCaptor.capture());
        operationValueCaptor.getValue().apply(valueDto);
        Mockito.verify(debugger).getValue(ArgumentMatchers.eq(selectedVariable), ArgumentMatchers.eq(DebuggerPresenterTest.THREAD_ID), ArgumentMatchers.eq(DebuggerPresenterTest.FRAME_INDEX));
        Mockito.verify(view).updateVariable(ArgumentMatchers.any(Variable.class));
    }

    @Test
    public void shouldClearPanelOnWorkspaceStopped() throws Exception {
        Promise promise = Mockito.mock(Promise.class);
        Mockito.when(promise.then(ArgumentMatchers.any(Operation.class))).thenReturn(promise);
        Mockito.when(debugger.evaluate(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(promise);
        WatchExpression watchExpression = Mockito.mock(WatchExpression.class);
        Mockito.when(watchExpression.getExpression()).thenReturn("expresion");
        presenter.onAddExpressionBtnClicked(watchExpression);
        presenter.onWorkspaceStopped(Mockito.mock(WorkspaceStoppedEvent.class));
        Mockito.verify(view).setVMName(ArgumentMatchers.eq(""));
        Mockito.verify(view).setExecutionPoint(ArgumentMatchers.eq(null));
        Mockito.verify(view).setThreadDump(ArgumentMatchers.eq(Collections.emptyList()), ArgumentMatchers.eq((-1L)));
        Mockito.verify(view).setFrames(ArgumentMatchers.eq(Collections.emptyList()));
        Mockito.verify(view).removeAllVariables();
        Mockito.verify(watchExpression).setResult(ArgumentMatchers.eq(""));
        Mockito.verify(view).updateExpression(ArgumentMatchers.eq(watchExpression));
    }
}

