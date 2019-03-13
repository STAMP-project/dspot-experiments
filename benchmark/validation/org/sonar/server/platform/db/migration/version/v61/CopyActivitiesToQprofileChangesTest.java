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
package org.sonar.server.platform.db.migration.version.v61;


import java.sql.SQLException;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class CopyActivitiesToQprofileChangesTest {
    private static final long A_DATE = 1500000000000L;

    private static final String TABLE_ACTIVITIES = "activities";

    private static final String TABLE_QPROFILE_CHANGES = "qprofile_changes";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CopyActivitiesToQprofileChangesTest.class, "schema.sql");

    private CopyActivitiesToQprofileChanges underTest = new CopyActivitiesToQprofileChanges(db.database());

    @Test
    public void migration_has_no_effect_on_empty_table() throws SQLException {
        underTest.execute();
        assertThat(db.countRowsOfTable(CopyActivitiesToQprofileChangesTest.TABLE_QPROFILE_CHANGES)).isEqualTo(0);
    }

    @Test
    public void copy_qprofile_changes() throws SQLException {
        String key = "U1";
        String profileKey = "P1";
        String login = "marcel";
        String type = "ACTIVATED";
        String data = "D1";
        insertActivity(key, profileKey, login, "QPROFILE", type, data, CopyActivitiesToQprofileChangesTest.A_DATE);
        underTest.execute();
        assertThat(db.countRowsOfTable(CopyActivitiesToQprofileChangesTest.TABLE_QPROFILE_CHANGES)).isEqualTo(1);
        Map<String, Object> change = selectChangeByKey(key);
        assertThat(change.get("qprofileKey")).isEqualTo(profileKey);
        assertThat(change.get("createdAt")).isEqualTo(CopyActivitiesToQprofileChangesTest.A_DATE);
        assertThat(change.get("login")).isEqualTo(login);
        assertThat(change.get("changeType")).isEqualTo(type);
        assertThat(change.get("changeData")).isEqualTo(data);
    }

    /**
     * Do not copy twice the same row
     */
    @Test
    public void copy_is_reentrant() throws SQLException {
        insertActivity("U1", "P1", "marcel", "QPROFILE", "ACTIVATED", "D1", CopyActivitiesToQprofileChangesTest.A_DATE);
        // first run
        underTest.execute();
        assertThat(db.countRowsOfTable(CopyActivitiesToQprofileChangesTest.TABLE_QPROFILE_CHANGES)).isEqualTo(1);
        // second run
        underTest.execute();
        assertThat(db.countRowsOfTable(CopyActivitiesToQprofileChangesTest.TABLE_QPROFILE_CHANGES)).isEqualTo(1);
    }

    @Test
    public void copy_nullable_fields() throws SQLException {
        String key = "U1";
        String type = "ACTIVATED";
        // no login nor data
        insertActivity(key, "P1", null, "QPROFILE", type, null, CopyActivitiesToQprofileChangesTest.A_DATE);
        underTest.execute();
        Map<String, Object> change = selectChangeByKey(key);
        assertThat(change.get("qprofileKey")).isEqualTo("P1");
        assertThat(change.get("createdAt")).isEqualTo(CopyActivitiesToQprofileChangesTest.A_DATE);
        assertThat(change.get("changeType")).isEqualTo(type);
        assertThat(change.get("login")).isNull();
        assertThat(change.get("data")).isNull();
    }

    @Test
    public void ignore_activities_that_do_not_relate_to_qprofiles() throws SQLException {
        insertActivity("U1", "P1", "marcel", "OTHER_ACTIVITY_TYPE", "T1", "D1", CopyActivitiesToQprofileChangesTest.A_DATE);
        underTest.execute();
        assertThat(db.countRowsOfTable(CopyActivitiesToQprofileChangesTest.TABLE_QPROFILE_CHANGES)).isEqualTo(0);
    }

    @Test
    public void ignore_invalid_activities() throws SQLException {
        // no change type
        insertActivity("U1", "P1", "marcel", "QPROFILE", null, "D1", CopyActivitiesToQprofileChangesTest.A_DATE);
        // no date
        insertActivity("U2", "P1", "marcel", "QPROFILE", "ACTIVATED", "D1", null);
        underTest.execute();
        assertThat(db.countRowsOfTable(CopyActivitiesToQprofileChangesTest.TABLE_QPROFILE_CHANGES)).isEqualTo(0);
    }
}

