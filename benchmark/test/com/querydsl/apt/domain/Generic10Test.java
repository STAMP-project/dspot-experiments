package com.querydsl.apt.domain;


import QGeneric10Test_AbstractFuturesMarket.abstractFuturesMarket;
import QGeneric10Test_AbstractTradingMarket.abstractTradingMarket;
import QGeneric10Test_CommonFuturesMarket.commonFuturesMarket;
import QGeneric10Test_FutureTrade.futureTrade;
import QGeneric10Test_TradingMarketLedger.tradingMarketLedger;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import org.junit.Assert;
import org.junit.Test;


public class Generic10Test extends AbstractTest {
    public interface Tradable {}

    public interface Market<T extends Generic10Test.Tradable> {}

    @Entity
    public static class FutureTrade implements Generic10Test.Tradable {}

    @MappedSuperclass
    public abstract static class AbstractTradingMarket<T extends Generic10Test.Tradable> implements Generic10Test.Market<T> {
        @OneToOne
        private Generic10Test.TradingMarketLedger<Generic10Test.AbstractTradingMarket<T>> ledger;
    }

    @Entity
    public abstract static class AbstractFuturesMarket extends Generic10Test.AbstractTradingMarket<Generic10Test.FutureTrade> {}

    @Entity
    public static class CommonFuturesMarket extends Generic10Test.AbstractFuturesMarket {}

    @Entity
    public static class TradingMarketLedger<M extends Generic10Test.Market<? extends Generic10Test.Tradable>> {}

    @Test
    public void test() {
        Assert.assertNotNull(futureTrade);
        start(QGeneric10Test_AbstractTradingMarket.class, abstractTradingMarket);
        assertPresent("ledger");
        start(QGeneric10Test_AbstractFuturesMarket.class, abstractFuturesMarket);
        assertPresent("ledger");
        start(QGeneric10Test_CommonFuturesMarket.class, commonFuturesMarket);
        assertPresent("ledger");
        start(QGeneric10Test_TradingMarketLedger.class, tradingMarketLedger);
    }
}

