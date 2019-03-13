/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.www;


import GetPropertiesServlet.CONTEXT_PATH;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


/**
 * Tests for {@link org.pentaho.di.www.GetPropertiesServlet}
 */
public class GetPropertiesServletTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void getContextPath() throws Exception {
        GetPropertiesServlet servlet = new GetPropertiesServlet();
        servlet.setJettyMode(true);
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter("xml")).thenReturn("Y");
        Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);
        Mockito.when(mockHttpServletResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
            private ByteArrayOutputStream baos = new ByteArrayOutputStream();

            @Override
            public void write(int b) throws IOException {
                baos.write(b);
            }

            public String toString() {
                return baos.toString();
            }
        });
        servlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        // check that response is not contains sample text
        Assert.assertFalse(mockHttpServletResponse.getOutputStream().toString().startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }
}

