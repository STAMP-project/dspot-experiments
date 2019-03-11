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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.grandcentrix.thirtyinch.test.TiTestPresenter;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jannisveerkamp
 * @since 11.07.16.
 */
public class TiPresenterTest {
    private TiMockPresenter mPresenter;

    private TiView mView;

    @Test
    public void attachDifferentView() throws Exception {
        TiView viewOverride = Mockito.mock(TiView.class);
        create();
        mPresenter.attachView(mView);
        assertThat(getView()).isEqualTo(mView);
        try {
            mPresenter.attachView(viewOverride);
            fail("no exception thrown");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("detachView");
        }
    }

    @Test
    public void attachNullView() throws Exception {
        create();
        try {
            mPresenter.attachView(null);
            fail("no exception thrown");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("detachView()");
        }
    }

    @Test
    public void attachSameViewTwice() throws Exception {
        create();
        mPresenter.attachView(mView);
        assertThat(getView()).isEqualTo(mView);
        mPresenter.attachView(mView);
        assertThat(getView()).isEqualTo(mView);
    }

    @Test
    public void attachViewToDestroyedPresenter() throws Exception {
        create();
        destroy();
        try {
            mPresenter.attachView(mView);
            fail("no exception thrown");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("terminal state");
        }
    }

    @Test
    public void attachWithoutInitialize() throws Exception {
        try {
            mPresenter.attachView(mView);
            fail("no exception thrown");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("create()");
        }
    }

    @Test
    public void destroyPresenterWithAttachedView() throws Exception {
        create();
        mPresenter.attachView(mView);
        try {
            destroy();
            fail("error expected");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("attached").hasMessageContaining("detachView()");
        }
    }

    @Test
    public void destroyWithoutAttachingView() throws Exception {
        create();
        destroy();
    }

    @Test
    public void detachView() throws Exception {
        create();
        assertThat(getView()).isNull();
        final TiView view = Mockito.mock(TiView.class);
        mPresenter.attachView(view);
        assertThat(getView()).isEqualTo(view);
        mPresenter.detachView();
        assertThat(getView()).isNull();
    }

    @Test
    public void detachViewNotAttached() throws Exception {
        create();
        // no exception, just ignoring
        mPresenter.detachView();
        attachView(Mockito.mock(TiView.class));
        // no exception, just ignoring
        mPresenter.detachView();
        mPresenter.detachView();
    }

    @Test
    public void testCallingOnAttachViewDirectly() throws Exception {
        try {
            onAttachView(Mockito.mock(TiView.class));
            fail("no exception thrown");
        } catch (IllegalAccessError e) {
            assertThat(e).hasMessageContaining("attachView(TiView)").hasMessageContaining("#onAttachView(TiView)");
        }
    }

    @Test
    public void testCallingOnCreateDirectly() throws Exception {
        try {
            mPresenter.onCreate();
            fail("no exception thrown");
        } catch (IllegalAccessError e) {
            assertThat(e).hasMessageContaining("create()").hasMessageContaining("#onCreate()");
        }
    }

    @Test
    public void testCallingOnDestroyDirectly() throws Exception {
        try {
            mPresenter.onDestroy();
            fail("no exception thrown");
        } catch (IllegalAccessError e) {
            assertThat(e).hasMessageContaining("destroy()").hasMessageContaining("#onDestroy()");
        }
    }

    @Test
    public void testCallingOnDetachViewDirectly() throws Exception {
        try {
            onDetachView();
            fail("no exception thrown");
        } catch (IllegalAccessError e) {
            assertThat(e).hasMessageContaining("detachView()").hasMessageContaining("#onDetachView()");
        }
    }

    @Test
    public void testCallingOnSleepDirectly() throws Exception {
        try {
            mPresenter.onSleep();
            fail("no exception thrown");
        } catch (IllegalAccessError e) {
            assertThat(e).hasMessageContaining("detachView()").hasMessageContaining("#onSleep()");
        }
    }

    @Test
    public void testCallingOnWakeUpDirectly() throws Exception {
        try {
            mPresenter.onWakeUp();
            fail("no exception thrown");
        } catch (IllegalAccessError e) {
            assertThat(e).hasMessageContaining("attachView(TiView)").hasMessageContaining("#onWakeUp()");
        }
    }

    @Test
    public void testCreate() throws Exception {
        assertThat(mPresenter.onCreateCalled).isEqualTo(0);
        create();
        assertThat(mPresenter.onCreateCalled).isEqualTo(1);
        // onCreate can only be called once
        create();
        assertThat(mPresenter.onCreateCalled).isEqualTo(1);
    }

    @Test(expected = SuperNotCalledException.class)
    public void testCreateSuperNotCalled() throws Exception {
        TiPresenter<TiView> presenter = new TiPresenter<TiView>() {
            @Override
            protected void onCreate() {
                // Intentionally not calling super.onCreate()
            }
        };
        presenter.create();
    }

    @Test
    public void testDestroy() throws Exception {
        create();
        assertThat(mPresenter.onDestroyCalled).isEqualTo(0);
        destroy();
        assertThat(mPresenter.onDestroyCalled).isEqualTo(1);
        destroy();
        assertThat(mPresenter.onDestroyCalled).isEqualTo(1);
    }

    @Test
    public void testDestroyCreateNotCalled() throws Exception {
        assertThat(mPresenter.onDestroyCalled).isEqualTo(0);
        destroy();
        assertThat(mPresenter.onDestroyCalled).isEqualTo(0);
    }

    @Test(expected = SuperNotCalledException.class)
    public void testDestroySuperNotCalled() throws Exception {
        TiPresenter<TiView> presenter = new TiPresenter<TiView>() {
            @Override
            protected void onDestroy() {
                // Intentionally not calling super.onDestroy()
            }
        };
        presenter.create();
        presenter.destroy();
    }

    @Test
    public void testGetViewOrThrow() {
        create();
        mPresenter.attachView(mView);
        mPresenter.detachView();
        try {
            getViewOrThrow();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("The view is currently not attached. Use 'sendToView(ViewAction)' instead.");
        }
    }

    @Test
    public void testGetViewOrThrowReturnsView() {
        create();
        mPresenter.attachView(mView);
        assertThat(getViewOrThrow()).isEqualTo(mView);
    }

    @Test
    public void testGetViewReturnsNull() {
        create();
        mPresenter.attachView(mView);
        mPresenter.detachView();
        assertThat(getView()).isNull();
    }

    @Test
    public void testGetViewReturnsView() {
        create();
        mPresenter.attachView(mView);
        assertThat(getView()).isEqualTo(mView);
    }

    @Test
    public void testMissingUiExecutorAndDetachedView() throws Exception {
        final TiPresenter<TiView> presenter = new TiPresenter<TiView>() {};
        try {
            presenter.runOnUiThread(Mockito.mock(Runnable.class));
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageContaining("view").hasMessageContaining("no executor");
        }
    }

    @Test
    public void testMissingUiExecutorAttachedView() throws Exception {
        final TiPresenter<TiView> presenter = new TiPresenter<TiView>() {};
        presenter.create();
        presenter.attachView(Mockito.mock(TiView.class));
        try {
            presenter.runOnUiThread(Mockito.mock(Runnable.class));
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).doesNotContain("view");
            assertThat(e).hasMessageContaining("no ui thread executor");
        }
    }

    @Test
    public void testOnAttachViewSuperNotCalled() throws Exception {
        TiPresenter<TiView> presenter = new TiPresenter<TiView>() {
            @Override
            protected void onAttachView(@NonNull
            final TiView view) {
                // Intentionally not calling super.onSleep()
            }
        };
        presenter.create();
        try {
            presenter.attachView(Mockito.mock(TiView.class));
            fail("no exception thrown");
        } catch (SuperNotCalledException e) {
            assertThat(e).hasMessageContaining("super.onAttachView(TiView)");
        }
    }

    @Test
    public void testOnDetachViewSuperNotCalled() throws Exception {
        TiPresenter<TiView> presenter = new TiPresenter<TiView>() {
            @Override
            protected void onDetachView() {
                // Intentionally not calling super.onSleep()
            }
        };
        presenter.create();
        presenter.attachView(Mockito.mock(TiView.class));
        try {
            presenter.detachView();
            fail("no exception thrown");
        } catch (SuperNotCalledException e) {
            assertThat(e).hasMessageContaining("super.onDetachView()");
        }
    }

    @Test
    public void testRunOnUiExecutor() throws Exception {
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
        // When scheduling work to the UI thread
        final CountDownLatch latch = new CountDownLatch(1);
        presenter.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Then the work gets executed on the correct thread
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
    public void testSleepSuperNotCalled() throws Exception {
        TiPresenter<TiView> presenter = new TiPresenter<TiView>() {
            @Override
            protected void onSleep() {
                // Intentionally not calling super.onSleep()
            }
        };
        presenter.create();
        presenter.attachView(Mockito.mock(TiView.class));
        try {
            presenter.detachView();
            fail("no exception thrown");
        } catch (SuperNotCalledException e) {
            assertThat(e).hasMessageContaining("super.onSleep()");
        }
    }

    @Test
    public void testTest_ShouldReturnTiTestPresenter() throws Exception {
        final TiTestPresenter<TiView> test = test();
        assertThat(test).isInstanceOf(TiTestPresenter.class);
    }

    @Test
    public void testToString() throws Exception {
        create();
        assertThat(mPresenter.toString()).contains("TiMockPresenter").contains("{view = null}");
        mPresenter.attachView(mView);
        assertThat(mPresenter.toString()).contains("TiMockPresenter").contains("{view = Mock for TiView, hashCode: ");
    }

    @Test
    public void testWakeUpSuperNotCalled() throws Exception {
        TiPresenter<TiView> presenter = new TiPresenter<TiView>() {
            @Override
            protected void onWakeUp() {
                // Intentionally not calling super.onWakeup()
            }
        };
        presenter.create();
        try {
            presenter.attachView(Mockito.mock(TiView.class));
            fail("no exception thrown");
        } catch (SuperNotCalledException e) {
            assertThat(e).hasMessageContaining("super.onWakeUp()");
        }
    }
}

