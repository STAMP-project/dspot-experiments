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
package org.apache.camel.component.rest;


import CollectionFormat.multi;
import RestParamType.header;
import RestParamType.query;
import java.util.Arrays;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.junit.Assert;
import org.junit.Test;


public class FromRestGetTest extends ContextTestSupport {
    @Test
    public void testFromRestModel() throws Exception {
        Assert.assertEquals(getExpectedNumberOfRoutes(), context.getRoutes().size());
        Assert.assertEquals(2, context.getRestDefinitions().size());
        RestDefinition rest = context.getRestDefinitions().get(0);
        Assert.assertNotNull(rest);
        Assert.assertEquals("/say/hello", rest.getPath());
        Assert.assertEquals(1, rest.getVerbs().size());
        ToDefinition to = TestSupport.assertIsInstanceOf(ToDefinition.class, rest.getVerbs().get(0).getTo());
        Assert.assertEquals("direct:hello", to.getUri());
        rest = context.getRestDefinitions().get(1);
        Assert.assertNotNull(rest);
        Assert.assertEquals("/say/bye", rest.getPath());
        Assert.assertEquals(2, rest.getVerbs().size());
        Assert.assertEquals("application/json", rest.getVerbs().get(0).getConsumes());
        Assert.assertEquals(2, rest.getVerbs().get(0).getParams().size());
        Assert.assertEquals(header, rest.getVerbs().get(0).getParams().get(0).getType());
        Assert.assertEquals(query, rest.getVerbs().get(0).getParams().get(1).getType());
        Assert.assertEquals("header param description1", rest.getVerbs().get(0).getParams().get(0).getDescription());
        Assert.assertEquals("header param description2", rest.getVerbs().get(0).getParams().get(1).getDescription());
        Assert.assertEquals("integer", rest.getVerbs().get(0).getParams().get(0).getDataType());
        Assert.assertEquals("string", rest.getVerbs().get(0).getParams().get(1).getDataType());
        Assert.assertEquals(Arrays.asList("1", "2", "3", "4"), rest.getVerbs().get(0).getParams().get(0).getAllowableValues());
        Assert.assertEquals(Arrays.asList("a", "b", "c", "d"), rest.getVerbs().get(0).getParams().get(1).getAllowableValues());
        Assert.assertEquals("1", rest.getVerbs().get(0).getParams().get(0).getDefaultValue());
        Assert.assertEquals("b", rest.getVerbs().get(0).getParams().get(1).getDefaultValue());
        Assert.assertEquals(null, rest.getVerbs().get(0).getParams().get(0).getCollectionFormat());
        Assert.assertEquals(multi, rest.getVerbs().get(0).getParams().get(1).getCollectionFormat());
        Assert.assertEquals("header_count", rest.getVerbs().get(0).getParams().get(0).getName());
        Assert.assertEquals("header_letter", rest.getVerbs().get(0).getParams().get(1).getName());
        Assert.assertEquals(Boolean.TRUE, rest.getVerbs().get(0).getParams().get(0).getRequired());
        Assert.assertEquals(Boolean.FALSE, rest.getVerbs().get(0).getParams().get(1).getRequired());
        Assert.assertEquals("300", rest.getVerbs().get(0).getResponseMsgs().get(0).getCode());
        Assert.assertEquals("rate", rest.getVerbs().get(0).getResponseMsgs().get(0).getHeaders().get(0).getName());
        Assert.assertEquals("Rate limit", rest.getVerbs().get(0).getResponseMsgs().get(0).getHeaders().get(0).getDescription());
        Assert.assertEquals("integer", rest.getVerbs().get(0).getResponseMsgs().get(0).getHeaders().get(0).getDataType());
        Assert.assertEquals("error", rest.getVerbs().get(0).getResponseMsgs().get(1).getCode());
        Assert.assertEquals("test msg", rest.getVerbs().get(0).getResponseMsgs().get(0).getMessage());
        Assert.assertEquals(Integer.class.getCanonicalName(), rest.getVerbs().get(0).getResponseMsgs().get(0).getResponseModel());
        to = TestSupport.assertIsInstanceOf(ToDefinition.class, rest.getVerbs().get(0).getTo());
        Assert.assertEquals("direct:bye", to.getUri());
        // the rest becomes routes and the input is a seda endpoint created by the DummyRestConsumerFactory
        getMockEndpoint("mock:update").expectedMessageCount(1);
        template.sendBody("seda:post-say-bye", "I was here");
        assertMockEndpointsSatisfied();
        String out = template.requestBody("seda:get-say-hello", "Me", String.class);
        Assert.assertEquals("Hello World", out);
        String out2 = template.requestBody("seda:get-say-bye", "Me", String.class);
        Assert.assertEquals("Bye World", out2);
    }
}

