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
package org.glassfish.jersey.tests.api;


import Variant.VariantListBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Server-side variant selection & handling test.
 *
 * @author Paul Sandoz
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class VariantsTest extends JerseyTest {
    @Path("/lvr")
    public static class LanguageVariantResource {
        @GET
        public Response doGet(@Context
        Request r) {
            List<Variant> vs = VariantListBuilder.newInstance().languages(new Locale("zh")).languages(new Locale("fr")).languages(new Locale("en")).add().build();
            Variant v = r.selectVariant(vs);
            if (v == null) {
                return Response.notAcceptable(vs).build();
            } else {
                return Response.ok(v.getLanguage().toString(), v).build();
            }
        }
    }

    @Test
    public void testGetLanguageEn() throws IOException {
        WebTarget rp = target("/lvr");
        Response r = rp.request().header("Accept-Language", "en").get();
        Assert.assertEquals("en", r.readEntity(String.class));
        Assert.assertEquals("en", r.getLanguage().toString());
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue((!(contains(vary, "Accept"))));
        Assert.assertTrue(contains(vary, "Accept-Language"));
    }

    @Test
    public void testGetLanguageZh() throws IOException {
        WebTarget rp = target("/lvr");
        Response r = rp.request().header("Accept-Language", "zh").get();
        Assert.assertEquals("zh", r.readEntity(String.class));
        Assert.assertEquals("zh", r.getLanguage().toString());
        System.out.println(r.getMetadata().getFirst("Vary"));
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue((!(contains(vary, "Accept"))));
        Assert.assertTrue(contains(vary, "Accept-Language"));
    }

    @Test
    public void testGetLanguageMultiple() throws IOException {
        WebTarget rp = target("/lvr");
        Response r = rp.request().header("Accept-Language", "en;q=0.3, zh;q=0.4, fr").get();
        Assert.assertEquals("fr", r.readEntity(String.class));
        Assert.assertEquals("fr", r.getLanguage().toString());
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue((!(contains(vary, "Accept"))));
        Assert.assertTrue(contains(vary, "Accept-Language"));
    }

    @Path("/cvr")
    public static class ComplexVariantResource {
        @GET
        public Response doGet(@Context
        Request r) {
            List<Variant> vs = VariantListBuilder.newInstance().mediaTypes(MediaType.valueOf("image/jpeg")).add().mediaTypes(MediaType.valueOf("application/xml")).languages(new Locale("en", "us")).add().mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en")).add().mediaTypes(MediaType.valueOf("text/xml")).languages(new Locale("en", "us")).add().build();
            Variant v = r.selectVariant(vs);
            if (v == null) {
                return Response.notAcceptable(vs).build();
            } else {
                return Response.ok("GET", v).build();
            }
        }
    }

    @Test
    public void testGetComplex1() throws IOException {
        WebTarget rp = target("/cvr");
        Response r = rp.request("text/xml", "application/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5").header("Accept-Language", "en-us,en;q=0.5").get();
        Assert.assertEquals("GET", r.readEntity(String.class));
        Assert.assertEquals(MediaType.valueOf("text/xml"), r.getMediaType());
        Assert.assertEquals("en", r.getLanguage().getLanguage());
        Assert.assertEquals("US", r.getLanguage().getCountry());
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        Assert.assertTrue(contains(vary, "Accept-Language"));
    }

    @Test
    public void testGetComplex2() throws IOException {
        WebTarget rp = target("/cvr");
        Response r = rp.request("text/xml", "application/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5").header("Accept-Language", "en,en-us").get();
        Assert.assertEquals("GET", r.readEntity(String.class));
        Assert.assertEquals(MediaType.valueOf("text/xml"), r.getMediaType());
        Assert.assertEquals("en", r.getLanguage().toString());
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        Assert.assertTrue(contains(vary, "Accept-Language"));
    }

    @Test
    public void testGetComplex3() throws IOException {
        WebTarget rp = target("/cvr");
        Response r = rp.request("application/xml", "text/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5").header("Accept-Language", "en-us,en;q=0.5").get();
        Assert.assertEquals("GET", r.readEntity(String.class));
        Assert.assertEquals(MediaType.valueOf("application/xml"), r.getMediaType());
        Assert.assertEquals("en", r.getLanguage().getLanguage());
        Assert.assertEquals("US", r.getLanguage().getCountry());
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        Assert.assertTrue(contains(vary, "Accept-Language"));
    }

    @Test
    public void testGetComplexNotAcceptable() throws IOException {
        WebTarget rp = target("/cvr");
        Response r = rp.request("application/atom+xml").header("Accept-Language", "en-us,en").get();
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        Assert.assertTrue(contains(vary, "Accept-Language"));
        Assert.assertEquals(406, r.getStatus());
        r = rp.request("application/xml").header("Accept-Language", "fr").get();
        vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        Assert.assertTrue(contains(vary, "Accept-Language"));
        Assert.assertEquals(406, r.getStatus());
    }

    @Path("/mtqsr")
    public static class MediaTypeQualitySourceResource {
        @GET
        public Response doGet(@Context
        Request r) {
            List<Variant> vs = VariantListBuilder.newInstance().mediaTypes(MediaType.valueOf("application/xml;qs=0.8")).mediaTypes(MediaType.valueOf("text/html;qs=1.0")).add().build();
            Variant v = r.selectVariant(vs);
            if (v == null) {
                return Response.notAcceptable(vs).build();
            } else {
                return Response.ok("GET", v).build();
            }
        }
    }

    @Test
    public void testMediaTypeQualitySource() throws IOException {
        WebTarget rp = target("/mtqsr");
        Response r = rp.request("application/xml", "text/html;q=0.9").get();
        Assert.assertTrue(MediaTypes.typeEqual(MediaType.valueOf("text/html"), r.getMediaType()));
        String vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        r = rp.request("text/html", "application/xml").get();
        Assert.assertTrue(MediaTypes.typeEqual(MediaType.valueOf("text/html"), r.getMediaType()));
        vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        r = rp.request("application/xml", "text/html").get();
        Assert.assertTrue(MediaTypes.typeEqual(MediaType.valueOf("text/html"), r.getMediaType()));
        vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
        r = rp.request("application/xml").get();
        Assert.assertTrue(MediaTypes.typeEqual(MediaType.valueOf("application/xml"), r.getMediaType()));
        vary = r.getHeaderString("Vary");
        Assert.assertNotNull(vary);
        Assert.assertTrue(contains(vary, "Accept"));
    }
}

