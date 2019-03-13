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


import StopJobServlet.CONTEXT_PATH;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
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
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class StopJobServletTest {
    private JobMap mockJobMap;

    private StopJobServlet stopJobServlet;

    @Test
    @PrepareForTest({ Encode.class })
    public void testStopJobServletEscapesHtmlWhenTransNotFound() throws IOException, ServletException {
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        PowerMockito.spy(Encode.class);
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter(ArgumentMatchers.anyString())).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);
        stopJobServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertFalse(ServletTestUtils.hasBadText(ServletTestUtils.getInsideOfTag("H1", out.toString())));
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        Encode.forHtml(ArgumentMatchers.anyString());
    }

    @Test
    @PrepareForTest({ Encode.class })
    public void testStopJobServletEscapesHtmlWhenTransFound() throws IOException, ServletException {
        KettleLogStore.init();
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        Job mockJob = Mockito.mock(Job.class);
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        LogChannelInterface mockLogChannelInterface = Mockito.mock(LogChannelInterface.class);
        mockJob.setName(ServletTestUtils.BAD_STRING_TO_TEST);
        StringWriter out = new StringWriter();
        PrintWriter printWriter = new PrintWriter(out);
        PowerMockito.spy(Encode.class);
        Mockito.when(mockHttpServletRequest.getContextPath()).thenReturn(CONTEXT_PATH);
        Mockito.when(mockHttpServletRequest.getParameter(ArgumentMatchers.anyString())).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockHttpServletResponse.getWriter()).thenReturn(printWriter);
        Mockito.when(mockJobMap.getJob(ArgumentMatchers.any(CarteObjectEntry.class))).thenReturn(mockJob);
        Mockito.when(mockJob.getLogChannelId()).thenReturn(ServletTestUtils.BAD_STRING_TO_TEST);
        Mockito.when(mockJob.getLogChannel()).thenReturn(mockLogChannelInterface);
        Mockito.when(mockJob.getJobMeta()).thenReturn(mockJobMeta);
        Mockito.when(mockJobMeta.getMaximum()).thenReturn(new Point(10, 10));
        stopJobServlet.doGet(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertFalse(ServletTestUtils.hasBadText(ServletTestUtils.getInsideOfTag("H1", out.toString())));
        PowerMockito.verifyStatic(Mockito.atLeastOnce());
        Encode.forHtml(ArgumentMatchers.anyString());
    }
}

