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
package org.apache.camel.example.cdi.metrics;


import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.annotation.Metric;
import javax.inject.Inject;
import org.apache.camel.test.cdi.CamelCdiRunner;
import org.apache.camel.test.cdi.Order;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(CamelCdiRunner.class)
public class CdiMetricsTest {
    @Inject
    private Meter generated;

    @Inject
    private Meter attempt;

    @Inject
    private Meter success;

    @Inject
    private Meter redelivery;

    @Inject
    private Meter error;

    @Inject
    @Metric(name = "success-ratio")
    private Gauge<Double> ratio;

    @Test
    @Order(2)
    public void testMetricsValues() {
        Assert.assertThat("Meter counts are not consistent!", ((((attempt.getCount()) - (redelivery.getCount())) - (success.getCount())) - (error.getCount())), Matchers.is(Matchers.equalTo(0L)));
        Assert.assertThat("Success rate gauge value is incorrect!", ratio.getValue(), Matchers.is(Matchers.equalTo(((success.getOneMinuteRate()) / (generated.getOneMinuteRate())))));
    }
}

