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
package org.eclipse.che.ide.actions;


import java.util.Collections;
import java.util.Optional;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.command.CommandExecutor;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.workspace.WsAgentServerUtil;
import org.eclipse.che.ide.api.workspace.model.MachineImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Max Shaposhnik
 */
@RunWith(MockitoJUnitRunner.class)
public class RunCommandActionTest {
    private static final String NAME_PROPERTY = "name";

    // constructors mocks
    @Mock
    private CommandManager commandManager;

    @Mock
    private CommandExecutor commandExecutor;

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private ActionEvent event;

    @Mock
    private CommandImpl command;

    @Mock
    private WsAgentServerUtil wsAgentServerUtil;

    @InjectMocks
    private RunCommandAction action;

    @Test
    public void commandNameShouldBePresent() {
        Mockito.when(event.getParameters()).thenReturn(Collections.singletonMap("otherParam", "MCI"));
        action.actionPerformed(event);
        Mockito.verify(commandExecutor, Mockito.never()).executeCommand(ArgumentMatchers.any(CommandImpl.class), ArgumentMatchers.nullable(String.class));
    }

    @Test
    public void actionShouldBePerformed() {
        Mockito.when(event.getParameters()).thenReturn(Collections.singletonMap(RunCommandActionTest.NAME_PROPERTY, "MCI"));
        MachineImpl machine = Mockito.mock(MachineImpl.class);
        Mockito.when(wsAgentServerUtil.getWsAgentServerMachine()).thenReturn(Optional.of(machine));
        action.actionPerformed(event);
        Mockito.verify(commandExecutor).executeCommand(ArgumentMatchers.eq(command), ArgumentMatchers.nullable(String.class));
    }
}

