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
package org.jboss.as.test.integration.web.headers;


import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test if certain response code wont erase content-type
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseCodeTestCase {
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    @ArquillianResource
    private URL url;

    // TODO: redo once/if Arq will support JUnitParams?
    @Test
    public void test200() throws Exception {
        final HttpGet get = new HttpGet(((url.toExternalForm()) + "jaxrs/test/returnCode/200"));
        final HttpResponse response = ResponseCodeTestCase.HTTP_CLIENT.execute(get);
        doContentTypeChecks(response, 200);
    }

    @Test
    public void test300() throws Exception {
        final HttpGet get = new HttpGet(((url.toExternalForm()) + "jaxrs/test/returnCode/300"));
        final HttpResponse response = ResponseCodeTestCase.HTTP_CLIENT.execute(get);
        doContentTypeChecks(response, 300);
    }

    @Test
    public void test400() throws Exception {
        final HttpGet get = new HttpGet(((url.toExternalForm()) + "jaxrs/test/returnCode/400"));
        final HttpResponse response = ResponseCodeTestCase.HTTP_CLIENT.execute(get);
        doContentTypeChecks(response, 400);
    }

    @Test
    public void test404() throws Exception {
        final HttpGet get = new HttpGet(((url.toExternalForm()) + "jaxrs/test/returnCode/404"));
        final HttpResponse response = ResponseCodeTestCase.HTTP_CLIENT.execute(get);
        doContentTypeChecks(response, 404);
    }

    @Test
    public void test500() throws Exception {
        final HttpGet get = new HttpGet(((url.toExternalForm()) + "jaxrs/test/returnCode/500"));
        final HttpResponse response = ResponseCodeTestCase.HTTP_CLIENT.execute(get);
        doContentTypeChecks(response, 500);
    }
}

