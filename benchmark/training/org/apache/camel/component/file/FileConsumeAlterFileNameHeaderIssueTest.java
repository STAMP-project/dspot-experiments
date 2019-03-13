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
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class FileConsumeAlterFileNameHeaderIssueTest extends ContextTestSupport {
    @Test
    public void testConsumeAndDeleteRemoveAllHeaders() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // remove all headers
                from("file://target/data/files?initialDelay=0&delay=10&delete=true").removeHeaders("*").to("mock:result");
            }
        });
        context.start();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBodyAndHeader("file://target/data/files", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        Assert.assertFalse("Headers should have been removed", mock.getExchanges().get(0).getIn().hasHeaders());
        // the original file should have been deleted, as the file consumer should be resilient against
        // end users deleting headers
        Assert.assertFalse("File should been deleted", new File("target/data/files/hello.txt").exists());
    }

    @Test
    public void testConsumeAndDeleteChangeFileHeader() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // change file header
                from("file://target/data/files?initialDelay=0&delay=10&delete=true").setHeader(FILE_NAME, constant("bye.txt")).to("mock:result");
            }
        });
        context.start();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        mock.expectedHeaderReceived(FILE_NAME, "bye.txt");
        template.sendBodyAndHeader("file://target/data/files", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // the original file should have been deleted, as the file consumer should be resilient against
        // end users changing headers
        Assert.assertFalse("File should been deleted", new File("target/data/files/hello.txt").exists());
    }

    @Test
    public void testConsumeAndMoveRemoveAllHeaders() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // remove all headers
                from("file://target/data/files?initialDelay=0&delay=10").removeHeaders("*").to("mock:result");
            }
        });
        context.start();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBodyAndHeader("file://target/data/files", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        Assert.assertFalse("Headers should have been removed", mock.getExchanges().get(0).getIn().hasHeaders());
        // the original file should have been moved, as the file consumer should be resilient against
        // end users deleting headers
        Assert.assertTrue("File should been moved", new File("target/data/files/.camel/hello.txt").exists());
    }

    @Test
    public void testConsumeAndMoveChangeFileHeader() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // change file header
                from("file://target/data/files?initialDelay=0&delay=10").setHeader(FILE_NAME, constant("bye.txt")).to("mock:result");
            }
        });
        context.start();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        mock.expectedHeaderReceived(FILE_NAME, "bye.txt");
        template.sendBodyAndHeader("file://target/data/files", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // the original file should have been moved, as the file consumer should be resilient against
        // end users changing headers
        Assert.assertTrue("File should been moved", new File("target/data/files/.camel/hello.txt").exists());
    }
}

