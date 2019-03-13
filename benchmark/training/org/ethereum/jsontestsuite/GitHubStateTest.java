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
import org.ethereum.jsontestsuite.suite.GeneralStateTestSuite;
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
public class GitHubStateTest {
    static String commitSHA = "253e99861fe406c7b1daf3d6a0c40906e8a8fd8f";

    static String treeSHA = "51fd8f9969ff488917f0832c57ece01f66516db2";// https://github.com/ethereum/tests/tree/develop/GeneralStateTests/


    static GitHubJSONTestSuite.Network[] targetNets = new GitHubJSONTestSuite.Network[]{ Frontier, Homestead, EIP150, EIP158, Byzantium, Constantinople };

    static GeneralStateTestSuite suite;

    @Test
    public void stAttackTest() throws IOException {
        GitHubStateTest.suite.runAll("stAttackTest");
    }

    @Test
    public void stCallCodes() throws IOException {
        GitHubStateTest.suite.runAll("stCallCodes");
    }

    @Test
    public void stExample() throws IOException {
        GitHubStateTest.suite.runAll("stExample");
    }

    @Test
    public void stCallDelegateCodesCallCodeHomestead() throws IOException {
        GitHubStateTest.suite.runAll("stCallDelegateCodesCallCodeHomestead");
    }

    @Test
    public void stCallDelegateCodesHomestead() throws IOException {
        GitHubStateTest.suite.runAll("stCallDelegateCodesHomestead");
    }

    @Test
    public void stChangedEIP150() throws IOException {
        GitHubStateTest.suite.runAll("stChangedEIP150");
    }

    @Test
    public void stCallCreateCallCodeTest() throws IOException {
        Set<String> excluded = new HashSet<>();
        excluded.add("CallRecursiveBombPreCall");// Max Gas value is pending to be < 2^63

        GitHubStateTest.suite.runAll("stCallCreateCallCodeTest", excluded);
    }

    @Test
    public void stDelegatecallTestHomestead() throws IOException {
        GitHubStateTest.suite.runAll("stDelegatecallTestHomestead");
    }

    @Test
    public void stEIP150Specific() throws IOException {
        GitHubStateTest.suite.runAll("stEIP150Specific");
    }

    @Test
    public void stEIP150singleCodeGasPrices() throws IOException {
        GitHubStateTest.suite.runAll("stEIP150singleCodeGasPrices");
    }

    @Test
    public void stEIP158Specific() throws IOException {
        GitHubStateTest.suite.runAll("stEIP158Specific");
    }

    @Test
    public void stHomesteadSpecific() throws IOException {
        GitHubStateTest.suite.runAll("stHomesteadSpecific");
    }

    @Test
    public void stInitCodeTest() throws IOException {
        GitHubStateTest.suite.runAll("stInitCodeTest");
    }

    @Test
    public void stLogTests() throws IOException {
        GitHubStateTest.suite.runAll("stLogTests");
    }

    @Test
    public void stMemExpandingEIP150Calls() throws IOException {
        GitHubStateTest.suite.runAll("stMemExpandingEIP150Calls");
    }

    @Test
    public void stPreCompiledContracts() throws IOException {
        GitHubStateTest.suite.runAll("stPreCompiledContracts");
    }

    @Test
    public void stPreCompiledContracts2() throws IOException {
        GitHubStateTest.suite.runAll("stPreCompiledContracts2");
    }

    @Test
    public void stMemoryStressTest() throws IOException {
        Set<String> excluded = new HashSet<>();
        excluded.add("mload32bitBound_return2");// The test extends memory to 4Gb which can't be handled with Java arrays

        excluded.add("mload32bitBound_return");// The test extends memory to 4Gb which can't be handled with Java arrays

        excluded.add("mload32bitBound_Msize");// The test extends memory to 4Gb which can't be handled with Java arrays

        GitHubStateTest.suite.runAll("stMemoryStressTest", excluded);
    }

    @Test
    public void stMemoryTest() throws IOException {
        GitHubStateTest.suite.runAll("stMemoryTest");
    }

    @Test
    public void stQuadraticComplexityTest() throws IOException {
        // leaving only Homestead version since the test runs too long
        GitHubStateTest.suite.runAll("stQuadraticComplexityTest", Homestead);
    }

    @Test
    public void stSolidityTest() throws IOException {
        GitHubStateTest.suite.runAll("stSolidityTest");
    }

    @Test
    public void stRecursiveCreate() throws IOException {
        GitHubStateTest.suite.runAll("stRecursiveCreate");
    }

    @Test
    public void stRefundTest() throws IOException {
        GitHubStateTest.suite.runAll("stRefundTest");
    }

    @Test
    public void stReturnDataTest() throws IOException {
        GitHubStateTest.suite.runAll("stReturnDataTest");
    }

    @Test
    public void stRevertTest() throws IOException {
        GitHubStateTest.suite.runAll("stRevertTest");
    }

    @Test
    public void stSpecialTest() throws IOException {
        GitHubStateTest.suite.runAll("stSpecialTest");
    }

    @Test
    public void stStackTests() throws IOException {
        GitHubStateTest.suite.runAll("stStackTests");
    }

    @Test
    public void stStaticCall() throws IOException {
        GitHubStateTest.suite.runAll("stStaticCall");
    }

    @Test
    public void stSystemOperationsTest() throws IOException {
        GitHubStateTest.suite.runAll("stSystemOperationsTest");
    }

    @Test
    public void stTransactionTest() throws IOException {
        // TODO enable when zero sig Txes comes in
        GitHubStateTest.suite.runAll("stTransactionTest", new HashSet<>(Arrays.asList("zeroSigTransactionCreate", "zeroSigTransactionCreatePrice0", "zeroSigTransacrionCreatePrice0", "zeroSigTransaction", "zeroSigTransaction0Price", "zeroSigTransactionInvChainID", "zeroSigTransactionInvNonce", "zeroSigTransactionInvNonce2", "zeroSigTransactionOOG", "zeroSigTransactionOrigin", "zeroSigTransactionToZero", "zeroSigTransactionToZero2")));
    }

    @Test
    public void stTransitionTest() throws IOException {
        GitHubStateTest.suite.runAll("stTransitionTest");
    }

    @Test
    public void stWalletTest() throws IOException {
        GitHubStateTest.suite.runAll("stWalletTest");
    }

    @Test
    public void stZeroCallsRevert() throws IOException {
        GitHubStateTest.suite.runAll("stZeroCallsRevert");
    }

    @Test
    public void stCreateTest() throws IOException {
        GitHubStateTest.suite.runAll("stCreateTest");
    }

    @Test
    public void stZeroCallsTest() throws IOException {
        GitHubStateTest.suite.runAll("stZeroCallsTest");
    }

    @Test
    public void stZeroKnowledge() throws IOException {
        GitHubStateTest.suite.runAll("stZeroKnowledge");
    }

    @Test
    public void stZeroKnowledge2() throws IOException {
        GitHubStateTest.suite.runAll("stZeroKnowledge2");
    }

    @Test
    public void stCodeSizeLimit() throws IOException {
        GitHubStateTest.suite.runAll("stCodeSizeLimit");
    }

    @Test
    public void stRandom() throws IOException {
        GitHubStateTest.suite.runAll("stRandom");
    }

    @Test
    public void stRandom2() throws IOException {
        GitHubStateTest.suite.runAll("stRandom2");
    }

    @Test
    public void stBadOpcode() throws IOException {
        GitHubStateTest.suite.runAll("stBadOpcode");
    }

    @Test
    public void stNonZeroCallsTest() throws IOException {
        GitHubStateTest.suite.runAll("stNonZeroCallsTest");
    }

    @Test
    public void stCodeCopyTest() throws IOException {
        GitHubStateTest.suite.runAll("stCodeCopyTest");
    }

    @Test
    public void stExtCodeHashTest() throws IOException {
        GitHubStateTest.suite.runAll("stExtCodeHash");
    }

    @Test
    public void stShiftTest() throws IOException {
        GitHubStateTest.suite.runAll("stShift");
    }

    @Test
    public void stCreate2Test() throws IOException {
        GitHubStateTest.suite.runAll("stCreate2");
    }

    @Test
    public void stSstoreTest() throws IOException {
        GitHubStateTest.suite.runAll("stSStoreTest");
    }
}

