/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.mod_cluster.undertow;


import java.util.Iterator;
import org.jboss.modcluster.container.Connector;
import org.jboss.modcluster.container.Engine;
import org.jboss.modcluster.container.Server;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.extension.undertow.UndertowService;


/**
 *
 *
 * @author Paul Ferraro
 * @author Radoslav Husar
 */
public class UndertowServerTestCase {
    private final Connector connector = Mockito.mock(Connector.class);

    private final String route = "route";

    private final Server undertowServer = new TestServer("default-server", "default-host");

    private final UndertowService service = new TestUndertowService("default-container", "default-server", "default-virtual-host", this.route, this.undertowServer);

    private final UndertowEventHandlerAdapterConfiguration configuration = Mockito.mock(UndertowEventHandlerAdapterConfiguration.class);

    private final Server server = new UndertowServer(undertowServer.getName(), this.service, this.connector);

    @Test
    public void getEngines() {
        Mockito.when(configuration.getServer()).thenReturn(Mockito.mock(Server.class));
        Mockito.when(configuration.getServer().getName()).thenReturn(undertowServer.getName());
        Iterator<Engine> engines = this.server.getEngines().iterator();
        Assert.assertTrue(engines.hasNext());
        Engine engine = engines.next();
        Assert.assertSame(this.connector, engine.getProxyConnector());
        Assert.assertFalse(engines.hasNext());
    }
}

