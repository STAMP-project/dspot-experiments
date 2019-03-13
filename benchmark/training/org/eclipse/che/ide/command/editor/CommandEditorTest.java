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
package org.eclipse.che.ide.command.editor;


import EditorAgent.OpenEditorCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.command.CommandRemovedEvent;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.command.editor.page.commandline.CommandLinePage;
import org.eclipse.che.ide.command.editor.page.goal.GoalPage;
import org.eclipse.che.ide.command.editor.page.name.NamePage;
import org.eclipse.che.ide.command.editor.page.previewurl.PreviewUrlPage;
import org.eclipse.che.ide.command.editor.page.project.ProjectsPage;
import org.eclipse.che.ide.command.node.CommandFileNode;
import org.eclipse.che.ide.command.node.NodeFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link CommandEditor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandEditorTest {
    private static final String EDITED_COMMAND_NAME = "build";

    @Mock
    private CommandEditorView view;

    @Mock
    private WorkspaceAgent workspaceAgent;

    @Mock
    private IconRegistry iconRegistry;

    @Mock
    private CommandManager commandManager;

    @Mock
    private NamePage namePage;

    @Mock
    private CommandLinePage commandLinePage;

    @Mock
    private GoalPage goalPage;

    @Mock
    private ProjectsPage projectsPage;

    @Mock
    private PreviewUrlPage previewUrlPage;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private CoreLocalizationConstant localizationConstants;

    @Mock
    private EditorMessages editorMessages;

    @Mock
    private NodeFactory nodeFactory;

    @Mock
    private EventBus eventBus;

    @InjectMocks
    private CommandEditor editor;

    @Mock
    private EditorInput editorInput;

    @Mock
    private CommandFileNode editedCommandFile;

    @Mock
    private CommandImpl editedCommand;

    @Mock
    private OpenEditorCallback openEditorCallback;

    @Mock
    private HandlerRegistration handlerRegistration;

    @Mock
    private Promise<CommandImpl> commandPromise;

    @Captor
    private ArgumentCaptor<Operation<CommandImpl>> operationCaptor;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> errorOperationCaptor;

    @Test
    public void shouldBeInitialized() throws Exception {
        Mockito.verify(view).setDelegate(editor);
        Mockito.verify(eventBus).addHandler(CommandRemovedEvent.getType(), editor);
        verifyPagesInitialized();
    }

    @Test
    public void shouldExposeViewOnGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        editor.go(container);
        Mockito.verify(container).setWidget(view);
    }

    @Test
    public void shouldReturnTitle() throws Exception {
        editor.getTitle();
        Mockito.verify(editorInput).getName();
    }

    @Test
    public void shouldReturnTitleTooltip() throws Exception {
        editor.getTitleToolTip();
        Mockito.verify(editorInput).getName();
    }

    @Test
    public void shouldReturnView() throws Exception {
        Assert.assertEquals(view, editor.getView());
    }

    @Test
    public void shouldSaveCommand() throws Exception {
        Mockito.when(commandManager.updateCommand(ArgumentMatchers.anyString(), ArgumentMatchers.eq(editor.editedCommand))).thenReturn(commandPromise);
        Mockito.when(commandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        editor.doSave();
        Mockito.verify(commandManager).updateCommand(ArgumentMatchers.anyString(), ArgumentMatchers.eq(editor.editedCommand));
        verifyPagesInitialized();
    }

    @Test(expected = OperationException.class)
    public void shouldShowNotificationWhenFailedToSaveCommand() throws Exception {
        Mockito.when(commandManager.updateCommand(ArgumentMatchers.anyString(), ArgumentMatchers.eq(editor.editedCommand))).thenReturn(commandPromise);
        Mockito.when(commandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        editor.doSave();
        Mockito.verify(commandPromise).catchError(errorOperationCaptor.capture());
        errorOperationCaptor.getValue().apply(Mockito.mock(PromiseError.class));
        Mockito.verify(editorMessages).editorMessageUnableToSave();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), WARNING, EMERGE_MODE);
    }

    @Test
    public void shouldNotSaveCommandWhenInvalidData() throws Exception {
        Mockito.when(namePage.hasInvalidData()).thenReturn(true);
        editor.doSave();
        Mockito.verify(dialogFactory).createMessageDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(ConfirmCallback.class));
        Mockito.verify(commandManager, Mockito.never()).updateCommand(ArgumentMatchers.anyString(), ArgumentMatchers.eq(editor.editedCommand));
    }

    @Test
    public void shouldCloseEditor() throws Exception {
        editor.close(true);
        Mockito.verify(workspaceAgent).removePart(editor);
    }

    @Test
    public void shouldCloseEditorWhenCancellingRequested() throws Exception {
        editor.onCommandCancel();
        Mockito.verify(workspaceAgent).removePart(editor);
    }

    @Test
    public void shouldSaveCommandWhenSavingRequested() throws Exception {
        Mockito.when(commandManager.updateCommand(ArgumentMatchers.anyString(), ArgumentMatchers.eq(editor.editedCommand))).thenReturn(commandPromise);
        Mockito.when(commandPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(commandPromise);
        editor.onCommandSave();
        Mockito.verify(commandManager).updateCommand(ArgumentMatchers.anyString(), ArgumentMatchers.eq(editor.editedCommand));
    }

    @Test
    public void shouldCloseEditorWhenEditedCommandRemoved() throws Exception {
        CommandImpl removedCommand = Mockito.mock(CommandImpl.class);
        Mockito.when(removedCommand.getName()).thenReturn(CommandEditorTest.EDITED_COMMAND_NAME);
        CommandRemovedEvent event = Mockito.mock(CommandRemovedEvent.class);
        Mockito.when(event.getCommand()).thenReturn(removedCommand);
        editor.onCommandRemoved(event);
        Mockito.verify(editorAgent).closeEditor(editor);
        Mockito.verify(handlerRegistration).removeHandler();
    }
}

