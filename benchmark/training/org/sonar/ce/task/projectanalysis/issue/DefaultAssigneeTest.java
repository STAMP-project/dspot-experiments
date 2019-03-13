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


import CoreProperties.DEFAULT_ISSUE_ASSIGNEE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderImpl;
import org.sonar.ce.task.projectanalysis.component.ConfigurationRepository;
import org.sonar.ce.task.projectanalysis.component.TestSettingsRepository;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;


public class DefaultAssigneeTest {
    public static final String PROJECT_KEY = "PROJECT_KEY";

    public static final String ORGANIZATION_UUID = "ORGANIZATION_UUID";

    public static final String QUALITY_GATE_UUID = "QUALITY_GATE_UUID";

    @Rule
    public DbTester db = DbTester.create();

    private MapSettings settings = new MapSettings();

    private ConfigurationRepository settingsRepository = new TestSettingsRepository(settings.asConfig());

    private AnalysisMetadataHolderImpl analysisMetadataHolder = new AnalysisMetadataHolderImpl();

    private OrganizationDto organizationDto;

    private DefaultAssignee underTest = new DefaultAssignee(db.getDbClient(), settingsRepository, analysisMetadataHolder);

    @Test
    public void no_default_assignee() {
        assertThat(underTest.loadDefaultAssigneeUuid()).isNull();
    }

    @Test
    public void set_default_assignee() {
        settings.setProperty(DEFAULT_ISSUE_ASSIGNEE, "erik");
        UserDto userDto = db.users().insertUser("erik");
        db.organizations().addMember(organizationDto, userDto);
        assertThat(underTest.loadDefaultAssigneeUuid()).isEqualTo(userDto.getUuid());
    }

    @Test
    public void configured_login_does_not_exist() {
        settings.setProperty(DEFAULT_ISSUE_ASSIGNEE, "erik");
        assertThat(underTest.loadDefaultAssigneeUuid()).isNull();
    }

    @Test
    public void configured_login_is_disabled() {
        settings.setProperty(DEFAULT_ISSUE_ASSIGNEE, "erik");
        db.users().insertUser(( user) -> user.setLogin("erik").setActive(false));
        assertThat(underTest.loadDefaultAssigneeUuid()).isNull();
    }

    @Test
    public void configured_login_is_not_member_of_organization() {
        settings.setProperty(DEFAULT_ISSUE_ASSIGNEE, "erik");
        OrganizationDto otherOrganization = db.organizations().insert();
        UserDto userDto = db.users().insertUser("erik");
        db.organizations().addMember(otherOrganization, userDto);
        assertThat(underTest.loadDefaultAssigneeUuid()).isNull();
    }

    @Test
    public void default_assignee_is_cached() {
        settings.setProperty(DEFAULT_ISSUE_ASSIGNEE, "erik");
        UserDto userDto = db.users().insertUser("erik");
        db.organizations().addMember(organizationDto, userDto);
        assertThat(underTest.loadDefaultAssigneeUuid()).isEqualTo(userDto.getUuid());
        // The setting is updated but the assignee hasn't changed
        settings.setProperty(DEFAULT_ISSUE_ASSIGNEE, "other");
        assertThat(underTest.loadDefaultAssigneeUuid()).isEqualTo(userDto.getUuid());
    }
}

