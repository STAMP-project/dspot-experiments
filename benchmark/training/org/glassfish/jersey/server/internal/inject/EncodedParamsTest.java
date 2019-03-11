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


import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Form;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@SuppressWarnings("unchecked")
public class EncodedParamsTest extends AbstractTest {
    @Path("/{u}")
    public static class EncodedOnPostClass {
        public EncodedOnPostClass(@PathParam("u")
        String u, @QueryParam("q")
        String q, @MatrixParam("m")
        String m, @FormParam("f")
        String f) {
            Assert.assertEquals(" u", u);
            Assert.assertEquals(" q", q);
            Assert.assertEquals(" m", m);
            Assert.assertEquals(":f", f);
        }

        @POST
        @Encoded
        public String doPost(@PathParam("u")
        String u, @QueryParam("q")
        String q, @MatrixParam("m")
        String m, @FormParam("f")
        String f) {
            Assert.assertEquals("%20u", u);
            Assert.assertEquals("%20q", q);
            Assert.assertEquals("%20m", m);
            Assert.assertEquals("%3Af", f);
            return "content";
        }

        @POST
        @Path("combined")
        public String doPostCombined(@Encoded
        @FormParam("f")
        String f, @FormParam("f2")
        String f2) {
            Assert.assertEquals("%3Af", f);
            Assert.assertEquals(":f2", f2);
            return "content";
        }
    }

    @Test
    public void testEncodedOnPostClass() throws InterruptedException, ExecutionException {
        initiateWebApplication(EncodedParamsTest.EncodedOnPostClass.class);
        Form form = new Form();
        form.param("f", ":f");
        RequestContextBuilder requestBuilder = RequestContextBuilder.from("/%20u;m=%20m?q=%20q", "POST").type(APPLICATION_FORM_URLENCODED_TYPE).entity(form);
        Assert.assertEquals("content", apply(requestBuilder.build()).getEntity());
    }

    @Test
    public void testCombinedEncodedOnPostClass() throws InterruptedException, ExecutionException {
        initiateWebApplication(EncodedParamsTest.EncodedOnPostClass.class);
        Form form = new Form();
        form.param("f", ":f");
        form.param("f2", ":f2");
        RequestContextBuilder requestBuilder = RequestContextBuilder.from("/%20u/combined;m=%20m?q=%20q", "POST").type(APPLICATION_FORM_URLENCODED_TYPE).entity(form);
        Assert.assertEquals("content", apply(requestBuilder.build()).getEntity());
    }

    @Encoded
    @Path("/{u}")
    public static class EncodedOnClass {
        public EncodedOnClass(@PathParam("u")
        String u, @QueryParam("q")
        String q, @MatrixParam("m")
        String m) {
            Assert.assertEquals("%20u", u);
            Assert.assertEquals("%20q", q);
            Assert.assertEquals("%20m", m);
        }

        @GET
        public String doGet(@PathParam("u")
        String u, @QueryParam("q")
        String q, @MatrixParam("m")
        String m) {
            Assert.assertEquals("%20u", u);
            Assert.assertEquals("%20q", q);
            Assert.assertEquals("%20m", m);
            return "content";
        }
    }

    @Test
    public void testEncodedOnClass() throws InterruptedException, ExecutionException {
        initiateWebApplication(EncodedParamsTest.EncodedOnClass.class);
        _test("/%20u;m=%20m?q=%20q");
    }

    @Path("/{u}")
    public static class EncodedOnAccessibleObject {
        @Encoded
        public EncodedOnAccessibleObject(@PathParam("u")
        String u, @QueryParam("q")
        String q, @MatrixParam("m")
        String m) {
            Assert.assertEquals("%20u", u);
            Assert.assertEquals("%20q", q);
            Assert.assertEquals("%20m", m);
        }

        @Encoded
        @GET
        public String doGet(@PathParam("u")
        String u, @QueryParam("q")
        String q, @MatrixParam("m")
        String m) {
            Assert.assertEquals("%20u", u);
            Assert.assertEquals("%20q", q);
            Assert.assertEquals("%20m", m);
            return "content";
        }
    }

    @Test
    public void testEncodedOnAccessibleObject() throws InterruptedException, ExecutionException {
        initiateWebApplication(EncodedParamsTest.EncodedOnAccessibleObject.class);
        _test("/%20u;m=%20m?q=%20q");
    }

    @Path("/{u}")
    public static class EncodedOnParameters {
        public EncodedOnParameters(@Encoded
        @PathParam("u")
        String u, @Encoded
        @QueryParam("q")
        String q, @Encoded
        @MatrixParam("m")
        String m) {
            Assert.assertEquals("%20u", u);
            Assert.assertEquals("%20q", q);
            Assert.assertEquals("%20m", m);
        }

        @GET
        public String doGet(@Encoded
        @PathParam("u")
        String u, @Encoded
        @QueryParam("q")
        String q, @Encoded
        @MatrixParam("m")
        String m) {
            Assert.assertEquals("%20u", u);
            Assert.assertEquals("%20q", q);
            Assert.assertEquals("%20m", m);
            return "content";
        }
    }

    @Test
    public void testEncodedOnParameters() throws InterruptedException, ExecutionException {
        initiateWebApplication(EncodedParamsTest.EncodedOnParameters.class);
        _test("/%20u;m=%20m?q=%20q");
    }

    @Path("/{u}")
    public static class MixedEncodedOnParameters {
        public MixedEncodedOnParameters(@PathParam("u")
        String du, @QueryParam("q")
        String dq, @MatrixParam("m")
        String dm, @Encoded
        @PathParam("u")
        String eu, @Encoded
        @QueryParam("q")
        String eq, @Encoded
        @MatrixParam("m")
        String em) {
            Assert.assertEquals(" u", du);
            Assert.assertEquals(" q", dq);
            Assert.assertEquals(" m", dm);
            Assert.assertEquals("%20u", eu);
            Assert.assertEquals("%20q", eq);
            Assert.assertEquals("%20m", em);
        }

        @GET
        public String doGet(@PathParam("u")
        String du, @QueryParam("q")
        String dq, @MatrixParam("m")
        String dm, @Encoded
        @PathParam("u")
        String eu, @Encoded
        @QueryParam("q")
        String eq, @Encoded
        @MatrixParam("m")
        String em) {
            Assert.assertEquals(" u", du);
            Assert.assertEquals(" q", dq);
            Assert.assertEquals(" m", dm);
            Assert.assertEquals("%20u", eu);
            Assert.assertEquals("%20q", eq);
            Assert.assertEquals("%20m", em);
            return "content";
        }
    }

    @Test
    public void testMixedEncodedOnParameters() throws InterruptedException, ExecutionException {
        initiateWebApplication(EncodedParamsTest.MixedEncodedOnParameters.class);
        _test("/%20u;m=%20m?q=%20q");
    }

    @Path("/")
    public static class EncodedOnFormParameters {
        public EncodedOnFormParameters(@Encoded
        @FormParam("u")
        String ue, @FormParam("u")
        String u) {
            Assert.assertThat(ue, CoreMatchers.is("%C5%A1"));
            Assert.assertThat(ue, CoreMatchers.is(CoreMatchers.not("\u0161")));
            Assert.assertThat(u, CoreMatchers.is(CoreMatchers.not("%C5%A1")));
            Assert.assertThat(u, CoreMatchers.is("\u0161"));
        }

        @POST
        public String doPost(@Encoded
        @FormParam("u")
        String ue, @FormParam("u")
        String u) {
            Assert.assertThat(ue, CoreMatchers.is("%C5%A1"));
            Assert.assertThat(ue, CoreMatchers.is(CoreMatchers.not("\u0161")));
            Assert.assertThat(u, CoreMatchers.is(CoreMatchers.not("%C5%A1")));
            Assert.assertThat(u, CoreMatchers.is("\u0161"));
            return "content";
        }
    }

    @Test
    public void testEncodedOnFormParameters() throws InterruptedException, ExecutionException {
        initiateWebApplication(EncodedParamsTest.EncodedOnFormParameters.class);
        final RequestContextBuilder requestBuilder = RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(new Form("u", "\u0161"));
        apply(requestBuilder.build());
    }
}

