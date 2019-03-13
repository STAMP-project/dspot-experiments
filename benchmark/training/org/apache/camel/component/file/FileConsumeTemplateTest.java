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
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.junit.Assert;
import org.junit.Test;


/**
 * Using ConsumerTemplate to consume a file
 */
public class FileConsumeTemplateTest extends ContextTestSupport {
    @Test
    public void testConsumeFileWithTemplate() throws Exception {
        template.sendBodyAndHeader("file://target/data/template", "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader("file://target/data/template", "Bye World", FILE_NAME, "bye.txt");
        Exchange out = consumer.receive("file://target/data/template?sortBy=file:name");
        Assert.assertNotNull(out);
        Exchange out2 = consumer.receive("file://target/data/template?sortBy=file:name");
        Assert.assertNotNull(out2);
        String body = out.getIn().getBody(String.class);
        String body2 = out2.getIn().getBody(String.class);
        // bye should come before hello (eg sorted a..z by file name)
        Assert.assertEquals("Bye World", body);
        Assert.assertEquals("Hello World", body2);
    }
}

