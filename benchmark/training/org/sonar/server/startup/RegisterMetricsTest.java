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
package org.sonar.server.startup;


import System2.INSTANCE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;


public class RegisterMetricsTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    DbClient dbClient = dbTester.getDbClient();

    /**
     * Insert new metrics, including custom metrics
     */
    @Test
    public void insert_new_metrics() {
        dbTester.prepareDbUnit(getClass(), "insert_new_metrics.xml");
        Metric m1 = setDescription("desc1").setDirection(1).setQualitative(true).setDomain("domain1").setUserManaged(false).create();
        Metric custom = setDescription("This is a custom metric").setUserManaged(true).create();
        RegisterMetrics register = new RegisterMetrics(dbClient);
        register.register(Arrays.asList(m1, custom));
        dbTester.assertDbUnit(getClass(), "insert_new_metrics-result.xml", "metrics");
    }

    /**
     * Update existing metrics, except if custom metric
     */
    @Test
    public void update_non_custom_metrics() {
        dbTester.prepareDbUnit(getClass(), "update_non_custom_metrics.xml");
        RegisterMetrics register = new RegisterMetrics(dbClient);
        Metric m1 = setDescription("new description").setDirection((-1)).setQualitative(true).setDomain("new domain").setUserManaged(false).setDecimalScale(3).setHidden(true).create();
        Metric custom = setDescription("New description of custom metric").setUserManaged(true).create();
        register.register(Arrays.asList(m1, custom));
        dbTester.assertDbUnit(getClass(), "update_non_custom_metrics-result.xml", "metrics");
    }

    @Test
    public void disable_undefined_metrics() {
        dbTester.prepareDbUnit(getClass(), "disable_undefined_metrics.xml");
        RegisterMetrics register = new RegisterMetrics(dbClient);
        register.register(Collections.emptyList());
        dbTester.assertDbUnit(getClass(), "disable_undefined_metrics-result.xml", "metrics");
    }

    @Test
    public void enable_disabled_metrics() {
        dbTester.prepareDbUnit(getClass(), "enable_disabled_metric.xml");
        RegisterMetrics register = new RegisterMetrics(dbClient);
        Metric m1 = setDescription("new description").setDirection((-1)).setQualitative(true).setDomain("new domain").setUserManaged(false).setHidden(true).create();
        register.register(Arrays.asList(m1));
        dbTester.assertDbUnit(getClass(), "enable_disabled_metric-result.xml", "metrics");
    }

    @Test
    public void insert_core_metrics() {
        RegisterMetrics register = new RegisterMetrics(dbClient);
        register.start();
        assertThat(dbTester.countRowsOfTable("metrics")).isEqualTo(org.sonar.api.measures.CoreMetrics.getMetrics().size());
    }

    @Test(expected = IllegalStateException.class)
    public void fail_if_duplicated_plugin_metrics() {
        Metrics plugin1 = new RegisterMetricsTest.TestMetrics(create());
        Metrics plugin2 = new RegisterMetricsTest.TestMetrics(create());
        start();
    }

    @Test(expected = IllegalStateException.class)
    public void fail_if_plugin_duplicates_core_metric() {
        Metrics plugin = new RegisterMetricsTest.TestMetrics(create());
        start();
    }

    private class TestMetrics implements Metrics {
        private final List<Metric> metrics;

        public TestMetrics(Metric... metrics) {
            this.metrics = Arrays.asList(metrics);
        }

        @Override
        public List<Metric> getMetrics() {
            return metrics;
        }
    }
}

