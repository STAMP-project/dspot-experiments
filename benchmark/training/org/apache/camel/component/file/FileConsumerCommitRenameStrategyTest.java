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
import java.io.FileWriter;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.IOConverter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the FileRenameStrategy using move options
 */
public class FileConsumerCommitRenameStrategyTest extends ContextTestSupport {
    @Test
    public void testRenameSuccess() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:report");
        mock.expectedBodiesReceived("Hello Paris");
        mock.expectedFileExists("target/data/done/paris.txt", "Hello Paris");
        template.sendBodyAndHeader("file:target/data/reports", "Hello Paris", FILE_NAME, "paris.txt");
        mock.assertIsSatisfied();
    }

    @Test
    public void testRenameFileExists() throws Exception {
        // create a file in done to let there be a duplicate file
        File file = new File("target/data/done");
        file.mkdirs();
        FileWriter fw = new FileWriter("target/data/done/london.txt");
        try {
            fw.write("I was there once in London");
            fw.flush();
        } finally {
            fw.close();
        }
        MockEndpoint mock = getMockEndpoint("mock:report");
        mock.expectedBodiesReceived("Hello London");
        template.sendBodyAndHeader("file:target/data/reports", "Hello London", FILE_NAME, "london.txt");
        mock.assertIsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        // content of file should be Hello London
        String content = IOConverter.toString(new File("target/data/done/london.txt"), null);
        Assert.assertEquals("The file should have been renamed replacing any existing files", "Hello London", content);
    }
}

