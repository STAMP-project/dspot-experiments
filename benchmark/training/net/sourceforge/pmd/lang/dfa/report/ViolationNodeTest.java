/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.report;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Philip Graf
 */
public final class ViolationNodeTest {
    /**
     * Verifies that two violations nodes with equal
     * {@code filename, beginLine, endLine, beginColumn, endColumn} and
     * {@code variableName} are equal.
     */
    @Test
    public void testEqualsNodeWithTwoEqualViolations() {
        final ViolationNode node1 = createViolationNode("Foo.java", 1, 1, 5, 15, "");
        final ViolationNode node2 = createViolationNode("Foo.java", 1, 1, 5, 15, "");
        Assert.assertTrue("Two equal violations should result in equal nodes", node1.equalsNode(node2));
    }

    /**
     * Verifies that two violations nodes with different {@code filename} are
     * not equal.
     */
    @Test
    public void testEqualsNodeWithTwoDifferentViolationsDifferentFilename() {
        final ViolationNode node1 = createViolationNode("Foo.java", 1, 1, 5, 15, "");
        final ViolationNode node2 = createViolationNode("Bar.java", 1, 1, 5, 15, "");
        Assert.assertFalse("Two violations with different filename should result in not equal nodes", node1.equalsNode(node2));
    }

    /**
     * Verifies that two violations nodes with different {@code beginLine} are
     * not equal.
     */
    @Test
    public void testEqualsNodeWithTwoDifferentViolationsDifferentBeginLine() {
        final ViolationNode node1 = createViolationNode("Foo.java", 1, 2, 5, 15, "");
        final ViolationNode node2 = createViolationNode("Foo.java", 2, 2, 5, 15, "");
        Assert.assertFalse("Two violations with different beginLine should result in not equal nodes", node1.equalsNode(node2));
    }

    /**
     * Verifies that two violations nodes with different {@code endLine} are not
     * equal.
     */
    @Test
    public void testEqualsNodeWithTwoDifferentViolationsDifferentEndLine() {
        final ViolationNode node1 = createViolationNode("Foo.java", 1, 1, 5, 15, "");
        final ViolationNode node2 = createViolationNode("Foo.java", 1, 2, 5, 15, "");
        Assert.assertFalse("Two violations with different endLine should result in not equal nodes", node1.equalsNode(node2));
    }

    /**
     * Verifies that two violations nodes with different {@code beginColumn} are
     * not equal.
     */
    @Test
    public void testEqualsNodeWithTwoDifferentViolationsDifferentBeginColumn() {
        final ViolationNode node1 = createViolationNode("Foo.java", 1, 1, 5, 15, "");
        final ViolationNode node2 = createViolationNode("Foo.java", 1, 1, 7, 15, "");
        Assert.assertFalse("Two violations with different beginColumn should result in not equal nodes", node1.equalsNode(node2));
    }

    /**
     * Verifies that two violations nodes with different {@code endColumn} are
     * not equal.
     */
    @Test
    public void testEqualsNodeWithTwoDifferentViolationsDifferentEndColumn() {
        final ViolationNode node1 = createViolationNode("Foo.java", 1, 1, 5, 15, "");
        final ViolationNode node2 = createViolationNode("Foo.java", 1, 1, 5, 17, "");
        Assert.assertFalse("Two violations with different end column should result in not equal nodes", node1.equalsNode(node2));
    }

    /**
     * Verifies that two violations with different {@code variableName} are not
     * equal.
     */
    @Test
    public void testEqualsNodeWithTwoDifferentViolationsDifferentVariableName() {
        final ViolationNode node1 = createViolationNode("Foo.java", 1, 1, 5, 15, "a");
        final ViolationNode node2 = createViolationNode("Foo.java", 1, 1, 5, 15, "b");
        Assert.assertFalse("Two violations with different variableName should result in not equal nodes", node1.equalsNode(node2));
    }
}

