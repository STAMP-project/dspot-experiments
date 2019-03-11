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


import ScannerReport.Component;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.scanner.protocol.output.ScannerReport;


public class IssueRelocationToRootTest {
    @Rule
    public BatchReportReaderRule reader = new BatchReportReaderRule();

    private IssueRelocationToRoot underTest = new IssueRelocationToRoot(reader);

    private Component root;

    private Component module;

    private Component directory;

    @Test
    public void move_module_and_directory_issues() {
        createComponents();
        createIssues();
        underTest.relocate(root, module);
        underTest.relocate(root, directory);
        assertThat(underTest.getMovedIssues()).hasSize(2);
        assertThat(underTest.getMovedIssues()).extracting(ScannerReport.Issue::getRuleKey).containsOnly("module_issue", "directory_issue");
    }

    @Test
    public void do_nothing_if_no_issues() {
        createComponents();
        underTest.relocate(root, module);
        underTest.relocate(root, directory);
        assertThat(underTest.getMovedIssues()).hasSize(0);
    }
}

