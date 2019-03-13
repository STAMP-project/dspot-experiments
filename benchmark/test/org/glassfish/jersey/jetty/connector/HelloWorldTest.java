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
package org.glassfish.jersey.jetty.connector;


import MediaType.TEXT_PLAIN;
import MediaType.TEXT_PLAIN_TYPE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class HelloWorldTest extends JerseyTest {
    private static final Logger LOGGER = Logger.getLogger(HelloWorldTest.class.getName());

    private static final String ROOT_PATH = "helloworld";

    @Path("helloworld")
    public static class HelloWorldResource {
        public static final String CLICHED_MESSAGE = "Hello World!";

        @GET
        @Produces("text/plain")
        public String getHello() {
            return HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE;
        }
    }

    @Test
    public void testConnection() {
        Response response = target().path(HelloWorldTest.ROOT_PATH).request("text/plain").get();
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void testClientStringResponse() {
        String s = target().path(HelloWorldTest.ROOT_PATH).request().get(String.class);
        Assert.assertEquals(HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE, s);
    }

    @Test
    public void testAsyncClientRequests() throws InterruptedException {
        final int REQUESTS = 20;
        final CountDownLatch latch = new CountDownLatch(REQUESTS);
        final long tic = System.currentTimeMillis();
        for (int i = 0; i < REQUESTS; i++) {
            final int id = i;
            target().path(HelloWorldTest.ROOT_PATH).request().async().get(new javax.ws.rs.client.InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    try {
                        final String result = response.readEntity(String.class);
                        Assert.assertEquals(HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE, result);
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void failed(Throwable error) {
                    error.printStackTrace();
                    latch.countDown();
                }
            });
        }
        latch.await((10 * (getAsyncTimeoutMultiplier())), TimeUnit.SECONDS);
        final long toc = System.currentTimeMillis();
        Logger.getLogger(HelloWorldTest.class.getName()).info(("Executed in: " + (toc - tic)));
    }

    @Test
    public void testHead() {
        Response response = target().path(HelloWorldTest.ROOT_PATH).request().head();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(TEXT_PLAIN_TYPE, response.getMediaType());
    }

    @Test
    public void testFooBarOptions() {
        Response response = target().path(HelloWorldTest.ROOT_PATH).request().header("Accept", "foo/bar").options();
        Assert.assertEquals(200, response.getStatus());
        final String allowHeader = response.getHeaderString("Allow");
        _checkAllowContent(allowHeader);
        Assert.assertEquals("foo/bar", response.getMediaType().toString());
        Assert.assertEquals(0, response.getLength());
    }

    @Test
    public void testTextPlainOptions() {
        Response response = target().path(HelloWorldTest.ROOT_PATH).request().header("Accept", TEXT_PLAIN).options();
        Assert.assertEquals(200, response.getStatus());
        final String allowHeader = response.getHeaderString("Allow");
        _checkAllowContent(allowHeader);
        Assert.assertEquals(TEXT_PLAIN_TYPE, response.getMediaType());
        final String responseBody = response.readEntity(String.class);
        _checkAllowContent(responseBody);
    }

    @Test
    public void testMissingResourceNotFound() {
        Response response;
        response = target().path(((HelloWorldTest.ROOT_PATH) + "arbitrary")).request().get();
        Assert.assertEquals(404, response.getStatus());
        response.close();
        response = target().path(HelloWorldTest.ROOT_PATH).path("arbitrary").request().get();
        Assert.assertEquals(404, response.getStatus());
        response.close();
    }

    @Test
    public void testLoggingFilterClientClass() {
        Client client = client();
        client.register(CustomLoggingFilter.class).property("foo", "bar");
        CustomLoggingFilter.preFilterCalled = CustomLoggingFilter.postFilterCalled = 0;
        String s = target().path(HelloWorldTest.ROOT_PATH).request().get(String.class);
        Assert.assertEquals(HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE, s);
        Assert.assertEquals(1, CustomLoggingFilter.preFilterCalled);
        Assert.assertEquals(1, CustomLoggingFilter.postFilterCalled);
        client.close();
    }

    @Test
    public void testLoggingFilterClientInstance() {
        Client client = client();
        client.register(new CustomLoggingFilter()).property("foo", "bar");
        CustomLoggingFilter.preFilterCalled = CustomLoggingFilter.postFilterCalled = 0;
        String s = target().path(HelloWorldTest.ROOT_PATH).request().get(String.class);
        Assert.assertEquals(HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE, s);
        Assert.assertEquals(1, CustomLoggingFilter.preFilterCalled);
        Assert.assertEquals(1, CustomLoggingFilter.postFilterCalled);
        client.close();
    }

    @Test
    public void testLoggingFilterTargetClass() {
        WebTarget target = target().path(HelloWorldTest.ROOT_PATH);
        target.register(CustomLoggingFilter.class).property("foo", "bar");
        CustomLoggingFilter.preFilterCalled = CustomLoggingFilter.postFilterCalled = 0;
        String s = target.request().get(String.class);
        Assert.assertEquals(HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE, s);
        Assert.assertEquals(1, CustomLoggingFilter.preFilterCalled);
        Assert.assertEquals(1, CustomLoggingFilter.postFilterCalled);
    }

    @Test
    public void testLoggingFilterTargetInstance() {
        WebTarget target = target().path(HelloWorldTest.ROOT_PATH);
        target.register(new CustomLoggingFilter()).property("foo", "bar");
        CustomLoggingFilter.preFilterCalled = CustomLoggingFilter.postFilterCalled = 0;
        String s = target.request().get(String.class);
        Assert.assertEquals(HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE, s);
        Assert.assertEquals(1, CustomLoggingFilter.preFilterCalled);
        Assert.assertEquals(1, CustomLoggingFilter.postFilterCalled);
    }

    @Test
    public void testConfigurationUpdate() {
        Client client1 = client();
        client1.register(CustomLoggingFilter.class).property("foo", "bar");
        Client client = ClientBuilder.newClient(client1.getConfiguration());
        CustomLoggingFilter.preFilterCalled = CustomLoggingFilter.postFilterCalled = 0;
        String s = target().path(HelloWorldTest.ROOT_PATH).request().get(String.class);
        Assert.assertEquals(HelloWorldTest.HelloWorldResource.CLICHED_MESSAGE, s);
        Assert.assertEquals(1, CustomLoggingFilter.preFilterCalled);
        Assert.assertEquals(1, CustomLoggingFilter.postFilterCalled);
        client.close();
    }
}

