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
import FileComponent.FILE_EXCHANGE_FILE;
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for consuming multiple directories.
 */
public class FileConsumeMultipleDirectoriesTest extends ContextTestSupport {
    private String fileUrl = "file://target/data/multidir/?initialDelay=0&delay=10&recursive=true&delete=true&sortBy=file:path";

    @SuppressWarnings("unchecked")
    @Test
    public void testMultiDir() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Bye World", "Hello World", "Godday World");
        template.sendBodyAndHeader(fileUrl, "Bye World", FILE_NAME, "bye.txt");
        template.sendBodyAndHeader(fileUrl, "Hello World", FILE_NAME, "sub/hello.txt");
        template.sendBodyAndHeader(fileUrl, "Godday World", FILE_NAME, "sub/sub2/godday.txt");
        assertMockEndpointsSatisfied();
        Exchange exchange = mock.getExchanges().get(0);
        GenericFile<File> gf = ((GenericFile<File>) (exchange.getProperty(FILE_EXCHANGE_FILE)));
        File file = gf.getFile();
        TestSupport.assertDirectoryEquals("target/data/multidir/bye.txt", file.getPath());
        Assert.assertEquals("bye.txt", file.getName());
        exchange = mock.getExchanges().get(1);
        gf = ((GenericFile<File>) (exchange.getProperty(FILE_EXCHANGE_FILE)));
        file = gf.getFile();
        TestSupport.assertDirectoryEquals("target/data/multidir/sub/hello.txt", file.getPath());
        Assert.assertEquals("hello.txt", file.getName());
        exchange = mock.getExchanges().get(2);
        gf = ((GenericFile<File>) (exchange.getProperty(FILE_EXCHANGE_FILE)));
        file = gf.getFile();
        TestSupport.assertDirectoryEquals("target/data/multidir/sub/sub2/godday.txt", file.getPath());
        Assert.assertEquals("godday.txt", file.getName());
    }
}

