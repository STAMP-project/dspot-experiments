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
package org.glassfish.jersey.media.multipart.internal;


import Invocation.Builder;
import MediaType.MULTIPART_FORM_DATA;
import MediaType.MULTIPART_FORM_DATA_TYPE;
import MediaType.TEXT_PLAIN_TYPE;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for multipart {@code MessageBodyReader} and {@code MessageBodyWriter} as well as {@code FormDataMultiPart} and {@code FormDataParam} injections.
 *
 * @author Paul Sandoz
 * @author Michal Gajdos
 */
public class FormDataMultiPartReaderWriterTest extends MultiPartJerseyTest {
    @Path("/ProducesFormDataUsingMultiPart")
    public static class ProducesFormDataUsingMultiPart {
        @GET
        @Produces("multipart/form-data")
        public Response get() {
            final MultiPart entity = new MultiPart();
            entity.setMediaType(MULTIPART_FORM_DATA_TYPE);
            final BodyPart part1 = new BodyPart();
            part1.setMediaType(TEXT_PLAIN_TYPE);
            part1.getHeaders().add("Content-Disposition", "form-data; name=\"field1\"");
            part1.setEntity("Joe Blow\r\n");
            final BodyPart part2 = new BodyPart();
            part2.setMediaType(TEXT_PLAIN_TYPE);
            part2.getHeaders().add("Content-Disposition", "form-data; name=\"pics\"; filename=\"file1.txt\"");
            part2.setEntity("... contents of file1.txt ...\r\n");
            return Response.ok(entity.bodyPart(part1).bodyPart(part2)).build();
        }
    }

    // Test a response of type "multipart/form-data".  The example comes from
    // Section 6 of RFC 1867.
    @Test
    public void testProducesFormDataUsingMultiPart() {
        final Invocation.Builder request = target().path("ProducesFormDataUsingMultiPart").request("multipart/form-data");
        try {
            final MultiPart result = request.get(MultiPart.class);
            checkMediaType(new MediaType("multipart", "form-data"), result.getMediaType());
            Assert.assertEquals(2, result.getBodyParts().size());
            final BodyPart part1 = result.getBodyParts().get(0);
            checkMediaType(new MediaType("text", "plain"), part1.getMediaType());
            checkEntity("Joe Blow\r\n", ((BodyPartEntity) (part1.getEntity())));
            final String value1 = part1.getHeaders().getFirst("Content-Disposition");
            Assert.assertEquals("form-data; name=\"field1\"", value1);
            final BodyPart part2 = result.getBodyParts().get(1);
            checkMediaType(new MediaType("text", "plain"), part2.getMediaType());
            checkEntity("... contents of file1.txt ...\r\n", ((BodyPartEntity) (part2.getEntity())));
            final String value2 = part2.getHeaders().getFirst("Content-Disposition");
            Assert.assertEquals("form-data; name=\"pics\"; filename=\"file1.txt\"", value2);
            result.getParameterizedHeaders();
            result.cleanup();
        } catch (IOException | ParseException e) {
            e.printStackTrace(System.out);
            Assert.fail(("Caught exception: " + e));
        }
    }

    @Path("/ProducesFormDataResource")
    public static class ProducesFormDataResource {
        // Test "multipart/form-data" the easy way (with subclasses)
        @GET
        @Produces("multipart/form-data")
        public Response get() {
            // Exercise builder pattern with explicit content type
            final MultiPartBean bean = new MultiPartBean("myname", "myvalue");
            return Response.ok(new FormDataMultiPart().field("foo", "bar").field("baz", "bop").field("bean", bean, new MediaType("x-application", "x-format"))).build();
        }
    }

    @Test
    public void testProducesFormDataResource() throws Exception {
        final Invocation.Builder request = target().path("ProducesFormDataResource").request("multipart/form-data");
        final FormDataMultiPart result = request.get(FormDataMultiPart.class);
        checkMediaType(new MediaType("multipart", "form-data"), result.getMediaType());
        Assert.assertEquals(3, result.getFields().size());
        Assert.assertNotNull(result.getField("foo"));
        Assert.assertEquals("bar", result.getField("foo").getValue());
        Assert.assertNotNull(result.getField("baz"));
        Assert.assertEquals("bop", result.getField("baz").getValue());
        Assert.assertNotNull(result.getField("bean"));
        final MultiPartBean bean = result.getField("bean").getValueAs(MultiPartBean.class);
        Assert.assertNotNull(bean);
        Assert.assertEquals("myname", bean.getName());
        Assert.assertEquals("myvalue", bean.getValue());
        result.cleanup();
    }

    @Path("/ProducesFormDataCharsetResource")
    public static class ProducesFormDataCharsetResource {
        // Test "multipart/form-data" the easy way (with subclasses)
        @GET
        @Produces("multipart/form-data")
        public Response get(@QueryParam("charset")
        final String charset) {
            return Response.ok(new FormDataMultiPart().field("foo", "\u00a9 CONTENT \u00ff \u2200 \u22ff", MediaType.valueOf(("text/plain;charset=" + charset)))).build();
        }
    }

    @Test
    public void testProducesFormDataCharsetResource() throws Exception {
        final String c = "\u00a9 CONTENT \u00ff \u2200 \u22ff";
        for (final String charset : Arrays.asList("US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16")) {
            final FormDataMultiPart p = target().path("ProducesFormDataCharsetResource").queryParam("charset", charset).request("multipart/form-data").get(FormDataMultiPart.class);
            final String expected = new String(c.getBytes(charset), charset);
            Assert.assertEquals(expected, p.getField("foo").getValue());
        }
    }

    @Path("/ConsumesFormDataResource")
    public static class ConsumesFormDataResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public Response get(final FormDataMultiPart multiPart) throws IOException {
            if (!((multiPart.getBodyParts().size()) == 3)) {
                return Response.ok((("FAILED:  Number of body parts is " + (multiPart.getBodyParts().size())) + " instead of 3")).build();
            }
            if ((multiPart.getField("foo")) == null) {
                return Response.ok("FAILED:  Missing field 'foo'").build();
            } else
                if (!("bar".equals(multiPart.getField("foo").getValue()))) {
                    return Response.ok((("FAILED:  Field 'foo' has value '" + (multiPart.getField("foo").getValue())) + "' instead of 'bar'")).build();
                }

            if ((multiPart.getField("baz")) == null) {
                return Response.ok("FAILED:  Missing field 'baz'").build();
            } else
                if (!("bop".equals(multiPart.getField("baz").getValue()))) {
                    return Response.ok((("FAILED:  Field 'baz' has value '" + (multiPart.getField("baz").getValue())) + "' instead of 'bop'")).build();
                }

            if ((multiPart.getField("bean")) == null) {
                return Response.ok("FAILED:  Missing field 'bean'").build();
            }
            final MultiPartBean bean = multiPart.getField("bean").getValueAs(MultiPartBean.class);
            if (!(bean.getName().equals("myname"))) {
                return Response.ok(("FAILED:  Second part name = " + (bean.getName()))).build();
            }
            if (!(bean.getValue().equals("myvalue"))) {
                return Response.ok(("FAILED:  Second part value = " + (bean.getValue()))).build();
            }
            return Response.ok("SUCCESS:  All tests passed").build();
        }
    }

    @Test
    public void testConsumesFormDataResource() {
        final Invocation.Builder request = target().path("ConsumesFormDataResource").request("text/plain");
        final MultiPartBean bean = new MultiPartBean("myname", "myvalue");
        final FormDataMultiPart entity = new FormDataMultiPart().field("foo", "bar").field("baz", "bop").field("bean", bean, new MediaType("x-application", "x-format"));
        final String response = request.put(Entity.entity(entity, "multipart/form-data"), String.class);
        if (!(response.startsWith("SUCCESS:"))) {
            Assert.fail((("Response is '" + response) + "'"));
        }
    }

    @Path("/ConsumesFormDataParamResource")
    public static class ConsumesFormDataParamResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public Response get(@FormDataParam("foo")
        final String foo, @FormDataParam("baz")
        final String baz, @FormDataParam("bean")
        final MultiPartBean bean, @FormDataParam("unknown1")
        final String unknown1, @FormDataParam("unknown2")
        @DefaultValue("UNKNOWN")
        final String unknown2, final FormDataMultiPart fdmp) {
            if (!("bar".equals(foo))) {
                return Response.ok((("FAILED:  Value of 'foo' is '" + foo) + "' instead of 'bar'")).build();
            } else
                if (!("bop".equals(baz))) {
                    return Response.ok((("FAILED:  Value of 'baz' is '" + baz) + "' instead of 'bop'")).build();
                } else
                    if (bean == null) {
                        return Response.ok("FAILED:  Value of 'bean' is NULL").build();
                    } else
                        if (!((bean.getName().equals("myname")) && (bean.getValue().equals("myvalue")))) {
                            return Response.ok("FAILED:  Value of 'bean.myName' and 'bean.MyValue' are not 'myname' and 'myvalue'").build();
                        } else
                            if (unknown1 != null) {
                                return Response.ok((("FAILED:  Value of 'unknown1' is '" + unknown1) + "' instead of NULL")).build();
                            } else
                                if (!("UNKNOWN".equals(unknown2))) {
                                    return Response.ok((("FAILED:  Value of 'unknown2' is '" + unknown2) + "' instead of 'UNKNOWN'")).build();
                                } else
                                    if (fdmp == null) {
                                        return Response.ok("FAILED:  Value of fdmp is NULL").build();
                                    } else
                                        if ((fdmp.getFields().size()) != 3) {
                                            return Response.ok((("FAILED:  Value of fdmp.getFields().size() is " + (fdmp.getFields().size())) + " instead of 3")).build();
                                        }







            return Response.ok("SUCCESS:  All tests passed").build();
        }
    }

    @Test
    public void testConsumesFormDataParamResource() {
        final Invocation.Builder request = target().path("ConsumesFormDataParamResource").request("text/plain");
        final MultiPartBean bean = new MultiPartBean("myname", "myvalue");
        final FormDataMultiPart entity = new FormDataMultiPart().field("foo", "bar").field("baz", "bop").field("bean", bean, new MediaType("x-application", "x-format"));
        final String response = request.put(Entity.entity(entity, "multipart/form-data"), String.class);
        if (!(response.startsWith("SUCCESS:"))) {
            Assert.fail((("Response is '" + response) + "'"));
        }
    }

    @Path("/FormDataTypesResource")
    public static class FormDataTypesResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String get(@FormDataParam("foo")
        final FormDataContentDisposition fooDisp, @FormDataParam("foo")
        final FormDataBodyPart fooPart, @FormDataParam("baz")
        final FormDataContentDisposition bazDisp, @FormDataParam("baz")
        final FormDataBodyPart bazPart) throws IOException {
            Assert.assertNotNull(fooDisp);
            Assert.assertNotNull(fooPart);
            Assert.assertNotNull(bazDisp);
            Assert.assertNotNull(bazPart);
            Assert.assertEquals("foo", fooDisp.getName());
            Assert.assertEquals("foo", fooPart.getName());
            Assert.assertEquals("bar", fooPart.getValue());
            Assert.assertEquals("baz", bazDisp.getName());
            Assert.assertEquals("baz", bazPart.getName());
            Assert.assertEquals("bop", bazPart.getValue());
            return "OK";
        }
    }

    @Test
    public void testFormDataTypesResource() {
        final Invocation.Builder request = target().path("FormDataTypesResource").request("text/plain");
        final FormDataMultiPart entity = new FormDataMultiPart().field("foo", "bar").field("baz", "bop");
        final String response = request.put(Entity.entity(entity, "multipart/form-data"), String.class);
        Assert.assertEquals("OK", response);
    }

    @Path("/FormDataListTypesResource")
    public static class FormDataListTypesResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String get(@FormDataParam("foo")
        final List<FormDataContentDisposition> fooDisp, @FormDataParam("foo")
        final List<FormDataBodyPart> fooPart, @FormDataParam("baz")
        final List<FormDataContentDisposition> bazDisp, @FormDataParam("baz")
        final List<FormDataBodyPart> bazPart) {
            Assert.assertNotNull(fooDisp);
            Assert.assertNotNull(fooPart);
            Assert.assertNotNull(bazDisp);
            Assert.assertNotNull(bazPart);
            Assert.assertEquals(2, fooDisp.size());
            Assert.assertEquals(2, fooPart.size());
            Assert.assertEquals(2, bazDisp.size());
            Assert.assertEquals(2, bazPart.size());
            return "OK";
        }
    }

    @Test
    public void testFormDataListTypesResource() {
        final Invocation.Builder request = target().path("FormDataListTypesResource").request("text/plain");
        final FormDataMultiPart entity = new FormDataMultiPart().field("foo", "bar").field("foo", "bar2").field("baz", "bop").field("baz", "bop2");
        final String response = request.put(Entity.entity(entity, "multipart/form-data"), String.class);
        Assert.assertEquals("OK", response);
    }

    @Path("/FormDataCollectionTypesResource")
    public static class FormDataCollectionTypesResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String get(@FormDataParam("foo")
        final Collection<FormDataContentDisposition> fooDisp, @FormDataParam("foo")
        final Collection<FormDataBodyPart> fooPart, @FormDataParam("baz")
        final Collection<FormDataContentDisposition> bazDisp, @FormDataParam("baz")
        final Collection<FormDataBodyPart> bazPart) {
            Assert.assertNotNull(fooDisp);
            Assert.assertNotNull(fooPart);
            Assert.assertNotNull(bazDisp);
            Assert.assertNotNull(bazPart);
            Assert.assertEquals(2, fooDisp.size());
            Assert.assertEquals(2, fooPart.size());
            Assert.assertEquals(2, bazDisp.size());
            Assert.assertEquals(2, bazPart.size());
            return "OK";
        }
    }

    @Test
    public void testFormDataCollectionTypesResource() {
        final Invocation.Builder request = target().path("FormDataCollectionTypesResource").request("text/plain");
        final FormDataMultiPart entity = new FormDataMultiPart().field("foo", "bar").field("foo", "bar2").field("baz", "bop").field("baz", "bop2");
        final String response = request.put(Entity.entity(entity, "multipart/form-data"), String.class);
        Assert.assertEquals("OK", response);
    }

    @Path("/PrimitivesFormDataParamResource")
    public static class PrimitivesFormDataParamResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String get(@FormDataParam("bP")
        final boolean bP, @FormDataParam("bT")
        final Boolean bT, @FormDataParam("bP_absent")
        final boolean bP_absent, @FormDataParam("bT_absent")
        final Boolean bT_absent, @DefaultValue("true")
        @FormDataParam("bP_absent_default")
        final boolean bP_absent_default, @DefaultValue("true")
        @FormDataParam("bT_absent_default")
        final Boolean bT_absent_default, @DefaultValue("true")
        @FormDataParam("bP_default")
        final boolean bP_default, @DefaultValue("true")
        @FormDataParam("bT_default")
        final Boolean bT_default) {
            Assert.assertTrue(bP);
            Assert.assertTrue(bT);
            Assert.assertFalse(bP_absent);
            Assert.assertNull(bT_absent);
            Assert.assertTrue(bP_absent_default);
            Assert.assertTrue(bT_absent_default);
            Assert.assertFalse(bP_default);
            Assert.assertFalse(bT_default);
            return "OK";
        }
    }

    @Test
    public void testPrimitivesFormDataParamResource() {
        final Invocation.Builder request = target().path("PrimitivesFormDataParamResource").request("text/plain");
        final FormDataMultiPart entity = new FormDataMultiPart().field("bP", "true").field("bT", "true").field("bP_default", "false").field("bT_default", "false");
        final String response = request.put(Entity.entity(entity, "multipart/form-data"), String.class);
        Assert.assertEquals("OK", response);
    }

    @Path("/DefaultFormDataParamResource")
    public static class DefaultFormDataParamResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String get(@FormDataParam("bean")
        final MultiPartBean bean, @FormDataParam("bean_absent")
        final MultiPartBean bean_absent, @DefaultValue("myname=myvalue")
        @FormDataParam("bean_default")
        final MultiPartBean bean_default) {
            Assert.assertNotNull(bean);
            Assert.assertNull(bean_absent);
            Assert.assertNull(bean_default);
            Assert.assertEquals("myname", bean.getName());
            Assert.assertEquals("myvalue", bean.getValue());
            return "OK";
        }
    }

    @Test
    public void testDefaultFormDataParamResource() {
        final Invocation.Builder request = target().path("DefaultFormDataParamResource").request("text/plain");
        final MultiPartBean bean = new MultiPartBean("myname", "myvalue");
        final FormDataMultiPart entity = new FormDataMultiPart().field("bean", bean, new MediaType("x-application", "x-format"));
        final String response = request.put(Entity.entity(entity, "multipart/form-data"), String.class);
        Assert.assertEquals("OK", response);
    }

    @Path("/NonContentTypeForPartResource")
    public static class NonContentTypeForPartResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String put(@FormDataParam("submit")
        final FormDataBodyPart bean) throws IOException {
            Assert.assertNotNull(bean);
            Assert.assertNull(bean.getHeaders().getFirst("Content-Type"));
            Assert.assertEquals("upload", bean.getValue());
            return "OK";
        }
    }

    @Test
    public void testNonContentTypeForPartResource() {
        final Invocation.Builder request = target().path("NonContentTypeForPartResource").request("text/plain");
        final String entity = "-----------------------------33219615019106944971719437488\n" + (("Content-Disposition: form-data; name=\"submit\"\n\n" + "upload\n") + "-----------------------------33219615019106944971719437488--");
        final String response = request.put(Entity.entity(entity, ("multipart/form-data;" + "boundary=\"---------------------------33219615019106944971719437488\"")), String.class);
        Assert.assertEquals("OK", response);
    }

    @Path("/MediaTypeWithBoundaryResource")
    public static class MediaTypeWithBoundaryResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String get(@Context
        final HttpHeaders h, @FormDataParam("submit")
        final String s) {
            final String b = h.getMediaType().getParameters().get("boundary");
            Assert.assertEquals("XXXX_YYYY", b);
            return s;
        }
    }

    @Test
    public void testMediaTypeWithBoundaryResource() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("boundary", "XXXX_YYYY");
        final MediaType mediaType = new MediaType(MULTIPART_FORM_DATA_TYPE.getType(), MULTIPART_FORM_DATA_TYPE.getSubtype(), parameters);
        final FormDataMultiPart entity = new FormDataMultiPart().field("submit", "OK");
        final Invocation.Builder request = target().path("MediaTypeWithBoundaryResource").request("text/plain");
        final String response = request.put(Entity.entity(entity, mediaType), String.class);
        Assert.assertEquals("OK", response);
    }

    @Test
    public void testMediaTypeWithQuotedBoundaryResource() throws Exception {
        final URL url = new URL(((getBaseUri().toString()) + "MediaTypeWithBoundaryResource"));
        final HttpURLConnection connection = ((HttpURLConnection) (url.openConnection()));
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", "text/plain");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=\"XXXX_YYYY\"");
        connection.setDoOutput(true);
        connection.connect();
        final OutputStream outputStream = connection.getOutputStream();
        outputStream.write("--XXXX_YYYY".getBytes());
        outputStream.write('\n');
        outputStream.write("Content-Type: text/plain".getBytes());
        outputStream.write('\n');
        outputStream.write("Content-Disposition: form-data; name=\"submit\"".getBytes());
        outputStream.write('\n');
        outputStream.write('\n');
        outputStream.write("OK".getBytes());
        outputStream.write('\n');
        outputStream.write("--XXXX_YYYY--".getBytes());
        outputStream.write('\n');
        outputStream.flush();
        Assert.assertEquals("OK", connection.getResponseMessage());
    }

    @Path("/FileResource")
    @Consumes("multipart/form-data")
    @Produces("text/plain")
    public static class FileResource {
        @POST
        @Path("InjectedFileNotCopied")
        public String injectedFileNotCopied(@FormDataParam("file")
        final File file) {
            final String path = file.getAbsolutePath();
            // noinspection ResultOfMethodCallIgnored
            file.delete();
            return path;
        }

        @POST
        @Path("ExceptionInMethod")
        public String exceptionInMethod(@FormDataParam("file")
        final File file) {
            throw new javax.ws.rs.WebApplicationException(Response.serverError().entity(file.getAbsolutePath()).build());
        }

        @POST
        @Path("SuccessfulMethod")
        public String successfulMethod(@FormDataParam("file")
        final File file) {
            return file.getAbsolutePath();
        }

        @POST
        @Path("FileSize")
        public long fileSize(@FormDataParam("file")
        final File file) {
            return file.length();
        }
    }

    /**
     * JERSEY-2846 reproducer. Make sure that temporary file created by MIMEPull deleted after a successful request.
     */
    @Test
    public void tempFileDeletedAfterSuccessfulProcessing() throws Exception {
        final FormDataMultiPart multipart = new FormDataMultiPart();
        final FormDataBodyPart bodypart = new FormDataBodyPart(FormDataContentDisposition.name("file").fileName("file").build(), "CONTENT");
        multipart.bodyPart(bodypart);
        final Response response = target().path("FileResource").path("SuccessfulMethod").request().post(Entity.entity(multipart, MULTIPART_FORM_DATA));
        // Make sure that the temp file has been removed.
        final String pathname = response.readEntity(String.class);
        // Wait a second to make sure the file doesn't exist.
        Thread.sleep(1000);
        Assert.assertThat((("Temporary file, " + pathname) + ", on the server has not been removed"), new File(pathname).exists(), CoreMatchers.is(false));
    }

    /**
     * JERSEY-2846 reproducer. Make sure that temporary file created by MIMEPull deleted after an unsuccessful request.
     */
    @Test
    public void tempFileDeletedAfterExceptionInMethod() throws Exception {
        final FormDataMultiPart multipart = new FormDataMultiPart();
        final FormDataBodyPart bodypart = new FormDataBodyPart(FormDataContentDisposition.name("file").fileName("file").build(), "CONTENT");
        multipart.bodyPart(bodypart);
        final Response response = target().path("FileResource").path("ExceptionInMethod").request().post(Entity.entity(multipart, MULTIPART_FORM_DATA));
        // Make sure that the temp file has been removed.
        final String pathname = response.readEntity(String.class);
        // Wait a second to make sure the file doesn't exist.
        Thread.sleep(1000);
        Assert.assertThat((("Temporary file, " + pathname) + ", on the server has not been removed"), new File(pathname).exists(), CoreMatchers.is(false));
    }

    /**
     * JERSEY-2862 reproducer. Make sure that mimepull is able to move it's temporary file to the one created by Jersey.
     * Reproducible only on Windows. Entity size has to be bigger than 8192 to make sure mimepull creates temporary file.
     */
    @Test
    public void testFileSize() throws Exception {
        final FormDataMultiPart multipart = new FormDataMultiPart();
        final byte[] content = new byte[2 * 8192];
        final FormDataBodyPart bodypart = new FormDataBodyPart(FormDataContentDisposition.name("file").fileName("file").build(), content, MediaType.TEXT_PLAIN_TYPE);
        multipart.bodyPart(bodypart);
        final Response response = target().path("FileResource").path("FileSize").request().post(Entity.entity(multipart, MULTIPART_FORM_DATA));
        Assert.assertThat("Temporary file has wrong size.", response.readEntity(int.class), CoreMatchers.is(content.length));
    }

    @Path("/InputStreamResource")
    public static class InputStreamResource {
        @PUT
        @Consumes("multipart/form-data")
        @Produces("text/plain")
        public String put(@FormDataParam("submit")
        final InputStream stream) {
            return "OK";
        }
    }
}

