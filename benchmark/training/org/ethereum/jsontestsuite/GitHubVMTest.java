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


import org.ethereum.jsontestsuite.suite.VMTestSuite;
import org.json.simple.parser.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubVMTest {
    static String commitSHA = "253e99861fe406c7b1daf3d6a0c40906e8a8fd8f";

    static String treeSHA = "d909a11b8315a81eaf610f457205025fb1284cc8";// https://github.com/ethereum/tests/tree/develop/VMTests/


    static VMTestSuite suite;

    @Test
    public void vmArithmeticTest() throws ParseException {
        GitHubVMTest.suite.runAll("vmArithmeticTest");
    }

    @Test
    public void vmBitwiseLogicOperation() throws ParseException {
        GitHubVMTest.suite.runAll("vmBitwiseLogicOperation");
    }

    @Test
    public void vmBlockInfoTest() throws ParseException {
        GitHubVMTest.suite.runAll("vmBlockInfoTest");
    }

    @Test
    public void vmEnvironmentalInfo() throws ParseException {
        GitHubVMTest.suite.runAll("vmEnvironmentalInfo");
    }

    @Test
    public void vmIOandFlowOperations() throws ParseException {
        GitHubVMTest.suite.runAll("vmIOandFlowOperations");
    }

    @Test
    public void vmLogTest() throws ParseException {
        GitHubVMTest.suite.runAll("vmLogTest");
    }

    @Test
    public void vmPushDupSwapTest() throws ParseException {
        GitHubVMTest.suite.runAll("vmPushDupSwapTest");
    }

    @Test
    public void vmRandomTest() throws ParseException {
        GitHubVMTest.suite.runAll("vmRandomTest");
    }

    @Test
    public void vmSha3Test() throws ParseException {
        GitHubVMTest.suite.runAll("vmSha3Test");
    }

    @Test
    public void vmSystemOperations() throws ParseException {
        GitHubVMTest.suite.runAll("vmSystemOperations");
    }

    @Test
    public void vmTests() throws ParseException {
        GitHubVMTest.suite.runAll("vmTests");
    }
}

