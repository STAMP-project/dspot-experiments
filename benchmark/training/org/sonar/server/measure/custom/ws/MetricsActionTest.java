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
package org.sonar.server.measure.custom.ws;


import Metric.ValueType.INT;
import MetricsAction.PARAM_PROJECT_ID;
import MetricsAction.PARAM_PROJECT_KEY;
import System2.INSTANCE;
import UserRole.ADMIN;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.measure.custom.CustomMeasureDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;


public class MetricsActionTest {
    private static final String DEFAULT_PROJECT_UUID = "project-uuid";

    private static final String DEFAULT_PROJECT_KEY = "project-key";

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private final DbClient dbClient = db.getDbClient();

    private final DbSession dbSession = db.getSession();

    private ComponentDto defaultProject;

    private WsTester ws;

    @Test
    public void list_metrics() throws Exception {
        insertCustomMetric("metric-key-1");
        insertCustomMetric("metric-key-2");
        insertCustomMetric("metric-key-3");
        String response = newRequest().outputAsString();
        assertThat(response).contains("metric-key-1", "metric-key-2", "metric-key-3");
    }

    @Test
    public void list_metrics_active_and_custom_only() throws Exception {
        insertCustomMetric("metric-key-1");
        dbClient.metricDao().insert(dbSession, newMetricDto().setEnabled(true).setUserManaged(false).setKey("metric-key-2"));
        dbClient.metricDao().insert(dbSession, newMetricDto().setEnabled(false).setUserManaged(true).setKey("metric-key-3"));
        dbSession.commit();
        String response = newRequest().outputAsString();
        assertThat(response).contains("metric-key-1").doesNotContain("metric-key-2").doesNotContain("metric-key-3");
    }

    @Test
    public void list_metrics_where_no_existing_custom_measure() throws Exception {
        MetricDto metric = insertCustomMetric("metric-key-1");
        insertCustomMetric("metric-key-2");
        insertProject("project-uuid-2", "project-key-2");
        CustomMeasureDto customMeasure = newCustomMeasureDto().setComponentUuid(defaultProject.uuid()).setMetricId(metric.getId());
        dbClient.customMeasureDao().insert(dbSession, customMeasure);
        dbSession.commit();
        String response = newRequest().outputAsString();
        assertThat(response).contains("metric-key-2").doesNotContain("metric-key-1");
    }

    @Test
    public void list_metrics_based_on_project_key() throws Exception {
        MetricDto metric = insertCustomMetric("metric-key-1");
        insertCustomMetric("metric-key-2");
        insertProject("project-uuid-2", "project-key-2");
        CustomMeasureDto customMeasure = newCustomMeasureDto().setComponentUuid(defaultProject.uuid()).setMetricId(metric.getId());
        dbClient.customMeasureDao().insert(dbSession, customMeasure);
        dbSession.commit();
        String response = ws.newGetRequest(CustomMeasuresWs.ENDPOINT, MetricsAction.ACTION).setParam(PARAM_PROJECT_KEY, MetricsActionTest.DEFAULT_PROJECT_KEY).execute().outputAsString();
        assertThat(response).contains("metric-key-2").doesNotContain("metric-key-1");
    }

    @Test
    public void list_metrics_as_a_project_admin() throws Exception {
        insertCustomMetric("metric-key-1");
        userSession.logIn("login").addProjectPermission(ADMIN, defaultProject);
        String response = newRequest().outputAsString();
        assertThat(response).contains("metric-key-1");
    }

    @Test
    public void response_with_correct_formatting() throws Exception {
        dbClient.metricDao().insert(dbSession, MetricsActionTest.newCustomMetric("custom-key-1").setShortName("custom-name-1").setDescription("custom-description-1").setDomain("custom-domain-1").setValueType(INT.name()).setDirection(1).setQualitative(false).setHidden(false));
        dbClient.metricDao().insert(dbSession, MetricsActionTest.newCustomMetric("custom-key-2").setShortName("custom-name-2").setDescription("custom-description-2").setDomain("custom-domain-2").setValueType(INT.name()).setDirection((-1)).setQualitative(true).setHidden(true));
        dbClient.metricDao().insert(dbSession, MetricsActionTest.newCustomMetric("custom-key-3").setShortName("custom-name-3").setDescription("custom-description-3").setDomain("custom-domain-3").setValueType(INT.name()).setDirection(0).setQualitative(false).setHidden(false));
        dbSession.commit();
        WsTester.Result response = ws.newGetRequest(CustomMeasuresWs.ENDPOINT, MetricsAction.ACTION).setParam(PARAM_PROJECT_ID, MetricsActionTest.DEFAULT_PROJECT_UUID).execute();
        response.assertJson(getClass(), "metrics.json");
    }

    @Test
    public void fail_if_project_id_nor_project_key_provided() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        ws.newGetRequest(CustomMeasuresWs.ENDPOINT, MetricsAction.ACTION).execute();
    }

    @Test
    public void fail_if_insufficient_privilege() throws Exception {
        expectedException.expect(ForbiddenException.class);
        userSession.logIn("login");
        insertCustomMetric("metric-key-1");
        newRequest();
    }
}

