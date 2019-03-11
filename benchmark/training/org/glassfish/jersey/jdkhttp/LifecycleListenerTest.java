/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.jdkhttp;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Reload and ContainerLifecycleListener support test.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class LifecycleListenerTest extends AbstractJdkHttpServerTester {
    @Path("/one")
    public static class One {
        @GET
        public String get() {
            return "one";
        }

        @GET
        @Path("sub")
        public String getSub() {
            return "one-sub";
        }
    }

    @Path("/two")
    public static class Two {
        @GET
        public String get() {
            return "two";
        }
    }

    public static class Reloader extends AbstractContainerLifecycleListener {
        Container container;

        public void reload(ResourceConfig newConfig) {
            container.reload(newConfig);
        }

        public void reload() {
            container.reload();
        }

        @Override
        public void onStartup(Container container) {
            this.container = container;
        }
    }

    @Test
    public void testReload() {
        final ResourceConfig rc = new ResourceConfig(LifecycleListenerTest.One.class);
        LifecycleListenerTest.Reloader reloader = new LifecycleListenerTest.Reloader();
        rc.registerInstances(reloader);
        startServer(rc);
        WebTarget r = ClientBuilder.newClient().target(getUri().path("/").build());
        Assert.assertEquals("one", r.path("one").request().get(String.class));
        Assert.assertEquals("one-sub", r.path("one/sub").request().get(String.class));
        Assert.assertEquals(404, r.path("two").request().get(javax.ws.rs.core.Response.class).getStatus());
        // add Two resource
        reloader.reload(new ResourceConfig(LifecycleListenerTest.One.class, LifecycleListenerTest.Two.class));
        Assert.assertEquals("one", r.path("one").request().get(String.class));
        Assert.assertEquals("one-sub", r.path("one/sub").request().get(String.class));
        Assert.assertEquals("two", r.path("two").request().get(String.class));
    }

    static class StartStopListener extends AbstractContainerLifecycleListener {
        volatile boolean started;

        volatile boolean stopped;

        @Override
        public void onStartup(Container container) {
            started = true;
        }

        @Override
        public void onShutdown(Container container) {
            stopped = true;
        }
    }

    @Test
    public void testStartupShutdownHooks() {
        final LifecycleListenerTest.StartStopListener listener = new LifecycleListenerTest.StartStopListener();
        startServer(new ResourceConfig(LifecycleListenerTest.One.class).register(listener));
        WebTarget r = ClientBuilder.newClient().target(getUri().path("/").build());
        Assert.assertThat(r.path("one").request().get(String.class), CoreMatchers.equalTo("one"));
        Assert.assertThat(r.path("two").request().get(javax.ws.rs.core.Response.class).getStatus(), CoreMatchers.equalTo(404));
        stopServer();
        Assert.assertTrue("ContainerLifecycleListener.onStartup has not been called.", listener.started);
        Assert.assertTrue("ContainerLifecycleListener.onShutdown has not been called.", listener.stopped);
    }
}

