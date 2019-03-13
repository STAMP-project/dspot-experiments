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
package org.apache.camel.dataformat.bindy.number.grouping;


import java.math.BigDecimal;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class BindyBigDecimalGroupingUnmarshallTest extends CamelTestSupport {
    private static final String URI_MOCK_RESULT = "mock:result";

    private static final String URI_DIRECT_START = "direct:start";

    @Produce(uri = BindyBigDecimalGroupingUnmarshallTest.URI_DIRECT_START)
    private ProducerTemplate template;

    @EndpointInject(uri = BindyBigDecimalGroupingUnmarshallTest.URI_MOCK_RESULT)
    private MockEndpoint result;

    private String record;

    @Test
    public void testBigDecimalPattern() throws Exception {
        record = "'123.456,234'";
        String bigDecimal = "123456.24";
        template.sendBody(record);
        result.expectedMessageCount(1);
        result.assertIsSatisfied();
        BindyBigDecimalGroupingUnmarshallTest.NumberModel bd = ((BindyBigDecimalGroupingUnmarshallTest.NumberModel) (result.getExchanges().get(0).getIn().getBody()));
        Assert.assertEquals(bigDecimal, bd.getGrouping().toString());
    }

    @CsvRecord(separator = ",", quote = "'")
    public static class NumberModel {
        @DataField(pos = 1, precision = 2, rounding = "CEILING", pattern = "###,###.###", decimalSeparator = ",", groupingSeparator = ".")
        private BigDecimal grouping;

        public BigDecimal getGrouping() {
            return grouping;
        }

        public void setGrouping(BigDecimal grouping) {
            this.grouping = grouping;
        }

        @Override
        public String toString() {
            return "bigDecimal grouping : " + (this.grouping);
        }
    }
}

