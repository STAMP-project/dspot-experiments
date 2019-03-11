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
package org.sonar.server.metric.ws;


import System2.INSTANCE;
import ValueType.BOOL;
import ValueType.INT;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.measure.custom.CustomMeasureTesting;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.metric.MetricTesting;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.ServerException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;


public class CreateActionTest {
    private static final String DEFAULT_KEY = "custom-metric-key";

    private static final String DEFAULT_NAME = "custom-metric-name";

    private static final String DEFAULT_DOMAIN = "custom-metric-domain";

    private static final String DEFAULT_DESCRIPTION = "custom-metric-description";

    private static final String DEFAULT_TYPE = INT.name();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private final DbSession dbSession = db.getSession();

    private WsTester ws;

    @Test
    public void insert_new_minimalist_metric() throws Exception {
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
        MetricDto metric = dbClient.metricDao().selectByKey(dbSession, CreateActionTest.DEFAULT_KEY);
        assertThat(metric.getKey()).isEqualTo(CreateActionTest.DEFAULT_KEY);
        assertThat(metric.getShortName()).isEqualTo(CreateActionTest.DEFAULT_NAME);
        assertThat(metric.getValueType()).isEqualTo(CreateActionTest.DEFAULT_TYPE);
        assertThat(metric.getDescription()).isNull();
        assertThat(metric.getDomain()).isNull();
        assertThat(metric.isUserManaged()).isTrue();
        assertThat(metric.isEnabled()).isTrue();
        assertThat(metric.getDirection()).isEqualTo(0);
        assertThat(metric.isQualitative()).isFalse();
    }

    @Test
    public void insert_new_full_metric() throws Exception {
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).setParam(CreateAction.PARAM_DOMAIN, CreateActionTest.DEFAULT_DOMAIN).setParam(CreateAction.PARAM_DESCRIPTION, CreateActionTest.DEFAULT_DESCRIPTION).execute();
        MetricDto metric = dbClient.metricDao().selectByKey(dbSession, CreateActionTest.DEFAULT_KEY);
        assertThat(metric.getKey()).isEqualTo(CreateActionTest.DEFAULT_KEY);
        assertThat(metric.getDescription()).isEqualTo(CreateActionTest.DEFAULT_DESCRIPTION);
        assertThat(metric.getDomain()).isEqualTo(CreateActionTest.DEFAULT_DOMAIN);
    }

    @Test
    public void return_metric_with_id() throws Exception {
        WsTester.Result result = newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).setParam(CreateAction.PARAM_DOMAIN, CreateActionTest.DEFAULT_DOMAIN).setParam(CreateAction.PARAM_DESCRIPTION, CreateActionTest.DEFAULT_DESCRIPTION).execute();
        result.assertJson(getClass(), "metric.json");
        assertThat(result.outputAsString()).matches(".*\"id\"\\s*:\\s*\"\\w+\".*");
    }

    @Test
    public void update_existing_metric_when_custom_and_disabled() throws Exception {
        MetricDto metricInDb = MetricTesting.newMetricDto().setKey(CreateActionTest.DEFAULT_KEY).setValueType(BOOL.name()).setUserManaged(true).setEnabled(false);
        dbClient.metricDao().insert(dbSession, metricInDb);
        dbSession.commit();
        WsTester.Result result = newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).setParam(CreateAction.PARAM_DESCRIPTION, CreateActionTest.DEFAULT_DESCRIPTION).setParam(CreateAction.PARAM_DOMAIN, CreateActionTest.DEFAULT_DOMAIN).execute();
        result.assertJson(getClass(), "metric.json");
        result.outputAsString().matches((("\"id\"\\s*:\\s*\"" + (metricInDb.getId())) + "\""));
        MetricDto metricAfterWs = dbClient.metricDao().selectByKey(dbSession, CreateActionTest.DEFAULT_KEY);
        assertThat(metricAfterWs.getId()).isEqualTo(metricInDb.getId());
        assertThat(metricAfterWs.getDomain()).isEqualTo(CreateActionTest.DEFAULT_DOMAIN);
        assertThat(metricAfterWs.getDescription()).isEqualTo(CreateActionTest.DEFAULT_DESCRIPTION);
        assertThat(metricAfterWs.getValueType()).isEqualTo(CreateActionTest.DEFAULT_TYPE);
        assertThat(metricAfterWs.getShortName()).isEqualTo(CreateActionTest.DEFAULT_NAME);
    }

    @Test
    public void fail_when_existing_activated_metric_with_same_key() throws Exception {
        expectedException.expect(ServerException.class);
        dbClient.metricDao().insert(dbSession, MetricTesting.newMetricDto().setKey(CreateActionTest.DEFAULT_KEY).setValueType(CreateActionTest.DEFAULT_TYPE).setUserManaged(true).setEnabled(true));
        dbSession.commit();
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, "any-name").setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void fail_when_existing_non_custom_metric_with_same_key() throws Exception {
        expectedException.expect(ServerException.class);
        dbClient.metricDao().insert(dbSession, MetricTesting.newMetricDto().setKey(CreateActionTest.DEFAULT_KEY).setValueType(CreateActionTest.DEFAULT_TYPE).setUserManaged(false).setEnabled(false));
        dbSession.commit();
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, "any-name").setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void fail_when_metric_type_is_changed_and_associated_measures_exist() throws Exception {
        expectedException.expect(ServerException.class);
        MetricDto metric = MetricTesting.newMetricDto().setKey(CreateActionTest.DEFAULT_KEY).setValueType(BOOL.name()).setUserManaged(true).setEnabled(false);
        dbClient.metricDao().insert(dbSession, metric);
        dbClient.customMeasureDao().insert(dbSession, CustomMeasureTesting.newCustomMeasureDto().setMetricId(metric.getId()));
        dbSession.commit();
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, "any-name").setParam(CreateAction.PARAM_TYPE, INT.name()).execute();
    }

    @Test
    public void fail_when_missing_key() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void fail_when_missing_name() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void fail_when_missing_type() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).execute();
    }

    @Test
    public void throw_ForbiddenException_if_not_system_administrator() throws Exception {
        userSessionRule.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        newRequest().setParam(CreateAction.PARAM_KEY, "any-key").setParam(CreateAction.PARAM_NAME, "any-name").setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void throw_UnauthorizedException_if_not_logged_in() throws Exception {
        userSessionRule.anonymous();
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        newRequest().setParam(CreateAction.PARAM_KEY, "any-key").setParam(CreateAction.PARAM_NAME, "any-name").setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void fail_when_ill_formatted_key() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Malformed metric key '123:456'. Allowed characters are alphanumeric, '-', '_', with at least one non-digit.");
        newRequest().setParam(CreateAction.PARAM_KEY, "123:456").setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void fail_when_empty_name() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, "").setParam(CreateAction.PARAM_TYPE, CreateActionTest.DEFAULT_TYPE).execute();
    }

    @Test
    public void fail_when_empty_type() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        newRequest().setParam(CreateAction.PARAM_KEY, CreateActionTest.DEFAULT_KEY).setParam(CreateAction.PARAM_NAME, CreateActionTest.DEFAULT_NAME).setParam(CreateAction.PARAM_TYPE, "").execute();
    }
}

