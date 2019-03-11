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
package org.sonar.server.plugins.ws;


import WebService.Action;
import WebService.Controller;
import WebService.NewController;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.DateUtils;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsActionTester;
import org.sonar.server.ws.WsTester;
import org.sonar.updatecenter.common.Plugin;
import org.sonar.updatecenter.common.Release;


public class AvailableActionTest extends AbstractUpdateCenterBasedPluginsWsActionTest {
    private static final Plugin FULL_PROPERTIES_PLUGIN = Plugin.factory("pkey").setName("p_name").setCategory("p_category").setDescription("p_description").setLicense("p_license").setOrganization("p_orga_name").setOrganizationUrl("p_orga_url").setHomepageUrl("p_homepage_url").setIssueTrackerUrl("p_issue_url").setTermsConditionsUrl("p_t_and_c_url");

    private static final Release FULL_PROPERTIES_PLUGIN_RELEASE = AbstractUpdateCenterBasedPluginsWsActionTest.release(AvailableActionTest.FULL_PROPERTIES_PLUGIN, "1.12.1").setDate(DateUtils.parseDate("2015-04-16")).setDownloadUrl("http://p_file.jar").addOutgoingDependency(AbstractUpdateCenterBasedPluginsWsActionTest.release(AbstractUpdateCenterBasedPluginsWsActionTest.PLUGIN_1, "0.3.6")).addOutgoingDependency(AbstractUpdateCenterBasedPluginsWsActionTest.release(AbstractUpdateCenterBasedPluginsWsActionTest.PLUGIN_2, "1.0.0"));

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AvailableAction underTest = new AvailableAction(userSession, updateCenterFactory);

    @Test
    public void action_available_is_defined() {
        logInAsSystemAdministrator();
        WsTester wsTester = new WsTester();
        WebService.NewController newController = wsTester.context().createController(AbstractUpdateCenterBasedPluginsWsActionTest.DUMMY_CONTROLLER_KEY);
        underTest.define(newController);
        newController.done();
        WebService.Controller controller = wsTester.controller(AbstractUpdateCenterBasedPluginsWsActionTest.DUMMY_CONTROLLER_KEY);
        assertThat(controller.actions()).extracting("key").containsExactly("available");
        WebService.Action action = controller.actions().iterator().next();
        assertThat(action.isPost()).isFalse();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExample()).isNotNull();
    }

    @Test
    public void verify_example() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(updateCenter.findAvailablePlugins()).thenReturn(ImmutableList.of(AbstractUpdateCenterBasedPluginsWsActionTest.pluginUpdate(AbstractUpdateCenterBasedPluginsWsActionTest.release(Plugin.factory("abap").setName("ABAP").setCategory("Languages").setDescription("Enable analysis and reporting on ABAP projects").setLicense("Commercial").setOrganization("SonarSource").setOrganizationUrl("http://www.sonarsource.com").setTermsConditionsUrl("http://dist.sonarsource.com/SonarSource_Terms_And_Conditions.pdf"), "3.2").setDate(DateUtils.parseDate("2015-03-10")), COMPATIBLE), AbstractUpdateCenterBasedPluginsWsActionTest.pluginUpdate(AbstractUpdateCenterBasedPluginsWsActionTest.release(Plugin.factory("android").setName("Android").setCategory("Languages").setDescription("Import Android Lint reports.").setLicense("GNU LGPL 3").setOrganization("SonarSource and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores").setOrganizationUrl("http://www.sonarsource.com"), "1.0").setDate(DateUtils.parseDate("2014-03-31")).addOutgoingDependency(AbstractUpdateCenterBasedPluginsWsActionTest.release(Plugin.factory("java").setName("Java").setDescription("SonarQube rule engine."), "0.3.6")), COMPATIBLE)));
        underTest.handle(request, response);
        WsActionTester actionTester = new WsActionTester(underTest);
        assertJson(response.outputAsString()).isSimilarTo(actionTester.getDef().responseExampleAsString());
    }

    @Test
    public void request_fails_with_ForbiddenException_when_user_is_not_logged_in() throws Exception {
        expectedException.expect(ForbiddenException.class);
        underTest.handle(request, response);
    }

    @Test
    public void request_fails_with_ForbiddenException_when_user_is_not_system_administrator() throws Exception {
        userSession.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        underTest.handle(request, response);
    }

    @Test
    public void empty_array_is_returned_when_there_is_no_plugin_available() throws Exception {
        logInAsSystemAdministrator();
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(AbstractUpdateCenterBasedPluginsWsActionTest.JSON_EMPTY_PLUGIN_LIST);
    }

    @Test
    public void empty_array_is_returned_when_update_center_is_not_accessible() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(updateCenterFactory.getUpdateCenter(ArgumentMatchers.anyBoolean())).thenReturn(Optional.absent());
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(AbstractUpdateCenterBasedPluginsWsActionTest.JSON_EMPTY_PLUGIN_LIST);
    }

    @Test
    public void verify_properties_displayed_in_json_per_plugin() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(updateCenter.findAvailablePlugins()).thenReturn(ImmutableList.of(AbstractUpdateCenterBasedPluginsWsActionTest.pluginUpdate(AvailableActionTest.FULL_PROPERTIES_PLUGIN_RELEASE, COMPATIBLE)));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(("{" + ((((((((((((((((((((((((((((((((("  \"plugins\": [" + "    {") + "      \"key\": \"pkey\",") + "      \"name\": \"p_name\",") + "      \"category\": \"p_category\",") + "      \"description\": \"p_description\",") + "      \"license\": \"p_license\",") + "      \"termsAndConditionsUrl\": \"p_t_and_c_url\",") + "      \"organizationName\": \"p_orga_name\",") + "      \"organizationUrl\": \"p_orga_url\",") + "      \"homepageUrl\": \"p_homepage_url\",") + "      \"issueTrackerUrl\": \"p_issue_url\",") + "      \"release\": {") + "        \"version\": \"1.12.1\",") + "        \"date\": \"2015-04-16\"") + "      },") + "      \"update\": {") + "        \"status\": \"COMPATIBLE\",") + "        \"requires\": [") + "          {") + "            \"key\": \"pkey1\",") + "            \"name\": \"p_name_1\"") + "          },") + "          {") + "            \"key\": \"pkey2\",") + "            \"name\": \"p_name_2\",") + "            \"description\": \"p_desc_2\"") + "          }") + "        ]") + "      }") + "    }") + "  ],") + "  \"updateCenterRefresh\": \"2015-04-24T16:08:36+0200\"") + "}")));
    }

    @Test
    public void status_COMPATIBLE_is_displayed_COMPATIBLE_in_JSON() throws Exception {
        logInAsSystemAdministrator();
        checkStatusDisplayedInJson(COMPATIBLE, "COMPATIBLE");
    }

    @Test
    public void status_INCOMPATIBLE_is_displayed_INCOMPATIBLE_in_JSON() throws Exception {
        logInAsSystemAdministrator();
        checkStatusDisplayedInJson(INCOMPATIBLE, "INCOMPATIBLE");
    }

    @Test
    public void status_REQUIRE_SONAR_UPGRADE_is_displayed_REQUIRES_SYSTEM_UPGRADE_in_JSON() throws Exception {
        logInAsSystemAdministrator();
        checkStatusDisplayedInJson(REQUIRE_SONAR_UPGRADE, "REQUIRES_SYSTEM_UPGRADE");
    }

    @Test
    public void status_DEPENDENCIES_REQUIRE_SONAR_UPGRADE_is_displayed_DEPS_REQUIRE_SYSTEM_UPGRADE_in_JSON() throws Exception {
        logInAsSystemAdministrator();
        checkStatusDisplayedInJson(DEPENDENCIES_REQUIRE_SONAR_UPGRADE, "DEPS_REQUIRE_SYSTEM_UPGRADE");
    }
}

