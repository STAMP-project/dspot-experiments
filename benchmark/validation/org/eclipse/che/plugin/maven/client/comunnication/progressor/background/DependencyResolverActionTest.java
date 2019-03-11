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
package org.eclipse.che.plugin.maven.client.comunnication.progressor.background;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.plugin.maven.client.MavenLocalizationConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class DependencyResolverActionTest {
    @Mock
    private BackgroundLoaderPresenter dependencyResolver;

    @Mock
    private MavenLocalizationConstant locale;

    @Mock
    private Presentation presentation;

    private DependencyResolverAction action;

    @Test
    public void constructorShouldBePerformed() throws Exception {
        Mockito.verify(locale).loaderActionName();
        Mockito.verify(locale).loaderActionDescription();
        Mockito.verify(dependencyResolver).hide();
    }

    @Test
    public void customComponentShouldBeCreated() throws Exception {
        action.createCustomComponent(presentation);
        Mockito.verify(dependencyResolver).getCustomComponent();
    }
}

