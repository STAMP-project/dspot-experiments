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
package org.sonar.server.platform;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.platform.ServerStartHandler;
import org.sonar.api.platform.ServerStopHandler;


public class ServerLifecycleNotifierTest {
    private Server server;

    private ServerStartHandler start1;

    private ServerStartHandler start2;

    private ServerStopHandler stop1;

    private ServerStopHandler stop2;

    /**
     * see the explanation in the method ServerLifecycleNotifier.start()
     */
    @Test
    public void doNotNotifyWithTheStartMethod() {
        ServerLifecycleNotifier notifier = new ServerLifecycleNotifier(server, new ServerStartHandler[]{ start1, start2 }, new ServerStopHandler[]{ stop2 });
        notifier.start();
        Mockito.verify(start1, Mockito.never()).onServerStart(server);
        Mockito.verify(start2, Mockito.never()).onServerStart(server);
        Mockito.verify(stop1, Mockito.never()).onServerStop(server);
    }

    @Test
    public void notifyOnStart() {
        ServerLifecycleNotifier notifier = new ServerLifecycleNotifier(server, new ServerStartHandler[]{ start1, start2 }, new ServerStopHandler[]{ stop2 });
        notifier.notifyStart();
        Mockito.verify(start1).onServerStart(server);
        Mockito.verify(start2).onServerStart(server);
        Mockito.verify(stop1, Mockito.never()).onServerStop(server);
    }

    @Test
    public void notifyOnStop() {
        ServerLifecycleNotifier notifier = new ServerLifecycleNotifier(server, new ServerStartHandler[]{ start1, start2 }, new ServerStopHandler[]{ stop1, stop2 });
        notifier.stop();
        Mockito.verify(start1, Mockito.never()).onServerStart(server);
        Mockito.verify(start2, Mockito.never()).onServerStart(server);
        Mockito.verify(stop1).onServerStop(server);
        Mockito.verify(stop2).onServerStop(server);
    }
}

