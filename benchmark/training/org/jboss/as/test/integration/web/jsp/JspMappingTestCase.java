/**
 * Copyright (C) 2014 Red Hat, inc., and individual contributors
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
package org.jboss.as.test.integration.web.jsp;


import java.io.InputStream;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test case for JSP pattern declaration in web.xml
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2014
Red Hat, inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JspMappingTestCase {
    protected static Logger log = Logger.getLogger(JspMappingTestCase.class);

    @ArquillianResource
    protected URL webappUrl;

    private static final String JSP_CONTENT = "<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>\n" + (((((((((("<!DOCTYPE html>\n" + "<html>\n") + "    <body>\n") + "        <p>\n") + "            Hello. Because of the mapping in web.xml, I should be evaluated as a JSP:\n") + "        </p>\n") + "        <p>\n") + "            1 + 1 = ${1+1}\n") + "        </p>\n") + "    </body>\n") + "</html>");

    @Test
    public void testSimpleJSP() throws Exception {
        JspMappingTestCase.log.trace("Simple JSP");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(new HttpGet(((webappUrl.toURI()) + "index.jsp")));
            try (InputStream in = response.getEntity().getContent()) {
                String content = getContent(in);
                Assert.assertThat(content, CoreMatchers.containsString("1 + 1 = 2"));
            }
        }
    }

    @Test
    public void testFalseCss() throws Exception {
        JspMappingTestCase.log.trace("False CSS");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(new HttpGet(((webappUrl.toURI()) + "test.css")));
            try (InputStream in = response.getEntity().getContent()) {
                String content = getContent(in);
                Assert.assertThat(content, CoreMatchers.containsString("1 + 1 = 2"));
            }
        }
    }

    @Test
    public void testFalseHtmlPage() throws Exception {
        JspMappingTestCase.log.trace("False HTML");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(new HttpGet(((webappUrl.toURI()) + "test.html")));
            try (InputStream in = response.getEntity().getContent()) {
                String content = getContent(in);
                Assert.assertThat(content, CoreMatchers.containsString("1 + 1 = 2"));
            }
        }
    }

    @Test
    public void testTrueHtmlPage() throws Exception {
        JspMappingTestCase.log.trace("True HTML");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(new HttpGet(((webappUrl.toURI()) + "index.html")));
            try (InputStream in = response.getEntity().getContent()) {
                String content = getContent(in);
                Assert.assertThat(content, CoreMatchers.not(CoreMatchers.containsString("1 + 1 = 2")));
            }
        }
    }
}

