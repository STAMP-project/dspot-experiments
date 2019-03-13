/**
 * Copyright (c) 2016 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import Opcode.ANEWARRAY;
import Opcode.MULTIANEWARRAY;
import Opcode.NEW;
import Opcode.NEWARRAY;
import java.util.HashSet;
import java.util.Set;
import org.adoptopenjdk.jitwatch.model.bytecode.Opcode;
import org.junit.Assert;
import org.junit.Test;


public class TestOpcode {
    @Test
    public void testIsAllocation() {
        Set<Opcode> allocations = new HashSet<>();
        for (Opcode opcode : Opcode.values()) {
            if (opcode.isAllocation()) {
                allocations.add(opcode);
            }
        }
        Assert.assertEquals(4, allocations.size());
        Assert.assertTrue(allocations.contains(NEW));
        Assert.assertTrue(allocations.contains(NEWARRAY));
        Assert.assertTrue(allocations.contains(ANEWARRAY));
        Assert.assertTrue(allocations.contains(MULTIANEWARRAY));
    }
}

