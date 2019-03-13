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
import org.ethereum.jsontestsuite.suite.BlockchainTestSuite;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.ByzantiumToConstantinopleFixAt5;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.EIP158ToByzantiumAt5;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.FrontierToHomesteadAt5;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.HomesteadToDaoAt5;
import static org.ethereum.jsontestsuite.GitHubJSONTestSuite.Network.HomesteadToEIP150At5;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubTestNetTest {
    static String commitSHA = "725dbc73a54649e22a00330bd0f4d6699a5060e5";

    static String treeSHA = "36528084e10bf993fdb20cc304f3421c7324c2a4";// https://github.com/ethereum/tests/tree/develop/BlockchainTests/TransitionTests


    static BlockchainTestSuite suite;

    @Test
    public void bcFrontierToHomestead() throws IOException {
        GitHubTestNetTest.suite.runAll("bcFrontierToHomestead", FrontierToHomesteadAt5);
    }

    @Test
    public void bcHomesteadToDao() throws IOException {
        GitHubTestNetTest.suite.runAll("bcHomesteadToDao", HomesteadToDaoAt5);
    }

    @Test
    public void bcHomesteadToEIP150() throws IOException {
        GitHubTestNetTest.suite.runAll("bcHomesteadToEIP150", HomesteadToEIP150At5);
    }

    @Test
    public void bcEIP158ToByzantium() throws IOException {
        GitHubTestNetTest.suite.runAll("bcEIP158ToByzantium", EIP158ToByzantiumAt5);
    }

    @Test
    public void byzantiumToConstantinople() throws IOException {
        GitHubTestNetTest.suite.runAll("bcByzantiumToConstantinople", ByzantiumToConstantinopleFixAt5);
    }
}

