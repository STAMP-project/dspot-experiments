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
package org.ethereum.solidity;


import Type.function;
import java.io.IOException;
import junit.framework.TestCase;
import org.ethereum.solidity.Abi.Entry;
import org.junit.Assert;
import org.junit.Test;


public class AbiTest {
    @Test
    public void simpleTest() throws IOException {
        String contractAbi = "[{" + ((((("\"name\":\"simpleFunction\"," + "\"constant\":true,") + "\"payable\":true,") + "\"type\":\"function\",") + "\"inputs\": [{\"name\":\"_in\", \"type\":\"bytes32\"}],") + "\"outputs\":[{\"name\":\"_out\",\"type\":\"bytes32\"}]}]");
        Abi abi = Abi.fromJson(contractAbi);
        TestCase.assertEquals(abi.size(), 1);
        Entry onlyFunc = abi.get(0);
        TestCase.assertEquals(onlyFunc.type, function);
        TestCase.assertEquals(onlyFunc.inputs.size(), 1);
        TestCase.assertEquals(onlyFunc.outputs.size(), 1);
        Assert.assertTrue(onlyFunc.payable);
        Assert.assertTrue(onlyFunc.constant);
    }

    @Test
    public void simpleLegacyTest() throws IOException {
        String contractAbi = "[{" + (((("\"name\":\"simpleFunction\"," + "\"constant\":true,") + "\"type\":\"function\",") + "\"inputs\": [{\"name\":\"_in\", \"type\":\"bytes32\"}],") + "\"outputs\":[{\"name\":\"_out\",\"type\":\"bytes32\"}]}]");
        Abi abi = Abi.fromJson(contractAbi);
        TestCase.assertEquals(abi.size(), 1);
        Entry onlyFunc = abi.get(0);
        TestCase.assertEquals(onlyFunc.type, function);
        TestCase.assertEquals(onlyFunc.inputs.size(), 1);
        TestCase.assertEquals(onlyFunc.outputs.size(), 1);
        Assert.assertTrue(onlyFunc.constant);
    }
}

