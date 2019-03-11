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
package org.jboss.as.test.integration.web.jsp.taglib.external;


import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.module.util.TestModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class ExternalTagLibTestCase {
    private static final String BOTH_DEPENDENCIES_WAR = "both-dependencies.war";

    private static final String EXTERNAL_DEPENDENCY_ONLY_WAR = "external-dependency-only.war";

    private static final String MODULE_NAME = "external-tag-lib";

    private static final String TEST_JSP = "test.jsp";

    private static TestModule testModule;

    @ArquillianResource
    @OperateOnDeployment(ExternalTagLibTestCase.BOTH_DEPENDENCIES_WAR)
    private URL both_dependencies_url;

    @ArquillianResource
    @OperateOnDeployment(ExternalTagLibTestCase.EXTERNAL_DEPENDENCY_ONLY_WAR)
    private URL external_dependency_only_url;

    @Test
    public void testExternalTagLibOnly() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(((external_dependency_only_url.toExternalForm()) + (ExternalTagLibTestCase.TEST_JSP)));
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            Assert.assertTrue(result, result.contains("External Tag!"));
        }
    }

    @Test
    public void testExternalAndInternalTagLib() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(((both_dependencies_url.toExternalForm()) + (ExternalTagLibTestCase.TEST_JSP)));
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            Assert.assertTrue(result, result.contains("External Tag!"));
            Assert.assertTrue(result, result.contains("Internal Tag!"));
        }
    }
}

