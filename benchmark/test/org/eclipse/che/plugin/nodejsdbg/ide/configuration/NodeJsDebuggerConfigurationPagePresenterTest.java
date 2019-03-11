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
package org.eclipse.che.plugin.nodejsdbg.ide.configuration;


import DebugConfigurationPage.DirtyStateListener;
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
public class NodeJsDebuggerConfigurationPagePresenterTest {
    private static final String HOST = "localhost";

    private static final int PORT = 8000;

    @Mock
    private NodeJsDebuggerConfigurationPageView pageView;

    @Mock
    private DebugConfiguration configuration;

    @Mock
    private CurrentProjectPathMacro currentProjectPathMacro;

    @Mock
    private AppContext appContext;

    @InjectMocks
    private NodeJsDebuggerConfigurationPagePresenter pagePresenter;

    @Test
    public void testResetting() throws Exception {
        Mockito.verify(configuration, Mockito.atLeastOnce()).getConnectionProperties();
        Mockito.verify(currentProjectPathMacro).getName();
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        pagePresenter.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(pageView));
        Mockito.verify(configuration, Mockito.atLeastOnce()).getConnectionProperties();
        Mockito.verify(pageView).setScriptPath(ArgumentMatchers.nullable(String.class));
    }

    @Test
    public void testOnBinaryPathChanged() throws Exception {
        String binPath = "/path";
        Mockito.when(pageView.getScriptPath()).thenReturn(binPath);
        final DebugConfigurationPage.DirtyStateListener listener = Mockito.mock(DirtyStateListener.class);
        pagePresenter.setDirtyStateListener(listener);
        pagePresenter.onScriptPathChanged();
        Mockito.verify(pageView).getScriptPath();
        ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(configuration).setConnectionProperties(argumentCaptor.capture());
        Map argumentCaptorValue = argumentCaptor.getValue();
        Assert.assertEquals(binPath, argumentCaptorValue.get(SCRIPT.toString()));
        Mockito.verify(listener).onDirtyStateChanged();
    }
}

