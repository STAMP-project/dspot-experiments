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
package org.sonar.db.purge;


import System2.INSTANCE;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;


public class PurgeCommandsTest {
    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private PurgeProfiler profiler = new PurgeProfiler();

    /**
     * Test that SQL queries execution do not fail with a huge number of parameter
     */
    @Test
    public void should_not_fail_when_deleting_huge_number_of_analyses() {
        new PurgeCommands(dbTester.getSession(), profiler).deleteAnalyses(getHugeNumberOfIdUuidPairs());
        // The goal of this test is only to check that the query do no fail, not to check result
    }

    /**
     * Test that all related data is purged.
     */
    @Test
    public void shouldPurgeAnalysis() {
        prepareDbUnit(getClass(), "shouldPurgeAnalysis.xml");
        new PurgeCommands(dbTester.getSession(), profiler).purgeAnalyses(Collections.singletonList(new IdUuidPair(1, "u1")));
        dbTester.assertDbUnit(getClass(), "shouldPurgeAnalysis-result.xml", "snapshots", "analysis_properties", "project_measures", "duplications_index", "events");
    }

    @Test
    public void delete_wasted_measures_when_purging_analysis() {
        prepareDbUnit(getClass(), "shouldDeleteWastedMeasuresWhenPurgingAnalysis.xml");
        new PurgeCommands(dbTester.getSession(), profiler).purgeAnalyses(Collections.singletonList(new IdUuidPair(1, "u1")));
        assertDbUnit(getClass(), "shouldDeleteWastedMeasuresWhenPurgingAnalysis-result.xml", "project_measures");
    }

    /**
     * Test that SQL queries execution do not fail with a huge number of parameter
     */
    @Test
    public void should_not_fail_when_purging_huge_number_of_analyses() {
        new PurgeCommands(dbTester.getSession(), profiler).purgeAnalyses(getHugeNumberOfIdUuidPairs());
        // The goal of this test is only to check that the query do no fail, not to check result
    }

    @Test
    public void shouldDeleteComponentsAndChildrenTables() {
        prepareDbUnit(getClass(), "shouldDeleteResource.xml");
        PurgeCommands purgeCommands = new PurgeCommands(dbTester.getSession(), profiler);
        purgeCommands.deleteComponents("uuid_1");
        assertThat(dbTester.countRowsOfTable("projects")).isZero();
        assertThat(dbTester.countRowsOfTable("snapshots")).isEqualTo(1);
        assertThat(dbTester.countRowsOfTable("events")).isEqualTo(3);
        assertThat(dbTester.countRowsOfTable("issues")).isEqualTo(1);
        assertThat(dbTester.countRowsOfTable("issue_changes")).isEqualTo(1);
    }

    @Test
    public void shouldDeleteAnalyses() {
        prepareDbUnit(getClass(), "shouldDeleteResource.xml");
        PurgeCommands purgeCommands = new PurgeCommands(dbTester.getSession(), profiler);
        purgeCommands.deleteAnalyses("uuid_1");
        assertThat(dbTester.countRowsOfTable("projects")).isEqualTo(1);
        assertThat(dbTester.countRowsOfTable("snapshots")).isZero();
        assertThat(dbTester.countRowsOfTable("analysis_properties")).isZero();
        assertThat(dbTester.countRowsOfTable("events")).isZero();
        assertThat(dbTester.countRowsOfTable("issues")).isEqualTo(1);
        assertThat(dbTester.countRowsOfTable("issue_changes")).isEqualTo(1);
    }

    @Test
    public void shouldDeleteIssuesAndIssueChanges() {
        prepareDbUnit(getClass(), "shouldDeleteResource.xml");
        PurgeCommands purgeCommands = new PurgeCommands(dbTester.getSession(), profiler);
        purgeCommands.deleteIssues("uuid_1");
        assertThat(dbTester.countRowsOfTable("projects")).isEqualTo(1);
        assertThat(dbTester.countRowsOfTable("snapshots")).isEqualTo(1);
        assertThat(dbTester.countRowsOfTable("events")).isEqualTo(3);
        assertThat(dbTester.countRowsOfTable("issues")).isZero();
        assertThat(dbTester.countRowsOfTable("issue_changes")).isZero();
    }

    @Test
    public void deletePermissions_deletes_permissions_of_public_project() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicProject(organization);
        addPermissions(organization, project);
        PurgeCommands purgeCommands = new PurgeCommands(dbTester.getSession(), profiler);
        purgeCommands.deletePermissions(project.getId());
        assertThat(dbTester.countRowsOfTable("group_roles")).isEqualTo(2);
        assertThat(dbTester.countRowsOfTable("user_roles")).isEqualTo(1);
    }

    @Test
    public void deletePermissions_deletes_permissions_of_private_project() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPrivateProject(organization);
        addPermissions(organization, project);
        PurgeCommands purgeCommands = new PurgeCommands(dbTester.getSession(), profiler);
        purgeCommands.deletePermissions(project.getId());
        assertThat(dbTester.countRowsOfTable("group_roles")).isEqualTo(1);
        assertThat(dbTester.countRowsOfTable("user_roles")).isEqualTo(1);
    }

    @Test
    public void deletePermissions_deletes_permissions_of_view() {
        OrganizationDto organization = dbTester.organizations().insert();
        ComponentDto project = dbTester.components().insertPublicPortfolio(organization);
        addPermissions(organization, project);
        PurgeCommands purgeCommands = new PurgeCommands(dbTester.getSession(), profiler);
        purgeCommands.deletePermissions(project.getId());
        assertThat(dbTester.countRowsOfTable("group_roles")).isEqualTo(2);
        assertThat(dbTester.countRowsOfTable("user_roles")).isEqualTo(1);
    }
}

