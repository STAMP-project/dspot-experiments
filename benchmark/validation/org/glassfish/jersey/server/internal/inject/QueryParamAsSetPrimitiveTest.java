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
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class QueryParamAsSetPrimitiveTest extends AbstractTest {
    public QueryParamAsSetPrimitiveTest() {
        initiateWebApplication(QueryParamAsSetPrimitiveTest.ResourceQueryPrimitiveSet.class, QueryParamAsSetPrimitiveTest.ResourceQueryPrimitiveSetDefaultEmpty.class, QueryParamAsSetPrimitiveTest.ResourceQueryPrimitiveSetDefault.class, QueryParamAsSetPrimitiveTest.ResourceQueryPrimitiveSetDefaultOverride.class);
    }

    @Path("/Set")
    public static class ResourceQueryPrimitiveSet {
        @GET
        @Produces("application/boolean")
        public String doGetBoolean(@QueryParam("boolean")
        Set<Boolean> v) {
            Assert.assertTrue(v.contains(true));
            return "content";
        }

        @GET
        @Produces("application/byte")
        public String doGetByte(@QueryParam("byte")
        Set<Byte> v) {
            Assert.assertTrue(v.contains(((byte) (127))));
            return "content";
        }

        @GET
        @Produces("application/short")
        public String doGetShort(@QueryParam("short")
        Set<Short> v) {
            Assert.assertTrue(v.contains(((short) (32767))));
            return "content";
        }

        @GET
        @Produces("application/int")
        public String doGetInteger(@QueryParam("int")
        Set<Integer> v) {
            Assert.assertTrue(v.contains(2147483647));
            return "content";
        }

        @GET
        @Produces("application/long")
        public String doGetLong(@QueryParam("long")
        Set<Long> v) {
            Assert.assertTrue(v.contains(9223372036854775807L));
            return "content";
        }

        @GET
        @Produces("application/float")
        public String doGetFloat(@QueryParam("float")
        Set<Float> v) {
            Assert.assertTrue(v.contains(3.1415927F));
            return "content";
        }

        @GET
        @Produces("application/double")
        public String doGetDouble(@QueryParam("double")
        Set<Double> v) {
            Assert.assertTrue(v.contains(3.14159265358979));
            return "content";
        }
    }

    @Path("/Set/default/null")
    public static class ResourceQueryPrimitiveSetDefaultEmpty {
        @GET
        @Produces("application/boolean")
        public String doGetBoolean(@QueryParam("boolean")
        Set<Boolean> v) {
            Assert.assertEquals(0, v.size());
            return "content";
        }

        @GET
        @Produces("application/byte")
        public String doGetByte(@QueryParam("byte")
        Set<Byte> v) {
            Assert.assertEquals(0, v.size());
            return "content";
        }

        @GET
        @Produces("application/short")
        public String doGetShort(@QueryParam("short")
        Set<Short> v) {
            Assert.assertEquals(0, v.size());
            return "content";
        }

        @GET
        @Produces("application/int")
        public String doGetInteger(@QueryParam("int")
        Set<Integer> v) {
            Assert.assertEquals(0, v.size());
            return "content";
        }

        @GET
        @Produces("application/long")
        public String doGetLong(@QueryParam("long")
        Set<Long> v) {
            Assert.assertEquals(0, v.size());
            return "content";
        }

        @GET
        @Produces("application/float")
        public String doGetFloat(@QueryParam("float")
        Set<Float> v) {
            Assert.assertEquals(0, v.size());
            return "content";
        }

        @GET
        @Produces("application/double")
        public String doGetDouble(@QueryParam("double")
        Set<Double> v) {
            Assert.assertEquals(0, v.size());
            return "content";
        }
    }

    @Path("/Set/default")
    public static class ResourceQueryPrimitiveSetDefault {
        @GET
        @Produces("application/boolean")
        public String doGetBoolean(@QueryParam("boolean")
        @DefaultValue("true")
        Set<Boolean> v) {
            Assert.assertTrue(v.contains(true));
            return "content";
        }

        @GET
        @Produces("application/byte")
        public String doGetByte(@QueryParam("byte")
        @DefaultValue("127")
        Set<Byte> v) {
            Assert.assertTrue(v.contains(((byte) (127))));
            return "content";
        }

        @GET
        @Produces("application/short")
        public String doGetShort(@QueryParam("short")
        @DefaultValue("32767")
        Set<Short> v) {
            Assert.assertTrue(v.contains(((short) (32767))));
            return "content";
        }

        @GET
        @Produces("application/int")
        public String doGetInteger(@QueryParam("int")
        @DefaultValue("2147483647")
        Set<Integer> v) {
            Assert.assertTrue(v.contains(2147483647));
            return "content";
        }

        @GET
        @Produces("application/long")
        public String doGetLong(@QueryParam("long")
        @DefaultValue("9223372036854775807")
        Set<Long> v) {
            Assert.assertTrue(v.contains(9223372036854775807L));
            return "content";
        }

        @GET
        @Produces("application/float")
        public String doGetFloat(@QueryParam("float")
        @DefaultValue("3.14159265")
        Set<Float> v) {
            Assert.assertTrue(v.contains(3.1415927F));
            return "content";
        }

        @GET
        @Produces("application/double")
        public String doGetDouble(@QueryParam("double")
        @DefaultValue("3.14159265358979")
        Set<Double> v) {
            Assert.assertTrue(v.contains(3.14159265358979));
            return "content";
        }
    }

    @Path("/Set/default/override")
    public static class ResourceQueryPrimitiveSetDefaultOverride {
        @GET
        @Produces("application/boolean")
        public String doGetBoolean(@QueryParam("boolean")
        @DefaultValue("false")
        Set<Boolean> v) {
            Assert.assertTrue(v.contains(true));
            return "content";
        }

        @GET
        @Produces("application/byte")
        public String doGetByte(@QueryParam("byte")
        @DefaultValue("0")
        Set<Byte> v) {
            Assert.assertTrue(v.contains(((byte) (127))));
            return "content";
        }

        @GET
        @Produces("application/short")
        public String doGetShort(@QueryParam("short")
        @DefaultValue("0")
        Set<Short> v) {
            Assert.assertTrue(v.contains(((short) (32767))));
            return "content";
        }

        @GET
        @Produces("application/int")
        public String doGetInteger(@QueryParam("int")
        @DefaultValue("0")
        Set<Integer> v) {
            Assert.assertTrue(v.contains(2147483647));
            return "content";
        }

        @GET
        @Produces("application/long")
        public String doGetLong(@QueryParam("long")
        @DefaultValue("0")
        Set<Long> v) {
            Assert.assertTrue(v.contains(9223372036854775807L));
            return "content";
        }

        @GET
        @Produces("application/float")
        public String doGetFloat(@QueryParam("float")
        @DefaultValue("0.0")
        Set<Float> v) {
            Assert.assertTrue(v.contains(3.1415927F));
            return "content";
        }

        @GET
        @Produces("application/double")
        public String doGetDouble(@QueryParam("double")
        @DefaultValue("0.0")
        Set<Double> v) {
            Assert.assertTrue(v.contains(3.14159265358979));
            return "content";
        }
    }

    @Test
    public void testGetBoolean() throws InterruptedException, ExecutionException {
        _test("boolean", "true");
    }

    @Test
    public void testGetBooleanPrimitiveSetDefault() throws InterruptedException, ExecutionException {
        _testSetDefault("boolean", "true");
    }

    @Test
    public void testGetByte() throws InterruptedException, ExecutionException {
        _test("byte", "127");
    }

    @Test
    public void testGetBytePrimitiveSetDefault() throws InterruptedException, ExecutionException {
        _testSetDefault("byte", "127");
    }

    @Test
    public void testGetShort() throws InterruptedException, ExecutionException {
        _test("short", "32767");
    }

    @Test
    public void testGetShortPrimtiveSetDefault() throws InterruptedException, ExecutionException {
        _testSetDefault("short", "32767");
    }

    @Test
    public void testGetInt() throws InterruptedException, ExecutionException {
        _test("int", "2147483647");
    }

    @Test
    public void testGetIntPrimitiveSetDefault() throws InterruptedException, ExecutionException {
        _testSetDefault("int", "2147483647");
    }

    @Test
    public void testGetLong() throws InterruptedException, ExecutionException {
        _test("long", "9223372036854775807");
    }

    @Test
    public void testGetLongPrimitiveSetDefault() throws InterruptedException, ExecutionException {
        _testSetDefault("long", "9223372036854775807");
    }

    @Test
    public void testGetFloat() throws InterruptedException, ExecutionException {
        _test("float", "3.14159265");
    }

    @Test
    public void testGetFloatPrimitiveSetDefault() throws InterruptedException, ExecutionException {
        _testSetDefault("float", "3.14159265");
    }

    @Test
    public void testGetDouble() throws InterruptedException, ExecutionException {
        _test("double", "3.14159265358979");
    }

    @Test
    public void testGetDoublePrimitiveSetDefault() throws InterruptedException, ExecutionException {
        _testSetDefault("double", "3.14159265358979");
    }

    @Test
    public void testBadPrimitiveSetValue() throws InterruptedException, ExecutionException {
        final ContainerResponse response = super.getResponseContext("/Set?int=abcdef&int=abcdef", "application/int");
        Assert.assertEquals(404, response.getStatus());
    }
}

