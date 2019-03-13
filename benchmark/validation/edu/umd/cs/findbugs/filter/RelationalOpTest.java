/**
 * Contributions to SpotBugs
 * Copyright (C) 2017, kengo
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
package edu.umd.cs.findbugs.filter;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 3.1
 */
public class RelationalOpTest {
    @Test
    public void test() {
        Assert.assertTrue(RelationalOp.byName("EQ").check("A", "A"));
        Assert.assertFalse(RelationalOp.byName("EQ").check("B", "C"));
        Assert.assertFalse(RelationalOp.byName("NEQ").check("A", "A"));
        Assert.assertTrue(RelationalOp.byName("NEQ").check("B", "C"));
        Assert.assertTrue(RelationalOp.byName("GEQ").check("A", "A"));
        Assert.assertFalse(RelationalOp.byName("GEQ").check("B", "C"));
        Assert.assertTrue(RelationalOp.byName("GEQ").check("E", "D"));
        Assert.assertFalse(RelationalOp.byName("GT").check("A", "A"));
        Assert.assertFalse(RelationalOp.byName("GT").check("B", "C"));
        Assert.assertTrue(RelationalOp.byName("GT").check("E", "D"));
        Assert.assertTrue(RelationalOp.byName("LEQ").check("A", "A"));
        Assert.assertTrue(RelationalOp.byName("LEQ").check("B", "C"));
        Assert.assertFalse(RelationalOp.byName("LEQ").check("E", "D"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testByName() {
        RelationalOp.byName("Unknown");
    }
}

