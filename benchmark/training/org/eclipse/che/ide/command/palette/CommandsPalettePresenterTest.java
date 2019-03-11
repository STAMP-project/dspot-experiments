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
package org.eclipse.che.ide.command.palette;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandExecutor;
import org.eclipse.che.ide.api.command.CommandGoal;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.workspace.model.MachineImpl;
import org.eclipse.che.ide.api.workspace.model.RuntimeImpl;
import org.eclipse.che.ide.api.workspace.model.WorkspaceImpl;
import org.eclipse.che.ide.command.CommandUtils;
import org.eclipse.che.ide.machine.chooser.MachineChooser;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
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
 * Tests for {@link CommandsPalettePresenter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommandsPalettePresenterTest {
    @Mock
    private CommandsPaletteView view;

    @Mock
    private CommandManager commandManager;

    @Mock
    private CommandExecutor commandExecutor;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private AppContext appContext;

    @Mock
    private MachineChooser machineChooser;

    @Mock
    private CommandUtils commandUtils;

    @Mock
    private PaletteMessages messages;

    @Mock
    private Promise<MachineImpl> machinePromise;

    @Captor
    private ArgumentCaptor<Operation<MachineImpl>> selectedMachineCaptor;

    @Captor
    private ArgumentCaptor<Map<CommandGoal, List<CommandImpl>>> filteredCommandsCaptor;

    @InjectMocks
    private CommandsPalettePresenter presenter;

    @Test
    public void shouldSetViewDelegate() throws Exception {
        Mockito.verify(view).setDelegate(ArgumentMatchers.eq(presenter));
    }

    @Test
    public void shouldShowViewAndSetCommands() throws Exception {
        presenter.showDialog();
        Mockito.verify(view).showDialog();
        Mockito.verify(commandManager).getCommands();
        Mockito.verify(view).setCommands(ArgumentMatchers.any());
    }

    @Test
    public void shouldFilterCommands() throws Exception {
        // given
        CommandImpl cmd1 = Mockito.mock(CommandImpl.class);
        Mockito.when(cmd1.getName()).thenReturn("test");
        CommandImpl cmd2 = Mockito.mock(CommandImpl.class);
        Mockito.when(cmd2.getName()).thenReturn("run");
        List<CommandImpl> commands = new ArrayList<>();
        commands.add(cmd1);
        commands.add(cmd2);
        Mockito.when(commandManager.getCommands()).thenReturn(commands);
        Map<CommandGoal, List<CommandImpl>> filteredCommandsMock = new HashMap<>();
        filteredCommandsMock.put(Mockito.mock(CommandGoal.class), commands);
        Mockito.when(commandUtils.groupCommandsByGoal(commands)).thenReturn(filteredCommandsMock);
        // when
        presenter.onFilterChanged("run");
        // then
        Mockito.verify(commandUtils).groupCommandsByGoal(commands);
        Mockito.verify(view).setCommands(filteredCommandsCaptor.capture());
        final Map<CommandGoal, List<CommandImpl>> filteredCommandsValue = filteredCommandsCaptor.getValue();
        Assert.assertEquals(filteredCommandsMock, filteredCommandsValue);
    }

    @Test
    public void shouldExecuteCommand() throws Exception {
        // given
        WorkspaceImpl workspace = Mockito.mock(WorkspaceImpl.class);
        Mockito.when(appContext.getWorkspace()).thenReturn(workspace);
        RuntimeImpl workspaceRuntime = Mockito.mock(RuntimeImpl.class);
        Mockito.when(workspace.getRuntime()).thenReturn(workspaceRuntime);
        Map<String, MachineImpl> machines = new HashMap<>();
        MachineImpl chosenMachine = Mockito.mock(MachineImpl.class);
        Mockito.when(chosenMachine.getName()).thenReturn("machine_id");
        machines.put("machine_id", chosenMachine);
        Mockito.when(workspaceRuntime.getMachines()).thenReturn(machines);
        Mockito.when(machineChooser.show()).thenReturn(machinePromise);
        CommandImpl commandToExecute = Mockito.mock(CommandImpl.class);
        // when
        presenter.onCommandExecute(commandToExecute);
        // then
        Mockito.verify(view).close();
        Mockito.verify(machineChooser).show();
        Mockito.verify(machinePromise).then(selectedMachineCaptor.capture());
        selectedMachineCaptor.getValue().apply(chosenMachine);
        Mockito.verify(commandExecutor).executeCommand(ArgumentMatchers.eq(commandToExecute), ArgumentMatchers.eq("machine_id"));
    }
}

