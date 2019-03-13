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


import java.util.SortedSet;
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
public class QueryParamAsSortedSetStringTest extends AbstractTest {
    @Path("/")
    public static class ResourceStringSortedSet {
        @GET
        @Produces("application/stringSortedSet")
        public String doGetString(@QueryParam("args")
        SortedSet<String> args) {
            Assert.assertTrue(args.contains("a"));
            Assert.assertTrue(args.contains("b"));
            Assert.assertTrue(args.contains("c"));
            return "content";
        }

        @GET
        @Produces("application/SortedSet")
        public String doGet(@QueryParam("args")
        SortedSet args) {
            Assert.assertTrue(args.contains("a"));
            Assert.assertTrue(args.contains("b"));
            Assert.assertTrue(args.contains("c"));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSortedSetEmpty {
        @GET
        @Produces("application/stringSortedSet")
        public String doGetString(@QueryParam("args")
        SortedSet<String> args) {
            Assert.assertEquals(1, args.size());
            Assert.assertTrue(args.contains(""));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSortedSetEmptyDefault {
        @GET
        @Produces("application/stringSortedSet")
        public String doGetString(@QueryParam("args")
        SortedSet<String> args) {
            Assert.assertEquals(0, args.size());
            return "content";
        }

        @GET
        @Produces("application/SortedSet")
        public String doGet(@QueryParam("args")
        SortedSet args) {
            Assert.assertEquals(0, args.size());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSortedSetDefault {
        @GET
        @Produces("application/stringSortedSet")
        public String doGetString(@QueryParam("args")
        @DefaultValue("a")
        SortedSet<String> args) {
            Assert.assertTrue(args.contains("a"));
            return "content";
        }

        @GET
        @Produces("application/SortedSet")
        public String doGet(@QueryParam("args")
        @DefaultValue("a")
        SortedSet args) {
            Assert.assertTrue(args.contains("a"));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringSortedSetDefaultOverride {
        @GET
        @Produces("application/stringSortedSet")
        public String doGetString(@QueryParam("args")
        @DefaultValue("a")
        SortedSet<String> args) {
            Assert.assertTrue(args.contains("b"));
            return "content";
        }

        @GET
        @Produces("application/SortedSet")
        public String doGet(@QueryParam("args")
        @DefaultValue("a")
        SortedSet args) {
            Assert.assertTrue(args.contains("b"));
            return "content";
        }
    }

    @Test
    public void testStringSortedSetGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSet.class);
        _test("/?args=a&args=b&args=c", "application/stringSortedSet");
    }

    @Test
    public void testStringSortedSetEmptyGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSetEmpty.class);
        _test("/?args&args&args", "application/stringSortedSet");
    }

    @Test
    public void testSortedSetGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSet.class);
        _test("/?args=a&args=b&args=c", "application/SortedSet");
    }

    @Test
    public void testStringSortedSetEmptyDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSetEmptyDefault.class);
        _test("/", "application/stringSortedSet");
    }

    @Test
    public void testSortedSetEmptyDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSetEmptyDefault.class);
        _test("/", "application/SortedSet");
    }

    @Test
    public void testStringSortedSetDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSetDefault.class);
        _test("/", "application/stringSortedSet");
    }

    @Test
    public void testSortedSetDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSetDefault.class);
        _test("/", "application/SortedSet");
    }

    @Test
    public void testSortedSetDefaultOverride() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamAsSortedSetStringTest.ResourceStringSortedSetDefaultOverride.class);
        _test("/?args=b", "application/SortedSet");
    }
}

