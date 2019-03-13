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


import Severity.MAJOR;
import Severity.MINOR;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualityprofile.QProfileDto;
import org.sonar.db.qualityprofile.QualityProfileTesting;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class CompareActionTest {
    @Rule
    public DbTester db = DbTester.create();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private DbClient dbClient = db.getDbClient();

    private DbSession session = db.getSession();

    private WsActionTester ws = new WsActionTester(new CompareAction(db.getDbClient(), new org.sonar.server.qualityprofile.QProfileComparison(db.getDbClient()), new org.sonar.api.resources.Languages(LanguageTesting.newLanguage("xoo", "Xoo")), new QProfileWsSupport(db.getDbClient(), userSession, TestDefaultOrganizationProvider.from(db))));

    @Test
    public void compare_nominal() {
        createRepository("blah", "xoo", "Blah");
        RuleDefinitionDto rule1 = createRule("xoo", "rule1");
        RuleDefinitionDto rule2 = createRule("xoo", "rule2");
        RuleDefinitionDto rule3 = createRule("xoo", "rule3");
        RuleDefinitionDto rule4 = createRuleWithParam("xoo", "rule4");
        RuleDefinitionDto rule5 = createRule("xoo", "rule5");
        /* Profile 1:
        - rule 1 active (on both profiles) => "same"
        - rule 2 active (only in this profile) => "inLeft"
        - rule 4 active with different parameters => "modified"
        - rule 5 active with different severity => "modified"
         */
        QProfileDto profile1 = createProfile("xoo", "Profile 1", "xoo-profile-1-01234");
        createActiveRule(rule1, profile1);
        createActiveRule(rule2, profile1);
        createActiveRuleWithParam(rule4, profile1, "polop");
        createActiveRuleWithSeverity(rule5, profile1, MINOR);
        session.commit();
        /* Profile 1:
        - rule 1 active (on both profiles) => "same"
        - rule 3 active (only in this profile) => "inRight"
        - rule 4 active with different parameters => "modified"
         */
        QProfileDto profile2 = createProfile("xoo", "Profile 2", "xoo-profile-2-12345");
        createActiveRule(rule1, profile2);
        createActiveRule(rule3, profile2);
        createActiveRuleWithParam(rule4, profile2, "palap");
        createActiveRuleWithSeverity(rule5, profile2, MAJOR);
        session.commit();
        ws.newRequest().setParam("leftKey", profile1.getKee()).setParam("rightKey", profile2.getKee()).execute().assertJson(this.getClass(), "compare_nominal.json");
    }

    @Test
    public void compare_param_on_left() {
        RuleDefinitionDto rule1 = createRuleWithParam("xoo", "rule1");
        createRepository("blah", "xoo", "Blah");
        QProfileDto profile1 = createProfile("xoo", "Profile 1", "xoo-profile-1-01234");
        createActiveRuleWithParam(rule1, profile1, "polop");
        QProfileDto profile2 = createProfile("xoo", "Profile 2", "xoo-profile-2-12345");
        createActiveRule(rule1, profile2);
        session.commit();
        ws.newRequest().setParam("leftKey", profile1.getKee()).setParam("rightKey", profile2.getKee()).execute().assertJson(this.getClass(), "compare_param_on_left.json");
    }

    @Test
    public void compare_param_on_right() {
        RuleDefinitionDto rule1 = createRuleWithParam("xoo", "rule1");
        createRepository("blah", "xoo", "Blah");
        QProfileDto profile1 = createProfile("xoo", "Profile 1", "xoo-profile-1-01234");
        createActiveRule(rule1, profile1);
        QProfileDto profile2 = createProfile("xoo", "Profile 2", "xoo-profile-2-12345");
        createActiveRuleWithParam(rule1, profile2, "polop");
        session.commit();
        ws.newRequest().setParam("leftKey", profile1.getKee()).setParam("rightKey", profile2.getKee()).execute().assertJson(this.getClass(), "compare_param_on_right.json");
    }

    @Test
    public void do_not_fail_to_compare_on_paid_organization() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto left = db.qualityProfiles().insert(organization);
        QProfileDto right = db.qualityProfiles().insert(organization);
        UserDto user = db.users().insertUser();
        userSession.logIn(user).addMembership(organization);
        ws.newRequest().setParam("leftKey", left.getKee()).setParam("rightKey", right.getKee()).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_on_missing_left_param() {
        ws.newRequest().setParam("rightKey", "polop").execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_on_missing_right_param() {
        ws.newRequest().setParam("leftKey", "polop").execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_on_left_profile_not_found() {
        createProfile("xoo", "Right", "xoo-right-12345");
        ws.newRequest().setParam("leftKey", "polop").setParam("rightKey", "xoo-right-12345").execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail_on_right_profile_not_found() {
        createProfile("xoo", "Left", "xoo-left-12345");
        ws.newRequest().setParam("leftKey", "xoo-left-12345").setParam("rightKey", "polop").execute();
    }

    @Test
    public void fail_to_compare_quality_profiles_from_different_organizations() {
        QProfileDto left = QualityProfileTesting.newQualityProfileDto();
        QProfileDto right = QualityProfileTesting.newQualityProfileDto();
        db.qualityProfiles().insert(left, right);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Cannot compare quality profiles of different organizations.");
        ws.newRequest().setParam("leftKey", left.getKee()).setParam("rightKey", right.getKee()).execute();
    }

    @Test
    public void fail_on_paid_organization_when_not_member() {
        OrganizationDto organization = db.organizations().insert(( o) -> o.setSubscription(PAID));
        QProfileDto left = db.qualityProfiles().insert(organization);
        QProfileDto right = db.qualityProfiles().insert(organization);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage(String.format("You're not member of organization '%s'", organization.getKey()));
        ws.newRequest().setParam("leftKey", left.getKee()).setParam("rightKey", right.getKee()).execute();
    }

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition).isNotNull();
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.isInternal()).isTrue();
        assertThat(definition.params()).hasSize(2).extracting("key").containsOnly("leftKey", "rightKey");
        assertThat(definition.responseExampleAsString()).isNotEmpty();
    }
}

