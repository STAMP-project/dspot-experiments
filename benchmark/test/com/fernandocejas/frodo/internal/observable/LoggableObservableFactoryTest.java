package com.fernandocejas.frodo.internal.observable;


import RxLogObservable.Scope.EVENTS;
import RxLogObservable.Scope.EVERYTHING;
import RxLogObservable.Scope.NOTHING;
import RxLogObservable.Scope.SCHEDULERS;
import RxLogObservable.Scope.STREAM;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class LoggableObservableFactoryTest {
    @Rule
    public final ObservableRule observableRule = new ObservableRule(this.getClass());

    private LoggableObservableFactory observableFactory;

    @Test
    public void shouldCreateLogEverythingObservable() {
        final RxLogObservable annotation = Mockito.mock(RxLogObservable.class);
        BDDMockito.given(annotation.value()).willReturn(EVERYTHING);
        final LoggableObservable loggableObservable = observableFactory.create(annotation);
        assertThat(loggableObservable).isInstanceOf(LogEverythingObservable.class);
    }

    @Test
    public void shouldCreateLogStreamObservable() {
        final RxLogObservable annotation = Mockito.mock(RxLogObservable.class);
        BDDMockito.given(annotation.value()).willReturn(STREAM);
        final LoggableObservable loggableObservable = observableFactory.create(annotation);
        assertThat(loggableObservable).isInstanceOf(LogStreamObservable.class);
    }

    @Test
    public void shouldCreateLogEventsObservable() {
        final RxLogObservable annotation = Mockito.mock(RxLogObservable.class);
        BDDMockito.given(annotation.value()).willReturn(EVENTS);
        final LoggableObservable loggableObservable = observableFactory.create(annotation);
        assertThat(loggableObservable).isInstanceOf(LogEventsObservable.class);
    }

    @Test
    public void shouldCreateLogSchedulersObservable() {
        final RxLogObservable annotation = Mockito.mock(RxLogObservable.class);
        BDDMockito.given(annotation.value()).willReturn(SCHEDULERS);
        final LoggableObservable loggableObservable = observableFactory.create(annotation);
        assertThat(loggableObservable).isInstanceOf(LogSchedulersObservable.class);
    }

    @Test
    public void shouldCreateLogNothingObservable() {
        final RxLogObservable annotation = Mockito.mock(RxLogObservable.class);
        BDDMockito.given(annotation.value()).willReturn(NOTHING);
        final LoggableObservable loggableObservable = observableFactory.create(annotation);
        assertThat(loggableObservable).isInstanceOf(LogNothingObservable.class);
    }
}

