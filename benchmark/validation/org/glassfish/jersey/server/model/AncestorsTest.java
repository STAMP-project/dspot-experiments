/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.server.model;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.server.ApplicationHandler;
import org.junit.Assert;
import org.junit.Test;

import static org.glassfish.jersey.server.RequestContextBuilder.from;


/**
 * Taken from Jersey-1: jersey-tests:com.sun.jersey.impl.subresources.AncestorsTest
 *
 * @author Paul Sandoz
 */
public class AncestorsTest {
    ApplicationHandler app;

    @Path("/node")
    public static class Node {
        int n = 0;

        public Node() {
        }

        private Node(int i) {
            this.n = i;
        }

        @Path("node")
        public AncestorsTest.Node getChild() {
            return new AncestorsTest.Node(((n) + 1));
        }

        @GET
        public String get(@Context
        UriInfo ui) {
            Assert.assertEquals(((n) + 1), ui.getMatchedResources().size());
            for (int i = 0; i <= (n); i++) {
                AncestorsTest.Node node = ((AncestorsTest.Node) (ui.getMatchedResources().get(i)));
                Assert.assertEquals(((n) - i), node.n);
            }
            Assert.assertEquals(((n) + 1), ui.getMatchedURIs().size());
            for (int i = 0; i <= (n); i++) {
                String p = ui.getMatchedURIs().get(i);
                Assert.assertEquals(getPath(((n) - i)), p);
            }
            return Integer.toString(n);
        }

        @Path("leaf")
        @GET
        public String getSub(@Context
        UriInfo ui) {
            Assert.assertEquals(((n) + 1), ui.getMatchedResources().size());
            for (int i = 0; i <= (n); i++) {
                AncestorsTest.Node node = ((AncestorsTest.Node) (ui.getMatchedResources().get(i)));
                Assert.assertEquals(((n) - i), node.n);
            }
            Assert.assertEquals((((n) + 1) + 1), ui.getMatchedURIs().size());
            String p = ui.getMatchedURIs().get(0);
            Assert.assertEquals(getPathLeaf(n), p);
            for (int i = 0; i <= (n); i++) {
                p = ui.getMatchedURIs().get((i + 1));
                Assert.assertEquals(getPath(((n) - i)), p);
            }
            return Integer.toString(n);
        }

        protected String getPath(int n) {
            String p = "node";
            for (int i = 1; i <= n; i++) {
                p += "/node";
            }
            return p;
        }

        protected String getPathLeaf(int n) {
            return (getPath(n)) + "/leaf";
        }
    }

    @Test
    public void testNode() throws Exception {
        app = createApplication(AncestorsTest.Node.class);
        Assert.assertEquals("0", app.apply(from("/node", "GET").build()).get().getEntity());
        Assert.assertEquals("1", app.apply(from("/node/node", "GET").build()).get().getEntity());
        Assert.assertEquals("2", app.apply(from("/node/node/node", "GET").build()).get().getEntity());
        Assert.assertEquals("3", app.apply(from("/node/node/node/node", "GET").build()).get().getEntity());
    }

    @Test
    public void testNodeLeaf() throws Exception {
        app = createApplication(AncestorsTest.Node.class);
        Assert.assertEquals("0", app.apply(from("/node/leaf", "GET").build()).get().getEntity());
        Assert.assertEquals("1", app.apply(from("/node/node/leaf", "GET").build()).get().getEntity());
        Assert.assertEquals("2", app.apply(from("/node/node/node/leaf", "GET").build()).get().getEntity());
        Assert.assertEquals("3", app.apply(from("/node/node/node/node/leaf", "GET").build()).get().getEntity());
    }

    @Path("/node/")
    public static class NodeSlash {
        int n = 0;

        public NodeSlash() {
        }

        private NodeSlash(int i) {
            this.n = i;
        }

        @Path("node/")
        public AncestorsTest.NodeSlash getChild() {
            return new AncestorsTest.NodeSlash(((n) + 1));
        }

        @GET
        public String get(@Context
        UriInfo ui) {
            Assert.assertEquals(((n) + 1), ui.getMatchedResources().size());
            for (int i = 0; i <= (n); i++) {
                AncestorsTest.NodeSlash node = ((AncestorsTest.NodeSlash) (ui.getMatchedResources().get(i)));
                Assert.assertEquals(((n) - i), node.n);
            }
            Assert.assertEquals(((n) + 1), ui.getMatchedURIs().size());
            for (int i = 0; i <= (n); i++) {
                String p = ui.getMatchedURIs().get(i);
                Assert.assertEquals(getPath(((n) - i)), p);
            }
            return Integer.toString(n);
        }

        @Path("leaf/")
        @GET
        public String getSub(@Context
        UriInfo ui) {
            Assert.assertEquals(((n) + 1), ui.getMatchedResources().size());
            for (int i = 0; i <= (n); i++) {
                AncestorsTest.NodeSlash node = ((AncestorsTest.NodeSlash) (ui.getMatchedResources().get(i)));
                Assert.assertEquals(((n) - i), node.n);
            }
            Assert.assertEquals((((n) + 1) + 1), ui.getMatchedURIs().size());
            String p = ui.getMatchedURIs().get(0);
            Assert.assertEquals(getPathLeaf(n), p);
            for (int i = 0; i <= (n); i++) {
                p = ui.getMatchedURIs().get((i + 1));
                Assert.assertEquals(getPath(((n) - i)), p);
            }
            return Integer.toString(n);
        }

        protected String getPath(int n) {
            String p = "node/";
            for (int i = 1; i <= n; i++) {
                p += "node/";
            }
            return p.substring(0, ((p.length()) - 1));
        }

        protected String getPathLeaf(int n) {
            return (getPath(n)) + "/leaf";
        }
    }

    @Test
    public void testNodeSlash() throws Exception {
        app = createApplication(AncestorsTest.NodeSlash.class);
        Assert.assertEquals("0", app.apply(from("/node/", "GET").build()).get().getEntity());
        Assert.assertEquals("1", app.apply(from("/node/node/", "GET").build()).get().getEntity());
        Assert.assertEquals("2", app.apply(from("/node/node/node/", "GET").build()).get().getEntity());
        Assert.assertEquals("3", app.apply(from("/node/node/node/node/", "GET").build()).get().getEntity());
    }

    @Test
    public void testNodeLeafSlash() throws Exception {
        app = createApplication(AncestorsTest.NodeSlash.class);
        Assert.assertEquals("0", app.apply(from("/node/leaf/", "GET").build()).get().getEntity());
        Assert.assertEquals("1", app.apply(from("/node/node/leaf/", "GET").build()).get().getEntity());
        Assert.assertEquals("2", app.apply(from("/node/node/node/leaf/", "GET").build()).get().getEntity());
        Assert.assertEquals("3", app.apply(from("/node/node/node/node/leaf/", "GET").build()).get().getEntity());
    }

    @Path("foo")
    public static class FooResource {
        @Context
        UriInfo ui;

        @GET
        public String getFoo() {
            Assert.assertEquals(1, ui.getMatchedResources().size());
            Assert.assertEquals(this, ui.getMatchedResources().get(0));
            Assert.assertEquals(1, ui.getMatchedURIs().size());
            Assert.assertEquals("foo", ui.getMatchedURIs().get(0));
            return "foo";
        }

        @Path("bar")
        public AncestorsTest.BarResource getBarResource() {
            Assert.assertEquals(1, ui.getMatchedResources().size());
            Assert.assertEquals(this, ui.getMatchedResources().get(0));
            Assert.assertEquals(2, ui.getMatchedURIs().size());
            Assert.assertEquals("foo/bar", ui.getMatchedURIs().get(0));
            Assert.assertEquals("foo", ui.getMatchedURIs().get(1));
            return new AncestorsTest.BarResource(ui, this);
        }
    }

    public static class BarResource {
        UriInfo ui;

        AncestorsTest.FooResource f;

        BarResource(UriInfo ui, AncestorsTest.FooResource f) {
            this.ui = ui;
            this.f = f;
        }

        @GET
        public String getBar() {
            Assert.assertEquals(2, ui.getMatchedResources().size());
            Assert.assertEquals(this, ui.getMatchedResources().get(0));
            Assert.assertEquals(f, ui.getMatchedResources().get(1));
            Assert.assertEquals(2, ui.getMatchedURIs().size());
            Assert.assertEquals("foo/bar", ui.getMatchedURIs().get(0));
            Assert.assertEquals("foo", ui.getMatchedURIs().get(1));
            return "bar";
        }
    }

    @Test
    public void testFooBar() throws Exception {
        app = createApplication(AncestorsTest.FooResource.class);
        Assert.assertEquals("foo", app.apply(from("/foo", "GET").build()).get().getEntity());
        Assert.assertEquals("bar", app.apply(from("/foo/bar", "GET").build()).get().getEntity());
    }
}

