package com.alibaba.csp.sentinel;


import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextTestUtil;
import com.alibaba.csp.sentinel.context.ContextUtil;
import org.junit.Assert;
import org.junit.Test;

import static EntryType.IN;
import static EntryType.OUT;


/**
 * Test cases for {@link AsyncEntry}.
 *
 * @author Eric Zhao
 * @since 0.2.0
 */
public class AsyncEntryTest {
    @Test
    public void testCleanCurrentEntryInLocal() {
        final String contextName = "abc";
        try {
            ContextUtil.enter(contextName);
            Context curContext = ContextUtil.getContext();
            Entry previousEntry = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("entry-sync", IN), null, curContext);
            AsyncEntry entry = new AsyncEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("testCleanCurrentEntryInLocal", OUT), null, curContext);
            Assert.assertSame(entry, curContext.getCurEntry());
            entry.cleanCurrentEntryInLocal();
            Assert.assertNotSame(entry, curContext.getCurEntry());
            Assert.assertSame(previousEntry, curContext.getCurEntry());
        } finally {
            ContextTestUtil.cleanUpContext();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCleanCurrentEntryInLocalError() {
        final String contextName = "abc";
        try {
            ContextUtil.enter(contextName);
            Context curContext = ContextUtil.getContext();
            AsyncEntry entry = new AsyncEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("testCleanCurrentEntryInLocal", OUT), null, curContext);
            entry.cleanCurrentEntryInLocal();
            entry.cleanCurrentEntryInLocal();
        } finally {
            ContextTestUtil.cleanUpContext();
        }
    }

    @Test
    public void testInitAndGetAsyncContext() {
        final String contextName = "abc";
        final String origin = "xxx";
        try {
            ContextUtil.enter(contextName, origin);
            Context curContext = ContextUtil.getContext();
            AsyncEntry entry = new AsyncEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("testInitAndGetAsyncContext", OUT), null, curContext);
            Assert.assertNull(entry.getAsyncContext());
            entry.initAsyncContext();
            Context asyncContext = entry.getAsyncContext();
            Assert.assertNotNull(asyncContext);
            Assert.assertEquals(contextName, asyncContext.getName());
            Assert.assertEquals(origin, asyncContext.getOrigin());
            Assert.assertSame(curContext.getEntranceNode(), asyncContext.getEntranceNode());
            Assert.assertSame(entry, asyncContext.getCurEntry());
            Assert.assertTrue(asyncContext.isAsync());
        } finally {
            ContextTestUtil.cleanUpContext();
        }
    }

    @Test
    public void testDuplicateInitAsyncContext() {
        Context context = new Context(null, "abc");
        AsyncEntry entry = new AsyncEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("testDuplicateInitAsyncContext", OUT), null, context);
        entry.initAsyncContext();
        Context asyncContext = entry.getAsyncContext();
        // Duplicate init.
        entry.initAsyncContext();
        Assert.assertSame(asyncContext, entry.getAsyncContext());
    }
}

