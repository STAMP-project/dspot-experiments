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
package org.glassfish.jersey.tests.e2e.client;


import HttpHeaders.CONTENT_LANGUAGE;
import HttpHeaders.CONTENT_LENGTH;
import HttpHeaders.DATE;
import HttpHeaders.ETAG;
import HttpHeaders.LAST_MODIFIED;
import MediaType.TEXT_HTML_TYPE;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests aborting the request on the client side.
 *
 * @author Miroslav Fuksa
 */
public class AbortResponseClientTest extends JerseyTest {
    @Test
    public void testRequestAbort() {
        final Date date = getDate();
        ClientRequestFilter outFilter = new ClientRequestFilter() {
            @Override
            public void filter(ClientRequestContext context) throws IOException {
                NewCookie cookie1 = new NewCookie("cookie1", "cookie1");
                NewCookie cookie2 = new NewCookie("cookie2", "cookie2");
                final Response response = Response.ok().cookie(cookie1).cookie(cookie2).header("head1", "head1").header(DATE, date).header(ETAG, "\"123465\"").header(CONTENT_LANGUAGE, "language").header(LAST_MODIFIED, date).header(CONTENT_LENGTH, 99).type(TEXT_HTML_TYPE).location(URI.create("www.oracle.com")).build();
                // abort the request
                context.abortWith(response);
            }
        };
        ClientResponseFilter inFilter = new ClientResponseFilter() {
            @Override
            public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
                Map<String, NewCookie> map = responseContext.getCookies();
                Assert.assertEquals("cookie1", map.get("cookie1").getValue());
                Assert.assertEquals("cookie2", map.get("cookie2").getValue());
                final MultivaluedMap<String, String> headers = responseContext.getHeaders();
                Assert.assertEquals("head1", headers.get("head1").get(0));
                Assert.assertEquals(date.getTime(), responseContext.getDate().getTime());
            }
        };
        WebTarget target = target().path("test");
        target.register(outFilter).register(inFilter);
        Invocation i = target.request().buildGet();
        Response r = i.invoke();
        Assert.assertEquals("head1", r.getHeaderString("head1"));
        Assert.assertEquals("cookie1", r.getCookies().get("cookie1").getValue());
        Assert.assertEquals("cookie2", r.getCookies().get("cookie2").getValue());
        Assert.assertEquals(date.getTime(), r.getDate().getTime());
        Assert.assertEquals("123465", r.getEntityTag().getValue());
        Assert.assertEquals("language", r.getLanguage().toString());
        Assert.assertEquals(date.getTime(), r.getLastModified().getTime());
        // Assert.assertEquals("uri", r.getLink("link")); TODO: not supported yet
        Assert.assertEquals("www.oracle.com", r.getLocation().toString());
        Assert.assertEquals(TEXT_HTML_TYPE, r.getMediaType());
        Assert.assertEquals(99, r.getLength());
        Assert.assertEquals(200, r.getStatus());
    }

    @Path("test")
    public static class TestResource {
        @GET
        public String get() {
            return "this will never be called.";
        }
    }
}

