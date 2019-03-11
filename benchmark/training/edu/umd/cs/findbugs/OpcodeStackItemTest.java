/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2005, University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.umd.cs.findbugs;


import OpcodeStack.Item;
import org.junit.Assert;
import org.junit.Test;


public class OpcodeStackItemTest {
    @Test
    public void testMergeIntAndZero() {
        OpcodeStack.Item intItem = new OpcodeStack.Item("I");
        OpcodeStack.Item zeroItem = new OpcodeStack.Item("I", 0);
        OpcodeStack.Item m1 = Item.merge(intItem, zeroItem);
        Assert.assertNull(m1.getConstant());
        OpcodeStack.Item m2 = Item.merge(zeroItem, intItem);
        Assert.assertNull(m2.getConstant());
    }

    @Test
    public void testMergeTypeOnly() {
        OpcodeStack.Item intOnly = Item.typeOnly("I");
        OpcodeStack.Item zeroItem = new OpcodeStack.Item("I", 0);
        OpcodeStack.Item m1 = Item.merge(intOnly, zeroItem);
        Assert.assertEquals(0, m1.getConstant());
        OpcodeStack.Item m2 = Item.merge(zeroItem, intOnly);
        Assert.assertEquals(0, m2.getConstant());
    }

    private static final String NEW_ITEM_KIND_NAME = "newItemKindName";

    @Test
    public void testDefineNewItemKind() {
        int defined = Item.defineSpecialKind(OpcodeStackItemTest.NEW_ITEM_KIND_NAME);
        Assert.assertEquals(OpcodeStackItemTest.NEW_ITEM_KIND_NAME, Item.getSpecialKindName(defined).get());
    }

    @Test
    public void testDefinedItemKindIsUsedInToStringMethod() {
        int defined = Item.defineSpecialKind(OpcodeStackItemTest.NEW_ITEM_KIND_NAME);
        OpcodeStack.Item intItem = new OpcodeStack.Item("I");
        intItem.setSpecialKind(defined);
        String result = intItem.toString();
        Assert.assertTrue(("Item.toString() does not use proper name of special kind:" + result), result.contains(OpcodeStackItemTest.NEW_ITEM_KIND_NAME));
    }
}

