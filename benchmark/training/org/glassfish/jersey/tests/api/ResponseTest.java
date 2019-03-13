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
package org.glassfish.jersey.tests.api;


import HttpHeaders.ALLOW;
import HttpHeaders.VARY;
import MediaType.TEXT_PLAIN_TYPE;
import Response.ResponseBuilder;
import Variant.VariantListBuilder;
import java.security.AccessController;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class ResponseTest {
    /* Create an instance of Response using Response.ok(String, Variant).build()
    verify that correct status code is returned
     */
    @Test
    public void OkTest5() {
        Response resp;
        int status = 200;
        String content = "Test Only";
        List<String> encoding = Arrays.asList("gzip", "compress");
        List<String> lang = Arrays.asList("en-US", "en-GB", "zh-CN");
        MediaType mt = new MediaType("text", "plain");
        List<Variant> vts = VariantListBuilder.newInstance().mediaTypes(mt).languages(new Locale("en", "US"), new Locale("en", "GB"), new Locale("zh", "CN")).encodings(((String[]) (encoding.toArray()))).add().build();
        String tmp;
        for (Variant vt : vts) {
            resp = Response.ok(content, vt).build();
            tmp = verifyResponse(resp, content, status, encoding, lang, null, null, null, null);
            if (tmp.endsWith("false")) {
                System.out.println(("### " + tmp));
                Assert.fail();
            }
        }
    }

    /* Create an instance of Response using
    Response.ResponseBuilder.clone()
    verify that correct status code is returned
     */
    @Test
    public void cloneTest() throws CloneNotSupportedException {
        StringBuilder sb = new StringBuilder();
        int status = 200;
        List<String> type = Arrays.asList("text/plain", "text/html");
        List<String> encoding = Arrays.asList("gzip", "compress");
        List<String> lang = Arrays.asList("en-US", "en-GB", "zh-CN");
        String name = "name_1";
        String value = "value_1";
        Cookie ck1 = new Cookie(name, value);
        NewCookie nck1 = new NewCookie(ck1);
        List<String> cookies = Arrays.asList(nck1.toString().toLowerCase());
        Response.ResponseBuilder respb1 = Response.status(status).header("Content-type", "text/plain").header("Content-type", "text/html").header("Content-Language", "en-US").header("Content-Language", "en-GB").header("Content-Language", "zh-CN").header("Cache-Control", "no-transform").header("Set-Cookie", "name_1=value_1;version=1");
        Response.ResponseBuilder respb2 = respb1.clone();
        Response resp2 = respb2.build();
        String tmp = verifyResponse(resp2, null, status, encoding, lang, type, null, null, cookies);
        if (tmp.endsWith("false")) {
            System.out.println(("### " + (sb.toString())));
            Assert.fail();
        }
        sb.append(tmp).append(ResponseTest.newline);
        String content = "TestOnly";
        Response resp1 = respb1.entity(content).cookie(((NewCookie) (null))).build();
        tmp = verifyResponse(resp1, content, status, encoding, lang, type, null, null, null);
        if (tmp.endsWith("false")) {
            System.out.println(("### " + (sb.toString())));
            Assert.fail();
        }
        MultivaluedMap<String, Object> mvp = resp1.getMetadata();
        if (mvp.containsKey("Set-Cookie")) {
            sb.append("Response contains unexpected Set-Cookie: ").append(mvp.getFirst("Set-Cookie").toString()).append(ResponseTest.newline);
            System.out.println(("### " + (sb.toString())));
            Assert.fail();
        }
        sb.append(tmp).append(ResponseTest.newline);
    }

    /* Create an instance of Response using
    Response.fromResponse(Response).build()
    verify that correct status code is returned
     */
    @Test
    public void fromResponseTest() {
        int status = 200;
        String content = "Test Only";
        List<String> type = Arrays.asList("text/plain", "text/html");
        List<String> encoding = Arrays.asList("gzip", "compress");
        List<String> lang = Arrays.asList("en-US", "en-GB", "zh-CN");
        MediaType mt1 = new MediaType("text", "plain");
        MediaType mt2 = new MediaType("text", "html");
        List<Variant> vts = VariantListBuilder.newInstance().mediaTypes(mt1, mt2).languages(new Locale("en", "US"), new Locale("en", "GB"), new Locale("zh", "CN")).encodings(((String[]) (encoding.toArray()))).add().build();
        String tmp;
        for (Variant vt : vts) {
            Response resp1 = Response.ok(content, vt).build();
            Response resp = Response.fromResponse(resp1).build();
            tmp = verifyResponse(resp, content, status, encoding, lang, type, null, null, null);
            if (tmp.endsWith("false")) {
                System.out.println(("### " + tmp));
                Assert.fail();
            }
        }
    }

    /* Create an instance of Response using
    Response.ResponseBuilder.header(String, Object).build()
    verify that correct status code is returned
     */
    @Test
    public void headerTest() {
        int status = 200;
        List<String> type = Arrays.asList("text/plain", "text/html");
        List<String> encoding = Arrays.asList("gzip", "compress");
        List<String> lang = Arrays.asList("en-US", "en-GB", "zh-CN");
        String name = "name_1";
        String value = "value_1";
        Cookie ck1 = new Cookie(name, value);
        NewCookie nck1 = new NewCookie(ck1);
        List<String> cookies = Arrays.asList(nck1.toString().toLowerCase());
        Response resp = Response.status(status).header("Content-type", "text/plain").header("Content-type", "text/html").header("Content-Language", "en-US").header("Content-Language", "en-GB").header("Content-Language", "zh-CN").header("Cache-Control", "no-transform").header("Set-Cookie", "name_1=value_1;version=1").build();
        String tmp = verifyResponse(resp, null, status, encoding, lang, type, null, null, cookies);
        if (tmp.endsWith("false")) {
            System.out.println(("### " + tmp));
            Assert.fail();
        }
    }

    /* Create an instance of Response using
    Response.status(int).variant(Variant).build()
    verify that correct status code is returned
     */
    @Test
    public void variantTest() {
        Response resp;
        int status = 200;
        List<String> encoding = Arrays.asList("gzip", "compress");
        List<String> lang = Arrays.asList("en-US", "en-GB", "zh-CN");
        MediaType mt = new MediaType("text", "plain");
        List<Variant> vts = VariantListBuilder.newInstance().mediaTypes(mt).languages(new Locale("en", "US"), new Locale("en", "GB"), new Locale("zh", "CN")).encodings(((String[]) (encoding.toArray()))).add().build();
        String tmp;
        for (Variant vt : vts) {
            resp = Response.status(status).variant(vt).build();
            tmp = verifyResponse(resp, null, status, encoding, lang, null, null, null, null);
            if (tmp.endsWith("false")) {
                System.out.println(("### " + tmp));
                Assert.fail();
            }
        }
    }

    private static final String indent = "    ";

    private static final String newline = AccessController.doPrivileged(PropertiesHelper.getSystemProperty("line.separator"));

    @Test
    public void testAllowString() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder = responseBuilder.allow("GET");
        Assert.assertTrue(responseBuilder.build().getHeaderString(ALLOW).contains("GET"));
        responseBuilder = responseBuilder.allow(((String) (null)));
        Assert.assertTrue(((responseBuilder.build().getHeaderString(ALLOW)) == null));
    }

    @Test
    public void testAllowSet() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder = responseBuilder.allow(new HashSet(Arrays.asList("GET")));
        Assert.assertTrue(responseBuilder.build().getHeaderString(ALLOW).contains("GET"));
        responseBuilder = responseBuilder.allow(((Set<String>) (null)));
        Assert.assertEquals(null, responseBuilder.build().getHeaderString(ALLOW));
    }

    @Test
    public void testAllowVariant() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder = responseBuilder.allow(new HashSet(Arrays.asList("GET")));
        Assert.assertTrue(responseBuilder.build().getHeaderString(ALLOW).contains("GET"));
        responseBuilder = responseBuilder.allow(((String[]) (null)));
        Assert.assertEquals(null, responseBuilder.build().getHeaderString(ALLOW));
    }

    @Test
    public void bufferEntityTest() {
        Response response = Response.ok().build();
        response.close();
        try {
            response.bufferEntity();
            Assert.fail("IllegalStateException expected when reading entity after response has been closed.");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void getEntityTest() {
        Response response = Response.ok().build();
        response.close();
        try {
            response.getEntity();
            Assert.fail("IllegalStateException expected when reading entity after response has been closed.");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void hasEntityTest() {
        Response response = Response.ok().build();
        response.close();
        try {
            response.hasEntity();
            Assert.fail("IllegalStateException expected when reading entity after response has been closed.");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    // Reproducer for JERSEY-1553
    @Test
    public void testVariants() {
        List<String> encoding = Arrays.asList("gzip", "compress");
        List<Variant> list = VariantListBuilder.newInstance().mediaTypes(TEXT_PLAIN_TYPE).languages(new Locale("en", "US"), new Locale("en", "GB")).encodings(encoding.toArray(new String[encoding.size()])).add().build();
        final Response r1 = Response.ok().variants(list).build();
        Assert.assertNotNull(r1);
        Assert.assertNotNull(r1.getHeaderString(VARY));
        final Response r2 = Response.ok().variants(list.toArray(new Variant[list.size()])).build();
        Assert.assertNotNull(r2);
        Assert.assertNotNull(r2.getHeaderString(VARY));
    }
}

