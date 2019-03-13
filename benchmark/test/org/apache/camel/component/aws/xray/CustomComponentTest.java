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
import org.apache.camel.component.aws.xray.bean.ProcessingCamelBean;
import org.apache.camel.component.aws.xray.component.CommonEndpoints;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * This test uses a custom component that will trigger a long-running backing task for certain
 * specific states. The task is forwarded via an asynchronous send to a Camel route which then
 * performs the task, such as an upload or a computation.
 * <p>
 * AWS XRay does monitor the subsegment count per segment and only emits the segment to the local
 * XRay daemon once the segment is closed and its internal count reaches 0. If the segment is closed
 * before the counter reached 0 the segment is not emitted till the last subsegments belonging to
 * that segment got closed.
 * <p>
 * Due to the asynchronous nature of the backing {@link ProcessingCamelBean processing camel bean},
 * the first request is still in progress when the second request is triggered. As those tasks
 * aren't executed in parallel, AWS XRay does not take notice of the seconds processing Camel bean
 * invocation yet which leads to a premature emit of that segment and thus missing subsegments
 * for the route and bean invocation. This is possible as the count of the segment reached 0 when
 * the segment got closed as Camel has not had a chance yet to create the subsegments for the
 * asynchronously executed route and its bean invocation.
 */
public class CustomComponentTest extends CamelAwsXRayTestSupport {
    private static final String START = "seda:start";

    private static final String DELIVERY = "seda:delivery";

    private static final String IN_QUEUE = "seda:inqueue";

    private static final String PERSISTENCE_QUEUE = "seda:persistence-queue";

    private static final String PERSISTING = "seda:persisting";

    public CustomComponentTest() {
        super(TestDataBuilder.createTrace().inRandomOrder().withSegment(TestDataBuilder.createSegment("start").withSubsegment(TestDataBuilder.createSubsegment(CustomComponentTest.DELIVERY))).withSegment(TestDataBuilder.createSegment("delivery").withSubsegment(TestDataBuilder.createSubsegment(CommonEndpoints.RECEIVED).withSubsegment(TestDataBuilder.createSubsegment("seda:backingTask")).withSubsegment(TestDataBuilder.createSubsegment("seda:backingTask")).withMetadata("state", "received")).withSubsegment(TestDataBuilder.createSubsegment(CustomComponentTest.IN_QUEUE))).withSegment(TestDataBuilder.createSegment("processing").withSubsegment(TestDataBuilder.createSubsegment(CommonEndpoints.PROCESSING)).withSubsegment(TestDataBuilder.createSubsegment(CustomComponentTest.PERSISTENCE_QUEUE))).withSegment(TestDataBuilder.createSegment("wait-for-persisting").withSubsegment(TestDataBuilder.createSubsegment(CommonEndpoints.PERSISTENCE_QUEUE)).withSubsegment(TestDataBuilder.createSubsegment(CustomComponentTest.PERSISTING))).withSegment(// not available due to the asynchronous, long-running nature of the processing
        // bean. If the sleep is commented out in the bean, this subsegments should be
        // available
        // .withSubsegment(createSubsegment("backingTask")
        // .withSubsegment(createSubsegment("bean:ProcessingCamelBean"))
        // )
        // .withMetadata("state", "ready")
        TestDataBuilder.createSegment("persisting").withSubsegment(TestDataBuilder.createSubsegment(CommonEndpoints.READY))));
    }

    @Test
    public void testRoute() {
        NotifyBuilder notify = whenDone(7).create();
        template.requestBody(CustomComponentTest.START, "Hello");
        assertThat("Not all exchanges were fully processed", notify.matches(10, TimeUnit.SECONDS), CoreMatchers.is(CoreMatchers.equalTo(true)));
        verify();
        assertThat(ProcessingCamelBean.gotInvoked(), CoreMatchers.is(Matchers.greaterThan(0)));
    }
}

