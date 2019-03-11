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
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.MutableProjectConfig;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.projecttype.wizard.presenter.ProjectWizardPresenter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class ConvertFolderToProjectActionTest {
    private static final String TEXT = "to be or not to be";

    @Mock
    private CoreLocalizationConstant locale;

    @Mock
    private AppContext appContext;

    @Mock
    private ProjectWizardPresenter projectConfigWizard;

    @InjectMocks
    private ConvertFolderToProjectAction action;

    @Mock
    private Resource resource;

    @Mock
    private ActionEvent event;

    @Mock
    private Presentation presentation;

    @Captor
    private ArgumentCaptor<MutableProjectConfig> projectConfigCapture;

    @Test
    public void actionShouldBeInitialized() throws Exception {
        Mockito.verify(locale).actionConvertFolderToProject();
        Mockito.verify(locale).actionConvertFolderToProjectDescription();
    }

    @Test
    public void actionShouldBeVisibleIfFolderWasSelected() throws Exception {
        action.updateInPerspective(event);
        Mockito.verify(presentation).setEnabledAndVisible(true);
    }

    @Test
    public void actionShouldBeHiddenIfSelectedElementIsNull() throws Exception {
        Mockito.when(appContext.getResource()).thenReturn(null);
        action.updateInPerspective(event);
        Mockito.verify(presentation).setEnabledAndVisible(false);
    }

    @Test
    public void actionShouldBeHiddenIfSelectedElementIsNotFolder() throws Exception {
        Mockito.when(resource.isFolder()).thenReturn(false);
        action.updateInPerspective(event);
        Mockito.verify(presentation).setEnabledAndVisible(false);
    }

    @Test
    public void configurationWizardShouldNotBeShowedIfSelectedElementIsNotFolder() throws Exception {
        Mockito.when(resource.isFolder()).thenReturn(false);
        action.actionPerformed(event);
        Mockito.verify(projectConfigWizard, Mockito.never()).show(((MutableProjectConfig) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void configurationWizardShouldNotBeShowedIfSelectedElementIsNull() throws Exception {
        Mockito.when(appContext.getResource()).thenReturn(null);
        action.actionPerformed(event);
        Mockito.verify(projectConfigWizard, Mockito.never()).show(((MutableProjectConfig) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void configurationWizardShouldNotBeShowedIfPathOfFolderIsNull() throws Exception {
        Mockito.when(resource.getLocation()).thenReturn(null);
        action.actionPerformed(event);
        Mockito.verify(projectConfigWizard, Mockito.never()).show(((MutableProjectConfig) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void configurationWizardShouldBeShowed() throws Exception {
        Mockito.when(resource.getName()).thenReturn(ConvertFolderToProjectActionTest.TEXT);
        action.actionPerformed(event);
        Mockito.verify(resource).getName();
        Mockito.verify(projectConfigWizard).show(projectConfigCapture.capture());
        MutableProjectConfig projectConfig = projectConfigCapture.getValue();
        Assert.assertEquals(ConvertFolderToProjectActionTest.TEXT, projectConfig.getName());
        Assert.assertEquals(ConvertFolderToProjectActionTest.TEXT, projectConfig.getPath());
    }
}

