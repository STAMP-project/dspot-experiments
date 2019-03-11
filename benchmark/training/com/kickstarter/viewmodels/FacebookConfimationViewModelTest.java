package com.kickstarter.viewmodels;


import ErrorEnvelope.FacebookUser;
import FacebookConfirmationViewModel.ViewModel;
import IntentKey.FACEBOOK_TOKEN;
import IntentKey.FACEBOOK_USER;
import KoalaEvent.ERRORED_USER_SIGNUP;
import KoalaEvent.FACEBOOK_CONFIRM;
import KoalaEvent.LOGIN;
import KoalaEvent.NEW_USER;
import KoalaEvent.SIGNUP_NEWSLETTER_TOGGLE;
import KoalaEvent.USER_SIGNUP;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.CurrentConfigType;
import com.kickstarter.libs.Environment;
import com.kickstarter.mock.MockCurrentConfig;
import com.kickstarter.mock.factories.ApiExceptionFactory;
import com.kickstarter.mock.factories.ConfigFactory;
import com.kickstarter.mock.services.MockApiClient;
import com.kickstarter.services.ApiClientType;
import com.kickstarter.services.apiresponses.AccessTokenEnvelope;
import com.kickstarter.services.apiresponses.ErrorEnvelope;
import java.util.Collections;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;


public class FacebookConfimationViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<String> prefillEmail = new TestSubscriber();

    private final TestSubscriber<String> signupError = new TestSubscriber();

    private final TestSubscriber<Void> signupSuccess = new TestSubscriber();

    private final TestSubscriber<Boolean> sendNewslettersIsChecked = new TestSubscriber();

    @Test
    public void testPrefillEmail() {
        final ErrorEnvelope.FacebookUser facebookUser = FacebookUser.builder().id(1).name("Test").email("test@kickstarter.com").build();
        this.vm = new FacebookConfirmationViewModel.ViewModel(environment());
        this.vm.intent(new Intent().putExtra(FACEBOOK_USER, facebookUser));
        this.vm.outputs.prefillEmail().subscribe(this.prefillEmail);
        this.prefillEmail.assertValue("test@kickstarter.com");
        this.koalaTest.assertValues(FACEBOOK_CONFIRM, USER_SIGNUP);
    }

    @Test
    public void testSignupErrorDisplay() {
        final ApiClientType apiClient = new MockApiClient() {
            @Override
            @NonNull
            public Observable<AccessTokenEnvelope> registerWithFacebook(@NonNull
            final String fbAccessToken, final boolean sendNewsletters) {
                return Observable.error(ApiExceptionFactory.apiError(ErrorEnvelope.builder().httpCode(404).errorMessages(Collections.singletonList("oh no")).build()));
            }
        };
        final Environment environment = environment().toBuilder().apiClient(apiClient).build();
        this.vm = new FacebookConfirmationViewModel.ViewModel(environment);
        this.vm.intent(new Intent().putExtra(FACEBOOK_TOKEN, "token"));
        this.vm.outputs.signupError().subscribe(this.signupError);
        this.koalaTest.assertValues(FACEBOOK_CONFIRM, USER_SIGNUP);
        this.vm.inputs.sendNewslettersClick(true);
        this.vm.inputs.createNewAccountClick();
        this.signupError.assertValue("oh no");
        this.koalaTest.assertValues(FACEBOOK_CONFIRM, USER_SIGNUP, SIGNUP_NEWSLETTER_TOGGLE, ERRORED_USER_SIGNUP);
    }

    @Test
    public void testSuccessfulUserCreation() {
        final ApiClientType apiClient = new MockApiClient();
        final Environment environment = environment().toBuilder().apiClient(apiClient).build();
        this.vm = new FacebookConfirmationViewModel.ViewModel(environment);
        this.vm.intent(new Intent().putExtra(FACEBOOK_TOKEN, "token"));
        this.vm.outputs.signupSuccess().subscribe(this.signupSuccess);
        this.vm.inputs.sendNewslettersClick(true);
        this.vm.inputs.createNewAccountClick();
        this.signupSuccess.assertValueCount(1);
        this.koalaTest.assertValues(FACEBOOK_CONFIRM, USER_SIGNUP, SIGNUP_NEWSLETTER_TOGGLE, LOGIN, NEW_USER);
    }

    @Test
    public void testToggleSendNewsLetter_isNotChecked() {
        final CurrentConfigType currentConfig = new MockCurrentConfig();
        currentConfig.config(ConfigFactory.config().toBuilder().countryCode("US").build());
        final Environment environment = environment().toBuilder().currentConfig(currentConfig).build();
        this.vm = new FacebookConfirmationViewModel.ViewModel(environment);
        this.vm.outputs.sendNewslettersIsChecked().subscribe(this.sendNewslettersIsChecked);
        this.sendNewslettersIsChecked.assertValue(false);
        this.vm.inputs.sendNewslettersClick(true);
        this.vm.inputs.sendNewslettersClick(false);
        this.sendNewslettersIsChecked.assertValues(false, true, false);
        this.koalaTest.assertValues(FACEBOOK_CONFIRM, USER_SIGNUP, SIGNUP_NEWSLETTER_TOGGLE, SIGNUP_NEWSLETTER_TOGGLE);
    }
}

