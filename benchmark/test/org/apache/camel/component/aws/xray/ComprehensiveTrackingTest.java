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
package org.apache.camel.component.aws.xray;


import java.util.concurrent.TimeUnit;
import org.apache.camel.Handler;
import org.apache.camel.builder.NotifyBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class ComprehensiveTrackingTest extends CamelAwsXRayTestSupport {
    private ComprehensiveTrackingTest.InvokeChecker invokeChecker = new ComprehensiveTrackingTest.InvokeChecker();

    public ComprehensiveTrackingTest() {
        super(// disabled by the LogSegmentDecorator (-> .to("log:...");
        // .withSubsegment(TestDataBuilder.createSubsegment("log:test"))
        // no tracing of the invoke checker bean as it wasn't annotated with
        // @XRayTrace
        TestDataBuilder.createTrace().inRandomOrder().withSegment(TestDataBuilder.createSegment("start").inRandomOrder().withSubsegment(TestDataBuilder.createSubsegment("seda:d")).withSubsegment(TestDataBuilder.createSubsegment("direct:a"))).withSegment(TestDataBuilder.createSegment("a").withSubsegment(TestDataBuilder.createSubsegment("seda:b")).withSubsegment(TestDataBuilder.createSubsegment("seda:c"))).withSegment(TestDataBuilder.createSegment("b")).withSegment(TestDataBuilder.createSegment("c")).withSegment(TestDataBuilder.createSegment("d")).withSegment(TestDataBuilder.createSegment("test")));
    }

    @Test
    public void testRoute() throws Exception {
        NotifyBuilder notify = from("seda:test").whenDone(1).create();
        template.requestBody("direct:start", "Hello");
        assertThat("Not all exchanges were fully processed", notify.matches(10, TimeUnit.SECONDS), CoreMatchers.is(CoreMatchers.equalTo(true)));
        verify();
        assertThat(invokeChecker.gotInvoked(), CoreMatchers.is(CoreMatchers.equalTo(true)));
    }

    public static class InvokeChecker {
        private boolean invoked;

        @Handler
        public void invoke() {
            this.invoked = true;
        }

        boolean gotInvoked() {
            return this.invoked;
        }
    }
}

