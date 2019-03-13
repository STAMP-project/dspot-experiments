package org.tron.core.capsule.utils;


import Constant.TEST_CONF;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.application.TronApplicationContext;
import org.tron.core.Wallet;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;


@Slf4j
public class ExchangeProcessorTest {
    private static ExchangeProcessor processor;

    private static final String dbPath = "output_buy_exchange_processor_test";

    private static TronApplicationContext context;

    private static final String OWNER_ADDRESS;

    private static final String OWNER_ADDRESS_INVALID = "aaaa";

    private static final String OWNER_ACCOUNT_INVALID;

    private static final long initBalance = 10000000000000000L;

    static {
        Args.setParam(new String[]{ "--output-directory", ExchangeProcessorTest.dbPath }, TEST_CONF);
        ExchangeProcessorTest.context = new TronApplicationContext(DefaultConfig.class);
        OWNER_ADDRESS = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a1abc";
        OWNER_ACCOUNT_INVALID = (Wallet.getAddressPreFixString()) + "548794500882809695a8a687866e76d4271a3456";
    }

    @Test
    public void testExchange() {
        long sellBalance = 100000000000000L;
        long buyBalance = ((128L * 1024) * 1024) * 1024;
        long sellQuant = 2000000000000L;// 2 million trx

        long result = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(2694881440L, result);
    }

    @Test
    public void testExchange2() {
        long sellBalance = 100000000000000L;
        long buyBalance = ((128L * 1024) * 1024) * 1024;
        long sellQuant = 1000000000000L;// 2 million trx

        long result = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(1360781717L, result);
        sellBalance += sellQuant;
        buyBalance -= result;
        long result2 = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals((2694881440L - 1360781717L), result2);
    }

    @Test
    public void testSellAndBuy() {
        long sellBalance = 100000000000000L;
        long buyBalance = ((128L * 1024) * 1024) * 1024;
        long sellQuant = 2000000000000L;// 2 million trx

        long result = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(2694881440L, result);
        sellBalance += sellQuant;
        buyBalance -= result;
        long result2 = ExchangeProcessorTest.processor.exchange(buyBalance, sellBalance, result);
        Assert.assertEquals(1999999999542L, result2);
    }

    @Test
    public void testSellAndBuy2() {
        long sellBalance = 100000000000000L;
        long buyBalance = ((128L * 1024) * 1024) * 1024;
        long sellQuant = 2000000000000L;// 2 million trx

        long result = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(2694881440L, result);
        sellBalance += sellQuant;
        buyBalance -= result;
        long quant1 = 2694881440L - 1360781717L;
        long quant2 = 1360781717L;
        long result1 = ExchangeProcessorTest.processor.exchange(buyBalance, sellBalance, quant1);
        Assert.assertEquals(999999999927L, result1);
        buyBalance += quant1;
        sellBalance -= result1;
        long result2 = ExchangeProcessorTest.processor.exchange(buyBalance, sellBalance, quant2);
        Assert.assertEquals(999999999587L, result2);
    }

    @Test
    public void testInject() {
        long sellBalance = 1000000000000L;
        long buyBalance = 10000000L;
        long sellQuant = 10000000L;// 10 trx

        long result = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(99L, result);
        // inject
        sellBalance += 100000000000L;
        buyBalance += 1000000L;
        long result2 = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(99L, result2);
    }

    @Test
    public void testWithdraw() {
        long sellBalance = 1000000000000L;
        long buyBalance = 10000000L;
        long sellQuant = 10000000L;// 10 trx

        long result = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(99L, result);
        // inject
        sellBalance -= 800000000000L;
        buyBalance -= 8000000L;
        long result2 = ExchangeProcessorTest.processor.exchange(sellBalance, buyBalance, sellQuant);
        Assert.assertEquals(99L, result2);
    }
}

