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
package org.eclipse.che.plugin.maven.client.command;


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
public class MavenPagePresenterTest {
    private static final String WORK_DIR = "project";

    private static final String ARGUMENTS = "clean install";

    private static final String COMMAND_LINE = (("mvn -f " + (MavenPagePresenterTest.WORK_DIR)) + ' ') + (MavenPagePresenterTest.ARGUMENTS);

    @Mock
    private MavenCommandPageView view;

    @Mock
    private CommandImpl command;

    @InjectMocks
    private MavenCommandPagePresenter presenter;

    @Test
    public void testResetting() throws Exception {
        Mockito.verify(command).getCommandLine();
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        presenter.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(view));
        Mockito.verify(view).setWorkingDirectory(ArgumentMatchers.eq(MavenPagePresenterTest.WORK_DIR));
        Mockito.verify(view).setArguments(ArgumentMatchers.eq(MavenPagePresenterTest.ARGUMENTS));
    }

    @Test
    public void testOnWorkingDirectoryChanged() throws Exception {
        Mockito.when(view.getWorkingDirectory()).thenReturn(MavenPagePresenterTest.WORK_DIR);
        final CommandPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        presenter.setDirtyStateListener(listener);
        presenter.onWorkingDirectoryChanged();
        Mockito.verify(view).getWorkingDirectory();
        Mockito.verify(command).setCommandLine(ArgumentMatchers.eq(MavenPagePresenterTest.COMMAND_LINE));
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnArgumentsChanged() throws Exception {
        Mockito.when(view.getArguments()).thenReturn(MavenPagePresenterTest.ARGUMENTS);
        final CommandPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        presenter.setDirtyStateListener(listener);
        presenter.onArgumentsChanged();
        Mockito.verify(view).getArguments();
        Mockito.verify(command).setCommandLine(ArgumentMatchers.eq(MavenPagePresenterTest.COMMAND_LINE));
        Mockito.verify(listener).onDirtyStateChanged();
    }
}

