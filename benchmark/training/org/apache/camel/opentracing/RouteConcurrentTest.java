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
import java.util.concurrent.TimeUnit;
import org.apache.camel.builder.NotifyBuilder;
import org.junit.Test;


public class RouteConcurrentTest extends CamelOpenTracingTestSupport {
    private static SpanTestData[] testdata = new SpanTestData[]{ new SpanTestData().setLabel("seda:foo client").setUri("seda://foo").setOperation("foo").setKind(SPAN_KIND_CLIENT), new SpanTestData().setLabel("seda:bar client").setUri("seda://bar").setOperation("bar").setKind(SPAN_KIND_CLIENT).setParentId(2), new SpanTestData().setLabel("seda:foo server").setUri("seda://foo?concurrentConsumers=5").setOperation("foo").setKind(SPAN_KIND_SERVER).setParentId(0), new SpanTestData().setLabel("seda:bar server").setUri("seda://bar?concurrentConsumers=5").setOperation("bar").setKind(SPAN_KIND_SERVER).setParentId(1) };

    public RouteConcurrentTest() {
        super(RouteConcurrentTest.testdata);
    }

    @Test
    public void testSingleInvocationsOfRoute() throws Exception {
        NotifyBuilder notify = whenDone(2).create();
        template.sendBody("seda:foo", "Hello World");
        assertTrue(notify.matches(30, TimeUnit.SECONDS));
        verify();
    }

    @Test
    public void testConcurrentInvocationsOfRoute() throws Exception {
        NotifyBuilder notify = whenDone(10).create();
        for (int i = 0; i < 5; i++) {
            template.sendBody("seda:foo", "Hello World");
        }
        assertTrue(notify.matches(30, TimeUnit.SECONDS));
        verifyTraceSpanNumbers(5, RouteConcurrentTest.testdata.length);
    }
}

