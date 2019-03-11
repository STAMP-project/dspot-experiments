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
package org.sonar.server.projecttag.ws;


import WebService.Action;
import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.es.EsTester;
import org.sonar.server.measure.index.ProjectMeasuresIndex;
import org.sonar.server.measure.index.ProjectMeasuresIndexer;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.ProjectTags.SearchResponse;


public class SearchActionTest {
    private static final OrganizationDto ORG = OrganizationTesting.newOrganizationDto();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private ProjectMeasuresIndexer projectMeasureIndexer = new ProjectMeasuresIndexer(null, es.client());

    private PermissionIndexerTester authorizationIndexer = new PermissionIndexerTester(es, projectMeasureIndexer);

    private ProjectMeasuresIndex index = new ProjectMeasuresIndex(es.client(), new WebAuthorizationTypeSupport(userSession), System2.INSTANCE);

    private WsActionTester ws = new WsActionTester(new SearchAction(index));

    @Test
    public void json_example() {
        index(SearchActionTest.newDoc().setTags(Lists.newArrayList("official", "offshore", "playoff")));
        String result = ws.newRequest().execute().getInput();
        assertJson(ws.getDef().responseExampleAsString()).isSimilarTo(result);
    }

    @Test
    public void search_by_query_and_page_size() {
        index(SearchActionTest.newDoc().setTags(Lists.newArrayList("whatever-tag", "official", "offshore", "yet-another-tag", "playoff")), SearchActionTest.newDoc().setTags(Lists.newArrayList("offshore", "playoff")));
        SearchResponse result = call("off", 2);
        assertThat(result.getTagsList()).containsOnly("offshore", "official");
    }

    @Test
    public void search_in_lexical_order() {
        index(SearchActionTest.newDoc().setTags(Lists.newArrayList("offshore", "official", "Playoff")));
        SearchResponse result = call(null, null);
        assertThat(result.getTagsList()).containsExactly("Playoff", "official", "offshore");
    }

    @Test
    public void definition() {
        WebService.Action definition = ws.getDef();
        assertThat(definition.key()).isEqualTo("search");
        assertThat(definition.isInternal()).isFalse();
        assertThat(definition.isPost()).isFalse();
        assertThat(definition.responseExampleAsString()).isNotEmpty();
        assertThat(definition.since()).isEqualTo("6.4");
        assertThat(definition.params()).extracting(WebService.Param::key).containsOnly("q", "ps");
    }
}

