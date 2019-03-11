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


import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class QueryParamStringConstructorTest extends AbstractTest {
    @Path("/")
    public static class ResourceString {
        @GET
        public String doGet(@QueryParam("arg1")
        BigDecimal arg1, @QueryParam("arg2")
        BigInteger arg2, @QueryParam("arg3")
        URI arg3) {
            Assert.assertEquals("3.145", arg1.toString());
            Assert.assertEquals("3145", arg2.toString());
            Assert.assertEquals("http://test", arg3.toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringList {
        @GET
        public String doGetString(@QueryParam("args")
        List<BigDecimal> args) {
            Assert.assertEquals("3.145", args.get(0).toString());
            Assert.assertEquals("2.718", args.get(1).toString());
            Assert.assertEquals("1.618", args.get(2).toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListEmpty {
        @GET
        public String doGetString(@QueryParam("args")
        List<BigDecimal> args) {
            Assert.assertEquals(3, args.size());
            Assert.assertEquals(null, args.get(0));
            Assert.assertEquals(null, args.get(1));
            Assert.assertEquals(null, args.get(2));
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringNull {
        @GET
        public String doGet(@QueryParam("arg1")
        BigDecimal arg1, @QueryParam("arg2")
        BigInteger arg2) {
            Assert.assertEquals(null, arg1);
            Assert.assertEquals(null, arg2);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringNullDefault {
        @GET
        public String doGet(@QueryParam("arg1")
        BigDecimal arg1) {
            Assert.assertEquals(null, arg1);
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringDefault {
        @GET
        public String doGet(@QueryParam("arg1")
        @DefaultValue("3.145")
        BigDecimal arg1) {
            Assert.assertEquals("3.145", arg1.toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringDefaultOverride {
        @GET
        public String doGet(@QueryParam("arg1")
        @DefaultValue("3.145")
        BigDecimal arg1) {
            Assert.assertEquals("2.718", arg1.toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListEmptyDefault {
        @GET
        public String doGetString(@QueryParam("args")
        List<BigDecimal> args) {
            Assert.assertEquals(0, args.size());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListDefault {
        @GET
        public String doGetString(@QueryParam("args")
        @DefaultValue("3.145")
        List<BigDecimal> args) {
            Assert.assertEquals("3.145", args.get(0).toString());
            return "content";
        }
    }

    @Path("/")
    public static class ResourceStringListDefaultOverride {
        @GET
        public String doGetString(@QueryParam("args")
        @DefaultValue("3.145")
        List<BigDecimal> args) {
            Assert.assertEquals("2.718", args.get(0).toString());
            return "content";
        }
    }

    @Test
    public void testStringConstructorGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceString.class);
        _test("/?arg1=3.145&arg2=3145&arg3=http:%2F%2Ftest");
    }

    @Test
    public void testStringConstructorListGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringList.class);
        _test("/?args=3.145&args=2.718&args=1.618", "application/stringlist");
    }

    @Test
    public void testStringConstructorListEmptyGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringListEmpty.class);
        _test("/?args&args&args", "application/stringlist");
    }

    @Test
    public void testStringConstructorNullGet() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringNull.class);
        _test("/?arg1=&arg2=");
    }

    @Test
    public void testStringConstructorNullDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringNullDefault.class);
        _test("/");
    }

    @Test
    public void testStringConstructorDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringDefault.class);
        _test("/");
    }

    @Test
    public void testStringConstructorDefaultOverride() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringDefault.class);
        _test("/?args=2.718");
    }

    @Test
    public void testStringConstructorListEmptyDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringListEmptyDefault.class);
        _test("/");
    }

    @Test
    public void testStringConstructorListDefault() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringListDefault.class);
        _test("/");
    }

    @Test
    public void testStringConstructorListDefaultOverride() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceStringListDefaultOverride.class);
        _test("/?args=2.718");
    }

    @Test
    public void testBadStringConstructorValue() throws InterruptedException, ExecutionException {
        initiateWebApplication(QueryParamStringConstructorTest.ResourceString.class);
        final ContainerResponse responseContext = getResponseContext("/?arg1=ABCDEF&arg2=3145&arg3=http:%2F%2Ftest");
        Assert.assertEquals(404, responseContext.getStatus());
    }
}

