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
import HttpHeaders.ALLOW;
import HttpHeaders.COOKIE;
import HttpHeaders.DATE;
import HttpHeaders.ETAG;
import HttpHeaders.LAST_MODIFIED;
import HttpHeaders.LOCATION;
import HttpHeaders.SET_COOKIE;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.ext.RuntimeDelegate;
import org.glassfish.jersey.message.internal.CookieProvider;
import org.glassfish.jersey.message.internal.InboundMessageContext;
import org.glassfish.jersey.tests.e2e.common.TestRuntimeDelegate;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link org.glassfish.jersey.message.internal.InboundMessageContext} test.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class InboundMessageContextTest {
    public InboundMessageContextTest() {
        RuntimeDelegate.setInstance(new TestRuntimeDelegate());
    }

    @Test
    public void testNoLength() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        Assert.assertEquals((-1), r.getLength());
    }

    @Test
    public void testRequestCookies() throws URISyntaxException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(COOKIE, "oreo=chocolate");
        r.header(COOKIE, "nilla=vanilla");
        Assert.assertEquals(r.getRequestCookies().size(), 2);
        Assert.assertTrue(r.getRequestCookies().containsKey("oreo"));
        Assert.assertTrue(r.getRequestCookies().containsKey("nilla"));
        CookieProvider cp = new CookieProvider();
        Assert.assertTrue(r.getRequestCookies().containsValue(cp.fromString("oreo=chocolate")));
        Assert.assertTrue(r.getRequestCookies().containsValue(cp.fromString("nilla=vanilla")));
    }

    @Test
    public void testDate() throws URISyntaxException, ParseException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(DATE, "Tue, 29 Jan 2002 22:14:02 -0500");
        SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        Date date = f.parse("Tue, 29 Jan 2002 22:14:02 -0500");
        Assert.assertEquals(r.getDate(), date);
    }

    @Test
    public void testHeader() throws URISyntaxException, ParseException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(ACCEPT, "application/xml, text/plain");
        r.header(ACCEPT, "application/json");
        r.header("FOO", "");
        Assert.assertTrue(r.getHeaderString(ACCEPT).contains("application/xml"));
        Assert.assertTrue(r.getHeaderString(ACCEPT).contains("text/plain"));
        Assert.assertTrue(r.getHeaderString(ACCEPT).contains("application/json"));
        Assert.assertEquals(r.getHeaderString("FOO").length(), 0);
        Assert.assertNull(r.getHeaderString("BAR"));
    }

    @Test
    public void testHeaderMap() throws URISyntaxException, ParseException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(ACCEPT, "application/xml, text/plain");
        r.header(ACCEPT, "application/json");
        r.header("Allow", "GET, PUT");
        r.header("Allow", "POST");
        Assert.assertTrue(r.getHeaders().containsKey(ACCEPT));
        Assert.assertTrue(r.getHeaders().containsKey("Allow"));
        Assert.assertTrue(r.getHeaders().get(ACCEPT).contains("application/json"));
        Assert.assertTrue(r.getHeaders().get("Allow").contains("POST"));
    }

    @Test
    public void testAllowedMethods() throws URISyntaxException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header("Allow", "GET, PUT");
        r.header("Allow", "POST");
        Assert.assertEquals(3, r.getAllowedMethods().size());
        Assert.assertTrue(r.getAllowedMethods().contains("GET"));
        Assert.assertTrue(r.getAllowedMethods().contains("PUT"));
        Assert.assertTrue(r.getAllowedMethods().contains("POST"));
        Assert.assertFalse(r.getAllowedMethods().contains("DELETE"));
    }

    @Test
    public void testResponseCookies() throws URISyntaxException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(SET_COOKIE, "oreo=chocolate");
        r.header(SET_COOKIE, "nilla=vanilla");
        Assert.assertEquals(2, r.getResponseCookies().size());
        Assert.assertTrue(r.getResponseCookies().containsKey("oreo"));
        Assert.assertTrue(r.getResponseCookies().containsKey("nilla"));
    }

    @Test
    public void testEntityTag() throws URISyntaxException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(ETAG, "\"tag\"");
        Assert.assertEquals(EntityTag.valueOf("\"tag\""), r.getEntityTag());
    }

    @Test
    public void testLastModified() throws URISyntaxException, ParseException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(LAST_MODIFIED, "Tue, 29 Jan 2002 22:14:02 -0500");
        SimpleDateFormat f = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        Date date = f.parse("Tue, 29 Jan 2002 22:14:02 -0500");
        Assert.assertEquals(date, r.getLastModified());
    }

    @Test
    public void testLocation() throws URISyntaxException {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(LOCATION, "http://example.org/app");
        Assert.assertEquals(URI.create("http://example.org/app"), r.getLocation());
    }

    @Test
    public void testGetLinks() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        Link link1 = Link.fromUri("http://example.org/app/link1").param("produces", "application/json").param("method", "GET").rel("self").build();
        Link link2 = Link.fromUri("http://example.org/app/link2").param("produces", "application/xml").param("method", "PUT").rel("self").build();
        r.header("Link", link1.toString());
        r.header("Link", link2.toString());
        Assert.assertEquals(2, r.getLinks().size());
        Assert.assertTrue(r.getLinks().contains(link1));
        Assert.assertTrue(r.getLinks().contains(link2));
    }

    @Test
    public void testGetLinksWithCommaInUri() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        Link link1 = Link.fromUri("http://example.org/app/foo,bar").param("produces", "application/json").param("method", "GET").rel("self").build();
        r.header("Link", link1.toString());
        Assert.assertEquals(1, r.getLinks().size());
        Assert.assertTrue(r.getLinks().contains(link1));
    }

    @Test
    public void testGetLinksWithMultipleLinksInOneHeaderAndCommaInUri() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        Link link1 = Link.fromUri("https://example.org/one/api/groups?foo,page=2").rel("next").build();
        Link link2 = Link.fromUri("https://example.org/one/api/groups?bar,page=39").rel("last").build();
        r.header("Link", "<https://example.org/one/api/groups?foo,page=2>; rel=\"next\", <https://example.org/one/api/groups?bar,page=39>; rel=\"last\"");
        Assert.assertEquals(2, r.getLinks().size());
        Assert.assertTrue(r.getLinks().contains(link1));
        Assert.assertTrue(r.getLinks().contains(link2));
    }

    @Test
    public void testGetLinksWithMultipleLinksInOneHeader() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        Link link1 = Link.fromUri("https://example.org/one/api/groups?page=2").rel("next").build();
        Link link2 = Link.fromUri("https://example.org/one/api/groups?page=39").rel("last").build();
        r.header("Link", "<https://example.org/one/api/groups?page=2>; rel=\"next\", <https://example.org/one/api/groups?page=39>; rel=\"last\"");
        Assert.assertEquals(2, r.getLinks().size());
        Assert.assertTrue(r.getLinks().contains(link1));
        Assert.assertTrue(r.getLinks().contains(link2));
    }

    @Test
    public void testGetLinksWithMultipleLinksInOneHeaderWithLtInValue() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        Link link1 = Link.fromUri("https://example.org/one/api/groups?page=2").rel("next").param("foo", "<bar>").build();
        Link link2 = Link.fromUri("https://example.org/one/api/groups?page=39").rel("last").param("bar", "<<foo").build();
        r.header("Link", "<https://example.org/one/api/groups?page=2>; rel=\"next\"; foo=\"<bar>\", <https://example.org/one/api/groups?page=39>; rel=\"last\"; bar=\"<<foo\"");
        Assert.assertEquals(2, r.getLinks().size());
        Assert.assertTrue(r.getLinks().contains(link1));
        Assert.assertTrue(r.getLinks().contains(link2));
    }

    @Test
    public void testGetLink() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        Link link1 = Link.fromUri("http://example.org/app/link1").param("produces", "application/json").param("method", "GET").rel("self").build();
        Link link2 = Link.fromUri("http://example.org/app/link2").param("produces", "application/xml").param("method", "PUT").rel("update").build();
        Link link3 = Link.fromUri("http://example.org/app/link2").param("produces", "application/xml").param("method", "POST").rel("update").build();
        r.header("Link", link1.toString());
        r.header("Link", link2.toString());
        r.header("Link", link3.toString());
        Assert.assertTrue(r.getLink("self").equals(link1));
        Assert.assertTrue(((r.getLink("update").equals(link2)) || (r.getLink("update").equals(link3))));
    }

    @Test
    public void testGetAllowedMethods() {
        InboundMessageContext r = InboundMessageContextTest.createInboundMessageContext();
        r.header(ALLOW, "a,B,CcC,dDd");
        final Set<String> allowedMethods = r.getAllowedMethods();
        Assert.assertEquals(4, allowedMethods.size());
        Assert.assertTrue(allowedMethods.contains("A"));
        Assert.assertTrue(allowedMethods.contains("B"));
        Assert.assertTrue(allowedMethods.contains("CCC"));
        Assert.assertTrue(allowedMethods.contains("DDD"));
    }
}

