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
package org.sonar.server.qualityprofile.ws;


import MediaTypes.JSON;
import WebService.Action;
import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WebService.Param;
import org.sonar.api.utils.System2;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.rule.RuleTesting;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.qualityprofile.QProfileExporters;
import org.sonar.server.qualityprofile.QProfileRules;
import org.sonar.server.qualityprofile.RuleActivator;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;
import org.sonar.server.rule.index.RuleIndex;
import org.sonar.server.rule.index.RuleIndexer;
import org.sonar.server.rule.index.RuleQuery;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestRequest;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;
import org.sonar.test.JsonAssert;
import org.sonarqube.ws.Qualityprofiles.CreateWsResponse;
import org.sonarqube.ws.Qualityprofiles.CreateWsResponse.QualityProfile;


public class CreateActionTest {
    private static final String XOO_LANGUAGE = "xoo";

    private static final RuleDefinitionDto RULE = RuleTesting.newXooX1().setSeverity("MINOR").setLanguage(CreateActionTest.XOO_LANGUAGE).getDefinition();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private RuleIndex ruleIndex = new RuleIndex(es.client(), System2.INSTANCE);

    private RuleIndexer ruleIndexer = new RuleIndexer(es.client(), dbClient);

    private ActiveRuleIndexer activeRuleIndexer = new ActiveRuleIndexer(dbClient, es.client());

    private ProfileImporter[] profileImporters = createImporters();

    private RuleActivator ruleActivator = new RuleActivator(System2.INSTANCE, dbClient, null, userSession);

    private QProfileRules qProfileRules = new org.sonar.server.qualityprofile.QProfileRulesImpl(dbClient, ruleActivator, ruleIndex, activeRuleIndexer);

    private QProfileExporters qProfileExporters = new QProfileExporters(dbClient, null, qProfileRules, profileImporters);

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private CreateAction underTest = new CreateAction(dbClient, new org.sonar.server.qualityprofile.QProfileFactoryImpl(dbClient, UuidFactoryFast.getInstance(), System2.INSTANCE, activeRuleIndexer), qProfileExporters, LanguageTesting.newLanguages(CreateActionTest.XOO_LANGUAGE), new QProfileWsSupport(dbClient, userSession, defaultOrganizationProvider), userSession, activeRuleIndexer, profileImporters);

    private WsActionTester ws = new WsActionTester(underTest);

    private OrganizationDto organization;

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.responseExampleAsString()).isNotEmpty();
        assertThat(definition.isPost()).isTrue();
        assertThat(definition.params()).extracting(Param::key).containsExactlyInAnyOrder("language", "organization", "name", "backup_with_messages", "backup_with_errors", "backup_xoo_lint");
        Param name = definition.param("name");
        assertThat(name.deprecatedKey()).isEqualTo("profileName");
        assertThat(name.deprecatedKeySince()).isEqualTo("6.6");
    }

    @Test
    public void create_profile() {
        logInAsQProfileAdministrator();
        CreateWsResponse response = executeRequest("New Profile", CreateActionTest.XOO_LANGUAGE);
        QProfileDto dto = dbClient.qualityProfileDao().selectByNameAndLanguage(dbSession, organization, "New Profile", CreateActionTest.XOO_LANGUAGE);
        assertThat(dto.getKee()).isNotNull();
        assertThat(dto.getLanguage()).isEqualTo(CreateActionTest.XOO_LANGUAGE);
        assertThat(dto.getName()).isEqualTo("New Profile");
        QualityProfile profile = response.getProfile();
        assertThat(profile.getKey()).isEqualTo(dto.getKee());
        assertThat(profile.getName()).isEqualTo("New Profile");
        assertThat(profile.getLanguage()).isEqualTo(CreateActionTest.XOO_LANGUAGE);
        assertThat(profile.getIsInherited()).isFalse();
        assertThat(profile.getIsDefault()).isFalse();
        assertThat(profile.hasInfos()).isFalse();
        assertThat(profile.hasWarnings()).isFalse();
    }

    @Test
    public void create_profile_from_backup_xml() {
        logInAsQProfileAdministrator();
        insertRule(CreateActionTest.RULE);
        executeRequest("New Profile", CreateActionTest.XOO_LANGUAGE, ImmutableMap.of("xoo_lint", "<xml/>"));
        QProfileDto dto = dbClient.qualityProfileDao().selectByNameAndLanguage(dbSession, organization, "New Profile", CreateActionTest.XOO_LANGUAGE);
        assertThat(dto.getKee()).isNotNull();
        assertThat(dbClient.activeRuleDao().selectByProfileUuid(dbSession, dto.getKee())).hasSize(1);
        assertThat(ruleIndex.searchAll(new RuleQuery().setQProfile(dto).setActivation(true))).hasSize(1);
    }

    @Test
    public void create_profile_with_messages() {
        logInAsQProfileAdministrator();
        CreateWsResponse response = executeRequest("Profile with messages", CreateActionTest.XOO_LANGUAGE, ImmutableMap.of("with_messages", "<xml/>"));
        QualityProfile profile = response.getProfile();
        assertThat(profile.getInfos().getInfosList()).containsOnly("an info");
        assertThat(profile.getWarnings().getWarningsList()).containsOnly("a warning");
    }

    @Test
    public void create_profile_for_specific_organization() {
        logInAsQProfileAdministrator();
        String orgKey = organization.getKey();
        TestRequest request = ws.newRequest().setParam("organization", orgKey).setParam("name", "Profile with messages").setParam("language", CreateActionTest.XOO_LANGUAGE).setParam("backup_with_messages", "<xml/>");
        assertThat(executeRequest(request).getProfile().getOrganization()).isEqualTo(orgKey);
    }

    @Test
    public void create_two_qprofiles_in_different_organizations_with_same_name_and_language() {
        // this name will be used twice
        String profileName = "Profile123";
        OrganizationDto organization1 = db.organizations().insert();
        logInAsQProfileAdministrator(organization1);
        TestRequest request1 = ws.newRequest().setParam("organization", organization1.getKey()).setParam("name", profileName).setParam("language", CreateActionTest.XOO_LANGUAGE);
        assertThat(executeRequest(request1).getProfile().getOrganization()).isEqualTo(organization1.getKey());
        OrganizationDto organization2 = db.organizations().insert();
        logInAsQProfileAdministrator(organization2);
        TestRequest request2 = ws.newRequest().setParam("organization", organization2.getKey()).setParam("name", profileName).setParam("language", CreateActionTest.XOO_LANGUAGE);
        assertThat(executeRequest(request2).getProfile().getOrganization()).isEqualTo(organization2.getKey());
    }

    @Test
    public void fail_if_unsufficient_privileges() {
        OrganizationDto organizationX = db.organizations().insert();
        OrganizationDto organizationY = db.organizations().insert();
        logInAsQProfileAdministrator(organizationX);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeRequest(ws.newRequest().setParam("organization", organizationY.getKey()).setParam("name", "some Name").setParam("language", CreateActionTest.XOO_LANGUAGE));
    }

    @Test
    public void fail_if_import_generate_error() {
        logInAsQProfileAdministrator();
        expectedException.expect(BadRequestException.class);
        executeRequest("Profile with errors", CreateActionTest.XOO_LANGUAGE, ImmutableMap.of("with_errors", "<xml/>"));
    }

    @Test
    public void test_json() {
        logInAsQProfileAdministrator(db.getDefaultOrganization());
        TestResponse response = ws.newRequest().setMethod("POST").setMediaType(JSON).setParam("language", CreateActionTest.XOO_LANGUAGE).setParam("name", "Yeehaw!").execute();
        JsonAssert.assertJson(response.getInput()).isSimilarTo(getClass().getResource("CreateActionTest/test_json.json"));
        assertThat(response.getMediaType()).isEqualTo(JSON);
    }
}

