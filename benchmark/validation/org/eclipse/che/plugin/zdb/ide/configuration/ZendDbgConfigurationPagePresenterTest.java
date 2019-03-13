/**
 * Copyright (c) 2016 Rogue Wave Software, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Rogue Wave Software, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.zdb.ide.configuration;


import DebugConfigurationPage.DirtyStateListener;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Zend dbg configuration page presenter tests.
 *
 * @author Bartlomiej Laczkowski
 */
@RunWith(MockitoJUnitRunner.class)
public class ZendDbgConfigurationPagePresenterTest {
    private static final Map<String, String> CONNECTION_PROPERTIES = new HashMap<>();

    static {
        ZendDbgConfigurationPagePresenterTest.CONNECTION_PROPERTIES.put(ZendDbgConfigurationType.ATTR_CLIENT_HOST_IP, ZendDbgConfigurationType.DEFAULT_CLIENT_HOST_IP);
        ZendDbgConfigurationPagePresenterTest.CONNECTION_PROPERTIES.put(ZendDbgConfigurationType.ATTR_DEBUG_PORT, ZendDbgConfigurationType.DEFAULT_DEBUG_PORT);
        ZendDbgConfigurationPagePresenterTest.CONNECTION_PROPERTIES.put(ZendDbgConfigurationType.ATTR_BREAK_AT_FIRST_LINE, ZendDbgConfigurationType.DEFAULT_BREAK_AT_FIRST_LINE);
        ZendDbgConfigurationPagePresenterTest.CONNECTION_PROPERTIES.put(ZendDbgConfigurationType.ATTR_USE_SSL_ENCRYPTION, ZendDbgConfigurationType.DEFAULT_USE_SSL_ENCRYPTION);
    }

    @Mock
    private ZendDbgConfigurationPageView pageView;

    @Mock
    private AppContext appContext;

    @Mock
    private DebugConfiguration configuration;

    @InjectMocks
    private ZendDbgConfigurationPagePresenter pagePresenter;

    @Test
    public void testResetting() throws Exception {
        Mockito.verify(configuration, Mockito.times(2)).getConnectionProperties();
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        pagePresenter.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(pageView));
        Mockito.verify(configuration, Mockito.times(3)).getConnectionProperties();
    }

    @Test
    public void testOnClientHostIPChanged() throws Exception {
        String clientHostIP = "127.0.0.1";
        Mockito.when(pageView.getClientHostIP()).thenReturn(clientHostIP);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onClientHostIPChanged();
        Mockito.verify(pageView).getClientHostIP();
        Mockito.verify(configuration, Mockito.times(3)).getConnectionProperties();
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnDebugPortChanged() throws Exception {
        int debugPort = 10000;
        Mockito.when(pageView.getDebugPort()).thenReturn(debugPort);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onDebugPortChanged();
        Mockito.verify(pageView).getDebugPort();
        Mockito.verify(configuration, Mockito.times(3)).getConnectionProperties();
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnBreakAtFirstLineChanged() throws Exception {
        boolean breakAtFirstLine = false;
        Mockito.when(pageView.getBreakAtFirstLine()).thenReturn(breakAtFirstLine);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onBreakAtFirstLineChanged(breakAtFirstLine);
        Mockito.verify(pageView).getBreakAtFirstLine();
        Mockito.verify(configuration, Mockito.times(3)).getConnectionProperties();
        Mockito.verify(listener).onDirtyStateChanged();
    }

    @Test
    public void testOnUseSslEncryptionChanged() throws Exception {
        boolean useSslEncryption = false;
        Mockito.when(pageView.getUseSslEncryption()).thenReturn(useSslEncryption);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onUseSslEncryptionChanged(useSslEncryption);
        Mockito.verify(pageView).getUseSslEncryption();
        Mockito.verify(configuration, Mockito.times(3)).getConnectionProperties();
        Mockito.verify(listener).onDirtyStateChanged();
    }
}

