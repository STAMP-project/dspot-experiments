/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.vm;


import Program.StackTooSmallException;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


/**
 * Simple tests for VM Arithmetic Operations
 */
public class VMArithmeticOpTest extends VMBaseOpTest {
    // ADD OP mal
    @Test
    public void testADD_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x02 PUSH1 0x02 ADD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000004";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // ADD OP
    @Test
    public void testADD_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1002 PUSH1 0x02 ADD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000001004";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // ADD OP
    @Test
    public void testADD_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1002 PUSH6 0x123456789009 ADD"), invoke);
        String s_expected_1 = "000000000000000000000000000000000000000000000000000012345678A00B";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // ADD OP mal
    @Test(expected = StackTooSmallException.class)
    public void testADD_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 ADD"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // ADDMOD OP mal
    @Test
    public void testADDMOD_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x02 PUSH1 0x02 PUSH1 0x03 ADDMOD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000001";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertTrue(program.isStopped());
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // ADDMOD OP
    @Test
    public void testADDMOD_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1000 PUSH1 0x02 PUSH2 0x1002 ADDMOD PUSH1 0x00"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000004";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertFalse(program.isStopped());
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // ADDMOD OP
    @Test
    public void testADDMOD_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1002 PUSH6 0x123456789009 PUSH1 0x02 ADDMOD"), invoke);
        String s_expected_1 = "000000000000000000000000000000000000000000000000000000000000093B";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertTrue(program.isStopped());
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // ADDMOD OP mal
    @Test(expected = StackTooSmallException.class)
    public void testADDMOD_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 ADDMOD"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // MUL OP
    @Test
    public void testMUL_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x03 PUSH1 0x02 MUL"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000006";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MUL OP
    @Test
    public void testMUL_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH3 0x222222 PUSH1 0x03 MUL"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000666666";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MUL OP
    @Test
    public void testMUL_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH3 0x222222 PUSH3 0x333333 MUL"), invoke);
        String s_expected_1 = "000000000000000000000000000000000000000000000000000006D3A05F92C6";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MUL OP mal
    @Test(expected = StackTooSmallException.class)
    public void testMUL_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x01 MUL"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // MULMOD OP
    @Test
    public void testMULMOD_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x03 PUSH1 0x02 PUSH1 0x04 MULMOD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000002";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MULMOD OP
    @Test
    public void testMULMOD_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH3 0x222222 PUSH1 0x03 PUSH1 0x04 MULMOD"), invoke);
        String s_expected_1 = "000000000000000000000000000000000000000000000000000000000000000C";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MULMOD OP
    @Test
    public void testMULMOD_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH3 0x222222 PUSH3 0x333333 PUSH3 0x444444 MULMOD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MULMOD OP mal
    @Test(expected = StackTooSmallException.class)
    public void testMULMOD_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x01 MULMOD"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // DIV OP
    @Test
    public void testDIV_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x02 PUSH1 0x04 DIV"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000002";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // DIV OP
    @Test
    public void testDIV_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x33 PUSH1 0x99 DIV"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000003";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // DIV OP
    @Test
    public void testDIV_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0x99 DIV"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000004";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // DIV OP
    @Test
    public void testDIV_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x15 PUSH1 0x99 DIV"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000007";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // DIV OP
    @Test
    public void testDIV_5() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x04 PUSH1 0x07 DIV"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000001";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // DIV OP
    @Test(expected = StackTooSmallException.class)
    public void testDIV_6() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x07 DIV"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // SDIV OP
    @Test
    public void testSDIV_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x03E8 PUSH32 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC18 SDIV"), invoke);
        String s_expected_1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SDIV OP
    @Test
    public void testSDIV_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0xFF PUSH1 0xFF SDIV"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000001";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SDIV OP
    @Test
    public void testSDIV_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x00 PUSH1 0xFF SDIV"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SDIV OP mal
    @Test(expected = StackTooSmallException.class)
    public void testSDIV_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0xFF SDIV"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // SUB OP
    @Test
    public void testSUB_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x04 PUSH1 0x06 SUB"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000002";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SUB OP
    @Test
    public void testSUB_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x4444 PUSH2 0x6666 SUB"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000002222";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SUB OP
    @Test
    public void testSUB_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x4444 PUSH4 0x99996666 SUB"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000099992222";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SUB OP mal
    @Test(expected = StackTooSmallException.class)
    public void testSUB_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH4 0x99996666 SUB"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // EXP OP mal
    @Test(expected = StackTooSmallException.class)
    public void testEXP_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH3 0x123456 EXP"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // MOD OP
    @Test
    public void testMOD_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x03 PUSH1 0x04 MOD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000001";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MOD OP
    @Test
    public void testMOD_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x012C PUSH2 0x01F4 MOD"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000000000C8";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MOD OP
    @Test
    public void testMOD_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x04 PUSH1 0x02 MOD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000002";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MOD OP mal
    @Test(expected = StackTooSmallException.class)
    public void testMOD_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x04 MOD"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // SMOD OP
    @Test
    public void testSMOD_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x03 PUSH1 0x04 SMOD"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000001";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SMOD OP
    @Test
    public void testSMOD_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile(("PUSH32 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFE2 "// -30
         + ("PUSH32 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF56 "// -170
         + "SMOD"))), invoke);
        String s_expected_1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEC";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SMOD OP
    @Test
    public void testSMOD_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile(("PUSH32 0x000000000000000000000000000000000000000000000000000000000000001E "// 30
         + ("PUSH32 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF56 "// -170
         + "SMOD"))), invoke);
        String s_expected_1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEC";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SMOD OP mal
    @Test(expected = StackTooSmallException.class)
    public void testSMOD_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile(("PUSH32 0x000000000000000000000000000000000000000000000000000000000000001E "// 30
         + "SMOD")), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }
}

