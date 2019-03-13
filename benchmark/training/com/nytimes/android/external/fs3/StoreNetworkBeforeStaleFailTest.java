package com.nytimes.android.external.fs3;


import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.RecordProvider;
import com.nytimes.android.external.store3.base.RecordState;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Maybe;
import io.reactivex.Single;
import javax.annotation.Nonnull;
import okio.BufferedSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StoreNetworkBeforeStaleFailTest {
    static final Exception SORRY = new Exception("sorry");

    private static final BarCode barCode = new BarCode("key", "value");

    @Mock
    Fetcher<BufferedSource, BarCode> fetcher;

    Store<BufferedSource, BarCode> store;

    @Test
    public void networkBeforeStaleNoNetworkResponse() {
        Single<BufferedSource> exception = Single.error(StoreNetworkBeforeStaleFailTest.SORRY);
        Mockito.when(fetcher.fetch(StoreNetworkBeforeStaleFailTest.barCode)).thenReturn(exception);
        store.get(StoreNetworkBeforeStaleFailTest.barCode).test().assertError(StoreNetworkBeforeStaleFailTest.SORRY);
        Mockito.verify(fetcher, Mockito.times(1)).fetch(StoreNetworkBeforeStaleFailTest.barCode);
    }

    private static final class TestPersister implements Persister<BufferedSource, BarCode> , RecordProvider<BarCode> {
        @Nonnull
        @Override
        public RecordState getRecordState(@Nonnull
        BarCode barCode) {
            return RecordState.MISSING;
        }

        @Nonnull
        @Override
        public Maybe<BufferedSource> read(@Nonnull
        BarCode barCode) {
            return Maybe.error(StoreNetworkBeforeStaleFailTest.SORRY);
        }

        @Nonnull
        @Override
        public Single<Boolean> write(@Nonnull
        BarCode barCode, @Nonnull
        BufferedSource bufferedSource) {
            return Single.just(true);
        }
    }
}

