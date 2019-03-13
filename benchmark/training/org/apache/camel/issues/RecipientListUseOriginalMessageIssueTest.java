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
package org.apache.camel.issues;


import Exchange.FILE_NAME;
import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class RecipientListUseOriginalMessageIssueTest extends ContextTestSupport {
    @Test
    public void testRecipientListUseOriginalMessageIssue() throws Exception {
        getMockEndpoint("mock:error").expectedMinimumMessageCount(1);
        template.sendBodyAndHeader("file:target/data/inbox", "A", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        File out = new File("target/data/outbox/hello.txt");
        String data = context.getTypeConverter().convertTo(String.class, out);
        Assert.assertEquals("A", data);
    }
}

