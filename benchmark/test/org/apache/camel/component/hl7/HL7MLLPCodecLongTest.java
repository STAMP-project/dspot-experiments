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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.camel.util.IOHelper;
import org.junit.Test;


/**
 * Unit test for the HL7MLLP Codec.
 */
public class HL7MLLPCodecLongTest extends HL7TestSupport {
    @Test
    public void testSendHL7Message() throws Exception {
        // START SNIPPET: e2
        BufferedReader in = IOHelper.buffered(new InputStreamReader(getClass().getResourceAsStream("/mdm_t02.txt")));
        String line = "";
        String message = "";
        while (line != null) {
            if ((line = in.readLine()) != null) {
                message += line + "\r";
            }
        } 
        message = message.substring(0, ((message.length()) - 1));
        assertEquals(70010, message.length());
        String out = template.requestBody((("mina2:tcp://127.0.0.1:" + (HL7TestSupport.getPort())) + "?sync=true&codec=#hl7codec"), message, String.class);
        assertEquals("some response", out);
        // END SNIPPET: e2
    }
}

