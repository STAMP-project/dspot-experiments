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
package org.sonar.server.measure.index;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.es.EsTester;
import org.sonar.server.measure.index.ProjectMeasuresQuery.MetricCriterion;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.permission.index.WebAuthorizationTypeSupport;
import org.sonar.server.tester.UserSessionRule;


public class ProjectMeasuresIndexTextSearchTest {
    private static final String NCLOC = "ncloc";

    private static final OrganizationDto ORG = OrganizationTesting.newOrganizationDto();

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private ProjectMeasuresIndexer projectMeasureIndexer = new ProjectMeasuresIndexer(null, es.client());

    private PermissionIndexerTester authorizationIndexer = new PermissionIndexerTester(es, projectMeasureIndexer);

    private ProjectMeasuresIndex underTest = new ProjectMeasuresIndex(es.client(), new WebAuthorizationTypeSupport(userSession), System2.INSTANCE);

    @Test
    public void match_exact_case_insensitive_name() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("Apache Struts")), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sonarqube").setName("SonarQube")));
        assertTextQueryResults("Apache Struts", "struts");
        assertTextQueryResults("APACHE STRUTS", "struts");
        assertTextQueryResults("APACHE struTS", "struts");
    }

    @Test
    public void match_from_sub_name() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("Apache Struts")));
        assertTextQueryResults("truts", "struts");
        assertTextQueryResults("pache", "struts");
        assertTextQueryResults("apach", "struts");
        assertTextQueryResults("che stru", "struts");
    }

    @Test
    public void match_name_with_dot() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("Apache.Struts")));
        assertTextQueryResults("apache struts", "struts");
    }

    @Test
    public void match_partial_name() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("XstrutsxXjavax")));
        assertTextQueryResults("struts java", "struts");
    }

    @Test
    public void match_partial_name_prefix_word1() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("MyStruts.java")));
        assertTextQueryResults("struts java", "struts");
    }

    @Test
    public void match_partial_name_suffix_word1() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("StrutsObject.java")));
        assertTextQueryResults("struts java", "struts");
    }

    @Test
    public void match_partial_name_prefix_word2() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("MyStruts.xjava")));
        assertTextQueryResults("struts java", "struts");
    }

    @Test
    public void match_partial_name_suffix_word2() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("MyStrutsObject.xjavax")));
        assertTextQueryResults("struts java", "struts");
    }

    @Test
    public void match_subset_of_document_terms() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("Some.Struts.Project.java.old")));
        assertTextQueryResults("struts java", "struts");
    }

    @Test
    public void match_partial_match_prefix_and_suffix_everywhere() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("MyStruts.javax")));
        assertTextQueryResults("struts java", "struts");
    }

    @Test
    public void ignore_empty_words() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("Struts")));
        assertTextQueryResults("            struts   \n     \n\n", "struts");
    }

    @Test
    public void match_name_from_prefix() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("Apache Struts")));
        assertTextQueryResults("apach", "struts");
        assertTextQueryResults("ApA", "struts");
        assertTextQueryResults("AP", "struts");
    }

    @Test
    public void match_name_from_two_words() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project").setName("ApacheStrutsFoundation")));
        assertTextQueryResults("apache struts", "project");
        assertTextQueryResults("struts apache", "project");
        // Only one word is matching
        assertNoResults("apache plugin");
        assertNoResults("project struts");
    }

    @Test
    public void match_long_name() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project1").setName("LongNameLongNameLongNameLongNameSonarQube")), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project2").setName("LongNameLongNameLongNameLongNameSonarQubeX")));
        assertTextQueryResults("LongNameLongNameLongNameLongNameSonarQube", "project1", "project2");
    }

    @Test
    public void match_name_with_two_characters() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("struts").setName("Apache Struts")));
        assertTextQueryResults("st", "struts");
        assertTextQueryResults("tr", "struts");
    }

    @Test
    public void match_exact_case_insensitive_key() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project1").setName("Windows").setDbKey("project1")), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project2").setName("apachee").setDbKey("project2")));
        assertTextQueryResults("project1", "project1");
        assertTextQueryResults("PROJECT1", "project1");
        assertTextQueryResults("pRoJecT1", "project1");
    }

    @Test
    public void match_key_with_dot() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sonarqube").setName("SonarQube").setDbKey("org.sonarqube")), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sq").setName("SQ").setDbKey("sonarqube")));
        assertTextQueryResults("org.sonarqube", "sonarqube");
        assertNoResults("orgsonarqube");
        assertNoResults("org-sonarqube");
        assertNoResults("org:sonarqube");
        assertNoResults("org sonarqube");
    }

    @Test
    public void match_key_with_dash() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sonarqube").setName("SonarQube").setDbKey("org-sonarqube")), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sq").setName("SQ").setDbKey("sonarqube")));
        assertTextQueryResults("org-sonarqube", "sonarqube");
        assertNoResults("orgsonarqube");
        assertNoResults("org.sonarqube");
        assertNoResults("org:sonarqube");
        assertNoResults("org sonarqube");
    }

    @Test
    public void match_key_with_colon() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sonarqube").setName("SonarQube").setDbKey("org:sonarqube")), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sq").setName("SQ").setDbKey("sonarqube")));
        assertTextQueryResults("org:sonarqube", "sonarqube");
        assertNoResults("orgsonarqube");
        assertNoResults("org-sonarqube");
        assertNoResults("org_sonarqube");
        assertNoResults("org sonarqube");
    }

    @Test
    public void match_key_having_all_special_characters() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("sonarqube").setName("SonarQube").setDbKey("org.sonarqube:sonar-s?rv?r_?")));
        assertTextQueryResults("org.sonarqube:sonar-s?rv?r_?", "sonarqube");
    }

    @Test
    public void does_not_match_partial_key() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project").setName("some name").setDbKey("theKey")));
        assertNoResults("theke");
        assertNoResults("hekey");
    }

    @Test
    public void facets_take_into_account_text_search() {
        // docs with ncloc<1K
        // docs with ncloc>=1K and ncloc<10K
        // docs with ncloc>=100K and ncloc<500K
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setName("Windows").setDbKey("project1"), ProjectMeasuresIndexTextSearchTest.NCLOC, 0.0), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setName("apachee").setDbKey("project2"), ProjectMeasuresIndexTextSearchTest.NCLOC, 999.0), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setName("Apache").setDbKey("project3"), ProjectMeasuresIndexTextSearchTest.NCLOC, 1000.0), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setName("Apache Foundation").setDbKey("project4"), ProjectMeasuresIndexTextSearchTest.NCLOC, 100000.0));
        assertNclocFacet(new ProjectMeasuresQuery().setQueryText("apache"), 1L, 1L, 0L, 1L, 0L);
        assertNclocFacet(new ProjectMeasuresQuery().setQueryText("PAch"), 1L, 1L, 0L, 1L, 0L);
        assertNclocFacet(new ProjectMeasuresQuery().setQueryText("apache foundation"), 0L, 0L, 0L, 1L, 0L);
        assertNclocFacet(new ProjectMeasuresQuery().setQueryText("project3"), 0L, 1L, 0L, 0L, 0L);
        assertNclocFacet(new ProjectMeasuresQuery().setQueryText("project"), 0L, 0L, 0L, 0L, 0L);
    }

    @Test
    public void filter_by_metric_take_into_account_text_search() {
        index(ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project1").setName("Windows").setDbKey("project1"), ProjectMeasuresIndexTextSearchTest.NCLOC, 30000.0), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project2").setName("apachee").setDbKey("project2"), ProjectMeasuresIndexTextSearchTest.NCLOC, 40000.0), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project3").setName("Apache").setDbKey("project3"), ProjectMeasuresIndexTextSearchTest.NCLOC, 50000.0), ProjectMeasuresIndexTextSearchTest.newDoc(newPrivateProjectDto(ProjectMeasuresIndexTextSearchTest.ORG).setUuid("project4").setName("Apache").setDbKey("project4"), ProjectMeasuresIndexTextSearchTest.NCLOC, 60000.0));
        assertResults(new ProjectMeasuresQuery().setQueryText("apache").addMetricCriterion(MetricCriterion.create(ProjectMeasuresIndexTextSearchTest.NCLOC, Operator.GT, 20000.0)), "project3", "project4", "project2");
        assertResults(new ProjectMeasuresQuery().setQueryText("apache").addMetricCriterion(MetricCriterion.create(ProjectMeasuresIndexTextSearchTest.NCLOC, Operator.LT, 55000.0)), "project3", "project2");
        assertResults(new ProjectMeasuresQuery().setQueryText("PAC").addMetricCriterion(MetricCriterion.create(ProjectMeasuresIndexTextSearchTest.NCLOC, Operator.LT, 55000.0)), "project3", "project2");
        assertResults(new ProjectMeasuresQuery().setQueryText("apachee").addMetricCriterion(MetricCriterion.create(ProjectMeasuresIndexTextSearchTest.NCLOC, Operator.GT, 30000.0)), "project2");
        assertResults(new ProjectMeasuresQuery().setQueryText("unknown").addMetricCriterion(MetricCriterion.create(ProjectMeasuresIndexTextSearchTest.NCLOC, Operator.GT, 20000.0)));
    }
}

