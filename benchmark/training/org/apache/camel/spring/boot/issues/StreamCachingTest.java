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
package org.apache.camel.spring.boot.issues;


import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class StreamCachingTest extends CamelTestSupport {
    // this is not a spring boot test as its standalone Camel testing by extending CamelTestSupport
    public static final String URI_END_OF_ROUTE = "mock:end_of_route";

    @EndpointInject(uri = StreamCachingTest.URI_END_OF_ROUTE)
    private MockEndpoint endOfRoute;

    @Test
    public void streamCachingWithSpring() throws Exception {
        endOfRoute.expectedMessageCount(1);
        template.sendBody("direct:foo", new FileInputStream(new File("src/test/resources/logback.xml")));
        endOfRoute.assertIsSatisfied();
    }

    public static class MyBean {
        public List<Integer> someNumbers() {
            return Arrays.asList(1, 2, 3);
        }
    }
}

