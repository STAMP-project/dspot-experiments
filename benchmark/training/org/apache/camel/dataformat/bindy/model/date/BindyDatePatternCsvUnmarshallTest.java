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
package org.apache.camel.dataformat.bindy.model.date;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.Format;
import org.apache.camel.dataformat.bindy.FormattingOptions;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FormatFactories;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.dataformat.bindy.format.factories.AbstractFormatFactory;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration
public class BindyDatePatternCsvUnmarshallTest extends AbstractJUnit4SpringContextTests {
    private static final String URI_MOCK_RESULT = "mock:result";

    private static final String URI_DIRECT_START = "direct:start";

    @Produce(uri = BindyDatePatternCsvUnmarshallTest.URI_DIRECT_START)
    private ProducerTemplate template;

    @EndpointInject(uri = BindyDatePatternCsvUnmarshallTest.URI_MOCK_RESULT)
    private MockEndpoint result;

    private String expected;

    @Test
    @DirtiesContext
    public void testUnMarshallMessage() throws Exception {
        expected = "10,Christian,Mueller,12-24-2013,12-26-2015,01-06-2016 12:14:49,13:15:01,broken";
        result.expectedBodiesReceived(((expected) + "\r\n"));
        template.sendBody(expected);
        result.assertIsSatisfied();
    }

    public static class ContextConfig extends RouteBuilder {
        BindyCsvDataFormat camelDataFormat = new BindyCsvDataFormat(BindyDatePatternCsvUnmarshallTest.Order.class);

        public void configure() {
            // because the marshaler will return an OutputStream
            from(BindyDatePatternCsvUnmarshallTest.URI_DIRECT_START).unmarshal(camelDataFormat).marshal(camelDataFormat).convertBodyTo(String.class).to(BindyDatePatternCsvUnmarshallTest.URI_MOCK_RESULT);
        }
    }

    @CsvRecord(separator = ",")
    @FormatFactories({ BindyDatePatternCsvUnmarshallTest.OrderNumberFormatFactory.class })
    public static class Order {
        @DataField(pos = 1)
        private BindyDatePatternCsvUnmarshallTest.OrderNumber orderNr;

        @DataField(pos = 2)
        private String firstName;

        @DataField(pos = 3)
        private String lastName;

        @DataField(pos = 4, pattern = "MM-dd-yyyy")
        private Date orderDate;

        @DataField(pos = 5, pattern = "MM-dd-yyyy")
        private LocalDate deliveryDate;

        @DataField(pos = 6, pattern = "MM-dd-yyyy HH:mm:ss")
        private LocalDateTime returnedDateTime;

        @DataField(pos = 7, pattern = "HH:mm:ss")
        private LocalTime receivedTime;

        @DataField(pos = 8)
        private BindyDatePatternCsvUnmarshallTest.ReturnReason returnReason;

        public BindyDatePatternCsvUnmarshallTest.OrderNumber getOrderNr() {
            return orderNr;
        }

        public void setOrderNr(BindyDatePatternCsvUnmarshallTest.OrderNumber orderNr) {
            this.orderNr = orderNr;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Date getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(Date orderDate) {
            this.orderDate = orderDate;
        }

        @Override
        public String toString() {
            return (((((((("Model : " + (BindyDatePatternCsvUnmarshallTest.Order.class.getName())) + " : ") + (this.orderNr)) + ", ") + (this.firstName)) + ", ") + (this.lastName)) + ", ") + (String.valueOf(this.orderDate));
        }

        public LocalDate getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(LocalDate deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public LocalDateTime getReturnedDateTime() {
            return returnedDateTime;
        }

        public void setReturnedDateTime(LocalDateTime returnedDateTime) {
            this.returnedDateTime = returnedDateTime;
        }

        public LocalTime getReceivedTime() {
            return receivedTime;
        }

        public void setReceivedTime(LocalTime receivedTime) {
            this.receivedTime = receivedTime;
        }

        public BindyDatePatternCsvUnmarshallTest.ReturnReason getReturnReason() {
            return returnReason;
        }

        public void setReturnReason(BindyDatePatternCsvUnmarshallTest.ReturnReason returnReason) {
            this.returnReason = returnReason;
        }
    }

    public enum ReturnReason {

        broken,
        other;}

    public static class OrderNumber {
        private int orderNr;

        public static BindyDatePatternCsvUnmarshallTest.OrderNumber ofString(String orderNumber) {
            BindyDatePatternCsvUnmarshallTest.OrderNumber result = new BindyDatePatternCsvUnmarshallTest.OrderNumber();
            result.orderNr = Integer.valueOf(orderNumber);
            return result;
        }
    }

    public static class OrderNumberFormatFactory extends AbstractFormatFactory {
        {
            supportedClasses.add(BindyDatePatternCsvUnmarshallTest.OrderNumber.class);
        }

        @Override
        public Format<?> build(FormattingOptions formattingOptions) {
            return new Format<BindyDatePatternCsvUnmarshallTest.OrderNumber>() {
                @Override
                public String format(BindyDatePatternCsvUnmarshallTest.OrderNumber object) throws Exception {
                    return String.valueOf(object.orderNr);
                }

                @Override
                public BindyDatePatternCsvUnmarshallTest.OrderNumber parse(String string) throws Exception {
                    return BindyDatePatternCsvUnmarshallTest.OrderNumber.ofString(string);
                }
            };
        }
    }
}

