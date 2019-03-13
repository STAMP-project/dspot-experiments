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
package org.glassfish.jersey.tests.e2e.server;


import MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link ParamConverter param converters} as e2e test.
 *
 * @author Miroslav Fuksa
 */
public class ParamConverterTest extends JerseyTest {
    @Test
    public void testMyBeanParam() {
        Form form = new Form();
        form.param("form", "formParam");
        final Response response = target().path("resource/myBean").path("pathParam").matrixParam("matrix", "matrixParam").queryParam("query", "queryParam").request().header("header", "headerParam").cookie("cookie", "cookieParam").post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE));
        final String str = response.readEntity(String.class);
        Assert.assertEquals("*pathParam*_*matrixParam*_*queryParam*_*headerParam*_*cookieParam*_*formParam*", str);
    }

    @Test
    public void testListOfMyBeanParam() {
        final Response response = target().path("resource/myBean/list").queryParam("q", "A").queryParam("q", "B").queryParam("q", "C").request().get();
        final String str = response.readEntity(String.class);
        Assert.assertEquals("*A**B**C*", str);
    }

    @Test
    public void testSetOfMyBeanParam() {
        final Response response = target().path("resource/myBean/set").queryParam("q", "A").queryParam("q", "B").queryParam("q", "C").request().get();
        final String str = response.readEntity(String.class);
        Assert.assertThat(str, Matchers.containsString("*A*"));
        Assert.assertThat(str, Matchers.containsString("*B*"));
        Assert.assertThat(str, Matchers.containsString("*C*"));
    }

    @Test
    public void testSortedSetOfMyBeanParam() {
        final Response response = target().path("resource/myBean/sortedset").queryParam("q", "A").queryParam("q", "B").queryParam("q", "C").request().get();
        final String str = response.readEntity(String.class);
        Assert.assertEquals("*A**B**C*", str);
    }

    @Test
    public void testStringParam() {
        Form form = new Form();
        form.param("form", "formParam");
        final Response response = target().path("resource/string").path("pathParam").matrixParam("matrix", "matrixParam").queryParam("query", "queryParam").request().header("header", "headerParam").cookie("cookie", "cookieParam").post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE));
        final String str = response.readEntity(String.class);
        Assert.assertEquals("-pathParam-_-matrixParam-_-queryParam-_-headerParam-_-cookieParam-_-formParam-", str);
    }

    @Test
    public void testMyBeanFormParamDefault() {
        Form form = new Form();
        Response response = target().path("resource/myBeanFormDefault").request().post(Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE));
        String str = response.readEntity(String.class);
        Assert.assertEquals("*form-default*", str);
    }

    @Test
    public void testMyBeanQueryParamDefault() {
        final Response response = target().path("resource/myBeanQueryDefault").request().get();
        final String str = response.readEntity(String.class);
        Assert.assertEquals("*query-default*", str);
    }

    @Test
    public void testMyBeanMatrixParamDefault() {
        final Response response = target().path("resource/myBeanMatrixDefault").request().get();
        final String str = response.readEntity(String.class);
        Assert.assertEquals("*matrix-default*", str);
    }

    @Test
    public void testMyBeanCookieParamDefault() {
        final Response response = target().path("resource/myBeanCookieDefault").request().get();
        final String str = response.readEntity(String.class);
        Assert.assertEquals("*cookie-default*", str);
    }

    @Test
    public void testMyBeanHeaderParamDefault() {
        final Response response = target().path("resource/myBeanHeaderDefault").request().get();
        final String str = response.readEntity(String.class);
        Assert.assertEquals("*header-default*", str);
    }

    @Path("resource")
    public static class Resource {
        @POST
        @Path("myBean/{path}")
        public String postMyBean(@PathParam("path")
        ParamConverterTest.MyBean pathParam, @MatrixParam("matrix")
        ParamConverterTest.MyBean matrix, @QueryParam("query")
        ParamConverterTest.MyBean query, @HeaderParam("header")
        ParamConverterTest.MyBean header, @CookieParam("cookie")
        ParamConverterTest.MyBean cookie, @FormParam("form")
        ParamConverterTest.MyBean form) {
            return ((((((((((pathParam.getValue()) + "_") + (matrix.getValue())) + "_") + (query.getValue())) + "_") + (header.getValue())) + "_") + (cookie.getValue())) + "_") + (form.getValue());
        }

        @GET
        @Path("myBean/list")
        public String postMyBean(@QueryParam("q")
        List<ParamConverterTest.MyBean> query) {
            StringBuilder sb = new StringBuilder();
            for (ParamConverterTest.MyBean bean : query) {
                sb.append(bean.getValue());
            }
            return sb.toString();
        }

        @GET
        @Path("myBean/set")
        public String postMyBean(@QueryParam("q")
        Set<ParamConverterTest.MyBean> query) {
            StringBuilder sb = new StringBuilder();
            for (ParamConverterTest.MyBean bean : query) {
                sb.append(bean.getValue());
            }
            return sb.toString();
        }

        @GET
        @Path("myBean/sortedset")
        public String postMyBean(@QueryParam("q")
        SortedSet<ParamConverterTest.MyBean> query) {
            StringBuilder sb = new StringBuilder();
            for (ParamConverterTest.MyBean bean : query) {
                sb.append(bean.getValue());
            }
            return sb.toString();
        }

        @POST
        @Path("myBeanFormDefault")
        public String postMyBeanFormDefault(@DefaultValue("form-default")
        @FormParam("form")
        ParamConverterTest.MyBean pathParam) {
            return pathParam.getValue();
        }

        @GET
        @Path("myBeanQueryDefault")
        public String getMyBeanQueryDefault(@DefaultValue("query-default")
        @QueryParam("q")
        ParamConverterTest.MyBean queryParam) {
            return queryParam.getValue();
        }

        @GET
        @Path("myBeanMatrixDefault")
        public String getMyBeanMatrixDefault(@DefaultValue("matrix-default")
        @MatrixParam("m")
        ParamConverterTest.MyBean matrixParam) {
            return matrixParam.getValue();
        }

        @GET
        @Path("myBeanCookieDefault")
        public String getMyBeanCookieDefault(@DefaultValue("cookie-default")
        @CookieParam("c")
        ParamConverterTest.MyBean cookieParam) {
            return cookieParam.getValue();
        }

        @GET
        @Path("myBeanHeaderDefault")
        public String getMyBeanHeaderDefault(@DefaultValue("header-default")
        @HeaderParam("h")
        ParamConverterTest.MyBean headerParam) {
            return headerParam.getValue();
        }

        @POST
        @Path("string/{path}")
        public String postString(@PathParam("path")
        String pathParam, @MatrixParam("matrix")
        String matrix, @QueryParam("query")
        String query, @HeaderParam("header")
        String header, @CookieParam("cookie")
        String cookie, @FormParam("form")
        String form) {
            return (((((((((pathParam + "_") + matrix) + "_") + query) + "_") + header) + "_") + cookie) + "_") + form;
        }

        @GET
        @Path("q")
        public String get(@QueryParam("query")
        String query) {
            return query;
        }

        @GET
        @Path("response")
        public Response getResponse() {
            return Response.ok().header("response-header", "res-head").entity("anything").build();
        }
    }

    public static class MyParamProvider implements ParamConverterProvider {
        @SuppressWarnings("unchecked")
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            if (rawType != (ParamConverterTest.MyBean.class)) {
                return null;
            }
            return ((ParamConverter<T>) (new ParamConverter<ParamConverterTest.MyBean>() {
                @Override
                public ParamConverterTest.MyBean fromString(String value) throws IllegalArgumentException {
                    final ParamConverterTest.MyBean myBean = new ParamConverterTest.MyBean();
                    myBean.setValue((("*" + value) + "*"));
                    return myBean;
                }

                @Override
                public String toString(ParamConverterTest.MyBean bean) throws IllegalArgumentException {
                    return ("*:" + (bean.getValue())) + ":*";
                }
            }));
        }
    }

    public static class MyStringParamProvider implements ParamConverterProvider {
        @SuppressWarnings("unchecked")
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            if (rawType != (String.class)) {
                return null;
            }
            return ((ParamConverter<T>) (new ParamConverter<String>() {
                @Override
                public String fromString(String value) throws IllegalArgumentException {
                    return ("-" + value) + "-";
                }

                @Override
                public String toString(String str) throws IllegalArgumentException {
                    return ("-:" + str) + ":-";
                }
            }));
        }
    }

    public static class MyBean implements Comparable<ParamConverterTest.MyBean> {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return ((("MyBean{" + "value='") + (value)) + '\'') + '}';
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof ParamConverterTest.MyBean)) {
                return false;
            }
            ParamConverterTest.MyBean myBean = ((ParamConverterTest.MyBean) (o));
            return !((value) != null ? !(value.equals(myBean.value)) : (myBean.value) != null);
        }

        @Override
        public int hashCode() {
            return (value) != null ? value.hashCode() : 0;
        }

        @Override
        public int compareTo(ParamConverterTest.MyBean o) {
            return value.compareTo(o.value);
        }
    }
}

