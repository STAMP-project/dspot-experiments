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


import java.sql.SQLException;
import java.sql.Types;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class CreateWebhooksTableTest {
    private static final String TABLE = "webhooks";

    @Rule
    public final CoreDbTester db = CoreDbTester.createForSchema(CreateWebhooksTableTest.class, "empty.sql");

    private CreateWebhooksTable underTest = new CreateWebhooksTable(db.database());

    @Test
    public void creates_table_on_empty_db() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(CreateWebhooksTableTest.TABLE)).isEqualTo(0);
        db.assertColumnDefinition(CreateWebhooksTableTest.TABLE, "uuid", Types.VARCHAR, 40, false);
        db.assertColumnDefinition(CreateWebhooksTableTest.TABLE, "name", Types.VARCHAR, 100, false);
        db.assertColumnDefinition(CreateWebhooksTableTest.TABLE, "url", Types.VARCHAR, 2000, false);
        db.assertColumnDefinition(CreateWebhooksTableTest.TABLE, "organization_uuid", Types.VARCHAR, 40, true);
        db.assertColumnDefinition(CreateWebhooksTableTest.TABLE, "project_uuid", Types.VARCHAR, 40, true);
        db.assertColumnDefinition(CreateWebhooksTableTest.TABLE, "created_at", Types.BIGINT, null, false);
        db.assertColumnDefinition(CreateWebhooksTableTest.TABLE, "updated_at", Types.BIGINT, null, true);
        db.assertIndex(CreateWebhooksTableTest.TABLE, "organization_webhook", "organization_uuid");
        db.assertIndex(CreateWebhooksTableTest.TABLE, "project_webhook", "project_uuid");
    }
}

