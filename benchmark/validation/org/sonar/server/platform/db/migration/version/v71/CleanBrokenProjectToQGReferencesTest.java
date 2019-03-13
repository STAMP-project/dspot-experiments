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
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.db.CoreDbTester;


@RunWith(DataProviderRunner.class)
public class CleanBrokenProjectToQGReferencesTest {
    private static final String PROPERTY_SONAR_QUALITYGATE = "sonar.qualitygate";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(CleanBrokenProjectToQGReferencesTest.class, "properties_and_quality_gates.sql");

    private CleanBrokenProjectToQGReferences underTest = new CleanBrokenProjectToQGReferences(db.database());

    @Test
    public void do_nothing_when_no_data() throws SQLException {
        assertThat(db.countRowsOfTable("PROPERTIES")).isEqualTo(0);
        underTest.execute();
        assertThat(db.countRowsOfTable("PROPERTIES")).isEqualTo(0);
    }

    @Test
    public void execute_deletes_all_qualitygate_component_properties_when_there_is_no_qualitygate() throws SQLException {
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, 30, "12");
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, 42, "val1");
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, null, "val2");
        underTest.execute();
        assertThat(selectPropertyValues()).containsOnly("val2");
        assertThat(db.countRowsOfTable("PROPERTIES")).isEqualTo(1);
    }

    @Test
    public void execute_deletes_only_project_qualitygate_property() throws SQLException {
        String qualityGateId = String.valueOf(insertQualityGate());
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, 84651, qualityGateId);
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, 7323, "does_not_exist");
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, null, "not a project property");
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, null, "not_a_qualitygate_id_either");
        underTest.execute();
        assertThat(selectPropertyValues()).containsExactly(qualityGateId, "not a project property", "not_a_qualitygate_id_either");
        assertThat(db.countRowsOfTable("PROPERTIES")).isEqualTo(3);
    }

    @Test
    public void execute_deletes_only_qualitygate_property_for_project() throws SQLException {
        String qualityGateId = String.valueOf(insertQualityGate());
        insertProperty(CleanBrokenProjectToQGReferencesTest.PROPERTY_SONAR_QUALITYGATE, 84651, qualityGateId);
        insertProperty("FOO", 84651, "does_not_exist");
        underTest.execute();
        assertThat(selectPropertyValues()).containsExactly(qualityGateId, "does_not_exist");
        assertThat(db.countRowsOfTable("PROPERTIES")).isEqualTo(2);
    }

    private static int qualityGateIdGenerator = 2999567 + (new Random().nextInt(56));
}

