package com.tagtraum.perf.gcviewer.imp;


import Generation.ALL;
import Generation.TENURED;
import Generation.YOUNG;
import Type.UJL_PAUSE_FULL;
import Type.UJL_PAUSE_INITIAL_MARK;
import Type.UJL_PAUSE_REMARK;
import Type.UJL_PAUSE_YOUNG;
import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests unified jvm logging parser for cms gc events.
 */
public class TestDataReaderUJLCMS {
    @Test
    public void parseGcDefaults() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-cms-gc-defaults.txt");
        Assert.assertThat("size", model.size(), Matchers.is(16));
        Assert.assertThat("amount of gc event types", model.getGcEventPauses().size(), Matchers.is(3));
        Assert.assertThat("amount of gc events", model.getGCPause().getN(), Matchers.is(5));
        Assert.assertThat("amount of full gc event types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of full gc events", model.getFullGCPause().getN(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(5));
        UnittestHelper.testMemoryPauseEvent(model.get(0), "young", UJL_PAUSE_YOUNG, 0.008822, (1024 * 41), (1024 * 38), (1024 * 150), YOUNG, false);
        AbstractGCEvent<?> event2 = model.get(1);
        UnittestHelper.testMemoryPauseEvent(event2, "initialMarkEvent", UJL_PAUSE_INITIAL_MARK, 1.36E-4, (1024 * 61), (1024 * 61), (1024 * 150), TENURED, false);
        Assert.assertThat("isInitialMark", event2.isInitialMark(), Matchers.is(true));
        AbstractGCEvent<?> event3 = model.get(10);
        UnittestHelper.testMemoryPauseEvent(event3, "RemarkEvent", UJL_PAUSE_REMARK, 8.59E-4, (1024 * 110), (1024 * 110), (1024 * 150), TENURED, false);
        Assert.assertThat("isRemark", event3.isRemark(), Matchers.is(true));
        AbstractGCEvent<?> event4 = model.get(15);
        UnittestHelper.testMemoryPauseEvent(event4, "full", UJL_PAUSE_FULL, 0.009775, (1024 * 125), (1024 * 31), (1024 * 150), ALL, true);
        AbstractGCEvent<?> concurrentMarkBeginEvent = model.get(2);
        Assert.assertThat("event is not start of concurrent collection", concurrentMarkBeginEvent.isConcurrentCollectionStart(), Matchers.is(false));
        AbstractGCEvent<?> concurrentMarkWithPauseEvent = model.get(3);
        Assert.assertThat("event is start of concurrent collection", concurrentMarkWithPauseEvent.isConcurrentCollectionStart(), Matchers.is(true));
        AbstractGCEvent<?> concurrentResetBeginEvent = model.get(13);
        Assert.assertThat("event is not end of concurrent collection", concurrentResetBeginEvent.isConcurrentCollectionEnd(), Matchers.is(false));
        AbstractGCEvent<?> concurrentResetEvent = model.get(14);
        Assert.assertThat("event is end of concurrent collection", concurrentResetEvent.isConcurrentCollectionEnd(), Matchers.is(true));
    }

    @Test
    public void parseGcAllSafepointOsCpu() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-cms-gc-all,safepoint,os+cpu.txt");
        Assert.assertThat("size", model.size(), Matchers.is(26));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(3));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(4));
        AbstractGCEvent<?> event1 = model.get(0);
        UnittestHelper.testMemoryPauseEvent(event1, "young", UJL_PAUSE_YOUNG, 0.009618, (1024 * 41), (1024 * 38), (1024 * 150), YOUNG, false);
        // GC(3) Pause Initial Mark
        AbstractGCEvent<?> event2 = model.get(3);
        UnittestHelper.testMemoryPauseEvent(event2, "initial mark", UJL_PAUSE_INITIAL_MARK, 1.65E-4, (91 * 1024), (91 * 1024), (150 * 1024), TENURED, false);
        Assert.assertThat("isInitialMark", event2.isInitialMark(), Matchers.is(true));
        // GC(3) Pause Remark
        AbstractGCEvent<?> remarkEvent = model.get(10);
        UnittestHelper.testMemoryPauseEvent(remarkEvent, "remark", UJL_PAUSE_REMARK, 9.08E-4, (130 * 1024), (130 * 1024), (150 * 1024), TENURED, false);
        Assert.assertThat("isRemark", remarkEvent.isRemark(), Matchers.is(true));
        // GC(5) Pause Full
        AbstractGCEvent<?> fullGcEvent = model.get(13);
        UnittestHelper.testMemoryPauseEvent(fullGcEvent, "full gc ", UJL_PAUSE_FULL, 0.009509, (119 * 1024), (33 * 1024), (150 * 1024), ALL, true);
        AbstractGCEvent<?> concurrentMarkBeginEvent = model.get(4);
        Assert.assertThat("event is not start of concurrent collection", concurrentMarkBeginEvent.isConcurrentCollectionStart(), Matchers.is(false));
        AbstractGCEvent<?> concurrentMarkWithPauseEvent = model.get(5);
        Assert.assertThat("event is start of concurrent collection", concurrentMarkWithPauseEvent.isConcurrentCollectionStart(), Matchers.is(true));
    }
}

