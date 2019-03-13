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
package org.sonar.server.setting.ws;


import OrganizationPermission.ADMINISTER;
import ProcessProperties.Property.JDBC_URL;
import PropertyType.PROPERTY_SET;
import Settings.Setting;
import System2.INSTANCE;
import UserRole.USER;
import WebService.Action;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.PropertyFieldDefinition;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.property.PropertyDbTester;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonar.test.JsonAssert;
import org.sonarqube.ws.Settings;
import org.sonarqube.ws.Settings.ValuesWsResponse;


public class ValuesActionTest {
    private static Joiner COMMA_JOINER = Joiner.on(",");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private PropertyDbTester propertyDb = new PropertyDbTester(db);

    private ComponentDbTester componentDb = new ComponentDbTester(db);

    private PropertyDefinitions definitions = new PropertyDefinitions();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private SettingsWsSupport support = new SettingsWsSupport(defaultOrganizationProvider, userSession);

    private ComponentDto project;

    @Test
    public void return_simple_value() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("one"));
        ValuesWsResponse result = executeRequestForGlobalProperties("foo");
        assertThat(result.getSettingsList()).hasSize(1);
        Settings.Setting value = result.getSettings(0);
        assertThat(value.getKey()).isEqualTo("foo");
        assertThat(value.getValue()).isEqualTo("one");
        assertThat(value.getInherited()).isFalse();
    }

    @Test
    public void return_multi_values() {
        logIn();
        // Property never defined, default value is returned
        definitions.addComponent(PropertyDefinition.builder("default").multiValues(true).defaultValue("one,two").build());
        // Property defined at global level
        definitions.addComponent(PropertyDefinition.builder("global").multiValues(true).build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("global").setValue("three,four"));
        ValuesWsResponse result = executeRequestForGlobalProperties("default", "global");
        assertThat(result.getSettingsList()).hasSize(2);
        Settings.Setting foo = result.getSettings(0);
        assertThat(foo.getKey()).isEqualTo("default");
        assertThat(foo.getValues().getValuesList()).containsOnly("one", "two");
        Settings.Setting bar = result.getSettings(1);
        assertThat(bar.getKey()).isEqualTo("global");
        assertThat(bar.getValues().getValuesList()).containsOnly("three", "four");
    }

    @Test
    public void return_multi_value_with_coma() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("global").multiValues(true).build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("global").setValue("three,four%2Cfive"));
        ValuesWsResponse result = executeRequestForGlobalProperties("global");
        assertThat(result.getSettingsList()).hasSize(1);
        Settings.Setting setting = result.getSettings(0);
        assertThat(setting.getKey()).isEqualTo("global");
        assertThat(setting.getValues().getValuesList()).containsOnly("three", "four,five");
    }

    @Test
    public void return_property_set() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").type(PROPERTY_SET).fields(Arrays.asList(PropertyFieldDefinition.build("key").name("Key").build(), PropertyFieldDefinition.build("size").name("Size").build())).build());
        propertyDb.insertPropertySet("foo", null, ImmutableMap.of("key", "key1", "size", "size1"), ImmutableMap.of("key", "key2"));
        ValuesWsResponse result = executeRequestForGlobalProperties("foo");
        assertThat(result.getSettingsList()).hasSize(1);
        Settings.Setting value = result.getSettings(0);
        assertThat(value.getKey()).isEqualTo("foo");
        assertFieldValues(value, ImmutableMap.of("key", "key1", "size", "size1"), ImmutableMap.of("key", "key2"));
    }

    @Test
    public void return_property_set_for_component() {
        logInAsProjectUser();
        definitions.addComponent(PropertyDefinition.builder("foo").type(PROPERTY_SET).onQualifiers(PROJECT).fields(Arrays.asList(PropertyFieldDefinition.build("key").name("Key").build(), PropertyFieldDefinition.build("size").name("Size").build())).build());
        propertyDb.insertPropertySet("foo", project, ImmutableMap.of("key", "key1", "size", "size1"), ImmutableMap.of("key", "key2"));
        ValuesWsResponse result = executeRequestForProjectProperties("foo");
        assertThat(result.getSettingsList()).hasSize(1);
        Settings.Setting value = result.getSettings(0);
        assertThat(value.getKey()).isEqualTo("foo");
        assertFieldValues(value, ImmutableMap.of("key", "key1", "size", "size1"), ImmutableMap.of("key", "key2"));
    }

    @Test
    public void return_default_values() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").defaultValue("default").build());
        ValuesWsResponse result = executeRequestForGlobalProperties("foo");
        assertThat(result.getSettingsList()).hasSize(1);
        assertSetting(result.getSettings(0), "foo", "default", true);
    }

    @Test
    public void return_global_values() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("property").defaultValue("default").build());
        // The property is overriding default value
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("property").setValue("one"));
        ValuesWsResponse result = executeRequestForGlobalProperties("property");
        assertThat(result.getSettingsList()).hasSize(1);
        assertSetting(result.getSettings(0), "property", "one", false);
    }

    @Test
    public void return_project_values() {
        logInAsProjectUser();
        definitions.addComponent(PropertyDefinition.builder("property").defaultValue("default").onQualifiers(PROJECT).build());
        // The property is overriding global value
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("property").setValue("one"), newComponentPropertyDto(project).setKey("property").setValue("two"));
        ValuesWsResponse result = executeRequestForProjectProperties("property");
        assertThat(result.getSettingsList()).hasSize(1);
        assertSetting(result.getSettings(0), "property", "two", false);
    }

    @Test
    public void return_settings_defined_only_at_global_level_when_loading_project_settings() {
        logInAsProjectUser();
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("global").build(), PropertyDefinition.builder("global.default").defaultValue("default").build(), PropertyDefinition.builder("project").onQualifiers(PROJECT).build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("global").setValue("one"), newComponentPropertyDto(project).setKey("project").setValue("two"));
        ValuesWsResponse result = executeRequestForProjectProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey, Settings.Setting::getValue).containsOnly(tuple("project", "two"), tuple("global.default", "default"), tuple("global", "one"));
    }

    @Test
    public void return_is_inherited_to_true_when_property_is_defined_only_at_global_level() {
        logInAsProjectUser();
        definitions.addComponent(PropertyDefinition.builder("property").defaultValue("default").onQualifiers(PROJECT).build());
        // The property is not defined on project
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("property").setValue("one"));
        ValuesWsResponse result = executeRequestForProjectProperties("property");
        assertThat(result.getSettingsList()).hasSize(1);
        assertSetting(result.getSettings(0), "property", "one", true);
    }

    @Test
    public void return_values_even_if_no_property_definition() {
        logIn();
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("globalPropertyWithoutDefinition").setValue("value"));
        ValuesWsResponse result = executeRequestForGlobalProperties("globalPropertyWithoutDefinition");
        Settings.Setting globalPropertyWithoutDefinitionValue = result.getSettings(0);
        assertThat(globalPropertyWithoutDefinitionValue.getKey()).isEqualTo("globalPropertyWithoutDefinition");
        assertThat(globalPropertyWithoutDefinitionValue.getValue()).isEqualTo("value");
        assertThat(globalPropertyWithoutDefinitionValue.getInherited()).isFalse();
    }

    @Test
    public void return_values_of_component_even_if_no_property_definition() {
        logInAsProjectUser();
        propertyDb.insertProperties(newComponentPropertyDto(project).setKey("property").setValue("foo"));
        ValuesWsResponse response = executeRequestForComponentProperties(project, "property");
        assertThat(response.getSettingsCount()).isEqualTo(1);
        assertSetting(response.getSettings(0), "property", "foo", false);
    }

    @Test
    public void return_empty_when_property_def_exists_but_no_value() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").build());
        ValuesWsResponse result = executeRequestForGlobalProperties("foo");
        assertThat(result.getSettingsList()).isEmpty();
    }

    @Test
    public void return_nothing_when_unknown_keys() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").defaultValue("default").build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("bar").setValue(""));
        ValuesWsResponse result = executeRequestForGlobalProperties("unknown");
        assertThat(result.getSettingsList()).isEmpty();
    }

    @Test
    public void return_module_values() {
        logInAsProjectUser();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project));
        definitions.addComponent(PropertyDefinition.builder("property").defaultValue("default").onQualifiers(PROJECT, MODULE).build());
        // The property is overriding global value
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("property").setValue("one"), newComponentPropertyDto(module).setKey("property").setValue("two"));
        ValuesWsResponse result = executeRequestForComponentProperties(module, "property");
        assertThat(result.getSettingsList()).hasSize(1);
        assertSetting(result.getSettings(0), "property", "two", false);
    }

    @Test
    public void return_inherited_values_on_module() {
        logInAsProjectUser();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project));
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("defaultProperty").defaultValue("default").onQualifiers(PROJECT, MODULE).build(), PropertyDefinition.builder("globalProperty").onQualifiers(PROJECT, MODULE).build(), PropertyDefinition.builder("projectProperty").onQualifiers(PROJECT, MODULE).build(), PropertyDefinition.builder("moduleProperty").onQualifiers(PROJECT, MODULE).build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("globalProperty").setValue("global"), newComponentPropertyDto(project).setKey("projectProperty").setValue("project"), newComponentPropertyDto(module).setKey("moduleProperty").setValue("module"));
        ValuesWsResponse result = executeRequestForComponentProperties(module, "defaultProperty", "globalProperty", "projectProperty", "moduleProperty");
        assertThat(result.getSettingsList()).hasSize(4);
        assertSetting(result.getSettings(0), "defaultProperty", "default", true);
        assertSetting(result.getSettings(1), "globalProperty", "global", true);
        assertSetting(result.getSettings(2), "projectProperty", "project", true);
        assertSetting(result.getSettings(3), "moduleProperty", "module", false);
    }

    @Test
    public void return_inherited_values_on_global_setting() {
        logIn();
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("defaultProperty").defaultValue("default").build(), PropertyDefinition.builder("globalProperty").build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("globalProperty").setValue("global"));
        ValuesWsResponse result = executeRequestForGlobalProperties("defaultProperty", "globalProperty");
        assertThat(result.getSettingsList()).hasSize(2);
        assertSetting(result.getSettings(0), "defaultProperty", "default", true);
        assertSetting(result.getSettings(1), "globalProperty", "global", false);
    }

    @Test
    public void return_parent_value() {
        logInAsProjectUser();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project));
        ComponentDto subModule = componentDb.insertComponent(newModuleDto(module));
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").defaultValue("default").onQualifiers(PROJECT, MODULE).build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("global"), newComponentPropertyDto(project).setKey("foo").setValue("project"), newComponentPropertyDto(module).setKey("foo").setValue("module"));
        assertParentValue(executeRequestForComponentProperties(subModule, "foo").getSettings(0), "module");
        assertParentValue(executeRequestForComponentProperties(module, "foo").getSettings(0), "project");
        assertParentValue(executeRequestForComponentProperties(project, "foo").getSettings(0), "global");
        assertParentValue(executeRequestForGlobalProperties("foo").getSettings(0), "default");
    }

    @Test
    public void return_parent_values() {
        logInAsProjectUser();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project));
        ComponentDto subModule = componentDb.insertComponent(newModuleDto(module));
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").defaultValue("default1,default2").multiValues(true).onQualifiers(PROJECT, MODULE).build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("global1,global2"), newComponentPropertyDto(project).setKey("foo").setValue("project1,project2"), newComponentPropertyDto(module).setKey("foo").setValue("module1,module2"));
        assertParentValues(executeRequestForComponentProperties(subModule, "foo").getSettings(0), "module1", "module2");
        assertParentValues(executeRequestForComponentProperties(module, "foo").getSettings(0), "project1", "project2");
        assertParentValues(executeRequestForComponentProperties(project, "foo").getSettings(0), "global1", "global2");
        assertParentValues(executeRequestForGlobalProperties("foo").getSettings(0), "default1", "default2");
    }

    @Test
    public void return_parent_field_values() {
        logInAsProjectUser();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project));
        ComponentDto subModule = componentDb.insertComponent(newModuleDto(module));
        definitions.addComponent(PropertyDefinition.builder("foo").onQualifiers(PROJECT, MODULE).type(PROPERTY_SET).fields(Arrays.asList(PropertyFieldDefinition.build("key").name("Key").build(), PropertyFieldDefinition.build("size").name("Size").build())).build());
        propertyDb.insertPropertySet("foo", null, ImmutableMap.of("key", "keyG1", "size", "sizeG1"));
        propertyDb.insertPropertySet("foo", project, ImmutableMap.of("key", "keyP1", "size", "sizeP1"));
        propertyDb.insertPropertySet("foo", module, ImmutableMap.of("key", "keyM1", "size", "sizeM1"));
        assertParentFieldValues(executeRequestForComponentProperties(subModule, "foo").getSettings(0), ImmutableMap.of("key", "keyM1", "size", "sizeM1"));
        assertParentFieldValues(executeRequestForComponentProperties(module, "foo").getSettings(0), ImmutableMap.of("key", "keyP1", "size", "sizeP1"));
        assertParentFieldValues(executeRequestForComponentProperties(project, "foo").getSettings(0), ImmutableMap.of("key", "keyG1", "size", "sizeG1"));
        assertParentFieldValues(executeRequestForGlobalProperties("foo").getSettings(0));
    }

    @Test
    public void return_no_parent_value() {
        logInAsProjectUser();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project));
        ComponentDto subModule = componentDb.insertComponent(newModuleDto(module));
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("simple").onQualifiers(PROJECT, MODULE).build(), PropertyDefinition.builder("multi").multiValues(true).onQualifiers(PROJECT, MODULE).build(), PropertyDefinition.builder("set").type(PROPERTY_SET).onQualifiers(PROJECT, MODULE).fields(Arrays.asList(PropertyFieldDefinition.build("key").name("Key").build(), PropertyFieldDefinition.build("size").name("Size").build())).build()));
        propertyDb.insertProperties(newComponentPropertyDto(module).setKey("simple").setValue("module"), newComponentPropertyDto(module).setKey("multi").setValue("module1,module2"));
        propertyDb.insertPropertySet("set", module, ImmutableMap.of("key", "keyM1", "size", "sizeM1"));
        assertParentValue(executeRequestForComponentProperties(subModule, "simple").getSettings(0), null);
        assertParentValues(executeRequestForComponentProperties(subModule, "multi").getSettings(0));
        assertParentFieldValues(executeRequestForComponentProperties(subModule, "set").getSettings(0));
    }

    @Test
    public void return_parent_value_when_no_definition() {
        logInAsProjectUser();
        ComponentDto module = componentDb.insertComponent(newModuleDto(project));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("global"), newComponentPropertyDto(project).setKey("foo").setValue("project"));
        assertParentValue(executeRequestForComponentProperties(module, "foo").getSettings(0), "project");
        assertParentValue(executeRequestForComponentProperties(project, "foo").getSettings(0), "global");
        assertParentValue(executeRequestForGlobalProperties("foo").getSettings(0), null);
    }

    @Test
    public void return_value_of_deprecated_key() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").deprecatedKey("deprecated").build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("one"));
        ValuesWsResponse result = executeRequestForGlobalProperties("deprecated");
        assertThat(result.getSettingsList()).hasSize(1);
        Settings.Setting value = result.getSettings(0);
        assertThat(value.getKey()).isEqualTo("deprecated");
        assertThat(value.getValue()).isEqualTo("one");
    }

    @Test
    public void do_not_return_secured_settings_when_not_authenticated() {
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").build(), PropertyDefinition.builder("secret.secured").build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("one"), newGlobalPropertyDto().setKey("secret.secured").setValue("password"));
        ValuesWsResponse result = executeRequestForGlobalProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("foo");
    }

    @Test
    public void do_not_return_secured_settings_in_property_set_when_not_authenticated() {
        definitions.addComponent(PropertyDefinition.builder("foo").type(PROPERTY_SET).fields(Arrays.asList(PropertyFieldDefinition.build("key").name("Key").build(), PropertyFieldDefinition.build("secret.secured").name("Secured").build())).build());
        propertyDb.insertPropertySet("foo", null, ImmutableMap.of("key", "key1", "secret.secured", "123456"));
        ValuesWsResponse result = executeRequestForGlobalProperties();
        assertFieldValues(result.getSettings(0), ImmutableMap.of("key", "key1"));
    }

    @Test
    public void return_global_secured_settings_when_not_authenticated_but_with_scan_permission() {
        userSession.anonymous().addPermission(SCAN, db.getDefaultOrganization());
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").build(), PropertyDefinition.builder("secret.secured").build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("one"), newGlobalPropertyDto().setKey("secret.secured").setValue("password"));
        ValuesWsResponse result = executeRequestForGlobalProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("foo", "secret.secured");
    }

    @Test
    public void return_component_secured_settings_when_not_authenticated_but_with_project_scan_permission() {
        userSession.addProjectPermission(UserRole.USER, project).addProjectPermission(SCAN_EXECUTION, project);
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").onQualifiers(PROJECT).build(), PropertyDefinition.builder("global.secret.secured").build(), PropertyDefinition.builder("secret.secured").onQualifiers(PROJECT).build()));
        propertyDb.insertProperties(newComponentPropertyDto(project).setKey("foo").setValue("one"), newGlobalPropertyDto().setKey("global.secret.secured").setValue("very secret"), newComponentPropertyDto(project).setKey("secret.secured").setValue("password"));
        ValuesWsResponse result = executeRequestForProjectProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("foo", "global.secret.secured", "secret.secured");
    }

    @Test
    public void return_component_secured_settings_even_if_not_defined_when_not_authenticated_but_with_scan_permission() {
        userSession.addProjectPermission(UserRole.USER, project).addProjectPermission(SCAN_EXECUTION, project);
        propertyDb.insertProperties(newComponentPropertyDto(project).setKey("not-defined.secured").setValue("123"));
        ValuesWsResponse result = executeRequestForProjectProperties("not-defined.secured");
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("not-defined.secured");
    }

    @Test
    public void return_secured_settings_when_system_admin() {
        logInAsAdmin();
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").build(), PropertyDefinition.builder("secret.secured").build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("one"), newGlobalPropertyDto().setKey("secret.secured").setValue("password"));
        ValuesWsResponse result = executeRequestForGlobalProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("foo", "secret.secured");
    }

    @Test
    public void return_secured_settings_when_project_admin() {
        logInAsProjectAdmin();
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").onQualifiers(PROJECT).build(), PropertyDefinition.builder("global.secret.secured").build(), PropertyDefinition.builder("secret.secured").onQualifiers(PROJECT).build()));
        propertyDb.insertProperties(newComponentPropertyDto(project).setKey("foo").setValue("one"), newGlobalPropertyDto().setKey("global.secret.secured").setValue("very secret"), newComponentPropertyDto(project).setKey("secret.secured").setValue("password"));
        ValuesWsResponse result = executeRequestForProjectProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("foo", "global.secret.secured", "secret.secured");
    }

    @Test
    public void return_secured_settings_even_if_not_defined_when_project_admin() {
        logInAsProjectAdmin();
        propertyDb.insertProperties(newComponentPropertyDto(project).setKey("not-defined.secured").setValue("123"));
        ValuesWsResponse result = executeRequestForProjectProperties("not-defined.secured");
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("not-defined.secured");
    }

    @Test
    public void return_secured_settings_in_property_set_when_system_admin() {
        logInAsAdmin();
        definitions.addComponent(PropertyDefinition.builder("foo").type(PROPERTY_SET).fields(Arrays.asList(PropertyFieldDefinition.build("key").name("Key").build(), PropertyFieldDefinition.build("secret.secured").name("Secured").build())).build());
        propertyDb.insertPropertySet("foo", null, ImmutableMap.of("key", "key1", "secret.secured", "123456"));
        ValuesWsResponse result = executeRequestForGlobalProperties();
        assertFieldValues(result.getSettings(0), ImmutableMap.of("key", "key1", "secret.secured", "123456"));
    }

    @Test
    public void return_global_settings_from_definitions_when_no_component_and_no_keys() {
        logInAsAdmin();
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").build(), PropertyDefinition.builder("secret.secured").build()));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("one"), newGlobalPropertyDto().setKey("secret.secured").setValue("password"));
        ValuesWsResponse result = executeRequestForGlobalProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("foo", "secret.secured");
    }

    @Test
    public void return_project_settings_from_definitions_when_component_and_no_keys() {
        logInAsProjectAdmin();
        definitions.addComponents(Arrays.asList(PropertyDefinition.builder("foo").onQualifiers(PROJECT).build(), PropertyDefinition.builder("secret.secured").onQualifiers(PROJECT).build()));
        propertyDb.insertProperties(newComponentPropertyDto(project).setKey("foo").setValue("one"), newComponentPropertyDto(project).setKey("secret.secured").setValue("password"));
        ValuesWsResponse result = executeRequestForProjectProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("foo", "secret.secured");
    }

    @Test
    public void return_additional_settings_specific_for_scanner_when_no_keys() {
        logInAsAdmin();
        definitions.addComponent(PropertyDefinition.builder("secret.secured").build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("sonar.core.id").setValue("ID"), newGlobalPropertyDto().setKey("sonar.core.startTime").setValue("2017-01-01"));
        ValuesWsResponse result = executeRequestForGlobalProperties();
        assertThat(result.getSettingsList()).extracting(Settings.Setting::getKey).containsOnly("sonar.core.id", "sonar.core.startTime");
    }

    @Test
    public void return_simple_value_with_non_ascii_characters() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("????"));
        ValuesWsResponse result = executeRequestForGlobalProperties("foo");
        assertThat(result.getSettings(0).getValue()).isEqualTo("????");
    }

    @Test
    public void branch_values() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        definitions.addComponent(PropertyDefinition.builder("sonar.leak.period").onQualifiers(PROJECT).build());
        propertyDb.insertProperties(newComponentPropertyDto(branch).setKey("sonar.leak.period").setValue("two"));
        ValuesWsResponse result = newTester().newRequest().setParam("keys", "sonar.leak.period").setParam("component", branch.getKey()).setParam("branch", branch.getBranch()).executeProtobuf(ValuesWsResponse.class);
        assertThat(result.getSettingsList()).hasSize(1);
        assertSetting(result.getSettings(0), "sonar.leak.period", "two", false);
    }

    @Test
    public void branch_values_inherit_from_project() {
        ComponentDto project = db.components().insertMainBranch();
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        definitions.addComponent(PropertyDefinition.builder("sonar.leak.period").onQualifiers(PROJECT).build());
        propertyDb.insertProperties(newComponentPropertyDto(project).setKey("sonar.leak.period").setValue("two"));
        ValuesWsResponse result = newTester().newRequest().setParam("keys", "sonar.leak.period").setParam("component", branch.getKey()).setParam("branch", branch.getBranch()).executeProtobuf(ValuesWsResponse.class);
        assertThat(result.getSettingsList()).hasSize(1);
        assertSetting(result.getSettings(0), "sonar.leak.period", "two", true);
    }

    @Test
    public void fail_when_user_has_not_project_browse_permission() {
        userSession.logIn("project-admin").addProjectPermission(UserRole.CODEVIEWER, project);
        definitions.addComponent(PropertyDefinition.builder("foo").build());
        expectedException.expect(ForbiddenException.class);
        executeRequest(project.getDbKey(), "foo");
    }

    @Test
    public void fail_when_deprecated_key_and_new_key_are_used() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("foo").deprecatedKey("deprecated").build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("foo").setValue("one"));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'foo' and 'deprecated' cannot be used at the same time as they refer to the same setting");
        executeRequestForGlobalProperties("foo", "deprecated");
    }

    @Test
    public void fail_when_component_not_found() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Component key 'unknown' not found");
        newTester().newRequest().setParam("keys", "foo").setParam("component", "unknown").execute();
    }

    @Test
    public void fail_when_branch_not_found() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        String settingKey = "not_allowed_on_branch";
        userSession.logIn().addProjectPermission(UserRole.USER, project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component '%s' on branch 'unknown' not found", branch.getKey()));
        newTester().newRequest().setParam("keys", settingKey).setParam("component", branch.getKey()).setParam("branch", "unknown").execute();
    }

    @Test
    public void test_example_json_response() {
        logIn();
        definitions.addComponent(PropertyDefinition.builder("sonar.test.jira").defaultValue("abc").build());
        definitions.addComponent(PropertyDefinition.builder("sonar.autogenerated").multiValues(true).build());
        propertyDb.insertProperties(newGlobalPropertyDto().setKey("sonar.autogenerated").setValue("val1,val2,val3"));
        definitions.addComponent(PropertyDefinition.builder("sonar.demo").type(PROPERTY_SET).fields(PropertyFieldDefinition.build("text").name("Text").build(), PropertyFieldDefinition.build("boolean").name("Boolean").build()).build());
        propertyDb.insertPropertySet("sonar.demo", null, ImmutableMap.of("text", "foo", "boolean", "true"), ImmutableMap.of("text", "bar", "boolean", "false"));
        String result = newTester().newRequest().setParam("keys", "sonar.test.jira,sonar.autogenerated,sonar.demo").setMediaType(JSON).execute().getInput();
        JsonAssert.assertJson(newTester().getDef().responseExampleAsString()).isSimilarTo(result);
    }

    @Test
    public void fail_when_using_branch_db_key() throws Exception {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertMainBranch(organization);
        userSession.logIn().addProjectPermission(USER, project);
        ComponentDto branch = db.components().insertProjectBranch(project);
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(String.format("Component key '%s' not found", branch.getDbKey()));
        newTester().newRequest().setParam("keys", "foo").setParam("component", branch.getDbKey()).execute();
    }

    @Test
    public void fail_when_setting_key_is_defined_in_sonar_properties() {
        ComponentDto project = db.components().insertPrivateProject();
        userSession.logIn().addProjectPermission(USER, project);
        String settingKey = JDBC_URL.getKey();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Setting '%s' can only be used in sonar.properties", settingKey));
        newTester().newRequest().setParam("keys", settingKey).setParam("component", project.getKey()).execute();
    }

    @Test
    public void test_ws_definition() {
        WebService.Action action = newTester().getDef();
        assertThat(action).isNotNull();
        assertThat(action.isInternal()).isFalse();
        assertThat(action.isPost()).isFalse();
        assertThat(action.responseExampleAsString()).isNotEmpty();
        assertThat(action.params()).extracting(WebService.Param::key).containsExactlyInAnyOrder("keys", "component", "branch", "pullRequest");
    }

    @Test
    public void sonarcloud_global_secured_properties_require_system_admin_permission() {
        PropertyDefinition securedDef = PropertyDefinition.builder("my.password.secured").build();
        PropertyDefinition standardDef = PropertyDefinition.builder("my.property").build();
        definitions.addComponents(Arrays.asList(securedDef, standardDef));
        propertyDb.insertProperties(newGlobalPropertyDto().setKey(securedDef.key()).setValue("securedValue"), newGlobalPropertyDto().setKey(standardDef.key()).setValue("standardValue"));
        // anonymous
        WsActionTester tester = newSonarCloudTester();
        ValuesWsResponse response = executeRequest(tester, null, securedDef.key(), standardDef.key());
        assertThat(response.getSettingsList()).extracting(Settings.Setting::getValue).containsExactly("standardValue");
        // organization administrator but not system administrator
        userSession.logIn().addPermission(OrganizationPermission.SCAN, db.getDefaultOrganization());
        response = executeRequest(tester, null, securedDef.key(), standardDef.key());
        assertThat(response.getSettingsList()).extracting(Settings.Setting::getValue).containsExactly("standardValue");
        // organization administrator
        userSession.logIn().addPermission(ADMINISTER, db.getDefaultOrganization());
        response = executeRequest(tester, null, securedDef.key(), standardDef.key());
        assertThat(response.getSettingsList()).extracting(Settings.Setting::getValue).containsExactly("standardValue");
        // system administrator
        userSession.logIn().setSystemAdministrator();
        response = executeRequest(tester, null, securedDef.key(), standardDef.key());
        assertThat(response.getSettingsList()).extracting(Settings.Setting::getValue).containsExactlyInAnyOrder("securedValue", "standardValue");
    }
}

