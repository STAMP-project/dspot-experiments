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
package org.eclipse.che.ide.actions;


import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.core.AgentURLModifier;
import org.eclipse.che.ide.download.DownloadContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class DownloadWsActionTest {
    @Mock
    private AppContext appContext;

    @Mock
    private AgentURLModifier agentURLModifier;

    @Mock
    private DownloadContainer downloadContainer;

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private Resources resources;

    @Mock
    private ActionEvent actionEvent;

    @Mock
    private Presentation presentation;

    @InjectMocks
    private DownloadWsAction action;

    @Test
    public void actionShouldBeInitialized() throws Exception {
        Mockito.verify(locale).downloadProjectAsZipName();
        Mockito.verify(locale).downloadProjectAsZipDescription();
        Mockito.verify(resources).downloadZip();
    }

    @Test
    public void actionShouldBePerformed() throws Exception {
        String baseUrl = "baseUrl";
        Mockito.when(appContext.getWsAgentServerApiEndpoint()).thenReturn(baseUrl);
        Mockito.when(agentURLModifier.modify(ArgumentMatchers.anyString())).thenReturn(baseUrl);
        action.actionPerformed(actionEvent);
        Mockito.verify(downloadContainer).setUrl(ArgumentMatchers.eq(baseUrl));
    }

    @Test
    public void actionShouldBeEnable() throws Exception {
        Project project = Mockito.mock(Project.class);
        Mockito.when(actionEvent.getPresentation()).thenReturn(presentation);
        Project[] projects = new Project[1];
        projects[0] = project;
        Mockito.when(appContext.getProjects()).thenReturn(projects);
        action.updateInPerspective(actionEvent);
        Mockito.verify(presentation).setVisible(true);
        Mockito.verify(presentation).setEnabled(true);
    }

    @Test
    public void actionShouldNotBeEnabledIfWsIsEmpty() throws Exception {
        Mockito.when(actionEvent.getPresentation()).thenReturn(presentation);
        Project[] projects = new Project[0];
        Mockito.when(appContext.getProjects()).thenReturn(projects);
        action.updateInPerspective(actionEvent);
        Mockito.verify(presentation).setVisible(true);
        Mockito.verify(presentation).setEnabled(false);
    }

    @Test
    public void actionShouldNotBeEnabledIfListOfProjectsIsNull() throws Exception {
        Mockito.when(actionEvent.getPresentation()).thenReturn(presentation);
        Mockito.when(appContext.getProjects()).thenReturn(null);
        action.updateInPerspective(actionEvent);
        Mockito.verify(presentation).setVisible(true);
        Mockito.verify(presentation).setEnabled(false);
    }
}

