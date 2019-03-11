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
package org.eclipse.che.ide.ext.gwt.client.command;


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
public class GwtPagePresenterTest {
    private static final String WORK_DIR = "project";

    private static final String GWT_MODULE = "org.eclipse.CHE";

    private static final String CODE_SERVER_ADDRESS = "127.0.0.1";

    private static final String COMMAND_LINE = (((("mvn clean gwt:run-codeserver -f " + (GwtPagePresenterTest.WORK_DIR)) + " -Dgwt.module=") + (GwtPagePresenterTest.GWT_MODULE)) + " -Dgwt.bindAddress=") + (GwtPagePresenterTest.CODE_SERVER_ADDRESS);

    @Mock
    private GwtCommandPageView view;

    @Mock
    private CommandImpl command;

    @InjectMocks
    private GwtCommandPagePresenter presenter;

    @Test
    public void testResetting() throws Exception {
        Mockito.verify(command).getCommandLine();
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        presenter.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(view));
        Mockito.verify(view).setWorkingDirectory(ArgumentMatchers.eq(GwtPagePresenterTest.WORK_DIR));
        Mockito.verify(view).setGwtModule(ArgumentMatchers.eq(GwtPagePresenterTest.GWT_MODULE));
        Mockito.verify(view).setCodeServerAddress(ArgumentMatchers.eq(GwtPagePresenterTest.CODE_SERVER_ADDRESS));
    }

    @Test
    public void testOnWorkingDirectoryChanged() throws Exception {
        Mockito.when(view.getWorkingDirectory()).thenReturn(GwtPagePresenterTest.WORK_DIR);
        final CommandPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        presenter.setDirtyStateListener(listener);
        presenter.onWorkingDirectoryChanged();
        Mockito.verify(view).getWorkingDirectory();
        Mockito.verify(command).setCommandLine(ArgumentMatchers.eq(GwtPagePresenterTest.COMMAND_LINE));
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnGwtModuleChanged() throws Exception {
        Mockito.when(view.getGwtModule()).thenReturn(GwtPagePresenterTest.GWT_MODULE);
        final CommandPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        presenter.setDirtyStateListener(listener);
        presenter.onGwtModuleChanged();
        Mockito.verify(view).getGwtModule();
        Mockito.verify(command).setCommandLine(ArgumentMatchers.eq(GwtPagePresenterTest.COMMAND_LINE));
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnCodeServerAddressChanged() throws Exception {
        Mockito.when(view.getCodeServerAddress()).thenReturn(GwtPagePresenterTest.CODE_SERVER_ADDRESS);
        final CommandPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        presenter.setDirtyStateListener(listener);
        presenter.onCodeServerAddressChanged();
        Mockito.verify(view).getCodeServerAddress();
        Mockito.verify(command).setCommandLine(ArgumentMatchers.eq(GwtPagePresenterTest.COMMAND_LINE));
        Mockito.verify(listener).onDirtyStateChanged();
    }
}

