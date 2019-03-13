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
import MediaType.APPLICATION_XML;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
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
// @InjectParam replace with @Inject?
// public static class ParamBean {
// @FormParam("a") String a;
// 
// @FormParam("b") String b;
// 
// @Context
// UriInfo ui;
// 
// @QueryParam("a") String qa;
// }
// 
// @Path("/")
// public static class FormResourceBean {
// @POST
// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
// public String post(
// @InjectParam ParamBean pb,
// @FormParam("a") String a,
// @FormParam("b") String b,
// Form form) {
// assertEquals(pb.a, form.getFirst("a"));
// assertEquals(pb.b, form.getFirst("b"));
// return pb.a + pb.b;
// }
// }
// 
// public void testFormParamBean() {
// initiateWebApplication(FormResourceBean.class);
// 
// WebResource r = resource("/");
// 
// Form form = new Form();
// form.add("a", "foo");
// form.add("b", "bar");
// 
// String s = r.post(String.class, form);
// assertEquals("foobar", s);
// }
// 
// @Path("/")
// public static class FormResourceBeanNoFormParam {
// @POST
// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
// public String post(@InjectParam ParamBean pb) {
// return pb.a + pb.b;
// }
// }
// 
// public void testFormParamBeanNoFormParam() {
// initiateWebApplication(FormResourceBeanNoFormParam.class);
// 
// WebResource r = resource("/");
// 
// Form form = new Form();
// form.add("a", "foo");
// form.add("b", "bar");
// 
// String s = r.post(String.class, form);
// assertEquals("foobar", s);
// }
// 
// @Path("/")
// public static class FormResourceBeanConstructor {
// private final ParamBean pb;
// 
// public FormResourceBeanConstructor(@InjectParam ParamBean pb) {
// this.pb = pb;
// }
// 
// @GET
// public String get() {
// return "GET";
// }
// 
// @POST
// @Consumes(MediaType.TEXT_PLAIN)
// public String postText(String s) {
// return s;
// }
// 
// @POST
// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
// public String post(String s) {
// assertTrue(s.contains("a=foo"));
// assertTrue(s.contains("b=bar"));
// 
// return pb.a + pb.b;
// }
// }
// 
// public void testFormParamBeanConstructor() {
// initiateWebApplication(FormResourceBeanConstructor.class);
// 
// WebResource r = resource("/");
// 
// Form form = new Form();
// form.add("a", "foo");
// form.add("b", "bar");
// 
// String s = r.post(String.class, form);
// assertEquals("foobar", s);
// }
// 
// public void testFormParamBeanConstructorIllegalState() {
// initiateWebApplication(FormResourceBeanConstructor.class);
// 
// WebResource r = resource("/");
// 
// boolean caught = false;
// try {
// ClientResponse cr = r.get(ClientResponse.class);
// } catch (ContainerException ex) {
// assertEquals(IllegalStateException.class, ex.getCause().getCause().getClass());
// caught = true;
// }
// assertTrue(caught);
// 
// 
// caught = false;
// try {
// ClientResponse cr = r.post(ClientResponse.class, "text");
// } catch (ContainerException ex) {
// assertEquals(IllegalStateException.class, ex.getCause().getCause().getClass());
// caught = true;
// }
// assertTrue(caught);
// }
// 
// 
// @Path("/")
// public static class FormResourceBeanConstructorFormParam {
// private final ParamBean pb;
// 
// public FormResourceBeanConstructorFormParam(@InjectParam ParamBean pb) {
// this.pb = pb;
// }
// 
// @POST
// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
// public String post(
// @FormParam("a") String a,
// @FormParam("b") String b,
// Form form) {
// assertEquals(a, form.getFirst("a"));
// assertEquals(b, form.getFirst("b"));
// return a + b;
// }
// }
// 
// public void testFormParamBeanConstructorFormParam() {
// initiateWebApplication(FormResourceBeanConstructorFormParam.class);
// 
// WebResource r = resource("/");
// 
// Form form = new Form();
// form.add("a", "foo");
// form.add("b", "bar");
// 
// String s = r.post(String.class, form);
// assertEquals("foobar", s);
// }
public class FormParamTest extends AbstractTest {
    @Path("/")
    public static class SimpleFormResource {
        @POST
        public String post(@FormParam("a")
        final String a) {
            Assert.assertEquals("foo", a);
            return a;
        }
    }

    @Test
    public void testSimpleFormResource() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.SimpleFormResource.class);
        final Form form = new Form();
        form.param("a", "foo");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("foo", responseContext.getEntity());
    }

    @Test
    public void testSimpleFormResourceWithCharset() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.SimpleFormResource.class);
        final Form form = new Form();
        form.param("a", "foo");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED_TYPE.withCharset("UTF-8")).entity(form).build());
        Assert.assertEquals("foo", responseContext.getEntity());
    }

    @Path("/")
    public static class FormResourceNoConsumes {
        @POST
        public String post(@FormParam("a")
        final String a, final MultivaluedMap<String, String> form) {
            Assert.assertEquals(a, form.getFirst("a"));
            return a;
        }
    }

    @Test
    public void testFormResourceNoConsumes() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormResourceNoConsumes.class);
        final Form form = new Form();
        form.param("a", "foo");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("foo", responseContext.getEntity());
    }

    @Path("/")
    public static class FormResourceFormEntityParam {
        @POST
        public String post(@FormParam("a")
        final String a, final Form form) {
            Assert.assertEquals(a, form.asMap().getFirst("a"));
            return a;
        }
    }

    @Test
    public void testFormResourceFormEntityParam() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormResourceFormEntityParam.class);
        final Form form = new Form();
        form.param("a", "foo");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("foo", responseContext.getEntity());
    }

    @XmlRootElement(name = "jaxbBean")
    public static class JAXBBean {
        public String value;

        public JAXBBean() {
        }

        @Override
        public boolean equals(final Object o) {
            return (o instanceof FormParamTest.JAXBBean) && (((FormParamTest.JAXBBean) (o)).value.equals(value));
        }

        @Override
        public String toString() {
            return "JAXBClass: " + (value);
        }
    }

    @Path("/")
    public static class FormResourceX {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String post(@FormParam("a")
        final String a, @FormParam("b")
        final String b, final MultivaluedMap<String, String> form, @Context
        final UriInfo ui, @QueryParam("a")
        final String qa) {
            Assert.assertEquals(a, form.getFirst("a"));
            Assert.assertEquals(b, form.getFirst("b"));
            return a + b;
        }
    }

    @Path("/")
    public static class FormResourceY {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String post(@FormParam("a")
        final String a, @FormParam("b")
        final String b, final Form form, @Context
        final UriInfo ui, @QueryParam("a")
        final String qa) {
            Assert.assertEquals(a, form.asMap().getFirst("a"));
            Assert.assertEquals(b, form.asMap().getFirst("b"));
            return a + b;
        }
    }

    @Test
    public void testFormParamX() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormResourceX.class);
        final Form form = new Form();
        form.param("a", "foo");
        form.param("b", "bar");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("foobar", responseContext.getEntity());
    }

    @Test
    public void testFormParamY() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormResourceY.class);
        final Form form = new Form();
        form.param("a", "foo");
        form.param("b", "bar");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("foobar", responseContext.getEntity());
    }

    @Path("/")
    public static class FormParamTypes {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String createSubscription(@FormParam("int")
        final int i, @FormParam("float")
        final float f, @FormParam("decimal")
        final BigDecimal d) {
            return (((("" + i) + " ") + f) + " ") + d;
        }
    }

    @Test
    public void testFormParamTypes() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormParamTypes.class);
        final Form form = new Form();
        form.param("int", "1");
        form.param("float", "3.14");
        form.param("decimal", "3.14");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("1 3.14 3.14", responseContext.getEntity());
    }

    /**
     * JERSEY-2637 reproducer outside of container (pure server).
     */
    @Test
    public void testFormParamAsQueryParams() throws Exception {
        initiateWebApplication(FormParamTest.FormParamTypes.class);
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/?int=2&float=2.71&decimal=2.71", "POST").type(APPLICATION_FORM_URLENCODED).build());
        Assert.assertEquals("0 0.0 null", responseContext.getEntity());
    }

    @Path("/")
    public static class FormDefaultValueParamTypes {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String createSubscription(@DefaultValue("1")
        @FormParam("int")
        final int i, @DefaultValue("3.14")
        @FormParam("float")
        final float f, @DefaultValue("3.14")
        @FormParam("decimal")
        final BigDecimal d) {
            return (((("" + i) + " ") + f) + " ") + d;
        }
    }

    @Test
    public void testFormDefaultValueParamTypes() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormDefaultValueParamTypes.class);
        final Form form = new Form();
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("1 3.14 3.14", responseContext.getEntity());
    }

    public static class TrimmedString {
        private final String string;

        public TrimmedString(final String string) {
            this.string = string.trim();
        }

        @Override
        public String toString() {
            return string;
        }
    }

    @Path("/")
    public static class FormConstructorValueParamTypes {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String createSubscription(@DefaultValue("")
        @FormParam("trim")
        final FormParamTest.TrimmedString s) {
            return s.toString();
        }
    }

    @Test
    public void testFormConstructorValueParamTypes() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormConstructorValueParamTypes.class);
        final Form form = new Form();
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("", responseContext.getEntity());
    }

    @Path("/")
    public static class FormResourceJAXB {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        @Produces(MediaType.APPLICATION_XML)
        public FormParamTest.JAXBBean post(@FormParam("a")
        final FormParamTest.JAXBBean a, @FormParam("b")
        final List<FormParamTest.JAXBBean> b) {
            Assert.assertEquals("a", a.value);
            Assert.assertEquals(2, b.size());
            Assert.assertEquals("b1", b.get(0).value);
            Assert.assertEquals("b2", b.get(1).value);
            return a;
        }
    }

    @Test
    public void testFormParamJAXB() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormResourceJAXB.class);
        final Form form = new Form();
        form.param("a", "<jaxbBean><value>a</value></jaxbBean>");
        form.param("b", "<jaxbBean><value>b1</value></jaxbBean>");
        form.param("b", "<jaxbBean><value>b2</value></jaxbBean>");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").accept(APPLICATION_XML).type(APPLICATION_FORM_URLENCODED).entity(form).build());
        final FormParamTest.JAXBBean b = ((FormParamTest.JAXBBean) (responseContext.getEntity()));
        Assert.assertEquals("a", b.value);
    }

    @Test
    public void testFormParamJAXBError() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormResourceJAXB.class);
        final Form form = new Form();
        form.param("a", "<x><value>a</value></jaxbBean>");
        form.param("b", "<x><value>b1</value></jaxbBean>");
        form.param("b", "<x><value>b2</value></jaxbBean>");
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals(400, responseContext.getStatus());
    }

    @Path("/")
    public static class FormResourceDate {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public String post(@FormParam("a")
        final Date a, @FormParam("b")
        final Date b, @FormParam("c")
        final Date c) {
            Assert.assertNotNull(a);
            Assert.assertNotNull(b);
            Assert.assertNotNull(c);
            return "POST";
        }
    }

    @Test
    public void testFormParamDate() throws InterruptedException, ExecutionException {
        initiateWebApplication(FormParamTest.FormResourceDate.class);
        final String date_RFC1123 = "Sun, 06 Nov 1994 08:49:37 GMT";
        final String date_RFC1036 = "Sunday, 06-Nov-94 08:49:37 GMT";
        final String date_ANSI_C = "Sun Nov  6 08:49:37 1994";
        final Form form = new Form();
        form.param("a", date_RFC1123);
        form.param("b", date_RFC1036);
        form.param("c", date_ANSI_C);
        final ContainerResponse responseContext = apply(RequestContextBuilder.from("/", "POST").type(APPLICATION_FORM_URLENCODED).entity(form).build());
        Assert.assertEquals("POST", responseContext.getEntity());
    }
}

