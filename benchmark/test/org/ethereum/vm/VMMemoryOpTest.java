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


import Program.BadJumpDestinationException;
import Program.StackTooSmallException;
import org.ethereum.config.SystemProperties;
import org.ethereum.config.blockchain.DaoHFConfig;
import org.ethereum.core.Repository;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


/**
 * Simple tests for VM Memory, Storage and Flow Operations
 */
public class VMMemoryOpTest extends VMBaseOpTest {
    private static final SystemProperties constantinopleConfig = new SystemProperties() {
        {
            setBlockchainConfig(new org.ethereum.config.blockchain.ConstantinopleConfig(new DaoHFConfig()));
        }
    };

    // PUSH1 OP
    @Test
    public void testPUSH1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0xa0"), invoke);
        String expected = "00000000000000000000000000000000000000000000000000000000000000A0";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH2 OP
    @Test
    public void testPUSH2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0xa0b0"), invoke);
        String expected = "000000000000000000000000000000000000000000000000000000000000A0B0";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH3 OP
    @Test
    public void testPUSH3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH3 0xA0B0C0"), invoke);
        String expected = "0000000000000000000000000000000000000000000000000000000000A0B0C0";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH4 OP
    @Test
    public void testPUSH4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH4 0xA0B0C0D0"), invoke);
        String expected = "00000000000000000000000000000000000000000000000000000000A0B0C0D0";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH5 OP
    @Test
    public void testPUSH5() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH5 0xA0B0C0D0E0"), invoke);
        String expected = "000000000000000000000000000000000000000000000000000000A0B0C0D0E0";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH6 OP
    @Test
    public void testPUSH6() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH6 0xA0B0C0D0E0F0"), invoke);
        String expected = "0000000000000000000000000000000000000000000000000000A0B0C0D0E0F0";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH7 OP
    @Test
    public void testPUSH7() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH7 0xA0B0C0D0E0F0A1"), invoke);
        String expected = "00000000000000000000000000000000000000000000000000A0B0C0D0E0F0A1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH8 OP
    @Test
    public void testPUSH8() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH8 0xA0B0C0D0E0F0A1B1"), invoke);
        String expected = "000000000000000000000000000000000000000000000000A0B0C0D0E0F0A1B1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH9 OP
    @Test
    public void testPUSH9() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH9 0xA0B0C0D0E0F0A1B1C1"), invoke);
        String expected = "0000000000000000000000000000000000000000000000A0B0C0D0E0F0A1B1C1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH10 OP
    @Test
    public void testPUSH10() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH10 0xA0B0C0D0E0F0A1B1C1D1"), invoke);
        String expected = "00000000000000000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH11 OP
    @Test
    public void testPUSH11() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH11 0xA0B0C0D0E0F0A1B1C1D1E1"), invoke);
        String expected = "000000000000000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH12 OP
    @Test
    public void testPUSH12() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH12 0xA0B0C0D0E0F0A1B1C1D1E1F1"), invoke);
        String expected = "0000000000000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH13 OP
    @Test
    public void testPUSH13() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH13 0xA0B0C0D0E0F0A1B1C1D1E1F1A2"), invoke);
        String expected = "00000000000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH14 OP
    @Test
    public void testPUSH14() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH14 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2"), invoke);
        String expected = "000000000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH15 OP
    @Test
    public void testPUSH15() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH15 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2"), invoke);
        String expected = "0000000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH16 OP
    @Test
    public void testPUSH16() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH16 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2"), invoke);
        String expected = "00000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH17 OP
    @Test
    public void testPUSH17() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH17 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2"), invoke);
        String expected = "000000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH18 OP
    @Test
    public void testPUSH18() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH18 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2"), invoke);
        String expected = "0000000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH19 OP
    @Test
    public void testPUSH19() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH19 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3"), invoke);
        String expected = "00000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH20 OP
    @Test
    public void testPUSH20() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH20 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3"), invoke);
        String expected = "000000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH21 OP
    @Test
    public void testPUSH21() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH21 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3"), invoke);
        String expected = "0000000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH22 OP
    @Test
    public void testPUSH22() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH22 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3"), invoke);
        String expected = "00000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH23 OP
    @Test
    public void testPUSH23() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH23 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3"), invoke);
        String expected = "000000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH24 OP
    @Test
    public void testPUSH24() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH24 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3"), invoke);
        String expected = "0000000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH25 OP
    @Test
    public void testPUSH25() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH25 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4"), invoke);
        String expected = "00000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH26 OP
    @Test
    public void testPUSH26() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH26 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4"), invoke);
        String expected = "000000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH27 OP
    @Test
    public void testPUSH27() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH27 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4"), invoke);
        String expected = "0000000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH28 OP
    @Test
    public void testPUSH28() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH28 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4"), invoke);
        String expected = "00000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH29 OP
    @Test
    public void testPUSH29() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH29 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4"), invoke);
        String expected = "000000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH30 OP
    @Test
    public void testPUSH30() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH30 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4F4"), invoke);
        String expected = "0000A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4F4";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH31 OP
    @Test
    public void testPUSH31() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH31 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4F4A1"), invoke);
        String expected = "00A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4F4A1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSH32 OP
    @Test
    public void testPUSH32() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH32 0xA0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4F4A1B1"), invoke);
        String expected = "A0B0C0D0E0F0A1B1C1D1E1F1A2B2C2D2E2F2A3B3C3D3E3F3A4B4C4D4E4F4A1B1";
        program.fullTrace();
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSHN OP not enough data
    @Test
    public void testPUSHN_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0xAA"), invoke);
        String expected = "000000000000000000000000000000000000000000000000000000000000AA00";
        program.fullTrace();
        vm.step(program);
        Assert.assertTrue(program.isStopped());
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PUSHN OP not enough data
    @Test
    public void testPUSHN_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH32 0xAABB"), invoke);
        String expected = "AABB000000000000000000000000000000000000000000000000000000000000";
        program.fullTrace();
        vm.step(program);
        Assert.assertTrue(program.isStopped());
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // POP OP
    @Test
    public void testPOP_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x0000 PUSH1 0x01 PUSH3 0x000002 POP"), invoke);
        String expected = "0000000000000000000000000000000000000000000000000000000000000001";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // POP OP
    @Test
    public void testPOP_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x0000 PUSH1 0x01 PUSH3 0x000002 POP POP"), invoke);
        String expected = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // POP OP mal data
    @Test(expected = StackTooSmallException.class)
    public void testPOP_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x0000 PUSH1 0x01 PUSH3 0x000002 POP POP POP POP"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // DUP1...DUP16 OP
    @Test
    public void testDUPS() {
        for (int i = 1; i < 17; i++) {
            testDUPN_1(i);
        }
    }

    // DUPN OP mal data
    @Test(expected = StackTooSmallException.class)
    public void testDUPN_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("DUP1"), invoke);
        try {
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // SWAP1...SWAP16 OP
    @Test
    public void testSWAPS() {
        for (int i = 1; i < 17; ++i) {
            testSWAPN_1(i);
        }
    }

    // SWAPN OP mal data
    @Test(expected = StackTooSmallException.class)
    public void testSWAPN_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("SWAP1"), invoke);
        try {
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // MSTORE OP
    @Test
    public void testMSTORE_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 PUSH1 0x00 MSTORE"), invoke);
        String expected = "0000000000000000000000000000000000000000000000000000000000001234";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getMemory()));
    }

    // MSTORE OP
    @Test
    public void testMSTORE_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 PUSH1 0x00 MSTORE PUSH2 0x5566 PUSH1 0x20 MSTORE"), invoke);
        String expected = "0000000000000000000000000000000000000000000000000000000000001234" + "0000000000000000000000000000000000000000000000000000000000005566";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getMemory()));
    }

    // MSTORE OP
    @Test
    public void testMSTORE_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 PUSH1 0x00 MSTORE PUSH2 0x5566 PUSH1 0x20 MSTORE PUSH2 0x8888 PUSH1 0x00 MSTORE"), invoke);
        String expected = "0000000000000000000000000000000000000000000000000000000000008888" + "0000000000000000000000000000000000000000000000000000000000005566";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getMemory()));
    }

    // MSTORE OP
    @Test
    public void testMSTORE_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 PUSH1 0xA0 MSTORE"), invoke);
        String expected = "" + ((((("0000000000000000000000000000000000000000000000000000000000000000" + "0000000000000000000000000000000000000000000000000000000000000000") + "0000000000000000000000000000000000000000000000000000000000000000") + "0000000000000000000000000000000000000000000000000000000000000000") + "0000000000000000000000000000000000000000000000000000000000000000") + "0000000000000000000000000000000000000000000000000000000000001234");
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(expected, Hex.toHexString(program.getMemory()));
    }

    // MSTORE OP
    @Test(expected = StackTooSmallException.class)
    public void testMSTORE_5() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 MSTORE"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // MLOAD OP
    @Test
    public void testMLOAD_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x00 MLOAD"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000";
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()));
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // MLOAD OP
    @Test
    public void testMLOAD_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 MLOAD"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000" + ("0000000000000000000000000000000000000000000000000000000000000000" + "0000000000000000000000000000000000000000000000000000000000000000");
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()).toUpperCase());
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // MLOAD OP
    @Test
    public void testMLOAD_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x20 MLOAD"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000" + "0000000000000000000000000000000000000000000000000000000000000000";
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()));
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // MLOAD OP
    @Test
    public void testMLOAD_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 PUSH1 0x20 MSTORE PUSH1 0x20 MLOAD"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000" + "0000000000000000000000000000000000000000000000000000000000001234";
        String s_expected = "0000000000000000000000000000000000000000000000000000000000001234";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()));
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // MLOAD OP
    @Test
    public void testMLOAD_5() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH2 0x1234 PUSH1 0x20 MSTORE PUSH1 0x1F MLOAD"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000" + "0000000000000000000000000000000000000000000000000000000000001234";
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000012";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()));
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // MLOAD OP mal data
    @Test(expected = StackTooSmallException.class)
    public void testMLOAD_6() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("MLOAD"), invoke);
        try {
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // MSTORE8 OP
    @Test
    public void testMSTORE8_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x11 PUSH1 0x00 MSTORE8"), invoke);
        String m_expected = "1100000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()));
    }

    // MSTORE8 OP
    @Test
    public void testMSTORE8_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0x01 MSTORE8"), invoke);
        String m_expected = "0022000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()));
    }

    // MSTORE8 OP
    @Test
    public void testMSTORE8_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0x21 MSTORE8"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000" + "0022000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()));
    }

    // MSTORE8 OP mal
    @Test(expected = StackTooSmallException.class)
    public void testMSTORE8_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 MSTORE8"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // SSTORE OP
    @Test
    public void testSSTORE_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0xAA SSTORE"), invoke);
        String s_expected_key = "00000000000000000000000000000000000000000000000000000000000000AA";
        String s_expected_val = "0000000000000000000000000000000000000000000000000000000000000022";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord key = DataWord.of(Hex.decode(s_expected_key));
        DataWord val = program.getStorage().getStorageValue(invoke.getOwnerAddress().getNoLeadZeroesData(), key);
        Assert.assertEquals(s_expected_val, Hex.toHexString(val.getData()).toUpperCase());
    }

    // SSTORE OP
    @Test
    public void testSSTORE_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0xAA SSTORE PUSH1 0x22 PUSH1 0xBB SSTORE"), invoke);
        String s_expected_key = "00000000000000000000000000000000000000000000000000000000000000BB";
        String s_expected_val = "0000000000000000000000000000000000000000000000000000000000000022";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Repository repository = program.getStorage();
        DataWord key = DataWord.of(Hex.decode(s_expected_key));
        DataWord val = repository.getStorageValue(invoke.getOwnerAddress().getNoLeadZeroesData(), key);
        Assert.assertEquals(s_expected_val, Hex.toHexString(val.getData()).toUpperCase());
    }

    // SSTORE OP
    @Test(expected = StackTooSmallException.class)
    public void testSSTORE_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 SSTORE"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("60006000556000600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(412, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("60006000556001600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(20212, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("60016000556000600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(20212, program.getResult().getGasUsed());
        Assert.assertEquals(19800, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("60016000556002600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(20212, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_5() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("60016000556001600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(20212, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_6() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60006000556000600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(15000, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_7() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60006000556001600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(4800, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_8() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60006000556002600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_9() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60026000556000600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(15000, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_10() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60026000556003600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_11() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60026000556001600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(4800, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_12() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60026000556002600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_13() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60016000556000600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(15000, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_14() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60016000556002600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(5212, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_15() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("60016000556001600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(412, program.getResult().getGasUsed());
        Assert.assertEquals(0, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_16() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("600160005560006000556001600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(40218, program.getResult().getGasUsed());
        Assert.assertEquals(19800, program.getResult().getFutureRefund());
    }

    // SSTORE EIP1283
    @Test
    public void testSSTORE_NET_17() {
        VM vm = new VM();
        setStorageToOne(vm);
        program = new org.ethereum.vm.program.Program(Hex.decode("600060005560016000556000600055"), invoke, VMMemoryOpTest.constantinopleConfig);
        while (!(program.isStopped()))
            vm.step(program);

        Assert.assertEquals(10218, program.getResult().getGasUsed());
        Assert.assertEquals(19800, program.getResult().getFutureRefund());
    }

    // SLOAD OP
    @Test
    public void testSLOAD_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0xAA SLOAD"), invoke);
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // SLOAD OP
    @Test
    public void testSLOAD_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0xAA SSTORE PUSH1 0xAA SLOAD"), invoke);
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000022";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // SLOAD OP
    @Test
    public void testSLOAD_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0xAA SSTORE PUSH1 0x33 PUSH1 0xCC SSTORE PUSH1 0xCC SLOAD"), invoke);
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000033";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // SLOAD OP
    @Test(expected = StackTooSmallException.class)
    public void testSLOAD_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("SLOAD"), invoke);
        try {
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // PC OP
    @Test
    public void testPC_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PC"), invoke);
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // PC OP
    @Test
    public void testPC_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x22 PUSH1 0xAA MSTORE PUSH1 0xAA SLOAD PC"), invoke);
        String s_expected = "0000000000000000000000000000000000000000000000000000000000000008";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // JUMP OP mal data
    @Test(expected = BadJumpDestinationException.class)
    public void testJUMP_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0xAA PUSH1 0xBB PUSH1 0x0E JUMP PUSH1 0xCC PUSH1 0xDD PUSH1 0xEE JUMPDEST PUSH1 0xFF"), invoke);
        String s_expected = "00000000000000000000000000000000000000000000000000000000000000FF";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // JUMP OP mal data
    @Test(expected = BadJumpDestinationException.class)
    public void testJUMP_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x0C PUSH1 0x0C SWAP1 JUMP PUSH1 0xCC PUSH1 0xDD PUSH1 0xEE PUSH1 0xFF"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // JUMPI OP
    @Test
    public void testJUMPI_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x01 PUSH1 0x05 JUMPI JUMPDEST PUSH1 0xCC"), invoke);
        String s_expected = "00000000000000000000000000000000000000000000000000000000000000CC";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(s_expected, Hex.toHexString(program.getStack().peek().getData()).toUpperCase());
    }

    // JUMPI OP
    @Test
    public void testJUMPI_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH4 0x00000000 PUSH1 0x44 JUMPI PUSH1 0xCC PUSH1 0xDD"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000000000DD";
        String s_expected_2 = "00000000000000000000000000000000000000000000000000000000000000CC";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        DataWord item2 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
        Assert.assertEquals(s_expected_2, Hex.toHexString(item2.getData()).toUpperCase());
    }

    // JUMPI OP mal
    @Test(expected = StackTooSmallException.class)
    public void testJUMPI_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x01 JUMPI"), invoke);
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // JUMPI OP mal
    @Test(expected = BadJumpDestinationException.class)
    public void testJUMPI_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x01 PUSH1 0x22 SWAP1 SWAP1 JUMPI"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // JUMP OP mal data
    @Test(expected = BadJumpDestinationException.class)
    public void testJUMPDEST_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x23 PUSH1 0x08 JUMP PUSH1 0x01 JUMPDEST PUSH1 0x02 SSTORE"), invoke);
        String s_expected_key = "0000000000000000000000000000000000000000000000000000000000000002";
        String s_expected_val = "0000000000000000000000000000000000000000000000000000000000000023";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord key = DataWord.of(Hex.decode(s_expected_key));
        DataWord val = program.getStorage().getStorageValue(invoke.getOwnerAddress().getNoLeadZeroesData(), key);
        Assert.assertTrue(program.isStopped());
        Assert.assertEquals(s_expected_val, Hex.toHexString(val.getData()).toUpperCase());
    }

    // JUMPDEST OP for JUMPI
    @Test
    public void testJUMPDEST_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x23 PUSH1 0x01 PUSH1 0x09 JUMPI PUSH1 0x01 JUMPDEST PUSH1 0x02 SSTORE"), invoke);
        String s_expected_key = "0000000000000000000000000000000000000000000000000000000000000002";
        String s_expected_val = "0000000000000000000000000000000000000000000000000000000000000023";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord key = DataWord.of(Hex.decode(s_expected_key));
        DataWord val = program.getStorage().getStorageValue(invoke.getOwnerAddress().getNoLeadZeroesData(), key);
        Assert.assertTrue(program.isStopped());
        Assert.assertEquals(s_expected_val, Hex.toHexString(val.getData()).toUpperCase());
    }

    // MSIZE OP
    @Test
    public void testMSIZE_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("MSIZE"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // MSIZE OP
    @Test
    public void testMSIZE_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(compile("PUSH1 0x20 PUSH1 0x30 MSTORE MSIZE"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000060";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }
}

