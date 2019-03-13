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
package org.apache.camel.component.stream;


import StreamConstants.STREAM_COMPLETE;
import StreamConstants.STREAM_INDEX;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class StreamGroupLinesTest extends CamelTestSupport {
    private FileOutputStream fos;

    @Test
    public void testGroupLines() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        mock.setAssertPeriod(1000);
        mock.message(0).header(STREAM_INDEX).isEqualTo(0);
        mock.message(0).header(STREAM_COMPLETE).isEqualTo(false);
        mock.message(1).header(STREAM_INDEX).isEqualTo(1);
        mock.message(1).header(STREAM_COMPLETE).isEqualTo(true);
        assertMockEndpointsSatisfied();
        List<?> list = mock.getExchanges().get(0).getIn().getBody(List.class);
        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
        List<?> list2 = mock.getExchanges().get(1).getIn().getBody(List.class);
        assertEquals(3, list2.size());
        assertEquals("D", list2.get(0));
        assertEquals("E", list2.get(1));
        assertEquals("F", list2.get(2));
    }
}

