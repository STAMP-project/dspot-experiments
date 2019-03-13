package com.fernandocejas.frodo.internal.observable;


import com.fernandocejas.frodo.core.optional.Optional;
import com.fernandocejas.frodo.internal.MessageManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class LogSchedulersObservableTest {
    @Rule
    public ObservableRule observableRule = new ObservableRule(this.getClass());

    private LogSchedulersObservable loggableObservable;

    private TestSubscriber subscriber;

    @Mock
    private MessageManager messageManager;

    @Test
    public void shouldLogOnlyObservableSchedulers() throws Throwable {
        loggableObservable.get(observableRule.stringType()).subscribe(subscriber);
        Mockito.verify(messageManager).printObservableThreadInfo(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verifyNoMoreInteractions(messageManager);
    }

    @Test
    public void shouldFillInObservableThreadInfo() throws Throwable {
        loggableObservable.get(observableRule.stringType()).subscribeOn(Schedulers.immediate()).observeOn(Schedulers.immediate()).subscribe(subscriber);
        final ObservableInfo observableInfo = loggableObservable.getInfo();
        final Optional<String> subscribeOnThread = observableInfo.getSubscribeOnThread();
        final Optional<String> observeOnThread = observableInfo.getObserveOnThread();
        final String currentThreadName = Thread.currentThread().getName();
        assertThat(subscribeOnThread.isPresent()).isTrue();
        assertThat(observeOnThread.isPresent()).isTrue();
        assertThat(subscribeOnThread.get()).isEqualTo(currentThreadName);
        assertThat(observeOnThread.get()).isEqualTo(currentThreadName);
    }
}

