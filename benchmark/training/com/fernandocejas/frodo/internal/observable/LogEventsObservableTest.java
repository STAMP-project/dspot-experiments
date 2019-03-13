package com.fernandocejas.frodo.internal.observable;


import com.fernandocejas.frodo.internal.MessageManager;
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
public class LogEventsObservableTest {
    @Rule
    public ObservableRule observableRule = new ObservableRule(this.getClass());

    private LogEventsObservable loggableObservable;

    private TestSubscriber subscriber;

    @Mock
    private MessageManager messageManager;

    @Test
    public void shouldLogOnlyObservableEvents() throws Throwable {
        loggableObservable.get(observableRule.stringType()).subscribe(subscriber);
        Mockito.verify(messageManager).printObservableOnSubscribe(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableOnNext(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableOnCompleted(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableOnTerminate(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableOnUnsubscribe(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verifyNoMoreInteractions(messageManager);
    }
}

