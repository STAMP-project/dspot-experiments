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
package org.apache.camel.dataformat.bindy.fixed.number;


import BindyType.Fixed;
import java.math.BigDecimal;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;
import org.apache.camel.model.dataformat.BindyDataFormat;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration
public class BindyNumberTest extends AbstractJUnit4SpringContextTests {
    public static final String URI_DIRECT_MARSHALL = "direct:marshall";

    public static final String URI_DIRECT_UNMARSHALL = "direct:unmarshall";

    public static final String URI_MOCK_MARSHALL_RESULT = "mock:marshall-result";

    public static final String URI_MOCK_UNMARSHALL_RESULT = "mock:unmarshall-result";

    // *************************************************************************
    // 
    // *************************************************************************
    @Produce(uri = BindyNumberTest.URI_DIRECT_MARSHALL)
    private ProducerTemplate mtemplate;

    @EndpointInject(uri = BindyNumberTest.URI_MOCK_MARSHALL_RESULT)
    private MockEndpoint mresult;

    @Produce(uri = BindyNumberTest.URI_DIRECT_UNMARSHALL)
    private ProducerTemplate utemplate;

    @EndpointInject(uri = BindyNumberTest.URI_MOCK_UNMARSHALL_RESULT)
    private MockEndpoint uresult;

    // *************************************************************************
    // TEST
    // *************************************************************************
    @Test
    @DirtiesContext
    public void testMarshall() throws Exception {
        BindyNumberTest.DataModel rec = new BindyNumberTest.DataModel();
        rec.field1 = new BigDecimal(123.45);
        rec.field2 = new BigDecimal(10.0);
        rec.field3 = new BigDecimal(10.0);
        rec.field4 = new Double(10.0);
        rec.field5 = new Double(10.0);
        mresult.expectedBodiesReceived("1234510.00   1010.00   10\r\n");
        mtemplate.sendBody(rec);
        mresult.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void testUnMarshall() throws Exception {
        utemplate.sendBody("1234510.00   1010.00   10");
        uresult.expectedMessageCount(1);
        uresult.assertIsSatisfied();
        // check the model
        Exchange exc = uresult.getReceivedExchanges().get(0);
        BindyNumberTest.DataModel data = exc.getIn().getBody(BindyNumberTest.DataModel.class);
        Assert.assertEquals(123.45, data.field1.doubleValue(), 0.0);
        Assert.assertEquals(10.0, data.field2.doubleValue(), 0.0);
        Assert.assertEquals(10.0, data.field3.doubleValue(), 0.0);
        Assert.assertEquals(10.0, data.field4.doubleValue(), 0.0);
        Assert.assertEquals(10.0, data.field5.doubleValue(), 0.0);
    }

    // *************************************************************************
    // ROUTES
    // *************************************************************************
    public static class ContextConfig extends RouteBuilder {
        public void configure() {
            BindyDataFormat bindy = new BindyDataFormat();
            bindy.setClassType(BindyNumberTest.DataModel.class);
            bindy.setLocale("en");
            bindy.setType(Fixed);
            from(BindyNumberTest.URI_DIRECT_MARSHALL).marshal(bindy).to(BindyNumberTest.URI_MOCK_MARSHALL_RESULT);
            from(BindyNumberTest.URI_DIRECT_UNMARSHALL).unmarshal().bindy(Fixed, BindyNumberTest.DataModel.class).to(BindyNumberTest.URI_MOCK_UNMARSHALL_RESULT);
        }
    }

    // *************************************************************************
    // DATA MODEL
    // *************************************************************************
    @FixedLengthRecord(length = 25, paddingChar = ' ')
    public static class DataModel {
        @DataField(pos = 1, length = 5, precision = 2, impliedDecimalSeparator = true)
        public BigDecimal field1;

        @DataField(pos = 6, length = 5, precision = 2)
        public BigDecimal field2;

        @DataField(pos = 11, length = 5)
        public BigDecimal field3;

        @DataField(pos = 16, length = 5, precision = 2)
        public Double field4;

        @DataField(pos = 21, length = 5)
        public Double field5;
    }
}

