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
package org.sonar.server.qualityprofile;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.AlwaysIncreasingSystem2;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.qualityprofile.RulesProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleParamDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;


public class QProfileFactoryImplTest {
    private System2 system2 = new AlwaysIncreasingSystem2();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(system2);

    private DbSession dbSession = db.getSession();

    private ActiveRuleIndexer activeRuleIndexer = Mockito.mock(ActiveRuleIndexer.class);

    private QProfileFactory underTest = new QProfileFactoryImpl(db.getDbClient(), new SequenceUuidFactory(), system2, activeRuleIndexer);

    private RuleDefinitionDto rule;

    private RuleParamDto ruleParam;

    @Test
    public void checkAndCreateCustom() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = underTest.checkAndCreateCustom(dbSession, organization, new QProfileName("xoo", "P1"));
        assertThat(profile.getOrganizationUuid()).isEqualTo(organization.getUuid());
        assertThat(profile.getKee()).isNotEmpty();
        assertThat(profile.getName()).isEqualTo("P1");
        assertThat(profile.getLanguage()).isEqualTo("xoo");
        assertThat(profile.getId()).isNotNull();
        assertThat(profile.isBuiltIn()).isFalse();
        QProfileDto reloaded = db.getDbClient().qualityProfileDao().selectByNameAndLanguage(dbSession, organization, profile.getName(), profile.getLanguage());
        QProfileFactoryImplTest.assertEqual(profile, reloaded);
        assertThat(db.getDbClient().qualityProfileDao().selectOrderedByOrganizationUuid(dbSession, organization)).extracting(QProfileDto::getKee).containsExactly(profile.getKee());
    }

    @Test
    public void checkAndCreateCustom_throws_BadRequestException_if_name_null() {
        QProfileName name = new QProfileName("xoo", null);
        OrganizationDto organization = db.organizations().insert();
        expectBadRequestException("quality_profiles.profile_name_cant_be_blank");
        underTest.checkAndCreateCustom(dbSession, organization, name);
    }

    @Test
    public void checkAndCreateCustom_throws_BadRequestException_if_name_empty() {
        QProfileName name = new QProfileName("xoo", "");
        OrganizationDto organization = db.organizations().insert();
        expectBadRequestException("quality_profiles.profile_name_cant_be_blank");
        underTest.checkAndCreateCustom(dbSession, organization, name);
    }

    @Test
    public void checkAndCreateCustom_throws_BadRequestException_if_already_exists() {
        QProfileName name = new QProfileName("xoo", "P1");
        OrganizationDto organization = db.organizations().insert();
        underTest.checkAndCreateCustom(dbSession, organization, name);
        dbSession.commit();
        expectBadRequestException("Quality profile already exists: xoo/P1");
        underTest.checkAndCreateCustom(dbSession, organization, name);
    }

    @Test
    public void delete_custom_profiles() {
        OrganizationDto org = db.organizations().insert();
        QProfileDto profile1 = createCustomProfile(org);
        QProfileDto profile2 = createCustomProfile(org);
        QProfileDto profile3 = createCustomProfile(org);
        underTest.delete(dbSession, Arrays.asList(profile1, profile2));
        verifyCallActiveRuleIndexerDelete(profile1.getKee(), profile2.getKee());
        assertThatCustomProfileDoesNotExist(profile1);
        assertThatCustomProfileDoesNotExist(profile2);
        assertThatCustomProfileExists(profile3);
    }

    @Test
    public void delete_removes_custom_profile_marked_as_default() {
        OrganizationDto org = db.organizations().insert();
        QProfileDto profile = createCustomProfile(org);
        db.qualityProfiles().setAsDefault(profile);
        underTest.delete(dbSession, Arrays.asList(profile));
        assertThatCustomProfileDoesNotExist(profile);
    }

    @Test
    public void delete_removes_custom_profile_from_project_associations() {
        OrganizationDto org = db.organizations().insert();
        QProfileDto profile = createCustomProfile(org);
        ComponentDto project = db.components().insertPrivateProject(org);
        db.qualityProfiles().associateWithProject(project, profile);
        underTest.delete(dbSession, Arrays.asList(profile));
        assertThatCustomProfileDoesNotExist(profile);
    }

    @Test
    public void delete_builtin_profile() {
        RulesProfileDto builtInProfile = createBuiltInProfile();
        OrganizationDto org = db.organizations().insert();
        QProfileDto profile = associateBuiltInProfileToOrganization(builtInProfile, org);
        underTest.delete(dbSession, Arrays.asList(profile));
        verifyNoCallsActiveRuleIndexerDelete();
        // remove only from org_qprofiles
        assertThat(db.getDbClient().qualityProfileDao().selectOrderedByOrganizationUuid(dbSession, org)).isEmpty();
        assertThatRulesProfileExists(builtInProfile);
    }

    @Test
    public void delete_builtin_profile_associated_to_project() {
        RulesProfileDto builtInProfile = createBuiltInProfile();
        OrganizationDto org = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(org);
        QProfileDto profile = associateBuiltInProfileToOrganization(builtInProfile, org);
        db.qualityProfiles().associateWithProject(project, profile);
        assertThat(db.getDbClient().qualityProfileDao().selectAssociatedToProjectAndLanguage(dbSession, project, profile.getLanguage())).isNotNull();
        underTest.delete(dbSession, Arrays.asList(profile));
        verifyNoCallsActiveRuleIndexerDelete();
        // remove only from org_qprofiles and project_qprofiles
        assertThat(db.getDbClient().qualityProfileDao().selectOrderedByOrganizationUuid(dbSession, org)).isEmpty();
        assertThat(db.getDbClient().qualityProfileDao().selectAssociatedToProjectAndLanguage(dbSession, project, profile.getLanguage())).isNull();
        assertThatRulesProfileExists(builtInProfile);
    }

    @Test
    public void delete_builtin_profile_marked_as_default_on_organization() {
        RulesProfileDto builtInProfile = createBuiltInProfile();
        OrganizationDto org = db.organizations().insert();
        QProfileDto profile = associateBuiltInProfileToOrganization(builtInProfile, org);
        db.qualityProfiles().setAsDefault(profile);
        underTest.delete(dbSession, Arrays.asList(profile));
        verifyNoCallsActiveRuleIndexerDelete();
        // remove only from org_qprofiles and default_qprofiles
        assertThat(db.getDbClient().qualityProfileDao().selectOrderedByOrganizationUuid(dbSession, org)).isEmpty();
        assertThat(db.getDbClient().qualityProfileDao().selectDefaultProfile(dbSession, org, profile.getLanguage())).isNull();
        assertThatRulesProfileExists(builtInProfile);
    }

    @Test
    public void delete_accepts_empty_list_of_keys() {
        OrganizationDto org = db.organizations().insert();
        QProfileDto profile = createCustomProfile(org);
        underTest.delete(dbSession, Collections.emptyList());
        Mockito.verifyZeroInteractions(activeRuleIndexer);
        assertQualityProfileFromDb(profile).isNotNull();
    }

    @Test
    public void delete_removes_qprofile_edit_permissions() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization);
        UserDto user = db.users().insertUser();
        db.qualityProfiles().addUserPermission(profile, user);
        GroupDto group = db.users().insertGroup(organization);
        db.qualityProfiles().addGroupPermission(profile, group);
        underTest.delete(dbSession, Arrays.asList(profile));
        assertThat(db.countRowsOfTable(dbSession, "qprofile_edit_users")).isZero();
        assertThat(db.countRowsOfTable(dbSession, "qprofile_edit_groups")).isZero();
    }
}

