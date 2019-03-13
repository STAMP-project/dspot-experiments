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
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.WebService;
import org.sonar.core.platform.PluginInfo;
import org.sonar.server.exceptions.ForbiddenException;
import org.sonar.server.plugins.PluginDownloader;
import org.sonar.server.plugins.PluginUninstaller;
import org.sonar.server.plugins.ServerPluginRepository;
import org.sonar.server.plugins.UpdateCenterMatrixFactory;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;


public class PendingActionTest {
    private static final String DUMMY_CONTROLLER_KEY = "dummy";

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PluginDownloader pluginDownloader = Mockito.mock(PluginDownloader.class);

    private PluginUninstaller pluginUninstaller = Mockito.mock(PluginUninstaller.class);

    private ServerPluginRepository serverPluginRepository = Mockito.mock(ServerPluginRepository.class);

    private UpdateCenterMatrixFactory updateCenterMatrixFactory = Mockito.mock(UpdateCenterMatrixFactory.class, Mockito.RETURNS_DEEP_STUBS);

    private PendingAction underTest = new PendingAction(userSession, pluginDownloader, serverPluginRepository, pluginUninstaller, updateCenterMatrixFactory);

    private Request request = Mockito.mock(Request.class);

    private WsTester.TestResponse response = new WsTester.TestResponse();

    @Test
    public void action_pending_is_defined() {
        logInAsSystemAdministrator();
        WsTester wsTester = new WsTester();
        WebService.NewController newController = wsTester.context().createController(PendingActionTest.DUMMY_CONTROLLER_KEY);
        underTest.define(newController);
        newController.done();
        WebService.Controller controller = wsTester.controller(PendingActionTest.DUMMY_CONTROLLER_KEY);
        assertThat(controller.actions()).extracting("key").containsExactly("pending");
        WebService.Action action = controller.actions().iterator().next();
        assertThat(action.isPost()).isFalse();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExample()).isNotNull();
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
    public void empty_arrays_are_returned_when_there_nothing_pending() throws Exception {
        logInAsSystemAdministrator();
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(("{" + ((("  \"installing\": []," + "  \"removing\": [],") + "  \"updating\": []") + "}")));
    }

    @Test
    public void empty_arrays_are_returned_when_update_center_is_unavailable() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(updateCenterMatrixFactory.getUpdateCenter(false)).thenReturn(Optional.absent());
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(("{" + ((("  \"installing\": []," + "  \"removing\": [],") + "  \"updating\": []") + "}")));
    }

    @Test
    public void verify_properties_displayed_in_json_per_installing_plugin() throws Exception {
        logInAsSystemAdministrator();
        newUpdateCenter("scmgit");
        Mockito.when(pluginDownloader.getDownloadedPlugins()).thenReturn(ImmutableList.of(newScmGitPluginInfo()));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(("{" + (((((((((((((((((("  \"installing\": " + "  [") + "    {") + "      \"key\": \"scmgit\",") + "      \"name\": \"Git\",") + "      \"description\": \"Git SCM Provider.\",") + "      \"version\": \"1.0\",") + "      \"license\": \"GNU LGPL 3\",") + "      \"category\":\"cat_1\",") + "      \"organizationName\": \"SonarSource\",") + "      \"organizationUrl\": \"http://www.sonarsource.com\",") + "      \"homepageUrl\": \"https://redirect.sonarsource.com/plugins/scmgit.html\",") + "      \"issueTrackerUrl\": \"http://jira.sonarsource.com/browse/SONARSCGIT\",") + "      \"implementationBuild\": \"9ce9d330c313c296fab051317cc5ad4b26319e07\"") + "    }") + "  ],") + "  \"removing\": [],") + "  \"updating\": []") + "}")));
    }

    @Test
    public void verify_properties_displayed_in_json_per_removing_plugin() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(pluginUninstaller.getUninstalledPlugins()).thenReturn(ImmutableList.of(newScmGitPluginInfo()));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(("{" + ((((((((((((((((("  \"installing\": []," + "  \"updating\": [],") + "  \"removing\": ") + "  [") + "    {") + "      \"key\": \"scmgit\",") + "      \"name\": \"Git\",") + "      \"description\": \"Git SCM Provider.\",") + "      \"version\": \"1.0\",") + "      \"license\": \"GNU LGPL 3\",") + "      \"organizationName\": \"SonarSource\",") + "      \"organizationUrl\": \"http://www.sonarsource.com\",") + "      \"homepageUrl\": \"https://redirect.sonarsource.com/plugins/scmgit.html\",") + "      \"issueTrackerUrl\": \"http://jira.sonarsource.com/browse/SONARSCGIT\",") + "      \"implementationBuild\": \"9ce9d330c313c296fab051317cc5ad4b26319e07\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void verify_properties_displayed_in_json_per_updating_plugin() throws Exception {
        logInAsSystemAdministrator();
        newUpdateCenter("scmgit");
        Mockito.when(serverPluginRepository.getPluginInfos()).thenReturn(ImmutableList.of(newScmGitPluginInfo()));
        Mockito.when(pluginDownloader.getDownloadedPlugins()).thenReturn(ImmutableList.of(newScmGitPluginInfo()));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(("{" + (((((((("  \"installing\": []," + "  \"removing\": [],") + "  \"updating\": ") + "  [") + "    {") + "      \"key\": \"scmgit\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void verify_properties_displayed_in_json_per_installing_removing_and_updating_plugins() throws Exception {
        logInAsSystemAdministrator();
        PluginInfo installed = newPluginInfo("java");
        PluginInfo removedPlugin = newPluginInfo("js");
        PluginInfo newPlugin = newPluginInfo("php");
        newUpdateCenter("scmgit");
        Mockito.when(serverPluginRepository.getPluginInfos()).thenReturn(ImmutableList.of(installed));
        Mockito.when(pluginUninstaller.getUninstalledPlugins()).thenReturn(ImmutableList.of(removedPlugin));
        Mockito.when(pluginDownloader.getDownloadedPlugins()).thenReturn(ImmutableList.of(newPlugin, installed));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).isSimilarTo(("{" + (((((((((((((((((("  \"installing\":" + "  [") + "    {") + "      \"key\": \"php\"") + "    }") + "  ],") + "  \"removing\":") + "  [") + "    {") + "      \"key\": \"js\"") + "    }") + "  ],") + "  \"updating\": ") + "  [") + "    {") + "      \"key\": \"java\"") + "    }") + "  ]") + "}")));
    }

    @Test
    public void installing_plugins_are_sorted_by_name_then_key_and_are_unique() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(pluginDownloader.getDownloadedPlugins()).thenReturn(ImmutableList.of(newPluginInfo(0).setName("Foo"), newPluginInfo(3).setName("Bar"), newPluginInfo(2).setName("Bar")));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(("{" + ((((((((((((((((("  \"installing\": " + "  [") + "    {") + "      \"key\": \"key2\",") + "      \"name\": \"Bar\",") + "    },") + "    {") + "      \"key\": \"key3\",") + "      \"name\": \"Bar\",") + "    },") + "    {") + "      \"key\": \"key0\",") + "      \"name\": \"Foo\",") + "    }") + "  ],") + "  \"removing\": [],") + "  \"updating\": []") + "}")));
    }

    @Test
    public void removing_plugins_are_sorted_and_unique() throws Exception {
        logInAsSystemAdministrator();
        Mockito.when(pluginUninstaller.getUninstalledPlugins()).thenReturn(ImmutableList.of(newPluginInfo(0).setName("Foo"), newPluginInfo(3).setName("Bar"), newPluginInfo(2).setName("Bar")));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(("{" + ((((((((((((((((("  \"installing\": []," + "  \"updating\": [],") + "  \"removing\": ") + "  [") + "    {") + "      \"key\": \"key2\",") + "      \"name\": \"Bar\",") + "    },") + "    {") + "      \"key\": \"key3\",") + "      \"name\": \"Bar\",") + "    },") + "    {") + "      \"key\": \"key0\",") + "      \"name\": \"Foo\",") + "    }") + "  ]") + "}")));
    }
}

