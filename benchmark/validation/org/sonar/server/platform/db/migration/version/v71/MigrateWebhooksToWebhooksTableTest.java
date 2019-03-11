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
package org.sonar.server.platform.db.migration.version.v71;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.core.util.UuidFactory;
import org.sonar.core.util.UuidFactoryFast;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.version.v63.DefaultOrganizationUuidProviderImpl;


@RunWith(DataProviderRunner.class)
public class MigrateWebhooksToWebhooksTableTest {
    private static final long NOW = 1500000000000L;

    private static final boolean ENABLED = true;

    private static final boolean DISABLED = false;

    @Rule
    public final CoreDbTester dbTester = CoreDbTester.createForSchema(MigrateWebhooksToWebhooksTableTest.class, "migrate_webhooks.sql");

    private final UuidFactory uuidFactory = UuidFactoryFast.getInstance();

    private MigrateWebhooksToWebhooksTable underTest = new MigrateWebhooksToWebhooksTable(dbTester.database(), new DefaultOrganizationUuidProviderImpl(), uuidFactory);

    @Test
    public void should_do_nothing_if_no_webhooks() throws SQLException {
        underTest.execute();
        assertNoMoreWebhookProperties();
        assertThat(dbTester.countRowsOfTable("webhooks")).isEqualTo(0);
    }

    @Test
    public void execute_deletes_inconsistent_properties_for_global_webhook() throws SQLException {
        String defaultOrganizationUuid = insertDefaultOrganization();
        insertGlobalWebhookProperties(4);
        insertGlobalWebhookProperty(1, null, "no name", defaultOrganizationUuid);
        insertGlobalWebhookProperty(2, "no url", null, defaultOrganizationUuid);
        insertGlobalWebhookProperty(3, null, null, defaultOrganizationUuid);
        MigrateWebhooksToWebhooksTableTest.Webhook webhook = insertGlobalWebhookProperty(4, "name", "url", defaultOrganizationUuid);
        underTest.execute();
        assertThat(selectWebhooksInDb()).containsOnly(new MigrateWebhooksToWebhooksTableTest.Row(webhook));
        assertNoMoreWebhookProperties();
    }

    @Test
    public void execute_delete_webhooks_of_non_existing_project() throws SQLException {
        MigrateWebhooksToWebhooksTableTest.Project project = insertProject(MigrateWebhooksToWebhooksTableTest.ENABLED);
        MigrateWebhooksToWebhooksTableTest.Project nonExistingProject = new MigrateWebhooksToWebhooksTableTest.Project(233, "foo");
        MigrateWebhooksToWebhooksTableTest.Row[] rows = Stream.of(project, nonExistingProject).map(( prj) -> {
            insertProjectWebhookProperties(prj, 1);
            return insertProjectWebhookProperty(prj, 1, "name", "url");
        }).map(MigrateWebhooksToWebhooksTableTest.Row::new).toArray(MigrateWebhooksToWebhooksTableTest.Row[]::new);
        underTest.execute();
        assertThat(selectWebhooksInDb()).containsOnly(Arrays.stream(rows).filter(( r) -> Objects.equals(r.projectUuid, project.uuid)).toArray(MigrateWebhooksToWebhooksTableTest.Row[]::new));
        assertNoMoreWebhookProperties();
    }

    @Test
    public void execute_delete_webhooks_of_disabled_project() throws SQLException {
        MigrateWebhooksToWebhooksTableTest.Project project = insertProject(MigrateWebhooksToWebhooksTableTest.ENABLED);
        MigrateWebhooksToWebhooksTableTest.Project nonExistingProject = insertProject(MigrateWebhooksToWebhooksTableTest.DISABLED);
        MigrateWebhooksToWebhooksTableTest.Row[] rows = Stream.of(project, nonExistingProject).map(( prj) -> {
            insertProjectWebhookProperties(prj, 1);
            return insertProjectWebhookProperty(prj, 1, "name", "url");
        }).map(MigrateWebhooksToWebhooksTableTest.Row::new).toArray(MigrateWebhooksToWebhooksTableTest.Row[]::new);
        underTest.execute();
        assertThat(selectWebhooksInDb()).containsOnly(Arrays.stream(rows).filter(( r) -> Objects.equals(r.projectUuid, project.uuid)).toArray(MigrateWebhooksToWebhooksTableTest.Row[]::new));
        assertNoMoreWebhookProperties();
    }

    @Test
    public void execute_deletes_inconsistent_properties_for_project_webhook() throws SQLException {
        MigrateWebhooksToWebhooksTableTest.Project project = insertProject(MigrateWebhooksToWebhooksTableTest.ENABLED);
        insertProjectWebhookProperties(project, 4);
        insertProjectWebhookProperty(project, 1, null, "no name");
        insertProjectWebhookProperty(project, 2, "no url", null);
        insertProjectWebhookProperty(project, 3, null, null);
        MigrateWebhooksToWebhooksTableTest.Webhook webhook = insertProjectWebhookProperty(project, 4, "name", "url");
        underTest.execute();
        assertThat(selectWebhooksInDb()).containsOnly(new MigrateWebhooksToWebhooksTableTest.Row(webhook));
        assertNoMoreWebhookProperties();
    }

    @Test
    public void should_migrate_global_webhooks() throws SQLException {
        insertDefaultOrganization();
        insertProperty("sonar.webhooks.global", "1,2", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.1.name", "a webhook", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.1.url", "http://webhook.com", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.2.name", "a webhook", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.2.url", "http://webhook.com", null, Long.parseLong(randomNumeric(7)));
        underTest.execute();
        assertNoMoreWebhookProperties();
        assertThat(dbTester.countRowsOfTable("webhooks")).isEqualTo(2);
    }

    @Test
    public void should_migrate_only_valid_webhooks() throws SQLException {
        insertDefaultOrganization();
        insertProperty("sonar.webhooks.global", "1,2,3,4", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.1.url", "http://webhook.com", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.2.name", "a webhook", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.3.name", "a webhook", null, Long.parseLong(randomNumeric(7)));
        insertProperty("sonar.webhooks.global.3.url", "http://webhook.com", null, Long.parseLong(randomNumeric(7)));
        // nothing for 4
        underTest.execute();
        assertNoMoreWebhookProperties();
        assertThat(dbTester.countRowsOfTable("webhooks")).isEqualTo(1);
    }

    private static long PROJECT_ID_GENERATOR = new Random().nextInt(343343);

    private static final class Webhook {
        @Nullable
        private final String name;

        @Nullable
        private final String url;

        @Nullable
        private final String organizationUuid;

        @Nullable
        private final String projectUuid;

        private final long createdAt;

        private Webhook(@Nullable
        String name, @Nullable
        String url, @Nullable
        String organizationUuid, @Nullable
        String projectUuid, long createdAt) {
            this.name = name;
            this.url = url;
            this.organizationUuid = organizationUuid;
            this.projectUuid = projectUuid;
            this.createdAt = createdAt;
        }
    }

    private static class Row {
        private final String uuid;

        private final String name;

        private final String url;

        @Nullable
        private final String organizationUuid;

        @Nullable
        private final String projectUuid;

        private final long createdAt;

        private final long updatedAt;

        private Row(Map<String, Object> row) {
            this.uuid = ((String) (row.get("UUID")));
            this.name = ((String) (row.get("NAME")));
            this.url = ((String) (row.get("URL")));
            this.organizationUuid = ((String) (row.get("ORGANIZATION_UUID")));
            this.projectUuid = ((String) (row.get("PROJECT_UUID")));
            this.createdAt = ((Long) (row.get("CREATED_AT")));
            this.updatedAt = ((Long) (row.get("UPDATED_AT")));
        }

        private Row(MigrateWebhooksToWebhooksTableTest.Webhook webhook) {
            this.uuid = "NOT KNOWN YET";
            this.name = webhook.name;
            this.url = webhook.url;
            this.organizationUuid = webhook.organizationUuid;
            this.projectUuid = webhook.projectUuid;
            this.createdAt = webhook.createdAt;
            this.updatedAt = webhook.createdAt;
        }

        public String getUuid() {
            return uuid;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MigrateWebhooksToWebhooksTableTest.Row row = ((MigrateWebhooksToWebhooksTableTest.Row) (o));
            return ((((((createdAt) == (row.createdAt)) && ((updatedAt) == (row.updatedAt))) && (Objects.equals(name, row.name))) && (Objects.equals(url, row.url))) && (Objects.equals(organizationUuid, row.organizationUuid))) && (Objects.equals(projectUuid, row.projectUuid));
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, url, organizationUuid, projectUuid, createdAt, updatedAt);
        }

        @Override
        public String toString() {
            return ((((((((((((((((((("Row{" + "uuid='") + (uuid)) + '\'') + ", name='") + (name)) + '\'') + ", url='") + (url)) + '\'') + ", organizationUuid='") + (organizationUuid)) + '\'') + ", projectUuid='") + (projectUuid)) + '\'') + ", createdAt=") + (createdAt)) + ", updatedAt=") + (updatedAt)) + '}';
        }
    }

    private static final class Project {
        private final long id;

        private final String uuid;

        private Project(long id, String uuid) {
            this.id = id;
            this.uuid = uuid;
        }
    }
}

