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
package org.ethereum.jsontestsuite;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.ethereum.jsontestsuite.suite.BlockchainTestSuite;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.Byzantium;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.Constantinople;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.EIP150;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.EIP158;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.Frontier;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.Homestead;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubBlockStateTest {
    static String commitSHA = "253e99861fe406c7b1daf3d6a0c40906e8a8fd8f";

    static String treeSHA = "724427f69f5573ed0f504a534b3ecbcd3070fa28";// https://github.com/ethereum/tests/tree/develop/BlockchainTests/GeneralStateTests/


    static GitHubJSONTestSuite.Network[] targetNets = new GitHubJSONTestSuite.Network[]{ Frontier, Homestead, EIP150, EIP158, Byzantium, Constantinople };

    static BlockchainTestSuite suite;

    @Test
    public void bcStAttackTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stAttackTest");
    }

    @Test
    public void bcStCallCodes() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCallCodes");
    }

    @Test
    public void bcStExample() throws IOException {
        GitHubBlockStateTest.suite.runAll("stExample");
    }

    @Test
    public void bcStCallDelegateCodesCallCodeHomestead() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCallDelegateCodesCallCodeHomestead");
    }

    @Test
    public void bcStCallDelegateCodesHomestead() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCallDelegateCodesHomestead");
    }

    @Test
    public void bcStChangedEIP150() throws IOException {
        GitHubBlockStateTest.suite.runAll("stChangedEIP150");
    }

    @Test
    public void bcStCallCreateCallCodeTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCallCreateCallCodeTest");
    }

    @Test
    public void bcStDelegatecallTestHomestead() throws IOException {
        GitHubBlockStateTest.suite.runAll("stDelegatecallTestHomestead");
    }

    @Test
    public void bcStEIP150Specific() throws IOException {
        GitHubBlockStateTest.suite.runAll("stEIP150Specific");
    }

    @Test
    public void bcStEIP150singleCodeGasPrices() throws IOException {
        GitHubBlockStateTest.suite.runAll("stEIP150singleCodeGasPrices");
    }

    @Test
    public void bcStEIP158Specific() throws IOException {
        GitHubBlockStateTest.suite.runAll("stEIP158Specific");
    }

    @Test
    public void bcStHomesteadSpecific() throws IOException {
        GitHubBlockStateTest.suite.runAll("stHomesteadSpecific");
    }

    @Test
    public void bcStInitCodeTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stInitCodeTest");
    }

    @Test
    public void bcStLogTests() throws IOException {
        GitHubBlockStateTest.suite.runAll("stLogTests");
    }

    @Test
    public void bcStMemExpandingEIP150Calls() throws IOException {
        GitHubBlockStateTest.suite.runAll("stMemExpandingEIP150Calls");
    }

    @Test
    public void bcStPreCompiledContracts() throws IOException {
        GitHubBlockStateTest.suite.runAll("stPreCompiledContracts");
    }

    @Test
    public void bcStPreCompiledContracts2() throws IOException {
        GitHubBlockStateTest.suite.runAll("stPreCompiledContracts2");
    }

    @Test
    public void bcStMemoryStressTest() throws IOException {
        Set<String> excluded = new HashSet<>();
        excluded.add("mload32bitBound_return2");// The test extends memory to 4Gb which can't be handled with Java arrays

        excluded.add("mload32bitBound_return");// The test extends memory to 4Gb which can't be handled with Java arrays

        excluded.add("mload32bitBound_Msize");// The test extends memory to 4Gb which can't be handled with Java arrays

        GitHubBlockStateTest.suite.runAll("stMemoryStressTest", excluded);
    }

    @Test
    public void bcStMemoryTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stMemoryTest");
    }

    @Test
    public void bcStQuadraticComplexityTest() throws IOException {
        // leaving only Homestead version since the test runs too long
        GitHubBlockStateTest.suite.runAll("stQuadraticComplexityTest", Homestead);
    }

    @Test
    public void bcStSolidityTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stSolidityTest");
    }

    @Test
    public void bcStRecursiveCreate() throws IOException {
        GitHubBlockStateTest.suite.runAll("stRecursiveCreate");
    }

    @Test
    public void bcStRefundTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stRefundTest");
    }

    @Test
    public void bcStReturnDataTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stReturnDataTest");
    }

    @Test
    public void bcStRevertTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stRevertTest");
    }

    @Test
    public void bcStSpecialTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stSpecialTest");
    }

    @Test
    public void bcStStackTests() throws IOException {
        GitHubBlockStateTest.suite.runAll("stStackTests");
    }

    @Test
    public void bcStStaticCall() throws IOException {
        GitHubBlockStateTest.suite.runAll("stStaticCall");
    }

    @Test
    public void bcStSystemOperationsTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stSystemOperationsTest");
    }

    @Test
    public void bcStTransactionTest() throws IOException {
        // TODO enable when zero sig Txes comes in
        GitHubBlockStateTest.suite.runAll("stTransactionTest", new HashSet<>(Arrays.asList("zeroSigTransacrionCreate", "zeroSigTransacrionCreatePrice0", "zeroSigTransaction", "zeroSigTransaction0Price", "zeroSigTransactionInvChainID", "zeroSigTransactionInvNonce", "zeroSigTransactionInvNonce2", "zeroSigTransactionOOG", "zeroSigTransactionOrigin", "zeroSigTransactionToZero", "zeroSigTransactionToZero2")));
    }

    @Test
    public void bcStTransitionTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stTransitionTest");
    }

    @Test
    public void bcStWalletTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stWalletTest");
    }

    @Test
    public void bcStZeroCallsRevert() throws IOException {
        GitHubBlockStateTest.suite.runAll("stZeroCallsRevert");
    }

    @Test
    public void bcStCreateTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCreateTest");
    }

    @Test
    public void bcStZeroCallsTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stZeroCallsTest");
    }

    @Test
    public void bcStZeroKnowledge() throws IOException {
        GitHubBlockStateTest.suite.runAll("stZeroKnowledge");
    }

    @Test
    public void bcStZeroKnowledge2() throws IOException {
        GitHubBlockStateTest.suite.runAll("stZeroKnowledge2");
    }

    @Test
    public void bcStCodeSizeLimit() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCodeSizeLimit");
    }

    @Test
    public void bcStRandom() throws IOException {
        GitHubBlockStateTest.suite.runAll("stRandom");
    }

    @Test
    public void bcStRandom2() throws IOException {
        GitHubBlockStateTest.suite.runAll("stRandom2");
    }

    @Test
    public void stBadOpcode() throws IOException {
        GitHubBlockStateTest.suite.runAll("stBadOpcode");
    }

    @Test
    public void stNonZeroCallsTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stNonZeroCallsTest");
    }

    @Test
    public void stCodeCopyTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCodeCopyTest");
    }

    @Test
    public void stExtCodeHash() throws IOException {
        GitHubBlockStateTest.suite.runAll("stExtCodeHash");
    }

    @Test
    public void stShiftTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stShift");
    }

    @Test
    public void stCreate2Test() throws IOException {
        GitHubBlockStateTest.suite.runAll("stCreate2");
    }

    @Test
    public void stSstoreTest() throws IOException {
        GitHubBlockStateTest.suite.runAll("stSStoreTest");
    }
}

