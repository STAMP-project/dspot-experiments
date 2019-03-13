/**
 * Copyright (c) [2017] [ <ether.camp> ]
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
package org.ethereum.config.blockchain;


import Eip150HFConfig.GasCostEip150HF;
import java.math.BigInteger;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Transaction;
import org.ethereum.vm.DataWord;
import org.junit.Assert;
import org.junit.Test;


/**
 * <p>This unit test covers EIP-150:</p>
 *
 * <pre>
 * EIP: 150
 * Title: Gas cost changes for IO-heavy operations
 * Author: Vitalik Buterin
 * Type: Standard Track
 * Category: Core
 * Status: Final
 * Created: 2016-09-24
 * </pre>
 *
 * <pre>If block.number >= FORK_BLKNUM, then:</pre>
 * <ul>
 *     <li>Increase the gas cost of EXTCODESIZE to 700</li>
 *     <li>Increase the base gas cost of EXTCODECOPY to 700</li>
 *     <li>Increase the gas cost of BALANCE to 400</li>
 *     <li>Increase the gas cost of SLOAD to 200</li>
 *     <li>Increase the gas cost of CALL, DELEGATECALL, CALLCODE to 700</li>
 *     <li>Increase the gas cost of SELFDESTRUCT to 5000</li>
 *     <li>If SELFDESTRUCT hits a newly created account, it triggers an additional gas cost of 25000 (similar to CALLs)</li>
 *     <li>Increase the recommended gas limit target to 5.5 million</li>
 *     <li>Define "all but one 64th" of N as N - floor(N / 64)</li>
 *     <li>If a call asks for more gas than the maximum allowed amount (ie. total amount of gas remaining
 *     in the parent after subtracting the gas cost of the call and memory expansion), do not return an
 *     OOG error; instead, if a call asks for more gas than all but one 64th of the maximum allowed amount,
 *     call with all but one 64th of the maximum allowed amount of gas (this is equivalent to a version of
 *     #90 plus #114). CREATE only provides all but one 64th of the parent gas to the child call.</li>
 * </ul>
 * <p>Source -- https://github.com/ethereum/EIPs/blob/master/EIPS/eip-150.md</p>
 */
public class Eip150HFConfigTest {
    @Test
    public void testNewGasCost() {
        Eip150HFConfig.GasCostEip150HF gasCostEip150HF = new Eip150HFConfig.GasCostEip150HF();
        Assert.assertEquals(700, gasCostEip150HF.getEXT_CODE_SIZE());
        Assert.assertEquals(700, gasCostEip150HF.getEXT_CODE_COPY());
        Assert.assertEquals(400, gasCostEip150HF.getBALANCE());
        Assert.assertEquals(200, gasCostEip150HF.getSLOAD());
        Assert.assertEquals(700, gasCostEip150HF.getCALL());
        Assert.assertEquals(5000, gasCostEip150HF.getSUICIDE());
        Assert.assertEquals(25000, gasCostEip150HF.getNEW_ACCT_SUICIDE());
        // TODO: Verify recommended gas limit target of 5.5 million. This may no longer apply though.
    }

    @Test
    public void testGetCreateGas() {
        Eip150HFConfig eip150 = new Eip150HFConfig(null);
        DataWord createGas = eip150.getCreateGas(DataWord.of(64000));
        Assert.assertEquals(BigInteger.valueOf(63000), createGas.value());
    }

    @Test
    public void testAllButOne64thToUseFloor() {
        Eip150HFConfig eip150 = new Eip150HFConfig(new DaoHFConfig());
        assertCallGas(eip150, 63, 64, 63);
        assertCallGas(eip150, 100, 64, 63);
        assertCallGas(eip150, 10, 64, 10);
        assertCallGas(eip150, 0, 64, 0);
        assertCallGas(eip150, (-10), 64, 63);
        assertCallGas(eip150, 11, 10, 10);
    }

    @Test
    public void testRelatedEip() {
        TestBlockchainConfig parentAllDependentEipTrue = new TestBlockchainConfig().enableEip161().enableEip198().enableEip212().enableEip213();
        Eip150HFConfig eip150 = new Eip150HFConfig(parentAllDependentEipTrue);
        // Inherited from parent
        Assert.assertTrue(eip150.eip161());
        Assert.assertTrue(eip150.eip198());
        Assert.assertTrue(eip150.eip212());
        Assert.assertTrue(eip150.eip213());
        // Always false
        Assert.assertFalse(eip150.eip206());
        Assert.assertFalse(eip150.eip211());
        Assert.assertFalse(eip150.eip214());
        Assert.assertFalse(eip150.eip658());
        /* By flipping parent's eip values, we assert that
        Eip150 delegates respective eip calls to parent.
         */
        parentAllDependentEipTrue = new TestBlockchainConfig();
        eip150 = new Eip150HFConfig(parentAllDependentEipTrue);
        // Inherited from parent
        Assert.assertFalse(eip150.eip161());
        Assert.assertFalse(eip150.eip198());
        Assert.assertFalse(eip150.eip212());
        Assert.assertFalse(eip150.eip213());
        // Always false
        Assert.assertFalse(eip150.eip206());
        Assert.assertFalse(eip150.eip211());
        Assert.assertFalse(eip150.eip214());
        Assert.assertFalse(eip150.eip658());
    }

    @Test
    public void testParentInheritedProperties() {
        byte[] emptyBytes = new byte[]{  };
        SystemProperties props = new SystemProperties();
        TestBlockchainConfig parent = new TestBlockchainConfig();
        Eip150HFConfig eip150 = new Eip150HFConfig(parent);
        Assert.assertEquals(parent.constants, eip150.getConstants());
        Assert.assertEquals(parent.minerIfc, eip150.getMineAlgorithm(props));
        Assert.assertEquals(parent.difficulty, eip150.calcDifficulty(null, null));
        Assert.assertEquals(parent.difficultyMultiplier, eip150.getCalcDifficultyMultiplier(null, null));
        Assert.assertEquals(parent.transactionCost, eip150.getTransactionCost(null));
        Assert.assertEquals(parent.validateTransactionChanges, eip150.validateTransactionChanges(null, null, null, null));
        Assert.assertEquals(parent.extraData, eip150.getExtraData(emptyBytes, 0L));
        Assert.assertEquals(parent.headerValidators, eip150.headerValidators());
        Assert.assertEquals(parent.constants, eip150.getCommonConstants());
        Assert.assertSame(eip150, eip150.getConfigForBlock(0));
        Assert.assertSame(eip150, eip150.getConfigForBlock(1));
        Assert.assertSame(eip150, eip150.getConfigForBlock(5000000));
        Assert.assertTrue(((eip150.getGasCost()) instanceof Eip150HFConfig.GasCostEip150HF));
        Transaction txWithoutChainId = new Transaction(emptyBytes, emptyBytes, emptyBytes, emptyBytes, emptyBytes, emptyBytes, null);
        Assert.assertTrue(eip150.acceptTransactionSignature(txWithoutChainId));
        Transaction txWithChainId = new Transaction(emptyBytes, emptyBytes, emptyBytes, emptyBytes, emptyBytes, emptyBytes, 1);
        Assert.assertFalse(eip150.acceptTransactionSignature(txWithChainId));
    }
}

