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
package org.eclipse.che.ide.command.toolbar.commands;


import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import org.eclipse.che.ide.api.command.CommandExecutor;
import org.eclipse.che.ide.api.command.CommandGoal;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.workspace.model.MachineImpl;
import org.eclipse.che.ide.command.goal.DebugGoal;
import org.eclipse.che.ide.command.goal.RunGoal;
import org.eclipse.che.ide.command.toolbar.CommandCreationGuide;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link ExecuteCommandPresenter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExecuteCommandPresenterTest {
    @Mock
    private ExecuteCommandView view;

    @Mock
    private CommandManager commandManager;

    @Mock
    private Provider<CommandExecutor> commandExecutorProvider;

    @Mock
    private CommandCreationGuide commandCreationGuide;

    @Mock
    private RunGoal runGoal;

    @Mock
    private DebugGoal debugGoal;

    @Mock
    private EventBus eventBus;

    @InjectMocks
    private ExecuteCommandPresenter presenter;

    @Mock
    private CommandExecutor commandExecutor;

    @Test
    public void shouldSetDelegate() throws Exception {
        Mockito.verify(view).setDelegate(presenter);
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        presenter.go(container);
        Mockito.verify(view).setGoals(ArgumentMatchers.anySetOf(CommandGoal.class));
        Mockito.verify(container).setWidget(view);
    }

    @Test
    public void shouldExecuteCommand() throws Exception {
        CommandImpl command = Mockito.mock(CommandImpl.class);
        presenter.onCommandExecute(command);
        Mockito.verify(commandExecutor).executeCommand(ArgumentMatchers.eq(command));
    }

    @Test
    public void shouldExecuteCommandOnMachine() throws Exception {
        CommandImpl command = Mockito.mock(CommandImpl.class);
        MachineImpl machine = Mockito.mock(MachineImpl.class);
        Mockito.when(machine.getName()).thenReturn("machine_name");
        presenter.onCommandExecute(command, machine);
        Mockito.verify(commandExecutor).executeCommand(ArgumentMatchers.eq(command), ArgumentMatchers.eq("machine_name"));
    }

    @Test
    public void shouldGuideUser() throws Exception {
        CommandGoal goal = Mockito.mock(CommandGoal.class);
        presenter.onGuide(goal);
        Mockito.verify(commandCreationGuide).guide(ArgumentMatchers.eq(goal));
    }
}

