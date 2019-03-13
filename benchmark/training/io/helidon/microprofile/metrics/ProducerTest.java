/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.microprofile.metrics;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Class ProducerTest.
 */
public class ProducerTest extends MetricsBaseTest {
    @Test
    public void testFieldProducer() throws Exception {
        ProducerBean bean = newBean(ProducerBean.class);
        MatcherAssert.assertThat(MetricsBaseTest.getMetricRegistry().getCounters().keySet().contains("counter1"), CoreMatchers.is(true));
        MatcherAssert.assertThat(MetricsBaseTest.getMetricRegistry().getCounters().get("counter1").getCount(), CoreMatchers.is(0L));
    }

    @Test
    public void testMethodProducer() throws Exception {
        ProducerBean bean = newBean(ProducerBean.class);
        MatcherAssert.assertThat(MetricsBaseTest.getMetricRegistry().getCounters().keySet().contains("counter2"), CoreMatchers.is(true));
        MatcherAssert.assertThat(MetricsBaseTest.getMetricRegistry().getCounters().get("counter2").getCount(), CoreMatchers.is(1L));
    }
}

