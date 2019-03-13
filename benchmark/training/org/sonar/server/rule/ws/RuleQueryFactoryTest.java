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
package org.sonar.server.rule.ws;


import System2.INSTANCE;
import WebService.NewAction;
import WebService.NewController;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.internal.SimpleGetRequest;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.rule.index.RuleQuery;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsAction;


public class RuleQueryFactoryTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private DbClient dbClient = db.getDbClient();

    private RuleQueryFactory underTest = new RuleQueryFactory(dbClient, new RuleWsSupport(dbClient, userSession, TestDefaultOrganizationProvider.from(db)));

    private RuleQueryFactoryTest.FakeAction fakeAction = new RuleQueryFactoryTest.FakeAction(underTest);

    @Test
    public void create_empty_query() {
        RuleQuery result = execute();
        assertThat(result.getKey()).isNull();
        assertThat(result.getActivation()).isNull();
        assertThat(result.getActiveSeverities()).isNull();
        assertThat(result.isAscendingSort()).isTrue();
        assertThat(result.getAvailableSinceLong()).isNull();
        assertThat(result.getInheritance()).isNull();
        assertThat(result.includeExternal()).isFalse();
        assertThat(result.isTemplate()).isNull();
        assertThat(result.getLanguages()).isNull();
        assertThat(result.getQueryText()).isNull();
        assertThat(result.getQProfile()).isNull();
        assertThat(result.getRepositories()).isNull();
        assertThat(result.getRuleKey()).isNull();
        assertThat(result.getSeverities()).isNull();
        assertThat(result.getStatuses()).isEmpty();
        assertThat(result.getTags()).isNull();
        assertThat(result.templateKey()).isNull();
        assertThat(result.getTypes()).isEmpty();
        assertThat(result.getSortField()).isNull();
        assertThat(result.getCompareToQProfile()).isNull();
    }

    @Test
    public void create_rule_search_query() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization);
        QProfileDto compareToQualityProfile = db.qualityProfiles().insert(organization);
        RuleQuery result = executeRuleSearchQuery(RulesWsParameters.PARAM_RULE_KEY, "ruleKey", RulesWsParameters.PARAM_ACTIVATION, "true", RulesWsParameters.PARAM_ACTIVE_SEVERITIES, "MINOR,MAJOR", RulesWsParameters.PARAM_AVAILABLE_SINCE, "2016-01-01", RulesWsParameters.PARAM_INHERITANCE, "INHERITED,OVERRIDES", RulesWsParameters.PARAM_IS_TEMPLATE, "true", RulesWsParameters.PARAM_INCLUDE_EXTERNAL, "false", RulesWsParameters.PARAM_LANGUAGES, "java,js", TEXT_QUERY, "S001", RulesWsParameters.PARAM_ORGANIZATION, organization.getKey(), RulesWsParameters.PARAM_QPROFILE, qualityProfile.getKee(), RulesWsParameters.PARAM_COMPARE_TO_PROFILE, compareToQualityProfile.getKee(), RulesWsParameters.PARAM_REPOSITORIES, "pmd,checkstyle", RulesWsParameters.PARAM_SEVERITIES, "MINOR,CRITICAL", RulesWsParameters.PARAM_STATUSES, "DEPRECATED,READY", RulesWsParameters.PARAM_TAGS, "tag1,tag2", RulesWsParameters.PARAM_TEMPLATE_KEY, "architectural", RulesWsParameters.PARAM_TYPES, "CODE_SMELL,BUG", SORT, "updatedAt", ASCENDING, "false");
        assertResult(result, qualityProfile, compareToQualityProfile);
        assertThat(result.includeExternal()).isFalse();
    }

    @Test
    public void include_external_is_mandatory_for_rule_search_query() {
        OrganizationDto organization = db.organizations().insert();
        db.qualityProfiles().insert(organization);
        db.qualityProfiles().insert(organization);
        Request request = new SimpleGetRequest();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The 'include_external' parameter is missing");
        underTest.createRuleSearchQuery(db.getSession(), request);
    }

    @Test
    public void create_query() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization);
        QProfileDto compareToQualityProfile = db.qualityProfiles().insert(organization);
        RuleQuery result = execute(RulesWsParameters.PARAM_RULE_KEY, "ruleKey", RulesWsParameters.PARAM_ACTIVATION, "true", RulesWsParameters.PARAM_ACTIVE_SEVERITIES, "MINOR,MAJOR", RulesWsParameters.PARAM_AVAILABLE_SINCE, "2016-01-01", RulesWsParameters.PARAM_INHERITANCE, "INHERITED,OVERRIDES", RulesWsParameters.PARAM_IS_TEMPLATE, "true", RulesWsParameters.PARAM_INCLUDE_EXTERNAL, "true", RulesWsParameters.PARAM_LANGUAGES, "java,js", TEXT_QUERY, "S001", RulesWsParameters.PARAM_ORGANIZATION, organization.getKey(), RulesWsParameters.PARAM_QPROFILE, qualityProfile.getKee(), RulesWsParameters.PARAM_COMPARE_TO_PROFILE, compareToQualityProfile.getKee(), RulesWsParameters.PARAM_REPOSITORIES, "pmd,checkstyle", RulesWsParameters.PARAM_SEVERITIES, "MINOR,CRITICAL", RulesWsParameters.PARAM_STATUSES, "DEPRECATED,READY", RulesWsParameters.PARAM_TAGS, "tag1,tag2", RulesWsParameters.PARAM_TEMPLATE_KEY, "architectural", RulesWsParameters.PARAM_TYPES, "CODE_SMELL,BUG", SORT, "updatedAt", ASCENDING, "false");
        assertResult(result, qualityProfile, compareToQualityProfile);
        assertThat(result.includeExternal()).isFalse();
    }

    @Test
    public void use_quality_profiles_language_if_available() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization);
        String qualityProfileKey = qualityProfile.getKee();
        RuleQuery result = execute(RulesWsParameters.PARAM_LANGUAGES, "specifiedLanguage", RulesWsParameters.PARAM_ACTIVATION, "true", RulesWsParameters.PARAM_QPROFILE, qualityProfileKey);
        assertThat(result.getLanguages()).containsExactly(qualityProfile.getLanguage());
    }

    @Test
    public void use_specified_languages_if_no_quality_profile_available() {
        RuleQuery result = execute(RulesWsParameters.PARAM_LANGUAGES, "specifiedLanguage");
        assertThat(result.getLanguages()).containsExactly("specifiedLanguage");
    }

    @Test
    public void create_query_add_language_from_profile() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setName("Sonar way").setLanguage("xoo").setKee("sonar-way"));
        RuleQuery result = execute(RulesWsParameters.PARAM_QPROFILE, profile.getKee(), RulesWsParameters.PARAM_LANGUAGES, "java,js");
        assertThat(result.getQProfile().getKee()).isEqualTo(profile.getKee());
        assertThat(result.getLanguages()).containsOnly("xoo");
    }

    @Test
    public void filter_on_quality_profiles_organization_if_searching_for_actives_with_no_organization_specified() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setName("Sonar way").setLanguage("xoo").setKee("sonar-way"));
        RuleQuery result = execute(RulesWsParameters.PARAM_ACTIVATION, "true", RulesWsParameters.PARAM_QPROFILE, profile.getKee());
        assertThat(result.getOrganization().getUuid()).isEqualTo(organization.getUuid());
    }

    @Test
    public void filter_on_compare_to() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto compareToProfile = db.qualityProfiles().insert(organization);
        RuleQuery result = execute(RulesWsParameters.PARAM_ORGANIZATION, organization.getKey(), RulesWsParameters.PARAM_COMPARE_TO_PROFILE, compareToProfile.getKee());
        assertThat(result.getCompareToQProfile().getKee()).isEqualTo(compareToProfile.getKee());
    }

    @Test
    public void activation_is_kept_when_member_of_paid_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setName("Sonar way").setLanguage("xoo").setKee("sonar-way"));
        userSession.logIn(db.users().insertUser()).addMembership(organization);
        RuleQuery result = execute(RulesWsParameters.PARAM_ACTIVATION, "true", RulesWsParameters.PARAM_QPROFILE, profile.getKee());
        assertThat(result.getActivation()).isTrue();
    }

    @Test
    public void activation_is_set_to_null_when_not_member_of_paid_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto profile = db.qualityProfiles().insert(organization, ( p) -> p.setName("Sonar way").setLanguage("xoo").setKee("sonar-way"));
        RuleQuery result = execute(RulesWsParameters.PARAM_ACTIVATION, "true", RulesWsParameters.PARAM_QPROFILE, profile.getKee());
        assertThat(result.getActivation()).isNull();
    }

    @Test
    public void fail_if_organization_and_quality_profile_are_contradictory() {
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization1);
        String qualityProfileKey = qualityProfile.getKee();
        String organization2Key = organization2.getKey();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((((("The specified quality profile '" + qualityProfileKey) + "' is not part of the specified organization '") + organization2Key) + "'"));
        execute(RulesWsParameters.PARAM_QPROFILE, qualityProfileKey, RulesWsParameters.PARAM_ORGANIZATION, organization2Key);
    }

    @Test
    public void fail_if_organization_and_compare_to_quality_profile_are_contradictory() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization);
        OrganizationDto otherOrganization = db.organizations().insert();
        QProfileDto compareToQualityProfile = db.qualityProfiles().insert(otherOrganization);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage((((("The specified quality profile '" + (compareToQualityProfile.getKee())) + "' is not part of the specified organization '") + (organization.getKey())) + "'"));
        execute(RulesWsParameters.PARAM_QPROFILE, qualityProfile.getKee(), RulesWsParameters.PARAM_COMPARE_TO_PROFILE, compareToQualityProfile.getKee(), RulesWsParameters.PARAM_ORGANIZATION, organization.getKey());
    }

    @Test
    public void fail_when_organization_does_not_exist() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization);
        String qualityProfileKey = qualityProfile.getKee();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("No organization with key 'unknown'");
        execute(RulesWsParameters.PARAM_QPROFILE, qualityProfileKey, RulesWsParameters.PARAM_ORGANIZATION, "unknown");
    }

    @Test
    public void fail_when_profile_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("The specified qualityProfile 'unknown' does not exist");
        execute(RulesWsParameters.PARAM_QPROFILE, "unknown");
    }

    @Test
    public void fail_when_compare_to_profile_does_not_exist() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("The specified qualityProfile 'unknown' does not exist");
        execute(RulesWsParameters.PARAM_QPROFILE, qualityProfile.getKee(), RulesWsParameters.PARAM_COMPARE_TO_PROFILE, "unknown");
    }

    private class FakeAction implements WsAction {
        private final RuleQueryFactory ruleQueryFactory;

        private RuleQuery ruleQuery;

        private FakeAction(RuleQueryFactory ruleQueryFactory) {
            this.ruleQueryFactory = ruleQueryFactory;
        }

        @Override
        public void define(WebService.NewController controller) {
            WebService.NewAction action = controller.createAction("fake").setHandler(this);
            RuleWsSupport.defineGenericRuleSearchParameters(action);
        }

        @Override
        public void handle(Request request, Response response) {
            ruleQuery = ruleQueryFactory.createRuleQuery(db.getSession(), request);
        }

        RuleQuery getRuleQuery() {
            return ruleQuery;
        }
    }
}

