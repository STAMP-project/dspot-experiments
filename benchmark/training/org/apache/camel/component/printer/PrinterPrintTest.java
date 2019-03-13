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
package org.apache.camel.component.printer;


import PrinterEndpoint.JOB_NAME;
import java.util.Map;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Sides;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PrinterPrintTest extends CamelTestSupport {
    @Test
    public void moreThanOneLprEndpoint() throws Exception {
        if (isAwtHeadless()) {
            return;
        }
        int numberOfPrintservicesBefore = PrintServiceLookup.lookupPrintServices(null, null).length;
        // setup javax.print
        PrintService ps1 = Mockito.mock(PrintService.class);
        Mockito.when(ps1.getName()).thenReturn("printer1");
        Mockito.when(ps1.isDocFlavorSupported(ArgumentMatchers.any(DocFlavor.class))).thenReturn(Boolean.TRUE);
        PrintService ps2 = Mockito.mock(PrintService.class);
        Mockito.when(ps2.getName()).thenReturn("printer2");
        boolean res1 = PrintServiceLookup.registerService(ps1);
        assertTrue("PrintService #1 should be registered.", res1);
        boolean res2 = PrintServiceLookup.registerService(ps2);
        assertTrue("PrintService #2 should be registered.", res2);
        PrintService[] pss = PrintServiceLookup.lookupPrintServices(null, null);
        assertEquals("lookup should report two PrintServices.", (numberOfPrintservicesBefore + 2), pss.length);
        DocPrintJob job1 = Mockito.mock(DocPrintJob.class);
        Mockito.when(ps1.createPrintJob()).thenReturn(job1);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start1").to("lpr://localhost/printer1?sendToPrinter=true");
                from("direct:start2").to("lpr://localhost/printer2?sendToPrinter=false");
            }
        });
        context.start();
        // Are there two different PrintConfigurations?
        Map<String, Endpoint> epm = context().getEndpointMap();
        assertEquals("Four endpoints", 4, epm.size());
        Endpoint lp1 = null;
        Endpoint lp2 = null;
        for (Map.Entry<String, Endpoint> ep : epm.entrySet()) {
            if (ep.getKey().contains("printer1")) {
                lp1 = ep.getValue();
            }
            if (ep.getKey().contains("printer2")) {
                lp2 = ep.getValue();
            }
        }
        assertNotNull(lp1);
        assertNotNull(lp2);
        assertEquals("printer1", getConfig().getPrintername());
        assertEquals("printer2", getConfig().getPrintername());
        template.sendBody("direct:start1", "Hello Printer 1");
        context.stop();
        Mockito.verify(job1, Mockito.times(1)).print(ArgumentMatchers.any(Doc.class), ArgumentMatchers.any(PrintRequestAttributeSet.class));
    }

    @Test
    public void printerNameTest() throws Exception {
        if (isAwtHeadless()) {
            return;
        }
        // setup javax.print
        PrintService ps1 = Mockito.mock(PrintService.class);
        Mockito.when(ps1.getName()).thenReturn("MyPrinter\\\\remote\\printer1");
        Mockito.when(ps1.isDocFlavorSupported(ArgumentMatchers.any(DocFlavor.class))).thenReturn(Boolean.TRUE);
        boolean res1 = PrintServiceLookup.registerService(ps1);
        assertTrue("The Remote PrintService #1 should be registered.", res1);
        DocPrintJob job1 = Mockito.mock(DocPrintJob.class);
        Mockito.when(ps1.createPrintJob()).thenReturn(job1);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start1").to("lpr://remote/printer1?sendToPrinter=true");
            }
        });
        context.start();
        template.sendBody("direct:start1", "Hello Printer 1");
        context.stop();
        Mockito.verify(job1, Mockito.times(1)).print(ArgumentMatchers.any(Doc.class), ArgumentMatchers.any(PrintRequestAttributeSet.class));
    }

    /* Test for CAMEL-12890
    Unable to send to remote printer
     */
    @Test
    public void testSendingFileToRemotePrinter() throws Exception {
        // setup javax.print
        PrintService ps1 = Mockito.mock(PrintService.class);
        Mockito.when(ps1.getName()).thenReturn("printer1");
        Mockito.when(ps1.isDocFlavorSupported(ArgumentMatchers.any(DocFlavor.class))).thenReturn(Boolean.TRUE);
        boolean res1 = PrintServiceLookup.registerService(ps1);
        assertTrue("The Remote PrintService #1 should be registered.", res1);
        DocPrintJob job1 = Mockito.mock(DocPrintJob.class);
        Mockito.when(ps1.createPrintJob()).thenReturn(job1);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start1").to("lpr://remote/printer1?sendToPrinter=true");
            }
        });
        context.start();
        template.sendBody("direct:start1", "Hello Printer 1");
        context.stop();
        Mockito.verify(job1, Mockito.times(1)).print(ArgumentMatchers.any(Doc.class), ArgumentMatchers.any(PrintRequestAttributeSet.class));
    }

    @Test
    public void setJobName() throws Exception {
        if (isAwtHeadless()) {
            return;
        }
        getMockEndpoint("mock:output").setExpectedMessageCount(1);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:start").to("lpr://localhost/default").to("mock:output");
            }
        });
        context.start();
        template.sendBodyAndHeader("direct:start", "Hello Printer", JOB_NAME, "Test-Job-Name");
        context.stop();
        assertMockEndpointsSatisfied();
    }

    @Test
    public void printToMiddleTray() throws Exception {
        PrinterEndpoint endpoint = new PrinterEndpoint();
        PrinterConfiguration configuration = new PrinterConfiguration();
        configuration.setHostname("localhost");
        configuration.setPort(631);
        configuration.setPrintername("DefaultPrinter");
        configuration.setMediaSizeName(MediaSizeName.ISO_A4);
        configuration.setInternalSides(Sides.ONE_SIDED);
        configuration.setInternalOrientation(OrientationRequested.PORTRAIT);
        configuration.setMediaTray("middle");
        PrinterProducer producer = new PrinterProducer(endpoint, configuration);
        producer.start();
        PrinterOperations printerOperations = producer.getPrinterOperations();
        PrintRequestAttributeSet attributeSet = printerOperations.getPrintRequestAttributeSet();
        Attribute attribute = attributeSet.get(Media.class);
        assertNotNull(attribute);
        assertTrue((attribute instanceof MediaTray));
        MediaTray mediaTray = ((MediaTray) (attribute));
        assertEquals("middle", mediaTray.toString());
    }

    @Test
    public void printsWithLandscapeOrientation() throws Exception {
        PrinterEndpoint endpoint = new PrinterEndpoint();
        PrinterConfiguration configuration = new PrinterConfiguration();
        configuration.setHostname("localhost");
        configuration.setPort(631);
        configuration.setPrintername("DefaultPrinter");
        configuration.setMediaSizeName(MediaSizeName.ISO_A4);
        configuration.setInternalSides(Sides.ONE_SIDED);
        configuration.setInternalOrientation(OrientationRequested.REVERSE_LANDSCAPE);
        configuration.setMediaTray("middle");
        configuration.setSendToPrinter(false);
        PrinterProducer producer = new PrinterProducer(endpoint, configuration);
        producer.start();
        PrinterOperations printerOperations = producer.getPrinterOperations();
        PrintRequestAttributeSet attributeSet = printerOperations.getPrintRequestAttributeSet();
        Attribute attribute = attributeSet.get(OrientationRequested.class);
        assertNotNull(attribute);
        assertEquals("reverse-landscape", attribute.toString());
    }
}

