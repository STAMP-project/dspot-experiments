/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.as.test.integration.web.reverseproxy;


import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ReverseProxyTestCase {
    @ContainerResource
    private ManagementClient managementClient;

    private static ManagementClient mc;

    @ArquillianResource
    @OperateOnDeployment("server1")
    private URL url;

    @Test
    public void testReverseProxy() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final Set<String> results = new HashSet<>();
            for (int i = 0; i < 10; ++i) {
                results.add(performCall(httpclient, "name"));
            }
            Assert.assertEquals(2, results.size());
            Assert.assertTrue(results.contains("server1"));
            Assert.assertTrue(results.contains("server2"));
            // TODO: re-add JVM route based sticky session testing
            // String session = performCall("name?session=true");
            // sticky sessions should stick it to this node
            // for (int i = 0; i < 10; ++i) {
            // Assert.assertEquals(session, performCall("name"));
            // }
        }
    }
}

