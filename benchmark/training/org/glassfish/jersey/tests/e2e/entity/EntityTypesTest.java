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


import MediaType.APPLICATION_JSON_TYPE;
import MediaType.APPLICATION_XML_TYPE;
import MediaType.TEXT_XML_TYPE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import javax.activation.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.message.internal.FileProvider;
import org.glassfish.jersey.test.util.runner.ConcurrentRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 *
 *
 * @author Paul Sandoz
 * @author Martin Matula
 */
@RunWith(ConcurrentRunner.class)
public class EntityTypesTest extends AbstractTypeTester {
    @Path("InputStreamResource")
    public static class InputStreamResource {
        @POST
        public InputStream post(final InputStream in) throws IOException {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read;
            final byte[] data = new byte[2048];
            while ((read = in.read(data)) != (-1)) {
                out.write(data, 0, read);
            } 
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Test
    public void testInputStream() {
        final ByteArrayInputStream in = new ByteArrayInputStream("CONTENT".getBytes());
        _test(in, EntityTypesTest.InputStreamResource.class);
    }

    @Path("StringResource")
    public static class StringResource extends AbstractTypeTester.AResource<String> {}

    @Test
    public void testString() {
        _test("CONTENT", EntityTypesTest.StringResource.class);
    }

    @Path("DataSourceResource")
    public static class DataSourceResource extends AbstractTypeTester.AResource<DataSource> {}

    @Path("ByteArrayResource")
    public static class ByteArrayResource extends AbstractTypeTester.AResource<byte[]> {}

    @Test
    public void testByteArrayRepresentation() {
        _test("CONTENT".getBytes(), EntityTypesTest.ByteArrayResource.class);
    }

    @Path("JaxbBeanResource")
    @Produces("application/xml")
    @Consumes("application/xml")
    public static class JaxbBeanResource extends AbstractTypeTester.AResource<JaxbBean> {}

    @Test
    public void testJaxbBeanRepresentation() {
        _test(new JaxbBean("CONTENT"), EntityTypesTest.JaxbBeanResource.class, APPLICATION_XML_TYPE);
    }

    @Path("JaxbBeanResourceMediaType")
    @Produces("application/foo+xml")
    @Consumes("application/foo+xml")
    public static class JaxbBeanResourceMediaType extends AbstractTypeTester.AResource<JaxbBean> {}

    @Test
    public void testJaxbBeanRepresentationMediaType() {
        _test(new JaxbBean("CONTENT"), EntityTypesTest.JaxbBeanResourceMediaType.class, MediaType.valueOf("application/foo+xml"));
    }

    @Test
    public void testJaxbBeanRepresentationError() {
        final WebTarget target = target("JaxbBeanResource");
        final String xml = "<root>foo</root>";
        final Response cr = target.request().post(javax.ws.rs.client.Entity.entity(xml, "application/xml"));
        Assert.assertEquals(400, cr.getStatus());
    }

    @Path("JaxbBeanTextResource")
    @Produces("text/xml")
    @Consumes("text/xml")
    public static class JaxbBeanTextResource extends AbstractTypeTester.AResource<JaxbBean> {}

    @Test
    public void testJaxbBeanTextRepresentation() {
        _test(new JaxbBean("CONTENT"), EntityTypesTest.JaxbBeanTextResource.class, TEXT_XML_TYPE);
    }

    @Path("JAXBElementBeanResource")
    @Produces("application/xml")
    @Consumes("application/xml")
    public static class JAXBElementBeanResource extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Test
    public void testJAXBElementBeanRepresentation() {
        _test(new JaxbBean("CONTENT"), EntityTypesTest.JAXBElementBeanResource.class, APPLICATION_XML_TYPE);
    }

    @Path("JAXBElementListResource")
    @Produces({ "application/xml", "application/json" })
    @Consumes({ "application/xml", "application/json" })
    public static class JAXBElementListResource extends AbstractTypeTester.AResource<List<JAXBElement<String>>> {}

    @Test
    public void testJAXBElementListXMLRepresentation() {
        _testListOrArray(true, APPLICATION_XML_TYPE);
    }

    @Test
    public void testJAXBElementListJSONRepresentation() {
        _testListOrArray(true, APPLICATION_JSON_TYPE);
    }

    @Path("JAXBElementArrayResource")
    @Produces({ "application/xml", "application/json" })
    @Consumes({ "application/xml", "application/json" })
    public static class JAXBElementArrayResource extends AbstractTypeTester.AResource<JAXBElement<String>[]> {}

    @Test
    public void testJAXBElementArrayXMLRepresentation() {
        _testListOrArray(false, APPLICATION_XML_TYPE);
    }

    @Test
    public void testJAXBElementArrayJSONRepresentation() {
        _testListOrArray(false, APPLICATION_JSON_TYPE);
    }

    @Path("JAXBElementBeanResourceMediaType")
    @Produces("application/foo+xml")
    @Consumes("application/foo+xml")
    public static class JAXBElementBeanResourceMediaType extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Test
    public void testJAXBElementBeanRepresentationMediaType() {
        _test(new JaxbBean("CONTENT"), EntityTypesTest.JAXBElementBeanResourceMediaType.class, MediaType.valueOf("application/foo+xml"));
    }

    @Test
    public void testJAXBElementBeanRepresentationError() {
        final WebTarget target = target("JAXBElementBeanResource");
        final String xml = "<root><value>foo";
        final Response cr = target.request().post(javax.ws.rs.client.Entity.entity(xml, "application/xml"));
        Assert.assertEquals(400, cr.getStatus());
    }

    @Path("JAXBElementBeanTextResource")
    @Produces("text/xml")
    @Consumes("text/xml")
    public static class JAXBElementBeanTextResource extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Test
    public void testJAXBElementBeanTextRepresentation() {
        _test(new JaxbBean("CONTENT"), EntityTypesTest.JAXBElementBeanTextResource.class, TEXT_XML_TYPE);
    }

    @Path("JaxbBeanResourceAtom")
    @Produces("application/atom+xml")
    @Consumes("application/atom+xml")
    public static class JaxbBeanResourceAtom extends AbstractTypeTester.AResource<JAXBElement<JaxbBean>> {}

    @Test
    public void testJaxbBeanRepresentationMediaTypeAtom() {
        _test(new JaxbBean("CONTENT"), EntityTypesTest.JaxbBeanResourceAtom.class, MediaType.valueOf("application/atom+xml"));
    }

    @Path("JAXBTypeResource")
    @Produces("application/xml")
    @Consumes("application/xml")
    public static class JAXBTypeResource {
        @POST
        public JaxbBean post(final JaxbBeanType t) {
            return new JaxbBean(t.value);
        }
    }

    @Test
    public void testJAXBTypeRepresentation() {
        final WebTarget target = target("JAXBTypeResource");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBeanType out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/xml"), JaxbBeanType.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBTypeResourceMediaType")
    @Produces("application/foo+xml")
    @Consumes("application/foo+xml")
    public static class JAXBTypeResourceMediaType extends EntityTypesTest.JAXBTypeResource {}

    @Test
    public void testJAXBTypeRepresentationMediaType() {
        final WebTarget target = target("JAXBTypeResourceMediaType");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBeanType out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/foo+xml"), JaxbBeanType.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBObjectResource")
    @Produces("application/xml")
    @Consumes("application/xml")
    public static class JAXBObjectResource {
        @POST
        public Object post(final Object o) {
            return o;
        }
    }

    @Provider
    public static class JAXBObjectResolver implements ContextResolver<JAXBContext> {
        public JAXBContext getContext(final Class<?> c) {
            if ((Object.class) == c) {
                try {
                    return JAXBContext.newInstance(JaxbBean.class);
                } catch (final JAXBException ex) {
                }
            }
            return null;
        }
    }

    @Test
    public void testJAXBObjectRepresentation() {
        final WebTarget target = target("JAXBObjectResource");
        final Object in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/xml"), JaxbBean.class);
        Assert.assertEquals(in, out);
    }

    @Path("JAXBObjectResourceMediaType")
    @Produces("application/foo+xml")
    @Consumes("application/foo+xml")
    public static class JAXBObjectResourceMediaType extends EntityTypesTest.JAXBObjectResource {}

    @Test
    public void testJAXBObjectRepresentationMediaType() {
        final WebTarget target = target("JAXBObjectResourceMediaType");
        final Object in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/foo+xml"), JaxbBean.class);
        Assert.assertEquals(in, out);
    }

    @Test
    public void testJAXBObjectRepresentationError() {
        final WebTarget target = target("JAXBObjectResource");
        final String xml = "<root>foo</root>";
        final Response cr = target.request().post(javax.ws.rs.client.Entity.entity(xml, "application/xml"));
        Assert.assertEquals(400, cr.getStatus());
    }

    @Path("FileResource")
    public static class FileResource extends AbstractTypeTester.AResource<File> {}

    @Test
    public void testFileRepresentation() throws IOException {
        final FileProvider fp = new FileProvider();
        final File in = fp.readFrom(File.class, File.class, null, null, null, new ByteArrayInputStream("CONTENT".getBytes()));
        _test(in, EntityTypesTest.FileResource.class);
    }

    @Produces("application/x-www-form-urlencoded")
    @Consumes("application/x-www-form-urlencoded")
    @Path("FormResource")
    public static class FormResource extends AbstractTypeTester.AResource<Form> {}

    @Test
    public void testFormRepresentation() {
        final Form fp = new Form();
        fp.param("Email", "johndoe@gmail.com");
        fp.param("Passwd", "north 23AZ");
        fp.param("service", "cl");
        fp.param("source", "Gulp-CalGul-1.05");
        final WebTarget target = target("FormResource");
        final Form response = target.request().post(javax.ws.rs.client.Entity.entity(fp, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Form.class);
        Assert.assertEquals(fp.asMap().size(), response.asMap().size());
        for (final Map.Entry<String, List<String>> entry : fp.asMap().entrySet()) {
            final List<String> s = response.asMap().get(entry.getKey());
            Assert.assertEquals(entry.getValue().size(), s.size());
            for (Iterator<String> it1 = entry.getValue().listIterator(), it2 = s.listIterator(); it1.hasNext();) {
                Assert.assertEquals(it1.next(), it2.next());
            }
        }
    }

    @Produces("application/json")
    @Consumes("application/json")
    @Path("JSONObjectResource")
    public static class JSONObjectResource extends AbstractTypeTester.AResource<JSONObject> {}

    @Test
    public void testJSONObjectRepresentation() throws Exception {
        final JSONObject object = new JSONObject();
        object.put("userid", 1234).put("username", "1234").put("email", "a@b").put("password", "****");
        _test(object, EntityTypesTest.JSONObjectResource.class, APPLICATION_JSON_TYPE);
    }

    @Produces("application/xxx+json")
    @Consumes("application/xxx+json")
    @Path("JSONObjectResourceGeneralMediaType")
    public static class JSONObjectResourceGeneralMediaType extends AbstractTypeTester.AResource<JSONObject> {}

    @Test
    public void testJSONObjectRepresentationGeneralMediaTyp() throws Exception {
        final JSONObject object = new JSONObject();
        object.put("userid", 1234).put("username", "1234").put("email", "a@b").put("password", "****");
        _test(object, EntityTypesTest.JSONObjectResourceGeneralMediaType.class, MediaType.valueOf("application/xxx+json"));
    }

    @Produces("application/json")
    @Consumes("application/json")
    @Path("JSONOArrayResource")
    public static class JSONOArrayResource extends AbstractTypeTester.AResource<JSONArray> {}

    @Test
    public void testJSONArrayRepresentation() throws Exception {
        final JSONArray array = new JSONArray();
        array.put("One").put("Two").put("Three").put(1).put(2.0);
        _test(array, EntityTypesTest.JSONOArrayResource.class, APPLICATION_JSON_TYPE);
    }

    @Produces("application/xxx+json")
    @Consumes("application/xxx+json")
    @Path("JSONOArrayResourceGeneralMediaType")
    public static class JSONOArrayResourceGeneralMediaType extends AbstractTypeTester.AResource<JSONArray> {}

    @Test
    public void testJSONArrayRepresentationGeneralMediaType() throws Exception {
        final JSONArray array = new JSONArray();
        array.put("One").put("Two").put("Three").put(1).put(2.0);
        _test(array, EntityTypesTest.JSONOArrayResourceGeneralMediaType.class, MediaType.valueOf("application/xxx+json"));
    }

    // @Path("FeedResource")
    // public static class FeedResource extends AResource<Feed> {
    // }
    // 
    // @Test
    // public void testFeedRepresentation() throws Exception {
    // InputStream in = this.getClass().getResourceAsStream("feed.xml");
    // AtomFeedProvider afp = new AtomFeedProvider();
    // Feed f = afp.readFrom(Feed.class, Feed.class, null, null, null, in);
    // 
    // _test(f, FeedResource.class);
    // }
    // 
    // @Path("EntryResource")
    // public static class EntryResource extends AResource<Entry> {
    // }
    // 
    // @Test
    // public void testEntryRepresentation() throws Exception {
    // InputStream in = this.getClass().getResourceAsStream("entry.xml");
    // AtomEntryProvider afp = new AtomEntryProvider();
    // Entry e = afp.readFrom(Entry.class, Entry.class, null, null, null, in);
    // 
    // _test(e, EntryResource.class);
    // }
    @Path("ReaderResource")
    public static class ReaderResource extends AbstractTypeTester.AResource<Reader> {}

    @Test
    public void testReaderRepresentation() throws Exception {
        _test(new StringReader("CONTENT"), EntityTypesTest.ReaderResource.class);
    }

    private static final String XML_DOCUMENT = "<n:x xmlns:n=\"urn:n\"><n:e>CONTNET</n:e></n:x>";

    @Path("StreamSourceResource")
    public static class StreamSourceResource extends AbstractTypeTester.AResource<StreamSource> {}

    @Test
    public void testStreamSourceRepresentation() throws Exception {
        final StreamSource ss = new StreamSource(new ByteArrayInputStream(EntityTypesTest.XML_DOCUMENT.getBytes()));
        _test(ss, EntityTypesTest.StreamSourceResource.class);
    }

    @Path("SAXSourceResource")
    public static class SAXSourceResource extends AbstractTypeTester.AResource<SAXSource> {}

    @Test
    public void testSAXSourceRepresentation() throws Exception {
        final StreamSource ss = new StreamSource(new ByteArrayInputStream(EntityTypesTest.XML_DOCUMENT.getBytes()));
        _test(ss, EntityTypesTest.SAXSourceResource.class);
    }

    @Path("DOMSourceResource")
    public static class DOMSourceResource extends AbstractTypeTester.AResource<DOMSource> {}

    @Test
    public void testDOMSourceRepresentation() throws Exception {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final Document d = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(EntityTypesTest.XML_DOCUMENT)));
        final DOMSource ds = new DOMSource(d);
        _test(ds, EntityTypesTest.DOMSourceResource.class);
    }

    @Path("DocumentResource")
    public static class DocumentResource extends AbstractTypeTester.AResource<Document> {}

    @Test
    public void testDocumentRepresentation() throws Exception {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final Document d = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(EntityTypesTest.XML_DOCUMENT)));
        _test(d, EntityTypesTest.DocumentResource.class);
    }

    @Path("FormMultivaluedMapResource")
    @Produces("application/x-www-form-urlencoded")
    @Consumes("application/x-www-form-urlencoded")
    public static class FormMultivaluedMapResource {
        @POST
        public MultivaluedMap<String, String> post(final MultivaluedMap<String, String> t) {
            return t;
        }
    }

    @Test
    public void testFormMultivaluedMapRepresentation() {
        final MultivaluedMap<String, String> fp = new MultivaluedStringMap();
        fp.add("Email", "johndoe@gmail.com");
        fp.add("Passwd", "north 23AZ");
        fp.add("service", "cl");
        fp.add("source", "Gulp-CalGul-1.05");
        fp.add("source", "foo.java");
        fp.add("source", "bar.java");
        final WebTarget target = target("FormMultivaluedMapResource");
        final MultivaluedMap _fp = target.request().post(javax.ws.rs.client.Entity.entity(fp, "application/x-www-form-urlencoded"), MultivaluedMap.class);
        Assert.assertEquals(fp, _fp);
    }

    @Path("StreamingOutputResource")
    public static class StreamingOutputResource {
        @GET
        public StreamingOutput get() {
            return new StreamingOutput() {
                public void write(final OutputStream entity) throws IOException {
                    entity.write("CONTENT".getBytes());
                }
            };
        }
    }

    @Test
    public void testStreamingOutputRepresentation() throws Exception {
        final WebTarget target = target("StreamingOutputResource");
        Assert.assertEquals("CONTENT", target.request().get(String.class));
    }

    @Path("JAXBElementBeanJSONResource")
    @Consumes("application/json")
    @Produces("application/json")
    public static class JAXBElementBeanJSONResource extends AbstractTypeTester.AResource<JAXBElement<String>> {}

    @Test
    public void testJAXBElementBeanJSONRepresentation() {
        final WebTarget target = target("JAXBElementBeanJSONResource");
        final Response rib = target.request().post(javax.ws.rs.client.Entity.entity(new JAXBElement(new javax.xml.namespace.QName("test"), String.class, "CONTENT"), "application/json"));
        // TODO: the following would not be needed if i knew how to workaround JAXBElement<String>.class literal
        final byte[] inBytes = AbstractTypeTester.getRequestEntity();
        final byte[] outBytes = AbstractTypeTester.getEntityAsByteArray(rib);
        Assert.assertEquals(new String(outBytes), inBytes.length, outBytes.length);
        for (int i = 0; i < (inBytes.length); i++) {
            if ((inBytes[i]) != (outBytes[i])) {
                Assert.assertEquals(("Index: " + i), inBytes[i], outBytes[i]);
            }
        }
    }

    @Path("JaxbBeanResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JaxbBeanResourceJSON extends AbstractTypeTester.AResource<JaxbBean> {}

    @Test
    public void testJaxbBeanRepresentationJSON() {
        final WebTarget target = target("JaxbBeanResourceJSON");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JaxbBeanResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JaxbBeanResourceJSONMediaType extends AbstractTypeTester.AResource<JaxbBean> {}

    @Test
    public void testJaxbBeanRepresentationJSONMediaType() {
        final WebTarget target = target("JaxbBeanResourceJSONMediaType");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/foo+json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBElementBeanResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JAXBElementBeanResourceJSON extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Test
    public void testJAXBElementBeanRepresentationJSON() {
        final WebTarget target = target("JAXBElementBeanResourceJSON");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBElementBeanResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JAXBElementBeanResourceJSONMediaType extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Test
    public void testJAXBElementBeanRepresentationJSONMediaType() {
        final WebTarget target = target("JAXBElementBeanResourceJSONMediaType");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBean out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/foo+json"), JaxbBean.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBTypeResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JAXBTypeResourceJSON {
        @POST
        public JaxbBean post(final JaxbBeanType t) {
            return new JaxbBean(t.value);
        }
    }

    @Test
    public void testJAXBTypeRepresentationJSON() {
        final WebTarget target = target("JAXBTypeResourceJSON");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBeanType out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/json"), JaxbBeanType.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JAXBTypeResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JAXBTypeResourceJSONMediaType {
        @POST
        public JaxbBean post(final JaxbBeanType t) {
            return new JaxbBean(t.value);
        }
    }

    @Test
    public void testJAXBTypeRepresentationJSONMediaType() {
        final WebTarget target = target("JAXBTypeResourceJSONMediaType");
        final JaxbBean in = new JaxbBean("CONTENT");
        final JaxbBeanType out = target.request().post(javax.ws.rs.client.Entity.entity(in, "application/foo+json"), JaxbBeanType.class);
        Assert.assertEquals(in.value, out.value);
    }

    @Path("JaxbBeanResourceFastInfoset")
    @Produces("application/fastinfoset")
    @Consumes("application/fastinfoset")
    public static class JaxbBeanResourceFastInfoset extends AbstractTypeTester.AResource<JaxbBean> {}

    @Path("JAXBElementBeanResourceFastInfoset")
    @Produces("application/fastinfoset")
    @Consumes("application/fastinfoset")
    public static class JAXBElementBeanResourceFastInfoset extends AbstractTypeTester.AResource<JAXBElement<JaxbBeanType>> {}

    @Path("JAXBTypeResourceFastInfoset")
    @Produces("application/fastinfoset")
    @Consumes("application/fastinfoset")
    public static class JAXBTypeResourceFastInfoset {
        @POST
        public JaxbBean post(final JaxbBeanType t) {
            return new JaxbBean(t.value);
        }
    }

    @Path("JAXBListResource")
    @Produces("application/xml")
    @Consumes("application/xml")
    public static class JAXBListResource {
        @POST
        public List<JaxbBean> post(final List<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("set")
        public Set<JaxbBean> postSet(final Set<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("queue")
        public Queue<JaxbBean> postQueue(final Queue<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("stack")
        public Stack<JaxbBean> postStack(final Stack<JaxbBean> l) {
            return l;
        }

        @POST
        @Path("custom")
        public MyArrayList<JaxbBean> postCustom(final MyArrayList<JaxbBean> l) {
            return l;
        }

        @GET
        public Collection<JaxbBean> get() {
            final ArrayList<JaxbBean> l = new ArrayList<>();
            l.add(new JaxbBean("one"));
            l.add(new JaxbBean("two"));
            l.add(new JaxbBean("three"));
            return l;
        }

        @POST
        @Path("type")
        public List<JaxbBean> postType(final Collection<JaxbBeanType> l) {
            final List<JaxbBean> beans = new ArrayList<>();
            for (final JaxbBeanType t : l) {
                beans.add(new JaxbBean(t.value));
            }
            return beans;
        }
    }

    @Path("JAXBArrayResource")
    @Produces("application/xml")
    @Consumes("application/xml")
    public static class JAXBArrayResource {
        @POST
        public JaxbBean[] post(final JaxbBean[] l) {
            return l;
        }

        @GET
        public JaxbBean[] get() {
            final ArrayList<JaxbBean> l = new ArrayList<>();
            l.add(new JaxbBean("one"));
            l.add(new JaxbBean("two"));
            l.add(new JaxbBean("three"));
            return l.toArray(new JaxbBean[l.size()]);
        }

        @POST
        @Path("type")
        public JaxbBean[] postType(final JaxbBeanType[] l) {
            final List<JaxbBean> beans = new ArrayList<>();
            for (final JaxbBeanType t : l) {
                beans.add(new JaxbBean(t.value));
            }
            return beans.toArray(new JaxbBean[beans.size()]);
        }
    }

    @Test
    public void testJAXBArrayRepresentation() {
        final WebTarget target = target("JAXBArrayResource");
        final JaxbBean[] a = target.request().get(JaxbBean[].class);
        JaxbBean[] b = target.request().post(javax.ws.rs.client.Entity.entity(a, "application/xml"), JaxbBean[].class);
        Assert.assertEquals(a.length, b.length);
        for (int i = 0; i < (a.length); i++) {
            Assert.assertEquals(a[i], b[i]);
        }
        b = target.path("type").request().post(javax.ws.rs.client.Entity.entity(a, "application/xml"), JaxbBean[].class);
        Assert.assertEquals(a.length, b.length);
        for (int i = 0; i < (a.length); i++) {
            Assert.assertEquals(a[i], b[i]);
        }
    }

    @Path("JAXBListResourceMediaType")
    @Produces("application/foo+xml")
    @Consumes("application/foo+xml")
    public static class JAXBListResourceMediaType extends EntityTypesTest.JAXBListResource {}

    @Test
    public void testJAXBListRepresentationMediaType() {
        final WebTarget target = target("JAXBListResourceMediaType");
        Collection<JaxbBean> a = target.request().get(new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Collection<JaxbBean> b = target.request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Collection<JaxbBean>>(a) {}, "application/foo+xml"), new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        b = target.path("type").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Collection<JaxbBean>>(a) {}, "application/foo+xml"), new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        a = new LinkedList<>(a);
        b = target.path("queue").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Queue<JaxbBean>>(((Queue<JaxbBean>) (a))) {}, "application/foo+xml"), new javax.ws.rs.core.GenericType<Queue<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        a = new HashSet<>(a);
        b = target.path("set").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Set<JaxbBean>>(((Set<JaxbBean>) (a))) {}, "application/foo+xml"), new javax.ws.rs.core.GenericType<Set<JaxbBean>>() {});
        final Comparator<JaxbBean> c = new Comparator<JaxbBean>() {
            @Override
            public int compare(final JaxbBean t, final JaxbBean t1) {
                return t.value.compareTo(t1.value);
            }
        };
        final TreeSet<JaxbBean> t1 = new TreeSet<>(c);
        final TreeSet<JaxbBean> t2 = new TreeSet<>(c);
        t1.addAll(a);
        t2.addAll(b);
        Assert.assertEquals(t1, t2);
        final Stack<JaxbBean> s = new Stack<>();
        s.addAll(a);
        b = target.path("stack").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Stack<JaxbBean>>(s) {}, "application/foo+xml"), new javax.ws.rs.core.GenericType<Stack<JaxbBean>>() {});
        Assert.assertEquals(s, b);
        a = new MyArrayList<>(a);
        b = target.path("custom").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<MyArrayList<JaxbBean>>(((MyArrayList<JaxbBean>) (a))) {}, "application/foo+xml"), new javax.ws.rs.core.GenericType<MyArrayList<JaxbBean>>() {});
        Assert.assertEquals(a, b);
    }

    @Test
    public void testJAXBListRepresentationError() {
        final WebTarget target = target("JAXBListResource");
        final String xml = "<root><value>foo";
        final Response cr = target.request().post(javax.ws.rs.client.Entity.entity(xml, "application/xml"));
        Assert.assertEquals(400, cr.getStatus());
    }

    @Path("JAXBListResourceFastInfoset")
    @Produces("application/fastinfoset")
    @Consumes("application/fastinfoset")
    public static class JAXBListResourceFastInfoset extends EntityTypesTest.JAXBListResource {}

    @Path("JAXBListResourceJSON")
    @Produces("application/json")
    @Consumes("application/json")
    public static class JAXBListResourceJSON extends EntityTypesTest.JAXBListResource {}

    @Test
    public void testJAXBListRepresentationJSON() throws Exception {
        final WebTarget target = target("JAXBListResourceJSON");
        Collection<JaxbBean> a = target.request().get(new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Collection<JaxbBean> b = target.request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Collection<JaxbBean>>(a) {}, "application/json"), new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        b = target.path("type").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Collection<JaxbBean>>(a) {}, "application/json"), new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        a = new LinkedList<>(a);
        b = target.path("queue").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Queue<JaxbBean>>(((Queue<JaxbBean>) (a))) {}, "application/json"), new javax.ws.rs.core.GenericType<Queue<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        a = new HashSet<>(a);
        b = target.path("set").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Set<JaxbBean>>(((Set<JaxbBean>) (a))) {}, "application/json"), new javax.ws.rs.core.GenericType<Set<JaxbBean>>() {});
        final Comparator<JaxbBean> c = new Comparator<JaxbBean>() {
            @Override
            public int compare(final JaxbBean t, final JaxbBean t1) {
                return t.value.compareTo(t1.value);
            }
        };
        final TreeSet<JaxbBean> t1 = new TreeSet<>(c);
        final TreeSet<JaxbBean> t2 = new TreeSet<>(c);
        t1.addAll(a);
        t2.addAll(b);
        Assert.assertEquals(t1, t2);
        final Stack<JaxbBean> s = new Stack<>();
        s.addAll(a);
        b = target.path("stack").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Stack<JaxbBean>>(s) {}, "application/json"), new javax.ws.rs.core.GenericType<Stack<JaxbBean>>() {});
        Assert.assertEquals(s, b);
        a = new MyArrayList<>(a);
        b = target.path("custom").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<MyArrayList<JaxbBean>>(((MyArrayList<JaxbBean>) (a))) {}, "application/json"), new javax.ws.rs.core.GenericType<MyArrayList<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        // TODO: would be nice to produce/consume a real JSON array like following
        // instead of what we have now:
        // JSONArray a = r.get(JSONArray.class);
        // JSONArray b = new JSONArray().
        // put(new JSONObject().put("value", "one")).
        // put(new JSONObject().put("value", "two")).
        // put(new JSONObject().put("value", "three"));
        // assertEquals(a.toString(), b.toString());
        // JSONArray c = r.post(JSONArray.class, b);
        // assertEquals(a.toString(), c.toString());
    }

    @Path("JAXBListResourceJSONMediaType")
    @Produces("application/foo+json")
    @Consumes("application/foo+json")
    public static class JAXBListResourceJSONMediaType extends EntityTypesTest.JAXBListResource {}

    @Test
    public void testJAXBListRepresentationJSONMediaType() throws Exception {
        final WebTarget target = target("JAXBListResourceJSONMediaType");
        final Collection<JaxbBean> a = target.request().get(new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Collection<JaxbBean> b = target.request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Collection<JaxbBean>>(a) {}, "application/foo+json"), new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        b = target.path("type").request().post(javax.ws.rs.client.Entity.entity(new javax.ws.rs.core.GenericEntity<Collection<JaxbBean>>(a) {}, "application/foo+json"), new javax.ws.rs.core.GenericType<Collection<JaxbBean>>() {});
        Assert.assertEquals(a, b);
        // TODO: would be nice to produce/consume a real JSON array like following
        // instead of what we have now:
        // JSONArray a = r.get(JSONArray.class);
        // JSONArray b = new JSONArray().
        // put(new JSONObject().put("value", "one")).
        // put(new JSONObject().put("value", "two")).
        // put(new JSONObject().put("value", "three"));
        // assertEquals(a.toString(), b.toString());
        // JSONArray c = r.post(JSONArray.class, b);
        // assertEquals(a.toString(), c.toString());
    }

    @Path("/NoContentTypeJAXBResource")
    public static class NoContentTypeJAXBResource {
        @POST
        public JaxbBean post(@Context
        final HttpHeaders headers, final JaxbBean bean) {
            Assert.assertThat(headers.getMediaType(), CoreMatchers.is(APPLICATION_XML_TYPE));
            return bean;
        }
    }

    @Test
    public void testNoContentTypeJaxbEntity() throws IOException {
        Assert.assertThat(target("NoContentTypeJAXBResource").request("application/xml").post(javax.ws.rs.client.Entity.xml(new JaxbBean("foo"))).getMediaType(), CoreMatchers.is(APPLICATION_XML_TYPE));
    }
}

