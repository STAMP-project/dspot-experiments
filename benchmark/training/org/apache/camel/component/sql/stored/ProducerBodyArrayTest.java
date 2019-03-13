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
package org.apache.camel.component.sql.stored;


import SqlStoredConstants.SQL_STORED_UPDATE_COUNT;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class ProducerBodyArrayTest extends CamelTestSupport {
    EmbeddedDatabase db;

    @Test
    public void shouldExecuteStoredProcedure() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:query");
        mock.expectedMessageCount(1);
        Integer[] numbers = new Integer[]{ 1, 2 };
        template.requestBody("direct:query", numbers);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getExchanges().get(0);
        assertEquals(Integer.valueOf((-1)), exchange.getIn().getBody(Map.class).get("resultofsub"));
        assertNotNull(exchange.getIn().getHeader(SQL_STORED_UPDATE_COUNT));
    }
}

