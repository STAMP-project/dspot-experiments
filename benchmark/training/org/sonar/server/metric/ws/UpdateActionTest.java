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
import org.sonar.db.metric.MetricDto;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.ServerException;
import org.sonar.server.exceptions.UnauthorizedException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;


public class UpdateActionTest {
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
    public void update_all_fields() throws Exception {
        int id = insertMetric(newDefaultMetric());
        newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).setParam(UpdateAction.PARAM_KEY, "another-key").setParam(UpdateAction.PARAM_NAME, "another-name").setParam(UpdateAction.PARAM_TYPE, BOOL.name()).setParam(UpdateAction.PARAM_DOMAIN, "another-domain").setParam(UpdateAction.PARAM_DESCRIPTION, "another-description").execute();
        dbSession.commit();
        MetricDto result = dbClient.metricDao().selectById(dbSession, id);
        assertThat(result.getKey()).isEqualTo("another-key");
        assertThat(result.getShortName()).isEqualTo("another-name");
        assertThat(result.getValueType()).isEqualTo(BOOL.name());
        assertThat(result.getDomain()).isEqualTo("another-domain");
        assertThat(result.getDescription()).isEqualTo("another-description");
    }

    @Test
    public void update_one_field() throws Exception {
        int id = insertMetric(newDefaultMetric());
        dbClient.customMeasureDao().insert(dbSession, newCustomMeasureDto().setMetricId(id));
        dbSession.commit();
        newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).setParam(UpdateAction.PARAM_DESCRIPTION, "another-description").execute();
        dbSession.commit();
        MetricDto result = dbClient.metricDao().selectById(dbSession, id);
        assertThat(result.getKey()).isEqualTo(UpdateActionTest.DEFAULT_KEY);
        assertThat(result.getShortName()).isEqualTo(UpdateActionTest.DEFAULT_NAME);
        assertThat(result.getValueType()).isEqualTo(UpdateActionTest.DEFAULT_TYPE);
        assertThat(result.getDomain()).isEqualTo(UpdateActionTest.DEFAULT_DOMAIN);
        assertThat(result.getDescription()).isEqualTo("another-description");
    }

    @Test
    public void update_return_the_full_object_with_id() throws Exception {
        int id = insertMetric(newDefaultMetric().setDescription("another-description"));
        WsTester.Result requestResult = newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).setParam(UpdateAction.PARAM_DESCRIPTION, UpdateActionTest.DEFAULT_DESCRIPTION).execute();
        dbSession.commit();
        requestResult.assertJson(getClass(), "metric.json");
        assertThat(requestResult.outputAsString()).matches(((".*\"id\"\\s*:\\s*\"" + id) + "\".*"));
    }

    @Test
    public void fail_when_changing_key_for_an_existing_one() throws Exception {
        expectedException.expect(ServerException.class);
        expectedException.expectMessage("The key 'metric-key' is already used by an existing metric.");
        insertMetric(newDefaultMetric().setKey("metric-key"));
        int id = insertMetric(newDefaultMetric().setKey("another-key"));
        newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).setParam(UpdateAction.PARAM_KEY, "metric-key").execute();
    }

    @Test
    public void fail_when_metric_not_in_db() throws Exception {
        expectedException.expect(ServerException.class);
        newRequest().setParam(UpdateAction.PARAM_ID, "42").execute();
    }

    @Test
    public void fail_when_metric_is_deactivated() throws Exception {
        expectedException.expect(ServerException.class);
        int id = insertMetric(newDefaultMetric().setEnabled(false));
        newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).execute();
    }

    @Test
    public void fail_when_metric_is_not_custom() throws Exception {
        expectedException.expect(ServerException.class);
        int id = insertMetric(newDefaultMetric().setUserManaged(false));
        newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).execute();
    }

    @Test
    public void fail_when_custom_measures_and_type_changed() throws Exception {
        expectedException.expect(ServerException.class);
        int id = insertMetric(newDefaultMetric());
        dbClient.customMeasureDao().insert(dbSession, newCustomMeasureDto().setMetricId(id));
        dbSession.commit();
        newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).setParam(UpdateAction.PARAM_TYPE, BOOL.name()).execute();
    }

    @Test
    public void fail_when_no_id() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        newRequest().execute();
    }

    @Test
    public void throw_ForbiddenException_if_not_system_administrator() throws Exception {
        userSessionRule.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        newRequest().execute();
    }

    @Test
    public void throw_UnauthorizedException_if_not_logged_in() throws Exception {
        userSessionRule.anonymous();
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage("Authentication is required");
        newRequest().execute();
    }

    @Test
    public void fail_when_metric_key_is_not_well_formatted() throws Exception {
        int id = insertMetric(newDefaultMetric());
        dbClient.customMeasureDao().insert(dbSession, newCustomMeasureDto().setMetricId(id));
        dbSession.commit();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Malformed metric key 'not well formatted key'. Allowed characters are alphanumeric, '-', '_', with at least one non-digit.");
        newRequest().setParam(UpdateAction.PARAM_ID, String.valueOf(id)).setParam(UpdateAction.PARAM_KEY, "not well formatted key").execute();
    }
}

