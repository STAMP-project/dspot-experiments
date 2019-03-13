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
package org.ethereum.net;


import java.math.BigInteger;
import java.util.Collections;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.net.eth.message.TransactionsMessage;
import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;


public class TransactionsMessageTest {
    /* Transactions msg encode */
    @Test
    public void test_3() throws Exception {
        String expected = "f872f870808b00d3c21bcecceda10000009479b08ad8787060333663d19704909ee7b1903e588609184e72a000824255801ca00f410a70e42b2c9854a8421d32c87c370a2b9fff0a27f9f031bb4443681d73b5a018a7dc4c4f9dee9f3dc35cb96ca15859aa27e219a8e4a8547be6bd3206979858";
        BigInteger value = new BigInteger("1000000000000000000000000");
        byte[] privKey = HashUtil.sha3("cat".getBytes());
        ECKey ecKey = ECKey.fromPrivate(privKey);
        byte[] gasPrice = Hex.decode("09184e72a000");
        byte[] gas = Hex.decode("4255");
        Transaction tx = new Transaction(null, value.toByteArray(), ecKey.getAddress(), gasPrice, gas, null);
        tx.sign(privKey);
        tx.getEncoded();
        TransactionsMessage transactionsMessage = new TransactionsMessage(Collections.singletonList(tx));
        Assert.assertEquals(expected, Hex.toHexString(transactionsMessage.getEncoded()));
    }

    @Test
    public void test_4() {
        String msg = "f872f87083011a6d850ba43b740083015f9094ec210ec3715d5918b37cfa4d344a45d177ed849f881b461c1416b9d000801ba023a3035235ca0a6f80f08a1d4bd760445d5b0f8a25c32678fe18a451a88d6377a0765dde224118bdb40a67f315583d542d93d17d8637302b1da26e1013518d3ae8";
        TransactionsMessage tmsg = new TransactionsMessage(Hex.decode(msg));
        Assert.assertEquals(1, tmsg.getTransactions().size());
    }
}

