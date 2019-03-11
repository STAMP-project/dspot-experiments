/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.grizzly.connector;


import Invocation.Builder;
import com.ning.http.client.RequestBuilder;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Async HTTP Client Config and Request customizers unit tests.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class CustomizersTest extends JerseyTest {
    @Path("/test")
    public static class EchoResource {
        @POST
        public Response post(@HeaderParam("X-Test-Config")
        String testConfigHeader, @HeaderParam("X-Test-Request")
        String testRequestHeader, String entity) {
            return Response.ok(("POSTed " + entity)).header("X-Test-Config", testConfigHeader).header("X-Test-Request", testRequestHeader).build();
        }
    }

    /**
     * Jersey-2540 related test.
     */
    @Test
    public void testCustomizers() {
        Response response;
        // now using global request customizer
        response = target("test").request().post(text("echo"));
        Assert.assertEquals("POSTed echo", response.readEntity(String.class));
        Assert.assertEquals("tested", response.getHeaderString("X-Test-Config"));
        Assert.assertEquals("tested-global", response.getHeaderString("X-Test-Request"));
        // now using request-specific request customizer
        final Invocation.Builder builder = target("test").request();
        GrizzlyConnectorProvider.register(builder, new GrizzlyConnectorProvider.RequestCustomizer() {
            @Override
            public RequestBuilder customize(ClientRequest requestContext, RequestBuilder requestBuilder) {
                requestBuilder.addHeader("X-Test-Request", "tested-per-request");
                return requestBuilder;
            }
        });
        response = builder.post(text("echo"));
        Assert.assertEquals("POSTed echo", response.readEntity(String.class));
        Assert.assertEquals("tested", response.getHeaderString("X-Test-Config"));
        Assert.assertEquals("tested-per-request", response.getHeaderString("X-Test-Request"));
        // now using global request customizer again
        response = target("test").request().post(text("echo"));
        Assert.assertEquals("POSTed echo", response.readEntity(String.class));
        Assert.assertEquals("tested", response.getHeaderString("X-Test-Config"));
        Assert.assertEquals("tested-global", response.getHeaderString("X-Test-Request"));
    }
}

