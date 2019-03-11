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
package org.eclipse.che.ide.command.editor.page.name;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.command.editor.page.name.NamePageView.ActionDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link NamePageViewImpl}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class NamePageViewImplTest {
    @Mock
    private ActionDelegate actionDelegate;

    @InjectMocks
    private NamePageViewImpl view;

    @Test
    public void shouldSetCommandName() throws Exception {
        String newName = "cmd 1";
        view.setCommandName(newName);
        Mockito.verify(view.commandName).setValue(ArgumentMatchers.eq(newName));
    }

    @Test
    public void shouldCallOnNameChanged() throws Exception {
        String commandName = "cmd name";
        Mockito.when(view.commandName.getValue()).thenReturn(commandName);
        view.onNameChanged(null);
        Mockito.verify(actionDelegate).onNameChanged(ArgumentMatchers.eq(commandName));
    }

    @Test
    public void shouldCallOnCommandRun() throws Exception {
        view.handleRunButton(null);
        Mockito.verify(actionDelegate).onCommandRun();
    }

    @Test
    public void shouldShowWarningLabel() throws Exception {
        view.showWarning(true);
        Mockito.verify(view.warningLabel).setVisible(true);
    }

    @Test
    public void shouldHideWarningLabel() throws Exception {
        view.showWarning(false);
        Mockito.verify(view.warningLabel).setVisible(false);
    }
}

