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
package org.apache.camel.component.xslt;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class XsltFromFileExceptionTest extends ContextTestSupport {
    @Test
    public void testXsltFromFileExceptionOk() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        getMockEndpoint("mock:error").expectedMessageCount(0);
        template.sendBodyAndHeader("file:target/data/xslt", "<hello>world!</hello>", FILE_NAME, "hello.xml");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        File file = new File("target/data/xslt/hello.xml");
        Assert.assertFalse(("File should not exists " + file), file.exists());
        file = new File("target/data/xslt/ok/hello.xml");
        Assert.assertTrue(("File should exists " + file), file.exists());
    }

    @Test
    public void testXsltFromFileExceptionFail() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:error").expectedMessageCount(1);
        // the last tag is not ended properly
        template.sendBodyAndHeader("file:target/data/xslt", "<hello>world!</hello", FILE_NAME, "hello2.xml");
        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesMockWaitTime();
        File file = new File("target/data/xslt/hello2.xml");
        Assert.assertFalse(("File should not exists " + file), file.exists());
        file = new File("target/data/xslt/error/hello2.xml");
        Assert.assertTrue(("File should exists " + file), file.exists());
    }
}

