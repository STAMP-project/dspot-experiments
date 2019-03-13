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
public class GitHubBlockTest {
    static String commitSHA = "253e99861fe406c7b1daf3d6a0c40906e8a8fd8f";

    static String treeSHA = "6c32ebcbce50112966427e18788069b9f7cbe285";// https://github.com/ethereum/tests/tree/develop/BlockchainTests/


    static GitHubJSONTestSuite.Network[] targetNets = new GitHubJSONTestSuite.Network[]{ Frontier, Homestead, EIP150, EIP158, Byzantium, Constantinople };

    static BlockchainTestSuite suite;

    @Test
    public void bcBlockGasLimitTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcBlockGasLimitTest");
    }

    @Test
    public void bcExploitTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcExploitTest", new HashSet<>(// it was checked once, but it's too heavy to hit it each time
        Arrays.asList("SuicideIssue")));
    }

    @Test
    public void bcForgedTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcForgedTest");
    }

    @Test
    public void bcForkStressTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcForkStressTest");
    }

    @Test
    public void bcGasPricerTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcGasPricerTest");
    }

    @Test
    public void bcInvalidHeaderTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcInvalidHeaderTest");
    }

    @Test
    public void bcMultiChainTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcMultiChainTest");
    }

    @Test
    public void bcRandomBlockhashTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcRandomBlockhashTest");
    }

    @Test
    public void bcStateTests() throws IOException {
        GitHubBlockTest.suite.runAll("bcStateTests");
    }

    @Test
    public void bcTotalDifficultyTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcTotalDifficultyTest");
    }

    @Test
    public void bcUncleHeaderValidity() throws IOException {
        GitHubBlockTest.suite.runAll("bcUncleHeaderValidity");
    }

    @Test
    public void bcUncleTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcUncleTest");
    }

    @Test
    public void bcValidBlockTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcValidBlockTest");
    }

    @Test
    public void bcWalletTest() throws IOException {
        GitHubBlockTest.suite.runAll("bcWalletTest");
    }
}

