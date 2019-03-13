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
package org.apache.camel.component.google.sheets.stream;


import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.camel.Exchange;
import org.apache.camel.component.google.sheets.AbstractGoogleSheetsTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class SheetsStreamConsumerIntegrationTest extends AbstractGoogleSheetsStreamTestSupport {
    private String range = "A1:B2";

    @Test
    public void testConsumeValueRange() throws Exception {
        Spreadsheet testSheet = getSpreadsheetWithTestData();
        context().addRoutes(createGoogleStreamRouteBuilder(testSheet.getSpreadsheetId()));
        context().start();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getReceivedExchanges().get(0);
        Assert.assertTrue(exchange.getIn().getHeaders().containsKey(GoogleSheetsStreamConstants.SPREADSHEET_ID));
        Assert.assertTrue(exchange.getIn().getHeaders().containsKey(GoogleSheetsStreamConstants.RANGE));
        Assert.assertTrue(exchange.getIn().getHeaders().containsKey(GoogleSheetsStreamConstants.MAJOR_DIMENSION));
        Assert.assertEquals(testSheet.getSpreadsheetId(), exchange.getIn().getHeaders().get(GoogleSheetsStreamConstants.SPREADSHEET_ID));
        Assert.assertEquals((((AbstractGoogleSheetsTestSupport.TEST_SHEET) + "!") + (range)), exchange.getIn().getHeaders().get(GoogleSheetsStreamConstants.RANGE));
        Assert.assertEquals("ROWS", exchange.getIn().getHeaders().get(GoogleSheetsStreamConstants.MAJOR_DIMENSION));
        ValueRange values = ((ValueRange) (exchange.getIn().getBody()));
        Assert.assertEquals(2L, values.getValues().size());
        Assert.assertEquals("a1", values.getValues().get(0).get(0));
        Assert.assertEquals("b1", values.getValues().get(0).get(1));
        Assert.assertEquals("a2", values.getValues().get(1).get(0));
        Assert.assertEquals("b2", values.getValues().get(1).get(1));
    }
}

