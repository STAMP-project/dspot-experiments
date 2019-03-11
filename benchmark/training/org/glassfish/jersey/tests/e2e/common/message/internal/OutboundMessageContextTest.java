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
package org.glassfish.jersey.tests.e2e.common.message.internal;


import HttpHeaders.ACCEPT;
import HttpHeaders.ACCEPT_LANGUAGE;
import HttpHeaders.COOKIE;
import HttpHeaders.DATE;
import HttpHeaders.ETAG;
import HttpHeaders.LAST_MODIFIED;
import HttpHeaders.LOCATION;
import HttpHeaders.SET_COOKIE;
import MediaType.APPLICATION_JSON_TYPE;
import MediaType.APPLICATION_XML_TYPE;
import MediaType.TEXT_PLAIN_TYPE;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;
import org.glassfish.jersey.message.internal.CookieProvider;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.glassfish.jersey.tests.e2e.common.TestRuntimeDelegate;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link OutboundMessageContext} test.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class OutboundMessageContextTest {
    public OutboundMessageContextTest() {
        RuntimeDelegate.setInstance(new TestRuntimeDelegate());
    }

    @Test
    public void testAcceptableMediaTypes() throws URISyntaxException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(ACCEPT, "application/xml, text/plain");
        r.getHeaders().add(ACCEPT, "application/json");
        final List<MediaType> acceptableMediaTypes = r.getAcceptableMediaTypes();
        Assert.assertThat(acceptableMediaTypes.size(), Matchers.equalTo(3));
        Assert.assertThat(acceptableMediaTypes, Matchers.contains(APPLICATION_XML_TYPE, TEXT_PLAIN_TYPE, APPLICATION_JSON_TYPE));
    }

    @Test
    public void testAcceptableLanguages() throws URISyntaxException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(ACCEPT_LANGUAGE, "en-gb;q=0.8, en;q=0.7");
        r.getHeaders().add(ACCEPT_LANGUAGE, "de");
        Assert.assertEquals(r.getAcceptableLanguages().size(), 3);
        Assert.assertTrue(r.getAcceptableLanguages().contains(Locale.UK));
        Assert.assertTrue(r.getAcceptableLanguages().contains(Locale.ENGLISH));
        Assert.assertTrue(r.getAcceptableLanguages().contains(Locale.GERMAN));
    }

    @Test
    public void testRequestCookies() throws URISyntaxException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(COOKIE, "oreo=chocolate");
        r.getHeaders().add(COOKIE, "nilla=vanilla");
        Assert.assertEquals(r.getRequestCookies().size(), 2);
        Assert.assertTrue(r.getRequestCookies().containsKey("oreo"));
        Assert.assertTrue(r.getRequestCookies().containsKey("nilla"));
        CookieProvider cp = new CookieProvider();
        Assert.assertTrue(r.getRequestCookies().containsValue(cp.fromString("oreo=chocolate")));
        Assert.assertTrue(r.getRequestCookies().containsValue(cp.fromString("nilla=vanilla")));
    }

    @Test
    public void testDate() throws URISyntaxException, ParseException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(DATE, "Tue, 29 Jan 2002 22:14:02 -0500");
        SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        Date date = f.parse("Tue, 29 Jan 2002 22:14:02 -0500");
        Assert.assertEquals(r.getDate(), date);
    }

    @Test
    public void testHeader() throws URISyntaxException, ParseException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(ACCEPT, "application/xml, text/plain");
        r.getHeaders().add(ACCEPT, "application/json");
        r.getHeaders().add("FOO", "");
        Assert.assertTrue(r.getHeaderString(ACCEPT).contains("application/xml"));
        Assert.assertTrue(r.getHeaderString(ACCEPT).contains("text/plain"));
        Assert.assertTrue(r.getHeaderString(ACCEPT).contains("application/json"));
        Assert.assertEquals(r.getHeaderString("FOO").length(), 0);
        Assert.assertNull(r.getHeaderString("BAR"));
    }

    @Test
    public void testHeaderMap() throws URISyntaxException, ParseException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(ACCEPT, "application/xml, text/plain");
        r.getHeaders().add(ACCEPT, "application/json");
        r.getHeaders().add("Allow", "GET, PUT");
        r.getHeaders().add("Allow", "POST");
        Assert.assertTrue(r.getHeaders().containsKey(ACCEPT));
        Assert.assertTrue(r.getHeaders().containsKey("Allow"));
        Assert.assertTrue(r.getHeaders().get(ACCEPT).contains("application/json"));
        Assert.assertTrue(r.getHeaders().get("Allow").contains("POST"));
    }

    @Test
    public void testAllowedMethods() throws URISyntaxException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add("Allow", "GET, PUT");
        r.getHeaders().add("Allow", "POST");
        Assert.assertEquals(3, r.getAllowedMethods().size());
        Assert.assertTrue(r.getAllowedMethods().contains("GET"));
        Assert.assertTrue(r.getAllowedMethods().contains("PUT"));
        Assert.assertTrue(r.getAllowedMethods().contains("POST"));
        Assert.assertFalse(r.getAllowedMethods().contains("DELETE"));
    }

    @Test
    public void testResponseCookies() throws URISyntaxException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(SET_COOKIE, "oreo=chocolate");
        r.getHeaders().add(SET_COOKIE, "nilla=vanilla");
        Assert.assertEquals(2, r.getResponseCookies().size());
        Assert.assertTrue(r.getResponseCookies().containsKey("oreo"));
        Assert.assertTrue(r.getResponseCookies().containsKey("nilla"));
    }

    @Test
    public void testEntityTag() throws URISyntaxException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(ETAG, "\"tag\"");
        Assert.assertEquals(EntityTag.valueOf("\"tag\""), r.getEntityTag());
    }

    @Test
    public void testLastModified() throws URISyntaxException, ParseException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(LAST_MODIFIED, "Tue, 29 Jan 2002 22:14:02 -0500");
        SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        Date date = f.parse("Tue, 29 Jan 2002 22:14:02 -0500");
        Assert.assertEquals(date, r.getLastModified());
    }

    @Test
    public void testLocation() throws URISyntaxException {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add(LOCATION, "http://example.org/app");
        Assert.assertEquals(URI.create("http://example.org/app"), r.getLocation());
    }

    @Test
    public void testGetLinks() {
        OutboundMessageContext r = new OutboundMessageContext();
        Link link1 = Link.fromUri("http://example.org/app/link1").param("produces", "application/json").param("method", "GET").rel("self").build();
        Link link2 = Link.fromUri("http://example.org/app/link2").param("produces", "application/xml").param("method", "PUT").rel("self").build();
        r.getHeaders().add("Link", link1.toString());
        r.getHeaders().add("Link", link2.toString());
        Assert.assertEquals(2, r.getLinks().size());
        Assert.assertTrue(r.getLinks().contains(link1));
        Assert.assertTrue(r.getLinks().contains(link2));
    }

    @Test
    public void testGetLink() {
        OutboundMessageContext r = new OutboundMessageContext();
        Link link1 = Link.fromUri("http://example.org/app/link1").param("produces", "application/json").param("method", "GET").rel("self").build();
        Link link2 = Link.fromUri("http://example.org/app/link2").param("produces", "application/xml").param("method", "PUT").rel("update").build();
        Link link3 = Link.fromUri("http://example.org/app/link2").param("produces", "application/xml").param("method", "POST").rel("update").build();
        r.getHeaders().add("Link", link1.toString());
        r.getHeaders().add("Link", link2.toString());
        r.getHeaders().add("Link", link3.toString());
        Assert.assertTrue(r.getLink("self").equals(link1));
        Assert.assertTrue(((r.getLink("update").equals(link2)) || (r.getLink("update").equals(link3))));
    }

    @Test
    public void testGetLength() {
        OutboundMessageContext r = new OutboundMessageContext();
        r.getHeaders().add("Content-Length", 50);
        Assert.assertEquals(50, r.getLengthLong());
    }

    @Test
    public void testGetLength_tooLongForInt() {
        OutboundMessageContext r = new OutboundMessageContext();
        long length = (Integer.MAX_VALUE) + 5L;
        r.getHeaders().add("Content-Length", length);
        Assert.assertEquals(length, r.getLengthLong());
        // value is not a valid integer -> ProcessingException is thrown.
        try {
            r.getLength();
        } catch (ProcessingException e) {
            return;
        }
        Assert.fail();
    }
}

