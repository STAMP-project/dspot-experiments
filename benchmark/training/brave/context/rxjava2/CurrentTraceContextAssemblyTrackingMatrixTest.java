package brave.context.rxjava2;


import CompletableEmpty.INSTANCE;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.fuseable.ScalarCallable;
import io.reactivex.internal.operators.completable.CompletableFromCallable;
import io.reactivex.internal.operators.flowable.FlowableFilter;
import io.reactivex.internal.operators.flowable.FlowableFromCallable;
import io.reactivex.internal.operators.flowable.FlowablePublish;
import io.reactivex.internal.operators.flowable.FlowableRange;
import io.reactivex.internal.operators.maybe.MaybeFilter;
import io.reactivex.internal.operators.maybe.MaybeFilterSingle;
import io.reactivex.internal.operators.maybe.MaybeFromCallable;
import io.reactivex.internal.operators.maybe.MaybeJust;
import io.reactivex.internal.operators.observable.ObservableFilter;
import io.reactivex.internal.operators.observable.ObservableFromCallable;
import io.reactivex.internal.operators.observable.ObservablePublish;
import io.reactivex.internal.operators.observable.ObservableRange;
import io.reactivex.internal.operators.parallel.ParallelFilter;
import io.reactivex.internal.operators.parallel.ParallelFromPublisher;
import io.reactivex.internal.operators.single.SingleFromCallable;
import io.reactivex.internal.operators.single.SingleJust;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.parallel.ParallelFlowable;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.reactivestreams.Subscriber;


/**
 * These test that features that are similar across reactive types act the same way. These are
 * largely cut/paste/find/replace with the exception of api differences between the types. When
 * behavior needs to be tested across all things, do it here. Keep interesting things in {@link CurrentTraceContextAssemblyTrackingTest} so they don't get lost!
 */
public class CurrentTraceContextAssemblyTrackingMatrixTest {
    CurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build();

    CurrentTraceContext throwingCurrentTraceContext = new CurrentTraceContext() {
        @Override
        public TraceContext get() {
            return subscribeContext;
        }

        @Override
        public Scope newScope(TraceContext currentSpan) {
            throw new AssertionError();
        }
    };

    TraceContext assemblyContext = TraceContext.newBuilder().traceId(1L).spanId(1L).build();

    TraceContext subscribeContext = assemblyContext.toBuilder().parentId(1L).spanId(2L).build();

    Predicate<Integer> lessThanThreeInAssemblyContext = ( i) -> {
        assertInAssemblyContext();
        return i < 3;
    };

    Predicate<Integer> lessThanThreeInSubscribeContext = ( i) -> {
        assertInSubscribeContext();
        return i < 3;
    };

    @Test
    public void completable_assembleInScope_subscribeNoScope() {
        Completable source;
        Completable errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Completable.complete().doOnComplete(this::assertInAssemblyContext);
            errorSource = Completable.error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult();
    }

    @Test
    public void completable_assembleInScope_subscribeInScope() {
        Completable source;
        Completable errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Completable.complete().doOnComplete(this::assertInAssemblyContext);
            errorSource = Completable.error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult();
    }

    @Test
    public void completable_assembleNoScope_subscribeInScope() {
        Completable source = Completable.complete().doOnComplete(this::assertInSubscribeContext);
        Completable errorSource = Completable.error(new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult();
    }

    @Test
    public void completable_unwrappedWhenNotInScope() {
        assertThat(Completable.complete()).isEqualTo(INSTANCE);
    }

    @Test
    public void flowable_assembleInScope_subscribeNoScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1, 2, 3);
    }

    @Test
    public void flowable_assembleInScope_subscribeInScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1, 2, 3);
    }

    @Test
    public void flowable_assembleNoScope_subscribeInScope() {
        Flowable<Integer> source = Flowable.range(1, 3).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Flowable<Integer> errorSource = Flowable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1, 2, 3);
    }

    @Test
    public void flowable_unwrappedWhenNotInScope() {
        assertThat(Flowable.range(1, 3)).isInstanceOf(FlowableRange.class);
    }

    @Test
    public void flowable_conditional_assembleInScope_subscribeNoScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1, 2);
    }

    @Test
    public void flowable_conditional_assembleInScope_subscribeInScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1, 2);
    }

    @Test
    public void flowable_conditional_assembleNoScope_subscribeInScope() {
        Flowable<Integer> source = Flowable.range(1, 3).filter(lessThanThreeInSubscribeContext).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Flowable<Integer> errorSource = Flowable.<Integer>error(new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1, 2);
    }

    @Test
    public void flowable_conditional_unwrappedWhenNotInScope() {
        assertThat(Flowable.range(1, 3).filter(( i) -> i < 3)).isInstanceOf(FlowableFilter.class);
    }

    @Test
    public void observable_assembleInScope_subscribeNoScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Observable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Observable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source, errorSource).assertResult(1, 2, 3);
    }

    @Test
    public void observable_assembleInScope_subscribeInScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Observable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Observable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2, 3);
    }

    @Test
    public void observable_assembleNoScope_subscribeInScope() {
        Observable<Integer> source = Observable.range(1, 3).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Observable<Integer> errorSource = Observable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2, 3);
    }

    @Test
    public void observable_unwrappedWhenNotInScope() {
        assertThat(Observable.range(1, 3)).isInstanceOf(ObservableRange.class);
    }

    @Test
    public void observable_conditional_assembleInScope_subscribeNoScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Observable.range(1, 3).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Observable.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source, errorSource).assertResult(1, 2);
    }

    @Test
    public void observable_conditional_assembleInScope_subscribeInScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Observable.range(1, 3).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Observable.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2);
    }

    @Test
    public void observable_conditional_assembleNoScope_subscribeInScope() {
        Observable<Integer> source = Observable.range(1, 3).filter(lessThanThreeInSubscribeContext).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Observable<Integer> errorSource = Observable.<Integer>error(new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2);
    }

    @Test
    public void observable_conditional_unwrappedWhenNotInScope() {
        assertThat(Observable.range(1, 3).filter(( i) -> i < 3)).isInstanceOf(ObservableFilter.class);
    }

    @Test
    public void single_assembleInScope_subscribeNoScope() {
        Single<Integer> source;
        Single<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Single.just(1).doOnSuccess(( e) -> assertInAssemblyContext());
            errorSource = Single.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext());
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void single_assembleInScope_subscribeInScope() {
        Single<Integer> source;
        Single<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Single.just(1).doOnSuccess(( e) -> assertInAssemblyContext());
            errorSource = Single.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext());
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void single_assembleNoScope_subscribeInScope() {
        Single<Integer> source = Single.just(1).doOnSuccess(( e) -> assertInSubscribeContext());
        Single<Integer> errorSource = Single.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext());
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void single_unwrappedWhenNotInScope() {
        assertThat(Single.just(1)).isInstanceOf(SingleJust.class);
    }

    @Test
    public void single_conditional_assembleInScope_subscribeNoScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Single.just(1).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInSubscribeContext);
            errorSource = Single.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext());
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void single_conditional_assembleInScope_subscribeInScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Single.just(1).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInSubscribeContext);
            errorSource = Single.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext());
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void single_conditional_assembleNoScope_subscribeInScope() {
        Maybe<Integer> source = Single.just(1).filter(lessThanThreeInSubscribeContext).doOnSuccess(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Maybe<Integer> errorSource = Single.<Integer>error(new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void single_conditionalunwrappedWhenNotInScope() {
        assertThat(Single.just(1).filter(( i) -> i < 3)).isInstanceOf(MaybeFilterSingle.class);
    }

    @Test
    public void maybe_assembleInScope_subscribeNoScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Maybe.just(1).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Maybe.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void maybe_assembleInScope_subscribeInScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Maybe.just(1).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Maybe.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void maybe_assembleNoScope_subscribeInScope() {
        Maybe<Integer> source = Maybe.just(1).doOnSuccess(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Maybe<Integer> errorSource = Maybe.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void maybe_unwrappedWhenNotInScope() {
        assertThat(Maybe.just(1)).isInstanceOf(MaybeJust.class);
    }

    @Test
    public void maybe_conditional_assembleInScope_subscribeNoScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Maybe.just(1).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInSubscribeContext);
            errorSource = Maybe.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void maybe_conditional_assembleInScope_subscribeInScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Maybe.just(1).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInSubscribeContext);
            errorSource = Maybe.<Integer>error(new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void maybe_conditional_assembleNoScope_subscribeInScope() {
        Maybe<Integer> source = Maybe.just(1).filter(lessThanThreeInSubscribeContext).doOnSuccess(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Maybe<Integer> errorSource = Maybe.<Integer>error(new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void maybe_conditionalunwrappedWhenNotInScope() {
        assertThat(Maybe.just(1).filter(( i) -> i < 3)).isInstanceOf(MaybeFilter.class);
    }

    @Test
    public void parallelFlowable_assembleInScope_subscribeNoScope() {
        ParallelFlowable<Integer> source;
        ParallelFlowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).parallel().doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>concat(Flowable.error(new IllegalStateException()), Flowable.error(new IllegalStateException())).parallel().doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source, errorSource).assertResult(1, 2, 3);
    }

    @Test
    public void parallelFlowable_assembleInScope_subscribeInScope() {
        ParallelFlowable<Integer> source;
        ParallelFlowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).parallel().doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>concat(Flowable.error(new IllegalStateException()), Flowable.error(new IllegalStateException())).parallel().doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2, 3);
    }

    @Test
    public void parallelFlowable_assembleNoScope_subscribeInScope() {
        ParallelFlowable<Integer> source = Flowable.range(1, 3).parallel().doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        ParallelFlowable<Integer> errorSource = Flowable.<Integer>concat(Flowable.error(new IllegalStateException()), Flowable.error(new IllegalStateException())).parallel().doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2, 3);
    }

    @Test
    public void parallelFlowable_unwrappedWhenNotInScope() {
        assertThat(Flowable.range(1, 3).parallel()).isInstanceOf(ParallelFromPublisher.class);
    }

    @Test
    public void parallelFlowable_conditional_assembleInScope_subscribeNoScope() {
        ParallelFlowable<Integer> source;
        ParallelFlowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).parallel().filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>concat(Flowable.error(new IllegalStateException()), Flowable.error(new IllegalStateException())).parallel().filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source, errorSource).assertResult(1, 2);
    }

    @Test
    public void parallelFlowable_conditional_assembleInScope_subscribeInScope() {
        ParallelFlowable<Integer> source;
        ParallelFlowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).parallel().filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = Flowable.<Integer>concat(Flowable.error(new IllegalStateException()), Flowable.error(new IllegalStateException())).parallel().filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2);
    }

    @Test
    public void parallelFlowable_conditional_assembleNoScope_subscribeInScope() {
        ParallelFlowable<Integer> source = Flowable.range(1, 3).parallel().filter(lessThanThreeInSubscribeContext).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        ParallelFlowable<Integer> errorSource = Flowable.<Integer>concat(Flowable.error(new IllegalStateException()), Flowable.error(new IllegalStateException())).parallel().filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source, errorSource).assertResult(1, 2);
    }

    @Test
    public void parallelFlowable_conditional_unwrappedWhenNotInScope() {
        assertThat(Flowable.range(1, 3).parallel().filter(( i) -> i < 3)).isInstanceOf(ParallelFilter.class);
    }

    @Test
    public void connectableFlowable_assembleInScope_subscribeNoScope() {
        ConnectableFlowable<Integer> source;
        ConnectableFlowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
            errorSource = Flowable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
        }
        subscribeInNoContext(source.autoConnect().toObservable(), errorSource.autoConnect().toObservable()).assertResult(1, 2, 3);
    }

    @Test
    public void connectableFlowable_assembleInScope_subscribeInScope() {
        ConnectableFlowable<Integer> source;
        ConnectableFlowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Flowable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
            errorSource = Flowable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
        }
        subscribeInDifferentContext(source.autoConnect().toObservable(), errorSource.autoConnect().toObservable()).assertResult(1, 2, 3);
    }

    @Test
    public void connectableFlowable_assembleNoScope_subscribeInScope() {
        ConnectableFlowable<Integer> source = Flowable.range(1, 3).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext).publish();
        ConnectableFlowable<Integer> errorSource = Flowable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext).publish();
        subscribeInDifferentContext(source.autoConnect().toObservable(), errorSource.autoConnect().toObservable()).assertResult(1, 2, 3);
    }

    @Test
    public void connectableFlowable_unwrappedWhenNotInScope() {
        assertThat(Flowable.range(1, 3).publish()).isInstanceOf(FlowablePublish.class);
    }

    // NOTE: we aren't doing separate tests for conditional ConnectableFlowable as there are no
    // operations on the type that are conditional (ex filter)
    @Test
    public void connectableObservable_assembleInScope_subscribeNoScope() {
        ConnectableObservable<Integer> source;
        ConnectableObservable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Observable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
            errorSource = Observable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
        }
        subscribeInNoContext(source.autoConnect(), errorSource.autoConnect()).assertResult(1, 2, 3);
    }

    @Test
    public void connectableObservable_assembleInScope_subscribeInScope() {
        ConnectableObservable<Integer> source;
        ConnectableObservable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = Observable.range(1, 3).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
            errorSource = Observable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext).publish();
        }
        subscribeInDifferentContext(source.autoConnect(), errorSource.autoConnect()).assertResult(1, 2, 3);
    }

    @Test
    public void connectableObservable_assembleNoScope_subscribeInScope() {
        ConnectableObservable<Integer> source = Observable.range(1, 3).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext).publish();
        ConnectableObservable<Integer> errorSource = Observable.<Integer>error(new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext).publish();
        subscribeInDifferentContext(source.autoConnect(), errorSource.autoConnect()).assertResult(1, 2, 3);
    }

    @Test
    public void connectableObservable_unwrappedWhenNotInScope() {
        assertThat(Observable.range(1, 3).publish()).isInstanceOf(ObservablePublish.class);
    }

    // NOTE: we aren't doing separate tests for conditional ConnectableObservable as there are no
    // operations on the type that are conditional (ex filter)
    @Test
    public void callable_completable_assembleInScope_subscribeNoScope() {
        Completable source;
        Completable errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableCompletable(null).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableCompletable(null, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult();
    }

    @Test
    public void callable_completable_assembleInScope_subscribeInScope() {
        Completable source;
        Completable errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableCompletable(subscribeContext).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableCompletable(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult();
    }

    @Test
    public void callable_completable_assembleNoScope_subscribeInScope() {
        Completable source = callableCompletable(subscribeContext).doOnComplete(this::assertInSubscribeContext);
        Completable errorSource = callableCompletable(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult();
    }

    @Test
    public void callable_completable_unwrappedWhenNotInScope() {
        assertThat(callableCompletable(null)).isInstanceOf(CurrentTraceContextAssemblyTrackingMatrixTest.CallableCompletable.class);
    }

    // Callable should inherit the subscribing context. Ensure we don't accidentally instrument it.
    @Test
    public void callable_completable_uninstrumented() throws Exception {
        currentTraceContext = throwingCurrentTraceContext;
        setup();
        // currentTraceContext.newScope would throw and break the test if call() was instrumented
        ((Callable) (callableCompletable(subscribeContext))).call();
    }

    @Test
    public void callable_flowable_assembleInScope_subscribeNoScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableFlowable(null).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableFlowable(null, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_flowable_assembleInScope_subscribeInScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableFlowable(subscribeContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableFlowable(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_flowable_assembleNoScope_subscribeInScope() {
        Flowable<Integer> source = callableFlowable(subscribeContext).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Flowable<Integer> errorSource = callableFlowable(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_flowable_unwrappedWhenNotInScope() {
        assertThat(callableFlowable(null)).isInstanceOf(CurrentTraceContextAssemblyTrackingMatrixTest.CallableFlowable.class);
    }

    @Test
    public void callable_flowable_conditional_assembleInScope_subscribeNoScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableFlowable(null).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableFlowable(null, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_flowable_conditional_assembleInScope_subscribeInScope() {
        Flowable<Integer> source;
        Flowable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableFlowable(subscribeContext).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableFlowable(subscribeContext, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_flowable_conditional_assembleNoScope_subscribeInScope() {
        Flowable<Integer> source = callableFlowable(subscribeContext).filter(lessThanThreeInSubscribeContext).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Flowable<Integer> errorSource = callableFlowable(subscribeContext, new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_flowable_conditional_unwrappedWhenNotInScope() {
        assertThat(callableFlowable(null).filter(( i) -> i < 3)).isInstanceOf(FlowableFilter.class);
    }

    // Callable should inherit the subscribing context. Ensure we don't accidentally instrument it.
    @Test
    public void callable_flowable_uninstrumented() throws Exception {
        currentTraceContext = throwingCurrentTraceContext;
        setup();
        // currentTraceContext.newScope would throw and break the test if call() was instrumented
        ((Callable) (callableFlowable(subscribeContext))).call();
    }

    @Test
    public void callable_observable_assembleInScope_subscribeNoScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableObservable(null).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableObservable(null, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source, errorSource).assertResult(1);
    }

    @Test
    public void callable_observable_assembleInScope_subscribeInScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableObservable(subscribeContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableObservable(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source, errorSource).assertResult(1);
    }

    @Test
    public void callable_observable_assembleNoScope_subscribeInScope() {
        Observable<Integer> source = callableObservable(subscribeContext).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Observable<Integer> errorSource = callableObservable(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source, errorSource).assertResult(1);
    }

    @Test
    public void callable_observable_unwrappedWhenNotInScope() {
        assertThat(callableObservable(null)).isInstanceOf(CurrentTraceContextAssemblyTrackingMatrixTest.CallableObservable.class);
    }

    @Test
    public void callable_observable_conditional_assembleInScope_subscribeNoScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableObservable(null).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableObservable(null, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source, errorSource).assertResult(1);
    }

    @Test
    public void callable_observable_conditional_assembleInScope_subscribeInScope() {
        Observable<Integer> source;
        Observable<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableObservable(subscribeContext).filter(lessThanThreeInAssemblyContext).doOnNext(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableObservable(subscribeContext, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source, errorSource).assertResult(1);
    }

    @Test
    public void callable_observable_conditional_assembleNoScope_subscribeInScope() {
        Observable<Integer> source = callableObservable(subscribeContext).filter(lessThanThreeInSubscribeContext).doOnNext(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Observable<Integer> errorSource = callableObservable(subscribeContext, new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        subscribeInDifferentContext(source, errorSource).assertResult(1);
    }

    @Test
    public void callable_observable_conditional_unwrappedWhenNotInScope() {
        assertThat(callableObservable(null).filter(( i) -> i < 3)).isInstanceOf(ObservableFilter.class);
    }

    // Callable should inherit the subscribing context. Ensure we don't accidentally instrument it.
    @Test
    public void callable_observable_uninstrumented() throws Exception {
        currentTraceContext = throwingCurrentTraceContext;
        setup();
        // currentTraceContext.newScope would throw and break the test if call() was instrumented
        ((Callable) (callableObservable(subscribeContext))).call();
    }

    @Test
    public void callable_single_assembleInScope_subscribeNoScope() {
        Single<Integer> source;
        Single<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableSingle(null).doOnSuccess(( e) -> assertInAssemblyContext());
            errorSource = callableSingle(null, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext());
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_single_assembleInScope_subscribeInScope() {
        Single<Integer> source;
        Single<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableSingle(subscribeContext).doOnSuccess(( e) -> assertInAssemblyContext());
            errorSource = callableSingle(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext());
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_single_assembleNoScope_subscribeInScope() {
        Single<Integer> source = callableSingle(subscribeContext).doOnSuccess(( e) -> assertInSubscribeContext());
        Single<Integer> errorSource = callableSingle(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext());
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_single_unwrappedWhenNotInScope() {
        assertThat(callableSingle(null)).isInstanceOf(CurrentTraceContextAssemblyTrackingMatrixTest.CallableSingle.class);
    }

    @Test
    public void callable_single_conditional_assembleInScope_subscribeNoScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableSingle(null).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableSingle(null, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_single_conditional_assembleInScope_subscribeInScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableSingle(subscribeContext).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableSingle(subscribeContext, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_single_conditional_assembleNoScope_subscribeInScope() {
        Maybe<Integer> source = callableSingle(subscribeContext).filter(lessThanThreeInSubscribeContext).doOnSuccess(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Maybe<Integer> errorSource = callableSingle(subscribeContext, new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext());
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_single_conditional_unwrappedWhenNotInScope() {
        assertThat(callableSingle(null).filter(( i) -> i < 3)).isInstanceOf(MaybeFilterSingle.class);
    }

    // Callable should inherit the subscribing context. Ensure we don't accidentally instrument it.
    @Test
    public void callable_single_uninstrumented() throws Exception {
        currentTraceContext = throwingCurrentTraceContext;
        setup();
        // currentTraceContext.newScope would throw and break the test if call() was instrumented
        ((Callable) (callableSingle(subscribeContext))).call();
    }

    @Test
    public void callable_maybe_assembleInScope_subscribeNoScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableMaybe(null).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableMaybe(null, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_maybe_assembleInScope_subscribeInScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableMaybe(subscribeContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableMaybe(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_maybe_assembleNoScope_subscribeInScope() {
        Maybe<Integer> source = callableMaybe(subscribeContext).doOnSuccess(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Maybe<Integer> errorSource = callableMaybe(subscribeContext, new IllegalStateException()).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_maybe_unwrappedWhenNotInScope() {
        assertThat(callableMaybe(null)).isInstanceOf(CurrentTraceContextAssemblyTrackingMatrixTest.CallableMaybe.class);
    }

    @Test
    public void callable_maybe_conditional_assembleInScope_subscribeNoScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableMaybe(null).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableMaybe(null, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInNoContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_maybe_conditional_assembleInScope_subscribeInScope() {
        Maybe<Integer> source;
        Maybe<Integer> errorSource;
        try (Scope scope = currentTraceContext.newScope(assemblyContext)) {
            source = callableMaybe(subscribeContext).filter(lessThanThreeInAssemblyContext).doOnSuccess(( e) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
            errorSource = callableMaybe(subscribeContext, new IllegalStateException()).filter(lessThanThreeInAssemblyContext).doOnError(( t) -> assertInAssemblyContext()).doOnComplete(this::assertInAssemblyContext);
        }
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_maybe_conditional_assembleNoScope_subscribeInScope() {
        Maybe<Integer> source = callableMaybe(subscribeContext).filter(lessThanThreeInSubscribeContext).doOnSuccess(( e) -> assertInSubscribeContext()).doOnComplete(this::assertInSubscribeContext);
        Maybe<Integer> errorSource = callableMaybe(subscribeContext, new IllegalStateException()).filter(lessThanThreeInSubscribeContext).doOnError(( t) -> assertInSubscribeContext()).doOnComplete(this::assertInAssemblyContext);
        subscribeInDifferentContext(source.toObservable(), errorSource.toObservable()).assertResult(1);
    }

    @Test
    public void callable_maybe_conditional_unwrappedWhenNotInScope() {
        assertThat(callableMaybe(null).filter(( i) -> i < 3)).isInstanceOf(MaybeFilter.class);
    }

    // Callable should inherit the subscribing context. Ensure we don't accidentally instrument it.
    @Test
    public void callable_maybe_uninstrumented() throws Exception {
        currentTraceContext = throwingCurrentTraceContext;
        setup();
        // currentTraceContext.newScope would throw and break the test if call() was instrumented
        ((Callable) (callableMaybe(subscribeContext))).call();
    }

    abstract static class CallableCompletable extends Completable implements Callable<Integer> {
        final CompletableFromCallable delegate = new CompletableFromCallable(this);

        @Override
        protected void subscribeActual(CompletableObserver o) {
            delegate.subscribe(o);
        }
    }

    abstract class CallableFlowable extends Flowable<Integer> implements Callable<Integer> {
        final FlowableFromCallable<Integer> delegate = new FlowableFromCallable(this);

        @Override
        protected void subscribeActual(Subscriber<? super Integer> s) {
            delegate.subscribe(s);
        }
    }

    abstract class CallableObservable extends Observable<Integer> implements Callable<Integer> {
        final ObservableFromCallable<Integer> delegate = new ObservableFromCallable(this);

        @Override
        protected void subscribeActual(Observer<? super Integer> o) {
            delegate.subscribe(o);
        }
    }

    abstract class CallableSingle extends Single<Integer> implements Callable<Integer> {
        final SingleFromCallable<Integer> delegate = new SingleFromCallable(this);

        @Override
        protected void subscribeActual(SingleObserver<? super Integer> o) {
            delegate.subscribe(o);
        }
    }

    abstract class CallableMaybe extends Maybe<Integer> implements Callable<Integer> {
        final MaybeFromCallable<Integer> delegate = new MaybeFromCallable(this);

        @Override
        protected void subscribeActual(MaybeObserver<? super Integer> o) {
            delegate.subscribe(o);
        }
    }

    abstract static class ScalarCallableCompletable extends Completable implements ScalarCallable<Integer> {
        final CompletableFromCallable delegate = new CompletableFromCallable(this);

        @Override
        protected void subscribeActual(CompletableObserver o) {
            delegate.subscribe(o);
        }
    }

    abstract class ScalarCallableFlowable extends Flowable<Integer> implements ScalarCallable<Integer> {
        final FlowableFromCallable<Integer> delegate = new FlowableFromCallable(this);

        @Override
        protected void subscribeActual(Subscriber<? super Integer> s) {
            delegate.subscribe(s);
        }
    }

    abstract class ScalarCallableObservable extends Observable<Integer> implements ScalarCallable<Integer> {
        final ObservableFromCallable<Integer> delegate = new ObservableFromCallable(this);

        @Override
        protected void subscribeActual(Observer<? super Integer> o) {
            delegate.subscribe(o);
        }
    }

    abstract class ScalarCallableSingle extends Single<Integer> implements ScalarCallable<Integer> {
        final SingleFromCallable<Integer> delegate = new SingleFromCallable(this);

        @Override
        protected void subscribeActual(SingleObserver<? super Integer> o) {
            delegate.subscribe(o);
        }
    }

    abstract class ScalarCallableMaybe extends Maybe<Integer> implements ScalarCallable<Integer> {
        final MaybeFromCallable<Integer> delegate = new MaybeFromCallable(this);

        @Override
        protected void subscribeActual(MaybeObserver<? super Integer> o) {
            delegate.subscribe(o);
        }
    }
}

