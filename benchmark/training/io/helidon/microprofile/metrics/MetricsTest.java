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


import MetricUnits.SECONDS;
import io.helidon.metrics.MetricsSupport;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Timer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;


/**
 * Class MetricsTest.
 */
public class MetricsTest extends MetricsBaseTest {
    @Test
    public void testCounted1() throws Exception {
        CountedBean bean = newBean(CountedBean.class);
        IntStream.range(0, 10).forEach(( i) -> bean.method1());
        Counter counter = MetricsBaseTest.getMetric(bean, "method1");
        MatcherAssert.assertThat(counter.getCount(), Is.is(10L));
    }

    @Test
    public void testCounted2() throws Exception {
        CountedBean bean = newBean(CountedBean.class);
        IntStream.range(0, 10).forEach(( i) -> bean.method2());
        Counter counter = MetricsBaseTest.getMetric(bean, "method1");
        MatcherAssert.assertThat(counter.getCount(), Is.is(10L));
    }

    @Test
    public void testMetered1() throws Exception {
        MeteredBean bean = newBean(MeteredBean.class);
        IntStream.range(0, 10).forEach(( i) -> bean.method1());
        Meter meter = MetricsBaseTest.getMetric(bean, "method1");
        MatcherAssert.assertThat(meter.getCount(), Is.is(10L));
        MatcherAssert.assertThat(meter.getMeanRate(), Is.is(greaterThan(0.0)));
    }

    @Test
    public void testMetered2() throws Exception {
        MeteredBean bean = newBean(MeteredBean.class);
        IntStream.range(0, 10).forEach(( i) -> bean.method2());
        Meter meter = MetricsBaseTest.getMetric(bean, "method2");
        MatcherAssert.assertThat(meter.getCount(), Is.is(10L));
        MatcherAssert.assertThat(meter.getMeanRate(), Is.is(greaterThan(0.0)));
    }

    @Test
    public void testTimed1() throws Exception {
        TimedBean bean = newBean(TimedBean.class);
        IntStream.range(0, 10).forEach(( i) -> bean.method1());
        Timer timer = MetricsBaseTest.getMetric(bean, "method1");
        MatcherAssert.assertThat(timer.getCount(), Is.is(10L));
        MatcherAssert.assertThat(timer.getMeanRate(), Is.is(greaterThan(0.0)));
    }

    @Test
    public void testTimed2() throws Exception {
        TimedBean bean = newBean(TimedBean.class);
        IntStream.range(0, 10).forEach(( i) -> bean.method2());
        Timer timer = MetricsBaseTest.getMetric(bean, "method2");
        MatcherAssert.assertThat(timer.getCount(), Is.is(10L));
        MatcherAssert.assertThat(timer.getMeanRate(), Is.is(greaterThan(0.0)));
    }

    @Test
    public void testInjection() throws Exception {
        InjectedBean bean = newBean(InjectedBean.class);
        MatcherAssert.assertThat(bean.counter, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(bean.meter, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(bean.timer, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(bean.histogram, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(bean.gaugeForInjectionTest, CoreMatchers.notNullValue());
    }

    @Test
    public void testGauge() throws Exception {
        final int EXPECTED_VALUE = 42;
        GaugedBean bean = newBean(GaugedBean.class);
        bean.setValue(EXPECTED_VALUE);
        Gauge<Integer> gauge = MetricsBaseTest.getMetric(bean, "reportValue");
        int valueViaGauge = gauge.getValue();
        MatcherAssert.assertThat(valueViaGauge, Is.is(EXPECTED_VALUE));
        Gauge<Integer> otherGauge = MetricsBaseTest.getMetric(bean, "retrieveValue");
        valueViaGauge = otherGauge.getValue();
        MatcherAssert.assertThat(valueViaGauge, Is.is(EXPECTED_VALUE));
    }

    @Test
    public void testGaugeMetadata() {
        final int EXPECTED_VALUE = 42;
        GaugedBean bean = newBean(GaugedBean.class);
        bean.setValue(EXPECTED_VALUE);
        Gauge<Integer> gauge = MetricsBaseTest.getMetric(bean, GaugedBean.LOCAL_INJECTABLE_GAUGE_NAME);
        String promData = MetricsSupport.toPrometheusData(gauge);
        Pattern prometheusDataPattern = Pattern.compile("(?s)#\\s+TYPE\\s+(\\w+):(\\w+)\\s*gauge.*#\\s*HELP.*\\{([^\\}]*)\\}\\s*(\\d*).*");
        Matcher m = prometheusDataPattern.matcher(promData);
        MatcherAssert.assertThat((("Prometheus data " + promData) + " for gauge bean did not match regex pattern"), m.matches(), Is.is(true));
        MatcherAssert.assertThat("Expected to find metric metadata and data in Prometheus data as 4 groups", m.groupCount(), Is.is(4));
        String beanScope = m.group(1);
        String promName = m.group(2);
        String tags = m.group(3);
        String value = m.group(4);
        String gaugeUnitsFromName = promName.substring(((promName.lastIndexOf('_')) + 1));
        MatcherAssert.assertThat("Unexpected bean scope for injected gauge", beanScope, Is.is("application"));
        MatcherAssert.assertThat("Unexpected units for injected gauge in Prometheus data", gaugeUnitsFromName, Is.is(SECONDS));
        MatcherAssert.assertThat("Unexpected tags for injected gauge in Prometheus data", tagsStringToMap(tags), Is.is(tagsStringToMap(GaugedBean.TAGS)));
        MatcherAssert.assertThat("Unexpected gauge value (in seconds)", Integer.parseInt(value), Is.is((EXPECTED_VALUE * 60)));
        /* Here is an example of the Prometheus data:

        # TYPE application:io_helidon_microprofile_metrics_cdi_gauged_bean_gauge_for_injection_test_seconds gauge
        # HELP application:io_helidon_microprofile_metrics_cdi_gauged_bean_gauge_for_injection_test_seconds
        application:io_helidon_microprofile_metrics_cdi_gauged_bean_gauge_for_injection_test_seconds{tag1="valA",tag2="valB"} 2520
         */
    }
}

