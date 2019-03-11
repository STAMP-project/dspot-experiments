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
package org.sonar.scanner.scan;


import CoreProperties.PROJECT_KEY_PROPERTY;
import ScannerProperties.BRANCH_NAME;
import ScannerProperties.BRANCH_TARGET;
import ScannerProperties.PULL_REQUEST_BASE;
import ScannerProperties.PULL_REQUEST_BRANCH;
import ScannerProperties.PULL_REQUEST_KEY;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.AnalysisMode;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.bootstrap.ProjectReactor;
import org.sonar.api.utils.MessageException;
import org.sonar.scanner.ProjectInfo;
import org.sonar.scanner.bootstrap.GlobalConfiguration;


@RunWith(DataProviderRunner.class)
public class ProjectReactorValidatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AnalysisMode mode = Mockito.mock(AnalysisMode.class);

    private GlobalConfiguration settings = Mockito.mock(GlobalConfiguration.class);

    private ProjectInfo projectInfo = Mockito.mock(ProjectInfo.class);

    private ProjectReactorValidator underTest = new ProjectReactorValidator(mode, settings);

    @Test
    public void allow_slash_issues_mode() {
        Mockito.when(mode.isIssues()).thenReturn(true);
        underTest.validate(createProjectReactor("project/key"));
        Mockito.when(mode.isIssues()).thenReturn(false);
        thrown.expect(MessageException.class);
        thrown.expectMessage("is not a valid project or module key");
        underTest.validate(createProjectReactor("project/key"));
    }

    @Test
    public void fail_with_invalid_key() {
        ProjectReactor reactor = createProjectReactor("foo$bar");
        thrown.expect(MessageException.class);
        thrown.expectMessage("\"foo$bar\" is not a valid project or module key");
        underTest.validate(reactor);
    }

    @Test
    public void fail_with_backslash_in_key() {
        ProjectReactor reactor = createProjectReactor("foo\\bar");
        thrown.expect(MessageException.class);
        thrown.expectMessage("\"foo\\bar\" is not a valid project or module key");
        underTest.validate(reactor);
    }

    @Test
    public void fail_with_only_digits() {
        ProjectReactor reactor = createProjectReactor("12345");
        thrown.expect(MessageException.class);
        thrown.expectMessage("\"12345\" is not a valid project or module key");
        underTest.validate(reactor);
    }

    @Test
    public void fail_when_branch_name_is_specified_but_branch_plugin_not_present() {
        ProjectDefinition def = ProjectDefinition.create().setProperty(PROJECT_KEY_PROPERTY, "foo");
        ProjectReactor reactor = new ProjectReactor(def);
        Mockito.when(settings.get(ArgumentMatchers.eq(BRANCH_NAME))).thenReturn(Optional.of("feature1"));
        thrown.expect(MessageException.class);
        thrown.expectMessage("the branch plugin is required but not installed");
        underTest.validate(reactor);
    }

    @Test
    public void fail_when_branch_target_is_specified_but_branch_plugin_not_present() {
        ProjectDefinition def = ProjectDefinition.create().setProperty(PROJECT_KEY_PROPERTY, "foo");
        ProjectReactor reactor = new ProjectReactor(def);
        Mockito.when(settings.get(ArgumentMatchers.eq(BRANCH_TARGET))).thenReturn(Optional.of("feature1"));
        thrown.expect(MessageException.class);
        thrown.expectMessage("the branch plugin is required but not installed");
        underTest.validate(reactor);
    }

    @Test
    public void fail_when_pull_request_id_specified_but_branch_plugin_not_present() {
        ProjectDefinition def = ProjectDefinition.create().setProperty(PROJECT_KEY_PROPERTY, "foo");
        ProjectReactor reactor = new ProjectReactor(def);
        Mockito.when(settings.get(ArgumentMatchers.eq(PULL_REQUEST_KEY))).thenReturn(Optional.of("#1984"));
        thrown.expect(MessageException.class);
        thrown.expectMessage("the branch plugin is required but not installed");
        underTest.validate(reactor);
    }

    @Test
    public void fail_when_pull_request_branch_is_specified_but_branch_plugin_not_present() {
        ProjectDefinition def = ProjectDefinition.create().setProperty(PROJECT_KEY_PROPERTY, "foo");
        ProjectReactor reactor = new ProjectReactor(def);
        Mockito.when(settings.get(ArgumentMatchers.eq(PULL_REQUEST_BRANCH))).thenReturn(Optional.of("feature1"));
        thrown.expect(MessageException.class);
        thrown.expectMessage("the branch plugin is required but not installed");
        underTest.validate(reactor);
    }

    @Test
    public void fail_when_pull_request_base_specified_but_branch_plugin_not_present() {
        ProjectDefinition def = ProjectDefinition.create().setProperty(PROJECT_KEY_PROPERTY, "foo");
        ProjectReactor reactor = new ProjectReactor(def);
        Mockito.when(settings.get(ArgumentMatchers.eq(PULL_REQUEST_BASE))).thenReturn(Optional.of("feature1"));
        thrown.expect(MessageException.class);
        thrown.expectMessage("the branch plugin is required but not installed");
        underTest.validate(reactor);
    }
}

