/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.web.security.servlet.methods;


import HttpServletResponse.SC_FORBIDDEN;
import HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import HttpServletResponse.SC_UNAUTHORIZED;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.integration.web.security.WebTestsSecurityDomainSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests whether the <deny-uncovered-http-methods/> tag in web.xml behavior is correct.
 *
 * @author Jan Tymel
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(WebTestsSecurityDomainSetup.class)
public class DenyUncoveredHttpMethodsTestCase {
    @ArquillianResource(SecuredServlet.class)
    URL deploymentURL;

    @Test
    public void testCorrectUserAndPassword() throws Exception {
        HttpGet httpGet = new HttpGet(getURL());
        HttpResponse response = getHttpResponse(httpGet);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_UNAUTHORIZED));
    }

    @Test
    public void testHeadMethod() throws Exception {
        HttpHead httpHead = new HttpHead(getURL());
        HttpResponse response = getHttpResponse(httpHead);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_UNAUTHORIZED));
    }

    @Test
    public void testTraceMethod() throws Exception {
        HttpTrace httpTrace = new HttpTrace(getURL());
        HttpResponse response = getHttpResponse(httpTrace);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_METHOD_NOT_ALLOWED));
    }

    @Test
    public void testPostMethod() throws Exception {
        HttpPost httpPost = new HttpPost(getURL());
        HttpResponse response = getHttpResponse(httpPost);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_FORBIDDEN));
    }

    @Test
    public void testPutMethod() throws Exception {
        HttpPut httpPut = new HttpPut(getURL());
        HttpResponse response = getHttpResponse(httpPut);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_FORBIDDEN));
    }

    @Test
    public void testDeleteMethod() throws Exception {
        HttpDelete httpDelete = new HttpDelete(getURL());
        HttpResponse response = getHttpResponse(httpDelete);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_FORBIDDEN));
    }

    @Test
    public void testOptionsMethod() throws Exception {
        HttpOptions httpOptions = new HttpOptions(getURL());
        HttpResponse response = getHttpResponse(httpOptions);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_FORBIDDEN));
    }

    /**
     * Tests whether the <deny-uncovered-http-methods/> tag filters methods before the servlet is called. This test creates
     * custom HTTP method and tries to invoke it. If <deny-uncovered-http-methods/> works correctly status code 403 should be
     * returned. 403 should be returned also in case the servlet returns anything else for unknown HTTP methods as well.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCustomMethod() throws Exception {
        HttpUriRequest request = new HttpGet(getURL()) {
            @Override
            public String getMethod() {
                return "customMethod";
            }
        };
        HttpResponse response = getHttpResponse(request);
        Assert.assertThat(statusCodeOf(response), CoreMatchers.is(SC_FORBIDDEN));
    }
}

