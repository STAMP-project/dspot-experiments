package org.web3j.protocol.core;


import DefaultBlockParameterName.LATEST;
import java.math.BigInteger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;


/**
 * Flowable callback tests.
 */
public class FlowableIT {
    private static Logger log = LoggerFactory.getLogger(FlowableIT.class);

    private static final int EVENT_COUNT = 5;

    private static final int TIMEOUT_MINUTES = 5;

    private Web3j web3j;

    @Test
    public void testBlockFlowable() throws Exception {
        run(web3j.blockFlowable(false));
    }

    @Test
    public void testPendingTransactionFlowable() throws Exception {
        run(web3j.pendingTransactionFlowable());
    }

    @Test
    public void testTransactionFlowable() throws Exception {
        run(web3j.transactionFlowable());
    }

    @Test
    public void testLogFlowable() throws Exception {
        run(web3j.ethLogFlowable(new EthFilter()));
    }

    @Test
    public void testReplayFlowable() throws Exception {
        run(web3j.replayPastBlocksFlowable(new DefaultBlockParameterNumber(0), new DefaultBlockParameterNumber(FlowableIT.EVENT_COUNT), true));
    }

    @Test
    public void testReplayPastAndFutureBlocksFlowable() throws Exception {
        EthBlock ethBlock = web3j.ethGetBlockByNumber(LATEST, false).send();
        BigInteger latestBlockNumber = ethBlock.getBlock().getNumber();
        run(web3j.replayPastAndFutureBlocksFlowable(new DefaultBlockParameterNumber(latestBlockNumber.subtract(BigInteger.ONE)), false));
    }
}

