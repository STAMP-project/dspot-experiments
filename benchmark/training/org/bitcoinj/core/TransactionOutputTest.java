/**
 * Copyright by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.core;


import AbstractBlockChain.NewBlockType.BEST_CHAIN;
import Coin.CENT;
import Coin.COIN;
import Transaction.MIN_NONDUST_OUTPUT;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bitcoinj.testing.TestWithWallet;
import org.bitcoinj.wallet.SendRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static Coin.COIN;


public class TransactionOutputTest extends TestWithWallet {
    @Test
    public void testMultiSigOutputToString() throws Exception {
        sendMoneyToWallet(BEST_CHAIN, COIN);
        ECKey myKey = new ECKey();
        this.wallet.importKey(myKey);
        // Simulate another signatory
        ECKey otherKey = new ECKey();
        // Create multi-sig transaction
        Transaction multiSigTransaction = new Transaction(TestWithWallet.UNITTEST);
        ImmutableList<ECKey> keys = ImmutableList.of(myKey, otherKey);
        Script scriptPubKey = ScriptBuilder.createMultiSigOutputScript(2, keys);
        multiSigTransaction.addOutput(COIN, scriptPubKey);
        SendRequest req = SendRequest.forTx(multiSigTransaction);
        this.wallet.completeTx(req);
        TransactionOutput multiSigTransactionOutput = multiSigTransaction.getOutput(0);
        Assert.assertThat(multiSigTransactionOutput.toString(), CoreMatchers.containsString("CHECKMULTISIG"));
    }

    @Test
    public void testP2SHOutputScript() throws Exception {
        String P2SHAddressString = "35b9vsyH1KoFT5a5KtrKusaCcPLkiSo1tU";
        Address P2SHAddress = LegacyAddress.fromBase58(TestWithWallet.MAINNET, P2SHAddressString);
        Script script = ScriptBuilder.createOutputScript(P2SHAddress);
        Transaction tx = new Transaction(TestWithWallet.MAINNET);
        tx.addOutput(COIN, script);
        Assert.assertEquals(P2SHAddressString, tx.getOutput(0).getScriptPubKey().getToAddress(TestWithWallet.MAINNET).toString());
    }

    @Test
    public void getAddressTests() throws Exception {
        Transaction tx = new Transaction(TestWithWallet.MAINNET);
        tx.addOutput(CENT, ScriptBuilder.createOpReturnScript("hello world!".getBytes()));
        Assert.assertTrue(ScriptPattern.isOpReturn(tx.getOutput(0).getScriptPubKey()));
        Assert.assertFalse(ScriptPattern.isP2PK(tx.getOutput(0).getScriptPubKey()));
        Assert.assertFalse(ScriptPattern.isP2PKH(tx.getOutput(0).getScriptPubKey()));
    }

    @Test
    public void getMinNonDustValue() throws Exception {
        TransactionOutput payToAddressOutput = new TransactionOutput(TestWithWallet.UNITTEST, null, COIN, myAddress);
        Assert.assertEquals(MIN_NONDUST_OUTPUT, payToAddressOutput.getMinNonDustValue());
    }
}

