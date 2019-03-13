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
package org.sonar.server.platform.db.migration.version.v64;


import java.sql.SQLException;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class SetCopyComponentUuidOnLocalViewsTest {
    private static final String TABLE_PROJECTS = "projects";

    private static final String QUALIFIER_SUB_VIEW = "SVW";

    private static final String QUALIFIER_VIEW = "VW";

    private static final String SCOPE_PROJECT = "PRJ";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(SetCopyComponentUuidOnLocalViewsTest.class, "in_progress_projects.sql");

    private SetCopyComponentUuidOnLocalViews underTest = new SetCopyComponentUuidOnLocalViews(db.database());

    @Test
    public void migration_has_no_effect_on_empty_tables() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(SetCopyComponentUuidOnLocalViewsTest.TABLE_PROJECTS)).isZero();
    }

    @Test
    public void set_copy_component_uuid_on_sub_views() throws Exception {
        insertProject("VIEW1", "VIEW1_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW2", "VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW1:VIEW2", "LOCAL_VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        underTest.execute();
        verifyOnlyOneComponentHasCopyComponentUuid("LOCAL_VIEW2_UUID", "VIEW2_UUID");
    }

    @Test
    public void set_copy_component_uuid_on_imbricated_sub_views() throws Exception {
        insertProject("VIEW1", "VIEW1_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW2", "VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW3", "VIEW3_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW1:VIEW2", "LOCAL_VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW2:VIEW3", "LOCAL_VIEW3_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW1:VIEW2:VIEW3", "LOCAL_VIEW3_BIS_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        underTest.execute();
        verifyComponentsHavingCopyComponentUuid(new SetCopyComponentUuidOnLocalViewsTest.Component("LOCAL_VIEW2_UUID", "VIEW2_UUID"), new SetCopyComponentUuidOnLocalViewsTest.Component("LOCAL_VIEW3_UUID", "VIEW3_UUID"), new SetCopyComponentUuidOnLocalViewsTest.Component("LOCAL_VIEW3_BIS_UUID", "VIEW3_UUID"));
    }

    @Test
    public void migration_doest_not_update_copy_component_uuid_if_already_exists() throws Exception {
        insertProject("VIEW1", "VIEW1_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW2", "VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW1:VIEW2", "LOCAL_VIEW2_UUID", "ALREADY_EXISTING", SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        underTest.execute();
        verifyOnlyOneComponentHasCopyComponentUuid("LOCAL_VIEW2_UUID", "ALREADY_EXISTING");
    }

    @Test
    public void migration_ignores_sub_view_having_bad_key_format() throws Exception {
        insertProject("VIEW1", "VIEW1_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW2", "VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        // Missing ':' in the key
        insertProject("VIEW1_VIEW2", "LOCAL_VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        underTest.execute();
        verifyComponentsHavingCopyComponentUuid();
    }

    @Test
    public void migration_does_nothing_when_no_root_views() throws Exception {
        insertProject("VIEW1:VIEW2", "LOCAL_VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT, false);
        underTest.execute();
        verifyComponentsHavingCopyComponentUuid();
    }

    @Test
    public void migration_ignores_disabled_views() throws Exception {
        insertProject("VIEW1", "VIEW1_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT, false);
        insertProject("VIEW2", "VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT, false);
        insertProject("VIEW1:VIEW2", "LOCAL_VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT, false);
        underTest.execute();
        verifyComponentsHavingCopyComponentUuid();
    }

    @Test
    public void migration_is_re_entrant() throws Exception {
        insertProject("VIEW1", "VIEW1_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW2", "VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        insertProject("VIEW1:VIEW2", "LOCAL_VIEW2_UUID", null, SetCopyComponentUuidOnLocalViewsTest.QUALIFIER_SUB_VIEW, SetCopyComponentUuidOnLocalViewsTest.SCOPE_PROJECT);
        underTest.execute();
        verifyOnlyOneComponentHasCopyComponentUuid("LOCAL_VIEW2_UUID", "VIEW2_UUID");
        underTest.execute();
        verifyOnlyOneComponentHasCopyComponentUuid("LOCAL_VIEW2_UUID", "VIEW2_UUID");
    }

    private static class Component {
        private final String uuid;

        private final String copyComponentUuid;

        Component(String uuid, @Nullable
        String copyComponentUuid) {
            this.uuid = uuid;
            this.copyComponentUuid = copyComponentUuid;
        }

        String getUuid() {
            return uuid;
        }

        @CheckForNull
        String getCopyComponentUuid() {
            return copyComponentUuid;
        }

        @Override
        public String toString() {
            return (((((("Component{" + "uuid='") + (uuid)) + '\'') + ", copyComponentUuid='") + (copyComponentUuid)) + '\'') + '}';
        }
    }
}

