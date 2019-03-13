/**
 * Copyright (C) 2016 Red Hat, inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.jboss.as.test.integration.deployment.deploymentoverlay;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a>  (c) 2015 Red Hat, inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExplodedDeploymentOverlayTestCase {
    public static final String TEST_OVERLAY = "test";

    public static final String TEST_WILDCARD = "test-wildcard";

    @ArquillianResource
    private ManagementClient managementClient;

    @Test
    public void testContentOverridden() throws Exception {
        final String requestURL = (managementClient.getWebUri()) + "/exploded-test/simple/";
        final HttpGet request = new HttpGet(requestURL);
        final HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
        String responseMessage = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("OVERRIDDEN", responseMessage);
        updateDeployment("update.xml", "WEB-INF/web.xml");
        response = httpClient.execute(request);
        responseMessage = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("OVERRIDDEN", responseMessage);
        updateDeployment("web.xml", "WEB-INF/web.xml");
    }

    @Test
    public void testAddingNewFile() throws Exception {
        final HttpClient httpClient = new DefaultHttpClient();
        String requestURL = (managementClient.getWebUri()) + "/exploded-test/overlay/";
        HttpGet request = new HttpGet(requestURL);
        HttpResponse response = httpClient.execute(request);
        String responseMessage = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("test", responseMessage);
        updateDeployment("web.xml", "WEB-INF/classes/wildcard-new-file");
        response = httpClient.execute(request);
        responseMessage = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("test", responseMessage);
        updateDeployment("web.xml", "WEB-INF/classes/wildcard-new-file");
        updateDeployment("index.html", "index.html");
        requestURL = (managementClient.getWebUri()) + "/exploded-test/index.html";
        request = new HttpGet(requestURL);
        response = httpClient.execute(request);
        responseMessage = EntityUtils.toString(response.getEntity());
        Assert.assertTrue(responseMessage, responseMessage.contains("Simple Content test for exploded-test.war"));
    }
}

