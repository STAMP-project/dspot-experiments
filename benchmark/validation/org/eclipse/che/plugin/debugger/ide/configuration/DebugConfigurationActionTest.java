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


import com.google.common.base.Optional;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationsManager;
import org.eclipse.che.plugin.debugger.ide.DebuggerLocalizationConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Artem Zatsarynnyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class DebugConfigurationActionTest {
    @Mock
    private DebuggerLocalizationConstant localizationConstant;

    @Mock
    private DebugConfigurationsManager debugConfigurationsManager;

    @Mock
    private DebugConfiguration debugConfiguration;

    @InjectMocks
    private DebugConfigurationAction action;

    @Test
    public void verifyActionConstruction() {
        Mockito.verify(debugConfiguration).getName();
        Mockito.verify(localizationConstant).debugConfigurationActionDescription();
    }

    @Test
    public void shouldBeVisibleOnUpdate() {
        String confName = "test_conf";
        Mockito.when(debugConfiguration.getName()).thenReturn(confName);
        DebugConfiguration configuration = Mockito.mock(DebugConfiguration.class);
        Optional<DebugConfiguration> configurationOptional = Mockito.mock(Optional.class);
        Mockito.when(configurationOptional.isPresent()).thenReturn(Boolean.TRUE);
        Mockito.when(configurationOptional.get()).thenReturn(configuration);
        Mockito.when(debugConfigurationsManager.getCurrentDebugConfiguration()).thenReturn(configurationOptional);
        ActionEvent event = Mockito.mock(ActionEvent.class);
        Presentation presentation = Mockito.mock(Presentation.class);
        Mockito.when(event.getPresentation()).thenReturn(presentation);
        action.updateInPerspective(event);
        Mockito.verify(presentation).setEnabledAndVisible(true);
    }

    @Test
    public void shouldSetCurrentConfigurationAndApplyOnActionPerformed() {
        action.actionPerformed(null);
        Mockito.verify(debugConfigurationsManager).setCurrentDebugConfiguration(ArgumentMatchers.eq(debugConfiguration));
        Mockito.verify(debugConfigurationsManager).apply(ArgumentMatchers.eq(debugConfiguration));
    }
}

