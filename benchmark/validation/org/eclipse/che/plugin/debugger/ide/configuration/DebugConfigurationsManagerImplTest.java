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
package org.eclipse.che.plugin.debugger.ide.configuration;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationType;
import org.eclipse.che.ide.debug.Debugger;
import org.eclipse.che.ide.debug.DebuggerManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.macro.CurrentProjectPathMacro;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.eclipse.che.ide.util.storage.LocalStorageProvider;
import org.eclipse.che.plugin.debugger.ide.DebuggerLocalizationConstant;
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
 * @author Anatoliy Bazko
 */
@RunWith(MockitoJUnitRunner.class)
public class DebugConfigurationsManagerImplTest extends TestCase {
    @Mock
    private LocalStorageProvider localStorageProvider;

    @Mock
    private DtoFactory dtoFactory;

    @Mock
    private DebuggerManager debuggerManager;

    @Mock
    private DebugConfigurationTypeRegistry debugConfigurationTypeRegistry;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private DebuggerLocalizationConstant localizationConstants;

    @Mock
    private DebugConfiguration debugConfiguration;

    @Mock
    private DebugConfigurationType debugConfigurationType;

    @Mock
    private CurrentProjectPathMacro currentProjectPathMacro;

    @Mock
    private Debugger debugger;

    @InjectMocks
    private DebugConfigurationsManagerImpl debugConfigurationsManager;

    @Test
    public void testShouldNotApplyConfigurationIfActiveDebuggerExists() throws Exception {
        Mockito.when(debuggerManager.getActiveDebugger()).thenReturn(Mockito.mock(Debugger.class));
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ((ConfirmCallback) (ArgumentMatchers.isNull())))).thenReturn(Mockito.mock(MessageDialog.class));
        debugConfigurationsManager.apply(debugConfiguration);
        Mockito.verify(debuggerManager, Mockito.never()).setActiveDebugger(ArgumentMatchers.any(Debugger.class));
    }

    @Test
    public void testShouldApplyNewConfiguration() throws Exception {
        final String debugId = "debug";
        final String host = "localhost";
        final int port = 9000;
        Mockito.when(debugConfiguration.getConnectionProperties()).thenReturn(ImmutableMap.of("prop", "value"));
        Mockito.when(debugConfigurationType.getId()).thenReturn(debugId);
        Mockito.when(debugConfiguration.getHost()).thenReturn(host);
        Mockito.when(debugConfiguration.getPort()).thenReturn(port);
        Mockito.when(debugConfiguration.getType()).thenReturn(debugConfigurationType);
        Mockito.when(debuggerManager.getDebugger(debugId)).thenReturn(debugger);
        Mockito.when(debugger.connect(ArgumentMatchers.anyMap())).thenReturn(Mockito.mock(Promise.class));
        Mockito.when(currentProjectPathMacro.expand()).thenReturn(Mockito.mock(Promise.class));
        Mockito.when(currentProjectPathMacro.getName()).thenReturn("key");
        debugConfigurationsManager.apply(debugConfiguration);
        ArgumentCaptor<Operation> operationArgumentCaptor = ArgumentCaptor.forClass(Operation.class);
        Mockito.verify(currentProjectPathMacro.expand()).then(operationArgumentCaptor.capture());
        operationArgumentCaptor.getValue().apply("project path");
        Mockito.verify(debuggerManager).setActiveDebugger(debugger);
        ArgumentCaptor<Map> properties = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(debugger).connect(properties.capture());
        Map<String, String> m = properties.getValue();
        TestCase.assertEquals(m.get("HOST"), host);
        TestCase.assertEquals(m.get("PORT"), String.valueOf(port));
        TestCase.assertEquals(m.get("prop"), "value");
    }
}

