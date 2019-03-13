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
import org.apache.camel.builder.NotifyBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class MulticastRouteTest extends CamelAwsXRayTestSupport {
    public MulticastRouteTest() {
        super(// disabled by the LogSegmentDecorator (-> .to("log:..."); .log("...") is still working)
        // .withSubsegment(TestDataBuilder.createSubsegment("log:routing%20at%20$%7BrouteId%7D"))
        TestDataBuilder.createTrace().withSegment(TestDataBuilder.createSegment("start").withSubsegment(TestDataBuilder.createSubsegment("seda:a"))).withSegment(TestDataBuilder.createSegment("a").withSubsegment(TestDataBuilder.createSubsegment("seda:b")).withSubsegment(TestDataBuilder.createSubsegment("seda:c"))).withSegment(TestDataBuilder.createSegment("b")).withSegment(TestDataBuilder.createSegment("c")));
    }

    @Test
    public void testRoute() throws Exception {
        NotifyBuilder notify = from("seda:c").whenDone(1).create();
        template.requestBody("direct:start", "Hello");
        assertThat("Not all exchanges were fully processed", notify.matches(5, TimeUnit.SECONDS), CoreMatchers.is(CoreMatchers.equalTo(true)));
        verify();
    }
}

