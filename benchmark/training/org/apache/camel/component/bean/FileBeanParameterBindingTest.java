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
package org.apache.camel.component.bean;


import Exchange.FILE_NAME;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.junit.Assert;
import org.junit.Test;


public class FileBeanParameterBindingTest extends ContextTestSupport {
    @Test
    public void testFileToBean() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBodyAndHeader("file:target/data/foo", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
    }

    public static class MyFooBean {
        public void before(@Header("bar")
        Integer bar, @Header(Exchange.FILE_NAME)
        String name) {
            Assert.assertNull("There should be no bar", bar);
            Assert.assertEquals("hello.txt", name);
        }

        public void after(@Header("bar")
        Integer bar, @Header(Exchange.FILE_NAME)
        String name) {
            Assert.assertNotNull("There should be bar", bar);
            Assert.assertEquals(123, bar.intValue());
            Assert.assertEquals("hello.txt", name);
        }
    }
}

