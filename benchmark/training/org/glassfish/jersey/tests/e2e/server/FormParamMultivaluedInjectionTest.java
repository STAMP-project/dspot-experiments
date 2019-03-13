/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.server;


import Response.Status.OK;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that the MultivaluedMap injection does not close the request buffer and allows
 * to proceed other FormParam injections.
 *
 * @author Petr Bouda
 */
public class FormParamMultivaluedInjectionTest extends JerseyTest {
    public static final String PREDEFINED_RESPONSE = "Hello George Javatar";

    @Path("form")
    public static class FormResource {
        @POST
        @Path("simple")
        public Response simple(MultivaluedMap<String, String> formParams, @FormParam("firstname")
        String firstname, @FormParam("lastname")
        String lastname) {
            Assert.assertEquals(2, formParams.size());
            Assert.assertEquals("George", formParams.get("firstname").get(0));
            Assert.assertEquals("Javatar", formParams.get("lastname").get(0));
            return Response.status(OK).entity(((("Hello " + firstname) + " ") + lastname)).build();
        }

        @POST
        @Path("nullable")
        public Response nullable(MultivaluedMap<String, String> formParams, @FormParam("firstname")
        String firstname, @FormParam("lastname")
        String lastname) {
            Assert.assertEquals(2, formParams.size());
            Assert.assertEquals(2, formParams.get("firstname").size());
            Assert.assertEquals("George", formParams.get("firstname").get(0));
            Assert.assertEquals("Javatar", formParams.get("lastname").get(0));
            return Response.status(OK).entity(((("Hello " + firstname) + " ") + lastname)).build();
        }

        @POST
        @Path("mixed")
        public Response mixed(@FormParam("firstname")
        String firstname, MultivaluedMap<String, String> formParams, @FormParam("lastname")
        String lastname) {
            Assert.assertEquals(2, formParams.size());
            Assert.assertEquals(2, formParams.get("firstname").size());
            Assert.assertEquals("George", formParams.get("firstname").get(0));
            Assert.assertEquals("Javatar", formParams.get("lastname").get(0));
            return Response.status(OK).entity(((("Hello " + firstname) + " ") + lastname)).build();
        }

        @POST
        @Path("encoded")
        public Response encoded(MultivaluedMap<String, String> formParams, @Encoded
        @FormParam("firstname")
        String firstname, @FormParam("lastname")
        String lastname) {
            Assert.assertEquals(2, formParams.size());
            Assert.assertEquals("George", formParams.get("firstname").get(0));
            Assert.assertEquals("Javatar", formParams.get("lastname").get(0));
            return Response.status(OK).entity(((("Hello " + firstname) + " ") + lastname)).build();
        }
    }

    @Path("form-ext")
    public static class FormExtResource {
        @Encoded
        @FormParam("firstname")
        String firstname;

        @POST
        @Path("encoded")
        public Response encoded(MultivaluedMap<String, String> formParams, @FormParam("lastname")
        String lastname) {
            Assert.assertEquals(2, formParams.size());
            Assert.assertEquals("George", formParams.get("firstname").get(0));
            Assert.assertEquals("Javatar", formParams.get("lastname").get(0));
            return Response.status(OK).entity(((("Hello " + (firstname)) + " ") + lastname)).build();
        }
    }

    @Test
    public void testFormMultivaluedParam() {
        Response result = call("/form/simple", "firstname=George&lastname=Javatar");
        Assert.assertEquals(FormParamMultivaluedInjectionTest.PREDEFINED_RESPONSE, result.readEntity(String.class));
    }

    @Test
    public void testFormMultivaluedParamWithNull() {
        Response result = call("/form/nullable", "firstname=George&firstname&lastname=Javatar");
        Assert.assertEquals(FormParamMultivaluedInjectionTest.PREDEFINED_RESPONSE, result.readEntity(String.class));
    }

    @Test
    public void testFormMultivaluedParamMixedParamOrder() {
        Response result = call("/form/mixed", "firstname=George&firstname&lastname=Javatar");
        Assert.assertEquals(FormParamMultivaluedInjectionTest.PREDEFINED_RESPONSE, result.readEntity(String.class));
    }

    @Test
    public void testFormMultivaluedParamEncoded() {
        Response result = call("/form/encoded", "firstname=George&lastname=Javatar");
        Assert.assertEquals(FormParamMultivaluedInjectionTest.PREDEFINED_RESPONSE, result.readEntity(String.class));
    }

    @Test
    public void testFormMultivaluedParamExternalEncodedInjection() {
        Response result = call("/form-ext/encoded", "firstname=George&lastname=Javatar");
        Assert.assertEquals(FormParamMultivaluedInjectionTest.PREDEFINED_RESPONSE, result.readEntity(String.class));
    }
}

