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
package org.apache.camel.component.cmis;


import java.util.List;
import org.apache.camel.Consumer;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class CMISConsumerTest extends CMISTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void getAllContentFromServerOrderedFromRootToLeaves() throws Exception {
        resultEndpoint.expectedMessageCount(5);
        Consumer treeBasedConsumer = createConsumerFor(((getUrl()) + "?pageSize=50"));
        treeBasedConsumer.start();
        resultEndpoint.assertIsSatisfied();
        treeBasedConsumer.stop();
        List<Exchange> exchanges = resultEndpoint.getExchanges();
        assertTrue(getNodeNameForIndex(exchanges, 0).equals("RootFolder"));
        assertTrue(getNodeNameForIndex(exchanges, 1).equals("Folder1"));
        assertTrue(getNodeNameForIndex(exchanges, 2).equals("Folder2"));
        assertTrue(getNodeNameForIndex(exchanges, 3).contains(".txt"));
        assertTrue(getNodeNameForIndex(exchanges, 4).contains(".txt"));
    }

    @Test
    public void consumeDocumentsWithQuery() throws Exception {
        resultEndpoint.expectedMessageCount(2);
        Consumer queryBasedConsumer = createConsumerFor(((getUrl()) + "?query=SELECT * FROM cmis:document"));
        queryBasedConsumer.start();
        resultEndpoint.assertIsSatisfied();
        queryBasedConsumer.stop();
    }
}

