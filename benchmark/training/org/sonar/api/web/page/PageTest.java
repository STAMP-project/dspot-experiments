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
package org.sonar.api.web.page;


import Page.Builder;
import Qualifier.APP;
import Qualifier.MODULE;
import Qualifier.PROJECT;
import Qualifier.SUB_VIEW;
import Qualifier.VIEW;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.Qualifiers.SUBVIEW;


public class PageTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Builder underTest = Page.builder("governance/project_dump").setName("Project Dump");

    @Test
    public void full_test() {
        Page result = underTest.setComponentQualifiers(Qualifier.PROJECT, Qualifier.MODULE).setScope(Scope.COMPONENT).setAdmin(true).build();
        assertThat(result.getKey()).isEqualTo("governance/project_dump");
        assertThat(result.getPluginKey()).isEqualTo("governance");
        assertThat(result.getName()).isEqualTo("Project Dump");
        assertThat(result.getComponentQualifiers()).containsOnly(Qualifier.PROJECT, Qualifier.MODULE);
        assertThat(result.getScope()).isEqualTo(Scope.COMPONENT);
        assertThat(result.isAdmin()).isTrue();
    }

    @Test
    public void qualifiers_map_to_key() {
        assertThat(PROJECT.getKey()).isEqualTo(org.sonar.api.resources.Qualifiers.PROJECT);
        assertThat(MODULE.getKey()).isEqualTo(org.sonar.api.resources.Qualifiers.MODULE);
        assertThat(VIEW.getKey()).isEqualTo(org.sonar.api.resources.Qualifiers.VIEW);
        assertThat(APP.getKey()).isEqualTo(org.sonar.api.resources.Qualifiers.APP);
        assertThat(SUB_VIEW.getKey()).isEqualTo(SUBVIEW);
    }

    @Test
    public void authorized_qualifiers() {
        org.sonar.api.web.page.Page.Qualifier[] qualifiers = org.sonar.api.web.page.Page.Qualifier.values();
        assertThat(qualifiers).containsExactlyInAnyOrder(Qualifier.PROJECT, Qualifier.MODULE, Qualifier.VIEW, Qualifier.SUB_VIEW, Qualifier.APP);
    }

    @Test
    public void default_values() {
        Page result = underTest.build();
        assertThat(result.getComponentQualifiers()).isEmpty();
        assertThat(result.getScope()).isEqualTo(Scope.GLOBAL);
        assertThat(result.isAdmin()).isFalse();
    }

    @Test
    public void all_qualifiers_when_component_page() {
        Page result = underTest.setScope(Scope.COMPONENT).build();
        assertThat(result.getComponentQualifiers()).containsOnly(org.sonar.api.web.page.Page.Qualifier.values());
    }

    @Test
    public void qualifiers_from_key() {
        assertThat(org.sonar.api.web.page.Page.Qualifier.fromKey(Qualifiers.PROJECT)).isEqualTo(PROJECT);
        assertThat(org.sonar.api.web.page.Page.Qualifier.fromKey("42")).isNull();
    }

    @Test
    public void fail_if_null_qualifiers() {
        expectedException.expect(NullPointerException.class);
        underTest.setComponentQualifiers(((org.sonar.api.web.page.Page.Qualifier[]) (null))).build();
    }

    @Test
    public void fail_if_a_page_has_a_null_key() {
        expectedException.expect(NullPointerException.class);
        Page.builder(null).setName("Say my name").build();
    }

    @Test
    public void fail_if_a_page_has_an_empty_key() {
        expectedException.expect(IllegalArgumentException.class);
        Page.builder("").setName("Say my name").build();
    }

    @Test
    public void fail_if_a_page_has_a_null_name() {
        expectedException.expect(IllegalArgumentException.class);
        Page.builder("governance/project_dump").build();
    }

    @Test
    public void fail_if_a_page_has_an_empty_name() {
        expectedException.expect(IllegalArgumentException.class);
        Page.builder("governance/project_dump").setName("").build();
    }

    @Test
    public void fail_if_qualifiers_without_scope() {
        expectedException.expect(IllegalArgumentException.class);
        underTest.setComponentQualifiers(Qualifier.PROJECT).build();
    }

    @Test
    public void fail_if_key_does_not_contain_a_slash() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Page key [project_dump] is not valid. It must contain a single slash, for example my_plugin/my_page.");
        Page.builder("project_dump").setName("Project Dump").build();
    }

    @Test
    public void fail_if_key_contains_more_than_one_slash() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Page key [governance/project/dump] is not valid. It must contain a single slash, for example my_plugin/my_page.");
        Page.builder("governance/project/dump").setName("Project Dump").build();
    }
}

