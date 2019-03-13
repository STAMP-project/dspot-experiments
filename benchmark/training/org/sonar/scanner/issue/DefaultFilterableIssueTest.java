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
package org.sonar.scanner.issue;


import java.util.Date;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.fs.internal.DefaultInputProject;
import org.sonar.scanner.ProjectInfo;
import org.sonar.scanner.protocol.output.ScannerReport.Issue;


public class DefaultFilterableIssueTest {
    private DefaultFilterableIssue issue;

    private DefaultInputProject mockedProject;

    private ProjectInfo projectInfo;

    private InputComponent component;

    private Issue rawIssue;

    @Test
    public void testRoundTrip() {
        rawIssue = createIssue();
        issue = new DefaultFilterableIssue(mockedProject, projectInfo, rawIssue, component);
        Mockito.when(projectInfo.getAnalysisDate()).thenReturn(new Date(10000));
        Mockito.when(mockedProject.key()).thenReturn("projectKey");
        assertThat(issue.componentKey()).isEqualTo(component.key());
        assertThat(issue.creationDate()).isEqualTo(new Date(10000));
        assertThat(issue.line()).isEqualTo(30);
        assertThat(issue.textRange().start().line()).isEqualTo(30);
        assertThat(issue.textRange().start().lineOffset()).isEqualTo(10);
        assertThat(issue.textRange().end().line()).isEqualTo(31);
        assertThat(issue.textRange().end().lineOffset()).isEqualTo(3);
        assertThat(issue.projectKey()).isEqualTo("projectKey");
        assertThat(issue.gap()).isEqualTo(3.0);
        assertThat(issue.severity()).isEqualTo("MAJOR");
    }

    @Test
    public void nullValues() {
        rawIssue = createIssueWithoutFields();
        issue = new DefaultFilterableIssue(mockedProject, projectInfo, rawIssue, component);
        assertThat(issue.line()).isNull();
        assertThat(issue.gap()).isNull();
    }
}

