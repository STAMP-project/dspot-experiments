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


import Const.IFEQ;
import Const.IFGE;
import Const.IFLT;
import Const.IFNE;
import Const.IFNONNULL;
import Const.IFNULL;
import Const.IF_ACMPEQ;
import Const.IF_ACMPNE;
import Const.IF_ICMPEQ;
import Const.IF_ICMPGE;
import Const.IF_ICMPLT;
import Const.IF_ICMPNE;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pugh
 */
public class DismantleBytecodeTest {
    @Test
    public void testAreOppositeBranches() {
        Assert.assertTrue(DismantleBytecode.areOppositeBranches(IF_ACMPEQ, IF_ACMPNE));
        Assert.assertTrue(DismantleBytecode.areOppositeBranches(IF_ICMPEQ, IF_ICMPNE));
        Assert.assertTrue(DismantleBytecode.areOppositeBranches(IF_ICMPLT, IF_ICMPGE));
        Assert.assertTrue(DismantleBytecode.areOppositeBranches(IFNE, IFEQ));
        Assert.assertTrue(DismantleBytecode.areOppositeBranches(IFLT, IFGE));
        Assert.assertTrue(DismantleBytecode.areOppositeBranches(IFNULL, IFNONNULL));
    }
}

