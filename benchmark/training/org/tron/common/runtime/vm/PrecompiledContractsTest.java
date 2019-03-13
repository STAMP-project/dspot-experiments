package org.tron.common.runtime.vm;


import Constant.TEST_CONF;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.tron.common.application.Application;
import org.tron.common.application.ApplicationFactory;
import org.tron.common.application.TronApplicationContext;
import org.tron.core.Wallet;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.db.Manager;


@Slf4j
public class PrecompiledContractsTest {
    // common
    private static final DataWord voteContractAddr = new DataWord("0000000000000000000000000000000000000000000000000000000000010001");

    // private static final DataWord freezeBalanceAddr = new DataWord(
    // "0000000000000000000000000000000000000000000000000000000000010002");
    // private static final DataWord unFreezeBalanceAddr = new DataWord(
    // "0000000000000000000000000000000000000000000000000000000000010003");
    private static final DataWord withdrawBalanceAddr = new DataWord("0000000000000000000000000000000000000000000000000000000000010004");

    private static final DataWord proposalApproveAddr = new DataWord("0000000000000000000000000000000000000000000000000000000000010005");

    private static final DataWord proposalCreateAddr = new DataWord("0000000000000000000000000000000000000000000000000000000000010006");

    private static final DataWord proposalDeleteAddr = new DataWord("0000000000000000000000000000000000000000000000000000000000010007");

    private static final DataWord convertFromTronBytesAddressAddr = new DataWord("0000000000000000000000000000000000000000000000000000000000010008");

    private static final DataWord convertFromTronBase58AddressAddr = new DataWord("0000000000000000000000000000000000000000000000000000000000010009");

    private static TronApplicationContext context;

    private static Application appT;

    private static Manager dbManager;

    private static final String dbPath = "output_PrecompiledContracts_test";

    private static final String ACCOUNT_NAME = "account";

    private static final String OWNER_ADDRESS;

    private static final String WITNESS_NAME = "witness";

    private static final String WITNESS_ADDRESS;

    private static final String WITNESS_ADDRESS_BASE = "548794500882809695a8a687866e76d4271a1abc";

    private static final String URL = "https://tron.network";

    // withdraw
    private static final long initBalance = 10000000000L;

    private static final long allowance = 32000000L;

    static {
        Args.setParam(new String[]{ "--output-directory", PrecompiledContractsTest.dbPath, "--debug" }, TEST_CONF);
        PrecompiledContractsTest.context = new TronApplicationContext(DefaultConfig.class);
        PrecompiledContractsTest.appT = ApplicationFactory.create(PrecompiledContractsTest.context);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "abd4b9367799eaa3197fecb144eb71de1e049abc";
        WITNESS_ADDRESS = (Wallet.getAddressPreFixString()) + (PrecompiledContractsTest.WITNESS_ADDRESS_BASE);
    }

    @Test
    public void convertFromTronBytesAddressNativeTest() {
        // PrecompiledContract contract = createPrecompiledContract(convertFromTronBytesAddressAddr, WITNESS_ADDRESS);
        // byte[] solidityAddress = contract.execute(new DataWord(WITNESS_ADDRESS).getData()).getRight();
        // Assert.assertArrayEquals(solidityAddress,new DataWord(Hex.decode(WITNESS_ADDRESS_BASE)).getData());
    }
}

