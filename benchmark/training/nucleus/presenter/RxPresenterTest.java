package nucleus.presenter;


import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import mocks.BundleMock;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Subscription;
import rx.functions.Func0;
import rx.observers.TestSubscriber;


public class RxPresenterTest {
    @Test
    public void testAdd() throws Exception {
        RxPresenter presenter = new RxPresenter();
        Subscription mock = Mockito.mock(Subscription.class);
        Mockito.when(mock.isUnsubscribed()).thenReturn(false);
        presenter.add(mock);
        presenter.onDestroy();
        Mockito.verify(mock, Mockito.times(1)).unsubscribe();
        Mockito.verify(mock, Mockito.atLeastOnce()).isUnsubscribed();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void testAddRemove() throws Exception {
        RxPresenter presenter = new RxPresenter();
        Subscription mock = Mockito.mock(Subscription.class);
        Mockito.when(mock.isUnsubscribed()).thenReturn(false);
        presenter.add(mock);
        presenter.remove(mock);
        Mockito.verify(mock, Mockito.atLeastOnce()).isUnsubscribed();
        Mockito.verify(mock, Mockito.times(1)).unsubscribe();
        presenter.onDestroy();
        Mockito.verifyNoMoreInteractions(mock);
    }

    @Test
    public void testRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);
        Func0<Subscription> restartable = Mockito.mock(Func0.class);
        Subscription subscription = Mockito.mock(Subscription.class);
        Mockito.when(restartable.call()).thenReturn(subscription);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(false);
        presenter.restartable(1, restartable);
        Mockito.verifyNoMoreInteractions(restartable);
        presenter.start(1);
        Mockito.verify(restartable, Mockito.times(1)).call();
        Mockito.verifyNoMoreInteractions(restartable);
        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);
        presenter = new RxPresenter();
        presenter.create(bundle);
        presenter.restartable(1, restartable);
        Mockito.verify(restartable, Mockito.times(2)).call();
        Mockito.verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testStopRestartable() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);
        Func0<Subscription> restartable = Mockito.mock(Func0.class);
        Subscription subscription = Mockito.mock(Subscription.class);
        Mockito.when(restartable.call()).thenReturn(subscription);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(false);
        presenter.restartable(1, restartable);
        Mockito.verifyNoMoreInteractions(restartable);
        presenter.start(1);
        Mockito.verify(restartable, Mockito.times(1)).call();
        Mockito.verifyNoMoreInteractions(restartable);
        presenter.stop(1);
        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);
        presenter = new RxPresenter();
        presenter.onCreate(bundle);
        presenter.restartable(1, restartable);
        Mockito.verify(restartable, Mockito.times(1)).call();
        Mockito.verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testCompletedRestartable() throws Exception {
        Func0<Subscription> restartable = Mockito.mock(Func0.class);
        Subscription subscription = Mockito.mock(Subscription.class);
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);
        Mockito.when(restartable.call()).thenReturn(subscription);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(true);
        presenter.restartable(1, restartable);
        Mockito.verifyNoMoreInteractions(restartable);
        presenter.start(1);
    }

    @Test
    public void testCompletedRestartableDoesNoRestart() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.onCreate(null);
        Func0<Subscription> restartable = Mockito.mock(Func0.class);
        Subscription subscription = Mockito.mock(Subscription.class);
        Mockito.when(restartable.call()).thenReturn(subscription);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(false);
        presenter.restartable(1, restartable);
        Mockito.verifyNoMoreInteractions(restartable);
        presenter.start(1);
        Mockito.verify(restartable, Mockito.times(1)).call();
        Mockito.verifyNoMoreInteractions(restartable);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(true);
        Bundle bundle = BundleMock.mock();
        presenter.onSave(bundle);
        presenter = new RxPresenter();
        presenter.onCreate(bundle);
        presenter.restartable(1, restartable);
        Mockito.verifyNoMoreInteractions(restartable);
    }

    @Test
    public void testRestartableIsUnsubscribed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);
        Func0<Subscription> restartable = Mockito.mock(Func0.class);
        Subscription subscription = Mockito.mock(Subscription.class);
        Mockito.when(restartable.call()).thenReturn(subscription);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(false);
        presenter.restartable(1, restartable);
        Assert.assertTrue(presenter.isUnsubscribed(1));
    }

    @Test
    public void testStartedRestartableIsNotUnsubscribed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);
        Func0<Subscription> restartable = Mockito.mock(Func0.class);
        Subscription subscription = Mockito.mock(Subscription.class);
        Mockito.when(restartable.call()).thenReturn(subscription);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(false);
        presenter.restartable(1, restartable);
        Assert.assertTrue(presenter.isUnsubscribed(1));
        presenter.start(1);
        Assert.assertFalse(presenter.isUnsubscribed(1));
    }

    @Test
    public void testCompletedRestartableIsUnsubscribed() throws Exception {
        RxPresenter presenter = new RxPresenter();
        presenter.create(null);
        Func0<Subscription> restartable = Mockito.mock(Func0.class);
        Subscription subscription = Mockito.mock(Subscription.class);
        Mockito.when(restartable.call()).thenReturn(subscription);
        Mockito.when(subscription.isUnsubscribed()).thenReturn(true);
        presenter.restartable(1, restartable);
        Assert.assertTrue(presenter.isUnsubscribed(1));
        presenter.start(1);
        Assert.assertTrue(presenter.isUnsubscribed(1));
    }

    @Test
    public void testViewObservable() {
        RxPresenter<Integer> presenter = new RxPresenter();
        presenter.onCreate(null);
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        presenter.view().subscribe(testSubscriber);
        testSubscriber.assertValueCount(0);
        List<Integer> values = new ArrayList<>();
        presenter.onTakeView(1);
        values.add(1);
        assertValues(values, testSubscriber);
        presenter.onDropView();
        values.add(null);
        assertValues(values, testSubscriber);
        presenter.onTakeView(2);
        values.add(2);
        assertValues(values, testSubscriber);
        presenter.onDestroy();
        assertValues(values, testSubscriber);
        testSubscriber.assertCompleted();
    }
}

