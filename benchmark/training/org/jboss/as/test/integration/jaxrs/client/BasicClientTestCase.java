/**
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.jaxrs.client;


import java.net.URL;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Use jaxrs client to send http requests to the server.
 *
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class BasicClientTestCase {
    @ArquillianResource
    private URL url;

    static Client client;

    @Test
    public void testGet() throws Exception {
        String result = BasicClientTestCase.client.target(((url.toExternalForm()) + "myjaxrs/client")).request("text/plain").get(String.class);
        Assert.assertEquals("GET: Hello World!", result);
    }

    @Test
    public void testPost() throws Exception {
        String result = BasicClientTestCase.client.target(((url.toExternalForm()) + "myjaxrs/client")).request("text/plain").post(Entity.text("David"), String.class);
        Assert.assertEquals("POST: David", result);
    }

    @Test
    public void testPut() throws Exception {
        String result = BasicClientTestCase.client.target(((url.toExternalForm()) + "myjaxrs/client")).request("text/plain").put(Entity.text("Michael"), String.class);
        Assert.assertEquals("PUT: Michael", result);
    }

    @Test
    public void testDelete() throws Exception {
        String result = BasicClientTestCase.client.target(((url.toExternalForm()) + "myjaxrs/client")).request("text/plain").delete(String.class);
        Assert.assertEquals("DELETE:", result);
    }
}

