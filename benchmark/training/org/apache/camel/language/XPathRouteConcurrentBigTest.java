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
package org.apache.camel.language;


import org.apache.camel.ContextTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XPathRouteConcurrentBigTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(XPathRouteConcurrentBigTest.class);

    private static final String XMLTEST1 = "<message><messageType>AAA</messageType><sender>0123456789101112131415</sender>" + (("<rawData>Uyw7TSVkUMxUyw7TSgGUMQAyw7TSVkUMxUyA7TSgGUMQAyw7TSVkUMxUyA</rawData>" + "<sentDate>2009-10-12T12:22:02+02:00</sentDate> <receivedDate>2009-10-12T12:23:31.248+02:00</receivedDate>") + "<intproperty>1</intproperty><stringproperty>aaaaaaabbbbbbbccccccccdddddddd</stringproperty></message>");

    private static final String XMLTEST2 = "<message><messageType>AAB</messageType><sender>0123456789101112131415</sender>" + (("<rawData>Uyw7TSVkUMxUyw7TSgGUMQAyw7TSVkUMxUyA7TSgGUMQAyw7TSVkUMxUyA</rawData>" + "<sentDate>2009-10-12T12:22:02+02:00</sentDate> <receivedDate>2009-10-12T12:23:31.248+02:00</receivedDate>") + "<intproperty>1</intproperty><stringproperty>aaaaaaabbbbbbbccccccccdddddddd</stringproperty></message>");

    private static final String XMLTEST3 = "<message><messageType>ZZZ</messageType><sender>0123456789101112131415</sender>" + (("<rawData>Uyw7TSVkUMxUyw7TSgGUMQAyw7TSVkUMxUyA7TSgGUMQAyw7TSVkUMxUyA</rawData>" + "<sentDate>2009-10-12T12:22:02+02:00</sentDate> <receivedDate>2009-10-12T12:23:31.248+02:00</receivedDate>") + "<intproperty>1</intproperty><stringproperty>aaaaaaabbbbbbbccccccccdddddddd</stringproperty></message>");

    @Test
    public void testConcurrent() throws Exception {
        doSendMessages(333);
    }
}

