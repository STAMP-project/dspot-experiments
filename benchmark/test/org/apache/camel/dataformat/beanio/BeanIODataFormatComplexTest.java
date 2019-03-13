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
package org.apache.camel.dataformat.beanio;


import java.util.Locale;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.beanio.InvalidRecordException;
import org.beanio.UnexpectedRecordException;
import org.beanio.UnidentifiedRecordException;
import org.junit.Test;


public class BeanIODataFormatComplexTest extends CamelTestSupport {
    private static Locale defaultLocale;

    private final String recordData = (((((((((((((((("0001917A112345.678900           " + (LS)) + "0002374A159303290.020           ") + (LS)) + "0015219B1SECURITY ONE           ") + (LS)) + "END OF SECTION 1                ") + (LS)) + "0076647A10.0000000001           ") + (LS)) + "0135515A1999999999999           ") + (LS)) + "2000815B1SECURITY TWO           ") + (LS)) + "2207122B1SECURITY THR           ") + (LS)) + "END OF FILE 000007              ") + (LS);

    private final String data = ((((("0000000A1030808PRICE            " + (LS)) + "0000000B1030808SECURITY         ") + (LS)) + "HEADER END                      ") + (LS)) + (recordData);

    private final String unExpectedData = ((((((("0000000A1030808PRICE            " + (LS)) + "0000000B1030808SECURITY         ") + (LS)) + "0000000B1030808SECURITY         ") + (LS)) + "HEADER END                      ") + (LS)) + (recordData);

    private final String invalidData = ((((((("0000000A1030808PRICE            " + (LS)) + "0000000B1030808SECURITY         EXTRA DATA") + (LS)) + "0000000B1030808SECURITY         ") + (LS)) + "HEADER END                      ") + (LS)) + (recordData);

    private final String unidentifiedData = ((((((("0000000A1030808PRICE            " + (LS)) + "0000000C1030808SECURITY         ") + (LS)) + "0000000B1030808SECURITY         ") + (LS)) + "HEADER END                      ") + (LS)) + (recordData);

    @Test
    public void testMarshal() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:beanio-marshal");
        mock.expectedBodiesReceived(data);
        template.sendBody("direct:marshal", createTestData(false));
        mock.assertIsSatisfied();
    }

    @Test
    public void testUnmarshal() throws Exception {
        context.setTracing(true);
        MockEndpoint mock = getMockEndpoint("mock:beanio-unmarshal");
        mock.expectedBodiesReceived(createTestData(false));
        template.sendBody("direct:unmarshal", data);
        mock.assertIsSatisfied();
    }

    @Test
    public void testUnmarshalUnexpected() throws Exception {
        Throwable ex = null;
        try {
            template.sendBody("direct:unmarshal", unExpectedData);
        } catch (Exception e) {
            ex = e.getCause();
        }
        assertIsInstanceOf(UnexpectedRecordException.class, ex);
    }

    @Test
    public void testUnmarshalInvalid() throws Exception {
        Throwable ex = null;
        try {
            template.sendBody("direct:unmarshal", invalidData);
        } catch (Exception e) {
            ex = e.getCause();
        }
        assertIsInstanceOf(InvalidRecordException.class, ex);
    }

    @Test
    public void testUnmarshalUnidentifiedIgnore() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:beanio-unmarshal");
        mock.expectedBodiesReceived(createTestData(false));
        template.sendBody("direct:unmarshal-forgiving", unidentifiedData);
        mock.assertIsSatisfied();
    }

    @Test
    public void testUnmarshalUnexpectedIgnore() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:beanio-unmarshal");
        mock.expectedBodiesReceived(createTestData(false));
        template.sendBody("direct:unmarshal-forgiving", unExpectedData);
        mock.assertIsSatisfied();
    }

    @Test
    public void testUnmarshalInvalidIgnore() throws Exception {
        context.setTracing(true);
        MockEndpoint mock = getMockEndpoint("mock:beanio-unmarshal");
        mock.expectedBodiesReceived(createTestData(true));
        template.sendBody("direct:unmarshal-forgiving", invalidData);
        mock.assertIsSatisfied();
    }

    @Test
    public void testUnmarshalUnidentified() throws Exception {
        Throwable ex = null;
        try {
            template.sendBody("direct:unmarshal", unidentifiedData);
        } catch (Exception e) {
            ex = e.getCause();
        }
        assertIsInstanceOf(UnidentifiedRecordException.class, ex);
    }
}

