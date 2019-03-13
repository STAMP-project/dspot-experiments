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


import org.ethereum.core.Repository;
import org.ethereum.vm.program.Program;
import org.ethereum.vm.program.invoke.ProgramInvokeMockImpl;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;


/**
 *
 *
 * @author Roman Mandeleil
 * @since 16.06.2014
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VMComplexTest {
    private static Logger logger = LoggerFactory.getLogger("TCK-Test");

    // CREATE magic
    @Test
    public void test4() {
        /**
         * #The code will run
         *       ------------------
         *
         * contract A: 77045e71a7a2c50903d88e564cd72fab11e82051
         * -----------
         *
         * a = 0x7f60c860005461012c6020540000000000000000000000000000000000000000
         * b = 0x0060005460206000f20000000000000000000000000000000000000000000000
         * create(100, 0 41)
         *
         *
         * contract B: (the contract to be created the addr will be defined to: 8e45367623a2865132d9bf875d5cfa31b9a0cd94)
         * -----------
         * a = 200
         * b = 300
         */
        // Set contract into Database
        byte[] caller_addr_bytes = Hex.decode("cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
        byte[] contractA_addr_bytes = Hex.decode("77045e71a7a2c50903d88e564cd72fab11e82051");
        byte[] codeA = Hex.decode(("7f7f60c860005461012c602054000000000000" + (("00000000000000000000000000006000547e60" + "005460206000f2000000000000000000000000") + "0000000000000000000000602054602960006064f0")));
        ProgramInvokeMockImpl pi = new ProgramInvokeMockImpl();
        pi.setOwnerAddress(contractA_addr_bytes);
        Repository repository = pi.getRepository();
        repository.createAccount(contractA_addr_bytes);
        repository.saveCode(contractA_addr_bytes, codeA);
        repository.createAccount(caller_addr_bytes);
        // ****************** //
        // Play the program  //
        // ****************** //
        VM vm = new VM();
        Program program = new Program(codeA, pi);
        try {
            while (!(program.isStopped()))
                vm.step(program);

        } catch (RuntimeException e) {
            program.setRuntimeFailure(e);
        }
        VMComplexTest.logger.info("============ Results ============");
        System.out.println(("*** Used gas: " + (program.getResult().getGasUsed())));
        // TODO: check that the value pushed after exec is the new address
        repository.close();
    }
}

