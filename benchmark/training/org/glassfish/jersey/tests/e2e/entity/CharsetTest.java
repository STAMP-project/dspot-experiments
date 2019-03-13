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
package org.glassfish.jersey.tests.e2e.entity;


import MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import MediaType.APPLICATION_JSON_TYPE;
import MediaType.APPLICATION_XML_TYPE;
import java.io.Reader;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.jettison.JettisonJaxbContext;
import org.junit.Test;


/**
 *
 *
 * @author Paul Sandoz
 * @author Martin Matula
 */
public class CharsetTest extends AbstractTypeTester {
    private static String[] CHARSETS = new String[]{ "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16" };

    private static final String CONTENT = "\u00a9 CONTENT \u00ff \u2200 \u22ff";

    @Path("/StringCharsetResource")
    public static class StringCharsetResource {
        @Path("US-ASCII")
        @POST
        @Produces("text/plain;charset=US-ASCII")
        public String postUs_Ascii(String t) {
            return t;
        }

        @Path("ISO-8859-1")
        @POST
        @Produces("text/plain;charset=ISO-8859-1")
        public String postIso_8859_1(String t) {
            return t;
        }

        @Path("UTF-8")
        @POST
        @Produces("text/plain;charset=UTF-8")
        public String postUtf_8(String t) {
            return t;
        }

        @Path("UTF-16BE")
        @POST
        @Produces("text/plain;charset=UTF-16BE")
        public String postUtf_16be(String t) {
            return t;
        }

        @Path("UTF-16LE")
        @POST
        @Produces("text/plain;charset=UTF-16LE")
        public String postUtf_16le(String t) {
            return t;
        }

        @Path("UTF-16")
        @POST
        @Produces("text/plain;charset=UTF-16")
        public String postUtf_16(String t) {
            return t;
        }
    }

    @Test
    public void testStringCharsetResource() {
        String in = "\u00a9 CONTENT \u00ff \u2200 \u22ff";
        WebTarget t = target().path("StringCharsetResource");
        for (String charset : CharsetTest.CHARSETS) {
            Response r = t.path(charset).request().post(Entity.entity(in, ("text/plain;charset=" + charset)));
            byte[] inBytes = AbstractTypeTester.getRequestEntity();
            byte[] outBytes = AbstractTypeTester.getEntityAsByteArray(r);
            AbstractTypeTester._verify(inBytes, outBytes);
        }
    }

    public abstract static class CharsetResource<T> {
        @Context
        HttpHeaders h;

        @POST
        @Produces("application/*")
        public Response post(T t) {
            return Response.ok(t, h.getMediaType()).build();
        }
    }

    @Path("/StringResource")
    public static class StringResource extends CharsetTest.CharsetResource<String> {}

    @Test
    public void testStringRepresentation() {
        _test(CharsetTest.CONTENT, CharsetTest.StringResource.class);
    }

    @Path("/FormMultivaluedMapResource")
    public static class FormMultivaluedMapResource extends CharsetTest.CharsetResource<MultivaluedMap<String, String>> {}

    @Test
    public void testFormMultivaluedMapRepresentation() {
        MultivaluedMap<String, String> map = new javax.ws.rs.core.MultivaluedHashMap();
        map.add("name", "\u00a9 CONTENT \u00ff \u2200 \u22ff");
        map.add("name", "? ? ?");
        _test(map, CharsetTest.FormMultivaluedMapResource.class, APPLICATION_FORM_URLENCODED_TYPE);
    }

    @Path("/FormResource")
    public static class FormResource extends CharsetTest.CharsetResource<Form> {}

    @Test
    public void testRepresentation() {
        Form form = new Form();
        form.param("name", "\u00a9 CONTENT \u00ff \u2200 \u22ff");
        form.param("name", "? ? ?");
        _test(form, CharsetTest.FormResource.class, APPLICATION_FORM_URLENCODED_TYPE);
    }

    @Path("/JSONObjectResource")
    public static class JSONObjectResource extends CharsetTest.CharsetResource<JSONObject> {}

    @Test
    public void testJSONObjectRepresentation() throws Exception {
        JSONObject object = new JSONObject();
        object.put("userid", 1234).put("username", CharsetTest.CONTENT).put("email", "a@b").put("password", "****");
        _test(object, CharsetTest.JSONObjectResource.class, APPLICATION_JSON_TYPE);
    }

    @Path("/JSONOArrayResource")
    public static class JSONOArrayResource extends CharsetTest.CharsetResource<JSONArray> {}

    @Test
    public void testJSONArrayRepresentation() throws Exception {
        JSONArray array = new JSONArray();
        array.put(CharsetTest.CONTENT).put("Two").put("Three").put(1).put(2.0);
        _test(array, CharsetTest.JSONOArrayResource.class, APPLICATION_JSON_TYPE);
    }

    @Path("/JAXBBeanResource")
    public static class JAXBBeanResource extends CharsetTest.CharsetResource<JaxbBean> {}

    @Test
    public void testJAXBBeanXMLRepresentation() {
        _test(new JaxbBean(CharsetTest.CONTENT), CharsetTest.JAXBBeanResource.class, APPLICATION_XML_TYPE);
    }

    @Test
    public void testJAXBBeanJSONRepresentation() {
        _test(new JaxbBean(CharsetTest.CONTENT), CharsetTest.JAXBBeanResource.class, APPLICATION_JSON_TYPE);
    }

    @Provider
    public static class MyJaxbContextResolver implements ContextResolver<JAXBContext> {
        JAXBContext context;

        public MyJaxbContextResolver() throws Exception {
            context = new JettisonJaxbContext(JaxbBean.class);
        }

        public JAXBContext getContext(Class<?> objectType) {
            return objectType == (JaxbBean.class) ? context : null;
        }
    }

    @Test
    public void testJAXBBeanJSONRepresentationWithContextResolver() throws Exception {
        JaxbBean in = new JaxbBean(CharsetTest.CONTENT);
        WebTarget t = target("/JAXBBeanResource");
        for (String charset : CharsetTest.CHARSETS) {
            Response rib = t.request().post(Entity.entity(in, ("application/json;charset=" + charset)));
            byte[] inBytes = AbstractTypeTester.getRequestEntity();
            byte[] outBytes = AbstractTypeTester.getEntityAsByteArray(rib);
            AbstractTypeTester._verify(inBytes, outBytes);
        }
    }

    @Path("/ReaderResource")
    public static class ReaderResource extends CharsetTest.CharsetResource<Reader> {}

    @Test
    public void testReaderRepresentation() throws Exception {
        WebTarget t = target("/ReaderResource");
        for (String charset : CharsetTest.CHARSETS) {
            Response rib = t.request().post(Entity.entity(new java.io.StringReader(CharsetTest.CONTENT), ("text/plain;charset=" + charset)));
            byte[] inBytes = AbstractTypeTester.getRequestEntity();
            byte[] outBytes = AbstractTypeTester.getEntityAsByteArray(rib);
            AbstractTypeTester._verify(inBytes, outBytes);
        }
    }
}

