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
package org.sonar.server.favorite.ws;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.server.ws.WebService.Controller;
import org.sonar.db.DbClient;
import org.sonar.server.component.ComponentFinder;
import org.sonar.server.favorite.FavoriteUpdater;
import org.sonar.server.user.UserSession;
import org.sonar.server.ws.WsTester;


public class FavoritesWsTest {
    private final FavoritesWsAction[] actions = new FavoritesWsAction[]{ new AddAction(Mockito.mock(UserSession.class), Mockito.mock(DbClient.class), Mockito.mock(FavoriteUpdater.class), Mockito.mock(ComponentFinder.class)) };

    private WsTester ws = new WsTester(new FavoritesWs(actions));

    private Controller underTest = ws.controller("api/favorites");

    @Test
    public void definition() {
        assertThat(underTest.path()).isEqualTo("api/favorites");
    }
}

