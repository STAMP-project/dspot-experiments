/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.oauth;


import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests OAuth 2 client.
 *
 * @author Miroslav Fuksa
 */
public class OAuth2Test extends JerseyTest {
    private static final String STATE = "4564dsf54654fsda654af";

    private static final String CODE = "code-xyz";

    private static final String CLIENT_PUBLIC = "clientPublic";

    private static final String CLIENT_SECRET = "clientSecret";

    @Path("oauth")
    public static class AuthorizationResource {
        @POST
        @Path("access-token")
        @Produces(MediaType.APPLICATION_JSON)
        public OAuth2Test.MyTokenResult getAccessToken(@FormParam("grant_type")
        String grantType, @FormParam("code")
        String code, @FormParam("redirect_uri")
        String redirectUri, @FormParam("client_id")
        String clientId) {
            try {
                Assert.assertEquals("authorization_code", grantType);
                Assert.assertEquals("urn:ietf:wg:oauth:2.0:oob", redirectUri);
                Assert.assertEquals(OAuth2Test.CODE, code);
                Assert.assertEquals(OAuth2Test.CLIENT_PUBLIC, clientId);
            } catch (AssertionError e) {
                e.printStackTrace();
                throw new javax.ws.rs.BadRequestException(Response.status(400).entity(e.getMessage()).build());
            }
            final OAuth2Test.MyTokenResult myTokenResult = new OAuth2Test.MyTokenResult();
            myTokenResult.setAccessToken("access-token-aab999f");
            myTokenResult.setExpiresIn("3600");
            myTokenResult.setTokenType("access-token");
            myTokenResult.setRefreshToken("refresh-xyz");
            return myTokenResult;
        }

        @GET
        @Path("authorization")
        public String authorization(@QueryParam("state")
        String state, @QueryParam("response_type")
        String responseType, @QueryParam("scope")
        String scope, @QueryParam("readOnly")
        String readOnly, @QueryParam("redirect_uri")
        String redirectUri) {
            try {
                Assert.assertEquals("code", responseType);
                Assert.assertEquals(OAuth2Test.STATE, state);
                Assert.assertEquals("urn:ietf:wg:oauth:2.0:oob", redirectUri);
                Assert.assertEquals("contact", scope);
                Assert.assertEquals("true", readOnly);
            } catch (AssertionError e) {
                e.printStackTrace();
                throw new javax.ws.rs.BadRequestException(Response.status(400).entity(e.getMessage()).build());
            }
            return OAuth2Test.CODE;
        }

        @POST
        @Path("refresh-token")
        @Produces(MediaType.APPLICATION_JSON)
        public String refreshToken(@FormParam("grant_type")
        String grantType, @FormParam("refresh_token")
        String refreshToken, @HeaderParam("isArray")
        @DefaultValue("false")
        boolean isArray) {
            try {
                Assert.assertEquals("refresh_token", grantType);
                Assert.assertEquals("refresh-xyz", refreshToken);
            } catch (AssertionError e) {
                e.printStackTrace();
                throw new javax.ws.rs.BadRequestException(Response.status(400).entity(e.getMessage()).build());
            }
            return isArray ? "{\"access_token\":[\"access-token-new\"],\"expires_in\":\"3600\",\"token_type\":\"access-token\"}" : "{\"access_token\":\"access-token-new\",\"expires_in\":\"3600\",\"token_type\":\"access-token\"}";
        }
    }

    @Test
    public void testFlow() {
        testFlow(false);
    }

    @Test
    public void testFlowWithArrayInResponse() {
        testFlow(true);
    }

    @XmlRootElement
    public static class MyTokenResult {
        @XmlAttribute(name = "access_token")
        private String accessToken;

        @XmlAttribute(name = "expires_in")
        private String expiresIn;

        @XmlAttribute(name = "token_type")
        private String tokenType;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        @XmlAttribute(name = "refresh_token")
        private String refreshToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(String expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }
}

