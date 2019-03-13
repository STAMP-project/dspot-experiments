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


import Status.COMPATIBLE;
import WebService.Action;
import WebService.Controller;
import WebService.NewController;
import WebService.Param;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.plugins.PluginDownloader;
import org.sonar.server.plugins.UpdateCenterMatrixFactory;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;
import org.sonar.updatecenter.common.Plugin;
import org.sonar.updatecenter.common.PluginUpdate;
import org.sonar.updatecenter.common.UpdateCenter;
import org.sonar.updatecenter.common.Version;


public class UpdateActionTest {
    private static final String DUMMY_CONTROLLER_KEY = "dummy";

    private static final String CONTROLLER_KEY = "api/plugins";

    private static final String ACTION_KEY = "update";

    private static final String KEY_PARAM = "key";

    private static final String PLUGIN_KEY = "pluginKey";

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    private UpdateCenterMatrixFactory updateCenterFactory = Mockito.mock(UpdateCenterMatrixFactory.class);

    private UpdateCenter updateCenter = Mockito.mock(UpdateCenter.class);

    private PluginDownloader pluginDownloader = Mockito.mock(PluginDownloader.class);

    private UpdateAction underTest = new UpdateAction(updateCenterFactory, pluginDownloader, userSessionRule);

    private WsTester wsTester = new WsTester(new PluginsWs(underTest));

    private Request invalidRequest = wsTester.newGetRequest(UpdateActionTest.CONTROLLER_KEY, UpdateActionTest.ACTION_KEY);

    private Request validRequest = wsTester.newGetRequest(UpdateActionTest.CONTROLLER_KEY, UpdateActionTest.ACTION_KEY).setParam(UpdateActionTest.KEY_PARAM, UpdateActionTest.PLUGIN_KEY);

    private WsTester.TestResponse response = new WsTester.TestResponse();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void action_update_is_defined() {
        logInAsSystemAdministrator();
        WsTester wsTester = new WsTester();
        WebService.NewController newController = wsTester.context().createController(UpdateActionTest.DUMMY_CONTROLLER_KEY);
        underTest.define(newController);
        newController.done();
        WebService.Controller controller = wsTester.controller(UpdateActionTest.DUMMY_CONTROLLER_KEY);
        assertThat(controller.actions()).extracting("key").containsExactly(UpdateActionTest.ACTION_KEY);
        WebService.Action action = controller.actions().iterator().next();
        assertThat(action.isPost()).isTrue();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExample()).isNull();
        assertThat(action.params()).hasSize(1);
        WebService.Param key = action.param(UpdateActionTest.KEY_PARAM);
        assertThat(key).isNotNull();
        assertThat(key.isRequired()).isTrue();
        assertThat(key.description()).isNotNull();
    }

    @Test
    public void IAE_is_raised_when_key_param_is_not_provided() throws Exception {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        underTest.handle(invalidRequest, response);
    }

    @Test
    public void IAE_is_raised_when_there_is_no_plugin_update_for_the_key() throws Exception {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No plugin with key 'pluginKey'");
        underTest.handle(validRequest, response);
    }

    @Test
    public void IAE_is_raised_when_update_center_is_unavailable() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(updateCenterFactory.getUpdateCenter(ArgumentMatchers.anyBoolean())).thenReturn(Optional.absent());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No plugin with key 'pluginKey'");
        underTest.handle(validRequest, response);
    }

    @Test
    public void if_plugin_has_an_update_download_is_triggered_with_latest_version_from_updatecenter() throws Exception {
        logInAsSystemAdministrator();
        Version version = Version.create("1.0");
        Mockito.when(updateCenter.findPluginUpdates()).thenReturn(ImmutableList.of(PluginUpdate.createWithStatus(new org.sonar.updatecenter.common.Release(Plugin.factory(UpdateActionTest.PLUGIN_KEY), version), COMPATIBLE)));
        underTest.handle(validRequest, response);
        Mockito.verify(pluginDownloader).download(UpdateActionTest.PLUGIN_KEY, version);
        assertThat(response.outputAsString()).isEmpty();
    }
}

