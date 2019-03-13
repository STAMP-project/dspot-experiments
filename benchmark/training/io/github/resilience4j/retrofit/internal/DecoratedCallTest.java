package io.github.resilience4j.retrofit.internal;


import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import retrofit2.Call;


@RunWith(JUnit4.class)
public class DecoratedCallTest {
    @Test
    public void passThroughCallsToDecoratedObject() throws IOException {
        final Call<String> call = Mockito.mock(DecoratedCallTest.StringCall.class);
        final Call<String> decorated = new DecoratedCall(call);
        decorated.cancel();
        Mockito.verify(call).cancel();
        decorated.enqueue(null);
        Mockito.verify(call).enqueue(ArgumentMatchers.any());
        decorated.isExecuted();
        Mockito.verify(call).isExecuted();
        decorated.isCanceled();
        Mockito.verify(call).isCanceled();
        decorated.clone();
        Mockito.verify(call).clone();
        decorated.request();
        Mockito.verify(call).request();
        decorated.execute();
        Mockito.verify(call).execute();
    }

    private interface StringCall extends Call<String> {}
}

