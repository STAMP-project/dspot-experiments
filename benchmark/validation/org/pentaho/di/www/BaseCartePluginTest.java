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


import CarteRequestHandler.CarteRequest;
import CarteRequestHandler.OutputStreamResponse;
import CarteRequestHandler.WriterResponse;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogChannelInterface;


public class BaseCartePluginTest {
    HttpServletRequest req = Mockito.mock(HttpServletRequest.class);

    HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

    LogChannelInterface log = Mockito.mock(LogChannelInterface.class);

    WriterResponse writerResponse = Mockito.mock(WriterResponse.class);

    OutputStreamResponse outputStreamResponse = Mockito.mock(OutputStreamResponse.class);

    PrintWriter printWriter = Mockito.mock(PrintWriter.class);

    ServletOutputStream outputStream = Mockito.mock(ServletOutputStream.class);

    ArgumentCaptor<CarteRequestHandler.CarteRequest> carteReqCaptor = ArgumentCaptor.forClass(CarteRequest.class);

    BaseCartePlugin baseCartePlugin;

    @Test
    @SuppressWarnings("deprecation")
    public void testDoGet() throws Exception {
        baseCartePlugin.doGet(req, resp);
        // doGet should delegate to .service
        Mockito.verify(baseCartePlugin).service(req, resp);
    }

    @Test
    public void testService() throws Exception {
        Mockito.when(req.getContextPath()).thenReturn("/Path");
        Mockito.when(baseCartePlugin.getContextPath()).thenReturn("/Path");
        Mockito.when(log.isDebug()).thenReturn(true);
        baseCartePlugin.service(req, resp);
        Mockito.verify(log).logDebug(baseCartePlugin.getService());
        Mockito.verify(baseCartePlugin).handleRequest(carteReqCaptor.capture());
        CarteRequestHandler.CarteRequest carteRequest = carteReqCaptor.getValue();
        testCarteRequest(carteRequest);
        testCarteResponse(carteRequest.respond(200));
    }

    @Test
    public void testGetService() throws Exception {
        Mockito.when(baseCartePlugin.getContextPath()).thenReturn("/Path");
        Assert.assertThat(baseCartePlugin.getService().startsWith("/Path"), CoreMatchers.is(true));
    }
}

