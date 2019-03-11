/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.snmp.processors;


import java.util.concurrent.atomic.AtomicLong;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.junit.Assert;
import org.junit.Test;
import org.snmp4j.PDU;

import static SNMPUtils.SNMP_PROP_PREFIX;


/**
 * Test class for {@link SNMPUtils}.
 */
public class SNMPUtilsTest {
    /**
     * Test for updating attributes of flow files with {@link PDU}
     */
    @Test
    public void validateUpdateFlowFileAttributes() {
        GetSNMP processor = new GetSNMP();
        ProcessSession processSession = new org.apache.nifi.util.MockProcessSession(new org.apache.nifi.util.SharedSessionState(processor, new AtomicLong()), processor);
        FlowFile sourceFlowFile = processSession.create();
        PDU pdu = new PDU();
        pdu.setErrorIndex(0);
        pdu.setErrorStatus(0);
        pdu.setType(4);
        FlowFile f2 = SNMPUtils.updateFlowFileAttributesWithPduProperties(pdu, sourceFlowFile, processSession);
        Assert.assertEquals("0", f2.getAttributes().get(((SNMP_PROP_PREFIX) + "errorIndex")));
        Assert.assertEquals("0", f2.getAttributes().get(((SNMP_PROP_PREFIX) + "errorStatus")));
        Assert.assertEquals("4", f2.getAttributes().get(((SNMP_PROP_PREFIX) + "type")));
    }
}

