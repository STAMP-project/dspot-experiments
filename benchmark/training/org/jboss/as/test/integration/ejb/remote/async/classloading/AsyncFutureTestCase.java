/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
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
package org.jboss.as.test.integration.ejb.remote.async.classloading;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test if Future can be returned as Async call
 *
 * @author baranowb
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncFutureTestCase {
    public static final String DEPLOYMENT_UNIT_JAR = "ejbjar";

    public static final String DEPLOYMENT_NAME_JAR = (AsyncFutureTestCase.DEPLOYMENT_UNIT_JAR) + ".jar";

    public static final String DEPLOYMENT_UNIT_EAR = "wildName";

    public static final String DEPLOYMENT_NAME_EAR = (AsyncFutureTestCase.DEPLOYMENT_UNIT_EAR) + ".ear";

    public static final String DEPLOYMENT_UNIT_WAR = "war-with-attitude";

    public static final String DEPLOYMENT_NAME_WAR = (AsyncFutureTestCase.DEPLOYMENT_UNIT_WAR) + ".war";

    public static final String DEPLOYMENT_NAME_COMMON_JAR = "common.jar";

    private static HttpClient HTTP_CLIENT;

    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testAsyncResultInServlet() throws Exception {
        final String requestURL = (((managementClient.getWebUri()) + "/") + (AsyncFutureTestCase.DEPLOYMENT_UNIT_WAR)) + "/x";
        final HttpGet get = new HttpGet(requestURL);
        final HttpResponse response = AsyncFutureTestCase.HTTP_CLIENT.execute(get);
        // consume!!
        consume(response);
        Assert.assertEquals(((requestURL + ">") + (response.getStatusLine().getReasonPhrase())), 200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testAsyncNullResultInServlet() throws Exception {
        final String requestURL = (((managementClient.getWebUri()) + "/") + (AsyncFutureTestCase.DEPLOYMENT_UNIT_WAR)) + "/x?null=true";
        final HttpGet get = new HttpGet(requestURL);
        final HttpResponse response = AsyncFutureTestCase.HTTP_CLIENT.execute(get);
        // consume!!
        consume(response);
        Assert.assertEquals(((requestURL + ">") + (response.getStatusLine().getReasonPhrase())), 200, response.getStatusLine().getStatusCode());
    }
}

