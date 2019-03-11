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


import Param.FIELDS;
import Param.PAGE;
import Param.PAGE_SIZE;
import System2.INSTANCE;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.server.ws.WsTester;


public class SearchActionTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    DbClient dbClient = db.getDbClient();

    final DbSession dbSession = db.getSession();

    WsTester ws;

    @Test
    public void search_metrics_in_database() throws Exception {
        insertNewCustomMetric("1", "2", "3");
        WsTester.Result result = newRequest().execute();
        result.assertJson(getClass(), "search_metrics.json");
    }

    @Test
    public void search_metrics_ordered_by_name_case_insensitive() throws Exception {
        insertNewCustomMetric("3", "1", "2");
        String firstResult = newRequest().setParam(PAGE, "1").setParam(PAGE_SIZE, "1").execute().outputAsString();
        String secondResult = newRequest().setParam(PAGE, "2").setParam(PAGE_SIZE, "1").execute().outputAsString();
        String thirdResult = newRequest().setParam(PAGE, "3").setParam(PAGE_SIZE, "1").execute().outputAsString();
        assertThat(firstResult).contains("custom-key-1").doesNotContain("custom-key-2").doesNotContain("custom-key-3");
        assertThat(secondResult).contains("custom-key-2").doesNotContain("custom-key-1").doesNotContain("custom-key-3");
        assertThat(thirdResult).contains("custom-key-3").doesNotContain("custom-key-1").doesNotContain("custom-key-2");
    }

    @Test
    public void search_metrics_with_pagination() throws Exception {
        insertNewCustomMetric("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        WsTester.Result result = newRequest().setParam(PAGE, "3").setParam(PAGE_SIZE, "4").execute();
        assertThat(StringUtils.countMatches(result.outputAsString(), "custom-key")).isEqualTo(2);
    }

    @Test
    public void list_metric_with_is_custom_true() throws Exception {
        insertNewCustomMetric("1", "2");
        insertNewNonCustomMetric("3");
        String result = newRequest().setParam(SearchAction.PARAM_IS_CUSTOM, "true").execute().outputAsString();
        assertThat(result).contains("custom-key-1", "custom-key-2").doesNotContain("non-custom-key-3");
    }

    @Test
    public void list_metric_with_is_custom_false() throws Exception {
        insertNewCustomMetric("1", "2");
        insertNewNonCustomMetric("3");
        String result = newRequest().setParam(SearchAction.PARAM_IS_CUSTOM, "false").execute().outputAsString();
        assertThat(result).doesNotContain("custom-key-1").doesNotContain("custom-key-2").contains("non-custom-key-3");
    }

    @Test
    public void list_metric_with_chosen_fields() throws Exception {
        insertNewCustomMetric("1");
        String result = newRequest().setParam(FIELDS, "name").execute().outputAsString();
        assertThat(result).contains("id", "key", "name", "type").doesNotContain("domain").doesNotContain("description");
    }
}

