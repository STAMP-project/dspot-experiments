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
package org.glassfish.jersey.server.internal.inject;


import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@SuppressWarnings("unchecked")
public class QueryParamAsSetStringTest extends AbstractTest {
    @Path("/")
    public static class ResourceStringSet {
        @GET
        @Produces("application/stringSet")
        public String doGetString(@QueryParam("args")
        Set<String> args) {
            Assert.assertTrue(args.contains("a"));
            Assert.assertTrue(args.contains("b"));
            Assert.assertTrue(args.contains("c"));
            return "content";
        }

        @GET
        @Produces("application/Set")
        public String doGet(@QueryParam("args")
        Set args) {
            Assert.assertTrue(args.contains("a"));
            Assert.assertTrue(args.contains("b"));
            Assert.assertTrue(args.contains("c"));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSetEmpty {
        @GET
        @Produces("application/stringSet")
        public String doGetString(@QueryParam("args")
        Set<String> args) {
            Assert.assertEquals(1, args.size());
            Assert.assertTrue(args.contains(""));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSetEmptyDefault {
        @GET
        @Produces("application/stringSet")
        public String doGetString(@QueryParam("args")
        Set<String> args) {
            Assert.assertEquals(0, args.size());
            return "content";
        }

        @GET
        @Produces("application/Set")
        public String doGet(@QueryParam("args")
        Set args) {
            Assert.assertEquals(0, args.size());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSetDefault {
        @GET
        @Produces("application/stringSet")
        public String doGetString(@QueryParam("args")
        @DefaultValue("a")
        Set<String> args) {
            Assert.assertTrue(args.contains("a"));
            return "content";
        }

        @GET
        @Produces("application/Set")
        public String doGet(@QueryParam("args")
        @DefaultValue("a")
        Set args) {
            Assert.assertTrue(args.contains("a"));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSetDefaultOverride {
        @GET
        @Produces("application/stringSet")
        public String doGetString(@QueryParam("args")
        @DefaultValue("a")
        Set<String> args) {
            Assert.assertTrue(args.contains("b"));
            return "content";
        }

        @GET
        @Produces("application/Set")
        public String doGet(@QueryParam("args")
        @DefaultValue("a")
        Set args) {
            Assert.assertTrue(args.contains("b"));
            return "content";
        }
    }

    @Test
    public void testStringSetGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSet.class);
        _test("/?args=a&args=b&args=c", "application/stringSet");
    }

    @Test
    public void testStringSetEmptyGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSetEmpty.class);
        _test("/?args&args&args", "application/stringSet");
    }

    @Test
    public void testSetGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSet.class);
        _test("/?args=a&args=b&args=c", "application/Set");
    }

    @Test
    public void testStringSetEmptyDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSetEmptyDefault.class);
        _test("/", "application/stringSet");
    }

    @Test
    public void testSetEmptyDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSetEmptyDefault.class);
        _test("/", "application/Set");
    }

    @Test
    public void testStringSetDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSetDefault.class);
        _test("/", "application/stringSet");
    }

    @Test
    public void testSetDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSetDefault.class);
        _test("/", "application/Set");
    }

    @Test
    public void testSetDefaultOverride() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSetStringTest.ResourceStringSetDefaultOverride.class);
        _test("/?args=b", "application/Set");
    }
}

