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
package org.apache.camel.service.lra;


import java.util.Collections;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class LRAManualIT extends AbstractLRATestSupport {
    @Test
    public void testCompletion() throws InterruptedException {
        MockEndpoint completeEndpoint = getMockEndpoint("mock:complete");
        completeEndpoint.expectedMessageCount(1);
        completeEndpoint.expectedHeaderReceived("id", "1");
        sendBody("direct:saga", "hello", Collections.singletonMap("myid", "1"));
        completeEndpoint.assertIsSatisfied();
    }

    @Test
    public void testFailure() throws InterruptedException {
        MockEndpoint compensateEndpoint = getMockEndpoint("mock:compensate");
        compensateEndpoint.expectedMessageCount(1);
        sendBody("direct:saga", "fail");
        compensateEndpoint.assertIsSatisfied();
    }
}

