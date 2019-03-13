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
package org.apache.camel.component.hl7;


import ErrorCode.APPLICATION_INTERNAL_ERROR;
import ErrorCode.DATA_TYPE_ERROR;
import ca.uhn.hl7v2.model.v24.message.ACK;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class AckExpressionTest extends CamelTestSupport {
    @Test
    public void testAckExpression() throws Exception {
        ADT_A01 a01 = AckExpressionTest.createADT01Message();
        ACK ack = template.requestBody("direct:test1", a01, ACK.class);
        assertEquals("AA", ack.getMSA().getAcknowledgementCode().getValue());
        assertEquals(a01.getMSH().getMessageControlID().getValue(), ack.getMSA().getMessageControlID().getValue());
    }

    @Test
    public void testAckExpressionWithCode() throws Exception {
        ADT_A01 a01 = AckExpressionTest.createADT01Message();
        ACK ack = template.requestBody("direct:test2", a01, ACK.class);
        assertEquals("CA", ack.getMSA().getAcknowledgementCode().getValue());
        assertEquals(a01.getMSH().getMessageControlID().getValue(), ack.getMSA().getMessageControlID().getValue());
    }

    @Test
    public void testNakExpression() throws Exception {
        ADT_A01 a01 = AckExpressionTest.createADT01Message();
        ACK ack = template.requestBody("direct:test3", a01, ACK.class);
        assertEquals("AE", ack.getMSA().getAcknowledgementCode().getValue());
        assertEquals(a01.getMSH().getMessageControlID().getValue(), ack.getMSA().getMessageControlID().getValue());
        assertEquals(String.valueOf(APPLICATION_INTERNAL_ERROR.getCode()), ack.getERR().getErrorCodeAndLocation(0).getCodeIdentifyingError().getIdentifier().getValue());
    }

    @Test
    public void testNakExpressionWithParameters() throws Exception {
        ADT_A01 a01 = AckExpressionTest.createADT01Message();
        ACK ack = template.requestBody("direct:test4", a01, ACK.class);
        assertEquals("AR", ack.getMSA().getAcknowledgementCode().getValue());
        assertEquals(a01.getMSH().getMessageControlID().getValue(), ack.getMSA().getMessageControlID().getValue());
        assertEquals(String.valueOf(APPLICATION_INTERNAL_ERROR.getCode()), ack.getERR().getErrorCodeAndLocation(0).getCodeIdentifyingError().getIdentifier().getValue());
        assertEquals("Problem!", ack.getERR().getErrorCodeAndLocation(0).getCodeIdentifyingError().getAlternateText().getValue());
    }

    @Test
    public void testNakExpressionWithoutException() throws Exception {
        ADT_A01 a01 = AckExpressionTest.createADT01Message();
        ACK ack = template.requestBody("direct:test5", a01, ACK.class);
        assertEquals("AR", ack.getMSA().getAcknowledgementCode().getValue());
        assertEquals(a01.getMSH().getMessageControlID().getValue(), ack.getMSA().getMessageControlID().getValue());
        assertEquals(String.valueOf(DATA_TYPE_ERROR.getCode()), ack.getERR().getErrorCodeAndLocation(0).getCodeIdentifyingError().getIdentifier().getValue());
        assertEquals("Problem!", ack.getERR().getErrorCodeAndLocation(0).getCodeIdentifyingError().getAlternateText().getValue());
    }
}

