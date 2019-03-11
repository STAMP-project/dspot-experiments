package com.arellomobile.mvp.sample.github.mvp.presenters;


import com.arellomobile.mvp.sample.github.mvp.common.AuthUtils;
import com.arellomobile.mvp.sample.github.mvp.views.SignOutView..State;
import com.arellomobile.mvp.sample.github.test.GithubSampleTestRunner;
import com.arellomobile.mvp.sample.github.test.TestComponentRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(GithubSampleTestRunner.class)
public final class SignOutPresenterTest {
    @Rule
    public TestComponentRule testComponentRule = new TestComponentRule();

    @Mock
    SignOutView$$State signOutViewState;

    private SignOutPresenter presenter;

    @Test
    public void signout_shouldSingOut() {
        presenter.signOut();
        Mockito.verify(signOutViewState).signOut();
        Assert.assertEquals("", AuthUtils.getToken());
    }
}

