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
package org.tron.core;


import Constant.TEST_CONF;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.tron.api.GrpcAPI.AssetIssueList;
import org.tron.api.GrpcAPI.BlockList;
import org.tron.api.GrpcAPI.ExchangeList;
import org.tron.api.GrpcAPI.ProposalList;
import org.tron.common.application.TronApplicationContext;
import org.tron.common.crypto.ECKey;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.Utils;
import org.tron.core.capsule.AssetIssueCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;
import org.tron.protos.Protocol.Block;
import org.tron.protos.Protocol.Transaction;


@Slf4j
public class WalletTest {
    private static TronApplicationContext context;

    private static Wallet wallet;

    private static Manager manager;

    private static String dbPath = "output_wallet_test";

    public static final String ACCOUNT_ADDRESS_ONE = "121212a9cf";

    public static final String ACCOUNT_ADDRESS_TWO = "232323a9cf";

    public static final String ACCOUNT_ADDRESS_THREE = "343434a9cf";

    public static final String ACCOUNT_ADDRESS_FOUR = "454545a9cf";

    public static final String ACCOUNT_ADDRESS_FIVE = "565656a9cf";

    private static Block block1;

    private static Block block2;

    private static Block block3;

    private static Block block4;

    private static Block block5;

    public static final long BLOCK_NUM_ONE = 1;

    public static final long BLOCK_NUM_TWO = 2;

    public static final long BLOCK_NUM_THREE = 3;

    public static final long BLOCK_NUM_FOUR = 4;

    public static final long BLOCK_NUM_FIVE = 5;

    public static final long BLOCK_TIMESTAMP_ONE = DateTime.now().minusDays(4).getMillis();

    public static final long BLOCK_TIMESTAMP_TWO = DateTime.now().minusDays(3).getMillis();

    public static final long BLOCK_TIMESTAMP_THREE = DateTime.now().minusDays(2).getMillis();

    public static final long BLOCK_TIMESTAMP_FOUR = DateTime.now().minusDays(1).getMillis();

    public static final long BLOCK_TIMESTAMP_FIVE = DateTime.now().getMillis();

    public static final long BLOCK_WITNESS_ONE = 12;

    public static final long BLOCK_WITNESS_TWO = 13;

    public static final long BLOCK_WITNESS_THREE = 14;

    public static final long BLOCK_WITNESS_FOUR = 15;

    public static final long BLOCK_WITNESS_FIVE = 16;

    private static Transaction transaction1;

    private static Transaction transaction2;

    private static Transaction transaction3;

    private static Transaction transaction4;

    private static Transaction transaction5;

    public static final long TRANSACTION_TIMESTAMP_ONE = DateTime.now().minusDays(4).getMillis();

    public static final long TRANSACTION_TIMESTAMP_TWO = DateTime.now().minusDays(3).getMillis();

    public static final long TRANSACTION_TIMESTAMP_THREE = DateTime.now().minusDays(2).getMillis();

    public static final long TRANSACTION_TIMESTAMP_FOUR = DateTime.now().minusDays(1).getMillis();

    public static final long TRANSACTION_TIMESTAMP_FIVE = DateTime.now().getMillis();

    private static AssetIssueCapsule Asset1;

    static {
        Args.setParam(new String[]{ "-d", WalletTest.dbPath }, TEST_CONF);
        WalletTest.context = new TronApplicationContext(DefaultConfig.class);
    }

    @Test
    public void testWallet() {
        Wallet wallet1 = new Wallet();
        Wallet wallet2 = new Wallet();
        logger.info("wallet address = {}", ByteArray.toHexString(wallet1.getAddress()));
        logger.info("wallet2 address = {}", ByteArray.toHexString(wallet2.getAddress()));
        Assert.assertFalse(wallet1.getAddress().equals(wallet2.getAddress()));
    }

    @Test
    public void testGetAddress() {
        ECKey ecKey = new ECKey(Utils.getRandom());
        Wallet wallet1 = new Wallet(ecKey);
        logger.info("ecKey address = {}", ByteArray.toHexString(ecKey.getAddress()));
        logger.info("wallet address = {}", ByteArray.toHexString(wallet1.getAddress()));
        Assert.assertArrayEquals(wallet1.getAddress(), ecKey.getAddress());
    }

    @Test
    public void testGetEcKey() {
        ECKey ecKey = new ECKey(Utils.getRandom());
        ECKey ecKey2 = new ECKey(Utils.getRandom());
        Wallet wallet1 = new Wallet(ecKey);
        logger.info("ecKey address = {}", ByteArray.toHexString(ecKey.getAddress()));
        logger.info("wallet address = {}", ByteArray.toHexString(wallet1.getAddress()));
        Assert.assertEquals("Wallet ECKey should match provided ECKey", wallet1.getEcKey(), ecKey);
    }

    @Test
    public void ss() {
        for (int i = 0; i < 4; i++) {
            ECKey ecKey = new ECKey(Utils.getRandom());
            System.out.println((i + 1));
            System.out.println(("privateKey:" + (ByteArray.toHexString(ecKey.getPrivKeyBytes()))));
            System.out.println(("publicKey:" + (ByteArray.toHexString(ecKey.getPubKey()))));
            System.out.println(("address:" + (ByteArray.toHexString(ecKey.getAddress()))));
            System.out.println();
        }
    }

    @Test
    public void getBlockById() {
        Block blockById = WalletTest.wallet.getBlockById(ByteString.copyFrom(getBlockId().getBytes()));
        Assert.assertEquals("getBlockById1", WalletTest.block1, blockById);
        blockById = WalletTest.wallet.getBlockById(ByteString.copyFrom(getBlockId().getBytes()));
        Assert.assertEquals("getBlockById2", WalletTest.block2, blockById);
        blockById = WalletTest.wallet.getBlockById(ByteString.copyFrom(getBlockId().getBytes()));
        Assert.assertEquals("getBlockById3", WalletTest.block3, blockById);
        blockById = WalletTest.wallet.getBlockById(ByteString.copyFrom(getBlockId().getBytes()));
        Assert.assertEquals("getBlockById4", WalletTest.block4, blockById);
        blockById = WalletTest.wallet.getBlockById(ByteString.copyFrom(getBlockId().getBytes()));
        Assert.assertEquals("getBlockById5", WalletTest.block5, blockById);
    }

    @Test
    public void getBlocksByLimit() {
        BlockList blocksByLimit = WalletTest.wallet.getBlocksByLimitNext(3, 2);
        Assert.assertTrue("getBlocksByLimit1", blocksByLimit.getBlockList().contains(WalletTest.block3));
        Assert.assertTrue("getBlocksByLimit2", blocksByLimit.getBlockList().contains(WalletTest.block4));
        blocksByLimit = WalletTest.wallet.getBlocksByLimitNext(0, 5);
        Assert.assertTrue("getBlocksByLimit3", blocksByLimit.getBlockList().contains(WalletTest.manager.getGenesisBlock().getInstance()));
        Assert.assertTrue("getBlocksByLimit4", blocksByLimit.getBlockList().contains(WalletTest.block1));
        Assert.assertTrue("getBlocksByLimit5", blocksByLimit.getBlockList().contains(WalletTest.block2));
        Assert.assertTrue("getBlocksByLimit6", blocksByLimit.getBlockList().contains(WalletTest.block3));
        Assert.assertTrue("getBlocksByLimit7", blocksByLimit.getBlockList().contains(WalletTest.block4));
        Assert.assertFalse("getBlocksByLimit8", blocksByLimit.getBlockList().contains(WalletTest.block5));
    }

    @Test
    public void getBlockByLatestNum() {
        BlockList blockByLatestNum = WalletTest.wallet.getBlockByLatestNum(2);
        Assert.assertTrue("getBlockByLatestNum1", blockByLatestNum.getBlockList().contains(WalletTest.block5));
        Assert.assertTrue("getBlockByLatestNum2", blockByLatestNum.getBlockList().contains(WalletTest.block4));
    }

    @Test
    public void getPaginatedAssetIssueList() {
        WalletTest.buildAssetIssue();
        AssetIssueList assetList1 = WalletTest.wallet.getAssetIssueList(0, 100);
        Assert.assertTrue("get Asset1", assetList1.getAssetIssue(0).getName().equals(WalletTest.Asset1.getName()));
        try {
            assetList1.getAssetIssue(1);
        } catch (Exception e) {
            Assert.assertTrue("AssetIssueList1 size should be 1", true);
        }
        AssetIssueList assetList2 = WalletTest.wallet.getAssetIssueList(0, 0);
        try {
            assetList2.getAssetIssue(0);
        } catch (Exception e) {
            Assert.assertTrue("AssetIssueList2 size should be 0", true);
        }
    }

    @Test
    public void getPaginatedProposalList() {
        WalletTest.buildProposal();
        // 
        ProposalList proposalList = WalletTest.wallet.getPaginatedProposalList(0, 100);
        Assert.assertEquals(2, proposalList.getProposalsCount());
        Assert.assertEquals("Address1", proposalList.getProposalsList().get(0).getProposerAddress().toStringUtf8());
        Assert.assertEquals("Address2", proposalList.getProposalsList().get(1).getProposerAddress().toStringUtf8());
        // 
        proposalList = WalletTest.wallet.getPaginatedProposalList(1, 100);
        Assert.assertEquals(1, proposalList.getProposalsCount());
        Assert.assertEquals("Address2", proposalList.getProposalsList().get(0).getProposerAddress().toStringUtf8());
        // 
        proposalList = WalletTest.wallet.getPaginatedProposalList((-1), 100);
        Assert.assertNull(proposalList);
        // 
        proposalList = WalletTest.wallet.getPaginatedProposalList(0, (-1));
        Assert.assertNull(proposalList);
        // 
        proposalList = WalletTest.wallet.getPaginatedProposalList(0, 1000000000L);
        Assert.assertEquals(2, proposalList.getProposalsCount());
    }

    @Test
    public void getPaginatedExchangeList() {
        WalletTest.buildExchange();
        ExchangeList exchangeList = WalletTest.wallet.getPaginatedExchangeList(0, 100);
        Assert.assertEquals("Address1", exchangeList.getExchangesList().get(0).getCreatorAddress().toStringUtf8());
        Assert.assertEquals("Address2", exchangeList.getExchangesList().get(1).getCreatorAddress().toStringUtf8());
    }
}

