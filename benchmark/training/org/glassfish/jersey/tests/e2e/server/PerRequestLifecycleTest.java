/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.server;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Marc Hadley
 */
public class PerRequestLifecycleTest extends JerseyTest {
    /**
     * Enum representing the actual state of the resource in the lifecycle.
     */
    private static enum ResourceState {

        NEW,
        CONSTRUCTED,
        DESTROYED;}

    @Path("/post-construct")
    public static class PostConstructResource {
        private int count;

        @Context
        Configuration configuration;

        public PostConstructResource() {
            this.count = 0;
        }

        @PostConstruct
        public void postConstruct() {
            Assert.assertNotNull(configuration);
            (count)++;
        }

        @GET
        public String doGet() {
            return Integer.toString(count);
        }
    }

    private static PerRequestLifecycleTest.ResourceState preDestroyState = PerRequestLifecycleTest.ResourceState.NEW;

    private static CountDownLatch preDestroyCdl = new CountDownLatch(1);

    @Path("pre-destroy")
    public static class PreDestroyResource {
        public PreDestroyResource() throws IOException {
            PerRequestLifecycleTest.preDestroyState = PerRequestLifecycleTest.ResourceState.CONSTRUCTED;
        }

        @GET
        public String getFileName() {
            return PerRequestLifecycleTest.preDestroyState.name();
        }

        @PreDestroy
        public void preDestroy() {
            Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED, PerRequestLifecycleTest.preDestroyState);
            PerRequestLifecycleTest.preDestroyState = PerRequestLifecycleTest.ResourceState.DESTROYED;
            PerRequestLifecycleTest.preDestroyCdl.countDown();
        }
    }

    @Path("referred")
    public static class ReferredToResource {
        @Path("sub")
        public PerRequestLifecycleTest.ReferencingOfResource get() {
            return new PerRequestLifecycleTest.ReferencingOfResource();
        }
    }

    private static PerRequestLifecycleTest.ResourceState prePostState = PerRequestLifecycleTest.ResourceState.NEW;

    private static CountDownLatch prePostCdl = new CountDownLatch(1);

    @Path("pre-post")
    public static class PreDestroyPostConstructResource {
        @PostConstruct
        public void postConstruct() throws IOException {
            PerRequestLifecycleTest.prePostState = PerRequestLifecycleTest.ResourceState.CONSTRUCTED;
        }

        @GET
        public String getState() {
            return PerRequestLifecycleTest.prePostState.name();
        }

        @PreDestroy
        public void preDestroy() {
            Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED, PerRequestLifecycleTest.prePostState);
            PerRequestLifecycleTest.prePostState = PerRequestLifecycleTest.ResourceState.DESTROYED;
            PerRequestLifecycleTest.prePostCdl.countDown();
        }
    }

    private static PerRequestLifecycleTest.ResourceState prePostPrivateState = PerRequestLifecycleTest.ResourceState.NEW;

    private static CountDownLatch prePostPrivateCdl = new CountDownLatch(1);

    @Path("pre-post-private")
    public static class PreDestroyPostConstructResourcePrivate {
        @PostConstruct
        private void postConstruct() throws IOException {
            PerRequestLifecycleTest.prePostPrivateState = PerRequestLifecycleTest.ResourceState.CONSTRUCTED;
        }

        @GET
        public String getState() {
            return PerRequestLifecycleTest.prePostPrivateState.name();
        }

        @PreDestroy
        private void preDestroy() {
            Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED, PerRequestLifecycleTest.prePostPrivateState);
            PerRequestLifecycleTest.prePostPrivateState = PerRequestLifecycleTest.ResourceState.DESTROYED;
            PerRequestLifecycleTest.prePostPrivateCdl.countDown();
        }
    }

    private static PerRequestLifecycleTest.ResourceState prePostProtectedState = PerRequestLifecycleTest.ResourceState.NEW;

    private static CountDownLatch prePostProtectedCdl = new CountDownLatch(1);

    @Path("pre-post-protected")
    public static class PreDestroyPostConstructResourceProtected {
        @PostConstruct
        protected void postConstruct() throws IOException {
            PerRequestLifecycleTest.prePostProtectedState = PerRequestLifecycleTest.ResourceState.CONSTRUCTED;
        }

        @GET
        public String getState() {
            return PerRequestLifecycleTest.prePostProtectedState.name();
        }

        @PreDestroy
        protected void preDestroy() {
            Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED, PerRequestLifecycleTest.prePostProtectedState);
            PerRequestLifecycleTest.prePostProtectedState = PerRequestLifecycleTest.ResourceState.DESTROYED;
            PerRequestLifecycleTest.prePostProtectedCdl.countDown();
        }
    }

    private static PerRequestLifecycleTest.ResourceState inheritedState = PerRequestLifecycleTest.ResourceState.NEW;

    private static CountDownLatch inheritedCdl = new CountDownLatch(1);

    public abstract static class PostConstructResourceInherited {
        @PostConstruct
        private void postConstruct() throws IOException {
            PerRequestLifecycleTest.inheritedState = PerRequestLifecycleTest.ResourceState.CONSTRUCTED;
        }

        @GET
        public String getState() {
            return PerRequestLifecycleTest.inheritedState.name();
        }
    }

    @Path("inherited")
    public static class PreDestroyResourceInherited extends PerRequestLifecycleTest.PostConstructResourceInherited {
        @PreDestroy
        private void preDestroy() {
            Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED, PerRequestLifecycleTest.inheritedState);
            PerRequestLifecycleTest.inheritedState = PerRequestLifecycleTest.ResourceState.DESTROYED;
            PerRequestLifecycleTest.inheritedCdl.countDown();
        }
    }

    public static class ReferencingOfResource {
        @GET
        public String get(@Context
        final ResourceContext rc) {
            final PerRequestLifecycleTest.ReferredToResource r1 = rc.getResource(PerRequestLifecycleTest.ReferredToResource.class);
            final PerRequestLifecycleTest.ReferredToResource r2 = rc.getResource(PerRequestLifecycleTest.ReferredToResource.class);
            Assert.assertEquals(r1, r2);
            return "GET";
        }
    }

    @Test
    public void testPostConstructResource() {
        final WebTarget target = target().path("post-construct");
        Assert.assertEquals("1", target.request().get(String.class));
        Assert.assertEquals("1", target.request().get(String.class));
        Assert.assertEquals("1", target.request().get(String.class));
    }

    @Test
    public void testPreDestroyResource() throws InterruptedException {
        final String s = target().path("pre-destroy").request().get(String.class);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED.name(), s);
        PerRequestLifecycleTest.preDestroyCdl.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.DESTROYED, PerRequestLifecycleTest.preDestroyState);
    }

    @Test
    public void testReferredToResource() {
        Assert.assertEquals("GET", target().path("referred/sub").request().get(String.class));
    }

    @Test
    public void testPreDestroyPostCreateResource() throws InterruptedException {
        final String s = target().path("pre-post").request().get(String.class);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED.name(), s);
        PerRequestLifecycleTest.prePostCdl.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.DESTROYED, PerRequestLifecycleTest.prePostState);
    }

    @Test
    public void testPreDestroyPostCreateResourcePrivate() throws InterruptedException {
        final String s = target().path("pre-post-private").request().get(String.class);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED.name(), s);
        PerRequestLifecycleTest.prePostPrivateCdl.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.DESTROYED, PerRequestLifecycleTest.prePostPrivateState);
    }

    @Test
    public void testPreDestroyPostCreateResourceProtected() throws InterruptedException {
        final String s = target().path("pre-post-protected").request().get(String.class);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED.name(), s);
        PerRequestLifecycleTest.prePostProtectedCdl.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.DESTROYED, PerRequestLifecycleTest.prePostProtectedState);
    }

    @Test
    public void testPreDestroyPostCreateResourceInherited() throws InterruptedException {
        final String s = target().path("inherited").request().get(String.class);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.CONSTRUCTED.name(), s);
        PerRequestLifecycleTest.inheritedCdl.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(PerRequestLifecycleTest.ResourceState.DESTROYED, PerRequestLifecycleTest.inheritedState);
    }
}

