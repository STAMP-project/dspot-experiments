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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link AbstractMenuItem}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractMenuItemTest {
    @Mock
    private CommandImpl command;

    private AbstractMenuItem item;

    @Test
    public void testGetCommand() throws Exception {
        Assert.assertEquals(command, item.getCommand());
    }

    private static class DummyMenuItem extends AbstractMenuItem {
        DummyMenuItem(CommandImpl command) {
            super(command);
        }

        @Override
        public String getName() {
            return null;
        }
    }
}

