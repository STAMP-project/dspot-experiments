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


import bisq.core.btc.wallet.BtcWalletService;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import org.bitcoinj.core.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DisplayedTransactionsTest {
    @Test
    public void testUpdate() {
        Set<Transaction> transactions = Sets.newHashSet(Mockito.mock(Transaction.class), Mockito.mock(Transaction.class));
        BtcWalletService walletService = Mockito.mock(BtcWalletService.class);
        Mockito.when(walletService.getTransactions(false)).thenReturn(transactions);
        TransactionListItemFactory transactionListItemFactory = Mockito.mock(TransactionListItemFactory.class, Mockito.RETURNS_DEEP_STUBS);
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        DisplayedTransactions testedEntity = new DisplayedTransactions(walletService, Mockito.mock(TradableRepository.class), transactionListItemFactory, Mockito.mock(TransactionAwareTradableFactory.class));
        testedEntity.update();
        Assert.assertEquals(transactions.size(), testedEntity.size());
    }

    @Test
    public void testUpdateWhenRepositoryIsEmpty() {
        BtcWalletService walletService = Mockito.mock(BtcWalletService.class);
        Mockito.when(walletService.getTransactions(false)).thenReturn(Collections.singleton(Mockito.mock(Transaction.class)));
        TradableRepository tradableRepository = Mockito.mock(TradableRepository.class);
        Mockito.when(tradableRepository.getAll()).thenReturn(Collections.emptySet());
        TransactionListItemFactory transactionListItemFactory = Mockito.mock(TransactionListItemFactory.class);
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        DisplayedTransactions testedEntity = new DisplayedTransactions(walletService, tradableRepository, transactionListItemFactory, Mockito.mock(TransactionAwareTradableFactory.class));
        testedEntity.update();
        Assert.assertEquals(1, testedEntity.size());
        Mockito.verify(transactionListItemFactory).create(ArgumentMatchers.any(), ArgumentMatchers.nullable(TransactionAwareTradable.class));
    }
}

