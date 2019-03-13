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
package org.eclipse.che.ide.command.explorer;


import CommandsExplorerPresenter.RefreshViewTask;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandAddedEvent;
import org.eclipse.che.ide.api.command.CommandAddedEvent.CommandAddedHandler;
import org.eclipse.che.ide.api.command.CommandGoal;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandImpl.ApplicableContext;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.command.CommandRemovedEvent;
import org.eclipse.che.ide.api.command.CommandRemovedEvent.CommandRemovedHandler;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.command.CommandUpdatedEvent;
import org.eclipse.che.ide.api.command.CommandUpdatedEvent.CommandUpdatedHandler;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.command.CommandResources;
import org.eclipse.che.ide.command.node.NodeFactory;
import org.eclipse.che.ide.command.type.chooser.CommandTypeChooser;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link CommandsExplorerPresenter}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class CommandsExplorerPresenterTest {
    @Mock
    private CommandsExplorerView view;

    @Mock
    private CommandResources resources;

    @Mock
    private WorkspaceAgent workspaceAgent;

    @Mock
    private CommandManager commandManager;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private CommandTypeChooser commandTypeChooser;

    @Mock
    private ExplorerMessages messages;

    @Mock
    private RefreshViewTask refreshViewTask;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private NodeFactory nodeFactory;

    @Mock
    private Provider<EditorAgent> editorAgentProvider;

    @Mock
    private AppContext appContext;

    @Mock
    private EventBus eventBus;

    @InjectMocks
    private CommandsExplorerPresenter presenter;

    @Mock
    private Promise<Void> voidPromise;

    @Mock
    private Promise<CommandImpl> commandPromise;

    @Mock
    private Promise<CommandType> commandTypePromise;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> errorOperationCaptor;

    @Captor
    private ArgumentCaptor<Operation<CommandType>> commandTypeOperationCaptor;

    @Test
    public void shouldSetViewDelegate() throws Exception {
        Mockito.verify(view).setDelegate(ArgumentMatchers.eq(presenter));
    }

    @Test
    public void testStart() throws Exception {
        Mockito.verify(eventBus).addHandler(ArgumentMatchers.eq(CommandAddedEvent.getType()), ArgumentMatchers.any(CommandAddedHandler.class));
        Mockito.verify(eventBus).addHandler(ArgumentMatchers.eq(CommandRemovedEvent.getType()), ArgumentMatchers.any(CommandRemovedHandler.class));
        Mockito.verify(eventBus).addHandler(ArgumentMatchers.eq(CommandUpdatedEvent.getType()), ArgumentMatchers.any(CommandUpdatedHandler.class));
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        presenter.go(container);
        verifyViewRefreshed();
        Mockito.verify(container).setWidget(view);
    }

    @Test
    public void shouldReturnTitle() throws Exception {
        presenter.getTitle();
        Mockito.verify(messages).partTitle();
    }

    @Test
    public void shouldReturnView() throws Exception {
        IsWidget view = presenter.getView();
        Assert.assertEquals(this.view, view);
    }

    @Test
    public void shouldReturnTitleTooltip() throws Exception {
        presenter.getTitleToolTip();
        Mockito.verify(messages).partTooltip();
    }

    @Test
    public void shouldReturnTitleImage() throws Exception {
        presenter.getTitleImage();
        Mockito.verify(resources).explorerPart();
    }

    @Test
    public void shouldCreateCommand() throws Exception {
        // given
        CommandType selectedCommandType = Mockito.mock(CommandType.class);
        String commandTypeId = "mvn";
        Mockito.when(selectedCommandType.getId()).thenReturn(commandTypeId);
        CommandGoal selectedCommandGoal = Mockito.mock(CommandGoal.class);
        String commandGoalId = "test";
        Mockito.when(selectedCommandGoal.getId()).thenReturn(commandGoalId);
        Mockito.when(view.getSelectedGoal()).thenReturn(selectedCommandGoal);
        Mockito.when(commandTypeChooser.show(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(commandTypePromise);
        Mockito.when(appContext.getProjects()).thenReturn(new Project[0]);
        Mockito.when(commandManager.createCommand(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ApplicableContext.class))).thenReturn(commandPromise);
        Mockito.when(commandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        Mockito.when(commandPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        // when
        presenter.onCommandAdd(0, 0);
        // then
        Mockito.verify(commandTypeChooser).show(0, 0);
        Mockito.verify(commandTypePromise).then(commandTypeOperationCaptor.capture());
        commandTypeOperationCaptor.getValue().apply(selectedCommandType);
        Mockito.verify(view).getSelectedGoal();
        Mockito.verify(commandManager).createCommand(ArgumentMatchers.eq(commandGoalId), ArgumentMatchers.eq(commandTypeId), ArgumentMatchers.any(ApplicableContext.class));
    }

    @Test(expected = OperationException.class)
    public void shouldShowNotificationWhenFailedToCreateCommand() throws Exception {
        // given
        CommandType selectedCommandType = Mockito.mock(CommandType.class);
        String commandTypeId = "mvn";
        Mockito.when(selectedCommandType.getId()).thenReturn(commandTypeId);
        CommandGoal selectedCommandGoal = Mockito.mock(CommandGoal.class);
        String commandGoalId = "test";
        Mockito.when(selectedCommandGoal.getId()).thenReturn(commandGoalId);
        Mockito.when(view.getSelectedGoal()).thenReturn(selectedCommandGoal);
        Mockito.when(commandTypeChooser.show(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(commandTypePromise);
        Mockito.when(appContext.getProjects()).thenReturn(new Project[0]);
        Mockito.when(commandManager.createCommand(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ApplicableContext.class))).thenReturn(commandPromise);
        Mockito.when(commandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        Mockito.when(commandPromise.catchError(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        // when
        presenter.onCommandAdd(0, 0);
        // then
        Mockito.verify(commandTypeChooser).show(0, 0);
        Mockito.verify(commandTypePromise).then(commandTypeOperationCaptor.capture());
        commandTypeOperationCaptor.getValue().apply(selectedCommandType);
        Mockito.verify(view).getSelectedGoal();
        Mockito.verify(commandManager).createCommand(ArgumentMatchers.eq(commandGoalId), ArgumentMatchers.eq(commandTypeId), ArgumentMatchers.any(ApplicableContext.class));
        Mockito.verify(commandPromise).catchError(errorOperationCaptor.capture());
        errorOperationCaptor.getValue().apply(Mockito.mock(PromiseError.class));
        Mockito.verify(messages).unableCreate();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(EMERGE_MODE));
    }

    @Test
    public void shouldDuplicateCommand() throws Exception {
        CommandImpl command = Mockito.mock(CommandImpl.class);
        Mockito.when(commandManager.createCommand(ArgumentMatchers.any(CommandImpl.class))).thenReturn(commandPromise);
        Mockito.when(commandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        presenter.onCommandDuplicate(command);
        Mockito.verify(commandManager).createCommand(command);
    }

    @Test(expected = OperationException.class)
    public void shouldShowNotificationWhenFailedToDuplicateCommand() throws Exception {
        CommandImpl command = Mockito.mock(CommandImpl.class);
        Mockito.when(commandManager.createCommand(ArgumentMatchers.any(CommandImpl.class))).thenReturn(commandPromise);
        Mockito.when(commandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        presenter.onCommandDuplicate(command);
        Mockito.verify(commandPromise).catchError(errorOperationCaptor.capture());
        errorOperationCaptor.getValue().apply(Mockito.mock(PromiseError.class));
        Mockito.verify(messages).unableDuplicate();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(EMERGE_MODE));
    }

    @Test
    public void shouldRemoveCommand() throws Exception {
        // given
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        ArgumentCaptor<ConfirmCallback> confirmCallbackCaptor = ArgumentCaptor.forClass(ConfirmCallback.class);
        CommandImpl command = Mockito.mock(CommandImpl.class);
        String cmdName = "build";
        Mockito.when(command.getName()).thenReturn(cmdName);
        Mockito.when(commandManager.removeCommand(ArgumentMatchers.nullable(String.class))).thenReturn(voidPromise);
        // when
        presenter.onCommandRemove(command);
        // then
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), confirmCallbackCaptor.capture(), ArgumentMatchers.isNull());
        confirmCallbackCaptor.getValue().accepted();
        Mockito.verify(confirmDialog).show();
        Mockito.verify(commandManager).removeCommand(cmdName);
    }

    @Test
    public void shouldNotRemoveCommandWhenCancelled() throws Exception {
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        CommandImpl command = Mockito.mock(CommandImpl.class);
        presenter.onCommandRemove(command);
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.isNull());
        Mockito.verify(confirmDialog).show();
        Mockito.verify(commandManager, Mockito.never()).removeCommand(ArgumentMatchers.anyString());
    }

    @Test(expected = OperationException.class)
    public void shouldShowNotificationWhenFailedToRemoveCommand() throws Exception {
        // given
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        ArgumentCaptor<ConfirmCallback> confirmCallbackCaptor = ArgumentCaptor.forClass(ConfirmCallback.class);
        Mockito.when(commandManager.removeCommand(ArgumentMatchers.nullable(String.class))).thenReturn(voidPromise);
        // when
        presenter.onCommandRemove(Mockito.mock(CommandImpl.class));
        // then
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), confirmCallbackCaptor.capture(), ArgumentMatchers.isNull());
        confirmCallbackCaptor.getValue().accepted();
        Mockito.verify(voidPromise).catchError(errorOperationCaptor.capture());
        errorOperationCaptor.getValue().apply(Mockito.mock(PromiseError.class));
        Mockito.verify(messages).unableRemove();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(EMERGE_MODE));
    }
}

