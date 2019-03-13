package com.nytimes.android.external.store3;


import com.nytimes.android.external.store.util.Result;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.Single;
import org.junit.Test;


public class SequentialTest {
    int networkCalls = 0;

    private Store<Integer, BarCode> store;

    @Test
    public void sequentially() {
        BarCode b = new BarCode("one", "two");
        store.get(b).test().awaitTerminalEvent();
        store.get(b).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(1);
    }

    @Test
    public void sequentiallyWithResult() {
        BarCode b = new BarCode("one", "two");
        store.getWithResult(b).test().awaitTerminalEvent();
        store.getWithResult(b).test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(1);
    }

    @Test
    public void parallel() {
        BarCode b = new BarCode("one", "two");
        Single<Integer> first = store.get(b);
        Single<Integer> second = store.get(b);
        first.test().awaitTerminalEvent();
        second.test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(1);
    }

    @Test
    public void parallelWithResult() {
        BarCode b = new BarCode("one", "two");
        Single<Result<Integer>> first = store.getWithResult(b);
        Single<Result<Integer>> second = store.getWithResult(b);
        first.test().awaitTerminalEvent();
        second.test().awaitTerminalEvent();
        assertThat(networkCalls).isEqualTo(1);
    }
}

