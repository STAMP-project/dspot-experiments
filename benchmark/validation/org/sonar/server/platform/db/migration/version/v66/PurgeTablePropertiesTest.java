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
package org.sonar.server.platform.db.migration.version.v66;


import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class PurgeTablePropertiesTest {
    private static final String TABLE_PROPERTIES = "properties";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PurgeTablePropertiesTest.class, "properties.sql");

    private PurgeTableProperties underTest = new PurgeTableProperties(db.database());

    @Test
    public void migration_has_no_effect_on_empty_db() throws Exception {
        underTest.execute();
        assertThat(db.countRowsOfTable(PurgeTablePropertiesTest.TABLE_PROPERTIES)).isEqualTo(0);
    }

    @Test
    public void migration_deletes_properties_by_keys() throws Exception {
        // to be deleted
        insert("views.analysisDelayingInMinutes");
        insert("views.status");
        insert("sonar.issuesdensity.weight");
        // to be kept
        insert("views.status.differentSuffix");
        insert("views.foo");
        underTest.execute();
        verifyRemainingKeys("views.status.differentSuffix", "views.foo");
    }

    @Test
    public void migration_deletes_properties_by_key_prefixes() throws Exception {
        // to be deleted
        insert("sonar.sqale.foo");
        insert("sonar.sqale.bar");
        // to be kept
        insert("sonar.sqale");
        insert("sqale");
        underTest.execute();
        verifyRemainingKeys("sonar.sqale", "sqale");
    }
}

