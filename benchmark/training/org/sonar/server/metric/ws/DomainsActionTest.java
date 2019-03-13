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


import MetricsWs.ENDPOINT;
import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.server.ws.WsTester;
import org.sonar.test.JsonAssert;


public class DomainsActionTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    WsTester ws;

    DbClient dbClient;

    DbSession dbSession;

    @Test
    public void json_example_validated() throws Exception {
        insertNewMetricDto(newEnabledMetric("API Compatibility"));
        insertNewMetricDto(newEnabledMetric("Issues"));
        insertNewMetricDto(newEnabledMetric("Rules"));
        insertNewMetricDto(newEnabledMetric("Tests"));
        insertNewMetricDto(newEnabledMetric("Documentation"));
        insertNewMetricDto(newEnabledMetric(null));
        insertNewMetricDto(newEnabledMetric(""));
        insertNewMetricDto(newMetricDto().setDomain("Domain of Deactivated Metric").setEnabled(false));
        WsTester.Result result = ws.newGetRequest(ENDPOINT, "domains").execute();
        JsonAssert.assertJson(result.outputAsString()).isSimilarTo(getClass().getResource("example-domains.json"));
    }
}

