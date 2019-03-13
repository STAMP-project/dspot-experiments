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
package org.apache.camel.component.sql;


import SqlConstants.SQL_ROW_COUNT;
import SqlConstants.SQL_UPDATE_COUNT;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class SqlProducerNoopTest extends CamelTestSupport {
    private EmbeddedDatabase db;

    @Test
    public void testInsertNoop() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:insert");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(SQL_UPDATE_COUNT, 1);
        mock.message(0).body().isEqualTo("Hi there!");
        template.requestBody("direct:insert", "Hi there!");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testQueryNoop() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:query");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(SQL_ROW_COUNT, 3);
        mock.message(0).body().isEqualTo("Hi there!");
        template.requestBody("direct:query", "Hi there!");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testUpdateNoop() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:update");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(SQL_UPDATE_COUNT, 1);
        mock.message(0).body().isEqualTo("Hi there!");
        template.requestBody("direct:update", "Hi there!");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDeleteNoop() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:delete");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(SQL_UPDATE_COUNT, 1);
        mock.message(0).body().isEqualTo("Hi there!");
        template.requestBody("direct:delete", "Hi there!");
        assertMockEndpointsSatisfied();
    }
}

