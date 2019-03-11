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
package org.eclipse.che.ide.ext.java.client.formatter.preferences;


import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.formatter.JavaFormatterServiceClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(GwtMockitoTestRunner.class)
public class FormatterPreferencePagePresenterTest {
    private static final String FORMATTER_CONTENT = "content";

    @Mock
    private JavaFormatterServiceClient javaFormatterServiceClient;

    @Mock
    private AppContext appContext;

    @Mock
    private JavaLocalizationConstant javaLocalizationConstant;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private FormatterPreferencePageView view;

    @Mock
    private Promise<Void> updatingResponce;

    private FormatterPreferencePagePresenter presenter;

    @Test
    public void presenterShouldBeInitialized() throws Exception {
        Mockito.verify(javaLocalizationConstant).formatterPreferencesJavaTitle();
        Mockito.verify(javaLocalizationConstant).formatterPreferencesGroupTitle();
        Mockito.verify(view).setDelegate(presenter);
    }

    @Test
    public void formatterPageCanNotBeUnsaved() throws Exception {
        Assert.assertFalse(presenter.isDirty());
    }

    @Test
    public void pageSouldBeShown() throws Exception {
        AcceptsOneWidget acceptsOneWidget = Mockito.mock(AcceptsOneWidget.class);
        presenter.go(acceptsOneWidget);
        Mockito.verify(view).showDialog();
        Mockito.verify(acceptsOneWidget).setWidget(view);
    }

    @Test
    public void projectFormatterIsNotImportedIfRootProjectIsNull() throws Exception {
        Mockito.when(appContext.getRootProject()).thenReturn(null);
        Mockito.when(view.isWorkspaceTarget()).thenReturn(false);
        presenter.onImportButtonClicked();
        Mockito.verify(notificationManager).notify(((Notification) (ArgumentMatchers.anyObject())));
        Mockito.verify(javaFormatterServiceClient, Mockito.never()).updateProjectFormatter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(javaFormatterServiceClient, Mockito.never()).updateRootFormatter(ArgumentMatchers.anyString());
    }

    @Test
    public void projectFormatterIsImported() throws Exception {
        String projectPath = "/path";
        Project rootProject = Mockito.mock(Project.class);
        Mockito.when(appContext.getRootProject()).thenReturn(rootProject);
        Mockito.when(view.isWorkspaceTarget()).thenReturn(false);
        Mockito.when(rootProject.getPath()).thenReturn(projectPath);
        presenter.onImportButtonClicked();
        Mockito.verify(notificationManager).notify(((Notification) (ArgumentMatchers.anyObject())));
        Mockito.verify(view).getFileContent();
        Mockito.verify(javaFormatterServiceClient).updateProjectFormatter(FormatterPreferencePagePresenterTest.FORMATTER_CONTENT, projectPath);
        Mockito.verify(javaFormatterServiceClient, Mockito.never()).updateRootFormatter(ArgumentMatchers.anyString());
    }

    @Test
    public void workspaceFormatterIsImported() throws Exception {
        Mockito.when(view.isWorkspaceTarget()).thenReturn(true);
        presenter.onImportButtonClicked();
        Mockito.verify(notificationManager).notify(((Notification) (ArgumentMatchers.anyObject())));
        Mockito.verify(view).getFileContent();
        Mockito.verify(javaFormatterServiceClient).updateRootFormatter(FormatterPreferencePagePresenterTest.FORMATTER_CONTENT);
        Mockito.verify(javaFormatterServiceClient, Mockito.never()).updateProjectFormatter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void showErrorMessage() throws Exception {
        String errorMessage = "error";
        presenter.showErrorMessage(errorMessage);
        Mockito.verify(notificationManager).notify(errorMessage);
    }
}

