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


import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.version.v63.DefaultOrganizationUuidProviderImpl;


public class UpgradeQualityTemplateLoadedTemplatesTest {
    private static final String TABLE_ORGANIZATIONS = "organizations";

    private static final String DEFAULT_ORGANIZATION_UUID = "def-org";

    private static final String TABLE_LOADED_TEMPLATES = "loaded_templates";

    private static final String QUALITY_PROFILE_TYPE = "QUALITY_PROFILE";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(UpgradeQualityTemplateLoadedTemplatesTest.class, "organizations_internal_properties_and_loaded_templates.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UpgradeQualityTemplateLoadedTemplates underTest = new UpgradeQualityTemplateLoadedTemplates(db.database(), new DefaultOrganizationUuidProviderImpl());

    @Test
    public void fails_with_ISE_when_no_default_organization_is_set() throws SQLException {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization uuid is missing");
        underTest.execute();
    }

    @Test
    public void fails_with_ISE_when_default_organization_does_not_exist_in_table_ORGANIZATIONS() throws SQLException {
        insertDefaultOrganizationUuid("blabla");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Default organization with uuid 'blabla' does not exist in table ORGANIZATIONS");
        underTest.execute();
    }

    @Test
    public void execute_has_no_effect_if_loaded_templates_table_is_empty() throws Exception {
        setupDefaultOrganization();
        underTest.execute();
        assertThat(db.countRowsOfTable(UpgradeQualityTemplateLoadedTemplatesTest.TABLE_LOADED_TEMPLATES)).isEqualTo(0);
    }

    @Test
    public void execute_has_no_effect_if_loaded_templates_has_no_row_with_type_QUALITY_PROFILE() throws Exception {
        setupDefaultOrganization();
        insertLoadedTemplate("foo", "bar");
        underTest.execute();
        assertThat(loadedTemplateExists("foo", "bar")).isTrue();
        assertThat(db.countRowsOfTable(UpgradeQualityTemplateLoadedTemplatesTest.TABLE_LOADED_TEMPLATES)).isEqualTo(1);
    }

    @Test
    public void execute_updates_any_row_with_type_QUALITY_PROFILE_to_type_based_on_current_key_md5_and_default_organization_uuid_as_key() throws SQLException {
        setupDefaultOrganization();
        // put accents to ensure UTF-8 byte array of String is used
        String key1 = "f?@?:bar";
        String key2 = "bar";
        insertLoadedTemplate(UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE, key1);
        // matching on type is case sensitive
        insertLoadedTemplate(UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE.toLowerCase(), key1);
        insertLoadedTemplate(UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE, key2);
        insertLoadedTemplate("other type", key2);
        underTest.execute();
        assertThat(loadedTemplateExists((((UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE) + '.') + (DigestUtils.md5Hex(key1.getBytes(StandardCharsets.UTF_8)))), UpgradeQualityTemplateLoadedTemplatesTest.DEFAULT_ORGANIZATION_UUID)).isTrue();
        assertThat(loadedTemplateExists(UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE.toLowerCase(), key1)).isTrue();
        assertThat(loadedTemplateExists("other type", key2)).isTrue();
        assertThat(loadedTemplateExists((((UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE) + '.') + (DigestUtils.md5Hex(key2.getBytes(StandardCharsets.UTF_8)))), UpgradeQualityTemplateLoadedTemplatesTest.DEFAULT_ORGANIZATION_UUID)).isTrue();
        assertThat(db.countRowsOfTable(UpgradeQualityTemplateLoadedTemplatesTest.TABLE_LOADED_TEMPLATES)).isEqualTo(4);
    }

    @Test
    public void execute_is_reentrant() throws Exception {
        setupDefaultOrganization();
        String key = "blabla";
        insertLoadedTemplate(UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE, key);
        underTest.execute();
        underTest.execute();
        assertThat(loadedTemplateExists((((UpgradeQualityTemplateLoadedTemplatesTest.QUALITY_PROFILE_TYPE) + '.') + (DigestUtils.md5Hex(key.getBytes(StandardCharsets.UTF_8)))), UpgradeQualityTemplateLoadedTemplatesTest.DEFAULT_ORGANIZATION_UUID)).isTrue();
        assertThat(db.countRowsOfTable(UpgradeQualityTemplateLoadedTemplatesTest.TABLE_LOADED_TEMPLATES)).isEqualTo(1);
    }
}

