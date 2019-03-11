package com.nytimes.android.external.store3;


import com.nytimes.android.external.store3.base.impl.Store;
import io.reactivex.observers.TestObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KeyParserTest {
    public static final String NETWORK = "Network";

    public static final int KEY = 5;

    private Store<String, Integer> store;

    @Test
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void testStoreWithKeyParserFuncNoPersister() throws Exception {
        TestObserver<String> testObservable = store.get(KeyParserTest.KEY).test().await();
        testObservable.assertNoErrors().assertValues(((KeyParserTest.NETWORK) + (KeyParserTest.KEY))).awaitTerminalEvent();
    }
}

