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
package net.grandcentrix.thirtyinch;


import android.support.annotation.NonNull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class SendToViewTest {
    private class TestPresenter extends TiPresenter<SendToViewTest.TestView> {}

    private interface TestView extends TiView {
        void doSomething1();

        void doSomething2();

        void doSomething3();
    }

    private Executor mImmediatelySameThread = new Executor() {
        @Override
        public void execute(@NonNull
        final Runnable action) {
            action.run();
        }
    };

    @Test
    public void sendToViewInOrder() throws Exception {
        final SendToViewTest.TestPresenter presenter = new SendToViewTest.TestPresenter();
        create();
        setUiThreadExecutor(mImmediatelySameThread);
        assertThat(getQueuedViewActions()).isEmpty();
        presenter.sendToView(new ViewAction<SendToViewTest.TestView>() {
            @Override
            public void call(final SendToViewTest.TestView view) {
                view.doSomething3();
            }
        });
        presenter.sendToView(new ViewAction<SendToViewTest.TestView>() {
            @Override
            public void call(final SendToViewTest.TestView view) {
                view.doSomething1();
            }
        });
        presenter.sendToView(new ViewAction<SendToViewTest.TestView>() {
            @Override
            public void call(final SendToViewTest.TestView view) {
                view.doSomething2();
            }
        });
        assertThat(getQueuedViewActions()).hasSize(3);
        final SendToViewTest.TestView view = Mockito.mock(SendToViewTest.TestView.class);
        attachView(view);
        assertThat(getQueuedViewActions()).isEmpty();
        final InOrder inOrder = Mockito.inOrder(view);
        inOrder.verify(view).doSomething3();
        inOrder.verify(view).doSomething1();
        inOrder.verify(view).doSomething2();
    }

    @Test
    public void testSendToViewRunsOnTheMainThread() throws Exception {
        // Given a presenter with executor (single thread)
        final TiPresenter<TiView> presenter = new TiPresenter<TiView>() {};
        presenter.create();
        final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(r, "test ui thread");
            }
        });
        presenter.setUiThreadExecutor(executor);
        presenter.attachView(Mockito.mock(TiView.class));
        final Thread testThread = Thread.currentThread();
        // When send work to the view
        final CountDownLatch latch = new CountDownLatch(1);
        presenter.sendToView(new ViewAction<TiView>() {
            @Override
            public void call(final TiView tiView) {
                // Then the work gets executed on the ui thread
                final Thread currentThread = Thread.currentThread();
                assertThat(testThread).isNotSameAs(currentThread);
                assertThat("test ui thread").as("executed on wrong thread").isEqualTo(currentThread.getName());
                latch.countDown();
            }
        });
        // wait a reasonable amount of time for the thread to execute the work
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void viewAttached() throws Exception {
        final SendToViewTest.TestPresenter presenter = new SendToViewTest.TestPresenter();
        create();
        setUiThreadExecutor(mImmediatelySameThread);
        assertThat(getQueuedViewActions()).isEmpty();
        final SendToViewTest.TestView view = Mockito.mock(SendToViewTest.TestView.class);
        attachView(view);
        presenter.sendToView(new ViewAction<SendToViewTest.TestView>() {
            @Override
            public void call(final SendToViewTest.TestView view) {
                view.doSomething1();
            }
        });
        assertThat(getQueuedViewActions()).isEmpty();
        Mockito.verify(view).doSomething1();
    }

    @Test
    public void viewDetached() throws Exception {
        final SendToViewTest.TestPresenter presenter = new SendToViewTest.TestPresenter();
        create();
        setUiThreadExecutor(mImmediatelySameThread);
        assertThat(getQueuedViewActions()).isEmpty();
        presenter.sendToView(new ViewAction<SendToViewTest.TestView>() {
            @Override
            public void call(final SendToViewTest.TestView view) {
                view.doSomething1();
            }
        });
        assertThat(getQueuedViewActions()).hasSize(1);
        final SendToViewTest.TestView view = Mockito.mock(SendToViewTest.TestView.class);
        attachView(view);
        Mockito.verify(view).doSomething1();
        assertThat(getQueuedViewActions()).isEmpty();
    }

    @Test
    public void viewReceivesNoInteractionsAfterDetaching() throws Exception {
        final SendToViewTest.TestPresenter presenter = new SendToViewTest.TestPresenter();
        create();
        setUiThreadExecutor(mImmediatelySameThread);
        assertThat(getQueuedViewActions()).isEmpty();
        final SendToViewTest.TestView view = Mockito.mock(SendToViewTest.TestView.class);
        attachView(view);
        detachView();
        presenter.sendToView(new ViewAction<SendToViewTest.TestView>() {
            @Override
            public void call(final SendToViewTest.TestView view) {
                view.doSomething1();
            }
        });
        assertThat(getQueuedViewActions()).hasSize(1);
        Mockito.verifyZeroInteractions(view);
        attachView(view);
        Mockito.verify(view).doSomething1();
        assertThat(getQueuedViewActions()).isEmpty();
        detachView();
        sendToView(new ViewAction<SendToViewTest.TestView>() {
            @Override
            public void call(final SendToViewTest.TestView view) {
                view.doSomething1();
            }
        });
        assertThat(getQueuedViewActions()).hasSize(1);
        Mockito.verifyNoMoreInteractions(view);
    }
}

