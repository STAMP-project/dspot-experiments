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
package org.apache.camel.example;


import org.apache.camel.example.server.Multiplier;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringJmsClientRemotingServerTest extends TestSupport {
    private static ClassPathXmlApplicationContext appCtx;

    @Test
    public void testCamelRemotingInvocation() {
        // just get the proxy to the service and we as the client can use the "proxy" as it was
        // a local object we are invoking. Camel will under the covers do the remote communication
        // to the remote ActiveMQ server and fetch the response.
        Multiplier multiplier = SpringJmsClientRemotingServerTest.appCtx.getBean("multiplierProxy", Multiplier.class);
        int response = multiplier.multiply(33);
        assertEquals("Get a wrong response", 99, response);
    }
}

