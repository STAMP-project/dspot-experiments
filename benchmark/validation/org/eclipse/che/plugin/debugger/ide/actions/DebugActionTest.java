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
package org.eclipse.che.plugin.debugger.ide.actions;


import com.google.common.base.Optional;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationsManager;
import org.eclipse.che.ide.debug.DebuggerManager;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.plugin.debugger.ide.DebuggerLocalizationConstant;
import org.eclipse.che.plugin.debugger.ide.DebuggerResources;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class DebugActionTest {
    @Mock
    private DebugConfigurationsManager debugConfigurationsManager;

    @Mock
    private DebuggerLocalizationConstant localizationConstant;

    @Mock
    private DebuggerResources javaRuntimeResources;

    @Mock
    private DebuggerManager debuggerManager;

    @Mock
    private DialogFactory dialogFactory;

    @InjectMocks
    private DebugAction action;

    @Test
    public void verifyActionConstruction() {
        Mockito.verify(localizationConstant).debugActionTitle();
        Mockito.verify(localizationConstant).debugActionDescription();
        Mockito.verify(javaRuntimeResources).debug();
    }

    @Test
    public void actionShouldBePerformed() {
        DebugConfiguration debugConfiguration = Mockito.mock(DebugConfiguration.class);
        Map<String, String> connectionProperties = new HashMap<>();
        connectionProperties.put("prop1", "val1");
        connectionProperties.put("prop2", "val2");
        Optional configurationOptional = Mockito.mock(Optional.class);
        Mockito.when(configurationOptional.isPresent()).thenReturn(Boolean.TRUE);
        Mockito.when(configurationOptional.get()).thenReturn(debugConfiguration);
        Mockito.when(debugConfigurationsManager.getCurrentDebugConfiguration()).thenReturn(configurationOptional);
        action.actionPerformed(null);
        Mockito.verify(debugConfigurationsManager).apply(debugConfiguration);
    }
}

