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
package org.sonar.server.platform;


import ComponentIndexDefinition.INDEX_TYPE_COMPONENT;
import IssueIndexDefinition.INDEX_TYPE_ISSUE;
import ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES;
import RuleIndexDefinition.INDEX_TYPE_RULE;
import System2.INSTANCE;
import ViewIndexDefinition.INDEX_TYPE_VIEW;
import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbTester;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.issue.IssueDocTesting;
import org.sonar.server.measure.index.ProjectMeasuresDoc;
import org.sonar.server.view.index.ViewDoc;


public class BackendCleanupTest {
    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private BackendCleanup underTest = new BackendCleanup(es.client(), dbTester.getDbClient());

    private OrganizationDto organization;

    @Test
    public void clear_db() {
        dbTester.prepareDbUnit(getClass(), "shared.xml");
        underTest.clearDb();
        assertThat(dbTester.countRowsOfTable("projects")).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("snapshots")).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("rules")).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(0);
    }

    @Test
    public void clear_indexes() {
        es.putDocuments(INDEX_TYPE_ISSUE, IssueDocTesting.newDoc());
        es.putDocuments(INDEX_TYPE_RULE, BackendCleanupTest.newRuleDoc());
        es.putDocuments(INDEX_TYPE_COMPONENT, newComponentDoc());
        underTest.clearIndexes();
        assertThat(es.countDocuments(INDEX_TYPE_ISSUE)).isEqualTo(0);
        assertThat(es.countDocuments(INDEX_TYPE_COMPONENT)).isEqualTo(0);
    }

    @Test
    public void clear_all() {
        dbTester.prepareDbUnit(getClass(), "shared.xml");
        es.putDocuments(INDEX_TYPE_ISSUE, IssueDocTesting.newDoc());
        es.putDocuments(INDEX_TYPE_RULE, BackendCleanupTest.newRuleDoc());
        es.putDocuments(INDEX_TYPE_COMPONENT, newComponentDoc());
        underTest.clearAll();
        assertThat(es.countDocuments(INDEX_TYPE_ISSUE)).isEqualTo(0);
        assertThat(es.countDocuments(INDEX_TYPE_RULE)).isEqualTo(0);
        assertThat(es.countDocuments(INDEX_TYPE_COMPONENT)).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("projects")).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("snapshots")).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("rules")).isEqualTo(0);
        assertThat(dbTester.countRowsOfTable("properties")).isEqualTo(0);
    }

    @Test
    public void reset_data() {
        dbTester.prepareDbUnit(getClass(), "shared.xml");
        es.putDocuments(INDEX_TYPE_ISSUE, IssueDocTesting.newDoc());
        es.putDocuments(INDEX_TYPE_VIEW, new ViewDoc().setUuid("CDEF").setProjects(Lists.newArrayList("DEFG")));
        es.putDocuments(INDEX_TYPE_RULE, BackendCleanupTest.newRuleDoc());
        es.putDocuments(INDEX_TYPE_PROJECT_MEASURES, new ProjectMeasuresDoc().setId("PROJECT").setKey("Key").setName("Name"));
        es.putDocuments(INDEX_TYPE_COMPONENT, newComponentDoc());
        underTest.resetData();
        assertThat(dbTester.countRowsOfTable("projects")).isZero();
        assertThat(dbTester.countRowsOfTable("snapshots")).isZero();
        assertThat(dbTester.countRowsOfTable("properties")).isZero();
        assertThat(es.countDocuments(INDEX_TYPE_ISSUE)).isZero();
        assertThat(es.countDocuments(INDEX_TYPE_VIEW)).isZero();
        assertThat(es.countDocuments(INDEX_TYPE_PROJECT_MEASURES)).isZero();
        assertThat(es.countDocuments(INDEX_TYPE_COMPONENT)).isZero();
        // Rules should not be removed
        assertThat(dbTester.countRowsOfTable("rules")).isEqualTo(1);
        assertThat(es.countDocuments(INDEX_TYPE_RULE)).isEqualTo(1);
    }
}

