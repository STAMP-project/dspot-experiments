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


import org.junit.Test;


/**
 * Unit test for the HL7MLLP Codec using different start and end bytes.
 */
public class HL7MLLPNettyCodecStandAndEndBytesTest extends HL7TestSupport {
    @Test
    public void testSendHL7Message() throws Exception {
        String line1 = "MSH|^~\\&|MYSENDER|MYRECEIVER|MYAPPLICATION||200612211200||QRY^A19|1234|P|2.4";
        String line2 = "QRD|200612211200|R|I|GetPatient|||1^RD|0101701234|DEM||";
        StringBuilder in = new StringBuilder();
        in.append(line1);
        in.append("\r");
        in.append(line2);
        String out = template.requestBody((("netty4:tcp://127.0.0.1:" + (HL7TestSupport.getPort())) + "?sync=true&decoder=#hl7decoder&encoder=#hl7encoder"), in.toString(), String.class);
        String[] lines = out.split("\r");
        assertEquals("MSH|^~\\&|MYSENDER||||200701011539||ADR^A19||||123", lines[0]);
        assertEquals("MSA|AA|123", lines[1]);
    }
}

