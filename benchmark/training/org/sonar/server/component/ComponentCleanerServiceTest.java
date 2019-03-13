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
package org.sonar.server.component;


import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.resources.ResourceType;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.issue.IssueDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.webhook.WebhookDto;
import org.sonar.server.es.TestProjectIndexers;


public class ComponentCleanerServiceTest {
    private System2 system2 = System2.INSTANCE;

    @Rule
    public DbTester db = DbTester.create(system2);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private TestProjectIndexers projectIndexers = new TestProjectIndexers();

    private ResourceTypes mockResourceTypes = Mockito.mock(ResourceTypes.class);

    private ComponentCleanerService underTest = new ComponentCleanerService(dbClient, mockResourceTypes, projectIndexers);

    @Test
    public void delete_project_from_db_and_index() {
        ComponentCleanerServiceTest.DbData data1 = insertData();
        ComponentCleanerServiceTest.DbData data2 = insertData();
        underTest.delete(dbSession, data1.project);
        assertNotExists(data1);
        assertExists(data2);
    }

    @Test
    public void delete_list_of_projects_from_db_and_index() {
        ComponentCleanerServiceTest.DbData data1 = insertData();
        ComponentCleanerServiceTest.DbData data2 = insertData();
        ComponentCleanerServiceTest.DbData data3 = insertData();
        underTest.delete(dbSession, Arrays.asList(data1.project, data2.project));
        dbSession.commit();
        assertNotExists(data1);
        assertNotExists(data2);
        assertExists(data3);
    }

    @Test
    public void delete_branch() {
        ComponentCleanerServiceTest.DbData data1 = insertData();
        ComponentCleanerServiceTest.DbData data2 = insertData();
        ComponentCleanerServiceTest.DbData data3 = insertData();
        underTest.deleteBranch(dbSession, data1.project);
        dbSession.commit();
        assertNotExists(data1);
        assertExists(data2);
        assertExists(data3);
    }

    @Test
    public void delete_webhooks_from_projects() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        WebhookDto webhook1 = db.webhooks().insertWebhook(project1);
        db.webhookDelivery().insert(webhook1);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        WebhookDto webhook2 = db.webhooks().insertWebhook(project2);
        db.webhookDelivery().insert(webhook2);
        ComponentDto projectNotToBeDeleted = db.components().insertPrivateProject(organization);
        WebhookDto webhook3 = db.webhooks().insertWebhook(projectNotToBeDeleted);
        db.webhookDelivery().insert(webhook3);
        mockResourceTypeAsValidProject();
        underTest.delete(dbSession, Arrays.asList(project1, project2));
        assertThat(db.countRowsOfTable(db.getSession(), "webhooks")).isEqualTo(1);
        assertThat(db.countRowsOfTable(db.getSession(), "webhook_deliveries")).isEqualTo(1);
    }

    @Test
    public void fail_with_IAE_if_not_a_project() {
        mockResourceTypeAsValidProject();
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        dbClient.componentDao().insert(dbSession, project);
        ComponentDto file = newFileDto(project, null);
        dbClient.componentDao().insert(dbSession, file);
        dbSession.commit();
        expectedException.expect(IllegalArgumentException.class);
        underTest.delete(dbSession, file);
    }

    @Test
    public void fail_to_delete_not_deletable_resource_type() {
        ResourceType resourceType = Mockito.mock(ResourceType.class);
        Mockito.when(resourceType.getBooleanProperty("deletable")).thenReturn(false);
        Mockito.when(mockResourceTypes.get(ArgumentMatchers.anyString())).thenReturn(resourceType);
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        dbClient.componentDao().insert(dbSession, project);
        dbSession.commit();
        expectedException.expect(IllegalArgumentException.class);
        underTest.delete(dbSession, project);
    }

    @Test
    public void fail_to_delete_null_resource_type() {
        Mockito.when(mockResourceTypes.get(ArgumentMatchers.anyString())).thenReturn(null);
        ComponentDto project = ComponentTesting.newPrivateProjectDto(db.organizations().insert());
        dbClient.componentDao().insert(dbSession, project);
        dbSession.commit();
        expectedException.expect(IllegalArgumentException.class);
        underTest.delete(dbSession, project);
    }

    @Test
    public void fail_to_delete_project_when_branch() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(IllegalArgumentException.class);
        underTest.delete(dbSession, branch);
    }

    private static class DbData {
        final ComponentDto project;

        final SnapshotDto snapshot;

        final IssueDto issue;

        DbData(ComponentDto project, SnapshotDto snapshot, IssueDto issue) {
            this.project = project;
            this.snapshot = snapshot;
            this.issue = issue;
        }
    }
}

