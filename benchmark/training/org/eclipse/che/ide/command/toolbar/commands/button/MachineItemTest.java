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
package org.eclipse.che.ide.command.toolbar.commands.button;


import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.workspace.model.MachineImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link MachineItem}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MachineItemTest {
    private static final String MACHINE_NAME = "dev-machine";

    @Mock
    private CommandImpl command;

    @Mock
    private MachineImpl machine;

    private MachineItem item;

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals(MachineItemTest.MACHINE_NAME, item.getName());
    }

    @Test
    public void testGetMachine() throws Exception {
        Assert.assertEquals(machine, item.getMachine());
    }
}

