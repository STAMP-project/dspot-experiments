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
package org.eclipse.che.ide.macro;


import WorkspaceNameMacro.KEY;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for the {@link WorkspaceNameMacro}.
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkspaceNameMacroTest {
    public static final String WS_NAME = "workspace";

    @Mock
    AppContext appContext;

    @Mock
    PromiseProvider promiseProvider;

    @Mock
    CoreLocalizationConstant localizationConstants;

    private WorkspaceNameMacro provider;

    @Test
    public void getKey() throws Exception {
        Assert.assertSame(provider.getName(), KEY);
    }

    @Test
    public void getValue() throws Exception {
        provider.expand();
        Mockito.verify(promiseProvider).resolve(ArgumentMatchers.eq(WorkspaceNameMacroTest.WS_NAME));
    }
}

