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
package org.apache.camel.component.syslog;


import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class NettyRfc5425Test extends CamelTestSupport {
    private static String uri;

    private static String uriClient;

    private static int serverPort;

    private final String rfc3164Message = "<165>Aug  4 05:34:00 mymachine myproc[10]: %% It\'s\n         time to make the do-nuts.  %%  Ingredients: Mix=OK, Jelly=OK #\n" + ("         Devices: Mixer=OK, Jelly_Injector=OK, Frier=OK # Transport:\n" + "         Conveyer1=OK, Conveyer2=OK # %%");

    private final String rfc5424Message = "<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8";

    private final String rfc5424WithStructuredData = "<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 " + "[exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"] BOM\'su root\' failed for lonvick on /dev/pts/8";

    @Test
    public void testSendingCamel() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:syslogReceiver");
        MockEndpoint mock2 = getMockEndpoint("mock:syslogReceiver2");
        mock.expectedMessageCount(2);
        mock2.expectedMessageCount(2);
        mock2.expectedBodiesReceived(rfc3164Message, rfc5424Message);
        template.sendBody(NettyRfc5425Test.uriClient, rfc3164Message.getBytes("UTF8"));
        template.sendBody(NettyRfc5425Test.uriClient, rfc5424Message.getBytes("UTF8"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testStructuredData() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:syslogReceiver");
        mock.expectedMessageCount(1);
        template.sendBody("direct:checkStructuredData", rfc5424WithStructuredData);
    }
}

