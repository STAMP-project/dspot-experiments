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
package org.apache.camel.component.splunk;


import Exchange.BATCH_COMPLETE;
import Exchange.BATCH_SIZE;
import com.splunk.Job;
import com.splunk.JobCollection;
import java.io.InputStream;
import java.util.Map;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.splunk.event.SplunkEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConsumerTest extends SplunkMockTestSupport {
    @Mock
    JobCollection jobCollection;

    @Mock
    Job jobMock;

    @Test
    public void testSearch() throws Exception {
        MockEndpoint searchMock = getMockEndpoint("mock:search-result");
        searchMock.expectedMessageCount(3);
        searchMock.expectedPropertyReceived(BATCH_SIZE, 3);
        Mockito.when(service.getJobs()).thenReturn(jobCollection);
        Mockito.when(jobCollection.create(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(jobMock);
        Mockito.when(jobMock.isDone()).thenReturn(Boolean.TRUE);
        InputStream stream = ConsumerTest.class.getResourceAsStream("/resultsreader_test_data.json");
        Mockito.when(jobMock.getResults(ArgumentMatchers.any())).thenReturn(stream);
        assertMockEndpointsSatisfied();
        SplunkEvent recieved = searchMock.getReceivedExchanges().get(0).getIn().getBody(SplunkEvent.class);
        assertNotNull(recieved);
        Map<String, String> data = recieved.getEventData();
        assertEquals("indexertpool", data.get("name"));
        assertEquals(true, searchMock.getReceivedExchanges().get(2).getProperty(BATCH_COMPLETE, Boolean.class));
        stream.close();
    }
}

