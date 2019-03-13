/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.security.form;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * <p>Tests if a request made to a secured resource is saved before the client is redirect to the login form. Once the authentication is
 * done, the server should restore the original/saved request.</p>
 *
 * @author Pedro Igor
 */
@RunWith(DefaultServer.class)
public class SaveOriginalPostRequestTestCase {
    @Test
    public void testParametersFromOriginalPostRequest() throws IOException {
        TestHttpClient client = createHttpClient();
        // let's test if a usual POST request have its parameters dumped in the response
        HttpResponse result = executePostRequest(client, "/servletContext/dumpRequest", new BasicNameValuePair("param1", "param1Value"), new BasicNameValuePair("param2", "param2Value"));
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        String response = HttpClientUtils.readResponse(result);
        Assert.assertTrue(response.contains("param1=param1Value/param2=param2Value"));
        // this request should be saved and the client redirect to the login form.
        result = executePostRequest(client, "/servletContext/secured/dumpRequest", new BasicNameValuePair("securedParam1", "securedParam1Value"), new BasicNameValuePair("securedParam2", "securedParam2Value"));
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        Assert.assertTrue(HttpClientUtils.readResponse(result).startsWith("j_security_check"));
        // let's perform a successful authentication and get the request restored
        result = executePostRequest(client, "/servletContext/j_security_check", new BasicNameValuePair("j_username", "user1"), new BasicNameValuePair("j_password", "password1"));
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        response = HttpClientUtils.readResponse(result);
        // let's check if the original request was saved, including its parameters.
        Assert.assertTrue(response.contains("securedParam1=securedParam1Value"));
        Assert.assertTrue(response.contains("securedParam2=securedParam2Value"));
    }

    @Test
    public void testSavedRequestWithWelcomeFile() throws IOException {
        TestHttpClient client = createHttpClient();
        // this request should be saved and the client redirect to the login form.
        HttpResponse result = executePostRequest(client, "/servletContext/", new BasicNameValuePair("securedParam1", "securedParam1Value"), new BasicNameValuePair("securedParam2", "securedParam2Value"));
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        Assert.assertTrue(HttpClientUtils.readResponse(result).startsWith("j_security_check"));
        // let's perform a successful authentication and get the request restored
        result = executePostRequest(client, "/servletContext/j_security_check", new BasicNameValuePair("j_username", "user1"), new BasicNameValuePair("j_password", "password1"));
        Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
        String response = HttpClientUtils.readResponse(result);
        // let's check if the original request was saved, including its parameters.
        Assert.assertTrue(response.contains("securedParam1=securedParam1Value"));
        Assert.assertTrue(response.contains("securedParam2=securedParam2Value"));
    }

    static class RequestDumper extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            dumpRequest(req, resp);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            dumpRequest(req, resp);
        }

        private void dumpRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            StringBuilder buffer = new StringBuilder();
            PrintWriter writer = resp.getWriter();
            buffer.append((("Method: " + (req.getMethod())) + "\n"));
            Enumeration<String> parameterNames = req.getParameterNames();
            buffer.append("Parameters: ");
            while (parameterNames.hasMoreElements()) {
                String parameterName = parameterNames.nextElement();
                buffer.append(((parameterName + "=") + (req.getParameter(parameterName))));
                buffer.append("/");
            } 
            writer.write(buffer.toString());
        }
    }
}

