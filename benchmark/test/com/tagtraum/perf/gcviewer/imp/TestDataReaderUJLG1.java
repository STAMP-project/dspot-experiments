package com.tagtraum.perf.gcviewer.imp;


import Generation.ALL;
import Generation.TENURED;
import Generation.YOUNG;
import Type.UJL_G1_PAUSE_CLEANUP;
import Type.UJL_G1_PAUSE_MIXED;
import Type.UJL_PAUSE_FULL;
import Type.UJL_PAUSE_INITIAL_MARK;
import Type.UJL_PAUSE_REMARK;
import Type.UJL_PAUSE_YOUNG;
import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCEventUJL;
import com.tagtraum.perf.gcviewer.model.GCModel;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests unified jvm logging parser for cms gc events.
 */
public class TestDataReaderUJLG1 {
    @Test
    public void parseGcDefaults() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-g1-gc-defaults.txt");
        Assert.assertThat("size", model.size(), Matchers.is(15));
        Assert.assertThat("amount of gc event types", model.getGcEventPauses().size(), Matchers.is(6));
        Assert.assertThat("amount of gc events", model.getGCPause().getN(), Matchers.is(12));
        Assert.assertThat("amount of full gc event types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of full gc events", model.getFullGCPause().getN(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(1));
        UnittestHelper.testMemoryPauseEvent(model.get(0), "young", UJL_PAUSE_YOUNG, 0.006529, (1024 * 14), (1024 * 12), (1024 * 128), YOUNG, false);
        AbstractGCEvent<?> mixedEvent = model.get(14);
        UnittestHelper.testMemoryPauseEvent(mixedEvent, "mixed", UJL_G1_PAUSE_MIXED, 0.00279, (1024 * 29), (1024 * 28), (1024 * 123), TENURED, false);
        AbstractGCEvent<?> initialMarkEvent = model.get(1);
        UnittestHelper.testMemoryPauseEvent(initialMarkEvent, "initialMarkEvent", UJL_PAUSE_INITIAL_MARK, 0.006187, (1024 * 80), (1024 * 80), (1024 * 128), TENURED, false);
        Assert.assertThat("isInitialMark", initialMarkEvent.isInitialMark(), Matchers.is(true));
        AbstractGCEvent<?> remarkEvent = model.get(6);
        UnittestHelper.testMemoryPauseEvent(remarkEvent, "RemarkEvent", UJL_PAUSE_REMARK, 8.37E-4, (1024 * 121), (1024 * 121), (1024 * 128), TENURED, false);
        Assert.assertThat("isRemark", remarkEvent.isRemark(), Matchers.is(true));
        AbstractGCEvent<?> fullGcEvent = model.get(11);
        UnittestHelper.testMemoryPauseEvent(fullGcEvent, "full", UJL_PAUSE_FULL, 0.014918, (1024 * 128), (1024 * 8), (1024 * 28), ALL, true);
        AbstractGCEvent<?> cleanupEvent = model.get(12);
        UnittestHelper.testMemoryPauseEvent(cleanupEvent, "cleanup", UJL_G1_PAUSE_CLEANUP, 1.0E-6, (1024 * 9), (1024 * 9), (1024 * 28), TENURED, false);
        AbstractGCEvent<?> concurrentCycleBegin = model.get(2);
        Assert.assertThat("event is start of concurrent collection", concurrentCycleBegin.isConcurrentCollectionStart(), Matchers.is(true));
        AbstractGCEvent<?> concurrentCycleEnd = model.get(13);
        Assert.assertThat("event is end of concurrent collection", concurrentCycleEnd.isConcurrentCollectionEnd(), Matchers.is(true));
    }

    @Test
    public void parseGcAllSafepointOsCpu() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-g1-gc-all,safepoint,os+cpu.txt");
        Assert.assertThat("size", model.size(), Matchers.is(15));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(4));
        Assert.assertThat("amount of STW GC pauses", model.getGCPause().getN(), Matchers.is(13));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(0));
        Assert.assertThat("amount of STW Full GC pauses", model.getFullGCPause().getN(), Matchers.is(0));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(1));
        AbstractGCEvent<?> event1 = model.get(0);
        UnittestHelper.testMemoryPauseEvent(event1, "young", UJL_PAUSE_YOUNG, 0.007033, (1024 * 14), (1024 * 12), (1024 * 128), YOUNG, false);
        Assert.assertThat("young heap before", event1.details().next().getPreUsed(), Matchers.is((1024 * 14)));
        // GC(3) Pause Initial Mark
        AbstractGCEvent<?> event2 = model.get(5);
        UnittestHelper.testMemoryPauseEvent(event2, "initial mark", UJL_PAUSE_INITIAL_MARK, 0.005011, (1024 * 80), (1024 * 80), (1024 * 128), TENURED, false);
        Assert.assertThat("isInitialMark", event2.isInitialMark(), Matchers.is(true));
        // GC(3) Pause Remark
        AbstractGCEvent<?> remarkEvent = model.get(10);
        UnittestHelper.testMemoryPauseEvent(remarkEvent, "remark", UJL_PAUSE_REMARK, 9.39E-4, (1024 * 113), (1024 * 113), (1024 * 128), TENURED, false);
        Assert.assertThat("isRemark", remarkEvent.isRemark(), Matchers.is(true));
        AbstractGCEvent<?> cleanupEvent = model.get(13);
        UnittestHelper.testMemoryPauseEvent(cleanupEvent, "cleanup", UJL_G1_PAUSE_CLEANUP, 3.67E-4, (1024 * 114), (1024 * 70), (1024 * 128), TENURED, false);
        AbstractGCEvent<?> concurrentCycleBeginEvent = model.get(6);
        Assert.assertThat("event is start of concurrent collection", concurrentCycleBeginEvent.isConcurrentCollectionStart(), Matchers.is(true));
        AbstractGCEvent<?> concurrentCycleEndEvent = model.get(14);
        Assert.assertThat("event is end of concurrent collection", concurrentCycleEndEvent.isConcurrentCollectionEnd(), Matchers.is(true));
    }

    @Test
    public void parseGcAllSafepointOsCpuWithToSpaceExhausted() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-g1-gc-all,safepoint,os+cpu-to-space-exhausted.txt");
        Assert.assertThat("size", model.size(), Matchers.is(1));
        AbstractGCEvent<?> youngEvent = model.get(0);
        UnittestHelper.testMemoryPauseEvent(youngEvent, "young", UJL_PAUSE_YOUNG, 0.002717, (1024 * 116), (1024 * 119), (1024 * 128), YOUNG, false);
        Assert.assertThat("typeAsString", youngEvent.getTypeAsString(), Matchers.equalTo("Pause Young (G1 Evacuation Pause); To-space exhausted; Eden regions:; Survivor regions:; Old regions:; Humongous regions:; Metaspace:"));
        Iterator<AbstractGCEvent<?>> iterator = ((Iterator<AbstractGCEvent<?>>) (youngEvent.details()));
        // skip "To-space exhausted"
        iterator.next();
        testHeapSizing(iterator.next(), "eden", 0, 0, 0);
        testHeapSizing(iterator.next(), "survivor", 0, 0, 0);
        testHeapSizing(iterator.next(), "old", 0, 0, 0);
        testHeapSizing(iterator.next(), "humongous", 0, 0, 0);
        testHeapSizing(iterator.next(), "metaspace", 3648, 3648, 1056768);
        GCEventUJL gcEventUJL = ((GCEventUJL) (youngEvent));
        testHeapSizing(gcEventUJL.getYoung(), "young", 0, 0, 0);
        testHeapSizing(gcEventUJL.getTenured(), "tenured", 0, 0, 0);
        testHeapSizing(gcEventUJL.getPerm(), "metaspace", 3648, 3648, 1056768);
    }
}

