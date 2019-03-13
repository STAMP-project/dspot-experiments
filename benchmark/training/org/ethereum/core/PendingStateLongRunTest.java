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
import java.util.List;
import org.ethereum.util.BIUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 24.09.2015
 */
@Ignore
public class PendingStateLongRunTest {
    private Blockchain blockchain;

    private PendingState pendingState;

    private List<String> strData;

    // test with real data from the frontier net
    @Test
    public void test_1() {
        Block b46169 = new Block(Hex.decode(strData.get(46169)));
        Block b46170 = new Block(Hex.decode(strData.get(46170)));
        Transaction tx46169 = b46169.getTransactionsList().get(0);
        Transaction tx46170 = b46170.getTransactionsList().get(0);
        Repository pending = pendingState.getRepository();
        BigInteger balanceBefore46169 = pending.getAccountState(tx46169.getReceiveAddress()).getBalance();
        BigInteger balanceBefore46170 = pending.getAccountState(tx46170.getReceiveAddress()).getBalance();
        pendingState.addPendingTransaction(tx46169);
        pendingState.addPendingTransaction(tx46170);
        for (int i = 46000; i < 46169; i++) {
            Block b = new Block(Hex.decode(strData.get(i)));
            blockchain.tryToConnect(b);
        }
        pending = pendingState.getRepository();
        BigInteger balanceAfter46169 = balanceBefore46169.add(BIUtil.toBI(tx46169.getValue()));
        Assert.assertEquals(pendingState.getPendingTransactions().size(), 2);
        Assert.assertEquals(balanceAfter46169, pending.getAccountState(tx46169.getReceiveAddress()).getBalance());
        blockchain.tryToConnect(b46169);
        pending = pendingState.getRepository();
        Assert.assertEquals(balanceAfter46169, pending.getAccountState(tx46169.getReceiveAddress()).getBalance());
        Assert.assertEquals(pendingState.getPendingTransactions().size(), 1);
        BigInteger balanceAfter46170 = balanceBefore46170.add(BIUtil.toBI(tx46170.getValue()));
        Assert.assertEquals(balanceAfter46170, pending.getAccountState(tx46170.getReceiveAddress()).getBalance());
        blockchain.tryToConnect(b46170);
        pending = pendingState.getRepository();
        Assert.assertEquals(balanceAfter46170, pending.getAccountState(tx46170.getReceiveAddress()).getBalance());
        Assert.assertEquals(pendingState.getPendingTransactions().size(), 0);
    }
}

