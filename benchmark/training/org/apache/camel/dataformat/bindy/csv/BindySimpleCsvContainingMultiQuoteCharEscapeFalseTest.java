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
package org.apache.camel.dataformat.bindy.csv;


import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.util.ConverterUtils;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:resultMarshal1")
    private MockEndpoint mockEndPointMarshal1;

    @EndpointInject(uri = "mock:resultUnMarshal1")
    private MockEndpoint mockEndPointUnMarshal1;

    @EndpointInject(uri = "mock:resultMarshal2")
    private MockEndpoint mockEndPointMarshal2;

    @EndpointInject(uri = "mock:resultUnMarshal2")
    private MockEndpoint mockEndPointUnMarshal2;

    @Test
    public void testMarshallCsvRecordFieldContainingMultiEscapedQuoteChar() throws Exception {
        mockEndPointMarshal1.expectedMessageCount(1);
        mockEndPointMarshal1.expectedBodiesReceived(("\"123\",\"\"\"foo\"\"\",\"10\"" + (ConverterUtils.getStringCarriageReturn("WINDOWS"))));
        BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75191 body = new BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75191();
        body.setFirstField("123");
        body.setSecondField("\"foo\"");
        body.setNumber(new BigDecimal(10));
        template.sendBody("direct:startMarshal1", body);
        assertMockEndpointsSatisfied();
        BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75191 model = mockEndPointUnMarshal1.getReceivedExchanges().get(0).getIn().getBody(BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75191.class);
        assertEquals("123", model.getFirstField());
        assertEquals("\"foo\"", model.getSecondField());
        assertEquals(new BigDecimal(10), model.getNumber());
    }

    @Test
    public void testMarshallCsvRecordFieldContainingMultiNonEscapedQuoteChar() throws Exception {
        mockEndPointMarshal2.expectedMessageCount(1);
        mockEndPointMarshal2.expectedBodiesReceived(("'123','''foo''','10'" + (ConverterUtils.getStringCarriageReturn("WINDOWS"))));
        BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75192 body = new BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75192();
        body.setFirstField("123");
        body.setSecondField("''foo''");
        body.setNumber(new BigDecimal(10));
        template.sendBody("direct:startMarshal2", body);
        assertMockEndpointsSatisfied();
        BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75192 model = mockEndPointUnMarshal2.getReceivedExchanges().get(0).getIn().getBody(BindySimpleCsvContainingMultiQuoteCharEscapeFalseTest.BindyCsvRowFormat75192.class);
        assertEquals("123", model.getFirstField());
        assertEquals("''foo''", model.getSecondField());
        assertEquals(new BigDecimal(10), model.getNumber());
    }

    // from https://issues.apache.org/jira/browse/CAMEL-7519
    @CsvRecord(separator = ",", quote = "\"", quoting = true, quotingEscaped = false)
    public static class BindyCsvRowFormat75191 implements Serializable {
        private static final long serialVersionUID = 1L;

        @DataField(pos = 1)
        private String firstField;

        @DataField(pos = 2)
        private String secondField;

        @DataField(pos = 3, pattern = "########.##")
        private BigDecimal number;

        public String getFirstField() {
            return firstField;
        }

        public void setFirstField(String firstField) {
            this.firstField = firstField;
        }

        public String getSecondField() {
            return secondField;
        }

        public void setSecondField(String secondField) {
            this.secondField = secondField;
        }

        public BigDecimal getNumber() {
            return number;
        }

        public void setNumber(BigDecimal number) {
            this.number = number;
        }
    }

    @CsvRecord(separator = ",", quote = "'", quoting = true, quotingEscaped = false)
    public static class BindyCsvRowFormat75192 implements Serializable {
        private static final long serialVersionUID = 1L;

        @DataField(pos = 1)
        private String firstField;

        @DataField(pos = 2)
        private String secondField;

        @DataField(pos = 3, pattern = "########.##")
        private BigDecimal number;

        public String getFirstField() {
            return firstField;
        }

        public void setFirstField(String firstField) {
            this.firstField = firstField;
        }

        public String getSecondField() {
            return secondField;
        }

        public void setSecondField(String secondField) {
            this.secondField = secondField;
        }

        public BigDecimal getNumber() {
            return number;
        }

        public void setNumber(BigDecimal number) {
            this.number = number;
        }
    }
}

