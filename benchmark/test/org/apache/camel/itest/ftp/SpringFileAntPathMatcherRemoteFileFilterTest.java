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
package org.apache.camel.itest.ftp;


import Exchange.FILE_NAME;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.ftpserver.FtpServer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * Unit testing FTP ant path matcher
 */
@ContextConfiguration
public class SpringFileAntPathMatcherRemoteFileFilterTest extends AbstractJUnit4SpringContextTests {
    private static int ftpPort = AvailablePortFinder.getNextAvailable(20123);

    static {
        // set them as system properties so Spring can use the property placeholder
        // things to set them into the URL's in the spring contexts
        System.setProperty("SpringFileAntPathMatcherRemoteFileFilterTest.ftpPort", Integer.toString(SpringFileAntPathMatcherRemoteFileFilterTest.ftpPort));
    }

    protected FtpServer ftpServer;

    protected String expectedBody = "Godday World";

    @Autowired
    protected ProducerTemplate template;

    @EndpointInject(uri = "ref:myFTPEndpoint")
    protected Endpoint inputFTP;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint result;

    @Test
    public void testAntPatchMatherFilter() throws Exception {
        if (!(canRunOnThisPlatform())) {
            return;
        }
        result.expectedBodiesReceived(expectedBody);
        template.sendBodyAndHeader(inputFTP, "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader(inputFTP, "Bye World", FILE_NAME, "bye.xml");
        template.sendBodyAndHeader(inputFTP, "Bad world", FILE_NAME, "subfolder/badday.txt");
        template.sendBodyAndHeader(inputFTP, "Day world", FILE_NAME, "day.xml");
        template.sendBodyAndHeader(inputFTP, expectedBody, FILE_NAME, "subfolder/foo/godday.txt");
        result.assertIsSatisfied();
    }
}

