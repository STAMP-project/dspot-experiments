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
package org.apache.camel.dataformat.bindy.fixed.converter;


import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.bindy.Format;
import org.apache.camel.dataformat.bindy.annotation.BindyConverter;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.FixedLengthRecord;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration
public class BindyConverterTest extends CamelTestSupport {
    public static final String URI_DIRECT_MARSHALL = "direct:marshall";

    public static final String URI_DIRECT_UNMARSHALL = "direct:unmarshall";

    public static final String URI_DIRECT_THROUGH = "direct:through";

    public static final String URI_MOCK_MARSHALL_RESULT = "mock:marshall-result";

    public static final String URI_MOCK_UNMARSHALL_RESULT = "mock:unmarshall-result";

    public static final String URI_MOCK_THROUGH = "mock:through-result";

    // *************************************************************************
    // 
    // *************************************************************************
    @Produce(uri = BindyConverterTest.URI_DIRECT_MARSHALL)
    private ProducerTemplate mtemplate;

    @EndpointInject(uri = BindyConverterTest.URI_MOCK_MARSHALL_RESULT)
    private MockEndpoint mresult;

    @Produce(uri = BindyConverterTest.URI_DIRECT_UNMARSHALL)
    private ProducerTemplate utemplate;

    @EndpointInject(uri = BindyConverterTest.URI_MOCK_UNMARSHALL_RESULT)
    private MockEndpoint uresult;

    @Produce(uri = BindyConverterTest.URI_DIRECT_THROUGH)
    private ProducerTemplate ttemplate;

    @EndpointInject(uri = BindyConverterTest.URI_MOCK_THROUGH)
    private MockEndpoint tresult;

    // *************************************************************************
    // TEST
    // *************************************************************************
    @Test
    @DirtiesContext
    public void testMarshall() throws Exception {
        BindyConverterTest.DataModel rec = new BindyConverterTest.DataModel();
        rec.field1 = "0123456789";
        mresult.expectedBodiesReceived("9876543210\r\n");
        mtemplate.sendBody(rec);
        mresult.assertIsSatisfied();
    }

    @Test
    @DirtiesContext
    public void testUnMarshall() throws Exception {
        utemplate.sendBody("9876543210\r\n");
        uresult.expectedMessageCount(1);
        uresult.assertIsSatisfied();
        // check the model
        Exchange exc = uresult.getReceivedExchanges().get(0);
        BindyConverterTest.DataModel data = exc.getIn().getBody(BindyConverterTest.DataModel.class);
        Assert.assertEquals("0123456789", data.field1);
    }

    @Test
    @DirtiesContext
    public void testRightAlignedNotTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Right aligned, padding not trimmed", data.field1, Is.is("!!!f1"));
    }

    @Test
    @DirtiesContext
    public void testLeftAlignedNotTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Left aligned, padding not trimmed", data.field2, Is.is("f2!!!"));
    }

    @Test
    @DirtiesContext
    public void testRightAlignedTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Right aligned, padding trimmed", data.field3, Is.is("f3"));
    }

    @Test
    @DirtiesContext
    public void testLeftAlignedTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Left aligned, padding trimmed", data.field4, Is.is("f4"));
    }

    @Test
    @DirtiesContext
    public void testRightAlignedRecordPaddingNotTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Right aligned, padding not trimmed", data.field5, Is.is("###f5"));
    }

    @Test
    @DirtiesContext
    public void testLeftAlignedRecordPaddingNotTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Left aligned, padding not trimmed", data.field6, Is.is("f6###"));
    }

    @Test
    @DirtiesContext
    public void testRightAlignedRecordPaddingTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Right aligned, padding trimmed", data.field7, Is.is("f7"));
    }

    @Test
    @DirtiesContext
    public void testLeftAlignedRecordPaddingTrimmed() throws Exception {
        BindyConverterTest.AllCombinations data = sendAndRecieveAllCombinations();
        assertThat("Left aligned, padding trimmed", data.field8, Is.is("f8"));
    }

    // *************************************************************************
    // DATA MODEL
    // *************************************************************************
    @FixedLengthRecord(length = 10, paddingChar = ' ')
    public static class DataModel {
        @DataField(pos = 1, length = 10, trim = true)
        @BindyConverter(BindyConverterTest.CustomConverter.class)
        public String field1;
    }

    public static class CustomConverter implements Format<String> {
        @Override
        public String format(String object) throws Exception {
            return new StringBuilder(object).reverse().toString();
        }

        @Override
        public String parse(String string) throws Exception {
            return new StringBuilder(string).reverse().toString();
        }
    }

    @FixedLengthRecord(length = 60, paddingChar = '#', ignoreMissingChars = true)
    public static class AllCombinations {
        @DataField(pos = 1, length = 5, paddingChar = '!')
        public String field1;

        @DataField(pos = 2, length = 5, paddingChar = '!', align = "L")
        public String field2;

        @DataField(pos = 3, length = 5, paddingChar = '#', trim = true)
        public String field3;

        @DataField(pos = 4, length = 5, paddingChar = '#', align = "L", trim = true)
        public String field4;

        @DataField(pos = 5, length = 5, paddingChar = 0)
        public String field5;

        @DataField(pos = 6, length = 5, paddingChar = 0, align = "L")
        public String field6;

        @DataField(pos = 7, length = 5, paddingChar = 0, trim = true)
        public String field7;

        @DataField(pos = 8, length = 5, paddingChar = 0, align = "L", trim = true)
        public String field8;
    }
}

