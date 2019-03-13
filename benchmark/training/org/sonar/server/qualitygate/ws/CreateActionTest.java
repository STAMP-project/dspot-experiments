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
package org.sonar.server.qualitygate.ws;


import System2.INSTANCE;
import WebService.Action;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.api.server.ws.WebService;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualitygate.QGateWithOrgDto;
import org.sonar.db.qualitygate.QualityGateDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Qualitygates.CreateResponse;


@RunWith(DataProviderRunner.class)
public class CreateActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private OrganizationDbTester organizationDbTester = new OrganizationDbTester(db);

    private TestDefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private CreateAction underTest = new CreateAction(dbClient, userSession, new org.sonar.server.qualitygate.QualityGateUpdater(dbClient, UuidFactoryFast.getInstance()), new QualityGatesWsSupport(dbClient, userSession, defaultOrganizationProvider));

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void default_organization_is_used_when_no_parameter() {
        logInAsQualityGateAdmin(db.getDefaultOrganization());
        String qgName = "Default";
        CreateResponse response = executeRequest(Optional.empty(), qgName);
        assertThat(response.getName()).isEqualTo(qgName);
        assertThat(response.getId()).isNotNull();
        dbSession.commit();
        QualityGateDto qualityGateDto = dbClient.qualityGateDao().selectByOrganizationAndName(dbSession, db.getDefaultOrganization(), qgName);
        assertThat(qualityGateDto).isNotNull();
    }

    @Test
    public void create_quality_gate_with_organization() {
        OrganizationDto organizationDto = organizationDbTester.insert();
        logInAsQualityGateAdmin(organizationDto);
        String qgName = "Default";
        CreateResponse response = executeRequest(Optional.of(organizationDto), qgName);
        assertThat(response.getName()).isEqualTo(qgName);
        assertThat(response.getId()).isNotNull();
        dbSession.commit();
        QualityGateDto qualityGateDto = dbClient.qualityGateDao().selectByOrganizationAndName(dbSession, organizationDto, qgName);
        assertThat(qualityGateDto).isNotNull();
    }

    @Test
    public void creating_a_qg_with_a_name_used_in_another_organization_should_work() {
        OrganizationDto anOrganization = db.organizations().insert();
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(anOrganization);
        OrganizationDto anotherOrganization = db.organizations().insert();
        userSession.addPermission(ADMINISTER_QUALITY_GATES, anotherOrganization);
        CreateResponse response = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_NAME, qualityGate.getName()).setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, anotherOrganization.getKey()).executeProtobuf(CreateResponse.class);
        assertThat(response.getName()).isEqualTo(qualityGate.getName());
        assertThat(response.getId()).isNotEqualTo(qualityGate.getId());
    }

    @Test
    public void test_ws_definition() {
        WebService.Action action = ws.getDef();
        assertThat(action).isNotNull();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.isPost()).isTrue();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).hasSize(2);
    }

    @Test
    public void throw_ForbiddenException_if_incorrect_organization() {
        logInAsQualityGateAdmin(db.getDefaultOrganization());
        OrganizationDto otherOrganization = organizationDbTester.insert();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeRequest(Optional.of(otherOrganization), "Default");
    }

    @Test
    public void throw_ForbiddenException_if_not_gate_administrator() {
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeRequest(Optional.empty(), "Default");
    }

    @Test
    public void throw_ForbiddenException_if_not_gate_administrator_of_own_organization() {
        // as long as organizations don't support Quality gates, the global permission
        // is defined on the default organization
        OrganizationDto org = db.organizations().insert();
        userSession.logIn().addPermission(ADMINISTER_QUALITY_GATES, org);
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        executeRequest(Optional.empty(), "Default");
    }

    @Test
    public void throw_ForbiddenException_if_unknown_organization() {
        OrganizationDto org = new OrganizationDto().setName("Unknown organization").setKey("unknown_key");
        userSession.logIn();
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("No organization with key 'unknown_key'");
        executeRequest(Optional.of(org), "Default");
    }

    @Test
    public void throw_BadRequestException_if_name_is_already_used() {
        OrganizationDto org = db.organizations().insert();
        userSession.logIn().addPermission(ADMINISTER_QUALITY_GATES, org);
        executeRequest(Optional.of(org), "Default");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Name has already been taken");
        executeRequest(Optional.of(org), "Default");
    }
}

