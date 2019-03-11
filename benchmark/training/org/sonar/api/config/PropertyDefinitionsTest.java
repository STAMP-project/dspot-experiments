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
package org.sonar.api.config;


import Qualifiers.APP;
import Qualifiers.MODULE;
import Qualifiers.PROJECT;
import Qualifiers.VIEW;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.Properties;
import org.sonar.api.Property;


public class PropertyDefinitionsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_build_with_predefined_list_of_definitions() {
        List<PropertyDefinition> list = Arrays.asList(PropertyDefinition.builder("foo").name("Foo").build(), PropertyDefinition.builder("one").name("One").build(), PropertyDefinition.builder("two").name("Two").defaultValue("2").build());
        PropertyDefinitions def = new PropertyDefinitions(list);
        assertProperties(def);
    }

    @Test
    public void should_inspect_plugin_objects() {
        PropertyDefinitions def = new PropertyDefinitions(PropertyDefinition.builder("foo").name("Foo").build(), PropertyDefinition.builder("one").name("One").build(), PropertyDefinition.builder("two").name("Two").defaultValue("2").build());
        assertProperties(def);
    }

    @Test
    public void should_inspect_annotation_plugin_objects() {
        PropertyDefinitions def = new PropertyDefinitions(new PropertyDefinitionsTest.PluginWithProperty(), new PropertyDefinitionsTest.PluginWithProperties());
        assertProperties(def);
    }

    @Test
    public void should_inspect_plugin_classes() {
        PropertyDefinitions def = new PropertyDefinitions(PropertyDefinitionsTest.PluginWithProperty.class, PropertyDefinitionsTest.PluginWithProperties.class);
        assertProperties(def);
    }

    @Test
    public void test_categories() {
        PropertyDefinitions def = new PropertyDefinitions(PropertyDefinition.builder("inCateg").name("In Categ").category("categ").build(), PropertyDefinition.builder("noCateg").name("No categ").build());
        assertThat(def.getCategory("inCateg")).isEqualTo("categ");
        assertThat(def.getCategory("noCateg")).isEmpty();
    }

    @Test
    public void test_categories_on_annotation_plugin() {
        PropertyDefinitions def = new PropertyDefinitions(PropertyDefinitionsTest.Categories.class);
        assertThat(def.getCategory("inCateg")).isEqualTo("categ");
        assertThat(def.getCategory("noCateg")).isEqualTo("");
    }

    @Test
    public void test_default_category() {
        PropertyDefinitions def = new PropertyDefinitions();
        def.addComponent(PropertyDefinition.builder("inCateg").name("In Categ").category("categ").build(), "default");
        def.addComponent(PropertyDefinition.builder("noCateg").name("No categ").build(), "default");
        assertThat(def.getCategory("inCateg")).isEqualTo("categ");
        assertThat(def.getCategory("noCateg")).isEqualTo("default");
    }

    @Test
    public void test_default_category_on_annotation_plugin() {
        PropertyDefinitions def = new PropertyDefinitions();
        def.addComponent(PropertyDefinitionsTest.Categories.class, "default");
        assertThat(def.getCategory("inCateg")).isEqualTo("categ");
        assertThat(def.getCategory("noCateg")).isEqualTo("default");
    }

    @Test
    public void should_return_special_categories() {
        PropertyDefinitions def = new PropertyDefinitions();
        assertThat(def.propertiesByCategory(null).get(new Category("general")).keySet()).containsOnly(new SubCategory("email"));
        assertThat(def.propertiesByCategory(null).get(new Category("general")).keySet().iterator().next().isSpecial()).isTrue();
        assertThat(def.propertiesByCategory(null).get(new Category("security")).keySet()).containsOnly(new SubCategory("encryption"));
        assertThat(def.propertiesByCategory(null).get(new Category("security")).keySet().iterator().next().isSpecial()).isTrue();
        assertThat(def.propertiesByCategory(null).get(new Category("licenses")).keySet()).containsOnly(new SubCategory("server_id"));
        assertThat(def.propertiesByCategory(null).get(new Category("licenses")).keySet().iterator().next().isSpecial()).isTrue();
    }

    @Test
    public void should_group_by_category() {
        PropertyDefinitions def = new PropertyDefinitions(PropertyDefinition.builder("global1").name("Global1").category("catGlobal1").build(), PropertyDefinition.builder("global2").name("Global2").category("catGlobal1").build(), PropertyDefinition.builder("global3").name("Global3").category("catGlobal2").build(), PropertyDefinition.builder("project").name("Project").category("catProject").onlyOnQualifiers(PROJECT).build(), PropertyDefinition.builder("module").name("Module").category("catModule").onlyOnQualifiers(MODULE).build(), PropertyDefinition.builder("view").name("View").category("catView").onlyOnQualifiers(VIEW).build(), PropertyDefinition.builder("app").name("Application").category("catApp").onlyOnQualifiers(APP).build());
        assertThat(def.propertiesByCategory(null).keySet()).contains(new Category("catGlobal1"), new Category("catGlobal2"));
        assertThat(def.propertiesByCategory(PROJECT).keySet()).containsOnly(new Category("catProject"));
        assertThat(def.propertiesByCategory(MODULE).keySet()).containsOnly(new Category("catModule"));
        assertThat(def.propertiesByCategory(VIEW).keySet()).containsOnly(new Category("catView"));
        assertThat(def.propertiesByCategory(APP).keySet()).containsOnly(new Category("catApp"));
        assertThat(def.propertiesByCategory("Unkown").keySet()).isEmpty();
    }

    @Test
    public void should_group_by_subcategory() {
        PropertyDefinitions def = new PropertyDefinitions(PropertyDefinition.builder("global1").name("Global1").category("catGlobal1").subCategory("sub1").build(), PropertyDefinition.builder("global2").name("Global2").category("catGlobal1").subCategory("sub2").build(), PropertyDefinition.builder("global3").name("Global3").category("catGlobal1").build(), PropertyDefinition.builder("global4").name("Global4").category("catGlobal2").build());
        assertThat(def.propertiesByCategory(null).get(new Category("catGlobal1")).keySet()).containsOnly(new SubCategory("catGlobal1"), new SubCategory("sub1"), new SubCategory("sub2"));
        assertThat(def.propertiesByCategory(null).get(new Category("catGlobal2")).keySet()).containsOnly(new SubCategory("catGlobal2"));
    }

    @Test
    public void should_group_by_category_on_annotation_plugin() {
        PropertyDefinitions def = new PropertyDefinitions(PropertyDefinitionsTest.ByCategory.class);
        assertThat(def.propertiesByCategory(null).keySet()).contains(new Category("catglobal1"), new Category("catglobal2"));
        assertThat(def.propertiesByCategory(PROJECT).keySet()).containsOnly(new Category("catproject"));
        assertThat(def.propertiesByCategory(MODULE).keySet()).containsOnly(new Category("catmodule"));
    }

    @Test
    public void validKey_throws_NPE_if_key_is_null() {
        PropertyDefinitions underTest = new PropertyDefinitions();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("key can't be null");
        underTest.validKey(null);
    }

    @Test
    public void get_throws_NPE_if_key_is_null() {
        PropertyDefinitions underTest = new PropertyDefinitions();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("key can't be null");
        underTest.get(null);
    }

    @Test
    public void get_trims_key_before_looking_for_replacement() {
        Random random = new Random();
        String key = RandomStringUtils.randomAlphanumeric(4);
        String deprecatedKey = RandomStringUtils.randomAlphanumeric(4);
        PropertyDefinitions underTest = new PropertyDefinitions(Collections.singletonList(PropertyDefinition.builder(key).deprecatedKey(deprecatedKey).build()));
        String untrimmedKey = ((PropertyDefinitionsTest.blank(random)) + deprecatedKey) + (PropertyDefinitionsTest.blank(random));
        assertThat(underTest.get(untrimmedKey).key()).describedAs("expecting key %s being returned for get(%s)", key, untrimmedKey).isEqualTo(key);
    }

    @Property(key = "foo", name = "Foo")
    static final class PluginWithProperty {}

    @Properties({ @Property(key = "one", name = "One"), @Property(key = "two", name = "Two", defaultValue = "2") })
    static final class PluginWithProperties {}

    @Properties({ @Property(key = "inCateg", name = "In Categ", category = "categ"), @Property(key = "noCateg", name = "No categ") })
    static final class Categories {}

    @Properties({ @Property(key = "global1", name = "Global1", category = "catGlobal1", global = true, project = false, module = false), @Property(key = "global2", name = "Global2", category = "catGlobal1", global = true, project = false, module = false), @Property(key = "global3", name = "Global3", category = "catGlobal2", global = true, project = false, module = false), @Property(key = "project", name = "Project", category = "catProject", global = false, project = true, module = false), @Property(key = "module", name = "Module", category = "catModule", global = false, project = false, module = true) })
    static final class ByCategory {}
}

