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
package org.apache.camel.component.bean;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.bean.issues.PrivateClasses;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Tests Bean binding for private & package-private classes where the target method is accessible through an interface.
 */
public final class BeanPrivateClassWithInterfaceMethodTest extends ContextTestSupport {
    private static final String INPUT_BODY = "Whatever";

    private final PrivateClasses.HelloCamel packagePrivateImpl = PrivateClasses.newPackagePrivateHelloCamel();

    private final PrivateClasses.HelloCamel privateImpl = PrivateClasses.newPrivateHelloCamel();

    @Test
    public void testPackagePrivateClassBinding() throws InterruptedException {
        MockEndpoint mockResult = getMockEndpoint("mock:packagePrivateClassResult");
        mockResult.setExpectedMessageCount(1);
        mockResult.message(0).body().isEqualTo(PrivateClasses.EXPECTED_OUTPUT);
        template.sendBody("direct:testPackagePrivateClass", BeanPrivateClassWithInterfaceMethodTest.INPUT_BODY);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testPrivateClassBinding() throws InterruptedException {
        MockEndpoint mockResult = getMockEndpoint("mock:privateClassResult");
        mockResult.setExpectedMessageCount(1);
        mockResult.message(0).body().isEqualTo(PrivateClasses.EXPECTED_OUTPUT);
        template.sendBody("direct:testPrivateClass", BeanPrivateClassWithInterfaceMethodTest.INPUT_BODY);
        assertMockEndpointsSatisfied();
    }
}

