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
package org.apache.camel.component.file;


import Exchange.FILE_NAME;
import Exchange.OVERRULE_FILE_NAME;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test to verify the writeFileName option
 */
public class FileProduceOverruleExpressionTest extends ContextTestSupport {
    @Test
    public void testNoOverrule() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(FILE_NAME, "hello.txt");
        mock.expectedFileExists("target/data/write/copy-of-hello.txt", "Hello World");
        template.sendBodyAndHeader("direct:start", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOverrule() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived(FILE_NAME, "hello.txt");
        mock.message(0).header(OVERRULE_FILE_NAME).isNull();
        mock.expectedFileExists("target/data/write/copy-of-overruled.txt", "Hello World");
        Map<String, Object> map = new HashMap<>();
        map.put(FILE_NAME, "hello.txt");
        // this header should overrule the endpoint configuration
        map.put(OVERRULE_FILE_NAME, "overruled.txt");
        template.sendBodyAndHeaders("direct:start", "Hello World", map);
        assertMockEndpointsSatisfied();
    }
}

