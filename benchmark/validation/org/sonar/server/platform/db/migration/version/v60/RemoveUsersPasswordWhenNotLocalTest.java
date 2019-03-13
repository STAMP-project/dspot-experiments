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
package org.sonar.server.platform.db.migration.version.v60;


import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class RemoveUsersPasswordWhenNotLocalTest {
    static long PAST_DATE = 1000000000L;

    static long NOW = 2000000000L;

    System2 system2 = Mockito.mock(System2.class);

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(RemoveUsersPasswordWhenNotLocalTest.class, "schema.sql");

    private RemoveUsersPasswordWhenNotLocal migration = new RemoveUsersPasswordWhenNotLocal(db.database(), system2);

    @Test
    public void update_not_local_user() throws Exception {
        insertUserWithPassword("john", false);
        migration.execute();
        List<Map<String, Object>> rows = db.select("SELECT CRYPTED_PASSWORD,SALT,UPDATED_AT FROM users");
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).get("CRYPTED_PASSWORD")).isNull();
        assertThat(rows.get(0).get("SALT")).isNull();
        assertThat(rows.get(0).get("UPDATED_AT")).isEqualTo(RemoveUsersPasswordWhenNotLocalTest.NOW);
    }

    @Test
    public void ignore_local_user() throws Exception {
        insertUserWithPassword("john", true);
        migration.execute();
        List<Map<String, Object>> rows = db.select("SELECT CRYPTED_PASSWORD,SALT,UPDATED_AT FROM users");
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).get("CRYPTED_PASSWORD")).isNotNull();
        assertThat(rows.get(0).get("SALT")).isNotNull();
        assertThat(rows.get(0).get("UPDATED_AT")).isEqualTo(RemoveUsersPasswordWhenNotLocalTest.PAST_DATE);
    }

    @Test
    public void ignore_already_migrated_user() throws Exception {
        insertUserWithoutPasword("john", false);
        migration.execute();
        List<Map<String, Object>> rows = db.select("SELECT CRYPTED_PASSWORD,SALT,UPDATED_AT FROM users");
        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).get("CRYPTED_PASSWORD")).isNull();
        assertThat(rows.get(0).get("SALT")).isNull();
        assertThat(rows.get(0).get("UPDATED_AT")).isEqualTo(RemoveUsersPasswordWhenNotLocalTest.PAST_DATE);
    }
}

