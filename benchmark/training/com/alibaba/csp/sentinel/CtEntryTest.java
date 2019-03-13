package com.alibaba.csp.sentinel;


import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.context.NullContext;
import com.alibaba.csp.sentinel.node.Node;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static EntryType.IN;


/**
 *
 *
 * @author Eric Zhao
 */
public class CtEntryTest {
    @Test
    public void testExitNotMatchCurEntry() {
        String contextName = "context-rpc";
        ContextUtil.enter(contextName);
        Context context = ContextUtil.getContext();
        CtEntry entry1 = null;
        CtEntry entry2 = null;
        try {
            entry1 = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("res1", IN), null, ContextUtil.getContext());
            Assert.assertSame(entry1, context.getCurEntry());
            entry2 = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("res2", IN), null, ContextUtil.getContext());
            Assert.assertSame(entry2, context.getCurEntry());
            // Forget to exit for entry 2...
            // Directly exit for entry 1, then boom...
            entry1.exit();
        } catch (ErrorEntryFreeException ex) {
            Assert.assertNotNull(entry1);
            Assert.assertNotNull(entry2);
            Assert.assertNull(entry1.context);
            Assert.assertNull(entry2.context);
            Assert.assertNull(context.getCurEntry());
            return;
        } finally {
            ContextUtil.exit();
        }
        Assert.fail("Mismatch entry-exit should throw an ErrorEntryFreeException");
    }

    @Test
    public void testExitLastEntryWithDefaultContext() {
        final Context defaultContext = getFakeDefaultContext();
        ContextUtil.runOnContext(defaultContext, new Runnable() {
            @Override
            public void run() {
                CtEntry entry = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("res", EntryType.IN), null, ContextUtil.getContext());
                Assert.assertSame(entry, defaultContext.getCurEntry());
                Assert.assertSame(defaultContext, ContextUtil.getContext());
                entry.exit();
                Assert.assertNull(defaultContext.getCurEntry());
                // Default context will be automatically exited.
                Assert.assertNull(ContextUtil.getContext());
            }
        });
    }

    @Test
    public void testExitTwoLastEntriesWithCustomContext() {
        String contextName = "context-rpc";
        ContextUtil.enter(contextName);
        Context context = ContextUtil.getContext();
        try {
            CtEntry entry1 = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("resA", IN), null, context);
            entry1.exit();
            Assert.assertEquals(context, ContextUtil.getContext());
            CtEntry entry2 = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("resB", IN), null, context);
            entry2.exit();
            Assert.assertEquals(context, ContextUtil.getContext());
        } finally {
            ContextUtil.exit();
            Assert.assertNull(ContextUtil.getContext());
        }
    }

    @Test
    public void testEntryAndExitWithNullContext() {
        Context context = new NullContext();
        CtEntry entry = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("testEntryAndExitWithNullContext", IN), null, context);
        Assert.assertNull(context.getCurEntry());
        entry.exit();
        Assert.assertNull(context.getCurEntry());
        // Won't true exit, so the context won't be cleared.
        Assert.assertEquals(context, entry.context);
    }

    @Test
    public void testGetLastNode() {
        Context context = new NullContext();
        CtEntry entry = new CtEntry(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("testGetLastNode", IN), null, context);
        Assert.assertNull(entry.parent);
        Assert.assertNull(entry.getLastNode());
        Entry parentEntry = Mockito.mock(Entry.class);
        Node node = Mockito.mock(Node.class);
        Mockito.when(parentEntry.getCurNode()).thenReturn(node);
        entry.parent = parentEntry;
        Assert.assertSame(node, entry.getLastNode());
    }
}

