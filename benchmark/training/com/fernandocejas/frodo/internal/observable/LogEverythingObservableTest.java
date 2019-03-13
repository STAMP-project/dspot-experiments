package com.fernandocejas.frodo.internal.observable;


import com.fernandocejas.frodo.core.optional.Optional;
import com.fernandocejas.frodo.internal.MessageManager;
import com.fernandocejas.frodo.joinpoint.FrodoProceedingJoinPoint;
import java.util.concurrent.TimeUnit;
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
public class LogEverythingObservableTest {
    @Rule
    public ObservableRule observableRule = new ObservableRule(this.getClass());

    private LogEverythingObservable loggableObservable;

    private TestSubscriber subscriber;

    @Mock
    private MessageManager messageManager;

    @Test
    public void shouldLogEverythingObservable() throws Throwable {
        loggableObservable.get(observableRule.stringType()).subscribe(subscriber);
        Mockito.verify(messageManager).printObservableOnSubscribe(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableOnNextWithValue(ArgumentMatchers.any(ObservableInfo.class), ArgumentMatchers.anyString());
        Mockito.verify(messageManager).printObservableOnCompleted(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableOnTerminate(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableItemTimeInfo(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableOnUnsubscribe(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verify(messageManager).printObservableThreadInfo(ArgumentMatchers.any(ObservableInfo.class));
    }

    @Test
    public void shouldFillInObservableBasicInfo() throws Throwable {
        loggableObservable.get(observableRule.stringType()).subscribe(subscriber);
        final ObservableInfo observableInfo = loggableObservable.getInfo();
        final FrodoProceedingJoinPoint frodoProceedingJoinPoint = observableRule.joinPoint();
        assertThat(observableInfo.getClassSimpleName()).isEqualTo(frodoProceedingJoinPoint.getClassSimpleName());
        assertThat(observableInfo.getJoinPoint()).isEqualTo(frodoProceedingJoinPoint);
        assertThat(observableInfo.getMethodName()).isEqualTo(frodoProceedingJoinPoint.getMethodName());
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

    @Test
    public void shouldFillInObservableItemsInfo() throws Throwable {
        loggableObservable.get(observableRule.stringType()).delay(2, TimeUnit.SECONDS).subscribe(subscriber);
        final ObservableInfo observableInfo = loggableObservable.getInfo();
        final Optional<Integer> totalEmittedItems = observableInfo.getTotalEmittedItems();
        final Optional<Long> totalExecutionTime = observableInfo.getTotalExecutionTime();
        assertThat(totalEmittedItems.isPresent()).isTrue();
        assertThat(totalExecutionTime.isPresent()).isTrue();
        assertThat(totalEmittedItems.get()).isEqualTo(1);
    }
}

