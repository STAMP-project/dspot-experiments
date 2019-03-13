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


import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbTester;
import org.sonar.server.ws.WsTester;


public class RepositoriesActionTest {
    private static final String EMPTY_JSON_RESPONSE = "{\"repositories\":[]}";

    private WsTester wsTester;

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Test
    public void should_list_repositories() throws Exception {
        newRequest().execute().assertJson(this.getClass(), "repositories.json");
        newRequest().setParam("language", "xoo").execute().assertJson(this.getClass(), "repositories_xoo.json");
        newRequest().setParam("language", "ws").execute().assertJson(this.getClass(), "repositories_ws.json");
    }

    @Test
    public void filter_repositories_by_name() throws Exception {
        newRequest().setParam("q", "common").execute().assertJson(this.getClass(), "repositories_common.json");
        newRequest().setParam("q", "squid").execute().assertJson(this.getClass(), "repositories_squid.json");
        newRequest().setParam("q", "sonar").execute().assertJson(this.getClass(), "repositories_sonar.json");
    }

    @Test
    public void do_not_consider_query_as_regexp_when_filtering_repositories_by_name() throws Exception {
        // invalid regexp : do not fail. Query is not a regexp.
        newRequest().setParam("q", "[").execute().assertJson(RepositoriesActionTest.EMPTY_JSON_RESPONSE);
        // this is not the "match all" regexp
        newRequest().setParam("q", ".*").execute().assertJson(RepositoriesActionTest.EMPTY_JSON_RESPONSE);
    }
}

