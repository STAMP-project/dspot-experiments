/**
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.grandcentrix.thirtyinch.rx;


import net.grandcentrix.thirtyinch.TiView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;


@RunWith(JUnit4.class)
public class RxTiPresenterSubscriptionHandlerTest {
    private TiMockPresenter mPresenter;

    private RxTiPresenterSubscriptionHandler mSubscriptionHandler;

    private TiView mView;

    @Test
    public void testManageSubscription_AfterDestroy_ShouldThrowIllegalState() throws Exception {
        create();
        destroy();
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        try {
            mSubscriptionHandler.manageSubscription(testSubscriber);
            fail("no exception");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("DESTROYED");
        }
    }

    @Test
    public void testManageSubscription_ShouldReturnSameSubscription() throws Exception {
        create();
        mPresenter.attachView(mView);
        final TestSubscriber<Void> testSubscriber = new TestSubscriber();
        final Subscription subscription = mSubscriptionHandler.manageSubscription(testSubscriber);
        assertThat(testSubscriber).isEqualTo(subscription);
    }

    @Test
    public void testManageSubscription_WithAlreadyUnsubscribedSubscription_ShouldDoNothing() throws Exception {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        testSubscriber.unsubscribe();
        mSubscriptionHandler.manageSubscription(testSubscriber);
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageSubscription_WithDestroy_ShouldUnsubscribe() throws Exception {
        create();
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        mSubscriptionHandler.manageSubscription(testSubscriber);
        assertThat(testSubscriber.isUnsubscribed()).isFalse();
        destroy();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageSubscriptions_AfterDestroy_ShouldThrowIllegalState() throws Exception {
        create();
        destroy();
        TestSubscriber<Void> firstSubscriber = new TestSubscriber();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber();
        TestSubscriber<Void> thirdSubscriber = new TestSubscriber();
        try {
            mSubscriptionHandler.manageSubscriptions(firstSubscriber, secondSubscriber, thirdSubscriber);
            fail("no exception");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("DESTROYED");
        }
    }

    @Test
    public void testManageSubscriptions_WithAlreadyUnsubscribedSubscription_ShouldDoNothing() throws Exception {
        TestSubscriber<Void> firstSubscriber = new TestSubscriber();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber();
        TestSubscriber<Void> thirdSubscriber = new TestSubscriber();
        firstSubscriber.unsubscribe();
        secondSubscriber.unsubscribe();
        thirdSubscriber.unsubscribe();
        mSubscriptionHandler.manageSubscriptions(firstSubscriber, secondSubscriber, thirdSubscriber);
        firstSubscriber.assertUnsubscribed();
        secondSubscriber.assertUnsubscribed();
        thirdSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageSubscriptions_WithDestroy_ShouldUnsubscribe() throws Exception {
        create();
        TestSubscriber<Void> firstSubscriber = new TestSubscriber();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber();
        TestSubscriber<Void> thirdSubscriber = new TestSubscriber();
        mSubscriptionHandler.manageSubscriptions(firstSubscriber, secondSubscriber, thirdSubscriber);
        assertThat(firstSubscriber.isUnsubscribed()).isFalse();
        assertThat(secondSubscriber.isUnsubscribed()).isFalse();
        assertThat(thirdSubscriber.isUnsubscribed()).isFalse();
        destroy();
        firstSubscriber.assertUnsubscribed();
        secondSubscriber.assertUnsubscribed();
        thirdSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageSubscriptions_WithDetach_ShouldUnsubcribe() throws Exception {
        create();
        mPresenter.attachView(mView);
        TestSubscriber<Void> firstSubscriber = new TestSubscriber();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber();
        TestSubscriber<Void> thirdSubscriber = new TestSubscriber();
        mSubscriptionHandler.manageViewSubscriptions(firstSubscriber, secondSubscriber, thirdSubscriber);
        assertThat(firstSubscriber.isUnsubscribed()).isFalse();
        assertThat(secondSubscriber.isUnsubscribed()).isFalse();
        assertThat(thirdSubscriber.isUnsubscribed()).isFalse();
        detachView();
        firstSubscriber.assertUnsubscribed();
        secondSubscriber.assertUnsubscribed();
        thirdSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageViewSubscription_InOnDetachView_ShouldThrow() throws Exception {
        final TiMockPresenter presenter = new TiMockPresenter() {
            private RxTiPresenterSubscriptionHandler mSubscriptionHandler = new RxTiPresenterSubscriptionHandler(this);

            @Override
            protected void onDetachView() {
                super.onDetachView();
                mSubscriptionHandler.manageViewSubscription(Observable.just("test").subscribe());
            }
        };
        create();
        presenter.attachView(mView);
        try {
            detachView();
            fail("did not throw");
        } catch (Throwable e) {
            assertThat(e).hasMessageContaining("no view");
        }
    }

    @Test
    public void testManageViewSubscription_ShouldReturnSameSubscription() throws Exception {
        create();
        mPresenter.attachView(mView);
        final TestSubscriber<Void> testSubscriber = new TestSubscriber();
        final Subscription subscription = mSubscriptionHandler.manageViewSubscription(testSubscriber);
        assertThat(testSubscriber).isEqualTo(subscription);
    }

    @Test
    public void testManageViewSubscription_WithDetachSingleSub_ShouldUnsubscribe() throws Exception {
        create();
        mPresenter.attachView(mView);
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        mSubscriptionHandler.manageViewSubscription(testSubscriber);
        assertThat(testSubscriber.isUnsubscribed()).isFalse();
        detachView();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageViewSubscription_WithDetachView_ShouldUnsubscribe() throws Exception {
        create();
        mPresenter.attachView(mView);
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        mSubscriptionHandler.manageViewSubscription(testSubscriber);
        detachView();
        testSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManageViewSubscription_manageAfterDetach_ShouldThrowIllegalStateException() throws Exception {
        create();
        mPresenter.attachView(mView);
        detachView();
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        try {
            mSubscriptionHandler.manageViewSubscription(testSubscriber);
            fail("no exception");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("when there is no view");
        }
    }

    @Test
    public void testManageViewSubscription_manageBeforeViewAttached_ShouldThrowIllegalStateException() throws Exception {
        create();
        TestSubscriber<Integer> testSubscriber = new TestSubscriber();
        try {
            mSubscriptionHandler.manageViewSubscription(testSubscriber);
            fail("no exception");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("when there is no view");
        }
    }

    @Test
    public void testManageViewSubscriptions_WithOneAlreadyUnsubscribed_ShouldNotAddToSubscription() throws Exception {
        create();
        mPresenter.attachView(mView);
        TestSubscriber<Void> firstSubscriber = new TestSubscriber();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber();
        secondSubscriber.unsubscribe();
        mSubscriptionHandler.manageViewSubscriptions(firstSubscriber, secondSubscriber);
        assertThat(firstSubscriber.isUnsubscribed()).isFalse();
        secondSubscriber.assertUnsubscribed();
    }

    @Test
    public void testManagerViewSubscriptions_WithDetach_ShouldUnsubcribe() throws Exception {
        create();
        mPresenter.attachView(mView);
        TestSubscriber<Void> firstSubscriber = new TestSubscriber();
        TestSubscriber<Void> secondSubscriber = new TestSubscriber();
        TestSubscriber<Void> thirdSubscriber = new TestSubscriber();
        mSubscriptionHandler.manageViewSubscriptions(firstSubscriber, secondSubscriber, thirdSubscriber);
        assertThat(firstSubscriber.isUnsubscribed()).isFalse();
        assertThat(secondSubscriber.isUnsubscribed()).isFalse();
        assertThat(thirdSubscriber.isUnsubscribed()).isFalse();
        detachView();
        firstSubscriber.assertUnsubscribed();
        secondSubscriber.assertUnsubscribed();
        thirdSubscriber.assertUnsubscribed();
    }
}

