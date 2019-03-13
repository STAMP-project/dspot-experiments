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
package org.apache.camel.dataformat.barcode;


import BarcodeFormat.AZTEC;
import BarcodeFormat.PDF_417;
import BarcodeFormat.QR_CODE;
import BarcodeImageType.PNG;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests all Camel dependend cases for {@link BarcodeDataFormat}.
 */
public class BarcodeDataFormatCamelTest extends BarcodeTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(BarcodeDataFormatCamelTest.class);

    /**
     * tests barcode (QR-Code) generation and reading.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDefaultQRCode() throws Exception {
        out.reset();
        out.expectedBodiesReceived(BarcodeTestBase.MSG);
        image.expectedMessageCount(1);
        template.sendBody("direct:code1", BarcodeTestBase.MSG);
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
        this.checkImage(image, 100, 100, PNG.toString(), QR_CODE);
    }

    /**
     * tests barcode (QR-Code) generation with modified size and reading.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testQRCodeWithModifiedSize() throws Exception {
        out.reset();
        out.expectedBodiesReceived(BarcodeTestBase.MSG);
        image.expectedMessageCount(1);
        template.sendBody("direct:code2", BarcodeTestBase.MSG);
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
        this.checkImage(image, 200, 200, PNG.toString(), QR_CODE);
    }

    /**
     * tests barcode (QR-Code) generation with modified image type and reading.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testQRCodeWithJPEGType() throws Exception {
        out.reset();
        out.expectedBodiesReceived(BarcodeTestBase.MSG);
        image.expectedMessageCount(1);
        template.sendBody("direct:code3", BarcodeTestBase.MSG);
        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
        this.checkImage(image, 100, 100, "JPEG", QR_CODE);
    }

    /**
     * tests barcode (PDF-417) with modiefied size and image taype generation and reading.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPDF417CodeWidthModifiedSizeAndImageType() throws Exception {
        out.reset();
        out.expectedBodiesReceived(BarcodeTestBase.MSG);
        image.expectedMessageCount(1);
        template.sendBody("direct:code4", BarcodeTestBase.MSG);
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);
        this.checkImage(image, "JPEG", PDF_417);
    }

    /**
     * tests barcode (AZTEC).
     *
     * @throws Exception
     * 		
     * @see CAMEL-7681
     */
    @Test
    public void testAZTECWidthModifiedSizeAndImageType() throws Exception {
        out.reset();
        out.expectedBodiesReceived(BarcodeTestBase.MSG);
        image.expectedMessageCount(1);
        template.sendBody("direct:code5", BarcodeTestBase.MSG);
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);
        this.checkImage(image, 200, 200, "PNG", AZTEC);
    }
}

