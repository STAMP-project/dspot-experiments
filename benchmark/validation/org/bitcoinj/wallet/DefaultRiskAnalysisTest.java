/**
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
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
package org.bitcoinj.wallet;


import Coin.CENT;
import Coin.SATOSHI;
import DefaultRiskAnalysis.FACTORY;
import DefaultRiskAnalysis.MIN_ANALYSIS_NONDUST_OUTPUT;
import DefaultRiskAnalysis.RuleViolation.NONE;
import DefaultRiskAnalysis.RuleViolation.SHORTEST_POSSIBLE_PUSHDATA;
import ECKey.CURVE;
import ECKey.ECDSASignature;
import RiskAnalysis.Result.NON_FINAL;
import RiskAnalysis.Result.NON_STANDARD;
import RiskAnalysis.Result.OK;
import RuleViolation.SIGNATURE_CANONICAL_ENCODING;
import TransactionConfidence.Source.SELF;
import TransactionInput.NO_SEQUENCE;
import Utils.HEX;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.testing.FakeTxBuilder;
import org.junit.Assert;
import org.junit.Test;

import static TransactionInput.NO_SEQUENCE;


public class DefaultRiskAnalysisTest {
    // Uses mainnet because isStandard checks are disabled on testnet.
    private static final NetworkParameters MAINNET = MainNetParams.get();

    private Wallet wallet;

    private final int TIMESTAMP = 1384190189;

    private static final ECKey key1 = new ECKey();

    private final ImmutableList<Transaction> NO_DEPS = ImmutableList.of();

    @Test(expected = IllegalStateException.class)
    public void analysisCantBeUsedTwice() {
        Transaction tx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(OK, analysis.analyze());
        Assert.assertNull(analysis.getNonFinal());
        // Verify we can't re-use a used up risk analysis.
        analysis.analyze();
    }

    @Test
    public void nonFinal() throws Exception {
        // Verify that just having a lock time in the future is not enough to be considered risky (it's still final).
        Transaction tx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        TransactionInput input = tx.addInput(DefaultRiskAnalysisTest.MAINNET.getGenesisBlock().getTransactions().get(0).getOutput(0));
        tx.addOutput(COIN, DefaultRiskAnalysisTest.key1);
        tx.setLockTime(((TIMESTAMP) + 86400));
        DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(OK, analysis.analyze());
        Assert.assertNull(analysis.getNonFinal());
        // Set a sequence number on the input to make it genuinely non-final. Verify it's risky.
        input.setSequenceNumber(((NO_SEQUENCE) - 1));
        analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(NON_FINAL, analysis.analyze());
        Assert.assertEquals(tx, analysis.getNonFinal());
        // If the lock time is the current block, it's about to become final and we consider it non-risky.
        tx.setLockTime(1000);
        analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(OK, analysis.analyze());
    }

    @Test
    public void selfCreatedAreNotRisky() {
        Transaction tx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        tx.addInput(DefaultRiskAnalysisTest.MAINNET.getGenesisBlock().getTransactions().get(0).getOutput(0)).setSequenceNumber(1);
        tx.addOutput(COIN, DefaultRiskAnalysisTest.key1);
        tx.setLockTime(((TIMESTAMP) + 86400));
        {
            // Is risky ...
            DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx, NO_DEPS);
            Assert.assertEquals(NON_FINAL, analysis.analyze());
        }
        tx.getConfidence().setSource(SELF);
        {
            // Is no longer risky.
            DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx, NO_DEPS);
            Assert.assertEquals(OK, analysis.analyze());
        }
    }

    @Test
    public void nonFinalDependency() {
        // Final tx has a dependency that is non-final.
        Transaction tx1 = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        tx1.addInput(DefaultRiskAnalysisTest.MAINNET.getGenesisBlock().getTransactions().get(0).getOutput(0)).setSequenceNumber(1);
        TransactionOutput output = tx1.addOutput(COIN, DefaultRiskAnalysisTest.key1);
        tx1.setLockTime(((TIMESTAMP) + 86400));
        Transaction tx2 = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        tx2.addInput(output);
        tx2.addOutput(COIN, new ECKey());
        DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx2, ImmutableList.of(tx1));
        Assert.assertEquals(NON_FINAL, analysis.analyze());
        Assert.assertEquals(tx1, analysis.getNonFinal());
    }

    @Test
    public void nonStandardDust() {
        Transaction standardTx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        standardTx.addInput(DefaultRiskAnalysisTest.MAINNET.getGenesisBlock().getTransactions().get(0).getOutput(0));
        standardTx.addOutput(COIN, DefaultRiskAnalysisTest.key1);
        Assert.assertEquals(OK, FACTORY.create(wallet, standardTx, NO_DEPS).analyze());
        Transaction dustTx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        dustTx.addInput(DefaultRiskAnalysisTest.MAINNET.getGenesisBlock().getTransactions().get(0).getOutput(0));
        dustTx.addOutput(SATOSHI, DefaultRiskAnalysisTest.key1);// 1 Satoshi

        Assert.assertEquals(NON_STANDARD, FACTORY.create(wallet, dustTx, NO_DEPS).analyze());
        Transaction edgeCaseTx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        edgeCaseTx.addInput(DefaultRiskAnalysisTest.MAINNET.getGenesisBlock().getTransactions().get(0).getOutput(0));
        edgeCaseTx.addOutput(MIN_ANALYSIS_NONDUST_OUTPUT, DefaultRiskAnalysisTest.key1);// Dust threshold

        Assert.assertEquals(OK, FACTORY.create(wallet, edgeCaseTx, NO_DEPS).analyze());
    }

    @Test
    public void nonShortestPossiblePushData() {
        ScriptChunk nonStandardChunk = new ScriptChunk(OP_PUSHDATA1, new byte[75]);
        byte[] nonStandardScript = new ScriptBuilder().addChunk(nonStandardChunk).build().getProgram();
        // Test non-standard script as an input.
        Transaction tx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        Assert.assertEquals(NONE, DefaultRiskAnalysis.isStandard(tx));
        tx.addInput(new TransactionInput(DefaultRiskAnalysisTest.MAINNET, null, nonStandardScript));
        Assert.assertEquals(SHORTEST_POSSIBLE_PUSHDATA, DefaultRiskAnalysis.isStandard(tx));
        // Test non-standard script as an output.
        tx.clearInputs();
        Assert.assertEquals(NONE, DefaultRiskAnalysis.isStandard(tx));
        tx.addOutput(new TransactionOutput(DefaultRiskAnalysisTest.MAINNET, null, COIN, nonStandardScript));
        Assert.assertEquals(SHORTEST_POSSIBLE_PUSHDATA, DefaultRiskAnalysis.isStandard(tx));
    }

    @Test
    public void canonicalSignature() {
        TransactionSignature sig = TransactionSignature.dummy();
        Script scriptOk = ScriptBuilder.createInputScript(sig);
        Assert.assertEquals(RuleViolation.NONE, DefaultRiskAnalysis.isInputStandard(new TransactionInput(DefaultRiskAnalysisTest.MAINNET, null, scriptOk.getProgram())));
        byte[] sigBytes = sig.encodeToBitcoin();
        // Appending a zero byte makes the signature uncanonical without violating DER encoding.
        Script scriptUncanonicalEncoding = new ScriptBuilder().data(Arrays.copyOf(sigBytes, ((sigBytes.length) + 1))).build();
        Assert.assertEquals(SIGNATURE_CANONICAL_ENCODING, DefaultRiskAnalysis.isInputStandard(new TransactionInput(DefaultRiskAnalysisTest.MAINNET, null, scriptUncanonicalEncoding.getProgram())));
    }

    @Test
    public void canonicalSignatureLowS() throws Exception {
        // First, a synthetic test.
        TransactionSignature sig = TransactionSignature.dummy();
        Script scriptHighS = ScriptBuilder.createInputScript(new TransactionSignature(sig.r, CURVE.getN().subtract(sig.s)));
        Assert.assertEquals(SIGNATURE_CANONICAL_ENCODING, DefaultRiskAnalysis.isInputStandard(new TransactionInput(DefaultRiskAnalysisTest.MAINNET, null, scriptHighS.getProgram())));
        // This is a real transaction. Its signatures S component is "low".
        Transaction tx1 = new Transaction(DefaultRiskAnalysisTest.MAINNET, HEX.decode("010000000200a2be4376b7f47250ad9ad3a83b6aa5eb6a6d139a1f50771704d77aeb8ce76c010000006a4730440220055723d363cd2d4fe4e887270ebdf5c4b99eaf233a5c09f9404f888ec8b839350220763c3794d310b384ce86decfb05787e5bfa5d31983db612a2dde5ffec7f396ae012102ef47e27e0c4bdd6dc83915f185d972d5eb8515c34d17bad584a9312e59f4e0bcffffffff52239451d37757eeacb86d32864ec1ee6b6e131d1e3fee6f1cff512703b71014030000006b483045022100ea266ac4f893d98a623a6fc0e6a961cd5a3f32696721e87e7570a68851917e75022056d75c3b767419f6f6cb8189a0ad78d45971523908dc4892f7594b75fd43a8d00121038bb455ca101ebbb0ecf7f5c01fa1dcb7d14fbf6b7d7ea52ee56f0148e72a736cffffffff0630b15a00000000001976a9146ae477b690cf85f21c2c01e2c8639a5c18dc884e88ac4f260d00000000001976a91498d08c02ab92a671590adb726dddb719695ee12e88ac65753b00000000001976a9140b2eb4ba6d364c82092f25775f56bc10cd92c8f188ac65753b00000000001976a914d1cb414e22081c6ba3a935635c0f1d837d3c5d9188ac65753b00000000001976a914df9d137a0d279471a2796291874c29759071340b88ac3d753b00000000001976a91459f5aa4815e3aa8e1720e8b82f4ac8e6e904e47d88ac00000000"));
        Assert.assertEquals("2a1c8569b2b01ebac647fb94444d1118d4d00e327456a3c518e40d47d72cd5fe", tx1.getTxId().toString());
        Assert.assertEquals(RuleViolation.NONE, DefaultRiskAnalysis.isStandard(tx1));
        // This tx is the same as the above, except for a "high" S component on the signature of input 1.
        // It was part of the Oct 2015 malleability attack.
        Transaction tx2 = new Transaction(DefaultRiskAnalysisTest.MAINNET, HEX.decode("010000000200a2be4376b7f47250ad9ad3a83b6aa5eb6a6d139a1f50771704d77aeb8ce76c010000006a4730440220055723d363cd2d4fe4e887270ebdf5c4b99eaf233a5c09f9404f888ec8b839350220763c3794d310b384ce86decfb05787e5bfa5d31983db612a2dde5ffec7f396ae012102ef47e27e0c4bdd6dc83915f185d972d5eb8515c34d17bad584a9312e59f4e0bcffffffff52239451d37757eeacb86d32864ec1ee6b6e131d1e3fee6f1cff512703b71014030000006c493046022100ea266ac4f893d98a623a6fc0e6a961cd5a3f32696721e87e7570a68851917e75022100a928a3c4898be60909347e765f52872a613d8aada66c57a8c8791316d2f298710121038bb455ca101ebbb0ecf7f5c01fa1dcb7d14fbf6b7d7ea52ee56f0148e72a736cffffffff0630b15a00000000001976a9146ae477b690cf85f21c2c01e2c8639a5c18dc884e88ac4f260d00000000001976a91498d08c02ab92a671590adb726dddb719695ee12e88ac65753b00000000001976a9140b2eb4ba6d364c82092f25775f56bc10cd92c8f188ac65753b00000000001976a914d1cb414e22081c6ba3a935635c0f1d837d3c5d9188ac65753b00000000001976a914df9d137a0d279471a2796291874c29759071340b88ac3d753b00000000001976a91459f5aa4815e3aa8e1720e8b82f4ac8e6e904e47d88ac00000000"));
        Assert.assertEquals("dbe4147cf89b89fd9fa6c8ce6a3e2adecb234db094ec88301ae09073ca17d61d", tx2.getTxId().toString());
        Assert.assertFalse(ECDSASignature.decodeFromDER(getChunks().get(0).data).isCanonical());
        Assert.assertEquals(SIGNATURE_CANONICAL_ENCODING, DefaultRiskAnalysis.isStandard(tx2));
    }

    @Test
    public void standardOutputs() throws Exception {
        Transaction tx = new Transaction(DefaultRiskAnalysisTest.MAINNET);
        tx.addInput(DefaultRiskAnalysisTest.MAINNET.getGenesisBlock().getTransactions().get(0).getOutput(0));
        // A pay to address output
        tx.addOutput(CENT, ScriptBuilder.createP2PKHOutputScript(DefaultRiskAnalysisTest.key1));
        // A pay to pubkey output
        tx.addOutput(CENT, ScriptBuilder.createP2PKOutputScript(DefaultRiskAnalysisTest.key1));
        tx.addOutput(CENT, ScriptBuilder.createP2PKOutputScript(DefaultRiskAnalysisTest.key1));
        // 1-of-2 multisig output.
        ImmutableList<ECKey> keys = ImmutableList.of(DefaultRiskAnalysisTest.key1, new ECKey());
        tx.addOutput(CENT, ScriptBuilder.createMultiSigOutputScript(1, keys));
        // 2-of-2 multisig output.
        tx.addOutput(CENT, ScriptBuilder.createMultiSigOutputScript(2, keys));
        // P2SH
        tx.addOutput(CENT, ScriptBuilder.createP2SHOutputScript(1, keys));
        // OP_RETURN
        tx.addOutput(CENT, ScriptBuilder.createOpReturnScript("hi there".getBytes()));
        Assert.assertEquals(OK, FACTORY.create(wallet, tx, NO_DEPS).analyze());
    }

    @Test
    public void optInFullRBF() throws Exception {
        Transaction tx = FakeTxBuilder.createFakeTx(DefaultRiskAnalysisTest.MAINNET);
        tx.getInput(0).setSequenceNumber(((NO_SEQUENCE) - 2));
        DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(NON_FINAL, analysis.analyze());
        Assert.assertEquals(tx, analysis.getNonFinal());
    }

    @Test
    public void relativeLockTime() throws Exception {
        Transaction tx = FakeTxBuilder.createFakeTx(DefaultRiskAnalysisTest.MAINNET);
        tx.setVersion(2);
        Preconditions.checkState((!(tx.hasRelativeLockTime())));
        tx.getInput(0).setSequenceNumber(NO_SEQUENCE);
        DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(OK, analysis.analyze());
        tx.getInput(0).setSequenceNumber(0);
        analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(NON_FINAL, analysis.analyze());
        Assert.assertEquals(tx, analysis.getNonFinal());
    }

    @Test
    public void transactionVersions() throws Exception {
        Transaction tx = FakeTxBuilder.createFakeTx(DefaultRiskAnalysisTest.MAINNET);
        tx.setVersion(1);
        DefaultRiskAnalysis analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(OK, analysis.analyze());
        tx.setVersion(2);
        analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(OK, analysis.analyze());
        tx.setVersion(3);
        analysis = FACTORY.create(wallet, tx, NO_DEPS);
        Assert.assertEquals(NON_STANDARD, analysis.analyze());
        Assert.assertEquals(tx, analysis.getNonStandard());
    }
}

