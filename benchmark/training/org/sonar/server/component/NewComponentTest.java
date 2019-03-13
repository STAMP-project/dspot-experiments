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
package org.sonar.server.component;


import NewComponent.Builder;
import com.google.common.base.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class NewComponentTest {
    private static final String ORGANIZATION_UUID = "org1";

    private static final String KEY = "key";

    private static final String NAME = "name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Builder underTest = NewComponent.newComponentBuilder();

    @Test
    public void build_throws_NPE_if_organizationUuid_is_null() {
        expectBuildException(NullPointerException.class, "organization uuid can't be null");
    }

    @Test
    public void build_throws_IAE_when_key_is_null() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID);
        expectBuildException(IllegalArgumentException.class, "Component key can't be empty");
    }

    @Test
    public void build_throws_IAE_when_key_is_empty() {
        underTest.setKey("").setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID);
        expectBuildException(IllegalArgumentException.class, "Component key can't be empty");
    }

    @Test
    public void build_throws_IAE_when_key_is_longer_than_400_characters() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(Strings.repeat("a", (400 + 1)));
        expectBuildException(IllegalArgumentException.class, "Component key length (401) is longer than the maximum authorized (400)");
    }

    @Test
    public void build_fails_with_IAE_when_name_is_null() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(NewComponentTest.KEY);
        expectBuildException(IllegalArgumentException.class, "Component name can't be empty");
    }

    @Test
    public void build_fails_with_IAE_when_name_is_empty() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(NewComponentTest.KEY).setName("");
        expectBuildException(IllegalArgumentException.class, "Component name can't be empty");
    }

    @Test
    public void build_fails_with_IAE_when_name_is_longer_than_2000_characters() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(NewComponentTest.KEY).setName(Strings.repeat("a", 501));
        expectBuildException(IllegalArgumentException.class, "Component name length (501) is longer than the maximum authorized (500)");
    }

    @Test
    public void build_fails_with_IAE_when_qualifier_is_null() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(NewComponentTest.KEY).setName(NewComponentTest.NAME).setQualifier(null);
        expectBuildException(IllegalArgumentException.class, "Component qualifier can't be empty");
    }

    @Test
    public void build_fails_with_IAE_when_qualifier_is_empty() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(NewComponentTest.KEY).setName(NewComponentTest.NAME).setQualifier("");
        expectBuildException(IllegalArgumentException.class, "Component qualifier can't be empty");
    }

    @Test
    public void build_fails_with_IAE_when_qualifier_is_longer_than_10_characters() {
        underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(NewComponentTest.KEY).setName(NewComponentTest.NAME).setQualifier(Strings.repeat("a", (10 + 1)));
        expectBuildException(IllegalArgumentException.class, "Component qualifier length (11) is longer than the maximum authorized (10)");
    }

    @Test
    public void getQualifier_returns_PROJECT_when_no_set_in_builder() {
        NewComponent newComponent = underTest.setOrganizationUuid(NewComponentTest.ORGANIZATION_UUID).setKey(NewComponentTest.KEY).setName(NewComponentTest.NAME).build();
        assertThat(newComponent.qualifier()).isEqualTo(PROJECT);
    }
}

