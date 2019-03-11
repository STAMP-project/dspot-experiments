package com.tagtraum.perf.gcviewer.imp;


import Generation.ALL;
import Generation.YOUNG;
import Type.UJL_PAUSE_FULL;
import Type.UJL_PAUSE_YOUNG;
import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests unified jvm logging parser for parallel gc events.
 */
public class TestDataReaderUJLParallel {
    @Test
    public void parseGcDefaults() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-parallel-gc-defaults.txt");
        Assert.assertThat("size", model.size(), Matchers.is(11));
        Assert.assertThat("amount of gc event types", model.getGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of gc events", model.getGCPause().getN(), Matchers.is(5));
        Assert.assertThat("amount of full gc event types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of full gc events", model.getFullGCPause().getN(), Matchers.is(6));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(0));
        AbstractGCEvent<?> event1 = model.get(0);
        UnittestHelper.testMemoryPauseEvent(event1, "pause young", UJL_PAUSE_YOUNG, 0.006868, (1024 * 32), (1024 * 29), (1024 * 123), YOUNG, false);
        AbstractGCEvent<?> event2 = model.get(2);
        UnittestHelper.testMemoryPauseEvent(event2, "pause full", UJL_PAUSE_FULL, 0.013765, (1024 * 62), (1024 * 61), (1024 * 123), ALL, true);
    }

    @Test
    public void parseGcAllSafepointOsCpu() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-parallel-gc-all,safepoint,os+cpu.txt");
        Assert.assertThat("size", model.size(), Matchers.is(8));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of gc events", model.getGCPause().getN(), Matchers.is(4));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of full gc events", model.getFullGCPause().getN(), Matchers.is(4));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(0));
        AbstractGCEvent<?> event1 = model.get(0);
        UnittestHelper.testMemoryPauseEvent(event1, "pause young", UJL_PAUSE_YOUNG, 0.008112, (1024 * 32), (1024 * 29), (1024 * 123), YOUNG, false);
        // GC(6) Pause Full (Ergonomics)
        AbstractGCEvent<?> event2 = model.get(6);
        UnittestHelper.testMemoryPauseEvent(event2, "pause full", UJL_PAUSE_FULL, 0.008792, (1024 * 95), (1024 * 31), (1024 * 123), ALL, true);
        // GC(7) Pause Young (Allocation Failure)
        AbstractGCEvent<?> event3 = model.get(7);
        UnittestHelper.testMemoryPauseEvent(event3, "pause young 2", UJL_PAUSE_YOUNG, 0.005794, (1024 * 63), (1024 * 63), (1024 * 123), YOUNG, false);
    }
}

