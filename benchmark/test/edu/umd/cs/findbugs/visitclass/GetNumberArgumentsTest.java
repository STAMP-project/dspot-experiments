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
package edu.umd.cs.findbugs.visitclass;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pugh
 */
public class GetNumberArgumentsTest {
    static String[] simpleTypes = "I J B C D F I S Z".split(" ");

    @Test
    public void testSimpleWithVoidReturnType() {
        for (String s : GetNumberArgumentsTest.simpleTypes) {
            Assert.assertEquals(1, PreorderVisitor.getNumberArguments((("(" + s) + ")V")));
        }
    }

    @Test
    public void testSimpleWithVoidIntegerType() {
        for (String s : GetNumberArgumentsTest.simpleTypes) {
            Assert.assertEquals(1, PreorderVisitor.getNumberArguments((("(" + s) + ")I")));
        }
    }

    @Test
    public void testArrays() {
        for (String s : GetNumberArgumentsTest.simpleTypes) {
            Assert.assertEquals(1, PreorderVisitor.getNumberArguments((("([" + s) + ")V")));
            Assert.assertEquals(1, PreorderVisitor.getNumberArguments((("([[" + s) + ")I")));
        }
    }

    @Test
    public void testStringArguments() {
        for (String s : GetNumberArgumentsTest.simpleTypes) {
            Assert.assertEquals(2, PreorderVisitor.getNumberArguments((("([Ljava/lang/String;" + s) + ")V")));
            Assert.assertEquals(2, PreorderVisitor.getNumberArguments((("([[" + s) + "Ljava/lang/String;)I")));
        }
    }

    @Test
    public void testSimpleObjectArgument() {
        Assert.assertEquals(1, PreorderVisitor.getNumberArguments("(Ledu/umd/cs/findbugs/ba/ClassContext;)V"));
    }
}

