/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce.task.projectanalysis.issue;


import Component.Type.FILE;
import Component.Type.PROJECT;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.core.issue.DefaultIssue;


public class IssuesRepositoryVisitorTest {
    static final String FILE_UUID = "FILE_UUID";

    static final String FILE_KEY = "FILE_KEY";

    static final int FILE_REF = 2;

    static final Component FILE = ReportComponent.builder(Component.Type.FILE, IssuesRepositoryVisitorTest.FILE_REF).setKey(IssuesRepositoryVisitorTest.FILE_KEY).setUuid(IssuesRepositoryVisitorTest.FILE_UUID).build();

    static final String PROJECT_KEY = "PROJECT_KEY";

    static final String PROJECT_UUID = "PROJECT_UUID";

    static final int PROJECT_REF = 1;

    static final Component PROJECT = ReportComponent.builder(Component.Type.PROJECT, IssuesRepositoryVisitorTest.PROJECT_REF).setKey(IssuesRepositoryVisitorTest.PROJECT_KEY).setUuid(IssuesRepositoryVisitorTest.PROJECT_UUID).addChildren(IssuesRepositoryVisitorTest.FILE).build();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public ComponentIssuesRepositoryRule componentIssuesRepository = new ComponentIssuesRepositoryRule(treeRootHolder);

    IssuesRepositoryVisitor underTest = new IssuesRepositoryVisitor(componentIssuesRepository);

    @Test
    public void feed_component_issues_repo() {
        DefaultIssue i1 = Mockito.mock(DefaultIssue.class);
        DefaultIssue i2 = Mockito.mock(DefaultIssue.class);
        underTest.beforeComponent(IssuesRepositoryVisitorTest.FILE);
        underTest.onIssue(IssuesRepositoryVisitorTest.FILE, i1);
        underTest.onIssue(IssuesRepositoryVisitorTest.FILE, i2);
        underTest.afterComponent(IssuesRepositoryVisitorTest.FILE);
        assertThat(componentIssuesRepository.getIssues(IssuesRepositoryVisitorTest.FILE_REF)).hasSize(2);
    }

    @Test
    public void empty_component_issues_repo_when_no_issue() {
        DefaultIssue i1 = Mockito.mock(DefaultIssue.class);
        DefaultIssue i2 = Mockito.mock(DefaultIssue.class);
        underTest.beforeComponent(IssuesRepositoryVisitorTest.FILE);
        underTest.onIssue(IssuesRepositoryVisitorTest.FILE, i1);
        underTest.onIssue(IssuesRepositoryVisitorTest.FILE, i2);
        underTest.afterComponent(IssuesRepositoryVisitorTest.FILE);
        assertThat(componentIssuesRepository.getIssues(IssuesRepositoryVisitorTest.FILE)).hasSize(2);
        underTest.beforeComponent(IssuesRepositoryVisitorTest.PROJECT);
        underTest.afterComponent(IssuesRepositoryVisitorTest.PROJECT);
        assertThat(componentIssuesRepository.getIssues(IssuesRepositoryVisitorTest.PROJECT)).isEmpty();
    }
}

