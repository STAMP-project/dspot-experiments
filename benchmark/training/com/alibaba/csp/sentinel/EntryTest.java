package com.alibaba.csp.sentinel;


import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slotchain.ResourceWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static EntryType.IN;


/**
 * Test cases for {@link Entry}.
 *
 * @author Eric Zhao
 */
public class EntryTest {
    @Test
    public void testEntryExitCounts() {
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("resA", IN);
        EntryTest.TestEntry entry = new EntryTest.TestEntry(resourceWrapper);
        entry.exit();
        Assert.assertEquals((-1), entry.count);
        entry.exit(9);
        Assert.assertEquals((-10), entry.count);
    }

    @Test
    public void testEntryFieldsGetSet() {
        ResourceWrapper resourceWrapper = new com.alibaba.csp.sentinel.slotchain.StringResourceWrapper("resA", IN);
        Entry entry = new EntryTest.TestEntry(resourceWrapper);
        Assert.assertSame(resourceWrapper, entry.getResourceWrapper());
        Throwable error = new IllegalStateException();
        entry.setError(error);
        Assert.assertSame(error, entry.getError());
        Node curNode = Mockito.mock(Node.class);
        entry.setCurNode(curNode);
        Assert.assertSame(curNode, entry.getCurNode());
        Node originNode = Mockito.mock(Node.class);
        entry.setOriginNode(originNode);
        Assert.assertSame(originNode, entry.getOriginNode());
    }

    private class TestEntry extends Entry {
        int count = 0;

        TestEntry(ResourceWrapper resourceWrapper) {
            super(resourceWrapper);
        }

        @Override
        public void exit(int count, Object... args) throws ErrorEntryFreeException {
            this.count -= count;
        }

        @Override
        protected Entry trueExit(int count, Object... args) throws ErrorEntryFreeException {
            return null;
        }

        @Override
        public Node getLastNode() {
            return null;
        }
    }
}

