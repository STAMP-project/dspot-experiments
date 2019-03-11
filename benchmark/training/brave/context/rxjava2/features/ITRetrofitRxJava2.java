package brave.context.rxjava2.features;


import brave.context.rxjava2.CurrentTraceContextAssemblyTracking;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import brave.test.http.ITHttp;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import java.util.function.BiConsumer;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import retrofit2.http.GET;


/**
 * This tests that propagation isn't lost when passing through an untraced system.
 */
public class ITRetrofitRxJava2 extends ITHttp {
    TraceContext context1 = TraceContext.newBuilder().traceId(1L).spanId(1L).build();

    CurrentTraceContextAssemblyTracking contextTracking = CurrentTraceContextAssemblyTracking.create(currentTraceContext);

    @Rule
    public MockWebServer server = new MockWebServer();

    ITRetrofitRxJava2.CurrentTraceContextObserver currentTraceContextObserver = new ITRetrofitRxJava2.CurrentTraceContextObserver();

    interface Service {
        @GET("/")
        Completable completable();

        @GET("/")
        Maybe<ResponseBody> maybe();

        @GET("/")
        Observable<ResponseBody> observable();

        @GET("/")
        Single<ResponseBody> single();

        @GET("/")
        Flowable<ResponseBody> flowable();
    }

    @Test
    public void createAsync_completable_success() {
        rxjava_createAsync_success(( service, observer) -> {
            try (CurrentTraceContext.Scope scope = currentTraceContext.newScope(context1)) {
                service.completable().subscribe(observer);
            }
        });
    }

    @Test
    public void createAsync_maybe_success() {
        rxjava_createAsync_success(( service, observer) -> {
            try (CurrentTraceContext.Scope scope = currentTraceContext.newScope(context1)) {
                service.maybe().subscribe(observer);
            }
        });
    }

    @Test
    public void createAsync_observable_success() {
        rxjava_createAsync_success(( service, observer) -> {
            try (CurrentTraceContext.Scope scope = currentTraceContext.newScope(context1)) {
                service.observable().subscribe(observer);
            }
        });
    }

    @Test
    public void createAsync_single_success() {
        rxjava_createAsync_success(( service, observer) -> {
            try (CurrentTraceContext.Scope scope = currentTraceContext.newScope(context1)) {
                service.single().subscribe(observer);
            }
        });
    }

    @Test
    public void createAsync_flowable_success() {
        rx_createAsync_success(( service, subscriber) -> {
            try (CurrentTraceContext.Scope scope = currentTraceContext.newScope(context1)) {
                service.flowable().subscribe(subscriber);
            }
        });
    }

    class CurrentTraceContextObserver implements Observer<Object> , Subscriber<Object> {
        volatile TraceContext onSubscribe;

        volatile TraceContext onNext;

        volatile TraceContext onError;

        volatile TraceContext onComplete;

        @Override
        public void onSubscribe(Disposable d) {
            onSubscribe = currentTraceContext.get();
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            onSubscribe = currentTraceContext.get();
        }

        @Override
        public void onNext(Object object) {
            onNext = currentTraceContext.get();
        }

        @Override
        public void onError(Throwable e) {
            onError = currentTraceContext.get();
        }

        @Override
        public void onComplete() {
            onComplete = currentTraceContext.get();
        }
    }
}

