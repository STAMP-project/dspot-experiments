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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.RoutePolicySupport;
import org.junit.Assert;
import org.junit.Test;


public class FileConsumerSuspendTest extends ContextTestSupport {
    @Test
    public void testConsumeSuspendFile() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBodyAndHeader("file://target/data/suspended", "Bye World", FILE_NAME, "bye.txt");
        template.sendBodyAndHeader("file://target/data/suspended", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // the route is suspended by the policy so we should only receive one
        String[] files = new File("target/data/suspended/").list();
        Assert.assertNotNull(files);
        Assert.assertEquals("The file should exists", 1, files.length);
    }

    private static class MyPolicy extends RoutePolicySupport {
        private int counter;

        public void onExchangeDone(Route route, Exchange exchange) {
            // only stop it at first run
            if (((counter)++) == 0) {
                try {
                    super.stopConsumer(route.getConsumer());
                } catch (Exception e) {
                    handleException(e);
                }
            }
        }
    }
}

