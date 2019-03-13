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
package org.sonar.server.platform.ws;


import WebService.Action;
import WebService.Controller;
import WebService.NewController;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.plugins.UpdateCenterMatrixFactory;
import org.sonar.server.ws.WsActionTester;
import org.sonar.server.ws.WsTester;
import org.sonar.updatecenter.common.Release;
import org.sonar.updatecenter.common.SonarUpdate;
import org.sonar.updatecenter.common.UpdateCenter;


public class UpgradesActionTest {
    private static final String DUMMY_CONTROLLER_KEY = "dummy";

    private static final String JSON_EMPTY_UPGRADE_LIST = "{" + (("  \"upgrades\":" + "[]") + "}");

    private static Release release;

    private UpdateCenterMatrixFactory updateCenterFactory = Mockito.mock(UpdateCenterMatrixFactory.class);

    private UpdateCenter updateCenter = Mockito.mock(UpdateCenter.class);

    private UpgradesAction underTest = new UpgradesAction(updateCenterFactory);

    private Request request = Mockito.mock(Request.class);

    private WsTester.TestResponse response = new WsTester.TestResponse();

    @Test
    public void action_updates_is_defined() {
        WsTester wsTester = new WsTester();
        WebService.NewController newController = wsTester.context().createController(UpgradesActionTest.DUMMY_CONTROLLER_KEY);
        underTest.define(newController);
        newController.done();
        WebService.Controller controller = wsTester.controller(UpgradesActionTest.DUMMY_CONTROLLER_KEY);
        assertThat(controller.actions()).extracting("key").containsExactly("upgrades");
        WebService.Action action = controller.actions().iterator().next();
        assertThat(action.isPost()).isFalse();
        assertThat(action.description()).isNotEmpty();
        assertThat(action.responseExample()).isNotNull();
        assertThat(action.params()).isEmpty();
    }

    @Test
    public void empty_array_is_returned_when_there_is_no_upgrade_available() throws Exception {
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(UpgradesActionTest.JSON_EMPTY_UPGRADE_LIST);
    }

    @Test
    public void empty_array_is_returned_when_update_center_is_unavailable() throws Exception {
        Mockito.when(updateCenterFactory.getUpdateCenter(ArgumentMatchers.anyBoolean())).thenReturn(Optional.absent());
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(UpgradesActionTest.JSON_EMPTY_UPGRADE_LIST);
    }

    @Test
    public void verify_JSON_response_against_example() throws Exception {
        SonarUpdate sonarUpdate = UpgradesActionTest.createSonar_51_update();
        Mockito.when(updateCenter.findSonarUpdates()).thenReturn(ImmutableList.of(sonarUpdate));
        underTest.handle(request, response);
        assertJson(response.outputAsString()).withStrictArrayOrder().isSimilarTo(new WsActionTester(underTest).getDef().responseExampleAsString());
    }
}

