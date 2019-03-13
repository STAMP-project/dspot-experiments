/**
 * Copyright 2012, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jf.dexlib2.util;


import com.google.common.collect.ImmutableList;
import junit.framework.Assert;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.immutable.ImmutableMethodImplementation;
import org.jf.dexlib2.immutable.reference.ImmutableStringReference;
import org.jf.dexlib2.immutable.reference.ImmutableTypeReference;
import org.jf.util.ExceptionWithContext;
import org.junit.Test;


public class InstructionOffsetMapTest {
    @Test
    public void testInstructionOffsetMap() {
        ImmutableList<ImmutableInstruction> instructions = /* 00: 0x00 */
        /* 01: 0x01 */
        /* 02: 0x02 */
        /* 03: 0x03 */
        /* 04: 0x04 */
        /* 05: 0x05 */
        /* 06: 0x07 */
        /* 07: 0x09 */
        /* 08: 0x0b */
        /* 09: 0x0d */
        /* 10: 0x0f */
        /* 11: 0x11 */
        /* 12: 0x13 */
        /* 13: 0x15 */
        /* 14: 0x17 */
        /* 15: 0x19 */
        /* 16: 0x1b */
        /* 17: 0x1d */
        /* 18: 0x20 */
        /* 19: 0x23 */
        /* 20: 0x26 */
        /* 21: 0x29 */
        /* 22: 0x2c */
        /* 23: 0x2f */
        /* 24: 0x32 */
        /* 25: 0x37 */
        ImmutableList.of(new ImmutableInstruction10t(Opcode.GOTO, 1), new ImmutableInstruction10x(Opcode.NOP), new ImmutableInstruction11n(Opcode.CONST_4, 2, 3), new ImmutableInstruction11x(Opcode.RETURN, 4), new ImmutableInstruction12x(Opcode.ARRAY_LENGTH, 5, 6), new ImmutableInstruction20t(Opcode.GOTO_16, 7), new ImmutableInstruction21c(Opcode.CONST_STRING, 8, new ImmutableStringReference("blah")), new ImmutableInstruction21ih(Opcode.CONST_HIGH16, 9, 65536), new ImmutableInstruction21lh(Opcode.CONST_WIDE_HIGH16, 10, 281474976710656L), new ImmutableInstruction21s(Opcode.CONST_16, 11, 12), new ImmutableInstruction21t(Opcode.IF_EQZ, 12, 13), new ImmutableInstruction22b(Opcode.ADD_INT_LIT8, 14, 15, 16), new ImmutableInstruction22c(Opcode.INSTANCE_OF, 0, 1, new ImmutableTypeReference("Ltype;")), new ImmutableInstruction22s(Opcode.ADD_INT_LIT16, 2, 3, 17), new ImmutableInstruction22t(Opcode.IF_EQ, 4, 5, 18), new ImmutableInstruction22x(Opcode.MOVE_FROM16, 19, 20), new ImmutableInstruction23x(Opcode.AGET, 21, 22, 23), new ImmutableInstruction30t(Opcode.GOTO_32, 24), new ImmutableInstruction31c(Opcode.CONST_STRING_JUMBO, 25, new ImmutableStringReference("this is a string")), new ImmutableInstruction31i(Opcode.CONST, 26, 27), new ImmutableInstruction31t(Opcode.FILL_ARRAY_DATA, 28, 29), new ImmutableInstruction32x(Opcode.MOVE_16, 30, 31), new ImmutableInstruction35c(Opcode.FILLED_NEW_ARRAY, 0, 0, 0, 0, 0, 0, new ImmutableTypeReference("Ltype;")), new ImmutableInstruction3rc(Opcode.FILLED_NEW_ARRAY_RANGE, 0, 0, new ImmutableTypeReference("Ltype;")), new ImmutableInstruction51l(Opcode.CONST_WIDE, 32, 33), new ImmutableInstruction10t(Opcode.GOTO, 1));
        ImmutableMethodImplementation impl = new ImmutableMethodImplementation(33, instructions, null, null);
        InstructionOffsetMap instructionOffsetMap = new InstructionOffsetMap(instructions);
        int[] expectedOffsets = new int[]{ 0, 1, 2, 3, 4, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 32, 35, 38, 41, 44, 47, 50, 55 };
        for (int i = 0; i < (instructions.size()); i++) {
            Assert.assertEquals(expectedOffsets[i], instructionOffsetMap.getInstructionCodeOffset(i));
            Assert.assertEquals(i, instructionOffsetMap.getInstructionIndexAtCodeOffset(expectedOffsets[i], true));
            Assert.assertEquals(i, instructionOffsetMap.getInstructionIndexAtCodeOffset(expectedOffsets[i], false));
        }
        int instructionIndex = -1;
        for (int codeOffset = 0; codeOffset <= (expectedOffsets[((expectedOffsets.length) - 1)]); codeOffset++) {
            if (codeOffset == (expectedOffsets[(instructionIndex + 1)])) {
                // this offset is at the beginning of an instruction
                instructionIndex++;
            } else {
                // this offset is in the middle of an instruction
                Assert.assertEquals(instructionIndex, instructionOffsetMap.getInstructionIndexAtCodeOffset(codeOffset, false));
                try {
                    instructionOffsetMap.getInstructionIndexAtCodeOffset(codeOffset, true);
                    Assert.fail(String.format("Exception exception didn't occur for code offset 0x%x", codeOffset));
                } catch (ExceptionWithContext ex) {
                    // expected exception
                }
            }
        }
        Assert.assertEquals(((expectedOffsets.length) - 1), instructionOffsetMap.getInstructionIndexAtCodeOffset(((expectedOffsets[((expectedOffsets.length) - 1)]) + 1), false));
        Assert.assertEquals(((expectedOffsets.length) - 1), instructionOffsetMap.getInstructionIndexAtCodeOffset(((expectedOffsets[((expectedOffsets.length) - 1)]) + 10), false));
    }
}

