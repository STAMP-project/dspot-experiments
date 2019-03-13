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
package org.apache.camel.opentracing;


import Tags.SPAN_KIND_CLIENT;
import Tags.SPAN_KIND_SERVER;
import org.junit.Test;


public class TwoServiceTest extends CamelOpenTracingTestSupport {
    private static SpanTestData[] testdata = new SpanTestData[]{ new SpanTestData().setLabel("ServiceB server").setUri("direct://ServiceB").setOperation("ServiceB").setKind(SPAN_KIND_SERVER).setParentId(1), new SpanTestData().setLabel("ServiceB client").setUri("direct://ServiceB").setOperation("ServiceB").setKind(SPAN_KIND_CLIENT).setParentId(2), new SpanTestData().setLabel("ServiceA server").setUri("direct://ServiceA").setOperation("ServiceA").setKind(SPAN_KIND_SERVER).setParentId(3), new SpanTestData().setLabel("ServiceA client").setUri("direct://ServiceA").setOperation("ServiceA").setKind(SPAN_KIND_CLIENT) };

    public TwoServiceTest() {
        super(TwoServiceTest.testdata);
    }

    @Test
    public void testRoute() throws Exception {
        template.requestBody("direct:ServiceA", "Hello");
        verify();
    }
}

