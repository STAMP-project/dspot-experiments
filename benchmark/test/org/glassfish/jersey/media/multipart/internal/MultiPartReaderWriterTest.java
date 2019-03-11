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


import MediaType.APPLICATION_OCTET_STREAM_TYPE;
import MediaType.TEXT_PLAIN_TYPE;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link org.glassfish.jersey.media.multipart.internal.MultiPartReaderClientSide} (in the client) and
 * {@link org.glassfish.jersey.media.multipart.internal.MultiPartWriter} (in the server)
 * .
 */
public class MultiPartReaderWriterTest extends MultiPartJerseyTest {
    private static Path TMP_DIRECTORY;

    private static String ORIGINAL_TMP_DIRECTORY;

    @Test
    public void testZero() {
        final WebTarget target = target().path("multipart").path("zero");
        final String result = target.request("text/plain").get(String.class);
        Assert.assertEquals("Hello, world\r\n", result);
    }

    @Test
    public void testOne() {
        final WebTarget target = target().path("multipart/one");
        try {
            final MultiPart result = target.request("multipart/mixed").get(MultiPart.class);
            checkMediaType(new MediaType("multipart", "mixed"), result.getMediaType());
            Assert.assertEquals(1, result.getBodyParts().size());
            final BodyPart part = result.getBodyParts().get(0);
            checkMediaType(new MediaType("text", "plain"), part.getMediaType());
            checkEntity("This is the only segment", ((BodyPartEntity) (part.getEntity())));
            result.getParameterizedHeaders();
            result.cleanup();
        } catch (IOException | ParseException e) {
            e.printStackTrace(System.out);
            Assert.fail(("Caught exception: " + e));
        }
    }

    @Test
    public void testETag() {
        final WebTarget target = target().path("multipart/etag");
        try {
            final MultiPart result = target.request("multipart/mixed").get(MultiPart.class);
            checkMediaType(new MediaType("multipart", "mixed"), result.getMediaType());
            Assert.assertEquals(1, result.getBodyParts().size());
            final BodyPart part = result.getBodyParts().get(0);
            checkMediaType(new MediaType("text", "plain"), part.getMediaType());
            checkEntity("This is the only segment", ((BodyPartEntity) (part.getEntity())));
            Assert.assertEquals("\"value\"", part.getHeaders().getFirst("ETag"));
            result.getParameterizedHeaders();
            result.cleanup();
        } catch (IOException | ParseException e) {
            e.printStackTrace(System.out);
            Assert.fail(("Caught exception: " + e));
        }
    }

    @Test
    public void testTwo() {
        final WebTarget target = target().path("multipart/two");
        try {
            final MultiPart result = target.request("multipart/mixed").get(MultiPart.class);
            checkMediaType(new MediaType("multipart", "mixed"), result.getMediaType());
            Assert.assertEquals(2, result.getBodyParts().size());
            final BodyPart part1 = result.getBodyParts().get(0);
            checkMediaType(new MediaType("text", "plain"), part1.getMediaType());
            checkEntity("This is the first segment", ((BodyPartEntity) (part1.getEntity())));
            final BodyPart part2 = result.getBodyParts().get(1);
            checkMediaType(new MediaType("text", "xml"), part2.getMediaType());
            checkEntity("<outer><inner>value</inner></outer>", ((BodyPartEntity) (part2.getEntity())));
            result.getParameterizedHeaders();
            result.cleanup();
        } catch (IOException | ParseException e) {
            e.printStackTrace(System.out);
            Assert.fail(("Caught exception: " + e));
        }
    }

    @Test
    public void testThree() {
        final WebTarget target = target().path("multipart/three");
        try {
            final MultiPart result = target.request("multipart/mixed").get(MultiPart.class);
            checkMediaType(new MediaType("multipart", "mixed"), result.getMediaType());
            Assert.assertEquals(2, result.getBodyParts().size());
            final BodyPart part1 = result.getBodyParts().get(0);
            checkMediaType(new MediaType("text", "plain"), part1.getMediaType());
            checkEntity("This is the first segment", ((BodyPartEntity) (part1.getEntity())));
            final BodyPart part2 = result.getBodyParts().get(1);
            checkMediaType(new MediaType("x-application", "x-format"), part2.getMediaType());
            final MultiPartBean entity = part2.getEntityAs(MultiPartBean.class);
            Assert.assertEquals("myname", entity.getName());
            Assert.assertEquals("myvalue", entity.getValue());
            result.getParameterizedHeaders();
            result.cleanup();
        } catch (IOException | ParseException e) {
            e.printStackTrace(System.out);
            Assert.fail(("Caught exception: " + e));
        }
    }

    @Test
    public void testFour() {
        final WebTarget target = target().path("multipart/four");
        final MultiPartBean bean = new MultiPartBean("myname", "myvalue");
        final MultiPart entity = new MultiPart().bodyPart("This is the first segment", new MediaType("text", "plain")).bodyPart(bean, new MediaType("x-application", "x-format"));
        final String response = target.request("text/plain").put(Entity.entity(entity, "multipart/mixed"), String.class);
        if (!(response.startsWith("SUCCESS:"))) {
            Assert.fail((("Response is '" + response) + "'"));
        }
    }

    @Test
    public void testFourBiz() {
        final WebTarget target = target().path("multipart/four");
        final MultiPartBean bean = new MultiPartBean("myname", "myvalue");
        final MultiPart entity = new MultiPart().bodyPart("This is the first segment", new MediaType("text", "plain")).bodyPart(bean, new MediaType("x-application", "x-format"));
        final String response = target.request("text/plain").header("Content-Type", "multipart/mixed").put(Entity.entity(entity, "multipart/mixed"), String.class);
        if (!(response.startsWith("SUCCESS:"))) {
            Assert.fail((("Response is '" + response) + "'"));
        }
    }

    /**
     * Test sending a completely empty MultiPart.
     */
    @Test(expected = ProcessingException.class)
    public void testSix() {
        target().path("multipart/six").request("text/plain").post(Entity.entity(new MultiPart(), "multipart/mixed"), String.class);
        Assert.fail("Should have thrown an exception about zero body parts");
    }

    /**
     * Zero length body part.
     */
    @Test
    public void testTen() {
        final WebTarget target = target().path("multipart/ten");
        final MultiPartBean bean = new MultiPartBean("myname", "myvalue");
        final MultiPart entity = new MultiPart().bodyPart(bean, new MediaType("x-application", "x-format")).bodyPart("", APPLICATION_OCTET_STREAM_TYPE);
        final String response = target.request("text/plain").put(Entity.entity(entity, "multipart/mixed"), String.class);
        if (!(response.startsWith("SUCCESS:"))) {
            Assert.fail((("Response is '" + response) + "'"));
        }
    }

    /**
     * Echo back various sized body part entities, and check size/content
     */
    @Test
    public void testEleven() throws Exception {
        final String seed = "0123456789ABCDEF";
        checkEleven(seed, 0);
        checkEleven(seed, 1);
        checkEleven(seed, 10);
        checkEleven(seed, 100);
        checkEleven(seed, 1000);
        checkEleven(seed, 10000);
        checkEleven(seed, 100000);
    }

    /**
     * Echo back the multipart that was sent.
     */
    @Test
    public void testTwelve() throws Exception {
        final WebTarget target = target().path("multipart/twelve");
        final MultiPart entity = new MultiPart().bodyPart("CONTENT", TEXT_PLAIN_TYPE);
        final MultiPart response = target.request("multipart/mixed").put(Entity.entity(entity, "multipart/mixed"), MultiPart.class);
        final String actual = response.getBodyParts().get(0).getEntityAs(String.class);
        Assert.assertEquals("CONTENT", actual);
        response.cleanup();
    }

    /**
     * Call clean up explicitly.
     */
    @Test
    public void testThirteen() {
        final WebTarget target = target().path("multipart/thirteen");
        final MultiPart entity = new MultiPart().bodyPart("CONTENT", TEXT_PLAIN_TYPE);
        final String response = target.request("multipart/mixed").put(Entity.entity(entity, "multipart/mixed"), String.class);
        Assert.assertEquals("cleanup", response);
    }

    /**
     * Test for JERSEY-2515 - Jersey should not leave any temporary files after verifying that it is possible
     * to create files in java.io.tmpdir.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    public void shouldNotBeAnyTemporaryFiles() {
        // Make sure the MBR is initialized on the client-side as well.
        target().request().get();
        Assert.assertEquals(0, MultiPartReaderWriterTest.TMP_DIRECTORY.toFile().listFiles().length);
    }
}

