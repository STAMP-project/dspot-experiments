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
package org.apache.camel.component.jetty.file;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.junit.Test;


public class JettyFileConsumerTest extends BaseJettyTest {
    @Test
    public void testSending4K() throws Exception {
        File src = new File("src/test/resources/log4j2.properties");
        testingSendingFile(src);
    }

    @Test
    public void testSendBinaryFile() throws Exception {
        deleteDirectory("target/test");
        File jpg = new File("src/test/resources/java.jpg");
        String response = template.requestBody("http://localhost:{{port}}/myapp/myservice2", jpg, String.class);
        assertEquals("Response should be OK ", "OK", response);
        File des = new File("target/test/java.jpg");
        assertTrue("The uploaded file should exists", des.exists());
        assertEquals("This two file should have same size", jpg.length(), des.length());
    }

    @Test
    public void testSendBinaryFileUsingCamelRoute() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        File jpg = new File("src/test/resources/java.jpg");
        template.sendBodyAndHeader("file://target/binary", jpg, FILE_NAME, "java.jpg");
        assertMockEndpointsSatisfied();
        Thread.sleep(1000);
        File des = new File("target/test/java.jpg");
        assertTrue("The uploaded file should exists", des.exists());
        assertEquals("This two file should have same size", jpg.length(), des.length());
    }
}

