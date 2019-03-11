package com.baeldung.metrics.micrometer;


import Metrics.globalRegistry;
import com.netflix.spectator.atlas.AtlasConfig;
import io.micrometer.atlas.AtlasMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.stats.hist.Histogram;
import io.micrometer.core.instrument.stats.quantile.WindowSketchQuantiles;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author aiet
 */
public class MicrometerAtlasIntegrationTest {
    private AtlasConfig atlasConfig;

    @Test
    public void givenCompositeRegistries_whenRecordMeter_thenAllRegistriesRecorded() {
        CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();
        SimpleMeterRegistry oneSimpleMeter = new SimpleMeterRegistry();
        AtlasMeterRegistry atlasMeterRegistry = new AtlasMeterRegistry(atlasConfig, Clock.SYSTEM);
        compositeRegistry.add(oneSimpleMeter);
        compositeRegistry.add(atlasMeterRegistry);
        compositeRegistry.gauge("baeldung.heat", 90);
        Optional<Gauge> oneGauge = oneSimpleMeter.find("baeldung.heat").gauge();
        Assert.assertTrue(oneGauge.isPresent());
        Iterator<Measurement> measurements = oneGauge.get().measure().iterator();
        Assert.assertTrue(measurements.hasNext());
        Assert.assertThat(measurements.next().getValue(), CoreMatchers.equalTo(90.0));
        Optional<Gauge> atlasGauge = atlasMeterRegistry.find("baeldung.heat").gauge();
        Assert.assertTrue(atlasGauge.isPresent());
        Iterator<Measurement> anotherMeasurements = atlasGauge.get().measure().iterator();
        Assert.assertTrue(anotherMeasurements.hasNext());
        Assert.assertThat(anotherMeasurements.next().getValue(), CoreMatchers.equalTo(90.0));
    }

    @Test
    public void givenGlobalRegistry_whenIncrementAnywhere_thenCounted() {
        class CountedObject {
            private CountedObject() {
                Metrics.counter("objects.instance").increment(1.0);
            }
        }
        Metrics.addRegistry(new SimpleMeterRegistry());
        Metrics.counter("objects.instance").increment();
        new CountedObject();
        Optional<Counter> counterOptional = globalRegistry.find("objects.instance").counter();
        Assert.assertTrue(counterOptional.isPresent());
        Assert.assertTrue(((counterOptional.get().count()) == 2.0));
    }

    @Test
    public void givenCounter_whenIncrement_thenValueChanged() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        Counter counter = Counter.builder("objects.instance").description("indicates instance count of the object").tags("dev", "performance").register(registry);
        counter.increment(2.0);
        Assert.assertTrue(((counter.count()) == 2));
        counter.increment((-1));
        Assert.assertTrue(((counter.count()) == 2));
    }

    @Test
    public void givenTimer_whenWrapTasks_thenTimeRecorded() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        Timer timer = registry.timer("app.event");
        timer.record(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(15);
            } catch ( ignored) {
            }
        });
        timer.record(30, TimeUnit.MILLISECONDS);
        Assert.assertTrue((2 == (timer.count())));
        Assert.assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isBetween(40.0, 55.0);
    }

    @Test
    public void givenLongTimer_whenRunTasks_thenTimerRecorded() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        LongTaskTimer longTaskTimer = LongTaskTimer.builder("3rdPartyService").register(registry);
        long currentTaskId = longTaskTimer.start();
        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException ignored) {
        }
        long timeElapsed = longTaskTimer.stop(currentTaskId);
        Assert.assertEquals(2L, (timeElapsed / ((int) (1000000.0))), 1L);
    }

    @Test
    public void givenGauge_whenMeterListSize_thenCurrentSizeMonitored() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        List<String> list = new ArrayList<>(4);
        Gauge gauge = Gauge.builder("cache.size", list, List::size).register(registry);
        Assert.assertTrue(((gauge.value()) == 0.0));
        list.add("1");
        Assert.assertTrue(((gauge.value()) == 1.0));
    }

    @Test
    public void givenDistributionSummary_whenRecord_thenSummarized() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        DistributionSummary distributionSummary = DistributionSummary.builder("request.size").baseUnit("bytes").register(registry);
        distributionSummary.record(3);
        distributionSummary.record(4);
        distributionSummary.record(5);
        Assert.assertTrue((3 == (distributionSummary.count())));
        Assert.assertTrue((12 == (distributionSummary.totalAmount())));
    }

    @Test
    public void givenTimer_whenEnrichWithQuantile_thenQuantilesComputed() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        Timer timer = Timer.builder("test.timer").quantiles(WindowSketchQuantiles.quantiles(0.3, 0.5, 0.95).create()).register(registry);
        timer.record(2, TimeUnit.SECONDS);
        timer.record(2, TimeUnit.SECONDS);
        timer.record(3, TimeUnit.SECONDS);
        timer.record(4, TimeUnit.SECONDS);
        timer.record(8, TimeUnit.SECONDS);
        timer.record(13, TimeUnit.SECONDS);
        Map<String, Integer> quantileMap = extractTagValueMap(registry, Type.Gauge, 1.0E9);
        Assert.assertThat(quantileMap, CoreMatchers.allOf(hasEntry("quantile=0.3", 2), hasEntry("quantile=0.5", 3), hasEntry("quantile=0.95", 8)));
    }

    @Test
    public void givenDistributionSummary_whenEnrichWithHistograms_thenDataAggregated() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        DistributionSummary hist = DistributionSummary.builder("summary").histogram(Histogram.linear(0, 10, 5)).register(registry);
        hist.record(3);
        hist.record(8);
        hist.record(20);
        hist.record(40);
        hist.record(13);
        hist.record(26);
        Map<String, Integer> histograms = extractTagValueMap(registry, Type.Counter, 1.0);
        Assert.assertThat(histograms, CoreMatchers.allOf(hasEntry("bucket=0.0", 0), hasEntry("bucket=10.0", 2), hasEntry("bucket=20.0", 2), hasEntry("bucket=30.0", 1), hasEntry("bucket=40.0", 1), hasEntry("bucket=Infinity", 0)));
    }

    @Test
    public void givenTimer_whenEnrichWithTimescaleHistogram_thenTimeScaleDataCollected() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        Timer timer = Timer.builder("timer").histogram(Histogram.linearTime(TimeUnit.MILLISECONDS, 0, 200, 3)).register(registry);
        timer.record(1000, TimeUnit.MILLISECONDS);
        timer.record(23, TimeUnit.MILLISECONDS);
        timer.record(450, TimeUnit.MILLISECONDS);
        timer.record(341, TimeUnit.MILLISECONDS);
        timer.record(500, TimeUnit.MILLISECONDS);
        Map<String, Integer> histograms = extractTagValueMap(registry, Type.Counter, 1.0);
        Assert.assertThat(histograms, CoreMatchers.allOf(hasEntry("bucket=0.0", 0), hasEntry("bucket=2.0E8", 1), hasEntry("bucket=4.0E8", 1), hasEntry("bucket=Infinity", 3)));
    }
}

