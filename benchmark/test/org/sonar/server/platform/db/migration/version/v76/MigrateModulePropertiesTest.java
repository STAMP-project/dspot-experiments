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
package org.sonar.server.platform.db.migration.version.v76;


import Qualifiers.PROJECT;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.core.util.SequenceUuidFactory;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.CoreDbTester;


public class MigrateModulePropertiesTest {
    private static final long NOW = 50000000000L;

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(MigrateModulePropertiesTest.class, "schema.sql");

    private System2 system2 = new TestSystem2().setNow(MigrateModulePropertiesTest.NOW);

    private UuidFactory uuidFactory = new SequenceUuidFactory();

    private MigrateModuleProperties underTest = new MigrateModuleProperties(db.database(), system2);

    @Test
    public void multi_module_project_migration() throws SQLException {
        setupMultiModuleProject();
        underTest.execute();
        verifyMultiModuleProjectMigration();
        assertThat(getRemainingProperties()).hasSize(3);
    }

    @Test
    public void migration_is_reentrant() throws SQLException {
        setupMultiModuleProject();
        underTest.execute();
        underTest.execute();
        verifyMultiModuleProjectMigration();
        assertThat(getRemainingProperties()).hasSize(3);
    }

    @Test
    public void single_module_project_migration() throws SQLException {
        String project2Uuid = uuidFactory.create();
        insertComponent(5, project2Uuid, null, project2Uuid, PROJECT, "Single module project");
        insertProperty(9, 5, "sonar.coverage.exclusions", "SingleModuleA.java", null);
        insertProperty(10, 5, "sonar.cp.exclusions", "SingleModuleB.java", null);
        underTest.execute();
        List<Map<String, Object>> properties = db.select(String.format(("select ID, TEXT_VALUE, CLOB_VALUE " + ("from properties " + "where PROP_KEY='%s' and RESOURCE_ID = 5")), MigrateModuleProperties.NEW_PROPERTY_NAME));
        assertThat(properties).hasSize(0);
        List<Map<String, Object>> remainingProperties = db.select("select ID from properties");
        assertThat(remainingProperties).hasSize(2);
    }

    @Test
    public void properties_do_not_leak_between_projects() throws SQLException {
        setupMultiModuleProject();
        setupSecondMultiModuleProject();
        underTest.execute();
        verifyMultiModuleProjectMigration();
        verifySecondMultiModuleProjectMigration();
        assertThat(getRemainingProperties()).hasSize(5);
    }
}

