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
package org.apache.camel.component.jms;


import java.io.File;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.FileUtil;
import org.junit.Test;


public class JmsStreamMessageTypeTest extends CamelTestSupport {
    @Test
    public void testStreamType() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        // copy the file
        FileUtil.copyFile(new File("src/test/data/message1.xml"), new File("target/stream/in/message1.xml"));
        assertMockEndpointsSatisfied();
        Object body = getMockEndpoint("mock:result").getReceivedExchanges().get(0).getIn().getBody();
        StreamMessageInputStream is = assertIsInstanceOf(StreamMessageInputStream.class, body);
        // no more bytes should be available on the inputstream
        assertEquals(0, is.available());
        // assert on the content of input versus output file
        String srcContent = context.getTypeConverter().mandatoryConvertTo(String.class, new File("src/test/data/message1.xml"));
        String dstContent = context.getTypeConverter().mandatoryConvertTo(String.class, new File("target/stream/out/message1.xml"));
        assertEquals("both the source and destination files should have the same content", srcContent, dstContent);
    }
}

