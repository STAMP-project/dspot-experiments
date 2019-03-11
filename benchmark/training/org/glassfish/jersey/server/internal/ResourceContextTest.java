/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.server.internal;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test basic application behavior.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ResourceContextTest {
    ApplicationHandler application;

    @Path("a")
    public static class ResourceA {
        @Path("b/{id}")
        public ResourceContextTest.ResourceB resourceB(@Context
        ResourceContext rc) {
            return rc.getResource(ResourceContextTest.ResourceB.class);
        }

        @Path("{name}")
        public ResourceContextTest.SubResource subResource(@Context
        ResourceContext rc) {
            return rc.getResource(ResourceContextTest.SubResource.class);
        }

        @GET
        @Path("is-null")
        public String isNull(@Context
        ResourceContext rc) {
            return (rc.getResource(ResourceContextTest.NotInstantiable.class)) == null ? "null" : "not null";
        }

        @Path("non-instantiable")
        public ResourceContextTest.NotInstantiable notInstantiable(@Context
        ResourceContext rc) {
            return rc.getResource(ResourceContextTest.NotInstantiable.class);
        }
    }

    @Path("b/{id}")
    public static class ResourceB {
        @PathParam("id")
        private String id;

        @GET
        public String doGet() {
            return "B: " + (id);
        }
    }

    public static class SubResource {
        @PathParam("name")
        private String name;

        @GET
        public String doGet() {
            return "SR: " + (name);
        }
    }

    public class NotInstantiable {}

    @Test
    public void testGetResource() throws Exception {
        ApplicationHandler app = createApplication(ResourceContextTest.ResourceA.class, ResourceContextTest.ResourceB.class);
        Assert.assertEquals("B: c", app.apply(RequestContextBuilder.from("/a/b/c", "GET").build()).get().getEntity());
        Assert.assertEquals("SR: foo", app.apply(RequestContextBuilder.from("/a/foo", "GET").build()).get().getEntity());
        Assert.assertEquals("null", app.apply(RequestContextBuilder.from("/a/is-null", "GET").build()).get().getEntity());
        Assert.assertEquals(404, app.apply(RequestContextBuilder.from("/a/non-instantiable", "GET").build()).get().getStatus());
    }
}

