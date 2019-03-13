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
import WebService.Param;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.WebService;
import org.sonar.core.platform.PluginInfo;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.plugins.PluginUninstaller;
import org.sonar.server.plugins.ServerPluginRepository;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;


@RunWith(DataProviderRunner.class)
public class UninstallActionTest {
    private static final String DUMMY_CONTROLLER_KEY = "dummy";

    private static final String CONTROLLER_KEY = "api/plugins";

    private static final String ACTION_KEY = "uninstall";

    private static final String KEY_PARAM = "key";

    private static final String PLUGIN_KEY = "findbugs";

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ServerPluginRepository serverPluginRepository = Mockito.mock(ServerPluginRepository.class);

    private PluginUninstaller pluginUninstaller = Mockito.mock(PluginUninstaller.class);

    private UninstallAction underTest = new UninstallAction(serverPluginRepository, pluginUninstaller, userSessionRule);

    private WsTester wsTester = new WsTester(new PluginsWs(underTest));

    private Request invalidRequest = wsTester.newGetRequest(UninstallActionTest.CONTROLLER_KEY, UninstallActionTest.ACTION_KEY);

    private Request validRequest = wsTester.newGetRequest(UninstallActionTest.CONTROLLER_KEY, UninstallActionTest.ACTION_KEY).setParam(UninstallActionTest.KEY_PARAM, UninstallActionTest.PLUGIN_KEY);

    private WsTester.TestResponse response = new WsTester.TestResponse();

    @Test
    public void request_fails_with_ForbiddenException_when_user_is_not_logged_in() throws Exception {
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        underTest.handle(validRequest, response);
    }

    @Test
    public void request_fails_with_ForbiddenException_when_user_is_not_system_administrator() throws Exception {
        userSessionRule.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        underTest.handle(validRequest, response);
    }

    @Test
    public void action_uninstall_is_defined() {
        logInAsSystemAdministrator();
        WsTester wsTester = new WsTester();
        WebService.NewController newController = wsTester.context().createController(UninstallActionTest.DUMMY_CONTROLLER_KEY);
        underTest.define(newController);
        newController.done();
        WebService.Controller controller = wsTester.controller(UninstallActionTest.DUMMY_CONTROLLER_KEY);
        assertThat(controller.actions()).extracting("key").containsExactly(UninstallActionTest.ACTION_KEY);
        WebService.Action action = controller.actions().iterator().next();
        assertThat(action.isPost()).isTrue();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExample()).isNull();
        assertThat(action.params()).hasSize(1);
        WebService.Param keyParam = action.param(UninstallActionTest.KEY_PARAM);
        assertThat(keyParam).isNotNull();
        assertThat(keyParam.isRequired()).isTrue();
        assertThat(keyParam.description()).isNotNull();
    }

    @Test
    public void IAE_is_raised_when_key_param_is_not_provided() throws Exception {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        underTest.handle(invalidRequest, response);
    }

    @Test
    public void do_not_attempt_uninstall_if_no_plugin_in_repository_for_specified_key() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(serverPluginRepository.getPluginInfo(UninstallActionTest.PLUGIN_KEY)).thenReturn(null);
        underTest.handle(validRequest, response);
        Mockito.verifyZeroInteractions(pluginUninstaller);
    }

    @Test
    public void if_plugin_is_installed_uninstallation_is_triggered() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(serverPluginRepository.getPluginInfo(UninstallActionTest.PLUGIN_KEY)).thenReturn(new PluginInfo(UninstallActionTest.PLUGIN_KEY));
        underTest.handle(validRequest, response);
        Mockito.verify(pluginUninstaller).uninstall(UninstallActionTest.PLUGIN_KEY);
        assertThat(response.outputAsString()).isEmpty();
    }
}

