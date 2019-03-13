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


import RuleStatus.REMOVED;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.qualityprofile.QProfileRules;
import org.sonar.server.rule.index.RuleIndexer;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;


public class DeleteActionTest {
    private static final long PAST = 10000L;

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester dbTester = DbTester.create();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DbClient dbClient = dbTester.getDbClient();

    private DbSession dbSession = dbTester.getSession();

    private RuleIndexer ruleIndexer = Mockito.spy(new RuleIndexer(es.client(), dbClient));

    private QProfileRules qProfileRules = Mockito.mock(QProfileRules.class);

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.fromUuid("ORG1");

    private RuleWsSupport ruleWsSupport = new RuleWsSupport(Mockito.mock(DbClient.class), userSession, defaultOrganizationProvider);

    private DeleteAction underTest = new DeleteAction(System2.INSTANCE, ruleIndexer, dbClient, qProfileRules, ruleWsSupport);

    private WsActionTester tester = new WsActionTester(underTest);

    @Test
    public void delete_custom_rule() {
        logInAsQProfileAdministrator();
        RuleDefinitionDto templateRule = dbTester.rules().insert(( r) -> r.setIsTemplate(true), ( r) -> r.setCreatedAt(PAST), ( r) -> r.setUpdatedAt(PAST));
        RuleDefinitionDto customRule = dbTester.rules().insert(( r) -> r.setTemplateId(templateRule.getId()), ( r) -> r.setCreatedAt(PAST), ( r) -> r.setUpdatedAt(PAST));
        tester.newRequest().setMethod("POST").setParam("key", customRule.getKey().toString()).execute();
        Mockito.verify(ruleIndexer).commitAndIndex(ArgumentMatchers.any(), ArgumentMatchers.eq(customRule.getId()));
        // Verify custom rule has status REMOVED
        RuleDefinitionDto customRuleReloaded = dbClient.ruleDao().selectOrFailDefinitionByKey(dbSession, customRule.getKey());
        assertThat(customRuleReloaded).isNotNull();
        assertThat(customRuleReloaded.getStatus()).isEqualTo(REMOVED);
        assertThat(customRuleReloaded.getUpdatedAt()).isNotEqualTo(DeleteActionTest.PAST);
    }

    @Test
    public void throw_ForbiddenException_if_not_profile_administrator() {
        userSession.logIn();
        thrown.expect(ForbiddenException.class);
        tester.newRequest().setMethod("POST").setParam("key", "anyRuleKey").execute();
    }

    @Test
    public void throw_UnauthorizedException_if_not_logged_in() {
        thrown.expect(UnauthorizedException.class);
        tester.newRequest().setMethod("POST").setParam("key", "anyRuleKey").execute();
    }

    @Test
    public void fail_to_delete_if_not_custom() {
        logInAsQProfileAdministrator();
        RuleDefinitionDto rule = dbTester.rules().insert();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("Rule '" + (rule.getKey().toString())) + "' cannot be deleted because it is not a custom rule"));
        tester.newRequest().setMethod("POST").setParam("key", rule.getKey().toString()).execute();
    }
}

