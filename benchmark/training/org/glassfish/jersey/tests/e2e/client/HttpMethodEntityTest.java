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


import HttpHeaders.CONTENT_ENCODING;
import HttpHeaders.CONTENT_LANGUAGE;
import java.util.concurrent.Future;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests HTTP methods and entity presence.
 *
 * @author Miroslav Fuksa
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class HttpMethodEntityTest extends JerseyTest {
    @Test
    public void testGet() {
        _test("GET", true, true);
        _test("GET", false, false);
    }

    @Test
    public void testPost() {
        _test("POST", true, false);
        _test("POST", false, false);
    }

    @Test
    public void testPut() {
        _test("PUT", true, false);
        _test("PUT", false, true);
    }

    @Test
    public void testDelete() {
        _test("DELETE", true, true);
        _test("DELETE", false, false);
    }

    @Test
    public void testHead() {
        _test("HEAD", true, true);
        _test("HEAD", false, false);
    }

    @Test
    public void testOptions() {
        _test("OPTIONS", true, true);
        _test("OPTIONS", false, false);
    }

    /**
     * Reproducer for JERSEY-2370: Sending POST without body.
     */
    @Test
    public void testEmptyPostWithoutContentType() {
        final WebTarget resource = target().path("resource");
        try {
            final Future<Response> future = resource.request().async().post(null);
            Assert.assertEquals(200, future.get().getStatus());
            final Response response = resource.request().post(null);
            Assert.assertEquals(200, response.getStatus());
        } catch (Exception e) {
            Assert.fail("Sending POST method without entity should not fail.");
        }
    }

    /**
     * Reproducer for JERSEY-2370: Sending POST without body.
     */
    @Test
    public void testEmptyPostWithContentType() {
        final WebTarget resource = target().path("resource");
        try {
            final Future<Response> future = resource.request().async().post(Entity.entity(null, "text/plain"));
            Assert.assertEquals(200, future.get().getStatus());
            final Response response = resource.request().post(Entity.entity(null, "text/plain"));
            Assert.assertEquals(200, response.getStatus());
        } catch (Exception e) {
            Assert.fail("Sending POST method without entity should not fail.");
        }
    }

    @Path("resource")
    public static class Resource {
        @Context
        HttpHeaders httpHeaders;

        @GET
        public String get() {
            return "get";
        }

        @POST
        public String post(String str) {
            // See JERSEY-1455
            Assert.assertFalse(httpHeaders.getRequestHeaders().containsKey(CONTENT_ENCODING));
            Assert.assertFalse(httpHeaders.getRequestHeaders().containsKey(CONTENT_LANGUAGE));
            return "post";
        }

        @PUT
        public String put(String str) {
            return "put";
        }

        @HEAD
        public String head() {
            return "head";
        }

        @DELETE
        public String delete() {
            return "delete";
        }
    }
}

