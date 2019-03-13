package com.alibaba.csp.sentinel;


import EntryType.IN;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.DefaultNode;
import com.alibaba.csp.sentinel.slotchain.AbstractLinkedProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ProcessorSlot;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.junit.Assert;
import org.junit.Test;

import static Constants.ON;
import static EntryType.IN;


/**
 * Test cases for Sentinel internal {@link CtSph}.
 *
 * @author Eric Zhao
 */
public class CtSphTest {
    private final CtSph ctSph = new CtSph();

    @Test
    public void testCustomContextSyncEntryWithFullContextSize() {
        String resourceName = "testCustomContextSyncEntryWithFullContextSize";
        testCustomContextEntryWithFullContextSize(resourceName, false);
    }

    @Test
    public void testCustomContextAsyncEntryWithFullContextSize() {
        String resourceName = "testCustomContextAsyncEntryWithFullContextSize";
        testCustomContextEntryWithFullContextSize(resourceName, true);
    }

    @Test
    public void testDefaultContextSyncEntryWithFullContextSize() {
        String resourceName = "testDefaultContextSyncEntryWithFullContextSize";
        testDefaultContextEntryWithFullContextSize(resourceName, false);
    }

    @Test
    public void testDefaultContextAsyncEntryWithFullContextSize() {
        String resourceName = "testDefaultContextAsyncEntryWithFullContextSize";
        testDefaultContextEntryWithFullContextSize(resourceName, true);
    }

    @Test
    public void testEntryAndAsyncEntryWhenSwitchOff() {
        // Turn off the switch.
        ON = false;
        String resourceNameA = "resSync";
        String resourceNameB = "resAsync";
        ResourceWrapper resourceWrapperA = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceNameA, IN);
        ResourceWrapper resourceWrapperB = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceNameB, IN);
        // Prepare a slot that "should not pass". If entered the slot, exception will be thrown.
        addShouldNotPassSlotFor(resourceWrapperA);
        addShouldNotPassSlotFor(resourceWrapperB);
        Entry entry = null;
        AsyncEntry asyncEntry = null;
        try {
            entry = ctSph.entry(resourceWrapperA, 1);
            asyncEntry = ctSph.asyncEntry(resourceNameB, resourceWrapperB.getType(), 1);
        } catch (BlockException ex) {
            Assert.fail(("Unexpected blocked: " + (ex.getClass().getCanonicalName())));
        } finally {
            if (asyncEntry != null) {
                asyncEntry.exit();
            }
            if (entry != null) {
                entry.exit();
            }
            ON = true;
        }
    }

    @Test
    public void testAsyncEntryNormalPass() {
        String resourceName = "testAsyncEntryNormalPass";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, IN);
        AsyncEntry entry = null;
        // Prepare a slot that "should pass".
        CtSphTest.ShouldPassSlot slot = addShouldPassSlotFor(resourceWrapper);
        Assert.assertFalse(((slot.entered) || (slot.exited)));
        ContextUtil.enter("abc");
        Entry previousEntry = ContextUtil.getContext().getCurEntry();
        try {
            entry = ctSph.asyncEntry(resourceName, IN, 1);
            Assert.assertTrue(slot.entered);
            Assert.assertFalse(slot.exited);
            Context asyncContext = entry.getAsyncContext();
            Assert.assertNotNull(asyncContext);
            Assert.assertSame(entry, asyncContext.getCurEntry());
            Assert.assertNotSame("The async entry should not be added to current context", entry, ContextUtil.getContext().getCurEntry());
            Assert.assertSame(previousEntry, ContextUtil.getContext().getCurEntry());
        } catch (BlockException ex) {
            Assert.fail(("Unexpected blocked: " + (ex.getClass().getCanonicalName())));
        } finally {
            if (entry != null) {
                Context asyncContext = entry.getAsyncContext();
                entry.exit();
                Assert.assertTrue(slot.exited);
                Assert.assertNull(entry.getAsyncContext());
                Assert.assertSame(previousEntry, asyncContext.getCurEntry());
            }
            ContextUtil.exit();
        }
    }

    @Test
    public void testAsyncEntryNestedInSyncEntryNormalBlocked() {
        String previousResourceName = "fff";
        String resourceName = "testAsyncEntryNestedInSyncEntryNormalBlocked";
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(resourceName, IN);
        // Prepare a slot that "must block".
        CtSphTest.MustBlockSlot slot = addMustBlockSlot(resourceWrapper);
        Assert.assertFalse(slot.exited);
        // Previous entry should pass.
        addShouldPassSlotFor(new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper(previousResourceName, IN));
        ContextUtil.enter(("bcd-" + (System.currentTimeMillis())));
        AsyncEntry entry = null;
        Entry syncEntry = null;
        Entry previousEntry = null;
        try {
            // First enter a sync resource.
            syncEntry = ctSph.entry(previousResourceName, IN, 1);
            // Record current entry (previous for next).
            previousEntry = ContextUtil.getContext().getCurEntry();
            // Then enter an async resource.
            entry = ctSph.asyncEntry(resourceName, IN, 1);
            // Should not pass here.
        } catch (BlockException ex) {
            Assert.assertNotNull(previousEntry);
            Assert.assertNull(entry);
            Assert.assertTrue(slot.exited);
            Assert.assertSame(previousEntry, ContextUtil.getContext().getCurEntry());
            return;
        } finally {
            Assert.assertNull(entry);
            Assert.assertNotNull(syncEntry);
            syncEntry.exit();
            ContextUtil.exit();
        }
        Assert.fail("This async entry is expected to be blocked");
    }

    @Test
    public void testEntryAmountExceededForSyncEntry() {
        testEntryAmountExceeded(false);
    }

    @Test
    public void testEntryAmountExceededForAsyncEntry() {
        testEntryAmountExceeded(true);
    }

    @Test
    public void testLookUpSlotChain() {
        ResourceWrapper r1 = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("firstRes", IN);
        Assert.assertFalse(CtSph.getChainMap().containsKey(r1));
        ProcessorSlot<Object> chainR1 = ctSph.lookProcessChain(r1);
        Assert.assertNotNull("The slot chain for r1 should be created", chainR1);
        Assert.assertSame("Should return the cached slot chain once it has been created", chainR1, ctSph.lookProcessChain(r1));
        fillFullResources();
        ResourceWrapper r2 = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("secondRes", IN);
        Assert.assertFalse(CtSph.getChainMap().containsKey(r2));
        Assert.assertNull("The slot chain for r2 should not be created because amount exceeded", ctSph.lookProcessChain(r2));
        Assert.assertNull(ctSph.lookProcessChain(r2));
    }

    private class ShouldNotPassSlot extends AbstractLinkedProcessorSlot<DefaultNode> {
        @Override
        public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode param, int count, boolean prioritized, Object... args) {
            throw new IllegalStateException("Should not enter this slot!");
        }

        @Override
        public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
            throw new IllegalStateException("Should not exit this slot!");
        }
    }

    private class MustBlockSlot extends AbstractLinkedProcessorSlot<DefaultNode> {
        boolean exited = false;

        @Override
        public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode param, int count, boolean prioritized, Object... args) throws Throwable {
            throw new BlockException("custom") {};
        }

        @Override
        public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
            exited = true;
        }
    }

    private class ShouldPassSlot extends AbstractLinkedProcessorSlot<DefaultNode> {
        boolean entered = false;

        boolean exited = false;

        @Override
        public void entry(Context context, ResourceWrapper resourceWrapper, DefaultNode param, int count, boolean prioritized, Object... args) {
            entered = true;
        }

        @Override
        public void exit(Context context, ResourceWrapper resourceWrapper, int count, Object... args) {
            exited = true;
        }
    }
}

