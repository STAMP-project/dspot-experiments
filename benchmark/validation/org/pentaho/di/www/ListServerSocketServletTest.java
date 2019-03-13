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


import ListServerSocketServlet.CONTEXT_PATH;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.owasp.encoder.Encode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class ListServerSocketServletTest {
    private TransformationMap mockTransformationMap;

    private ListServerSocketServlet listServerSocketServlet;

    @Test
    @PrepareForTest({ Encode.class })
    public void testListServerSocketServletEncodesParametersForHmtlResponse() throws IOException, ServletException {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        SocketPortAllocation mockSocketPortAllocation = Mockito.mock(SocketPortAllocation.class);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                byteArrayOutputStream.write(b);
            }
        };
        PowerMockito.spy(Encode.class);
        Mockito.when(mockRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockRequest.getParameter(ArgumentMatchers.anyString())).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockResponse.getOutputStream()).thenReturn(servletOutputStream);
        Mockito.when(mockTransformationMap.allocateServerSocketPort(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockSocketPortAllocation);
        listServerSocketServlet.doGet(mockRequest, mockResponse);
        String response = byteArrayOutputStream.toString();
        Assert.assertFalse(ServletTestUtils.hasBadText(ServletTestUtils.getInsideOfTag("H1", response)));
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        Encode.forHtml(ArgumentMatchers.anyString());
    }
}

