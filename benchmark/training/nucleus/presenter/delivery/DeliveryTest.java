package nucleus.presenter.delivery;


import org.junit.Test;
import rx.Notification;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class DeliveryTest {
    @Test
    public void testSplitOnNext() throws Exception {
        testWithOnNextOnError(new rx.functions.Action2<rx.functions.Action2, rx.functions.Action2>() {
            @Override
            public void call(rx.functions.Action2 onNext, rx.functions.Action2 onError) {
                new Delivery(1, Notification.createOnNext(2)).split(onNext, onError);
                verify(onNext, times(1)).call(1, 2);
            }
        });
    }

    @Test
    public void testSplitOnError() throws Exception {
        testWithOnNextOnError(new rx.functions.Action2<rx.functions.Action2, rx.functions.Action2>() {
            @Override
            public void call(rx.functions.Action2 onNext, rx.functions.Action2 onError) {
                Throwable throwable = new Throwable();
                new Delivery(1, Notification.createOnError(throwable)).split(onNext, onError);
                verify(onError, times(1)).call(1, throwable);
            }
        });
    }

    @Test
    public void testSplitOnComplete() throws Exception {
        testWithOnNextOnError(new rx.functions.Action2<rx.functions.Action2, rx.functions.Action2>() {
            @Override
            public void call(rx.functions.Action2 onNext, rx.functions.Action2 onError) {
                new Delivery(1, Notification.createOnCompleted()).split(onNext, onError);
            }
        });
    }
}

