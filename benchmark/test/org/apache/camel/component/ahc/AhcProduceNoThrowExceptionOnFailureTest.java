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
package org.apache.camel.component.ahc;


import Exchange.HTTP_RESPONSE_CODE;
import Exchange.HTTP_RESPONSE_TEXT;
import org.junit.Test;


public class AhcProduceNoThrowExceptionOnFailureTest extends BaseAhcTest {
    @Test
    public void testAhcProduce() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Does not work");
        getMockEndpoint("mock:result").expectedHeaderReceived(HTTP_RESPONSE_CODE, 500);
        getMockEndpoint("mock:result").expectedHeaderReceived(HTTP_RESPONSE_TEXT, "Server Error");
        template.sendBody("direct:start", null);
        assertMockEndpointsSatisfied();
    }
}

