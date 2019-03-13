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
package org.apache.camel.spring.xml;


import org.apache.camel.builder.RouteBuilderTest;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


/**
 * A test case of the builder using Spring 2.0 to load the rules
 */
public class SpringXmlRouteBuilderTest extends RouteBuilderTest {
    protected AbstractXmlApplicationContext applicationContext;

    @Override
    @Test
    public void testIdempotentConsumer() throws Exception {
        // is tested elsewhere
    }

    @Override
    @Test
    public void testRouteWithInterceptor() throws Exception {
        // is tested elsewhere
    }
}

