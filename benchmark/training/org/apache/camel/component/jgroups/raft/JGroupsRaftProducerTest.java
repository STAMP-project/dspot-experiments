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
package org.apache.camel.component.jgroups.raft;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;


public class JGroupsRaftProducerTest extends JGroupsRaftAbstractTest {
    private static final String CLUSTER_NAME = "JGroupsRaftProducerTest";

    private static final String CONFIGURED_ENDPOINT_URI = ("jgroups-raft:" + (JGroupsRaftProducerTest.CLUSTER_NAME)) + "?raftId=A&channelProperties=raftABC.xml";

    private static final String CONFIGURED_ENDPOINT_URI2 = ("jgroups-raft:" + (JGroupsRaftProducerTest.CLUSTER_NAME)) + "?raftId=B&channelProperties=raftABC.xml";

    private static final String CONFIGURED_ENDPOINT_URI3 = ("jgroups-raft:" + (JGroupsRaftProducerTest.CLUSTER_NAME)) + "?raftId=C&channelProperties=raftABC.xml";

    private static final String MESSAGE = "MESSAGE";

    @Test
    public void shouldSendBody() throws Exception {
        JGroupsRaftEndpoint endpoint = getMandatoryEndpoint(JGroupsRaftProducerTest.CONFIGURED_ENDPOINT_URI, JGroupsRaftEndpoint.class);
        JGroupsRaftEndpoint endpoint2 = getMandatoryEndpoint(JGroupsRaftProducerTest.CONFIGURED_ENDPOINT_URI2, JGroupsRaftEndpoint.class);
        JGroupsRaftEndpoint endpoint3 = getMandatoryEndpoint(JGroupsRaftProducerTest.CONFIGURED_ENDPOINT_URI3, JGroupsRaftEndpoint.class);
        waitForLeader(5, endpoint.getResolvedRaftHandle(), endpoint2.getResolvedRaftHandle(), endpoint3.getResolvedRaftHandle());
        Processor processor = ( exchange) -> exchange.getIn().setBody(MESSAGE.getBytes());
        Exchange res = template.request("direct:start", processor);
        Exchange res2 = template.request("direct:start2", processor);
        Exchange res3 = template.request("direct:start3", processor);
        assertNotNull(res.getIn().getBody());
        checkHeaders(res);
        assertNotNull(res2.getIn().getBody());
        checkHeaders(res2);
        assertNotNull(res3.getIn().getBody());
        checkHeaders(res3);
    }
}

