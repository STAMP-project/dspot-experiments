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
package org.sonar.server.qualityprofile.ws;


import WebService.Action;
import org.junit.Test;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.ws.WsActionTester;


public class ExportersActionTest {
    private WsActionTester ws = new WsActionTester(new ExportersAction(createExporters()));

    @Test
    public void importers_nominal() {
        String result = ws.newRequest().execute().getInput();
        assertJson(result).isSimilarTo(ws.getDef().responseExampleAsString());
    }

    @Test
    public void define_exporters_action() {
        WebService.Action exporters = ws.getDef();
        assertThat(exporters).isNotNull();
        assertThat(exporters.isPost()).isFalse();
        assertThat(exporters.params()).isEmpty();
        assertThat(exporters.responseExampleAsString()).isNotEmpty();
    }
}

