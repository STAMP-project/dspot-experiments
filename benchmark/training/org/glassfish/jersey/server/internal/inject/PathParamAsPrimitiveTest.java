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


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Taken from Jersey-1: jersey-tests: com.sun.jersey.impl.methodparams.PathParamAsPrimitiveTest
 *
 * @author Paul Sandoz
 */
public class PathParamAsPrimitiveTest {
    ApplicationHandler app;

    public PathParamAsPrimitiveTest() {
        app = createApplication(PathParamAsPrimitiveTest.ResourceUriBoolean.class, PathParamAsPrimitiveTest.ResourceUriByte.class, PathParamAsPrimitiveTest.ResourceUriCharacter.class, PathParamAsPrimitiveTest.ResourceUriShort.class, PathParamAsPrimitiveTest.ResourceUriInt.class, PathParamAsPrimitiveTest.ResourceUriLong.class, PathParamAsPrimitiveTest.ResourceUriFloat.class, PathParamAsPrimitiveTest.ResourceUriDouble.class, PathParamAsPrimitiveTest.ResourceUriBooleanWrapper.class, PathParamAsPrimitiveTest.ResourceUriByteWrapper.class, PathParamAsPrimitiveTest.ResourceUriCharacterWrapper.class, PathParamAsPrimitiveTest.ResourceUriShortWrapper.class, PathParamAsPrimitiveTest.ResourceUriIntWrapper.class, PathParamAsPrimitiveTest.ResourceUriLongWrapper.class, PathParamAsPrimitiveTest.ResourceUriFloatWrapper.class, PathParamAsPrimitiveTest.ResourceUriDoubleWrapper.class);
    }

    @Path("/boolean/{arg}")
    public static class ResourceUriBoolean {
        @GET
        public String doGet(@PathParam("arg")
        boolean v) {
            Assert.assertEquals(true, v);
            return "content";
        }
    }

    @Path("/byte/{arg}")
    public static class ResourceUriByte {
        @GET
        public String doGet(@PathParam("arg")
        byte v) {
            Assert.assertEquals(127, v);
            return "content";
        }
    }

    @Path("/char/{arg}")
    public static class ResourceUriCharacter {
        @GET
        public String doGet(@PathParam("arg")
        char v) {
            Assert.assertEquals('c', v);
            return "content";
        }
    }

    @Path("/short/{arg}")
    public static class ResourceUriShort {
        @GET
        public String doGet(@PathParam("arg")
        short v) {
            Assert.assertEquals(32767, v);
            return "content";
        }
    }

    @Path("/int/{arg}")
    public static class ResourceUriInt {
        @GET
        public String doGet(@PathParam("arg")
        int v) {
            Assert.assertEquals(2147483647, v);
            return "content";
        }
    }

    @Path("/long/{arg}")
    public static class ResourceUriLong {
        @GET
        public String doGet(@PathParam("arg")
        long v) {
            Assert.assertEquals(9223372036854775807L, v);
            return "content";
        }
    }

    @Path("/float/{arg}")
    public static class ResourceUriFloat {
        @GET
        public String doGet(@PathParam("arg")
        float v) {
            Assert.assertEquals(3.1415927F, v, 0.0F);
            return "content";
        }
    }

    @Path("/double/{arg}")
    public static class ResourceUriDouble {
        @GET
        public String doGet(@PathParam("arg")
        double v) {
            Assert.assertEquals(3.14159265358979, v, 0.0);
            return "content";
        }
    }

    @Path("/boolean/wrapper/{arg}")
    public static class ResourceUriBooleanWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Boolean v) {
            Assert.assertEquals(true, v);
            return "content";
        }
    }

    @Path("/byte/wrapper/{arg}")
    public static class ResourceUriByteWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Byte v) {
            Assert.assertEquals(127, v.byteValue());
            return "content";
        }
    }

    @Path("/char/wrapper/{arg}")
    public static class ResourceUriCharacterWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Character v) {
            Assert.assertEquals('c', v.charValue());
            return "content";
        }
    }

    @Path("/short/wrapper/{arg}")
    public static class ResourceUriShortWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Short v) {
            Assert.assertEquals(32767, v.shortValue());
            return "content";
        }
    }

    @Path("/int/wrapper/{arg}")
    public static class ResourceUriIntWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Integer v) {
            Assert.assertEquals(2147483647, v.intValue());
            return "content";
        }
    }

    @Path("/long/wrapper/{arg}")
    public static class ResourceUriLongWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Long v) {
            Assert.assertEquals(9223372036854775807L, v.longValue());
            return "content";
        }
    }

    @Path("/float/wrapper/{arg}")
    public static class ResourceUriFloatWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Float v) {
            Assert.assertEquals(3.1415927F, v, 0.0F);
            return "content";
        }
    }

    @Path("/double/wrapper/{arg}")
    public static class ResourceUriDoubleWrapper {
        @GET
        public String doGet(@PathParam("arg")
        Double v) {
            Assert.assertEquals(3.14159265358979, v, 0.0);
            return "content";
        }
    }

    @Test
    public void testGetBoolean() throws Exception {
        _test("boolean", "true");
    }

    @Test
    public void testGetByte() throws Exception {
        _test("byte", "127");
    }

    @Test
    public void testGetCharacter() throws Exception {
        _test("char", "c");
    }

    @Test
    public void testGetShort() throws Exception {
        _test("short", "32767");
    }

    @Test
    public void testGetInt() throws Exception {
        _test("int", "2147483647");
    }

    @Test
    public void testGetLong() throws Exception {
        _test("long", "9223372036854775807");
    }

    @Test
    public void testGetFloat() throws Exception {
        _test("float", "3.14159265");
    }

    @Test
    public void testGetDouble() throws Exception {
        _test("double", "3.14159265358979");
    }

    @Test
    public void testBadPrimitiveValue() throws Exception {
        ContainerResponse responseContext = app.apply(RequestContextBuilder.from("/int/abcdef", "GET").build()).get();
        Assert.assertEquals(404, responseContext.getStatus());
    }

    @Test
    public void testBadPrimitiveWrapperValue() throws Exception {
        ContainerResponse responseContext = app.apply(RequestContextBuilder.from("/int/wrapper/abcdef", "GET").build()).get();
        Assert.assertEquals(404, responseContext.getStatus());
    }
}

