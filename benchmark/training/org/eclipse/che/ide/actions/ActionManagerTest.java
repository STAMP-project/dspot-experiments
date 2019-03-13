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


import IdeActions.GROUP_MAIN_MENU;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.DefaultActionGroup;
import org.eclipse.che.ide.api.keybinding.KeyBindingAgent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Evgen Vidolob
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionManagerTest {
    @Mock
    private KeyBindingAgent agent;

    private ActionManager actionManager;

    @Test
    public void shouldUnregister() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction(GROUP_MAIN_MENU, defaultActionGroup);
        actionManager.unregisterAction(GROUP_MAIN_MENU);
        Action action = actionManager.getAction(GROUP_MAIN_MENU);
        Assert.assertNull(action);
    }

    @Test
    public void testIsGroup() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction(GROUP_MAIN_MENU, defaultActionGroup);
        boolean isGroup = actionManager.isGroup(GROUP_MAIN_MENU);
        Assert.assertTrue(isGroup);
    }
}

