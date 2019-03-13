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
 * Simple tests for VM Environmental Information
 */
public class VMEnvOpTest extends VMBaseOpTest {
    // CODECOPY OP
    @Test
    public void testCODECOPY_5() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("611234600054615566602054607060006020396000605f556014600054601e60205463abcddcba6040545b51602001600a5254516040016014525451606001601e5254516080016028525460a052546016604860003960166000f26000603f556103e756600054600053602002351234"), invoke);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertFalse(program.isStopped());
    }

    // CODECOPY OP mal
    @Test(expected = StackTooSmallException.class)
    public void testCODECOPY_6() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("605E6007396000605f556014600054601e60205463abcddcba6040545b51602001600a5254516040016014525451606001601e5254516080016028525460a052546016604860003960166000f26000603f556103e756600054600053602002351234"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // EXTCODECOPY OP
    @Test
    public void testEXTCODECOPY_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("60036007600073471FD3AD3E9EEADEEC4608B92D16CE6B500704CC3C123456"), invoke);
        String m_expected_1 = "6000600000000000000000000000000000000000000000000000000000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected_1, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // EXTCODECOPY OP
    @Test
    public void testEXTCODECOPY_2() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("603E6007600073471FD3AD3E9EEADEEC4608B92D16CE6B500704CC3C6000605f556014600054601e60205463abcddcba6040545b51602001600a5254516040016014525451606001601e5254516080016028525460a052546016604860003960166000f26000603f556103e75660005460005360200235602054"), invoke);
        String m_expected_1 = "6000605F556014600054601E60205463ABCDDCBA6040545B51602001600A5254516040016014525451606001601E5254516080016028525460A0525460160000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected_1, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // EXTCODECOPY OP
    @Test
    public void testEXTCODECOPY_3() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("605E6007600073471FD3AD3E9EEADEEC4608B92D16CE6B500704CC3C6000605f556014600054601e60205463abcddcba6040545b51602001600a5254516040016014525451606001601e5254516080016028525460a052546016604860003960166000f26000603f556103e75660005460005360200235"), invoke);
        String m_expected_1 = "6000605F556014600054601E60205463ABCDDCBA6040545B51602001600A5254516040016014525451606001601E5254516080016028525460A052546016604860003960166000F26000603F556103E756600054600053602002350000000000";
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertEquals(m_expected_1, Hex.toHexString(program.getMemory()).toUpperCase());
    }

    // EXTCODECOPY OP
    @Test
    public void testEXTCODECOPY_4() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("611234600054615566602054603E6000602073471FD3AD3E9EEADEEC4608B92D16CE6B500704CC3C6000605f556014600054601e60205463abcddcba6040545b51602001600a5254516040016014525451606001601e5254516080016028525460a052546016604860003960166000f26000603f556103e756600054600053602002351234"), invoke);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        vm.step(program);
        Assert.assertFalse(program.isStopped());
    }

    // EXTCODECOPY OP mal
    @Test(expected = StackTooSmallException.class)
    public void testEXTCODECOPY_5() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("605E600773471FD3AD3E9EEADEEC4608B92D16CE6B500704CC3C"), invoke);
        try {
            vm.step(program);
            vm.step(program);
            vm.step(program);
            vm.step(program);
        } finally {
            Assert.assertTrue(program.isStopped());
        }
    }

    // CODESIZE OP
    @Test
    public void testCODESIZE_1() {
        VM vm = new VM();
        program = new org.ethereum.vm.program.Program(Hex.decode("385E60076000396000605f556014600054601e60205463abcddcba6040545b51602001600a5254516040016014525451606001601e5254516080016028525460a052546016604860003960166000f26000603f556103e75660005460005360200235"), invoke);
        String s_expected_1 = "0000000000000000000000000000000000000000000000000000000000000062";
        vm.step(program);
        DataWord item1 = program.stackPop();
        Assert.assertEquals(s_expected_1, Hex.toHexString(item1.getData()).toUpperCase());
    }
}

