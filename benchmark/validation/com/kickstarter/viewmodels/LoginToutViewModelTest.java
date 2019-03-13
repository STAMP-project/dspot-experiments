package com.kickstarter.viewmodels;


import LoginToutViewModel.ViewModel;
import com.kickstarter.KSRobolectricTestCase;
import org.junit.Test;
import rx.observers.TestSubscriber;


public class LoginToutViewModelTest extends KSRobolectricTestCase {
    @Test
    public void testLoginButtonClicked() {
        final LoginToutViewModel.ViewModel vm = new LoginToutViewModel.ViewModel(environment());
        final TestSubscriber<Void> startLoginActivity = new TestSubscriber();
        vm.outputs.startLoginActivity().subscribe(startLoginActivity);
        startLoginActivity.assertNoValues();
        vm.inputs.loginClick();
        startLoginActivity.assertValueCount(1);
    }

    @Test
    public void testSignupButtonClicked() {
        final LoginToutViewModel.ViewModel vm = new LoginToutViewModel.ViewModel(environment());
        final TestSubscriber<Void> startSignupActivity = new TestSubscriber();
        vm.outputs.startSignupActivity().subscribe(startSignupActivity);
        startSignupActivity.assertNoValues();
        vm.inputs.signupClick();
        startSignupActivity.assertValueCount(1);
    }
}

