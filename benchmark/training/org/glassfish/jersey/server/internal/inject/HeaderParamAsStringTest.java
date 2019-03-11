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


import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@SuppressWarnings("unchecked")
public class HeaderParamAsStringTest extends AbstractTest {
    @Path("/")
    public static class ResourceString {
        @GET
        public String doGet(@HeaderParam("arg1")
        String arg1, @HeaderParam("arg2")
        String arg2, @HeaderParam("arg3")
        String arg3) {
            Assert.assertEquals("a", arg1);
            Assert.assertEquals("b", arg2);
            Assert.assertEquals("c", arg3);
            return "content";
        }

        @POST
        public String doPost(@HeaderParam("arg1")
        String arg1, @HeaderParam("arg2")
        String arg2, @HeaderParam("arg3")
        String arg3, String r) {
            Assert.assertEquals("a", arg1);
            Assert.assertEquals("b", arg2);
            Assert.assertEquals("c", arg3);
            Assert.assertEquals("content", r);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringEmpty {
        @GET
        public String doGet(@HeaderParam("arg1")
        String arg1) {
            Assert.assertEquals("", arg1);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringAbsent {
        @GET
        public String doGet(@HeaderParam("arg1")
        String arg1) {
            Assert.assertEquals(null, arg1);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringList {
        @GET
        @Produces("application/stringlist")
        public String doGetString(@HeaderParam("args")
        List<String> args) {
            Assert.assertEquals("a", args.get(0));
            Assert.assertEquals("b", args.get(1));
            Assert.assertEquals("c", args.get(2));
            return "content";
        }

        @GET
        @Produces("application/list")
        public String doGet(@HeaderParam("args")
        List args) {
            Assert.assertEquals(String.class, args.get(0).getClass());
            Assert.assertEquals("a", args.get(0));
            Assert.assertEquals(String.class, args.get(1).getClass());
            Assert.assertEquals("b", args.get(1));
            Assert.assertEquals(String.class, args.get(2).getClass());
            Assert.assertEquals("c", args.get(2));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListEmpty {
        @GET
        @Produces("application/stringlist")
        public String doGetString(@HeaderParam("args")
        List<String> args) {
            Assert.assertEquals(3, args.size());
            Assert.assertEquals("", args.get(0));
            Assert.assertEquals("", args.get(1));
            Assert.assertEquals("", args.get(2));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringEmptyDefault {
        @GET
        public String doGet(@HeaderParam("arg1")
        String arg1, @HeaderParam("arg2")
        String arg2, @HeaderParam("arg3")
        String arg3) {
            Assert.assertEquals(null, arg1);
            Assert.assertEquals(null, arg2);
            Assert.assertEquals(null, arg3);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringDefault {
        @GET
        public String doGet(@HeaderParam("arg1")
        @DefaultValue("a")
        String arg1, @HeaderParam("arg2")
        @DefaultValue("b")
        String arg2, @HeaderParam("arg3")
        @DefaultValue("c")
        String arg3) {
            Assert.assertEquals("a", arg1);
            Assert.assertEquals("b", arg2);
            Assert.assertEquals("c", arg3);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringDefaultOverride {
        @GET
        public String doGet(@HeaderParam("arg1")
        @DefaultValue("a")
        String arg1, @HeaderParam("arg2")
        @DefaultValue("b")
        String arg2, @HeaderParam("arg3")
        @DefaultValue("c")
        String arg3) {
            Assert.assertEquals("d", arg1);
            Assert.assertEquals("e", arg2);
            Assert.assertEquals("f", arg3);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListNullDefault {
        @GET
        @Produces("application/stringlist")
        public String doGetString(@HeaderParam("args")
        List<String> args) {
            Assert.assertEquals(0, args.size());
            return "content";
        }

        @GET
        @Produces("application/list")
        public String doGet(@HeaderParam("args")
        List args) {
            Assert.assertEquals(0, args.size());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListDefault {
        @GET
        @Produces("application/stringlist")
        public String doGetString(@HeaderParam("args")
        @DefaultValue("a")
        List<String> args) {
            Assert.assertEquals("a", args.get(0));
            return "content";
        }

        @GET
        @Produces("application/list")
        public String doGet(@HeaderParam("args")
        @DefaultValue("a")
        List args) {
            Assert.assertEquals(String.class, args.get(0).getClass());
            Assert.assertEquals("a", args.get(0));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListDefaultOverride {
        @GET
        @Produces("application/stringlist")
        public String doGetString(@HeaderParam("args")
        @DefaultValue("a")
        List<String> args) {
            Assert.assertEquals("b", args.get(0));
            return "content";
        }

        @GET
        @Produces("application/list")
        public String doGet(@HeaderParam("args")
        @DefaultValue("a")
        List args) {
            Assert.assertEquals(String.class, args.get(0).getClass());
            Assert.assertEquals("b", args.get(0));
            return "content";
        }
    }

    @Test
    public void testStringGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceString.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").header("arg1", "a").header("arg2", "b").header("arg3", "c").build()).getEntity());
    }

    @Test
    public void testStringEmptyGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringEmpty.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").header("arg1", "").build()).getEntity());
    }

    @Test
    public void testStringAbsentGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringAbsent.class);
        _test("/");
    }

    @Test
    public void testStringPost() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceString.class);
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").entity("content").header("arg1", "a").header("arg2", "b").header("arg3", "c").build());
        Assert.assertEquals("content", responseContext.getEntity());
    }

    @Test
    public void testStringListGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringList.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/stringlist").header("args", "a").header("args", "b").header("args", "c").build()).getEntity());
    }

    @Test
    public void testStringListEmptyGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringListEmpty.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/stringlist").header("args", "").header("args", "").header("args", "").build()).getEntity());
    }

    @Test
    public void testListGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringList.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/list").header("args", "a").header("args", "b").header("args", "c").build()).getEntity());
    }

    @Test
    public void testStringNullDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringEmptyDefault.class);
        _test("/");
    }

    @Test
    public void testStringDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringDefault.class);
        _test("/");
    }

    @Test
    public void testStringDefaultOverride() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringDefaultOverride.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").header("arg1", "d").header("arg2", "e").header("arg3", "f").build()).getEntity());
    }

    @Test
    public void testStringListEmptyDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringListNullDefault.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/stringlist").build()).getEntity());
    }

    @Test
    public void testListEmptyDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringListNullDefault.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/list").build()).getEntity());
    }

    @Test
    public void testStringListDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringListDefault.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/stringlist").build()).getEntity());
    }

    @Test
    public void testListDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringListDefault.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/list").build()).getEntity());
    }

    @Test
    public void testListDefaultOverride() throws InterruptedException, ExecutionException {
        initiateWebApplication(HeaderParamAsStringTest.ResourceStringListDefaultOverride.class);
        Assert.assertEquals("content", apply(RequestContextBuilder.from("/", "GET").accept("application/list").header("args", "b").build()).getEntity());
    }
}

