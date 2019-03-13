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


import WebService.Action;
import WebService.Param;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.qualityprofile.index.ActiveRuleIndexer;
import org.sonar.server.rule.index.RuleIndex;
import org.sonar.server.rule.index.RuleIndexer;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Qualityprofiles.ShowResponse;
import org.sonarqube.ws.Qualityprofiles.ShowResponse.CompareToSonarWay;


public class ShowActionTest {
    private static Language XOO1 = LanguageTesting.newLanguage("xoo1");

    private static Language XOO2 = LanguageTesting.newLanguage("xoo2");

    private static Languages LANGUAGES = new Languages(ShowActionTest.XOO1, ShowActionTest.XOO2);

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RuleIndexer ruleIndexer = new RuleIndexer(es.client(), db.getDbClient());

    private ActiveRuleIndexer activeRuleIndexer = new ActiveRuleIndexer(db.getDbClient(), es.client());

    private RuleIndex ruleIndex = new RuleIndex(es.client(), System2.INSTANCE);

    private WsActionTester ws = new WsActionTester(new ShowAction(db.getDbClient(), new QProfileWsSupport(db.getDbClient(), userSession, TestDefaultOrganizationProvider.from(db)), ShowActionTest.LANGUAGES, ruleIndex));

    @Test
    public void profile_info() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()));
        assertThat(result.getProfile()).extracting(QualityProfile::getKey, QualityProfile::getName, QualityProfile::getIsBuiltIn, QualityProfile::getLanguage, QualityProfile::getLanguageName, QualityProfile::getIsInherited).containsExactly(profile.getKee(), profile.getName(), profile.isBuiltIn(), profile.getLanguage(), ShowActionTest.XOO1.getName(), false);
    }

    @Test
    public void default_profile() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        db.qualityProfiles().setAsDefault(profile);
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()));
        assertThat(result.getProfile().getIsDefault()).isTrue();
    }

    @Test
    public void non_default_profile() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto defaultProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        db.qualityProfiles().setAsDefault(defaultProfile);
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()));
        assertThat(result.getProfile().getIsDefault()).isFalse();
    }

    @Test
    public void map_dates() {
        long time = DateUtils.parseDateTime("2016-12-22T19:10:03+0100").getTime();
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()).setRulesUpdatedAt("2016-12-21T19:10:03+0100").setLastUsed(time).setUserUpdatedAt(time));
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()));
        assertThat(result.getProfile().getRulesUpdatedAt()).isEqualTo("2016-12-21T19:10:03+0100");
        assertThat(parseDateTime(result.getProfile().getLastUsed()).getTime()).isEqualTo(time);
        assertThat(parseDateTime(result.getProfile().getUserUpdatedAt()).getTime()).isEqualTo(time);
    }

    @Test
    public void statistics() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        // Active rules
        IntStream.range(0, 10).mapToObj(( i) -> db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition()).forEach(( r) -> db.qualityProfiles().activateRule(profile, r));
        // Deprecated rules
        IntStream.range(0, 3).mapToObj(( i) -> db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey()).setStatus(DEPRECATED)).getDefinition()).forEach(( r) -> db.qualityProfiles().activateRule(profile, r));
        // Projects
        IntStream.range(0, 7).mapToObj(( i) -> db.components().insertPrivateProject()).forEach(( project) -> db.qualityProfiles().associateWithProject(project, profile));
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()));
        assertThat(result.getProfile()).extracting(QualityProfile::getActiveRuleCount, QualityProfile::getActiveDeprecatedRuleCount, QualityProfile::getProjectCount).containsExactly(13L, 3L, 7L);
    }

    @Test
    public void compare_to_sonar_way_profile() {
        QProfileDto sonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("Sonar way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        RuleDefinitionDto commonRule = db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition();
        RuleDefinitionDto sonarWayRule1 = db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition();
        RuleDefinitionDto sonarWayRule2 = db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition();
        RuleDefinitionDto profileRule1 = db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition();
        RuleDefinitionDto profileRule2 = db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition();
        RuleDefinitionDto profileRule3 = db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition();
        db.qualityProfiles().activateRule(profile, commonRule);
        db.qualityProfiles().activateRule(profile, profileRule1);
        db.qualityProfiles().activateRule(profile, profileRule2);
        db.qualityProfiles().activateRule(profile, profileRule3);
        db.qualityProfiles().activateRule(sonarWayProfile, commonRule);
        db.qualityProfiles().activateRule(sonarWayProfile, sonarWayRule1);
        db.qualityProfiles().activateRule(sonarWayProfile, sonarWayRule2);
        ruleIndexer.indexOnStartup(ruleIndexer.getIndexTypes());
        activeRuleIndexer.indexOnStartup(activeRuleIndexer.getIndexTypes());
        CompareToSonarWay result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "true")).getCompareToSonarWay();
        assertThat(result).extracting(CompareToSonarWay::getProfile, CompareToSonarWay::getProfileName, CompareToSonarWay::getMissingRuleCount).containsExactly(sonarWayProfile.getKee(), sonarWayProfile.getName(), 2L);
    }

    @Test
    public void compare_to_sonar_way_profile_when_same_active_rules() {
        QProfileDto sonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("Sonar way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        RuleDefinitionDto commonRule = db.rules().insertRule(( r) -> r.setLanguage(ShowActionTest.XOO1.getKey())).getDefinition();
        db.qualityProfiles().activateRule(profile, commonRule);
        db.qualityProfiles().activateRule(sonarWayProfile, commonRule);
        ruleIndexer.indexOnStartup(ruleIndexer.getIndexTypes());
        activeRuleIndexer.indexOnStartup(activeRuleIndexer.getIndexTypes());
        CompareToSonarWay result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "true")).getCompareToSonarWay();
        assertThat(result).extracting(CompareToSonarWay::getProfile, CompareToSonarWay::getProfileName, CompareToSonarWay::getMissingRuleCount).containsExactly(sonarWayProfile.getKee(), sonarWayProfile.getName(), 0L);
    }

    @Test
    public void no_comparison_when_sonar_way_does_not_exist() {
        QProfileDto anotherSonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("Another Sonar way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "true"));
        assertThat(result.hasCompareToSonarWay()).isFalse();
    }

    @Test
    public void no_comparison_when_profile_is_built_in() {
        QProfileDto sonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("Sonar way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto anotherBuiltInProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setLanguage(ShowActionTest.XOO1.getKey()));
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, anotherBuiltInProfile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "true"));
        assertThat(result.hasCompareToSonarWay()).isFalse();
    }

    @Test
    public void no_comparison_if_sonar_way_is_not_built_in() {
        QProfileDto sonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(false).setName("Sonar way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "true"));
        assertThat(result.hasCompareToSonarWay()).isFalse();
    }

    @Test
    public void no_comparison_when_param_is_false() {
        QProfileDto sonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("Sonar way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "false"));
        assertThat(result.hasCompareToSonarWay()).isFalse();
    }

    @Test
    public void compare_to_sonarqube_way_profile() {
        QProfileDto sonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("SonarQube way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        CompareToSonarWay result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "true")).getCompareToSonarWay();
        assertThat(result).extracting(CompareToSonarWay::getProfile, CompareToSonarWay::getProfileName).containsExactly(sonarWayProfile.getKee(), sonarWayProfile.getName());
    }

    @Test
    public void compare_to_sonar_way_over_sonarqube_way() {
        QProfileDto sonarWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("Sonar way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto sonarQubeWayProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setIsBuiltIn(true).setName("SonarQube way").setLanguage(ShowActionTest.XOO1.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        CompareToSonarWay result = call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()).setParam(PARAM_COMPARE_TO_SONAR_WAY, "true")).getCompareToSonarWay();
        assertThat(result).extracting(CompareToSonarWay::getProfile, CompareToSonarWay::getProfileName).containsExactly(sonarWayProfile.getKee(), sonarWayProfile.getName());
    }

    @Test
    public void show_on_paid_organization() {
        OrganizationDto organization = db.organizations().insert();
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization, ( p) -> p.setLanguage(ShowActionTest.XOO1.getKey()));
        UserDto user = db.users().insertUser();
        db.organizations().addMember(organization, user);
        userSession.logIn(user);
        ShowResponse result = call(ws.newRequest().setParam(PARAM_KEY, qualityProfile.getKee()));
        assertThat(result.getProfile()).extracting(QualityProfile::getKey).containsExactly(qualityProfile.getKee());
    }

    @Test
    public void fail_if_profile_language_is_not_supported() {
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setKee("unknown-profile").setLanguage("kotlin"));
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Quality Profile with key 'unknown-profile' does not exist");
        call(ws.newRequest().setParam(PARAM_KEY, profile.getKee()));
    }

    @Test
    public void fail_if_profile_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Quality Profile with key 'unknown-profile' does not exist");
        call(ws.newRequest().setParam(PARAM_KEY, "unknown-profile"));
    }

    @Test
    public void fail_on_paid_organization_when_not_member() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto qualityProfile = db.qualityProfiles().insert(organization);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage(String.format("You're not member of organization '%s'", organization.getKey()));
        call(ws.newRequest().setParam(PARAM_KEY, qualityProfile.getKee()));
    }

    @Test
    public void json_example() {
        Language cs = LanguageTesting.newLanguage("cs", "C#");
        QProfileDto parentProfile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setKee("AU-TpxcA-iU5OvuD2FL1").setName("Parent Company Profile").setLanguage(cs.getKey()));
        QProfileDto profile = db.qualityProfiles().insert(db.getDefaultOrganization(), ( p) -> p.setKee("AU-TpxcA-iU5OvuD2FL3").setName("My Company Profile").setLanguage(cs.getKey()).setIsBuiltIn(false).setRulesUpdatedAt("2016-12-22T19:10:03+0100").setParentKee(parentProfile.getKee()));
        // Active rules
        IntStream.range(0, 10).mapToObj(( i) -> db.rules().insertRule(( r) -> r.setLanguage(cs.getKey())).getDefinition()).forEach(( r) -> db.qualityProfiles().activateRule(profile, r));
        // Projects
        IntStream.range(0, 7).mapToObj(( i) -> db.components().insertPrivateProject()).forEach(( project) -> db.qualityProfiles().associateWithProject(project, profile));
        ws = new WsActionTester(new ShowAction(db.getDbClient(), new QProfileWsSupport(db.getDbClient(), userSession, TestDefaultOrganizationProvider.from(db)), new Languages(cs), ruleIndex));
        String result = ws.newRequest().setParam(PARAM_KEY, profile.getKee()).execute().getInput();
        assertJson(result).ignoreFields("rulesUpdatedAt", "lastUsed", "userUpdatedAt").isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void test_definition() {
        WebService.Action action = ws.getDef();
        assertThat(action.key()).isEqualTo("show");
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.isPost()).isFalse();
        assertThat(action.since()).isEqualTo("6.5");
        WebService.Param profile = action.param("key");
        assertThat(profile.isRequired()).isTrue();
        assertThat(profile.isInternal()).isFalse();
        assertThat(profile.description()).isNotEmpty();
        WebService.Param compareToSonarWay = action.param("compareToSonarWay");
        assertThat(compareToSonarWay.isRequired()).isFalse();
        assertThat(compareToSonarWay.isInternal()).isTrue();
        assertThat(compareToSonarWay.description()).isNotEmpty();
        assertThat(compareToSonarWay.defaultValue()).isEqualTo("false");
        assertThat(compareToSonarWay.possibleValues()).contains("true", "false");
    }
}

