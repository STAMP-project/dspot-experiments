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
package org.sonar.server.ui.ws;


import Navigation.MarketplaceResponse;
import WebService.Action;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestRequest;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Navigation;


@RunWith(DataProviderRunner.class)
public class MarketplaceActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private Server server = Mockito.mock(Server.class);

    private DbClient dbClient = db.getDbClient();

    private MarketplaceAction underTest = new MarketplaceAction(userSessionRule, server, dbClient, new org.sonar.server.organization.DefaultOrganizationProviderImpl(dbClient));

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void definition() {
        WebService.Action def = ws.getDef();
        assertThat(def.key()).isEqualTo("marketplace");
        assertThat(def.since()).isEqualTo("7.2");
        assertThat(def.isPost()).isFalse();
        assertThat(def.isInternal()).isTrue();
        assertThat(def.description()).isNotEmpty();
        assertThat(def.params()).isEmpty();
    }

    @Test
    public void request_fails_if_user_not_logged_in() {
        userSessionRule.anonymous();
        TestRequest request = ws.newRequest();
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        request.execute();
    }

    @Test
    public void request_fails_if_user_is_not_system_administer() {
        userSessionRule.logIn();
        TestRequest request = ws.newRequest();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        request.execute();
    }

    @Test
    public void json_example() {
        userSessionRule.logIn().setSystemAdministrator();
        Mockito.when(server.getId()).thenReturn("AU-Tpxb--iU5OvuD2FLy");
        setNcloc(12345L);
        String result = ws.newRequest().execute().getInput();
        assertJson(result).isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void returns_server_id_and_nloc() {
        userSessionRule.logIn().setSystemAdministrator();
        Mockito.when(server.getId()).thenReturn("myserver");
        long ncloc = 256L;
        setNcloc(ncloc);
        Navigation.MarketplaceResponse expectedResponse = MarketplaceResponse.newBuilder().setServerId("myserver").setNcloc(ncloc).build();
        Navigation.MarketplaceResponse result = ws.newRequest().executeProtobuf(MarketplaceResponse.class);
        assertThat(result).isEqualTo(expectedResponse);
    }
}

