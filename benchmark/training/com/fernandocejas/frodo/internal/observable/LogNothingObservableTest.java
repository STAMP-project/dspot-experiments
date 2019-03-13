package com.fernandocejas.frodo.internal.observable;


import com.fernandocejas.frodo.internal.MessageManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.observers.TestSubscriber;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class LogNothingObservableTest {
    @Rule
    public ObservableRule observableRule = new ObservableRule(this.getClass());

    private LogNothingObservable loggableObservable;

    private TestSubscriber subscriber;

    @Mock
    private MessageManager messageManager;

    @Test
    public void shouldNotLogAnything() throws Throwable {
        loggableObservable.get(observableRule.stringType()).subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        Mockito.verifyZeroInteractions(messageManager);
    }
}

