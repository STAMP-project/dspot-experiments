package com.kickstarter.viewmodels;


import SignupViewModel.ViewModel;
import androidx.annotation.NonNull;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.Environment;
import com.kickstarter.mock.factories.ApiExceptionFactory;
import com.kickstarter.mock.factories.ConfigFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.services.ApiClientType;
import com.kickstarter.services.apiresponses.AccessTokenEnvelope;
import com.kickstarter.services.apiresponses.ErrorEnvelope;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class SignupViewModelTest extends KSRobolectricTestCase {
    @Test
    public void testSignupViewModel_FormValidation() {
        final Environment environment = environment();
        environment.currentConfig().config(ConfigFactory.config());
        final SignupViewModel.ViewModel vm = new SignupViewModel.ViewModel(environment);
        final TestSubscriber<Boolean> formIsValidTest = new TestSubscriber();
        vm.outputs.formIsValid().subscribe(formIsValidTest);
        vm.inputs.name("brandon");
        formIsValidTest.assertNoValues();
        vm.inputs.email("incorrect@kickstarter");
        formIsValidTest.assertNoValues();
        vm.inputs.password("danisawesome");
        formIsValidTest.assertValues(false);
        vm.inputs.email("hello@kickstarter.com");
        formIsValidTest.assertValues(false, true);
    }

    @Test
    public void testSignupViewModel_SuccessfulSignup() {
        final SignupViewModel.ViewModel vm = new SignupViewModel.ViewModel(environment());
        final TestSubscriber<Void> signupSuccessTest = new TestSubscriber();
        vm.outputs.signupSuccess().subscribe(signupSuccessTest);
        final TestSubscriber<Boolean> formSubmittingTest = new TestSubscriber();
        vm.outputs.formSubmitting().subscribe(formSubmittingTest);
        vm.inputs.name("brandon");
        vm.inputs.email("hello@kickstarter.com");
        vm.inputs.email("incorrect@kickstarter");
        vm.inputs.password("danisawesome");
        vm.inputs.sendNewslettersClick(true);
        vm.inputs.signupClick();
        formSubmittingTest.assertValues(true, false);
        signupSuccessTest.assertValueCount(1);
        koalaTest.assertValues("User Signup", "Signup Newsletter Toggle", "Login", "New User");
    }

    @Test
    public void testSignupViewModel_ApiValidationError() {
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<AccessTokenEnvelope> signup(@NonNull
            final String name, @NonNull
            final String email, @NonNull
            final String password, @NonNull
            final String passwordConfirmation, final boolean sendNewsletters) {
                return Observable.error(ApiExceptionFactory.apiError(ErrorEnvelope.builder().httpCode(422).build()));
            }
        };
        final Environment environment = environment().toBuilder().apiClient(apiClient).build();
        final SignupViewModel.ViewModel vm = new SignupViewModel.ViewModel(environment);
        final TestSubscriber<Void> signupSuccessTest = new TestSubscriber();
        vm.outputs.signupSuccess().subscribe(signupSuccessTest);
        final TestSubscriber<String> signupErrorTest = new TestSubscriber();
        vm.outputs.errorString().subscribe(signupErrorTest);
        final TestSubscriber<Boolean> formSubmittingTest = new TestSubscriber();
        vm.outputs.formSubmitting().subscribe(formSubmittingTest);
        vm.inputs.name("brandon");
        vm.inputs.email("hello@kickstarter.com");
        vm.inputs.email("incorrect@kickstarter");
        vm.inputs.password("danisawesome");
        vm.inputs.sendNewslettersClick(true);
        vm.inputs.signupClick();
        formSubmittingTest.assertValues(true, false);
        signupSuccessTest.assertValueCount(0);
        signupErrorTest.assertValueCount(1);
        koalaTest.assertValues("User Signup", "Signup Newsletter Toggle", "Errored User Signup");
    }

    @Test
    public void testSignupViewModel_ApiError() {
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<AccessTokenEnvelope> signup(@NonNull
            final String name, @NonNull
            final String email, @NonNull
            final String password, @NonNull
            final String passwordConfirmation, final boolean sendNewsletters) {
                return Observable.error(ApiExceptionFactory.badRequestException());
            }
        };
        final Environment environment = environment().toBuilder().apiClient(apiClient).build();
        final SignupViewModel.ViewModel vm = new SignupViewModel.ViewModel(environment);
        final TestSubscriber<Void> signupSuccessTest = new TestSubscriber();
        vm.outputs.signupSuccess().subscribe(signupSuccessTest);
        final TestSubscriber<String> signupErrorTest = new TestSubscriber();
        vm.outputs.errorString().subscribe(signupErrorTest);
        final TestSubscriber<Boolean> formSubmittingTest = new TestSubscriber();
        vm.outputs.formSubmitting().subscribe(formSubmittingTest);
        vm.inputs.name("brandon");
        vm.inputs.email("hello@kickstarter.com");
        vm.inputs.email("incorrect@kickstarter");
        vm.inputs.password("danisawesome");
        vm.inputs.sendNewslettersClick(true);
        vm.inputs.signupClick();
        formSubmittingTest.assertValues(true, false);
        signupSuccessTest.assertValueCount(0);
        signupErrorTest.assertValueCount(1);
        koalaTest.assertValues("User Signup", "Signup Newsletter Toggle", "Errored User Signup");
    }
}

