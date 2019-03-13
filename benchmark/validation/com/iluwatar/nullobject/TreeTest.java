/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.nullobject;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/26/15 - 11:44 PM
 *
 * @author Jeroen Meulemeester
 */
public class TreeTest {
    private TreeTest.InMemoryAppender appender;

    /**
     * During the tests, the same tree structure will be used, shown below. End points will be
     * terminated with the {@link NullNode} instance.
     *
     * <pre>
     * root
     * ??? level1_a
     * ??? ??? level2_a
     * ??? ??? ??? level3_a
     * ??? ??? ??? level3_b
     * ??? ??? level2_b
     * ??? level1_b
     * </pre>
     */
    private static final Node TREE_ROOT;

    static {
        final NodeImpl level1B = new NodeImpl("level1_b", NullNode.getInstance(), NullNode.getInstance());
        final NodeImpl level2B = new NodeImpl("level2_b", NullNode.getInstance(), NullNode.getInstance());
        final NodeImpl level3A = new NodeImpl("level3_a", NullNode.getInstance(), NullNode.getInstance());
        final NodeImpl level3B = new NodeImpl("level3_b", NullNode.getInstance(), NullNode.getInstance());
        final NodeImpl level2A = new NodeImpl("level2_a", level3A, level3B);
        final NodeImpl level1A = new NodeImpl("level1_a", level2A, level2B);
        TREE_ROOT = new NodeImpl("root", level1A, level1B);
    }

    /**
     * Verify the number of items in the tree. The root has 6 children so we expect a {@link Node#getTreeSize()} of 7 {@link Node}s in total.
     */
    @Test
    public void testTreeSize() {
        Assertions.assertEquals(7, TreeTest.TREE_ROOT.getTreeSize());
    }

    /**
     * Walk through the tree and verify if every item is handled
     */
    @Test
    public void testWalk() {
        TreeTest.TREE_ROOT.walk();
        Assertions.assertTrue(appender.logContains("root"));
        Assertions.assertTrue(appender.logContains("level1_a"));
        Assertions.assertTrue(appender.logContains("level2_a"));
        Assertions.assertTrue(appender.logContains("level3_a"));
        Assertions.assertTrue(appender.logContains("level3_b"));
        Assertions.assertTrue(appender.logContains("level2_b"));
        Assertions.assertTrue(appender.logContains("level1_b"));
        Assertions.assertEquals(7, appender.getLogSize());
    }

    @Test
    public void testGetLeft() {
        final Node level1 = TreeTest.TREE_ROOT.getLeft();
        Assertions.assertNotNull(level1);
        Assertions.assertEquals("level1_a", level1.getName());
        Assertions.assertEquals(5, level1.getTreeSize());
        final Node level2 = level1.getLeft();
        Assertions.assertNotNull(level2);
        Assertions.assertEquals("level2_a", level2.getName());
        Assertions.assertEquals(3, level2.getTreeSize());
        final Node level3 = level2.getLeft();
        Assertions.assertNotNull(level3);
        Assertions.assertEquals("level3_a", level3.getName());
        Assertions.assertEquals(1, level3.getTreeSize());
        Assertions.assertSame(NullNode.getInstance(), level3.getRight());
        Assertions.assertSame(NullNode.getInstance(), level3.getLeft());
    }

    @Test
    public void testGetRight() {
        final Node level1 = TreeTest.TREE_ROOT.getRight();
        Assertions.assertNotNull(level1);
        Assertions.assertEquals("level1_b", level1.getName());
        Assertions.assertEquals(1, level1.getTreeSize());
        Assertions.assertSame(NullNode.getInstance(), level1.getRight());
        Assertions.assertSame(NullNode.getInstance(), level1.getLeft());
    }

    private class InMemoryAppender extends AppenderBase<ILoggingEvent> {
        private List<ILoggingEvent> log = new LinkedList<>();

        public InMemoryAppender() {
            addAppender(this);
            start();
        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            log.add(eventObject);
        }

        public boolean logContains(String message) {
            return log.stream().anyMatch(( event) -> event.getMessage().equals(message));
        }

        public int getLogSize() {
            return log.size();
        }
    }
}

