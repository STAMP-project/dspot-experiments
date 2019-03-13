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
package org.sonar.server.platform.db.migration.version.v73;


import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.utils.System2;
import org.sonar.db.CoreDbTester;


public class PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest {
    private static final long PAST = 100000000000L;

    private static final long NOW = 500000000000L;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.class, "perm_tpl_characteristics.sql");

    private System2 system2 = Mockito.mock(System2.class);

    private PopulateHotspotAdminPermissionOnTemplatesCharacteristics underTest = new PopulateHotspotAdminPermissionOnTemplatesCharacteristics(db.database(), system2);

    @Test
    public void insert_missing_permission() throws SQLException {
        Mockito.when(system2.now()).thenReturn(PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.NOW);
        insertPermTemplateCharacteristic(1, "noissueadmin", true);
        insertPermTemplateCharacteristic(3, "issueadmin", true);
        insertPermTemplateCharacteristic(3, "another", true);
        insertPermTemplateCharacteristic(5, "securityhotspotadmin", true);
        insertPermTemplateCharacteristic(11, "noissueadmin", false);
        insertPermTemplateCharacteristic(13, "issueadmin", false);
        insertPermTemplateCharacteristic(13, "another", false);
        insertPermTemplateCharacteristic(15, "securityhotspotadmin", false);
        underTest.execute();
        assertPermTemplateCharacteristics(tuple(1L, "noissueadmin", true, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST), tuple(3L, "issueadmin", true, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST), tuple(3L, "another", true, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST), tuple(3L, "securityhotspotadmin", true, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.NOW, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.NOW), tuple(5L, "securityhotspotadmin", true, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST), tuple(11L, "noissueadmin", false, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST), tuple(13L, "issueadmin", false, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST), tuple(13L, "another", false, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST), tuple(15L, "securityhotspotadmin", false, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST, PopulateHotspotAdminPermissionOnTemplatesCharacteristicsTest.PAST));
    }
}

