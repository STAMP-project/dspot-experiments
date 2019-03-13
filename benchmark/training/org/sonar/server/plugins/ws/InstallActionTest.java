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


import PluginUpdate.Status.COMPATIBLE;
import WebService.Action;
import WebService.Controller;
import WebService.NewController;
import WebService.Param;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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


@RunWith(DataProviderRunner.class)
public class InstallActionTest {
    private static final String DUMMY_CONTROLLER_KEY = "dummy";

    private static final String CONTROLLER_KEY = "api/plugins";

    private static final String ACTION_KEY = "install";

    private static final String KEY_PARAM = "key";

    private static final String PLUGIN_KEY = "pluginKey";

    @Rule
    public UserSessionRule userSessionRule = UserSessionRule.standalone();

    private UpdateCenterMatrixFactory updateCenterFactory = Mockito.mock(UpdateCenterMatrixFactory.class);

    private UpdateCenter updateCenter = Mockito.mock(UpdateCenter.class);

    private PluginDownloader pluginDownloader = Mockito.mock(PluginDownloader.class);

    private InstallAction underTest = new InstallAction(updateCenterFactory, pluginDownloader, userSessionRule);

    private WsTester wsTester = new WsTester(new PluginsWs(underTest));

    private WsTester.TestRequest invalidRequest = wsTester.newPostRequest(InstallActionTest.CONTROLLER_KEY, InstallActionTest.ACTION_KEY);

    private WsTester.TestRequest validRequest = wsTester.newPostRequest(InstallActionTest.CONTROLLER_KEY, InstallActionTest.ACTION_KEY).setParam(InstallActionTest.KEY_PARAM, InstallActionTest.PLUGIN_KEY);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void request_fails_with_ForbiddenException_when_user_is_not_logged_in() throws Exception {
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        validRequest.execute();
    }

    @Test
    public void request_fails_with_ForbiddenException_when_user_is_not_system_administrator() throws Exception {
        userSessionRule.logIn().setNonSystemAdministrator();
        expectedException.expect(ForbiddenException.class);
        expectedException.expectMessage("Insufficient privileges");
        validRequest.execute();
    }

    @Test
    public void action_install_is_defined() {
        logInAsSystemAdministrator();
        WsTester wsTester = new WsTester();
        WebService.NewController newController = wsTester.context().createController(InstallActionTest.DUMMY_CONTROLLER_KEY);
        underTest.define(newController);
        newController.done();
        WebService.Controller controller = wsTester.controller(InstallActionTest.DUMMY_CONTROLLER_KEY);
        assertThat(controller.actions()).extracting("key").containsExactly(InstallActionTest.ACTION_KEY);
        WebService.Action action = controller.actions().iterator().next();
        assertThat(action.isPost()).isTrue();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExample()).isNull();
        assertThat(action.params()).hasSize(1);
        WebService.Param keyParam = action.param(InstallActionTest.KEY_PARAM);
        assertThat(keyParam).isNotNull();
        assertThat(keyParam.isRequired()).isTrue();
        assertThat(keyParam.description()).isNotNull();
    }

    @Test
    public void IAE_is_raised_when_key_param_is_not_provided() throws Exception {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        invalidRequest.execute();
    }

    @Test
    public void IAE_is_raised_when_there_is_no_available_plugin_for_the_key() throws Exception {
        logInAsSystemAdministrator();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No plugin with key 'pluginKey'");
        validRequest.execute();
    }

    @Test
    public void IAE_is_raised_when_update_center_is_unavailable() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(updateCenterFactory.getUpdateCenter(ArgumentMatchers.anyBoolean())).thenReturn(Optional.absent());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No plugin with key 'pluginKey'");
        validRequest.execute();
    }

    @Test
    public void if_plugin_is_found_available_download_is_triggered_with_latest_version_from_updatecenter() throws Exception {
        logInAsSystemAdministrator();
        Version version = Version.create("1.0");
        Mockito.when(updateCenter.findAvailablePlugins()).thenReturn(ImmutableList.of(PluginUpdate.createWithStatus(new org.sonar.updatecenter.common.Release(Plugin.factory(InstallActionTest.PLUGIN_KEY), version), COMPATIBLE)));
        WsTester.Result result = validRequest.execute();
        Mockito.verify(pluginDownloader).download(InstallActionTest.PLUGIN_KEY, version);
        result.assertNoContent();
    }
}

