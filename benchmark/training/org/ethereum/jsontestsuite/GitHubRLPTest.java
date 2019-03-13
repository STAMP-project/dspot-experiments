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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ethereum.jsontestsuite.suite.RLPTestCase;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubRLPTest {
    private static final Logger logger = LoggerFactory.getLogger("TCK-Test");

    private static Map<String, RLPTestCase> TEST_SUITE = new HashMap<>();

    private static String commitSHA = "develop";

    @Test
    public void rlpEncodeTest() throws Exception {
        GitHubRLPTest.logger.info("    Testing RLP encoding...");
        for (String key : GitHubRLPTest.TEST_SUITE.keySet()) {
            GitHubRLPTest.logger.info(("    " + key));
            RLPTestCase testCase = GitHubRLPTest.TEST_SUITE.get(key);
            testCase.doEncode();
            Assert.assertEquals(testCase.getExpected(), testCase.getComputed());
        }
    }

    @Test
    public void rlpDecodeTest() throws Exception {
        GitHubRLPTest.logger.info("    Testing RLP decoding...");
        Set<String> excluded = new HashSet<>();
        for (String key : GitHubRLPTest.TEST_SUITE.keySet()) {
            if (excluded.contains(key)) {
                GitHubRLPTest.logger.info(("[X] " + key));
                continue;
            } else {
                GitHubRLPTest.logger.info(("    " + key));
            }
            RLPTestCase testCase = GitHubRLPTest.TEST_SUITE.get(key);
            testCase.doDecode();
            Assert.assertEquals(testCase.getExpected(), testCase.getComputed());
        }
    }
}

