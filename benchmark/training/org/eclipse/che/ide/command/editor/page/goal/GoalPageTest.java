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
package org.eclipse.che.ide.command.editor.page.goal;


import org.eclipse.che.ide.api.command.CommandGoal;
import org.eclipse.che.ide.api.command.CommandGoalRegistry;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandImpl.ApplicableContext;
import org.eclipse.che.ide.command.editor.EditorMessages;
import org.eclipse.che.ide.command.editor.page.CommandEditorPage.DirtyStateListener;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.input.InputCallback;
import org.eclipse.che.ide.ui.dialogs.input.InputDialog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link GoalPage}.
 */
@RunWith(MockitoJUnitRunner.class)
public class GoalPageTest {
    private static final String COMMAND_GOAL_ID = "build";

    @Mock
    private GoalPageView view;

    @Mock
    private CommandGoalRegistry goalRegistry;

    @Mock
    private EditorMessages messages;

    @Mock
    private DialogFactory dialogFactory;

    @InjectMocks
    private GoalPage page;

    @Mock
    private DirtyStateListener dirtyStateListener;

    @Mock
    private CommandImpl editedCommand;

    @Mock
    private ApplicableContext editedCommandApplicableContext;

    @Test
    public void shouldSetViewDelegate() throws Exception {
        Mockito.verify(view).setDelegate(page);
    }

    @Test
    public void shouldInitializeView() throws Exception {
        Mockito.verify(goalRegistry).getAllGoals();
        Mockito.verify(view).setAvailableGoals(ArgumentMatchers.anySet());
        Mockito.verify(view).setGoal(ArgumentMatchers.eq(GoalPageTest.COMMAND_GOAL_ID));
    }

    @Test
    public void shouldReturnView() throws Exception {
        Assert.assertEquals(view, page.getView());
    }

    @Test
    public void shouldNotifyListenerWhenGoalChanged() throws Exception {
        page.onGoalChanged("test");
        Mockito.verify(dirtyStateListener, Mockito.times(2)).onDirtyStateChanged();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetDefaultGoalIfInitialIsNull() throws Exception {
        Mockito.reset(goalRegistry);
        Mockito.reset(view);
        String defaultGoalId = "Common";
        CommandGoal defaultGoal = Mockito.mock(CommandGoal.class);
        Mockito.when(goalRegistry.getDefaultGoal()).thenReturn(defaultGoal);
        Mockito.when(defaultGoal.getId()).thenReturn(defaultGoalId);
        Mockito.when(editedCommand.getGoal()).thenReturn(null);
        page.initialize();
        Mockito.verify(goalRegistry).getAllGoals();
        Mockito.verify(view).setAvailableGoals(ArgumentMatchers.anySet());
        Mockito.verify(view).setGoal(defaultGoalId);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSetDefaultGoalIfInitialIsEmpty() throws Exception {
        Mockito.reset(goalRegistry);
        Mockito.reset(view);
        String defaultGoalId = "Common";
        CommandGoal defaultGoal = Mockito.mock(CommandGoal.class);
        Mockito.when(goalRegistry.getDefaultGoal()).thenReturn(defaultGoal);
        Mockito.when(defaultGoal.getId()).thenReturn(defaultGoalId);
        Mockito.when(editedCommand.getGoal()).thenReturn("");
        page.initialize();
        Mockito.verify(goalRegistry).getAllGoals();
        Mockito.verify(view).setAvailableGoals(ArgumentMatchers.anySet());
        Mockito.verify(view).setGoal(defaultGoalId);
    }

    @Test
    public void shouldCreateGoal() throws Exception {
        // given
        InputDialog inputDialog = Mockito.mock(InputDialog.class);
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(0), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(InputCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(inputDialog);
        String newGoalId = "new goal";
        // when
        page.onCreateGoal();
        // then
        ArgumentCaptor<InputCallback> inputCaptor = ArgumentCaptor.forClass(InputCallback.class);
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(0), ArgumentMatchers.nullable(String.class), inputCaptor.capture(), ArgumentMatchers.isNull());
        Mockito.verify(inputDialog).show();
        inputCaptor.getValue().accepted(newGoalId);
        Mockito.verify(view).setGoal(ArgumentMatchers.eq(newGoalId));
        Mockito.verify(editedCommand).setGoal(ArgumentMatchers.eq(newGoalId));
        Mockito.verify(dirtyStateListener, Mockito.times(2)).onDirtyStateChanged();
    }
}

