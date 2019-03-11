/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.desktop.main.funds.transactions;


import bisq.core.arbitration.Dispute;
import bisq.core.arbitration.DisputeManager;
import bisq.core.trade.Trade;
import java.util.Collections;
import javafx.collections.FXCollections;
import org.bitcoinj.core.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Dispute.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
@SuppressWarnings("ConstantConditions")
public class TransactionAwareTradeTest {
    private static final String XID = "123";

    private Transaction transaction;

    private DisputeManager manager;

    private Trade delegate;

    private TransactionAwareTradable trade;

    @Test
    public void testIsRelatedToTransactionWhenTakerOfferFeeTx() {
        Mockito.when(delegate.getTakerFeeTxId()).thenReturn(TransactionAwareTradeTest.XID);
        Assert.assertTrue(trade.isRelatedToTransaction(transaction));
    }

    @Test
    public void testIsRelatedToTransactionWhenPayoutTx() {
        Mockito.when(delegate.getPayoutTx().getHashAsString()).thenReturn(TransactionAwareTradeTest.XID);
        Assert.assertTrue(trade.isRelatedToTransaction(transaction));
    }

    @Test
    public void testIsRelatedToTransactionWhenDepositTx() {
        Mockito.when(delegate.getDepositTx().getHashAsString()).thenReturn(TransactionAwareTradeTest.XID);
        Assert.assertTrue(trade.isRelatedToTransaction(transaction));
    }

    @Test
    public void testIsRelatedToTransactionWhenOfferFeeTx() {
        Mockito.when(delegate.getOffer().getOfferFeePaymentTxId()).thenReturn(TransactionAwareTradeTest.XID);
        Assert.assertTrue(trade.isRelatedToTransaction(transaction));
    }

    @Test
    public void testIsRelatedToTransactionWhenDisputedPayoutTx() {
        final String tradeId = "7";
        Dispute dispute = Mockito.mock(Dispute.class);
        Mockito.when(dispute.getDisputePayoutTxId()).thenReturn(TransactionAwareTradeTest.XID);
        Mockito.when(dispute.getTradeId()).thenReturn(tradeId);
        Mockito.when(manager.getDisputesAsObservableList()).thenReturn(FXCollections.observableArrayList(Collections.singleton(dispute)));
        Mockito.when(delegate.getId()).thenReturn(tradeId);
        Assert.assertTrue(trade.isRelatedToTransaction(transaction));
    }
}

