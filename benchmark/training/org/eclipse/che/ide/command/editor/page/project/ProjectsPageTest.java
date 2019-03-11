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
package org.eclipse.che.ide.command.editor.page.project;


import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Map;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandImpl.ApplicableContext;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.api.resources.ResourceDelta;
import org.eclipse.che.ide.command.editor.EditorMessages;
import org.eclipse.che.ide.command.editor.page.CommandEditorPage.DirtyStateListener;
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
 * Tests for {@link ProjectsPage}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class ProjectsPageTest {
    private static final String PROJECT_PATH = "/projects/p1";

    @Mock
    private ProjectsPageView view;

    @Mock
    private AppContext appContext;

    @Mock
    private EditorMessages messages;

    @Mock
    private EventBus eventBus;

    @InjectMocks
    private ProjectsPage page;

    @Mock
    private DirtyStateListener dirtyStateListener;

    @Mock
    private CommandImpl editedCommand;

    @Mock
    private ApplicableContext applicableContext;

    @Mock
    private Project project;

    @Captor
    private ArgumentCaptor<Map<Project, Boolean>> projectsStatesCaptor;

    @Test
    public void shouldSetViewDelegate() throws Exception {
        Mockito.verify(view).setDelegate(page);
    }

    @Test
    public void shouldReturnView() throws Exception {
        Assert.assertEquals(view, page.getView());
    }

    @Test
    public void shouldSetProjects() throws Exception {
        setUpApplicableProjectToContext();
        verifySettingProjects();
    }

    @Test
    public void shouldNotifyListenerWhenApplicableProjectChanged() throws Exception {
        page.onApplicableProjectChanged(Mockito.mock(Project.class), true);
        Mockito.verify(dirtyStateListener, Mockito.times(2)).onDirtyStateChanged();
    }

    @Test
    public void shouldAddApplicableProjectInContext() throws Exception {
        page.onApplicableProjectChanged(project, true);
        Mockito.verify(applicableContext).addProject(ArgumentMatchers.eq(ProjectsPageTest.PROJECT_PATH));
    }

    @Test
    public void shouldRemoveApplicableProjectFromContext() throws Exception {
        page.onApplicableProjectChanged(project, false);
        Mockito.verify(applicableContext).removeProject(ArgumentMatchers.eq(ProjectsPageTest.PROJECT_PATH));
    }

    @Test
    public void shouldUnsetWorkspaceApplicableWhenAnyApplicableProject() throws Exception {
        page.onApplicableProjectChanged(project, true);
        Mockito.verify(applicableContext).setWorkspaceApplicable(ArgumentMatchers.eq(Boolean.FALSE));
    }

    @Test
    public void shouldSetWorkspaceApplicableWhenNoApplicableProject() throws Exception {
        page.onApplicableProjectChanged(project, false);
        Mockito.verify(applicableContext).setWorkspaceApplicable(ArgumentMatchers.eq(Boolean.TRUE));
    }

    @Test
    public void shouldSetProjectsOnResourceChanged() throws Exception {
        setUpApplicableProjectToContext();
        ResourceDelta resourceDelta = Mockito.mock(ResourceDelta.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resourceDelta.getResource()).thenReturn(resource);
        Mockito.when(resource.isProject()).thenReturn(true);
        page.onResourceChanged(new org.eclipse.che.ide.api.resources.ResourceChangedEvent(resourceDelta));
        verifySettingProjects();
    }
}

