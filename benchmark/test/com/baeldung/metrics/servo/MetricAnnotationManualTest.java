package com.baeldung.metrics.servo;


import com.google.common.collect.Lists;
import com.netflix.servo.Metric;
import com.netflix.servo.annotations.DataSourceType;
import com.netflix.servo.annotations.Monitor;
import com.netflix.servo.annotations.MonitorTags;
import com.netflix.servo.monitor.Monitors;
import com.netflix.servo.tag.BasicTag;
import com.netflix.servo.tag.TagList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MetricAnnotationManualTest extends MetricTestBase {
    @Monitor(name = "integerCounter", type = DataSourceType.COUNTER, description = "Total number of update operations.")
    private final AtomicInteger updateCount = new AtomicInteger(0);

    @MonitorTags
    private TagList tags = new com.netflix.servo.tag.BasicTagList(Lists.newArrayList(new BasicTag("tag-key", "tag-value")));

    @Test
    public void givenAnnotatedMonitor_whenUpdated_thenDataCollected() throws Exception {
        Monitors.registerObject("testObject", this);
        Assert.assertTrue(Monitors.isObjectRegistered("testObject", this));
        updateCount.incrementAndGet();
        updateCount.incrementAndGet();
        TimeUnit.SECONDS.sleep(1);
        List<List<Metric>> metrics = observer.getObservations();
        System.out.println(metrics);
        Assert.assertThat(metrics, hasSize(greaterThanOrEqualTo(1)));
        Iterator<List<Metric>> metricIterator = metrics.iterator();
        // skip first empty observation
        metricIterator.next();
        while (metricIterator.hasNext()) {
            Assert.assertThat(metricIterator.next(), CoreMatchers.hasItem(hasProperty("config", hasProperty("name", CoreMatchers.is("integerCounter")))));
        } 
    }
}

