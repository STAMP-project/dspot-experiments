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


import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.TiView;
import org.junit.Test;
import org.mockito.Mockito;


public class TestPresenterExampleTest {
    private class LoginPresenter extends TiPresenter<TestPresenterExampleTest.LoginView> {
        void onSubmitClicked() {
            sendToView(new net.grandcentrix.thirtyinch.ViewAction<TestPresenterExampleTest.LoginView>() {
                @Override
                public void call(final TestPresenterExampleTest.LoginView view) {
                    view.showError("No username entered");
                }
            });
        }
    }

    private interface LoginView extends TiView {
        void showError(String msg);
    }

    @Test
    public void testLoadData() throws Exception {
        final TestPresenterExampleTest.LoginPresenter loginPresenter = new TestPresenterExampleTest.LoginPresenter();
        final TiTestPresenter<TestPresenterExampleTest.LoginView> testPresenter = test();
        final TestPresenterExampleTest.LoginView view = testPresenter.attachView(Mockito.mock(TestPresenterExampleTest.LoginView.class));
        loginPresenter.onSubmitClicked();
        Mockito.verify(view).showError("No username entered");
    }
}

