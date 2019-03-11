package org.springside.modules.metrics;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;
import org.springside.modules.metrics.exporter.JmxExporter;
import org.springside.modules.metrics.metric.Counter;
import org.springside.modules.metrics.metric.Gauge;
import org.springside.modules.metrics.metric.Histogram;
import org.springside.modules.metrics.metric.Timer;
import org.springside.modules.metrics.metric.Timer.TimerContext;
import org.springside.modules.metrics.reporter.ConsoleReporter;
import org.springside.modules.metrics.reporter.Slf4jReporter;


/**
 * ??????ConsoleReporter?Slf4jReporter, ??Counter, Histogram ? Timer???.
 *
 * src/test/resources/logback.xml???Slf4jReporter????
 */
public class MetricExamples {
    @Test
    public void counterExample() throws InterruptedException {
        MetricRegistry metricRegistry = new MetricRegistry();
        // ??ConsoleReporter
        ConsoleReporter consoleReporter = new ConsoleReporter();
        Counter counter = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.counter"));
        ReportScheduler scheduler = new ReportScheduler(metricRegistry, consoleReporter);
        scheduler.start(1, TimeUnit.SECONDS);
        counter.inc();
        Thread.sleep(1050);
        counter.inc(2);
        Thread.sleep(1050);
        scheduler.stop();
    }

    @Test
    public void histogramExample() throws InterruptedException {
        MetricRegistry metricRegistry = new MetricRegistry();
        // ??slf4j reporter,?????logger??
        Slf4jReporter slf4jReporter = new Slf4jReporter();
        ReportScheduler scheduler = new ReportScheduler(metricRegistry, slf4jReporter);
        scheduler.start(1, TimeUnit.SECONDS);
        Histogram histogram = metricRegistry.histogram(MetricRegistry.name("UserService", "getUser.latency"));
        histogram.update(1);
        histogram.update(100);
        Thread.sleep(1050);
        // ?????
        histogram.setPcts(new Double[]{ 99.0, 99.95 });
        histogram.update(2);
        histogram.update(200);
        Thread.sleep(1050);
        scheduler.stop();
    }

    @Test
    public void timerExample() throws InterruptedException {
        MetricRegistry metricRegistry = new MetricRegistry();
        // ??slf4j reporter,??????logger??
        Slf4jReporter slf4jReporter = new Slf4jReporter("mymetrics");
        ReportScheduler scheduler = new ReportScheduler(metricRegistry);
        scheduler.addReporter(slf4jReporter);
        scheduler.start(1, TimeUnit.SECONDS);
        Timer timer = metricRegistry.timer(MetricRegistry.name("UserService", "getUser.timer"), new Double[]{ 99.0, 99.99 });
        // ??1
        TimerContext timerContext = timer.start();
        Thread.sleep(100);
        timerContext.stop();
        timerContext = timer.start();
        Thread.sleep(200);
        timerContext.stop();
        Thread.sleep(750);
        // ??2
        long start = System.currentTimeMillis();
        Thread.sleep(150);
        timer.update(start);
        start = System.currentTimeMillis();
        Thread.sleep(250);
        timer.update(start);
        Thread.sleep(650);
        scheduler.stop();
    }

    @Test
    public void gaugeExample() throws InterruptedException {
        MetricRegistry metricRegistry = new MetricRegistry();
        // ??ConsoleReporter
        ConsoleReporter consoleReporter = new ConsoleReporter();
        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Gauge<Long> usedMemoryGague = new Gauge<Long>() {
            public Long getValue() {
                return memoryMXBean.getHeapMemoryUsage().getUsed();
            }
        };
        Gauge<Long> cachedUsedMemoryGague = new org.springside.modules.metrics.metric.CachedGauge<Long>(10, TimeUnit.SECONDS) {
            public Long loadValue() {
                return memoryMXBean.getHeapMemoryUsage().getUsed();
            }
        };
        metricRegistry.registerGauge(MetricRegistry.name("JVM", "usedMemory"), usedMemoryGague);
        metricRegistry.registerGauge(MetricRegistry.name("JVM", "cachedUsedMemory"), cachedUsedMemoryGague);
        ReportScheduler scheduler = new ReportScheduler(metricRegistry, consoleReporter);
        scheduler.start(1, TimeUnit.SECONDS);
        Thread.sleep(1050);
        // use some memory
        List<Integer> list = new ArrayList<Integer>(200000);
        list.add(1);
        Thread.sleep(1050);
        scheduler.stop();
    }

    @Test
    public void jmxExample() throws Exception, InterruptedException {
        MetricRegistry metricRegistry = new MetricRegistry();
        Counter counter = metricRegistry.counter(MetricRegistry.name("UserService", "getUser.counter"));
        Timer timer = metricRegistry.timer(MetricRegistry.name("UserService", "getUser.timer"), new Double[]{ 0.99, 0.999 });
        // ?reporter?only exporter
        JmxExporter jmxExporter = new JmxExporter("metrics-example", metricRegistry);
        jmxExporter.initMBeans();
        ReportScheduler scheduler = new ReportScheduler(metricRegistry);
        scheduler.start(1, TimeUnit.SECONDS);
        counter.inc();
        TimerContext timerContext = timer.start();
        Thread.sleep(100);
        timerContext.stop();
        Thread.sleep(950);
        counter.inc(2);
        Thread.sleep(1050);
        scheduler.stop();
        // ??MBean???
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        System.out.println(("TotalCount from MBean:" + (mBeanServer.getAttribute(new ObjectName("metrics-example", "name", "UserService.getUser.counter"), "TotalCount").toString())));
    }
}

