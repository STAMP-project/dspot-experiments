package com.fernandocejas.frodo.internal.observable;


import com.fernandocejas.frodo.internal.MessageManager;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.observers.TestSubscriber;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class FrodoObservableTest {
    @Rule
    public ObservableRule observableRule = new ObservableRule(this.getClass());

    private FrodoObservable frodoObservable;

    private TestSubscriber subscriber;

    @Mock
    private MessageManager messageManager;

    @Mock
    private LoggableObservableFactory observableFactory;

    @Test
    public void shouldPrintObservableInfo() throws Throwable {
        frodoObservable.getObservable();
        Mockito.verify(messageManager).printObservableInfo(ArgumentMatchers.any(ObservableInfo.class));
    }

    @Test
    public void shouldBuildObservable() throws Throwable {
        frodoObservable.getObservable().subscribe(subscriber);
        subscriber.assertReceivedOnNext(Collections.singletonList(observableRule.OBSERVABLE_STREAM_VALUE));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
    }

    @Test
    public void shouldLogObservableInformation() throws Throwable {
        frodoObservable.getObservable().subscribe(subscriber);
        Mockito.verify(messageManager).printObservableInfo(ArgumentMatchers.any(ObservableInfo.class));
    }
}

