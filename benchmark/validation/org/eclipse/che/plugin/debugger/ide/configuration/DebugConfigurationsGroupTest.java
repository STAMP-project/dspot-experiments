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


import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationsManager;
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
public class DebugConfigurationsGroupTest {
    @Mock
    private ActionManager actionManager;

    @Mock
    private DebugConfigurationsManager debugConfigurationsManager;

    @Mock
    private DebugConfigurationActionFactory debugConfigurationActionFactory;

    @Mock
    private DebugConfiguration debugConfiguration;

    @InjectMocks
    private DebugConfigurationsGroup actionGroup;

    @Test
    public void verifyActionConstruction() {
        Mockito.verify(debugConfigurationsManager).addConfigurationsChangedListener(actionGroup);
        Mockito.verify(debugConfigurationsManager).getConfigurations();
    }

    @Test
    public void shouldFillActionsOnConfigurationAdded() {
        actionGroup.onConfigurationAdded(Mockito.mock(DebugConfiguration.class));
        verifyChildActionsFilled();
    }

    @Test
    public void shouldFillActionsOnConfigurationRemoved() {
        actionGroup.onConfigurationRemoved(Mockito.mock(DebugConfiguration.class));
        verifyChildActionsFilled();
    }
}

