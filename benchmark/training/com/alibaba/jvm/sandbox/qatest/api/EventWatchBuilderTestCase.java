package com.alibaba.jvm.sandbox.qatest.api;


import AccessFlags.ACF_PUBLIC;
import Event.Type.BEFORE;
import Event.Type.CALL_BEFORE;
import Event.Type.CALL_RETURN;
import Event.Type.CALL_THROWS;
import Event.Type.IMMEDIATELY_RETURN;
import Event.Type.IMMEDIATELY_THROWS;
import Event.Type.LINE;
import Event.Type.RETURN;
import Event.Type.THROWS;
import com.alibaba.jvm.sandbox.api.filter.Filter;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.qatest.api.mock.MockForBuilderModuleEventWatcher;
import com.alibaba.jvm.sandbox.qatest.api.util.ApiQaArrayUtils;
import org.junit.Assert;
import org.junit.Test;


public class EventWatchBuilderTestCase {
    @Test
    public void test$$EventWatchBuilder$$normal$$normal() {
        final MockForBuilderModuleEventWatcher mockForBuilderModuleEventWatcher = new MockForBuilderModuleEventWatcher();
        new EventWatchBuilder(mockForBuilderModuleEventWatcher).onClass(String.class).onBehavior("toString").onWatch(new AdviceListener());
        Assert.assertEquals(5, mockForBuilderModuleEventWatcher.getEventTypeArray().length);
        Assert.assertTrue(ApiQaArrayUtils.has(RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(BEFORE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertEquals(1, mockForBuilderModuleEventWatcher.getEventWatchCondition().getOrFilterArray().length);
    }

    @Test
    public void test$$EventWatchBuilder$$normal$$all() {
        final MockForBuilderModuleEventWatcher mockForBuilderModuleEventWatcher = new MockForBuilderModuleEventWatcher();
        new EventWatchBuilder(mockForBuilderModuleEventWatcher).onClass(String.class).onBehavior("toString").onWatching().withCall().withLine().onWatch(new AdviceListener());
        Assert.assertEquals(9, mockForBuilderModuleEventWatcher.getEventTypeArray().length);
        Assert.assertTrue(ApiQaArrayUtils.has(RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(BEFORE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(CALL_BEFORE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(CALL_THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(CALL_RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(LINE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertEquals(1, mockForBuilderModuleEventWatcher.getEventWatchCondition().getOrFilterArray().length);
    }

    @Test
    public void test$$EventWatchBuilder$$normal$$CallOnly() {
        final MockForBuilderModuleEventWatcher mockForBuilderModuleEventWatcher = new MockForBuilderModuleEventWatcher();
        new EventWatchBuilder(mockForBuilderModuleEventWatcher).onClass(String.class).onBehavior("toString").onWatching().withCall().onWatch(new AdviceListener());
        Assert.assertEquals(8, mockForBuilderModuleEventWatcher.getEventTypeArray().length);
        Assert.assertTrue(ApiQaArrayUtils.has(RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(BEFORE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(CALL_BEFORE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(CALL_THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(CALL_RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertFalse(ApiQaArrayUtils.has(LINE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
    }

    @Test
    public void test$$EventWatchBuilder$$normal$$LineOnly() {
        final MockForBuilderModuleEventWatcher mockForBuilderModuleEventWatcher = new MockForBuilderModuleEventWatcher();
        new EventWatchBuilder(mockForBuilderModuleEventWatcher).onClass(String.class).onBehavior("toString").onWatching().withLine().onWatch(new AdviceListener());
        Assert.assertEquals(6, mockForBuilderModuleEventWatcher.getEventTypeArray().length);
        Assert.assertTrue(ApiQaArrayUtils.has(RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(BEFORE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertFalse(ApiQaArrayUtils.has(CALL_BEFORE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertFalse(ApiQaArrayUtils.has(CALL_THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertFalse(ApiQaArrayUtils.has(CALL_RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(LINE, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_RETURN, mockForBuilderModuleEventWatcher.getEventTypeArray()));
        Assert.assertTrue(ApiQaArrayUtils.has(IMMEDIATELY_THROWS, mockForBuilderModuleEventWatcher.getEventTypeArray()));
    }

    @Test
    public void test$$EventWatchBuilder$$regex() {
        final MockForBuilderModuleEventWatcher mockForBuilderModuleEventWatcher = new MockForBuilderModuleEventWatcher();
        onClass("java\\.lang\\.String").onBehavior("<init>").withParameterTypes("byte\\[\\]").hasAnnotationTypes(".*Override").hasExceptionTypes(".*Exception").onWatch(new AdviceListener());
        Assert.assertEquals(1, mockForBuilderModuleEventWatcher.getEventWatchCondition().getOrFilterArray().length);
        final Filter filter = mockForBuilderModuleEventWatcher.getEventWatchCondition().getOrFilterArray()[0];
        Assert.assertTrue(filter.doClassFilter(ACF_PUBLIC, String.class.getName(), null, null, null));
        Assert.assertFalse(filter.doClassFilter(ACF_PUBLIC, Integer.class.getName(), null, null, null));
        Assert.assertTrue(filter.doMethodFilter(ACF_PUBLIC, "<init>", new String[]{ byte[].class.getCanonicalName() }, new String[]{ RuntimeException.class.getName(), IllegalAccessException.class.getName() }, new String[]{ Override.class.getName() }));
        Assert.assertFalse(filter.doMethodFilter(ACF_PUBLIC, "<cinit>", null, null, null));
        Assert.assertFalse(filter.doMethodFilter(ACF_PUBLIC, "<init>", new String[]{ short[].class.getCanonicalName() }, null, null));
        Assert.assertFalse(filter.doMethodFilter(ACF_PUBLIC, "<init>", new String[]{ byte[].class.getCanonicalName() }, new String[]{ Integer.class.getName() }, null));
    }
}

