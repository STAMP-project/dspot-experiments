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


import WebService.Action;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualitygate.QGateWithOrgDto;
import org.sonar.db.qualitygate.QualityGateConditionDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.Qualitygates.CreateConditionResponse;


@RunWith(DataProviderRunner.class)
public class CreateConditionActionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create();

    private TestDefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private CreateConditionAction underTest = new CreateConditionAction(dbClient, new org.sonar.server.qualitygate.QualityGateConditionsUpdater(dbClient), new QualityGatesWsSupport(dbClient, userSession, defaultOrganizationProvider));

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void create_error_condition() {
        OrganizationDto organization = db.organizations().insert();
        logInAsQualityGateAdmin(organization);
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization);
        MetricDto metric = insertMetric();
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_GATE_ID, qualityGate.getId().toString()).setParam(QualityGatesWsParameters.PARAM_METRIC, metric.getKey()).setParam(QualityGatesWsParameters.PARAM_OPERATOR, "LT").setParam(QualityGatesWsParameters.PARAM_ERROR, "90").setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).execute();
        assertCondition(qualityGate, metric, "LT", "90");
    }

    @Test
    public void create_condition_over_leak_period() {
        OrganizationDto organization = db.organizations().insert();
        logInAsQualityGateAdmin(organization);
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization);
        MetricDto metric = insertMetric();
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_GATE_ID, qualityGate.getId().toString()).setParam(QualityGatesWsParameters.PARAM_METRIC, metric.getKey()).setParam(QualityGatesWsParameters.PARAM_OPERATOR, "LT").setParam(QualityGatesWsParameters.PARAM_ERROR, "90").setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).execute();
        assertCondition(qualityGate, metric, "LT", "90");
    }

    @Test
    public void default_organization_is_used_when_no_organization_parameter() {
        logInAsQualityGateAdmin(db.getDefaultOrganization());
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(db.getDefaultOrganization());
        OrganizationDto otherOrganization = db.organizations().insert();
        QGateWithOrgDto otherQualityGate = db.qualityGates().insertQualityGate(otherOrganization);
        MetricDto metric = insertMetric();
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_GATE_ID, qualityGate.getId().toString()).setParam(QualityGatesWsParameters.PARAM_METRIC, metric.getKey()).setParam(QualityGatesWsParameters.PARAM_OPERATOR, "LT").setParam(QualityGatesWsParameters.PARAM_ERROR, "10").execute();
        assertCondition(qualityGate, metric, "LT", "10");
    }

    @Test
    public void fail_to_update_built_in_quality_gate() {
        OrganizationDto organization = db.organizations().insert();
        logInAsQualityGateAdmin(organization);
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization, ( qg) -> qg.setBuiltIn(true));
        MetricDto metric = insertMetric();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Operation forbidden for built-in Quality Gate '%s'", qualityGate.getName()));
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_GATE_ID, qualityGate.getId().toString()).setParam(QualityGatesWsParameters.PARAM_METRIC, metric.getKey()).setParam(QualityGatesWsParameters.PARAM_OPERATOR, "LT").setParam(QualityGatesWsParameters.PARAM_ERROR, "90").setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).execute();
    }

    @Test
    public void fail_with_unknown_operator() {
        OrganizationDto organization = db.organizations().insert();
        logInAsQualityGateAdmin(organization);
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization);
        MetricDto metric = db.measures().insertMetric(( m) -> m.setValueType(INT.name()).setHidden(false).setDirection(0));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value of parameter 'op' (ABC) must be one of: [LT, GT]");
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_GATE_ID, qualityGate.getId().toString()).setParam(QualityGatesWsParameters.PARAM_METRIC, metric.getKey()).setParam(QualityGatesWsParameters.PARAM_OPERATOR, "ABC").setParam(QualityGatesWsParameters.PARAM_ERROR, "90").setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).execute();
    }

    @Test
    public void test_response() {
        OrganizationDto organization = db.organizations().insert();
        logInAsQualityGateAdmin(organization);
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization);
        MetricDto metric = insertMetric();
        CreateConditionResponse response = ws.newRequest().setParam(QualityGatesWsParameters.PARAM_GATE_ID, qualityGate.getId().toString()).setParam(QualityGatesWsParameters.PARAM_METRIC, metric.getKey()).setParam(QualityGatesWsParameters.PARAM_OPERATOR, "LT").setParam(QualityGatesWsParameters.PARAM_ERROR, "45").setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).executeProtobuf(CreateConditionResponse.class);
        QualityGateConditionDto condition = new java.util.ArrayList(dbClient.gateConditionDao().selectForQualityGate(dbSession, qualityGate.getId())).get(0);
        assertThat(response.getId()).isEqualTo(condition.getId());
        assertThat(response.getMetric()).isEqualTo(metric.getKey());
        assertThat(response.getOp()).isEqualTo("LT");
        assertThat(response.getError()).isEqualTo("45");
    }

    @Test
    public void throw_ForbiddenException_if_not_gate_administrator() {
        OrganizationDto organization = db.organizations().insert();
        QGateWithOrgDto qualityGate = db.qualityGates().insertQualityGate(organization);
        MetricDto metric = insertMetric();
        userSession.logIn();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        ws.newRequest().setParam(QualityGatesWsParameters.PARAM_GATE_ID, qualityGate.getId().toString()).setParam(QualityGatesWsParameters.PARAM_METRIC, metric.getKey()).setParam(QualityGatesWsParameters.PARAM_OPERATOR, "LT").setParam(QualityGatesWsParameters.PARAM_ERROR, "90").setParam(QualityGatesWsParameters.PARAM_ORGANIZATION, organization.getKey()).execute();
    }

    @Test
    public void test_ws_definition() {
        WebService.Action action = ws.getDef();
        assertThat(action).isNotNull();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.isPost()).isTrue();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).extracting(WebService.Param::key, WebService.Param::isRequired).containsExactlyInAnyOrder(tuple("gateId", true), tuple("metric", true), tuple("error", true), tuple("op", false), tuple("organization", false));
    }
}

