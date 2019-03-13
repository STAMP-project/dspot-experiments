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
import java.net.MalformedURLException;
import java.net.URL;
import javax.activation.DataHandler;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;


public class SqlProducerOutputAttachment extends CamelTestSupport {
    private EmbeddedDatabase db;

    @Test
    public void testHeaderAndAttachmentAreAvailableAfterProducer() throws InterruptedException, MalformedURLException {
        MockEndpoint mock = getMockEndpoint("mock:query");
        DataHandler content = new DataHandler(new URL("http://www.nu.nl"));
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(SQL_ROW_COUNT, 1);
        mock.expectedHeaderReceived("TheProjectID", 1);
        mock.expectedHeaderReceived("maintain", "this");
        mock.expects(() -> {
            assertThat(mock.getReceivedExchanges().get(0).getIn().getAttachments().size(), is(1));
            assertThat(mock.getReceivedExchanges().get(0).getIn().getAttachment("att1"), notNullValue());
            assertThat(mock.getReceivedExchanges().get(0).getIn().getAttachment("att1"), is(content));
        });
        mock.message(0).body().isEqualTo("Hi there!");
        Exchange exchange = context.getEndpoint("direct:query").createExchange();
        exchange.getIn().setBody("Hi there!");
        exchange.getIn().setHeader("myProject", "Camel");
        exchange.getIn().setHeader("maintain", "this");
        exchange.getIn().addAttachment("att1", content);
        template.send("direct:query", exchange);
        assertMockEndpointsSatisfied();
    }
}

