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
package org.apache.camel.dataformat.beanio.csv;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CsvTest extends CamelTestSupport {
    private static final String FIXED_DATA = (("James,Strachan,22" + (LS)) + "Claus,Ibsen,21") + (LS);

    private boolean verbose;

    /* @Test
    public void testMarshal() throws Exception {
    List<Employee> employees = getEmployees();

    MockEndpoint mock = getMockEndpoint("mock:beanio-marshal");
    mock.expectedBodiesReceived(FIXED_DATA);

    template.sendBody("direct:marshal", employees);

    mock.assertIsSatisfied();
    }
     */
    @Test
    public void testUnmarshal() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:beanio-unmarshal");
        mock.expectedMessageCount(2);
        template.sendBody("direct:unmarshal", CsvTest.FIXED_DATA);
        mock.assertIsSatisfied();
        List<Exchange> exchanges = mock.getExchanges();
        if (verbose) {
            for (Exchange exchange : exchanges) {
                Object body = exchange.getIn().getBody();
                log.info("received message {} of class {}", body, body.getClass().getName());
            }
        }
        List<Map> results = new ArrayList<>();
        for (Exchange exchange : exchanges) {
            Map body = exchange.getIn().getBody(Map.class);
            if (body != null) {
                results.add(body);
            }
        }
        CsvTest.assertRecord(results, 0, "James", "Strachan", 22);
        CsvTest.assertRecord(results, 1, "Claus", "Ibsen", 21);
    }
}

