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


import org.eclipse.che.ide.api.command.CommandExecutor;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.command.editor.EditorMessages;
import org.eclipse.che.ide.command.editor.page.CommandEditorPage.DirtyStateListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link NamePage}.
 */
@RunWith(MockitoJUnitRunner.class)
public class NamePageTest {
    private static final String COMMAND_NAME = "build";

    @Mock
    private NamePageView view;

    @Mock
    private EditorMessages messages;

    @Mock
    private CommandExecutor commandExecutor;

    @InjectMocks
    private NamePage page;

    @Mock
    private DirtyStateListener dirtyStateListener;

    @Mock
    private CommandImpl editedCommand;

    @Test
    public void shouldSetViewDelegate() throws Exception {
        Mockito.verify(view).setDelegate(page);
    }

    @Test
    public void shouldInitializeView() throws Exception {
        Mockito.verify(view).setCommandName(ArgumentMatchers.eq(NamePageTest.COMMAND_NAME));
    }

    @Test
    public void shouldReturnView() throws Exception {
        Assert.assertEquals(view, page.getView());
    }

    @Test
    public void shouldNotifyListenerWhenNameChanged() throws Exception {
        page.onNameChanged("mvn");
        Mockito.verify(dirtyStateListener, Mockito.times(2)).onDirtyStateChanged();
    }

    @Test
    public void shouldExecuteCommandWhenTestingRequested() throws Exception {
        page.onCommandRun();
        Mockito.verify(commandExecutor).executeCommand(editedCommand);
    }

    @Test
    public void shouldShowWarningWhenInvalidName() throws Exception {
        page.onNameChanged("mvn*");
        Mockito.verify(view).showWarning(true);
    }

    @Test
    public void shouldHideWarningWhenValidName() throws Exception {
        page.onNameChanged("mvn");
        Mockito.verify(view).showWarning(false);
    }
}

