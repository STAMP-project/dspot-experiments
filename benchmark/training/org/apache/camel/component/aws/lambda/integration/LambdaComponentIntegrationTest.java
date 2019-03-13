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
package org.apache.camel.component.aws.lambda.integration;


import ExchangePattern.InOut;
import LambdaConstants.DESCRIPTION;
import LambdaConstants.HANDLER;
import LambdaConstants.ROLE;
import LambdaConstants.RUNTIME;
import com.amazonaws.services.lambda.model.CreateFunctionResult;
import com.amazonaws.services.lambda.model.DeleteFunctionResult;
import com.amazonaws.services.lambda.model.GetFunctionResult;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Must be manually tested. Provide your own accessKey and secretKey!")
public class LambdaComponentIntegrationTest extends CamelTestSupport {
    @Test
    public void lambdaCreateFunctionTest() throws Exception {
        Exchange exchange = template.send("direct:createFunction", InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(RUNTIME, "nodejs6.10");
                exchange.getIn().setHeader(HANDLER, "GetHelloWithName.handler");
                exchange.getIn().setHeader(DESCRIPTION, "Hello with node.js on Lambda");
                exchange.getIn().setHeader(ROLE, "arn:aws:iam::643534317684:role/lambda-execution-role");
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource("org/apache/camel/component/aws/lambda/function/node/GetHelloWithName.zip").getFile());
                FileInputStream inputStream = new FileInputStream(file);
                exchange.getIn().setBody(IOUtils.toByteArray(inputStream));
            }
        });
        assertNotNull(exchange.getOut().getBody(CreateFunctionResult.class));
        assertEquals(exchange.getOut().getBody(CreateFunctionResult.class).getFunctionName(), "GetHelloWithName");
    }

    @Test
    public void lambdaListFunctionsTest() throws Exception {
        Exchange exchange = template.send("direct:listFunctions", InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertNotNull(exchange.getOut().getBody(ListFunctionsResult.class));
        assertEquals(exchange.getOut().getBody(ListFunctionsResult.class).getFunctions().size(), 3);
    }

    @Test
    public void lambdaGetFunctionTest() throws Exception {
        Exchange exchange = template.send("direct:getFunction", InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
            }
        });
        GetFunctionResult result = exchange.getOut().getBody(GetFunctionResult.class);
        assertNotNull(result);
        assertEquals(result.getConfiguration().getFunctionName(), "GetHelloWithName");
        assertEquals(result.getConfiguration().getRuntime(), "nodejs6.10");
    }

    @Test
    public void lambdaInvokeFunctionTest() throws Exception {
        Exchange exchange = template.send("direct:invokeFunction", InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("{\"name\":\"Camel\"}");
            }
        });
        assertNotNull(exchange.getOut().getBody(String.class));
        assertEquals(exchange.getOut().getBody(String.class), "{\"Hello\":\"Camel\"}");
    }

    @Test
    public void lambdaDeleteFunctionTest() throws Exception {
        Exchange exchange = template.send("direct:deleteFunction", InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
            }
        });
        assertNotNull(exchange.getOut().getBody(DeleteFunctionResult.class));
    }
}

