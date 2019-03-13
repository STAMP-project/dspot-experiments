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
package org.eclipse.che.plugin.gdb.ide.configuration;


import DebugConfigurationPage.DirtyStateListener;
import GdbConfigurationPagePresenter.BIN_PATH_CONNECTION_PROPERTY;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.Map;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationPage;
import org.eclipse.che.ide.macro.CurrentProjectPathMacro;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class GdbConfigurationPagePresenterTest {
    private static final String HOST = "localhost";

    private static final int PORT = 8000;

    @Mock
    private GdbConfigurationPageView pageView;

    @Mock
    private DebugConfiguration configuration;

    @Mock
    private CurrentProjectPathMacro currentProjectPathMacro;

    @Mock
    private AppContext appContext;

    @InjectMocks
    private GdbConfigurationPagePresenter pagePresenter;

    @Test
    public void testResetting() throws Exception {
        Mockito.verify(configuration, Mockito.atLeastOnce()).getHost();
        Mockito.verify(configuration, Mockito.atLeastOnce()).getPort();
        Mockito.verify(configuration, Mockito.atLeastOnce()).getConnectionProperties();
        Mockito.verify(currentProjectPathMacro).getName();
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        pagePresenter.go(container);
        Mockito.verify(appContext).getWorkspace();
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(pageView));
        Mockito.verify(configuration, Mockito.atLeastOnce()).getHost();
        Mockito.verify(configuration, Mockito.atLeastOnce()).getPort();
        Mockito.verify(configuration, Mockito.atLeastOnce()).getConnectionProperties();
        Mockito.verify(pageView).setHost(ArgumentMatchers.eq(GdbConfigurationPagePresenterTest.HOST));
        Mockito.verify(pageView).setPort(ArgumentMatchers.eq(GdbConfigurationPagePresenterTest.PORT));
        Mockito.verify(pageView).setBinaryPath(ArgumentMatchers.nullable(String.class));
        Mockito.verify(pageView).setDevHost(ArgumentMatchers.eq(false));
        Mockito.verify(pageView).setPortEnableState(ArgumentMatchers.eq(true));
        Mockito.verify(pageView).setHostEnableState(ArgumentMatchers.eq(true));
    }

    @Test
    public void testOnHostChanged() throws Exception {
        String host = "localhost";
        Mockito.when(pageView.getHost()).thenReturn(host);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onHostChanged();
        Mockito.verify(pageView).getHost();
        Mockito.verify(configuration).setHost(ArgumentMatchers.eq(host));
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnPortChanged() throws Exception {
        int port = 8000;
        Mockito.when(pageView.getPort()).thenReturn(port);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onPortChanged();
        Mockito.verify(pageView).getPort();
        Mockito.verify(configuration).setPort(ArgumentMatchers.eq(port));
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnBinaryPathChanged() throws Exception {
        String binPath = "/path";
        Mockito.when(pageView.getBinaryPath()).thenReturn(binPath);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onBinaryPathChanged();
        Mockito.verify(pageView).getBinaryPath();
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(configuration).setConnectionProperties(argumentCaptor.capture());
        Map argumentCaptorValue = argumentCaptor.getValue();
        Assert.assertEquals(binPath, argumentCaptorValue.get(BIN_PATH_CONNECTION_PROPERTY));
        Mockito.verify(listener).onDirtyStateChanged();
    }
}

