package com.firebase.ui.auth.viewmodel;


import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.FirebaseAuthAnonymousUpgradeException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.FakeAuthResult;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.util.data.ProviderUtils;
import com.firebase.ui.auth.viewmodel.idp.LinkingSocialProviderResponseHandler;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link LinkingSocialProviderResponseHandler}.
 * <p>
 * This handler is used by WelcomeBackIdpPrompt. This prompt handles the case where a user attempts
 * to log in with an IDP, but there is a user that has a different IDP with the same email.
 * <p>
 * In this case, the handler will link the provider to the existing firebase user. When the user
 * is anonymous, a triple linking case occurs.
 */
@RunWith(RobolectricTestRunner.class)
public class LinkingSocialProviderResponseHandlerTest {
    @Mock
    FirebaseAuth mMockAuth;

    @Mock
    FirebaseAuth mScratchMockAuth;

    @Mock
    FirebaseUser mMockUser;

    @Mock
    Observer<Resource<IdpResponse>> mResponseObserver;

    private LinkingSocialProviderResponseHandler mHandler;

    @Test
    public void testSignIn_withSameIdp_expectSuccess() {
        mHandler.getOperation().observeForever(mResponseObserver);
        // Fake social response from Google
        IdpResponse response = setToken(TestConstants.TOKEN).build();
        Mockito.when(mMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth).signInWithCredential(ArgumentMatchers.any(GoogleAuthCredential.class));
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSignIn_anonymousUpgradeEnabledWithSameIdp_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        // Fake social response from Google
        IdpResponse response = setToken(TestConstants.TOKEN).build();
        mHandler.startSignIn(response);
        // Since we are signing in with the same IDP and anonymous upgrade is enabled, a merge
        // failure should occur without any RPC calls
        AuthCredential credential = GoogleAuthProvider.getCredential(TestConstants.TOKEN, null);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResponseObserver).onChanged(resolveCaptor.capture());
        FirebaseAuthAnonymousUpgradeException e = ((FirebaseAuthAnonymousUpgradeException) (resolveCaptor.getValue().getException()));
        GoogleAuthCredential responseCredential = ((GoogleAuthCredential) (e.getResponse().getCredentialForLinking()));
        assertThat(responseCredential.getProvider()).isEqualTo(credential.getProvider());
        assertThat(responseCredential.getSignInMethod()).isEqualTo(credential.getSignInMethod());
    }

    @Test
    public void testSignIn_withDifferentIdp_expectSuccess() {
        mHandler.getOperation().observeForever(mResponseObserver);
        // We're going to fake a sign in with facebook, where the email belongs
        // to an existing account with a Google provider.
        // Fake social response from Google
        IdpResponse response = setToken(TestConstants.TOKEN).build();
        // Set facebook credential
        AuthCredential facebookAuthCredential = FacebookAuthProvider.getCredential(TestConstants.TOKEN);
        mHandler.setRequestedSignInCredentialForEmail(facebookAuthCredential, TestConstants.EMAIL);
        // mock sign in with Google credential to always work
        Mockito.when(mMockAuth.signInWithCredential(ArgumentMatchers.any(GoogleAuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        // Mock linking with Facebook to always work
        Mockito.when(FakeAuthResult.INSTANCE.getUser().linkWithCredential(facebookAuthCredential)).thenReturn(new com.firebase.ui.auth.testhelpers.AutoContinueTask(FakeAuthResult.INSTANCE, FakeAuthResult.INSTANCE, true, null));
        mHandler.startSignIn(response);
        Mockito.verify(mMockAuth).signInWithCredential(ArgumentMatchers.any(GoogleAuthCredential.class));
        Mockito.verify(FakeAuthResult.INSTANCE.getUser()).linkWithCredential(facebookAuthCredential);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSignIn_anonymousUpgradeEnabledWithDifferentIdp_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        // We're going to fake a sign in with facebook, where the email belongs
        // to an existing account with a Google provider.
        // We need to link Facebook to this account, and then a merge failure should occur
        // so that the developer can handle it.
        // Before we can link, they need to sign in with Google to prove they own the account.
        // Fake social response from Google
        IdpResponse response = setToken(TestConstants.TOKEN).build();
        // Set facebook credential
        AuthCredential facebookAuthCredential = FacebookAuthProvider.getCredential(TestConstants.TOKEN);
        mHandler.setRequestedSignInCredentialForEmail(facebookAuthCredential, TestConstants.EMAIL);
        Mockito.when(mScratchMockAuth.signInWithCredential(ArgumentMatchers.any(GoogleAuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        // Mock linking with Facebook to always work
        Mockito.when(FakeAuthResult.INSTANCE.getUser().linkWithCredential(facebookAuthCredential)).thenReturn(new com.firebase.ui.auth.testhelpers.AutoContinueTask(FakeAuthResult.INSTANCE, FakeAuthResult.INSTANCE, true, null));
        mHandler.startSignIn(response);
        Mockito.verify(mScratchMockAuth).signInWithCredential(ArgumentMatchers.any(GoogleAuthCredential.class));
        Mockito.verify(FakeAuthResult.INSTANCE.getUser()).linkWithCredential(facebookAuthCredential);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResponseObserver).onChanged(resolveCaptor.capture());
        // Merge failure should occur after successful linking
        FirebaseAuthAnonymousUpgradeException e = ((FirebaseAuthAnonymousUpgradeException) (resolveCaptor.getValue().getException()));
        AuthCredential credential = ProviderUtils.getAuthCredential(response);
        GoogleAuthCredential responseCredential = ((GoogleAuthCredential) (e.getResponse().getCredentialForLinking()));
        assertThat(responseCredential.getProvider()).isEqualTo(credential.getProvider());
        assertThat(responseCredential.getSignInMethod()).isEqualTo(credential.getSignInMethod());
    }
}

