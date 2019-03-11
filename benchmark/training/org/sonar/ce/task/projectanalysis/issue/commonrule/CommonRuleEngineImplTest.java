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
package org.sonar.ce.task.projectanalysis.issue.commonrule;


import Component.Type.FILE;
import java.util.Collection;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.FileAttributes;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.core.issue.DefaultIssue;


public class CommonRuleEngineImplTest {
    CommonRule rule1 = Mockito.mock(CommonRule.class);

    CommonRule rule2 = Mockito.mock(CommonRule.class);

    CommonRuleEngineImpl underTest = new CommonRuleEngineImpl(rule1, rule2);

    @Test
    public void process_files_with_known_language() {
        ReportComponent file = ReportComponent.builder(FILE, 1).setKey("FILE_KEY").setUuid("FILE_UUID").setFileAttributes(new FileAttributes(false, "java", 1)).build();
        DefaultIssue issue = new DefaultIssue();
        Mockito.when(rule1.processFile(file, "java")).thenReturn(issue);
        Mockito.when(rule2.processFile(file, "java")).thenReturn(null);
        Collection<DefaultIssue> issues = underTest.process(file);
        assertThat(issues).containsOnly(issue);
    }

    @Test
    public void do_not_process_files_with_unknown_language() {
        ReportComponent file = ReportComponent.builder(FILE, 1).setKey("FILE_KEY").setUuid("FILE_UUID").setFileAttributes(new FileAttributes(false, null, 1)).build();
        Collection<DefaultIssue> issues = underTest.process(file);
        assertThat(issues).isEmpty();
        Mockito.verifyZeroInteractions(rule1, rule2);
    }

    @Test
    public void do_not_process_non_files() {
        Collection<DefaultIssue> issues = underTest.process(ReportComponent.DUMB_PROJECT);
        assertThat(issues).isEmpty();
        Mockito.verifyZeroInteractions(rule1, rule2);
    }
}

