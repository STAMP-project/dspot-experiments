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
import org.ethereum.jsontestsuite.suite.TxTestSuite;
import org.json.simple.parser.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitHubTransactionTest {
    static String commitSHA = "253e99861fe406c7b1daf3d6a0c40906e8a8fd8f";

    static String treeSHA = "8c0d8131cb772b572862f0d88e778830bfddb006";// https://github.com/ethereum/tests/tree/develop/TransactionTests/


    static TxTestSuite suite;

    @Test
    public void ttAddress() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttAddress");
    }

    @Test
    public void ttData() throws IOException, ParseException {
        GitHubTransactionTest.suite.run("ttData", new HashSet<>(// too big to run it each time
        Arrays.asList("String10MbData")));
    }

    @Test
    public void ttGasLimit() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttGasLimit");
    }

    @Test
    public void ttGasPrice() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttGasPrice");
    }

    @Test
    public void ttNonce() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttNonce");
    }

    @Test
    public void ttRSValue() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttRSValue");
    }

    @Test
    public void ttVValue() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttVValue");
    }

    @Test
    public void ttSignature() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttSignature");
    }

    @Test
    public void ttValue() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttValue");
    }

    @Test
    public void ttWrongRLP() throws IOException, ParseException {
        GitHubTransactionTest.suite.runAll("ttWrongRLP");
    }
}

