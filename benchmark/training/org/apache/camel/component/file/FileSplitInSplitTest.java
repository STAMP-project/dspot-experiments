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
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class FileSplitInSplitTest extends ContextTestSupport {
    private final int size = 3;

    private final String comma = ",";

    @Test
    public void testConcurrentAppend() throws Exception {
        // create file with many lines
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (size); i++) {
            sb.append((("Block1 Line " + i) + (TestSupport.LS)));
        }
        sb.append(comma);
        for (int i = 10; i < ((size) + 10); i++) {
            sb.append((("Block2 Line " + i) + (TestSupport.LS)));
        }
        template.sendBodyAndHeader("file:target/data/split", sb.toString(), FILE_NAME, "input.txt");
        // start route
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        // check one file has expected number of lines +1 saying split is
        // complete.
        String txt = context.getTypeConverter().convertTo(String.class, new File("target/data/split/outbox/result0.txt"));
        Assert.assertNotNull(txt);
        String[] lines = txt.split(TestSupport.LS);
        Assert.assertEquals((("Should be " + ((size) + 1)) + " lines"), ((size) + 1), lines.length);
        txt = context.getTypeConverter().convertTo(String.class, new File("target/data/split/outbox/result1.txt"));
        Assert.assertNotNull(txt);
        lines = txt.split(TestSupport.LS);
        Assert.assertEquals((("Should be " + ((size) + 1)) + " lines"), ((size) + 1), lines.length);
    }
}

