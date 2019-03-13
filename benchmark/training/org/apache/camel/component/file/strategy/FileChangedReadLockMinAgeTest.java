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
package org.apache.camel.component.file.strategy;


import Exchange.RECEIVED_TIMESTAMP;
import java.io.File;
import java.util.Date;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileChangedReadLockMinAgeTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(FileChangedReadLockMinAgeTest.class);

    @Test
    public void testChangedReadLockMinAge() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedFileExists("target/data/changed/out/slowfile.dat");
        mock.expectedMessagesMatches(TestSupport.exchangeProperty(RECEIVED_TIMESTAMP).convertTo(long.class).isGreaterThan(((new Date().getTime()) + 500)));
        writeSlowFile();
        assertMockEndpointsSatisfied();
        String content = context.getTypeConverter().convertTo(String.class, new File("target/data/changed/out/slowfile.dat"));
        String[] lines = content.split(TestSupport.LS);
        Assert.assertEquals("There should be 20 lines in the file", 20, lines.length);
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(("Line " + i), lines[i]);
        }
    }
}

