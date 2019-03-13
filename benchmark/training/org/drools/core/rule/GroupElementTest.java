/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.rule;


import GroupElement.Type.OR;
import org.drools.core.base.ClassObjectType;
import org.drools.core.test.model.Person;
import org.junit.Assert;
import org.junit.Test;


public class GroupElementTest {
    @Test
    public void testPackNestedAnd() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern1 = new Pattern(0, null);
        and1.addChild(pattern1);
        final Pattern pattern2 = new Pattern(0, null);
        and1.addChild(pattern2);
        Assert.assertEquals(2, and1.getChildren().size());
        Assert.assertSame(pattern1, and1.getChildren().get(0));
        Assert.assertSame(pattern2, and1.getChildren().get(1));
        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild(and1);
        and2.pack();
        Assert.assertEquals(2, and2.getChildren().size());
        Assert.assertSame(pattern1, and2.getChildren().get(0));
        Assert.assertSame(pattern2, and2.getChildren().get(1));
    }

    @Test
    public void testDeclarationOrdering() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern1 = new Pattern(0, new ClassObjectType(Person.class), "x");
        and1.addChild(pattern1);
        final Pattern pattern2 = new Pattern(2, new ClassObjectType(Person.class), "y");
        and1.addChild(pattern2);
        Declaration x1 = ((Declaration) (and1.getInnerDeclarations().get("x")));
        Declaration y1 = ((Declaration) (and1.getInnerDeclarations().get("y")));
        Declaration z1 = ((Declaration) (and1.getInnerDeclarations().get("z")));
        Assert.assertNotNull(x1);
        Assert.assertNotNull(y1);
        Assert.assertNull(z1);
        Assert.assertEquals(2, and1.getChildren().size());
        Assert.assertSame(pattern1, and1.getChildren().get(0));
        Assert.assertSame(pattern2, and1.getChildren().get(1));
        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild(and1);
        final Pattern pattern3 = new Pattern(3, new ClassObjectType(Person.class), "x");
        and2.addChild(pattern3);
        and2.pack();
        Assert.assertEquals(3, and2.getChildren().size());
        Assert.assertSame(pattern1, and2.getChildren().get(0));
        Assert.assertSame(pattern2, and2.getChildren().get(1));
        Assert.assertSame(pattern3, and2.getChildren().get(2));
        Declaration x2 = ((Declaration) (and2.getInnerDeclarations().get("x")));
        Declaration y2 = ((Declaration) (and2.getInnerDeclarations().get("y")));
        Declaration z2 = ((Declaration) (and2.getInnerDeclarations().get("z")));
        Assert.assertNotNull(x2);
        Assert.assertNotNull(y2);
        Assert.assertNull(z2);
        Assert.assertNotSame(x1, x2);
        Assert.assertSame(x2, pattern3.getDeclaration());
        Assert.assertSame(y1, y2);
        Assert.assertSame(z1, z2);
    }

    @Test
    public void testPackNestedOr() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern(0, null);
        or1.addChild(pattern1);
        final Pattern pattern2 = new Pattern(0, null);
        or1.addChild(pattern2);
        Assert.assertEquals(2, or1.getChildren().size());
        Assert.assertSame(pattern1, or1.getChildren().get(0));
        Assert.assertSame(pattern2, or1.getChildren().get(1));
        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild(or1);
        or2.pack();
        Assert.assertEquals(2, or2.getChildren().size());
        Assert.assertSame(pattern1, or2.getChildren().get(0));
        Assert.assertSame(pattern2, or2.getChildren().get(1));
    }

    @Test
    public void testPackNestedExists() {
        final GroupElement exists1 = GroupElementFactory.newExistsInstance();
        final Pattern pattern1 = new Pattern(0, null);
        exists1.addChild(pattern1);
        Assert.assertEquals(1, exists1.getChildren().size());
        Assert.assertSame(pattern1, exists1.getChildren().get(0));
        final GroupElement exists2 = GroupElementFactory.newExistsInstance();
        exists2.addChild(exists1);
        exists2.pack();
        Assert.assertEquals(1, exists2.getChildren().size());
        Assert.assertSame(pattern1, exists2.getChildren().get(0));
    }

    @Test
    public void testAddMultipleChildsIntoNot() {
        final GroupElement not = GroupElementFactory.newNotInstance();
        final Pattern pattern1 = new Pattern(0, null);
        try {
            not.addChild(pattern1);
        } catch (final RuntimeException rde) {
            Assert.fail(("Adding a single child is not supposed to throw Exception for NOT GE: " + (rde.getMessage())));
        }
        final Pattern pattern2 = new Pattern(0, null);
        try {
            not.addChild(pattern2);
            Assert.fail("Adding a second child into a NOT GE should throw Exception");
        } catch (final RuntimeException rde) {
            // everything is fine
        }
    }

    @Test
    public void testAddSingleBranchAnd() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern = new Pattern(0, null);
        and1.addChild(pattern);
        Assert.assertEquals(1, and1.getChildren().size());
        Assert.assertSame(pattern, and1.getChildren().get(0));
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        or1.addChild(and1);
        or1.pack();
        Assert.assertEquals(1, or1.getChildren().size());
        Assert.assertSame(pattern, or1.getChildren().get(0));
    }

    @Test
    public void testAddSingleBranchOr() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern = new Pattern(0, null);
        or1.addChild(pattern);
        Assert.assertEquals(1, or1.getChildren().size());
        Assert.assertSame(pattern, or1.getChildren().get(0));
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild(or1);
        and1.pack();
        Assert.assertEquals(1, and1.getChildren().size());
        Assert.assertSame(pattern, and1.getChildren().get(0));
    }

    /**
     * This test tests deep nested structures, and shall transform this:
     *
     *    AND2
     *     |
     *    OR3
     *     |
     *    OR2
     *     |
     *    AND1
     *     |
     *    OR1
     *    / \
     *   C1  C2
     *
     * Into this:
     *
     *   OR1
     *   / \
     *  C1 C2
     */
    @Test
    public void testDeepNestedStructure() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern(0, null);
        or1.addChild(pattern1);
        final Pattern pattern2 = new Pattern(0, null);
        or1.addChild(pattern2);
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild(or1);
        Assert.assertEquals(1, and1.getChildren().size());
        Assert.assertSame(or1, and1.getChildren().get(0));
        Assert.assertSame(pattern1, or1.getChildren().get(0));
        Assert.assertSame(pattern2, or1.getChildren().get(1));
        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild(and1);
        Assert.assertEquals(1, or2.getChildren().size());
        Assert.assertSame(and1, or2.getChildren().get(0));
        final GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild(or2);
        Assert.assertEquals(1, or2.getChildren().size());
        Assert.assertSame(or2, or3.getChildren().get(0));
        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild(or3);
        Assert.assertEquals(1, and2.getChildren().size());
        Assert.assertSame(or3, and2.getChildren().get(0));
        // Now pack the structure
        and2.pack();
        // and2 now is in fact transformed into an OR
        Assert.assertEquals(OR, and2.getType());
        Assert.assertEquals(2, and2.getChildren().size());
        Assert.assertSame(pattern1, and2.getChildren().get(0));
        Assert.assertSame(pattern2, and2.getChildren().get(1));
    }

    /**
     * This test tests deep nested structures, and shall transform this:
     *
     *      AND2
     *      / \
     *    OR3  C3
     *     |
     *    OR2
     *     |
     *    AND1
     *     |
     *    OR1
     *    / \
     *   C1  C2
     *
     * Into this:
     *
     *     AND2
     *     /  \
     *   OR1  C3
     *   / \
     *  C1 C2
     */
    @Test
    public void testDeepNestedStructureWithMultipleElementsInRoot() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern(0, null);
        or1.addChild(pattern1);
        final Pattern pattern2 = new Pattern(0, null);
        or1.addChild(pattern2);
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild(or1);
        Assert.assertEquals(1, and1.getChildren().size());
        Assert.assertSame(or1, and1.getChildren().get(0));
        Assert.assertSame(pattern1, or1.getChildren().get(0));
        Assert.assertSame(pattern2, or1.getChildren().get(1));
        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild(and1);
        Assert.assertEquals(1, or2.getChildren().size());
        Assert.assertSame(and1, or2.getChildren().get(0));
        final GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild(or2);
        Assert.assertEquals(1, or2.getChildren().size());
        Assert.assertSame(or2, or3.getChildren().get(0));
        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild(or3);
        final Pattern pattern3 = new Pattern(0, null);
        and2.addChild(pattern3);
        Assert.assertEquals(2, and2.getChildren().size());
        Assert.assertSame(or3, and2.getChildren().get(0));
        Assert.assertSame(pattern3, and2.getChildren().get(1));
        // Now pack the structure
        and2.pack();
        // and2 now is in fact transformed into an OR
        Assert.assertTrue(and2.isAnd());
        Assert.assertEquals(2, and2.getChildren().size());
        // order must be the same
        Assert.assertSame(or1, and2.getChildren().get(0));
        Assert.assertSame(pattern3, and2.getChildren().get(1));
        Assert.assertEquals(2, or1.getChildren().size());
        Assert.assertSame(pattern1, or1.getChildren().get(0));
        Assert.assertSame(pattern2, or1.getChildren().get(1));
    }
}

