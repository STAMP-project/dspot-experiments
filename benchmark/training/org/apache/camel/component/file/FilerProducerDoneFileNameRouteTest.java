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
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for writing done files
 */
public class FilerProducerDoneFileNameRouteTest extends ContextTestSupport {
    private Properties myProp = new Properties();

    @Test
    public void testProducerPlaceholderPrefixDoneFileName() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:start", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(oneExchangeDone.matches(5, TimeUnit.SECONDS));
        File file = new File("target/data/done/hello.txt");
        Assert.assertEquals("File should exists", true, file.exists());
        File done = new File("target/data/done/done-hello.txt");
        Assert.assertEquals("Done file should exists", true, done.exists());
    }
}

