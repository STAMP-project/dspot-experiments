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
import java.util.HashMap;
import java.util.Map;
import org.eclipse.che.ide.api.resources.Project;
import org.eclipse.che.ide.command.editor.page.project.ProjectsPageView.ActionDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link ProjectsPageViewImpl}.
 */
@RunWith(GwtMockitoTestRunner.class)
public class ProjectsPageViewImplTest {
    @Mock
    private ActionDelegate actionDelegate;

    @InjectMocks
    private ProjectsPageViewImpl view;

    @Test
    public void shouldSetProjects() throws Exception {
        // given
        Project p1 = Mockito.mock(Project.class);
        Project p2 = Mockito.mock(Project.class);
        Map<Project, Boolean> projects = new HashMap<>();
        projects.put(p1, true);
        projects.put(p2, true);
        // when
        view.setProjects(projects);
        // then
        Mockito.verify(view.projectsPanel).clear();
        Mockito.verify(view.mainPanel).setVisible(ArgumentMatchers.eq(Boolean.TRUE));
        Mockito.verify(view.projectsPanel, Mockito.times(projects.size())).add(ArgumentMatchers.any(ProjectSwitcher.class));
    }

    @Test
    public void shouldHidePanelWhenNoProject() throws Exception {
        view.setProjects(new HashMap());
        Mockito.verify(view.projectsPanel).clear();
        Mockito.verify(view.mainPanel, Mockito.times(2)).setVisible(ArgumentMatchers.eq(Boolean.FALSE));
        Mockito.verify(view.projectsPanel, Mockito.never()).add(ArgumentMatchers.any(ProjectSwitcher.class));
    }
}

