/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jetty;


import java.io.File;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.eclipse.jetty.servlets.MultiPartFilter;
import org.junit.Test;


public class MultiPartFormWithCustomFilterTest extends BaseJettyTest {
    private static class MyMultipartFilter extends MultiPartFilter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            // set a marker attribute to show that this filter class was used
            addHeader("MyMultipartFilter", "true");
            super.doFilter(request, response, chain);
        }
    }

    @Test
    public void testSendMultiPartForm() throws Exception {
        HttpClient httpclient = new HttpClient();
        File file = new File("src/test/resources/log4j2.properties");
        PostMethod httppost = new PostMethod((("http://localhost:" + (BaseJettyTest.getPort())) + "/test"));
        Part[] parts = new Part[]{ new StringPart("comment", "A binary file of some kind"), new FilePart(file.getName(), file) };
        MultipartRequestEntity reqEntity = new MultipartRequestEntity(parts, httppost.getParams());
        httppost.setRequestEntity(reqEntity);
        int status = httpclient.executeMethod(httppost);
        assertEquals("Get a wrong response status", 200, status);
        String result = httppost.getResponseBodyAsString();
        assertEquals("Get a wrong result", "A binary file of some kind", result);
        assertNotNull("Did not use custom multipart filter", httppost.getResponseHeader("MyMultipartFilter"));
    }

    @Test
    public void testSendMultiPartFormOverrideEnableMultpartFilterFalse() throws Exception {
        HttpClient httpclient = new HttpClient();
        File file = new File("src/test/resources/log4j2.properties");
        PostMethod httppost = new PostMethod((("http://localhost:" + (BaseJettyTest.getPort())) + "/test2"));
        Part[] parts = new Part[]{ new StringPart("comment", "A binary file of some kind"), new FilePart(file.getName(), file) };
        MultipartRequestEntity reqEntity = new MultipartRequestEntity(parts, httppost.getParams());
        httppost.setRequestEntity(reqEntity);
        int status = httpclient.executeMethod(httppost);
        assertEquals("Get a wrong response status", 200, status);
        assertNotNull("Did not use custom multipart filter", httppost.getResponseHeader("MyMultipartFilter"));
    }
}

