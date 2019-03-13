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
package org.ethereum.core;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ethereum.vm.DataWord;
import org.ethereum.vm.LogInfo;
import org.ethereum.vm.program.InternalTransaction;
import org.junit.Assert;
import org.junit.Test;


public class TransactionExecutionSummaryTest {
    @Test
    public void testRlpEncoding() {
        Transaction tx = TransactionExecutionSummaryTest.randomTransaction();
        Set<DataWord> deleteAccounts = new java.util.HashSet(TransactionExecutionSummaryTest.randomDataWords(10));
        List<LogInfo> logs = TransactionExecutionSummaryTest.randomLogsInfo(5);
        final Map<DataWord, DataWord> readOnly = TransactionExecutionSummaryTest.randomStorageEntries(20);
        final Map<DataWord, DataWord> changed = TransactionExecutionSummaryTest.randomStorageEntries(5);
        Map<DataWord, DataWord> all = new HashMap<DataWord, DataWord>() {
            {
                putAll(readOnly);
                putAll(changed);
            }
        };
        BigInteger gasLeftover = new BigInteger("123");
        BigInteger gasRefund = new BigInteger("125");
        BigInteger gasUsed = new BigInteger("556");
        final int nestedLevelCount = 5000;
        final int countByLevel = 1;
        List<InternalTransaction> internalTransactions = TransactionExecutionSummaryTest.randomInternalTransactions(tx, nestedLevelCount, countByLevel);
        byte[] result = TransactionExecutionSummaryTest.randomBytes(32);
        byte[] encoded = new TransactionExecutionSummary.Builder(tx).deletedAccounts(deleteAccounts).logs(logs).touchedStorage(all, changed).gasLeftover(gasLeftover).gasRefund(gasRefund).gasUsed(gasUsed).internalTransactions(internalTransactions).result(result).build().getEncoded();
        TransactionExecutionSummary summary = new TransactionExecutionSummary(encoded);
        Assert.assertArrayEquals(tx.getHash(), summary.getTransactionHash());
        Assert.assertEquals(size(deleteAccounts), size(summary.getDeletedAccounts()));
        for (DataWord account : summary.getDeletedAccounts()) {
            Assert.assertTrue(deleteAccounts.contains(account));
        }
        Assert.assertEquals(size(logs), size(summary.getLogs()));
        for (int i = 0; i < (logs.size()); i++) {
            TransactionExecutionSummaryTest.assertLogInfoEquals(logs.get(i), summary.getLogs().get(i));
        }
        TransactionExecutionSummaryTest.assertStorageEquals(all, summary.getTouchedStorage().getAll());
        TransactionExecutionSummaryTest.assertStorageEquals(changed, summary.getTouchedStorage().getChanged());
        TransactionExecutionSummaryTest.assertStorageEquals(readOnly, summary.getTouchedStorage().getReadOnly());
        Assert.assertEquals(gasRefund, summary.getGasRefund());
        Assert.assertEquals(gasLeftover, summary.getGasLeftover());
        Assert.assertEquals(gasUsed, summary.getGasUsed());
        Assert.assertEquals((nestedLevelCount * countByLevel), size(internalTransactions));
        Assert.assertArrayEquals(result, summary.getResult());
    }
}

