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


import CallTransaction.Contract;
import CallTransaction.Function;
import MainNetConfig.INSTANCE;
import SolidityCompiler.Options.ABI;
import SolidityCompiler.Options.BIN;
import SolidityCompiler.Result;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import org.ethereum.config.SystemProperties;
import org.ethereum.config.blockchain.HomesteadConfig;
import org.ethereum.core.genesis.GenesisLoader;
import org.ethereum.crypto.ECKey;
import org.ethereum.crypto.HashUtil;
import org.ethereum.jsontestsuite.suite.StateTestCase;
import org.ethereum.jsontestsuite.suite.StateTestSuite;
import org.ethereum.jsontestsuite.suite.runners.StateTestRunner;
import org.ethereum.solidity.compiler.CompilationResult;
import org.ethereum.solidity.compiler.SolidityCompiler;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.program.ProgramResult;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.BigIntegers;
import org.spongycastle.util.encoders.Hex;


public class TransactionTest {
    private static final Logger logger = LoggerFactory.getLogger(TransactionTest.class);

    /* sign transaction  https://tools.ietf.org/html/rfc6979 */
    @Test
    public void test1() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        // python taken exact data
        String txRLPRawData = "a9e880872386f26fc1000085e8d4a510008203e89413978aee95f38490e9769c39b2773ed763d9cd5f80";
        // String txRLPRawData = "f82804881bc16d674ec8000094cd2a3d9f938e13cd947ec05abc7fe734df8dd8268609184e72a0006480";
        byte[] cowPrivKey = Hex.decode("c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4");
        ECKey key = ECKey.fromPrivate(cowPrivKey);
        byte[] data = Hex.decode(txRLPRawData);
        // step 1: serialize + RLP encode
        // step 2: hash = keccak(step1)
        byte[] txHash = HashUtil.sha3(data);
        String signature = key.doSign(txHash).toBase64();
        TransactionTest.logger.info(signature);
    }

    // Testdata from: https://github.com/ethereum/tests/blob/master/txtest.json
    String RLP_ENCODED_RAW_TX = "e88085e8d4a510008227109413978aee95f38490e9769c39b2773ed763d9cd5f872386f26fc1000080";

    String RLP_ENCODED_UNSIGNED_TX = "eb8085e8d4a510008227109413978aee95f38490e9769c39b2773ed763d9cd5f872386f26fc1000080808080";

    String HASH_TX = "328ea6d24659dec48adea1aced9a136e5ebdf40258db30d1b1d97ed2b74be34e";

    String RLP_ENCODED_SIGNED_TX = "f86b8085e8d4a510008227109413978aee95f38490e9769c39b2773ed763d9cd5f872386f26fc10000801ba0eab47c1a49bf2fe5d40e01d313900e19ca485867d462fe06e139e3a536c6d4f4a014a569d327dcda4b29f74f93c0e9729d2f49ad726e703f9cd90dbb0fbf6649f1";

    String KEY = "c85ef7d79691fe79573b1a7064c19c1a9819ebdbd1faaab1a8ec92344438aaf4";

    byte[] testNonce = Hex.decode("");

    byte[] testGasPrice = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(1000000000000L));

    byte[] testGasLimit = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(10000));

    byte[] testReceiveAddress = Hex.decode("13978aee95f38490e9769c39b2773ed763d9cd5f");

    byte[] testValue = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(10000000000000000L));

    byte[] testData = Hex.decode("");

    byte[] testInit = Hex.decode("");

    @Test
    public void testTransactionCreateContract() {
        // String rlp =
        // "f89f808609184e72a0008203e8808203e8b84b4560005444602054600f60056002600a02010b0d630000001d596002602054630000003b5860066000530860056006600202010a0d6300000036596004604054630000003b5860056060541ca0ddc901d83110ea50bc40803f42083afea1bbd420548f6392a679af8e24b21345a06620b3b512bea5f0a272703e8d6933177c23afc79516fd0ca4a204aa6e34c7e9";
        byte[] senderPrivKey = HashUtil.sha3("cow".getBytes());
        byte[] nonce = BigIntegers.asUnsignedByteArray(BigInteger.ZERO);
        byte[] gasPrice = Hex.decode("09184e72a000");// 10000000000000

        byte[] gas = Hex.decode("03e8");
        // 1000
        byte[] recieveAddress = null;
        byte[] endowment = Hex.decode("03e8");// 10000000000000000"

        byte[] init = Hex.decode("4560005444602054600f60056002600a02010b0d630000001d596002602054630000003b5860066000530860056006600202010a0d6300000036596004604054630000003b586005606054");
        Transaction tx1 = new Transaction(nonce, gasPrice, gas, recieveAddress, endowment, init);
        tx1.sign(ECKey.fromPrivate(senderPrivKey));
        byte[] payload = tx1.getEncoded();
        TransactionTest.logger.info(Hex.toHexString(payload));
        Transaction tx2 = new Transaction(payload);
        // tx2.getSender();
        String plainTx1 = Hex.toHexString(tx1.getEncodedRaw());
        String plainTx2 = Hex.toHexString(tx2.getEncodedRaw());
        // Transaction tx = new Transaction(Hex.decode(rlp));
        TransactionTest.logger.info(("tx1.hash: " + (Hex.toHexString(tx1.getHash()))));
        TransactionTest.logger.info(("tx2.hash: " + (Hex.toHexString(tx2.getHash()))));
        TransactionTest.logger.info("");
        TransactionTest.logger.info(("plainTx1: " + plainTx1));
        TransactionTest.logger.info(("plainTx2: " + plainTx2));
        TransactionTest.logger.info(Hex.toHexString(tx2.getSender()));
    }

    @Test
    public void constantCallConflictTest() throws Exception {
        /* 0x095e7baea6a6c7c4c2dfeb977efac326af552d87 contract is the following Solidity code:

        contract Test {
        uint a = 256;

        function set(uint s) {
        a = s;
        }

        function get() returns (uint) {
        return a;
        }
        }
         */
        String json = "{ " + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("    'test1' : { " + "        'env' : { ") + "            'currentCoinbase' : '2adc25665018aa1fe0e6bc666dac8fc2697ff9ba', ") + "            'currentDifficulty' : '0x0100', ") + "            'currentGasLimit' : '0x0f4240', ") + "            'currentNumber' : '0x00', ") + "            'currentTimestamp' : '0x01', ") + "            'previousHash' : '5e20a0453cecd065ea59c37ac63e079ee08998b6045136a8ce6635c7912ec0b6' ") + "        }, ") + "        'logs' : [ ") + "        ], ") + "        'out' : '0x', ") + "        'post' : { ") + "            '095e7baea6a6c7c4c2dfeb977efac326af552d87' : { ") + "                'balance' : '0x0de0b6b3a76586a0', ") + "                'code' : '0x606060405260e060020a600035046360fe47b1811460245780636d4ce63c14602e575b005b6004356000556022565b6000546060908152602090f3', ") + "                'nonce' : '0x00', ") + "                'storage' : { ") + "                    '0x00' : '0x0400' ") + "                } ") + "            }, ") + "            '2adc25665018aa1fe0e6bc666dac8fc2697ff9ba' : { ") + "                'balance' : '0x67c3', ") + "                'code' : '0x', ") + "                'nonce' : '0x00', ") + "                'storage' : { ") + "                } ") + "            }, ") + "            'a94f5374fce5edbc8e2a8697c15331677e6ebf0b' : { ") + "                'balance' : '0x0DE0B6B3A762119D', ") + "                'code' : '0x', ") + "                'nonce' : '0x01', ") + "                'storage' : { ") + "                } ") + "            } ") + "        }, ") + "        'postStateRoot' : '17454a767e5f04461256f3812ffca930443c04a47d05ce3f38940c4a14b8c479', ") + "        'pre' : { ") + "            '095e7baea6a6c7c4c2dfeb977efac326af552d87' : { ") + "                'balance' : '0x0de0b6b3a7640000', ") + "                'code' : '0x606060405260e060020a600035046360fe47b1811460245780636d4ce63c14602e575b005b6004356000556022565b6000546060908152602090f3', ") + "                'nonce' : '0x00', ") + "                'storage' : { ") + "                    '0x00' : '0x02' ") + "                } ") + "            }, ") + "            'a94f5374fce5edbc8e2a8697c15331677e6ebf0b' : { ") + "                'balance' : '0x0de0b6b3a7640000', ") + "                'code' : '0x', ") + "                'nonce' : '0x00', ") + "                'storage' : { ") + "                } ") + "            } ") + "        }, ") + "        'transaction' : { ") + "            'data' : '0x60fe47b10000000000000000000000000000000000000000000000000000000000000400', ") + "            'gasLimit' : '0x061a80', ") + "            'gasPrice' : '0x01', ") + "            'nonce' : '0x00', ") + "            'secretKey' : '45a915e4d060149eb4365960e6a7a45f334393093061116b197e3240065ff2d8', ") + "            'to' : '095e7baea6a6c7c4c2dfeb977efac326af552d87', ") + "            'value' : '0x0186a0' ") + "        } ") + "    } ") + "}");
        StateTestSuite stateTestSuite = new StateTestSuite(json.replaceAll("'", "\""));
        List<String> res = new StateTestRunner(stateTestSuite.getTestCases().get("test1")) {
            @Override
            protected ProgramResult executeTransaction(Transaction tx) {
                // first emulating the constant call (Ethereum.callConstantFunction)
                // to ensure it doesn't affect the final state
                {
                    Repository track = repository.startTracking();
                    Transaction txConst = CallTransaction.createCallTransaction(0, 0, 100000000000000L, "095e7baea6a6c7c4c2dfeb977efac326af552d87", 0, Function.fromSignature("get"));
                    txConst.sign(new ECKey());
                    Block bestBlock = block;
                    TransactionExecutor executor = setLocalCall(true);
                    executor.init();
                    executor.execute();
                    executor.go();
                    executor.finalization();
                    track.rollback();
                    TransactionTest.logger.info(("Return value: " + (new IntType("uint").decode(executor.getResult().getHReturn()))));
                }
                // now executing the JSON test transaction
                return super.executeTransaction(tx);
            }
        }.runImpl();
        if (!(res.isEmpty()))
            throw new RuntimeException(("Test failed: " + res));

    }

    @Test
    public void homesteadContractCreationTest() throws Exception {
        // Checks Homestead updates (1) & (3) from
        // https://github.com/ethereum/EIPs/blob/master/EIPS/eip-2.mediawiki
        /* trying to create a contract with the following Solidity code:

        contract Test {
        uint a = 256;

        function set(uint s) {
        a = s;
        }

        function get() returns (uint) {
        return a;
        }
        }
         */
        int iBitLowGas = 89988;// [actual gas required] - 1

        String aBitLowGas = "0x0" + (Integer.toHexString(iBitLowGas));
        String senderPostBalance = "0x0" + (Long.toHexString((1000000000000000000L - iBitLowGas)));
        String json = (((((((((((((((((((((((((((((((((((((((("{ " + (((((((((((((("    'test1' : { " + "        'env' : { ") + "            'currentCoinbase' : '2adc25665018aa1fe0e6bc666dac8fc2697ff9ba', ") + "            'currentDifficulty' : '0x0100', ") + "            'currentGasLimit' : '0x0f4240', ") + "            'currentNumber' : '0x01', ") + "            'currentTimestamp' : '0x01', ") + "            'previousHash' : '5e20a0453cecd065ea59c37ac63e079ee08998b6045136a8ce6635c7912ec0b6' ") + "        }, ") + "        'logs' : [ ") + "        ], ") + "        'out' : '0x', ") + "        'post' : { ") + "            '2adc25665018aa1fe0e6bc666dac8fc2697ff9ba' : { ") + "                'balance' : '")) + aBitLowGas) + "', ") + "                'code' : '0x', ") + "                'nonce' : '0x00', ") + "                'storage' : { ") + "                } ") + "            },") + "            'a94f5374fce5edbc8e2a8697c15331677e6ebf0b' : { ") + "                'balance' : '") + senderPostBalance) + "', ") + "                'code' : '0x', ") + "                'nonce' : '0x01', ") + "                'storage' : { ") + "                } ") + "            } ") + "        }, ") + "        'postStateRoot' : '17454a767e5f04461256f3812ffca930443c04a47d05ce3f38940c4a14b8c479', ") + "        'pre' : { ") + "            'a94f5374fce5edbc8e2a8697c15331677e6ebf0b' : { ") + "                'balance' : '0x0de0b6b3a7640000', ") + "                'code' : '0x', ") + "                'nonce' : '0x00', ") + "                'storage' : { ") + "                } ") + "            } ") + "        }, ") + "        'transaction' : { ") + "            'data' : '0x6060604052610100600060005055603b8060196000396000f3606060405260e060020a600035046360fe47b1811460245780636d4ce63c14602e575b005b6004356000556022565b6000546060908152602090f3', ") + "            'gasLimit' : '") + aBitLowGas) + "', ") + "            'gasPrice' : '0x01', ") + "            'nonce' : '0x00', ") + "            'secretKey' : '45a915e4d060149eb4365960e6a7a45f334393093061116b197e3240065ff2d8', ") + "            'to' : '', ") + "            'value' : '0x0' ") + "        } ") + "    } ") + "}";
        StateTestSuite stateTestSuite = new StateTestSuite(json.replaceAll("'", "\""));
        TransactionTest.logger.info(json.replaceAll("'", "\""));
        try {
            SystemProperties.getDefault().setBlockchainConfig(new HomesteadConfig());
            List<String> res = new StateTestRunner(stateTestSuite.getTestCases().get("test1")).runImpl();
            if (!(res.isEmpty()))
                throw new RuntimeException(("Test failed: " + res));

        } finally {
            SystemProperties.getDefault().setBlockchainConfig(INSTANCE);
        }
    }

    @Test
    public void multiSuicideTest() throws IOException, InterruptedException {
        String contract = "pragma solidity ^0.4.3;" + (((((((((((("contract PsychoKiller {" + "    function () payable {}") + "    function homicide() {") + "        suicide(msg.sender);") + "    }") + "    function multipleHomocide() {") + "        PsychoKiller k  = this;") + "        k.homicide.gas(10000)();") + "        k.homicide.gas(10000)();") + "        k.homicide.gas(10000)();") + "        k.homicide.gas(10000)();") + "    }") + "}");
        SolidityCompiler.Result res = SolidityCompiler.compile(contract.getBytes(), true, ABI, BIN);
        TransactionTest.logger.info(res.errors);
        CompilationResult cres = CompilationResult.parse(res.output);
        BlockchainImpl blockchain = ImportLightTest.createBlockchain(GenesisLoader.loadGenesis(getClass().getResourceAsStream("/genesis/genesis-light.json")));
        ECKey sender = ECKey.fromPrivate(Hex.decode("3ec771c31cac8c0dba77a69e503765701d3c2bb62435888d4ffa38fed60c445c")).compress();
        TransactionTest.logger.info(("address: " + (Hex.toHexString(sender.getAddress()))));
        if ((cres.getContract("PsychoKiller")) != null) {
            Transaction tx = createTx(blockchain, sender, new byte[0], Hex.decode(cres.getContract("PsychoKiller").bin));
            executeTransaction(blockchain, tx);
            byte[] contractAddress = tx.getContractAddress();
            CallTransaction.Contract contract1 = new CallTransaction.Contract(cres.getContract("PsychoKiller").abi);
            byte[] callData = contract1.getByName("multipleHomocide").encode();
            Transaction tx1 = createTx(blockchain, sender, contractAddress, callData, 0L);
            ProgramResult programResult = executeTransaction(blockchain, tx1).getResult();
            // suicide of a single account should be counted only once
            Assert.assertEquals(24000, programResult.getFutureRefund());
        } else {
            Assert.fail();
        }
    }

    @Test
    public void receiptErrorTest() throws Exception {
        BlockchainImpl blockchain = ImportLightTest.createBlockchain(GenesisLoader.loadGenesis(getClass().getResourceAsStream("/genesis/genesis-light.json")));
        ECKey sender = ECKey.fromPrivate(Hex.decode("3ec771c31cac8c0dba77a69e503765701d3c2bb62435888d4ffa38fed60c445c"));
        {
            // Receipt RLP backward compatibility
            TransactionReceipt receipt = new TransactionReceipt(Hex.decode("f9010c80825208b9010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c082520880"));
            Assert.assertTrue(receipt.isValid());
            Assert.assertTrue(receipt.isSuccessful());
        }
        {
            Transaction tx = createTx(blockchain, sender, new byte[32], new byte[0], 100);
            TransactionReceipt receipt = executeTransaction(blockchain, tx).getReceipt();
            TransactionTest.logger.info(Hex.toHexString(receipt.getEncoded()));
            receipt = new TransactionReceipt(receipt.getEncoded());
            Assert.assertTrue(receipt.isValid());
            Assert.assertTrue(receipt.isSuccessful());
        }
        {
            Transaction tx = createTx(blockchain, new ECKey(), new byte[32], new byte[0], 100);
            TransactionReceipt receipt = executeTransaction(blockchain, tx).getReceipt();
            receipt = new TransactionReceipt(receipt.getEncoded());
            Assert.assertFalse(receipt.isValid());
            Assert.assertFalse(receipt.isSuccessful());
            Assert.assertTrue(receipt.getError().contains("Not enough"));
        }
        {
            Transaction tx = new Transaction(ByteUtil.intToBytesNoLeadZeroes(100500), ByteUtil.longToBytesNoLeadZeroes(1), ByteUtil.longToBytesNoLeadZeroes(3000000), new byte[0], ByteUtil.longToBytesNoLeadZeroes(0), new byte[0]);
            tx.sign(sender);
            TransactionReceipt receipt = executeTransaction(blockchain, tx).getReceipt();
            receipt = new TransactionReceipt(receipt.getEncoded());
            Assert.assertFalse(receipt.isValid());
            Assert.assertFalse(receipt.isSuccessful());
            Assert.assertTrue(receipt.getError().contains("nonce"));
        }
        {
            String contract = "contract GasConsumer {" + (((("    function GasConsumer() {" + "        int i = 0;") + "        while(true) sha3(i++);") + "    }") + "}");
            SolidityCompiler.Result res = SolidityCompiler.compile(contract.getBytes(), true, ABI, BIN);
            TransactionTest.logger.info(res.errors);
            CompilationResult cres = CompilationResult.parse(res.output);
            Transaction tx = createTx(blockchain, sender, new byte[0], Hex.decode(cres.getContract("GasConsumer").bin), 0);
            TransactionReceipt receipt = executeTransaction(blockchain, tx).getReceipt();
            receipt = new TransactionReceipt(receipt.getEncoded());
            Assert.assertTrue(receipt.isValid());
            Assert.assertFalse(receipt.isSuccessful());
            Assert.assertTrue(receipt.getError().contains("Not enough gas"));
        }
    }

    @Test
    public void afterEIP158Test() throws Exception {
        int chainId = 1;
        String rlpUnsigned = "ec098504a817c800825208943535353535353535353535353535353535353535880de0b6b3a764000080018080";
        String unsignedHash = "daf5a779ae972f972197303d7b574746c7ef83eadac0f2791ad23db92e4c8e53";
        String privateKey = "4646464646464646464646464646464646464646464646464646464646464646";
        BigInteger signatureR = new BigInteger("18515461264373351373200002665853028612451056578545711640558177340181847433846");
        BigInteger signatureS = new BigInteger("46948507304638947509940763649030358759909902576025900602547168820602576006531");
        String signedTxRlp = "f86c098504a817c800825208943535353535353535353535353535353535353535880de0b6b3a76400008025a028ef61340bd939bc2195fe537567866003e1a15d3c71ff63e1590620aa636276a067cbe9d8997f761aecb703304b3800ccf555c9f3dc64214b297fb1966a3b6d83";
        Transaction tx = Transaction.create("3535353535353535353535353535353535353535", new BigInteger("1000000000000000000"), new BigInteger("9"), new BigInteger("20000000000"), new BigInteger("21000"), chainId);
        // Checking RLP of unsigned transaction and its hash
        Assert.assertArrayEquals(Hex.decode(rlpUnsigned), tx.getEncoded());
        Assert.assertArrayEquals(Hex.decode(unsignedHash), tx.getHash());
        Assert.assertNull(tx.getSignature());
        ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
        tx.sign(ecKey);
        // Checking modified signature
        Assert.assertEquals(signatureR, tx.getSignature().r);
        Assert.assertEquals(signatureS, tx.getSignature().s);
        // TODO: Strange, it's still 27. Why is signature used for getEncoded() never assigned to signature field?
        Assert.assertEquals(27, tx.getSignature().v);
        // Checking that we get correct TX in the end
        Assert.assertArrayEquals(Hex.decode(signedTxRlp), tx.getEncoded());
        // Check that we could correctly extract tx from new RLP
        Transaction txSigned = new Transaction(Hex.decode(signedTxRlp));
        Assert.assertEquals(((int) (txSigned.getChainId())), chainId);
    }

    @Test
    public void etcChainIdTest() {
        Transaction tx = new Transaction(Hex.decode("f871830617428504a817c80083015f90940123286bd94beecd40905321f5c3202c7628d685880ecab7b2bae2c27080819ea021355678b1aa704f6ad4706fb8647f5125beadd1d84c6f9cf37dda1b62f24b1aa06b4a64fd29bb6e54a2c5107e8be42ac039a8ffb631e16e7bcbd15cdfc0015ee2"));
        Integer chainId = tx.getChainId();
        Assert.assertEquals(61, chainId.intValue());
    }

    @Test
    public void longChainIdTest() {
        Transaction tx = new Transaction(Hex.decode("f8ae82477b8504a817c80083015f9094977ddf44438d540892d1b8618fea65395399971680b844eceb6e3e57696e6454757262696e655f30310000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000007827e19a025f55532f5cebec362f3f750a3b9c47ab76322622eb3a26ad24c80f9c388c15ba02dcc7ebcfb6ad6ae09f56a29d710cc4115e960a83b98405cf98f7177c14d8a51"));
        Integer chainId = tx.getChainId();
        Assert.assertEquals(16123, chainId.intValue());
        Transaction tx1 = Transaction.create("3535353535353535353535353535353535353535", new BigInteger("1000000000000000000"), new BigInteger("9"), new BigInteger("20000000000"), new BigInteger("21000"), 333333);
        ECKey key = new ECKey();
        tx1.sign(key);
        Transaction tx2 = new Transaction(tx1.getEncoded());
        Assert.assertEquals(333333, tx2.getChainId().intValue());
        Assert.assertArrayEquals(tx2.getSender(), key.getAddress());
    }

    @Test
    public void unsignedChainIdTransactionTest() {
        byte[] rlpUnsignedTx = Hex.decode("ef098504a817c800825208943535353535353535353535353535353535353535880de0b6b3a764000080830516158080");
        Transaction tx = new Transaction(rlpUnsignedTx);
        Assert.assertEquals(333333, ((long) (tx.getChainId())));
        Transaction copyTx = new Transaction(tx.getNonce(), tx.getGasPrice(), tx.getGasLimit(), tx.getReceiveAddress(), tx.getValue(), tx.getData(), tx.getChainId());
        Assert.assertArrayEquals(rlpUnsignedTx, copyTx.getEncoded());
    }
}

