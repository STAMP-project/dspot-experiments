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
package org.apache.camel.component.cxf.mtom;


import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPBinding;
import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.cxf.mtom_feature.Hello;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * Unit test for exercising MTOM enabled end-to-end router in PAYLOAD mode
 */
@ContextConfiguration
public class CxfMtomRouterPayloadModeTest extends AbstractJUnit4SpringContextTests {
    static int port1 = CXFTestSupport.getPort1();

    static int port2 = CXFTestSupport.getPort2();

    @Autowired
    protected CamelContext context;

    private Endpoint endpoint;

    @Test
    public void testInvokingServiceFromCXFClient() throws Exception {
        if (MtomTestHelper.isAwtHeadless(logger, null)) {
            return;
        }
        Holder<byte[]> photo = new Holder<>(MtomTestHelper.REQ_PHOTO_DATA);
        Holder<Image> image = new Holder<>(getImage("/java.jpg"));
        Hello port = getPort();
        SOAPBinding binding = ((SOAPBinding) (((BindingProvider) (port)).getBinding()));
        binding.setMTOMEnabled(true);
        port.detail(photo, image);
        MtomTestHelper.assertEquals(MtomTestHelper.RESP_PHOTO_DATA, photo.value);
        Assert.assertNotNull(image.value);
        if ((image.value) instanceof BufferedImage) {
            Assert.assertEquals(560, ((BufferedImage) (image.value)).getWidth());
            Assert.assertEquals(300, ((BufferedImage) (image.value)).getHeight());
        }
    }
}

