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


import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.logging.LogChannelInterface;


public class GetSlavesServletTest {
    private GetSlavesServlet servlet;

    private HttpServletRequest request;

    private HttpServletResponse response;

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testUpdateActivityStatusInDoGet() throws Exception {
        LogChannelInterface log = Mockito.mock(LogChannelInterface.class);
        ServletOutputStream outputStream = Mockito.mock(ServletOutputStream.class);
        SlaveServerDetection activeServerDetection = Mockito.mock(SlaveServerDetection.class);
        SlaveServerDetection inactiveServerDetection = Mockito.mock(SlaveServerDetection.class);
        SlaveServer activeSlaveServer = Mockito.mock(SlaveServer.class);
        SlaveServer inactiveSlaveServer = Mockito.mock(SlaveServer.class);
        servlet.log = log;
        List<SlaveServerDetection> detections = new ArrayList<>();
        detections.add(activeServerDetection);
        detections.add(inactiveServerDetection);
        Mockito.doReturn(false).when(log).isDebug();
        Mockito.doReturn(outputStream).when(response).getOutputStream();
        Mockito.doReturn(detections).when(servlet).getDetections();
        Mockito.doReturn(activeSlaveServer).when(activeServerDetection).getSlaveServer();
        Mockito.doReturn(inactiveSlaveServer).when(inactiveServerDetection).getSlaveServer();
        Mockito.doThrow(new Exception()).when(inactiveSlaveServer).getStatus();
        Mockito.doCallRealMethod().when(servlet).doGet(request, response);
        servlet.doGet(request, response);
        Mockito.verify(activeSlaveServer).getStatus();
        Mockito.verify(activeServerDetection, Mockito.never()).setActive(false);
        Mockito.verify(activeServerDetection, Mockito.never()).setLastInactiveDate(ArgumentMatchers.anyObject());
        Mockito.verify(activeServerDetection).getXML();
        Mockito.verify(inactiveSlaveServer).getStatus();
        Mockito.verify(inactiveServerDetection).setActive(false);
        Mockito.verify(inactiveServerDetection).setLastInactiveDate(ArgumentMatchers.anyObject());
        Mockito.verify(inactiveServerDetection).getXML();
    }
}

