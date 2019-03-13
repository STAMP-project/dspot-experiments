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


import Program.IllegalOperationException;
import org.ethereum.vm.program.Program;
import org.ethereum.vm.program.Program.OutOfGasException;
import org.ethereum.vm.program.Program.StackTooSmallException;
import org.ethereum.vm.program.invoke.ProgramInvokeMockImpl;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.spongycastle.util.encoders.Hex;


/**
 *
 *
 * @author Roman Mandeleil
 * @since 01.06.2014
 */
/* TEST CASE LIST END */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VMCustomTest {
    private ProgramInvokeMockImpl invoke;

    private Program program;

    // CALLDATASIZE OP
    @Test
    public void testCALLDATASIZE_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("36"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000040";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLDATALOAD OP
    @Test
    public void testCALLDATALOAD_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("600035"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000000000A1";
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLDATALOAD OP
    @Test
    public void testCALLDATALOAD_2() {
        VM vm = new VM();
        program = new Program(Hex.decode("600235"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000A10000";
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLDATALOAD OP
    @Test
    public void testCALLDATALOAD_3() {
        VM vm = new VM();
        program = new Program(Hex.decode("602035"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000000000B1";
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLDATALOAD OP
    @Test
    public void testCALLDATALOAD_4() {
        VM vm = new VM();
        program = new Program(Hex.decode("602335"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000B1000000";
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLDATALOAD OP
    @Test
    public void testCALLDATALOAD_5() {
        VM vm = new VM();
        program = new Program(Hex.decode("603F35"), invoke);
        String s_expected_1 = "B100000000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLDATALOAD OP mal
    @Test(expected = RuntimeException.class)
    public void testCALLDATALOAD_6() {
        VM vm = new VM();
        program = new Program(Hex.decode("35"), invoke);
        try {
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // CALLDATACOPY OP
    @Test
    public void testCALLDATACOPY_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("60206000600037"), invoke);
        String m_expected = "00000000000000000000000000000000000000000000000000000000000000A1";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // CALLDATACOPY OP
    @Test
    public void testCALLDATACOPY_2() {
        VM vm = new VM();
        program = new Program(Hex.decode("60406000600037"), invoke);
        String m_expected = "00000000000000000000000000000000000000000000000000000000000000A1" + "00000000000000000000000000000000000000000000000000000000000000B1";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // CALLDATACOPY OP
    @Test
    public void testCALLDATACOPY_3() {
        VM vm = new VM();
        program = new Program(Hex.decode("60406004600037"), invoke);
        String m_expected = "000000000000000000000000000000000000000000000000000000A100000000" + "000000000000000000000000000000000000000000000000000000B100000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // CALLDATACOPY OP
    @Test
    public void testCALLDATACOPY_4() {
        VM vm = new VM();
        program = new Program(Hex.decode("60406000600437"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000" + ("000000A100000000000000000000000000000000000000000000000000000000" + "000000B100000000000000000000000000000000000000000000000000000000");
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // CALLDATACOPY OP
    @Test
    public void testCALLDATACOPY_5() {
        VM vm = new VM();
        program = new Program(Hex.decode("60406000600437"), invoke);
        String m_expected = "0000000000000000000000000000000000000000000000000000000000000000" + ("000000A100000000000000000000000000000000000000000000000000000000" + "000000B100000000000000000000000000000000000000000000000000000000");
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // CALLDATACOPY OP mal
    @Test(expected = StackTooSmallException.class)
    public void testCALLDATACOPY_6() {
        VM vm = new VM();
        program = new Program(Hex.decode("6040600037"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // CALLDATACOPY OP mal
    @Test(expected = OutOfGasException.class)
    public void testCALLDATACOPY_7() {
        VM vm = new VM();
        program = new Program(Hex.decode("6020600073CC0929EB16730E7C14FEFC63006AC2D794C5795637"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // ADDRESS OP
    @Test
    public void testADDRESS_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("30"), invoke);
        String s_expected_1 = "00000000000000000000000077045E71A7A2C50903D88E564CD72FAB11E82051";
        vm.step(program);
        DataWord item1 = program.stackPop();
        program.getStorage().close();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // BALANCE OP
    @Test
    public void testBALANCE_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("3031"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000000003E8";
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // ORIGIN OP
    @Test
    public void testORIGIN_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("32"), invoke);
        String s_expected_1 = "00000000000000000000000013978AEE95F38490E9769C39B2773ED763D9CD5F";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLER OP
    @Test
    public void testCALLER_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("33"), invoke);
        String s_expected_1 = "000000000000000000000000885F93EED577F2FC341EBB9A5C9B2CE4465D96C4";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // CALLVALUE OP
    @Test
    public void testCALLVALUE_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("34"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000DE0B6B3A7640000";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SHA3 OP
    @Test
    public void testSHA3_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("60016000536001600020"), invoke);
        String s_expected_1 = "5FE7F977E71DBA2EA1A68E21057BEEBB9BE2AC30C6410AA38D4F3FBE41DCFFD2";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SHA3 OP
    @Test
    public void testSHA3_2() {
        VM vm = new VM();
        program = new Program(Hex.decode("6102016000526002601E20"), invoke);
        String s_expected_1 = "114A3FE82A0219FCC31ABD15617966A125F12B0FD3409105FC83B487A9D82DE4";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // SHA3 OP mal
    @Test(expected = StackTooSmallException.class)
    public void testSHA3_3() {
        VM vm = new VM();
        program = new Program(Hex.decode("610201600052600220"), invoke);
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

    // BLOCKHASH OP
    @Test
    public void testBLOCKHASH_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("600140"), invoke);
        String s_expected_1 = "C89EFDAA54C0F20C7ADF612882DF0950F5A951637E0307CDCB4C672F298B8BC6";
        vm.step(program);
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // COINBASE OP
    @Test
    public void testCOINBASE_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("41"), invoke);
        String s_expected_1 = "000000000000000000000000E559DE5527492BCB42EC68D07DF0742A98EC3F1E";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // TIMESTAMP OP
    @Test
    public void testTIMESTAMP_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("42"), invoke);
        String s_expected_1 = "000000000000000000000000000000000000000000000000000000005387FE24";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // NUMBER OP
    @Test
    public void testNUMBER_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("43"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000021";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // DIFFICULTY OP
    @Test
    public void testDIFFICULTY_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("44"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000003ED290";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // GASPRICE OP
    @Test
    public void testGASPRICE_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("3A"), invoke);
        String s_expected_1 = "000000000000000000000000000000000000000000000000000009184E72A000";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // GASLIMIT OP
    @Test
    public void testGASLIMIT_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("45"), invoke);
        String s_expected_1 = "00000000000000000000000000000000000000000000000000000000000F4240";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }

    // INVALID OP
    @Test(expected = IllegalOperationException.class)
    public void testINVALID_1() {
        VM vm = new VM();
        program = new Program(Hex.decode("60012F6002"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000001";
        try {
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
            DataWord item1 = program.stackPop();
            Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
        }
    }
}

/**
 * TODO: add gas expeted and calculated to all test cases
 */
/**
 * TODO: considering: G_TXDATA + G_TRANSACTION
 */
/**
 * TODO:
 *
 *   22) CREATE:
 *   23) CALL:
 */
/**
 * contract creation (gas usage)
 * -----------------------------
 * G_TRANSACTION =                                (500)
 * 60016000546006601160003960066000f261778e600054 (115)
 * PUSH1    6001 (1)
 * PUSH1    6000 (1)
 * MSTORE   54   (1 + 1)
 * PUSH1    6006 (1)
 * PUSH1    6011 (1)
 * PUSH1    6000 (1)
 * CODECOPY 39   (1)
 * PUSH1    6006 (1)
 * PUSH1    6000 (1)
 * RETURN   f2   (1)
 * 61778e600054
 */
