package com.firebase.ui.auth.viewmodel;


import Activity.RESULT_OK;
import AuthUI.SUPPORTED_PROVIDERS;
import EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD;
import FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD;
import GithubAuthProvider.PROVIDER_ID;
import GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD;
import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.FirebaseAuthAnonymousUpgradeException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.IntentRequiredException;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.FakeAuthResult;
import com.firebase.ui.auth.testhelpers.FakeSignInMethodQueryResult;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.ui.email.WelcomeBackPasswordPrompt;
import com.firebase.ui.auth.ui.idp.WelcomeBackIdpPrompt;
import com.firebase.ui.auth.viewmodel.idp.SocialProviderResponseHandler;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link SmartLockHandler}.
 */
@RunWith(RobolectricTestRunner.class)
public class SocialProviderResponseHandlerTest {
    @Mock
    FirebaseAuth mMockAuth;

    @Mock
    FirebaseUser mUser;

    @Mock
    Observer<Resource<IdpResponse>> mResultObserver;

    private SocialProviderResponseHandler mHandler;

    private static final ArrayList<String> NON_GITHUB_PROVIDERS = new ArrayList<>();

    static {
        SocialProviderResponseHandlerTest.NON_GITHUB_PROVIDERS.addAll(SUPPORTED_PROVIDERS);
        SocialProviderResponseHandlerTest.NON_GITHUB_PROVIDERS.remove(PROVIDER_ID);
    }

    @Test
    public void testSignInIdp_success() {
        mHandler.getOperation().observeForever(mResultObserver);
        Mockito.when(mMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        IdpResponse response = build();
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth).signInWithCredential(ArgumentMatchers.any(AuthCredential.class));
        InOrder inOrder = Mockito.inOrder(mResultObserver);
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test(expected = IllegalStateException.class)
    public void testSignInNonIdp_failure() {
        mHandler.getOperation().observeForever(mResultObserver);
        IdpResponse response = build();
        mHandler.startSignIn(response);
    }

    @Test
    public void testSignInResponse_failure() {
        mHandler.getOperation().observeForever(mResultObserver);
        IdpResponse response = IdpResponse.from(new Exception("Failure"));
        mHandler.startSignIn(response);
        Mockito.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isFailure()));
    }

    @Test
    public void testSignInIdp_resolution() {
        mHandler.getOperation().observeForever(mResultObserver);
        Mockito.when(mMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.<AuthResult>forFailure(new FirebaseAuthUserCollisionException("foo", "bar")));
        Mockito.when(mMockAuth.fetchSignInMethodsForEmail(ArgumentMatchers.any(String.class))).thenReturn(AutoCompleteTask.<SignInMethodQueryResult>forSuccess(new FakeSignInMethodQueryResult(Collections.singletonList(FACEBOOK_SIGN_IN_METHOD))));
        IdpResponse response = build();
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth).signInWithCredential(ArgumentMatchers.any(AuthCredential.class));
        Mockito.verify(mMockAuth).fetchSignInMethodsForEmail(ArgumentMatchers.any(String.class));
        InOrder inOrder = Mockito.inOrder(mResultObserver);
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResultObserver).onChanged(resolveCaptor.capture());
        // Call activity result
        IntentRequiredException e = ((IntentRequiredException) (resolveCaptor.getValue().getException()));
        mHandler.onActivityResult(e.getRequestCode(), RESULT_OK, response.toIntent());
        // Make sure we get success
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSignInIdp_anonymousUserUpgradeEnabledAndNewUser_expectSuccess() {
        mHandler.getOperation().observeForever(mResultObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        IdpResponse response = build();
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth.getCurrentUser()).linkWithCredential(ArgumentMatchers.any(AuthCredential.class));
        InOrder inOrder = Mockito.inOrder(mResultObserver);
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSignInIdp_anonymousUserUpgradeEnabledAndExistingUserWithSameIdp_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResultObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.<AuthResult>forFailure(new FirebaseAuthUserCollisionException("foo", "bar")));
        // Case 1: Anon user signing in with a Google credential that belongs to an existing user.
        Mockito.when(mMockAuth.fetchSignInMethodsForEmail(ArgumentMatchers.any(String.class))).thenReturn(AutoCompleteTask.<SignInMethodQueryResult>forSuccess(new FakeSignInMethodQueryResult(Arrays.asList(GOOGLE_SIGN_IN_METHOD, FACEBOOK_SIGN_IN_METHOD))));
        IdpResponse response = build();
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth.getCurrentUser()).linkWithCredential(ArgumentMatchers.any(AuthCredential.class));
        InOrder inOrder = Mockito.inOrder(mResultObserver);
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResultObserver).onChanged(resolveCaptor.capture());
        FirebaseAuthAnonymousUpgradeException e = ((FirebaseAuthAnonymousUpgradeException) (resolveCaptor.getValue().getException()));
        assertThat(e.getResponse().getCredentialForLinking()).isNotNull();
    }

    @Test
    public void testSignInIdp_anonymousUserUpgradeEnabledAndExistingIdpUserWithDifferentIdp_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResultObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.<AuthResult>forFailure(new FirebaseAuthUserCollisionException("foo", "bar")));
        // Case 2 & 3: trying to link with an account that has 1 idp, which is different from the
        // one that we're trying to log in with
        Mockito.when(mMockAuth.fetchSignInMethodsForEmail(ArgumentMatchers.any(String.class))).thenReturn(AutoCompleteTask.<SignInMethodQueryResult>forSuccess(new FakeSignInMethodQueryResult(Collections.singletonList(FACEBOOK_SIGN_IN_METHOD))));
        IdpResponse response = build();
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth.getCurrentUser()).linkWithCredential(ArgumentMatchers.any(AuthCredential.class));
        InOrder inOrder = Mockito.inOrder(mResultObserver);
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResultObserver).onChanged(resolveCaptor.capture());
        // Make sure that we are trying to start the WelcomeBackIdpPrompt activity
        IntentRequiredException e = ((IntentRequiredException) (resolveCaptor.getValue().getException()));
        assertThat(e.getIntent().getComponent().getClassName()).isEqualTo(WelcomeBackIdpPrompt.class.toString().split(" ")[1]);
        assertThat(IdpResponse.fromResultIntent(e.getIntent())).isEqualTo(response);
    }

    @Test
    public void testSignInIdp_anonymousUserUpgradeEnabledAndExistingPasswordUserWithDifferentIdp_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResultObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.<AuthResult>forFailure(new FirebaseAuthUserCollisionException("foo", "bar")));
        // Case 2 & 3: trying to link with an account that has 1 password provider and logging in
        // with an idp that has the same email
        Mockito.when(mMockAuth.fetchSignInMethodsForEmail(ArgumentMatchers.any(String.class))).thenReturn(AutoCompleteTask.<SignInMethodQueryResult>forSuccess(new FakeSignInMethodQueryResult(Collections.singletonList(EMAIL_PASSWORD_SIGN_IN_METHOD))));
        IdpResponse response = build();
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth.getCurrentUser()).linkWithCredential(ArgumentMatchers.any(AuthCredential.class));
        InOrder inOrder = Mockito.inOrder(mResultObserver);
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResultObserver).onChanged(resolveCaptor.capture());
        // Make sure that we are trying to start the WelcomeBackIdpPrompt activity
        IntentRequiredException e = ((IntentRequiredException) (resolveCaptor.getValue().getException()));
        assertThat(e.getIntent().getComponent().getClassName()).isEqualTo(WelcomeBackPasswordPrompt.class.toString().split(" ")[1]);
        assertThat(IdpResponse.fromResultIntent(e.getIntent())).isEqualTo(response);
    }
}

