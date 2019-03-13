/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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


import GetStatusServlet.CONTEXT_PATH;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;


public class GetStatusServletTest {
    private TransformationMap mockTransformationMap;

    private JobMap mockJobMap;

    private GetStatusServlet getStatusServlet;

    @Test
    public void testGetStatusServletEscapesHtmlWhenTransNotFound() throws IOException, ServletException {
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter(ArgumentMatchers.anyString())).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);
        getStatusServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertFalse(ServletTestUtils.hasBadText(ServletTestUtils.getInsideOfTag("TITLE", out.toString())));// title will more reliably be plain text

    }

    @Test
    public void testGetStatusServletEscapesHtmlWhenTransFound() throws IOException, ServletException {
        KettleLogStore.init();
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        Trans mockTrans = Mockito.mock(Trans.class);
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        LogChannelInterface mockChannelInterface = Mockito.mock(LogChannelInterface.class);
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter(ArgumentMatchers.anyString())).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);
        Mockito.when(mockTransformationMap.getTransformation(ArgumentMatchers.any(CarteObjectEntry.class))).thenReturn(mockTrans);
        Mockito.when(mockTrans.getLogChannel()).thenReturn(mockChannelInterface);
        Mockito.when(mockTrans.getTransMeta()).thenReturn(mockTransMeta);
        Mockito.when(mockTransMeta.getMaximum()).thenReturn(new Point(10, 10));
        getStatusServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertFalse(out.toString().contains(ServletTestUtils.BAD_STRING_TO_TEST));
    }
}

