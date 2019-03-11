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
package net.grandcentrix.thirtyinch.test;


import State.VIEW_ATTACHED;
import State.VIEW_DETACHED;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import junit.framework.Assert;
import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.TiView;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class TiTestPresenterTest {
    interface MockTiView extends TiView {
        void helloWorld();
    }

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private TiPresenter<TiView> mMockPresenter;

    private TiPresenter<TiTestPresenterTest.MockTiView> mMockTiPresenter;

    private TiTestPresenterTest.MockTiView mMockTiView;

    @Mock
    private TiView mMockView;

    @Test
    public void testAttachView_ShouldReplaceUIThreadExecutor() throws Exception {
        // Given the presenter is currently in the state VIEW_DETACHED.
        Mockito.when(mMockPresenter.getState()).thenReturn(VIEW_DETACHED);
        final TiTestPresenter<TiView> tiTestPresenter = new TiTestPresenter(mMockPresenter);
        // When a new View is attached to the TiTestPresenter.
        tiTestPresenter.attachView(mMockView);
        // Then the TiTestPresenter should set any ui thread executor on the Presenter.
        Mockito.verify(mMockPresenter).setUiThreadExecutor(ArgumentMatchers.any(Executor.class));
        // And then the TiTestPresenter should attach the new View to the Presenter.
        Mockito.verify(mMockPresenter).attachView(mMockView);
    }

    @Test
    public void testAttachView_WithAttachedView_ShouldDetachPreviousView() {
        // Given the presenter is currently in the state VIEW_ATTACHED.
        Mockito.when(mMockPresenter.getState()).thenReturn(VIEW_ATTACHED);
        final TiTestPresenter<TiView> tiTestPresenter = new TiTestPresenter(mMockPresenter);
        // When a new View is attached to the TiTestPresenter.
        tiTestPresenter.attachView(mMockTiView);
        // Then the TiTestPresenter should call detachView() on the Presenter.
        Mockito.verify(mMockPresenter).detachView();
    }

    @Test
    public void testSendToView_InUnitTestWithTiTestPresenter_ShouldNotThrow() throws Exception {
        final TiTestPresenter<TiTestPresenterTest.MockTiView> testPresenter = new TiTestPresenter(mMockTiPresenter);
        testPresenter.attachView(mMockTiView);
        Mockito.verify(mMockTiView).helloWorld();
    }

    @Test
    public void testSendToView_InUnitTest_ShouldThrow() throws Exception {
        try {
            mMockTiPresenter.attachView(mMockTiView);
            Assert.fail("No exception");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("no ui thread executor available");
        }
    }

    @Test
    public void testSimpleViewInvocationWithTestPresenter() throws Exception {
        final TiPresenter<TiTestPresenterTest.MockTiView> presenter = new TiPresenter<TiTestPresenterTest.MockTiView>() {
            @Override
            protected void onAttachView(@NonNull
            TiTestPresenterTest.MockTiView view) {
                super.onAttachView(view);
                sendToView(new net.grandcentrix.thirtyinch.ViewAction<TiTestPresenterTest.MockTiView>() {
                    @Override
                    public void call(TiTestPresenterTest.MockTiView tiView) {
                        tiView.helloWorld();
                    }
                });
            }
        };
        final TiTestPresenter<TiTestPresenterTest.MockTiView> testPresenter = presenter.test();
        final TiTestPresenterTest.MockTiView view = Mockito.mock(TiTestPresenterTest.MockTiView.class);
        testPresenter.attachView(view);
        Mockito.verify(view).helloWorld();
    }
}

