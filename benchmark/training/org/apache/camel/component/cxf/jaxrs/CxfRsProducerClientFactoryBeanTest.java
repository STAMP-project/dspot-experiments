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
package org.apache.camel.component.cxf.jaxrs;


import CxfRsProducer.ClientFactoryBeanCache;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;


/**
 *
 */
public class CxfRsProducerClientFactoryBeanTest extends Assert {
    private CamelContext context;

    private AbstractApplicationContext applicationContext;

    @Test
    public void testProducerInOutInterceptors() throws Exception {
        CxfRsEndpoint e = context.getEndpoint("cxfrs://bean://rsClientHttpInterceptors", CxfRsEndpoint.class);
        CxfRsProducer p = new CxfRsProducer(e);
        CxfRsProducer.ClientFactoryBeanCache cache = p.getClientFactoryBeanCache();
        JAXRSClientFactoryBean bean = cache.get("http://localhost:8080/CxfRsProducerClientFactoryBeanInterceptors/");
        List<Interceptor<?>> ins = bean.getInInterceptors();
        Assert.assertEquals(1, ins.size());
        Assert.assertTrue(((ins.get(0)) instanceof LoggingInInterceptor));
        List<Interceptor<?>> outs = bean.getOutInterceptors();
        Assert.assertEquals(1, outs.size());
        Assert.assertTrue(((outs.get(0)) instanceof LoggingOutInterceptor));
    }
}

