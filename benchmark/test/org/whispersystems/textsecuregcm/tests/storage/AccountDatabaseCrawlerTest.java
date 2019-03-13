/**
 * Copyright (C) 2018 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.tests.storage;


import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountDatabaseCrawler;
import org.whispersystems.textsecuregcm.storage.AccountDatabaseCrawlerCache;
import org.whispersystems.textsecuregcm.storage.AccountDatabaseCrawlerListener;
import org.whispersystems.textsecuregcm.storage.Accounts;


public class AccountDatabaseCrawlerTest {
    private static final String ACCOUNT1 = "+1";

    private static final String ACCOUNT2 = "+2";

    private static final int CHUNK_SIZE = 1000;

    private static final long CHUNK_INTERVAL_MS = 30000L;

    private final Account account1 = Mockito.mock(Account.class);

    private final Account account2 = Mockito.mock(Account.class);

    private final Accounts accounts = Mockito.mock(Accounts.class);

    private final AccountDatabaseCrawlerListener listener = Mockito.mock(AccountDatabaseCrawlerListener.class);

    private final AccountDatabaseCrawlerCache cache = Mockito.mock(AccountDatabaseCrawlerCache.class);

    private final AccountDatabaseCrawler crawler = new AccountDatabaseCrawler(accounts, cache, Arrays.asList(listener), AccountDatabaseCrawlerTest.CHUNK_SIZE, AccountDatabaseCrawlerTest.CHUNK_INTERVAL_MS);

    @Test
    public void testCrawlStart() {
        Mockito.when(cache.getLastNumber()).thenReturn(Optional.empty());
        boolean accelerated = crawler.doPeriodicWork();
        assertThat(accelerated).isFalse();
        Mockito.verify(cache, Mockito.times(1)).claimActiveWork(ArgumentMatchers.any(String.class), ArgumentMatchers.anyLong());
        Mockito.verify(cache, Mockito.times(1)).getLastNumber();
        Mockito.verify(listener, Mockito.times(1)).onCrawlStart();
        Mockito.verify(accounts, Mockito.times(1)).getAllFrom(ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(accounts, Mockito.times(0)).getAllFrom(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(account1, Mockito.times(0)).getNumber();
        Mockito.verify(account2, Mockito.times(1)).getNumber();
        Mockito.verify(listener, Mockito.times(1)).onCrawlChunk(ArgumentMatchers.eq(Optional.empty()), ArgumentMatchers.eq(Arrays.asList(account1, account2)));
        Mockito.verify(cache, Mockito.times(1)).setLastNumber(ArgumentMatchers.eq(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT2)));
        Mockito.verify(cache, Mockito.times(1)).isAccelerated();
        Mockito.verify(cache, Mockito.times(1)).releaseActiveWork(ArgumentMatchers.any(String.class));
        Mockito.verifyNoMoreInteractions(account1);
        Mockito.verifyNoMoreInteractions(account2);
        Mockito.verifyNoMoreInteractions(accounts);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyNoMoreInteractions(cache);
    }

    @Test
    public void testCrawlChunk() {
        Mockito.when(cache.getLastNumber()).thenReturn(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT1));
        boolean accelerated = crawler.doPeriodicWork();
        assertThat(accelerated).isFalse();
        Mockito.verify(cache, Mockito.times(1)).claimActiveWork(ArgumentMatchers.any(String.class), ArgumentMatchers.anyLong());
        Mockito.verify(cache, Mockito.times(1)).getLastNumber();
        Mockito.verify(accounts, Mockito.times(0)).getAllFrom(ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(accounts, Mockito.times(1)).getAllFrom(ArgumentMatchers.eq(AccountDatabaseCrawlerTest.ACCOUNT1), ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(account2, Mockito.times(1)).getNumber();
        Mockito.verify(listener, Mockito.times(1)).onCrawlChunk(ArgumentMatchers.eq(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT1)), ArgumentMatchers.eq(Arrays.asList(account2)));
        Mockito.verify(cache, Mockito.times(1)).setLastNumber(ArgumentMatchers.eq(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT2)));
        Mockito.verify(cache, Mockito.times(1)).isAccelerated();
        Mockito.verify(cache, Mockito.times(1)).releaseActiveWork(ArgumentMatchers.any(String.class));
        Mockito.verifyZeroInteractions(account1);
        Mockito.verifyNoMoreInteractions(account2);
        Mockito.verifyNoMoreInteractions(accounts);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyNoMoreInteractions(cache);
    }

    @Test
    public void testCrawlChunkAccelerated() {
        Mockito.when(cache.isAccelerated()).thenReturn(true);
        Mockito.when(cache.getLastNumber()).thenReturn(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT1));
        boolean accelerated = crawler.doPeriodicWork();
        assertThat(accelerated).isTrue();
        Mockito.verify(cache, Mockito.times(1)).claimActiveWork(ArgumentMatchers.any(String.class), ArgumentMatchers.anyLong());
        Mockito.verify(cache, Mockito.times(1)).getLastNumber();
        Mockito.verify(accounts, Mockito.times(0)).getAllFrom(ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(accounts, Mockito.times(1)).getAllFrom(ArgumentMatchers.eq(AccountDatabaseCrawlerTest.ACCOUNT1), ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(account2, Mockito.times(1)).getNumber();
        Mockito.verify(listener, Mockito.times(1)).onCrawlChunk(ArgumentMatchers.eq(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT1)), ArgumentMatchers.eq(Arrays.asList(account2)));
        Mockito.verify(cache, Mockito.times(1)).setLastNumber(ArgumentMatchers.eq(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT2)));
        Mockito.verify(cache, Mockito.times(1)).isAccelerated();
        Mockito.verify(cache, Mockito.times(1)).releaseActiveWork(ArgumentMatchers.any(String.class));
        Mockito.verifyZeroInteractions(account1);
        Mockito.verifyNoMoreInteractions(account2);
        Mockito.verifyNoMoreInteractions(accounts);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyNoMoreInteractions(cache);
    }

    @Test
    public void testCrawlEnd() {
        Mockito.when(cache.getLastNumber()).thenReturn(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT2));
        boolean accelerated = crawler.doPeriodicWork();
        assertThat(accelerated).isFalse();
        Mockito.verify(cache, Mockito.times(1)).claimActiveWork(ArgumentMatchers.any(String.class), ArgumentMatchers.anyLong());
        Mockito.verify(cache, Mockito.times(1)).getLastNumber();
        Mockito.verify(accounts, Mockito.times(0)).getAllFrom(ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(accounts, Mockito.times(1)).getAllFrom(ArgumentMatchers.eq(AccountDatabaseCrawlerTest.ACCOUNT2), ArgumentMatchers.eq(AccountDatabaseCrawlerTest.CHUNK_SIZE));
        Mockito.verify(account1, Mockito.times(0)).getNumber();
        Mockito.verify(account2, Mockito.times(0)).getNumber();
        Mockito.verify(listener, Mockito.times(1)).onCrawlEnd(ArgumentMatchers.eq(Optional.of(AccountDatabaseCrawlerTest.ACCOUNT2)));
        Mockito.verify(cache, Mockito.times(1)).setLastNumber(ArgumentMatchers.eq(Optional.empty()));
        Mockito.verify(cache, Mockito.times(1)).clearAccelerate();
        Mockito.verify(cache, Mockito.times(1)).isAccelerated();
        Mockito.verify(cache, Mockito.times(1)).releaseActiveWork(ArgumentMatchers.any(String.class));
        Mockito.verifyZeroInteractions(account1);
        Mockito.verifyZeroInteractions(account2);
        Mockito.verifyNoMoreInteractions(accounts);
        Mockito.verifyNoMoreInteractions(listener);
        Mockito.verifyNoMoreInteractions(cache);
    }
}

