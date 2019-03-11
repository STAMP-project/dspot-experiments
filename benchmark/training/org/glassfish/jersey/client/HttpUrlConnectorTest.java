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
package org.glassfish.jersey.client;


import HttpHeaders.LINK;
import HttpUrlConnectorProvider.ConnectionFactory;
import Response.Status.NO_CONTENT;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import javax.ws.rs.core.Link;
import org.glassfish.jersey.client.internal.HttpUrlConnector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Various tests for the default client connector.
 *
 * @author Carlo Pellegrini
 * @author Miroslav Fuksa
 * @author Marek Potociar
 * @author Jakub Podlesak
 */
public class HttpUrlConnectorTest {
    // there is about 30 ms overhead on my laptop, 500 ms should be safe
    private final int TimeoutBASE = 500;

    /**
     * Reproducer for JERSEY-1611.
     */
    @Test
    public void testResolvedRequestUri() {
        HttpUrlConnectorProvider.ConnectionFactory factory = new HttpUrlConnectorProvider.ConnectionFactory() {
            @Override
            public HttpURLConnection getConnection(URL endpointUrl) throws IOException {
                HttpURLConnection result = ((HttpURLConnection) (endpointUrl.openConnection()));
                return wrapRedirectedHttp(result);
            }
        };
        JerseyClient client = new JerseyClientBuilder().build();
        ClientRequest request = client.target("http://localhost:8080").request().buildGet().request();
        final HttpUrlConnectorProvider connectorProvider = new HttpUrlConnectorProvider().connectionFactory(factory);
        HttpUrlConnector connector = ((HttpUrlConnector) (connectorProvider.getConnector(client, client.getConfiguration())));
        ClientResponse res = connector.apply(request);
        Assert.assertEquals(URI.create("http://localhost:8080"), res.getRequestContext().getUri());
        Assert.assertEquals(URI.create("http://redirected.org:8080/redirected"), res.getResolvedRequestUri());
        res.getHeaders().putSingle(LINK, Link.fromPath("action").rel("test").build().toString());
        Assert.assertEquals(URI.create("http://redirected.org:8080/action"), res.getLink("test").getUri());
    }

    /**
     * Test SSL connection.
     */
    @Test
    public void testSSLConnection() {
        JerseyClient client = new JerseyClientBuilder().build();
        ClientRequest request = client.target("https://localhost:8080").request().buildGet().request();
        HttpUrlConnectorProvider.ConnectionFactory factory = new HttpUrlConnectorProvider.ConnectionFactory() {
            @Override
            public HttpURLConnection getConnection(URL endpointUrl) throws IOException {
                HttpURLConnection result = ((HttpURLConnection) (endpointUrl.openConnection()));
                return wrapNoContentHttps(result);
            }
        };
        final HttpUrlConnectorProvider connectorProvider = new HttpUrlConnectorProvider().connectionFactory(factory);
        HttpUrlConnector connector = ((HttpUrlConnector) (connectorProvider.getConnector(client, client.getConfiguration())));
        ClientResponse res = connector.apply(request);
        Assert.assertEquals(NO_CONTENT.getStatusCode(), res.getStatusInfo().getStatusCode());
        Assert.assertEquals(NO_CONTENT.getReasonPhrase(), res.getStatusInfo().getReasonPhrase());
    }
}

