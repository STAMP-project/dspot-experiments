/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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


import GetTransStatusServlet.CONTEXT_PATH;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.www.cache.CarteStatusCache;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class GetTransStatusServletTest {
    private TransformationMap mockTransformationMap;

    private GetTransStatusServlet getTransStatusServlet;

    @Test
    @PrepareForTest({ Encode.class })
    public void testGetTransStatusServletEscapesHtmlWhenTransNotFound() throws IOException, ServletException {
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        PowerMockito.spy(Encode.class);
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter(ArgumentMatchers.anyString())).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);
        getTransStatusServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertFalse(ServletTestUtils.hasBadText(ServletTestUtils.getInsideOfTag("H1", out.toString())));
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        Encode.forHtml(ArgumentMatchers.anyString());
    }

    @Test
    @PrepareForTest({ Encode.class })
    public void testGetTransStatusServletEscapesHtmlWhenTransFound() throws IOException, ServletException {
        KettleLogStore.init();
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        Trans mockTrans = Mockito.mock(Trans.class);
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        LogChannelInterface mockChannelInterface = Mockito.mock(LogChannelInterface.class);
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        PowerMockito.spy(Encode.class);
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter(ArgumentMatchers.anyString())).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);
        Mockito.when(mockTransformationMap.getTransformation(ArgumentMatchers.any(CarteObjectEntry.class))).thenReturn(mockTrans);
        Mockito.when(mockTrans.getLogChannel()).thenReturn(mockChannelInterface);
        Mockito.when(mockTrans.getTransMeta()).thenReturn(mockTransMeta);
        Mockito.when(mockTransMeta.getMaximum()).thenReturn(new Point(10, 10));
        getTransStatusServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertFalse(ServletTestUtils.hasBadText(ServletTestUtils.getInsideOfTag("TITLE", out.toString())));
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        Encode.forHtml(ArgumentMatchers.anyString());
    }

    @Test
    public void testGetTransStatus() throws IOException, ServletException {
        KettleLogStore.init();
        CarteStatusCache cacheMock = Mockito.mock(CarteStatusCache.class);
        getTransStatusServlet.cache = cacheMock;
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        Trans mockTrans = Mockito.mock(Trans.class);
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        LogChannelInterface mockChannelInterface = Mockito.mock(LogChannelInterface.class);
        ServletOutputStream outMock = Mockito.mock(ServletOutputStream.class);
        String id = "123";
        String logId = "logId";
        String useXml = "Y";
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter("id")).thenReturn(id);
        Mockito.when(mockHttpServletRequest.getParameter("xml")).thenReturn(useXml);
        Mockito.when(mockHttpServletResponse.getOutputStream()).thenReturn(outMock);
        Mockito.when(mockTransformationMap.getTransformation(ArgumentMatchers.any(CarteObjectEntry.class))).thenReturn(mockTrans);
        Mockito.when(mockTrans.getLogChannel()).thenReturn(mockChannelInterface);
        Mockito.when(mockTrans.getTransMeta()).thenReturn(mockTransMeta);
        Mockito.when(mockTrans.getLogChannelId()).thenReturn(logId);
        Mockito.when(mockTrans.isFinishedOrStopped()).thenReturn(true);
        Mockito.when(mockTrans.getStatus()).thenReturn("Finished");
        Mockito.when(mockTransMeta.getMaximum()).thenReturn(new Point(10, 10));
        getTransStatusServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Mockito.when(cacheMock.get(logId, 0)).thenReturn(new byte[]{ 0, 1, 2 });
        getTransStatusServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Mockito.verify(cacheMock, Mockito.times(2)).get(logId, 0);
        Mockito.verify(cacheMock, Mockito.times(1)).put(ArgumentMatchers.eq(logId), ArgumentMatchers.anyString(), ArgumentMatchers.eq(0));
        Mockito.verify(mockTrans, Mockito.times(1)).getLogChannel();
    }
}

