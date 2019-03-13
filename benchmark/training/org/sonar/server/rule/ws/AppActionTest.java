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


import OrganizationPermission.ADMINISTER_QUALITY_PROFILES;
import System2.INSTANCE;
import WebService.Action;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class AppActionTest {
    private static final Language LANG1 = LanguageTesting.newLanguage("xoo", "Xoo");

    private static final Language LANG2 = LanguageTesting.newLanguage("ws", "Whitespace");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private Languages languages = new Languages(AppActionTest.LANG1, AppActionTest.LANG2);

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private RuleWsSupport wsSupport = new RuleWsSupport(db.getDbClient(), userSession, defaultOrganizationProvider);

    private AppAction underTest = new AppAction(languages, db.getDbClient(), userSession, wsSupport);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void test_definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.isInternal()).isTrue();
        assertThat(definition.key()).isEqualTo("app");
        assertThat(definition.params()).hasSize(1);
        assertThat(definition.param("organization")).matches(( p) -> p.isInternal()).matches(( p) -> p.since().equals("6.4")).matches(( p) -> !(p.isRequired()));
    }

    @Test
    public void response_contains_rule_repositories() {
        insertRules();
        String json = ws.newRequest().execute().getInput();
        assertJson(json).isSimilarTo(("{" + (((((((((((("\"repositories\": [" + "    {") + "      \"key\": \"xoo\",") + "      \"name\": \"SonarQube\",") + "      \"language\": \"xoo\"") + "    },") + "    {") + "      \"key\": \"squid\",") + "      \"name\": \"SonarQube\",") + "      \"language\": \"ws\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void response_contains_languages() {
        String json = ws.newRequest().execute().getInput();
        assertJson(json).isSimilarTo(("{" + (((("\"languages\": {" + "    \"xoo\": \"Xoo\",") + "    \"ws\": \"Whitespace\"") + "  }") + "}")));
    }

    @Test
    public void canWrite_is_true_if_user_is_profile_administrator_of_default_organization() {
        userSession.addPermission(ADMINISTER_QUALITY_PROFILES, db.getDefaultOrganization());
        String json = ws.newRequest().execute().getInput();
        assertJson(json).isSimilarTo("{ \"canWrite\": true }");
    }

    @Test
    public void canWrite_is_true_if_user_is_profile_administrator_of_specified_organization() {
        OrganizationDto organization = db.organizations().insert();
        userSession.addPermission(ADMINISTER_QUALITY_PROFILES, organization);
        String json = ws.newRequest().setParam("organization", organization.getKey()).execute().getInput();
        assertJson(json).isSimilarTo("{ \"canWrite\": true }");
    }

    @Test
    public void canWrite_is_false_if_user_is_not_profile_administrator_of_specified_organization() {
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        userSession.addPermission(ADMINISTER_QUALITY_PROFILES, organization1);
        String json = ws.newRequest().setParam("organization", organization2.getKey()).execute().getInput();
        assertJson(json).isSimilarTo("{ \"canWrite\": false }");
    }

    @Test
    public void canWrite_is_false_if_user_is_not_profile_administrator_of_default_organization() {
        OrganizationDto organization = db.organizations().insert();
        userSession.addPermission(ADMINISTER_QUALITY_PROFILES, organization);
        String json = ws.newRequest().execute().getInput();
        assertJson(json).isSimilarTo("{ \"canWrite\": false }");
    }

    @Test
    public void throw_NotFoundException_if_organization_does_not_exist() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("No organization with key 'does_not_exist'");
        ws.newRequest().setParam("organization", "does_not_exist").execute();
    }
}

