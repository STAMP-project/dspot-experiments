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
package org.eclipse.che.ide.command.type.custom;


import CommandPage.DirtyStateListener;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandPage;
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
 * @author Artem Zatsarynnyi
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomPagePresenterTest {
    private static final String COMMAND_LINE = "cmd";

    @Mock
    private CustomPageView arbitraryPageView;

    @Mock
    private CommandImpl command;

    @InjectMocks
    private CustomPagePresenter arbitraryPagePresenter;

    @Test
    public void testResetting() throws Exception {
        Mockito.verify(command).getCommandLine();
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        arbitraryPagePresenter.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(arbitraryPageView));
        Mockito.verify(command, Mockito.times(2)).getCommandLine();
        Mockito.verify(arbitraryPageView).setCommandLine(ArgumentMatchers.eq(CustomPagePresenterTest.COMMAND_LINE));
    }

    @Test
    public void testOnCommandLineChanged() throws Exception {
        String commandLine = "commandLine";
        Mockito.when(arbitraryPageView.getCommandLine()).thenReturn(commandLine);
        final CommandPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        arbitraryPagePresenter.setDirtyStateListener(listener);
        arbitraryPagePresenter.onCommandLineChanged();
        Mockito.verify(arbitraryPageView).getCommandLine();
        Mockito.verify(command).setCommandLine(ArgumentMatchers.eq(commandLine));
        Mockito.verify(listener).onDirtyStateChanged();
    }
}

