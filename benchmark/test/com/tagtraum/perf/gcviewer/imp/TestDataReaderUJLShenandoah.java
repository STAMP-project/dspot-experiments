package com.tagtraum.perf.gcviewer.imp;


import AbstractGCEvent.Generation.ALL;
import AbstractGCEvent.Generation.TENURED;
import AbstractGCEvent.Type.UJL_SHEN_CONCURRENT_CONC_MARK;
import AbstractGCEvent.Type.UJL_SHEN_INIT_MARK;
import DateHelper.DATE_TIME_FORMATTER;
import Type.UJL_PAUSE_FULL;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent.Type;
import com.tagtraum.perf.gcviewer.model.ConcurrentGCEvent;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import java.time.ZonedDateTime;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Mart on 10/05/2017.
 */
public class TestDataReaderUJLShenandoah {
    @Test
    public void parseBasicEvent() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahBasic.txt");
        Assert.assertThat("size", model.size(), Matchers.is(5));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(2));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(0));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(3));
        Assert.assertThat("total pause time", model.getPause().getSum(), Matchers.closeTo(0.001318, 1.0E-6));
        Assert.assertThat("gc pause time", model.getGCPause().getSum(), Matchers.closeTo(0.001318, 1.0E-6));
        Assert.assertThat("full gc pause time", model.getFullGCPause().getSum(), Matchers.is(0.0));
        Assert.assertThat("heap size after concurrent cycle", model.getPostConcurrentCycleHeapUsedSizes().getMax(), Matchers.is((33 * 1024)));
        Assert.assertThat("max memory freed during STW pauses", model.getFreedMemoryByGC().getMax(), Matchers.is((34 * 1024)));
        AbstractGCEvent<?> initialMarkEvent = model.get(0);
        Assert.assertThat("isInitialMark", initialMarkEvent.isInitialMark(), Matchers.is(true));
        AbstractGCEvent<?> finalMarkEvent = model.get(2);
        Assert.assertThat("isRemark", finalMarkEvent.isRemark(), Matchers.is(true));
        AbstractGCEvent<?> concurrentMarkingEvent = model.get(1);
        Assert.assertThat("event is start of concurrent collection", concurrentMarkingEvent.isConcurrentCollectionStart(), Matchers.is(true));
        AbstractGCEvent<?> concurrentResetEvent = model.get(4);
        Assert.assertThat("event is end of concurrent collection", concurrentResetEvent.isConcurrentCollectionEnd(), Matchers.is(true));
    }

    @Test
    public void parseAllocationFailure() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahAllocationFailure.txt");
        Assert.assertThat("size", model.size(), Matchers.is(1));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(0));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(0));
        Assert.assertThat("total pause time", model.getPause().getSum(), Matchers.closeTo(14.289335, 1.0E-6));
        Assert.assertThat("gc pause time", model.getGCPause().getSum(), Matchers.is(0.0));
        Assert.assertThat("full gc pause time", model.getFullGCPause().getSum(), Matchers.closeTo(14.289335, 1.0E-6));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_FULL.toString()));
        Assert.assertThat("preUsed heap size", event.getPreUsed(), Matchers.is((7943 * 1024)));
        Assert.assertThat("postUsed heap size", event.getPostUsed(), Matchers.is((6013 * 1024)));
        Assert.assertThat("total heap size", event.getTotal(), Matchers.is((8192 * 1024)));
        Assert.assertThat("timestamp", event.getTimestamp(), Matchers.closeTo(43.948, 0.001));
        Assert.assertThat("generation", event.getGeneration(), Matchers.is(ALL));
    }

    @Test
    public void parseDefaultConfiguration() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahDefaultConfiguration.txt");
        Assert.assertThat("size", model.size(), Matchers.is(140));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(4));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(5));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.equalTo(((Type.UJL_PAUSE_FULL) + " (System.gc())")));
        Assert.assertThat("is system gc", event.isSystem(), Matchers.is(true));
        Assert.assertThat("preUsed heap size", event.getPreUsed(), Matchers.is((10 * 1024)));
        Assert.assertThat("postUsed heap size", event.getPostUsed(), Matchers.is((1 * 1024)));
        Assert.assertThat("total heap size", event.getTotal(), Matchers.is((128 * 1024)));
        Assert.assertThat("timestamp", event.getTimestamp(), Matchers.closeTo(1.337, 0.001));
        Assert.assertThat("generation", event.getGeneration(), Matchers.is(ALL));
    }

    @Test
    public void parsePassiveHeuristics() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahPassiveHeuristics.txt");
        Assert.assertThat("size", model.size(), Matchers.is(0));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(0));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(0));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(0));
    }

    @Test
    public void parseAggressiveHeuristics() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahAggressiveHeuristics.txt");
        Assert.assertThat("size", model.size(), Matchers.is(549));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(4));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(0));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(5));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.is(UJL_SHEN_INIT_MARK.toString()));
        ConcurrentGCEvent event2 = ((ConcurrentGCEvent) (model.get(1)));
        Assert.assertThat("type", event2.getTypeAsString(), Matchers.is(UJL_SHEN_CONCURRENT_CONC_MARK.toString()));
        Assert.assertThat("preUsed heap size", event2.getPreUsed(), Matchers.is((90 * 1024)));
        Assert.assertThat("postUsed heap size", event2.getPostUsed(), Matchers.is((90 * 1024)));
        Assert.assertThat("total heap size", event2.getTotal(), Matchers.is((128 * 1024)));
        Assert.assertThat("timestamp", event2.getTimestamp(), Matchers.closeTo(8.35, 0.001));
        Assert.assertThat("generation", event2.getGeneration(), Matchers.is(TENURED));
    }

    @Test
    public void parseSingleSystemGCEvent() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahSingleSystemGC.txt");
        Assert.assertThat("size", model.size(), Matchers.is(353));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(4));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(5));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.equalTo(((Type.UJL_PAUSE_FULL) + " (System.gc())")));
        Assert.assertThat("is system gc", event.isSystem(), Matchers.is(true));
        Assert.assertThat("preUsed heap size", event.getPreUsed(), Matchers.is((10 * 1024)));
        Assert.assertThat("postUsed heap size", event.getPostUsed(), Matchers.is((1 * 1024)));
        Assert.assertThat("total heap size", event.getTotal(), Matchers.is((128 * 1024)));
        Assert.assertThat("timestamp", event.getTimestamp(), Matchers.closeTo(1.481, 0.001));
        Assert.assertThat("generation", event.getGeneration(), Matchers.is(ALL));
    }

    @Test
    public void parseSeveralSystemGCEvents() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahSeveralSystemGC.txt");
        Assert.assertThat("size", model.size(), Matchers.is(438));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(0));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(0));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.equalTo(((Type.UJL_PAUSE_FULL) + " (System.gc())")));
        Assert.assertThat("is system gc", event.isSystem(), Matchers.is(true));
        Assert.assertThat("preUsed heap size", event.getPreUsed(), Matchers.is((10 * 1024)));
        Assert.assertThat("postUsed heap size", event.getPostUsed(), Matchers.is((1 * 1024)));
        Assert.assertThat("total heap size", event.getTotal(), Matchers.is((128 * 1024)));
        Assert.assertThat("generation", event.getGeneration(), Matchers.is(ALL));
        Assert.assertThat("timestamp", event.getTimestamp(), Matchers.closeTo(1.303, 0.001));
    }

    @Test
    public void parseDateTimeStamps() throws Exception {
        GCModel model = getGCModelFromLogFile("SampleShenandoahDateTimeStamps.txt");
        Assert.assertThat("size", model.size(), Matchers.is(557));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("datestamp", event.getDatestamp(), Matchers.is(ZonedDateTime.parse("2017-08-30T23:22:47.357+0300", DATE_TIME_FORMATTER)));
        Assert.assertThat("timestamp", event.getTimestamp(), Matchers.is(0.0));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.is(UJL_SHEN_INIT_MARK.toString()));
        Assert.assertThat("generation", event.getGeneration(), Matchers.is(TENURED));
        ConcurrentGCEvent event2 = ((ConcurrentGCEvent) (model.get(1)));
        Assert.assertThat("datestamp", event.getDatestamp(), Matchers.is(ZonedDateTime.parse("2017-08-30T23:22:47.357+0300", DATE_TIME_FORMATTER)));
        Assert.assertThat("timestamp", event2.getTimestamp(), Matchers.closeTo(0.003, 0.001));
        Assert.assertThat("type", event2.getTypeAsString(), Matchers.is(UJL_SHEN_CONCURRENT_CONC_MARK.toString()));
        Assert.assertThat("preUsed heap size", event2.getPreUsed(), Matchers.is((90 * 1024)));
        Assert.assertThat("postUsed heap size", event2.getPostUsed(), Matchers.is((90 * 1024)));
        Assert.assertThat("total heap size", event2.getTotal(), Matchers.is((128 * 1024)));
        Assert.assertThat("generation", event2.getGeneration(), Matchers.is(TENURED));
    }
}

