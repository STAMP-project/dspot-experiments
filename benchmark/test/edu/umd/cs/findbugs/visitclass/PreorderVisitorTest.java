/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2008 University of Maryland
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
 * @author bill.pugh
 */
public class PreorderVisitorTest {
    @Test
    public void testGetNumberArguments() {
        Assert.assertEquals(0, PreorderVisitor.getNumberArguments("()V"));
        Assert.assertEquals(0, PreorderVisitor.getNumberArguments("()I"));
        Assert.assertEquals(0, PreorderVisitor.getNumberArguments("()J"));
        Assert.assertEquals(1, PreorderVisitor.getNumberArguments("(I)V"));
        Assert.assertEquals(1, PreorderVisitor.getNumberArguments("(I)I"));
        Assert.assertEquals(1, PreorderVisitor.getNumberArguments("(J)I"));
        Assert.assertEquals(1, PreorderVisitor.getNumberArguments("([J)I"));
        Assert.assertEquals(1, PreorderVisitor.getNumberArguments("([Ljava/lang/String;)I"));
        Assert.assertEquals(3, PreorderVisitor.getNumberArguments("(J[Ljava/lang/String;J)I"));
    }
}

