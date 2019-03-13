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
package org.apache.camel.component.spring.integration.adapter;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ConfigurationTest {
    private AbstractXmlApplicationContext context;

    @Test
    public void testCamelSourceEndpoint() throws Exception {
        context = new ClassPathXmlApplicationContext("/org/apache/camel/component/spring/integration/adapter/CamelSource.xml");
        CamelSourceAdapter camelSourceA = context.getBean("camelSourceA", CamelSourceAdapter.class);
        Assert.assertNotNull(camelSourceA);
        Assert.assertEquals("Get the wrong request channel name", camelSourceA.getChannel().toString(), "channelA");
        Assert.assertEquals("ExpectReply should be false ", camelSourceA.isExpectReply(), false);
        CamelSourceAdapter camelSourceB = context.getBean("camelSourceB", CamelSourceAdapter.class);
        Assert.assertNotNull(camelSourceB);
        Assert.assertEquals("Get the wrong request channel name", camelSourceB.getChannel().toString(), "channelB");
        Assert.assertEquals("ExpectReply should be true ", camelSourceB.isExpectReply(), true);
    }

    @Test
    public void testCamelTragetEndpoint() throws Exception {
        context = new ClassPathXmlApplicationContext(new String[]{ "/org/apache/camel/component/spring/integration/adapter/CamelTarget.xml" });
        CamelTargetAdapter camelTargetA = context.getBean("camelTargetA", CamelTargetAdapter.class);
        Assert.assertNotNull(camelTargetA);
        Assert.assertEquals("Subscript the wrong CamelEndpointUri", camelTargetA.getCamelEndpointUri(), "direct:EndpointA");
        CamelTargetAdapter camelTargetB = context.getBean("camelTargetB", CamelTargetAdapter.class);
        Assert.assertNotNull(camelTargetB);
        Assert.assertEquals("Subscript the wrong reply channel name", camelTargetB.getReplyChannel().toString(), "channelC");
    }
}

