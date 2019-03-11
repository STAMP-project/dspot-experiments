package com.arellomobile.mvp.sample.github.mvp.presenters;


import R.string.error_field_required;
import R.string.error_invalid_password;
import com.arellomobile.mvp.sample.github.mvp.GithubService;
import com.arellomobile.mvp.sample.github.mvp.common.AuthUtils;
import com.arellomobile.mvp.sample.github.mvp.models.User;
import com.arellomobile.mvp.sample.github.mvp.views.SignInView..State;
import com.arellomobile.mvp.sample.github.test.GithubSampleTestRunner;
import com.arellomobile.mvp.sample.github.test.TestComponentRule;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import rx.Observable;


@RunWith(GithubSampleTestRunner.class)
public final class SignInPresenterTest {
    @Mock
    GithubService githubService;

    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule(testAppComponent());

    @Mock
    SignInView$$State signInViewState;

    private SignInPresenter presenter;

    @Test
    public void signin_shouldSignSuccessfull() {
        String token = token();
        Mockito.when(githubService.signIn(token)).thenReturn(Observable.just(new User()));
        presenter.signIn(email(), password());
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(token, AuthUtils.getToken());
        isSignInAndHideShowProgressCalled();
        Mockito.verify(signInViewState).successSignIn();
    }

    @Test
    public void signin_shouldShowError() {
        Mockito.when(githubService.signIn(token())).thenReturn(Observable.error(new Throwable()));
        presenter.signIn(email(), password());
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("", "");
        isSignInAndHideShowProgressCalled();
        Mockito.verify(signInViewState).failedSignIn(ArgumentMatchers.anyString());
    }

    @Test
    public void signin_shouldShowPasswordAndEmailEmptyErros() {
        presenter.signIn(null, null);
        Mockito.verify(signInViewState).showFormError(error_field_required, error_invalid_password);
    }

    @Test
    public void signin_shouldOnErrorCancel() {
        presenter.onErrorCancel();
        Mockito.verify(signInViewState).hideError();
    }
}

