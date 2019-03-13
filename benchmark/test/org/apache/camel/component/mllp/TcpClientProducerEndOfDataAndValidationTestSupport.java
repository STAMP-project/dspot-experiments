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
package org.apache.camel.component.mllp;


import MllpConstants.MLLP_ACKNOWLEDGEMENT;
import MllpConstants.MLLP_ACKNOWLEDGEMENT_STRING;
import MllpConstants.MLLP_ACKNOWLEDGEMENT_TYPE;
import java.util.concurrent.TimeUnit;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit.rule.mllp.MllpServerResource;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.mllp.Hl7TestMessageGenerator;
import org.junit.Rule;
import org.junit.Test;


public abstract class TcpClientProducerEndOfDataAndValidationTestSupport extends CamelTestSupport {
    static final int RECEIVE_TIMEOUT = 1000;

    static final int READ_TIMEOUT = 500;

    static final String TEST_MESSAGE = (((((((((((((((((((((((((("MSH|^~\\&|ADT|EPIC|JCAPS|CC|20161206193919|RISTECH|ADT^A08|00001|D|2.3^^|||||||" + '\r') + "EVN|A08|20150107161440||REG_UPDATE_SEND_VISIT_MESSAGES_ON_PATIENT_CHANGES|RISTECH^RADIOLOGY^TECHNOLOGIST^^^^^^UCLA^^^^^RRMC||") + '\r') + "PID|1|2100355^^^MRN^MRN|2100355^^^MRN^MRN||MDCLS9^MC9||19700109|F||U|111 HOVER STREET^^LOS ANGELES^CA^90032^USA^P^^LOS ANGELE|LOS ANGELE|") + "(310)725-6952^P^PH^^^310^7256952||ENGLISH|U||60000013647|565-33-2222|||U||||||||N||") + '\r') + "PD1|||UCLA HEALTH SYSTEM^^10|10002116^ADAMS^JOHN^D^^^^^EPIC^^^^PROVID||||||||||||||") + '\r') + "NK1|1|DOE^MC9^^|OTH|^^^^^USA|(310)888-9999^^^^^310^8889999|(310)999-2222^^^^^310^9992222|Emergency Contact 1|||||||||||||||||||||||||||") + '\r') + "PV1|1|OUTPATIENT|RR CT^^^1000^^^^^^^DEPID|EL|||017511^TOBIAS^JONATHAN^^^^^^EPIC^^^^PROVID|017511^TOBIAS^JONATHAN^^^^^^EPIC^^^^PROVID||||||") + "CLR|||||60000013647|SELF|||||||||||||||||||||HOV_CONF|^^^1000^^^^^^^||20150107161438||||||||||") + '\r') + "PV2||||||||20150107161438||||CT BRAIN W WO CONTRAST||||||||||N|||||||||||||||||||||||||||") + '\r') + "ZPV||||||||||||20150107161438|||||||||") + '\r') + "AL1|1||33361^NO KNOWN ALLERGIES^^NOTCOMPUTRITION^NO KNOWN ALLERGIES^EXTELG||||||") + '\r') + "DG1|1|DX|784.0^Headache^DX|Headache||VISIT") + '\r') + "GT1|1|1000235129|MDCLS9^MC9^^||111 HOVER STREET^^LOS ANGELES^CA^90032^USA^^^LOS ANGELE|(310)725-6952^^^^^310^7256952||19700109|F|P/F|SLF|") + "565-33-2222|||||^^^^^USA|||UNKNOWN|||||||||||||||||||||||||||||") + '\r') + "UB2||||||||") + '\r') + '\n';

    static final String EXPECTED_AA = ((("MSH|^~\\&|JCAPS|CC|ADT|EPIC|20161206193919|RISTECH|ACK^A08|00001|D|2.3^^|||||||" + '\r') + "MSA|AA|00001|") + '\r') + '\n';

    static final String EXPECTED_AR = ((("MSH|^~\\&|JCAPS|CC|ADT|EPIC|20161206193919|RISTECH|ACK^A08|00001|D|2.3^^|||||||" + '\r') + "MSA|AR|00001|") + '\r') + '\n';

    static final String EXPECTED_AE = ((("MSH|^~\\&|JCAPS|CC|ADT|EPIC|20161206193919|RISTECH|ACK^A08|00001|D|2.3^^|||||||" + '\r') + "MSA|AE|00001|") + '\r') + '\n';

    @Rule
    public MllpServerResource mllpServer = new MllpServerResource("localhost", AvailablePortFinder.getNextAvailable());

    @EndpointInject(uri = "direct://source")
    protected ProducerTemplate source;

    @EndpointInject(uri = "mock://aa-ack")
    protected MockEndpoint aa;

    @EndpointInject(uri = "mock://ae-nack")
    protected MockEndpoint ae;

    @EndpointInject(uri = "mock://ar-nack")
    protected MockEndpoint ar;

    @EndpointInject(uri = "mock://invalid-ack")
    protected MockEndpoint invalid;

    @EndpointInject(uri = "mock://ack-receive-error")
    protected MockEndpoint ackReceiveError;

    @EndpointInject(uri = "mock://ack-timeout-error")
    protected MockEndpoint ackTimeoutError;

    @EndpointInject(uri = "mock://failed")
    protected MockEndpoint failed;

    protected int expectedAACount;

    protected int expectedAECount;

    protected int expectedARCount;

    protected int expectedInvalidCount;

    protected int expectedReceiveErrorCount;

    protected int expectedTimeoutCount;

    protected int expectedFailedCount;

    @Test
    public void testSendSingleMessageWithEndOfDataByte() throws Exception {
        aa.expectedMessageCount(1);
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
    }

    @Test
    public void testSendMultipleMessagesWithEndOfDataByte() throws Exception {
        expectedAACount = 5;
        setExpectedCounts();
        NotifyBuilder[] complete = new NotifyBuilder[expectedAACount];
        for (int i = 0; i < (expectedAACount); ++i) {
            complete[i] = whenDone((i + 1)).create();
        }
        for (int i = 0; i < (expectedAACount); ++i) {
            source.sendBody(Hl7TestMessageGenerator.generateMessage((i + 1)));
            assertTrue((("Messege " + i) + " not completed"), complete[i].matches(1, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testNoResponseOnFirstMessage() throws Exception {
        int sendMessageCount = 5;
        expectedAACount = sendMessageCount - 1;
        expectedTimeoutCount = 1;
        setExpectedCounts();
        NotifyBuilder[] complete = new NotifyBuilder[sendMessageCount];
        for (int i = 0; i < sendMessageCount; ++i) {
            complete[i] = whenDone((i + 1)).create();
        }
        mllpServer.disableResponse();
        source.sendBody(Hl7TestMessageGenerator.generateMessage(1));
        assertTrue("Messege 1 not completed", complete[0].matches(1, TimeUnit.SECONDS));
        mllpServer.enableResponse();
        for (int i = 1; i < sendMessageCount; ++i) {
            source.sendBody(Hl7TestMessageGenerator.generateMessage((i + 1)));
            assertTrue((("Messege " + i) + " not completed"), complete[i].matches(1, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testNoResponseOnNthMessage() throws Exception {
        int sendMessageCount = 3;
        expectedAACount = sendMessageCount - 1;
        expectedTimeoutCount = 1;
        setExpectedCounts();
        NotifyBuilder[] complete = new NotifyBuilder[sendMessageCount];
        for (int i = 0; i < sendMessageCount; ++i) {
            complete[i] = whenDone((i + 1)).create();
        }
        mllpServer.disableResponse(sendMessageCount);
        for (int i = 0; i < sendMessageCount; ++i) {
            source.sendBody(Hl7TestMessageGenerator.generateMessage((i + 1)));
            assertTrue((("Messege " + i) + " not completed"), complete[i].matches(1, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testApplicationAcceptAcknowledgement() throws Exception {
        setExpectedCounts();
        aa.expectedBodiesReceived(TcpClientProducerEndOfDataAndValidationTestSupport.TEST_MESSAGE);
        aa.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT_TYPE, "AA");
        aa.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT, TcpClientProducerEndOfDataAndValidationTestSupport.EXPECTED_AA.getBytes());
        aa.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT_STRING, TcpClientProducerEndOfDataAndValidationTestSupport.EXPECTED_AA);
        source.sendBody(TcpClientProducerEndOfDataAndValidationTestSupport.TEST_MESSAGE);
    }

    @Test
    public void testApplicationRejectAcknowledgement() throws Exception {
        setExpectedCounts();
        ar.expectedBodiesReceived(TcpClientProducerEndOfDataAndValidationTestSupport.TEST_MESSAGE);
        ar.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT_TYPE, "AR");
        ar.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT, TcpClientProducerEndOfDataAndValidationTestSupport.EXPECTED_AR.getBytes());
        ar.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT_STRING, TcpClientProducerEndOfDataAndValidationTestSupport.EXPECTED_AR);
        mllpServer.setSendApplicationRejectAcknowledgementModulus(1);
        source.sendBody(TcpClientProducerEndOfDataAndValidationTestSupport.TEST_MESSAGE);
    }

    @Test
    public void testApplicationErrorAcknowledgement() throws Exception {
        setExpectedCounts();
        ae.expectedBodiesReceived(TcpClientProducerEndOfDataAndValidationTestSupport.TEST_MESSAGE);
        ae.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT_TYPE, "AE");
        ae.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT, TcpClientProducerEndOfDataAndValidationTestSupport.EXPECTED_AE.getBytes());
        ae.expectedHeaderReceived(MLLP_ACKNOWLEDGEMENT_STRING, TcpClientProducerEndOfDataAndValidationTestSupport.EXPECTED_AE);
        mllpServer.setSendApplicationErrorAcknowledgementModulus(1);
        source.sendBody(TcpClientProducerEndOfDataAndValidationTestSupport.TEST_MESSAGE);
    }

    @Test
    public void testAcknowledgementReceiveTimeout() throws Exception {
        setExpectedCounts();
        ackTimeoutError.expectedMessageCount(1);
        mllpServer.disableResponse(1);
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
    }

    @Test
    public void testAcknowledgementReadTimeout() throws Exception {
        setExpectedCounts();
        ackTimeoutError.expectedMessageCount(1);
        mllpServer.setDelayDuringAcknowledgement(15000);
        source.sendBody(Hl7TestMessageGenerator.generateMessage());
    }

    @Test
    public void testMissingEndOfBlockByte() throws Exception {
        int sendMessageCount = 3;
        expectedAACount = sendMessageCount - 1;
        expectedTimeoutCount = 1;
        setExpectedCounts();
        NotifyBuilder[] complete = new NotifyBuilder[sendMessageCount];
        for (int i = 0; i < sendMessageCount; ++i) {
            complete[i] = whenDone((i + 1)).create();
        }
        mllpServer.setExcludeEndOfBlockModulus(sendMessageCount);
        for (int i = 0; i < sendMessageCount; ++i) {
            source.sendBody(Hl7TestMessageGenerator.generateMessage((i + 1)));
            assertTrue((("Messege " + i) + " not completed"), complete[i].matches(1, TimeUnit.SECONDS));
        }
    }
}

