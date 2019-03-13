package com.fernandocejas.frodo.internal.observable;


import com.fernandocejas.frodo.core.optional.Optional;
import com.fernandocejas.frodo.internal.MessageManager;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class LogStreamObservableTest {
    @Rule
    public ObservableRule observableRule = new ObservableRule(this.getClass());

    private LogStreamObservable loggableObservable;

    private TestSubscriber subscriber;

    @Mock
    private MessageManager messageManager;

    @Test
    public void shouldLogOnlyStreamData() throws Throwable {
        loggableObservable.get(observableRule.stringType()).subscribe(subscriber);
        Mockito.verify(messageManager).printObservableOnNextWithValue(ArgumentMatchers.any(ObservableInfo.class), ArgumentMatchers.anyString());
        Mockito.verify(messageManager).printObservableItemTimeInfo(ArgumentMatchers.any(ObservableInfo.class));
        Mockito.verifyNoMoreInteractions(messageManager);
    }

    @Test
    public void shouldFillInObservableItemsInfo() throws Throwable {
        final TestScheduler testScheduler = new TestScheduler();
        loggableObservable.get(observableRule.stringType()).delay(2, TimeUnit.SECONDS).subscribeOn(testScheduler).subscribe(subscriber);
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
        final ObservableInfo observableInfo = loggableObservable.getInfo();
        final Optional<Integer> totalEmittedItems = observableInfo.getTotalEmittedItems();
        final Optional<Long> totalExecutionTime = observableInfo.getTotalExecutionTime();
        assertThat(totalEmittedItems.isPresent()).isTrue();
        assertThat(totalEmittedItems.get()).isEqualTo(1);
    }
}

