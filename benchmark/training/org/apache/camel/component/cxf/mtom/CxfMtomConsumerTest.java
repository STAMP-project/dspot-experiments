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
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPBinding;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.cxf.mtom_feature.Hello;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CxfMtomConsumerTest extends CamelTestSupport {
    protected static final String MTOM_ENDPOINT_ADDRESS = ("http://localhost:" + (CXFTestSupport.getPort1())) + "/CxfMtomConsumerTest/jaxws-mtom/hello";

    protected static final String MTOM_ENDPOINT_URI = ("cxf://" + (CxfMtomConsumerTest.MTOM_ENDPOINT_ADDRESS)) + "?serviceClass=org.apache.camel.cxf.mtom_feature.Hello";

    private final QName serviceName = new QName("http://apache.org/camel/cxf/mtom_feature", "HelloService");

    @Test
    public void testInvokingService() throws Exception {
        if (MtomTestHelper.isAwtHeadless(null, log)) {
            return;
        }
        Holder<byte[]> photo = new Holder<>("RequestFromCXF".getBytes("UTF-8"));
        Holder<Image> image = new Holder<>(getImage("/java.jpg"));
        Hello port = getPort();
        SOAPBinding binding = ((SOAPBinding) (((BindingProvider) (port)).getBinding()));
        binding.setMTOMEnabled(true);
        port.detail(photo, image);
        assertEquals("ResponseFromCamel", new String(photo.value, "UTF-8"));
        assertNotNull(image.value);
    }
}

