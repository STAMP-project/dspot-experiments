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


import ZendDbgConfigurationType.DISPLAY_NAME;
import ZendDebugger.ID;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.api.debug.DebugConfiguration;
import org.eclipse.che.ide.api.debug.DebugConfigurationPage;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.plugin.zdb.ide.ZendDbgResources;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


/**
 * Zend dbg configuration type tests.
 *
 * @author Bartlomiej Laczkowski
 */
@RunWith(GwtMockitoTestRunner.class)
public class ZendDbgConfigurationTypeTest {
    @Mock
    private ZendDbgResources zendDbgResources;

    @Mock
    private ZendDbgConfigurationPagePresenter zendDbgConfigurationPagePresenter;

    @Mock
    private IconRegistry iconRegistry;

    @InjectMocks
    private ZendDbgConfigurationType zendDbgConfigurationType;

    @Test
    public void testGetId() throws Exception {
        final String id = zendDbgConfigurationType.getId();
        Assert.assertEquals(ID, id);
    }

    @Test
    public void testGetDisplayName() throws Exception {
        final String displayName = zendDbgConfigurationType.getDisplayName();
        Assert.assertEquals(DISPLAY_NAME, displayName);
    }

    @Test
    public void testGetConfigurationPage() throws Exception {
        final DebugConfigurationPage<? extends DebugConfiguration> page = zendDbgConfigurationType.getConfigurationPage();
        Assert.assertEquals(zendDbgConfigurationPagePresenter, page);
    }
}

