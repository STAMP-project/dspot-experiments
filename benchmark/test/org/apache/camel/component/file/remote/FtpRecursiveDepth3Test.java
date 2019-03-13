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
package org.apache.camel.component.file.remote;


import Exchange.FILE_NAME;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class FtpRecursiveDepth3Test extends FtpServerTestSupport {
    @Test
    public void testDepthMin1Max1() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceivedInAnyOrder("a", "b");
        template.sendBodyAndHeader((("ftp://admin@localhost:" + (getPort())) + "/depth3?password=admin"), "a", FILE_NAME, "a.txt");
        template.sendBodyAndHeader((("ftp://admin@localhost:" + (getPort())) + "/depth3?password=admin"), "b", FILE_NAME, "b.txt");
        template.sendBodyAndHeader((("ftp://admin@localhost:" + (getPort())) + "/depth3/foo?password=admin"), "a2", FILE_NAME, "a.txt");
        template.sendBodyAndHeader((("ftp://admin@localhost:" + (getPort())) + "/depth3/foo/bar?password=admin"), "a3", FILE_NAME, "a.txt");
        template.sendBodyAndHeader((("ftp://admin@localhost:" + (getPort())) + "/depth3/bar?password=admin"), "b2", FILE_NAME, "b.txt");
        template.sendBodyAndHeader((("ftp://admin@localhost:" + (getPort())) + "/depth3/bar/foo?password=admin"), "b3", FILE_NAME, "b.txt");
        assertMockEndpointsSatisfied();
    }
}

