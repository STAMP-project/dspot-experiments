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
import java.util.Date;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class FileProducerChmodOptionTest extends ContextTestSupport {
    public static final String TEST_DIRECTORY = "target/data/fileProducerChmodOptionTest/";

    @Test
    public void testWriteValidChmod0755() throws Exception {
        if (!(canTest())) {
            return;
        }
        runChmodCheck("0755", "rwxr-xr-x");
    }

    @Test
    public void testWriteValidChmod666() throws Exception {
        if (!(canTest())) {
            return;
        }
        runChmodCheck("666", "rw-rw-rw-");
    }

    @Test
    public void testInvalidChmod() throws Exception {
        if (!(canTest())) {
            return;
        }
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:writeBadChmod1").to((("file://" + (FileProducerChmodOptionTest.TEST_DIRECTORY)) + "?chmod=abc")).to("mock:badChmod1");
                }
            });
            Assert.fail("Expected FailedToCreateRouteException");
        } catch (Exception e) {
            Assert.assertTrue(("Expected FailedToCreateRouteException, was " + (e.getClass().getCanonicalName())), (e instanceof FailedToCreateRouteException));
            Assert.assertTrue((("Message was [" + (e.getMessage())) + "]"), e.getMessage().endsWith("conversion possible: chmod option [abc] is not valid"));
        }
    }

    /**
     * Write a file without chmod set, should work normally and not throw an exception for invalid chmod value
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteNoChmod() throws Exception {
        if (!(canTest())) {
            return;
        }
        MockEndpoint mock = getMockEndpoint("mock:noChmod");
        mock.expectedMessageCount(1);
        String testFileName = "noChmod.txt";
        String fullTestFileName = (FileProducerChmodOptionTest.TEST_DIRECTORY) + testFileName;
        String testFileContent = "Writing file with no chmod option at " + (new Date());
        mock.expectedFileExists(fullTestFileName, testFileContent);
        template.sendBodyAndHeader("direct:writeNoChmod", testFileContent, FILE_NAME, testFileName);
        assertMockEndpointsSatisfied();
    }
}

