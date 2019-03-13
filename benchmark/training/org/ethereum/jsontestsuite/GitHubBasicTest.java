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


import MainNetConfig.INSTANCE;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 02.09.2015
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubBasicTest {
    String commitSHA = "253e99861fe406c7b1daf3d6a0c40906e8a8fd8f";

    @Test
    public void btCrypto() throws IOException {
        GitHubJSONTestSuite.runCryptoTest("BasicTests/crypto.json", commitSHA);
    }

    @Test
    public void btDifficulty() throws IOException, ParseException {
        GitHubJSONTestSuite.runDifficultyTest(INSTANCE, "BasicTests/difficulty.json", commitSHA);
    }

    @Test
    public void btDifficultyByzantium() throws IOException, ParseException {
        GitHubJSONTestSuite.runDifficultyTest(new ByzantiumConfig(new DaoHFConfig()), "BasicTests/difficultyByzantium.json", commitSHA);
    }

    @Test
    public void btDifficultyConstantinople() throws IOException, ParseException {
        GitHubJSONTestSuite.runDifficultyTest(new ConstantinopleConfig(new DaoHFConfig()), "BasicTests/difficultyConstantinople.json", commitSHA);
    }

    @Test
    public void btDifficultyFrontier() throws IOException, ParseException {
        GitHubJSONTestSuite.runDifficultyTest(new FrontierConfig(), "BasicTests/difficultyFrontier.json", commitSHA);
    }

    @Test
    public void btDifficultyHomestead() throws IOException, ParseException {
        GitHubJSONTestSuite.runDifficultyTest(new HomesteadConfig(), "BasicTests/difficultyHomestead.json", commitSHA);
    }

    @Test
    public void btDifficultyMainNetwork() throws IOException, ParseException {
        GitHubJSONTestSuite.runDifficultyTest(INSTANCE, "BasicTests/difficultyMainNetwork.json", commitSHA);
    }
}

