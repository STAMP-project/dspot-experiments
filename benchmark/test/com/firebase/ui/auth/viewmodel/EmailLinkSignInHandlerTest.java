package com.firebase.ui.auth.viewmodel;


import AuthUI.EMAIL_LINK_PROVIDER;
import EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD;
import ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR;
import ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR;
import ErrorCodes.INVALID_EMAIL_LINK_ERROR;
import FacebookAuthProvider.PROVIDER_ID;
import RuntimeEnvironment.application;
import State.SUCCESS;
import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.FirebaseAuthAnonymousUpgradeException;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.AutoContinueTask;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.util.data.AuthOperationManager;
import com.firebase.ui.auth.util.data.EmailLinkPersistenceManager;
import com.firebase.ui.auth.util.data.SessionUtils;
import com.firebase.ui.auth.viewmodel.email.EmailLinkSignInHandler;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link EmailLinkSignInHandler}.
 */
@RunWith(RobolectricTestRunner.class)
public class EmailLinkSignInHandlerTest {
    private static final String EMAIL_LINK = "https://fake.com/__/auth/action?apiKey=apiKey&mode=signIn" + ("&oobCode=oobCode" + "&continueUrl=");

    private static final String CONTINUE_URL = "https://google.com";

    private EmailLinkSignInHandler mHandler;

    private EmailLinkPersistenceManager mPersistenceManager;

    @Mock
    private FirebaseAuth mMockAuth;

    @Mock
    private FirebaseAuth mScratchMockAuth;

    @Mock
    private FirebaseUser mMockAnonUser;

    @Mock
    private Observer<Resource<IdpResponse>> mResponseObserver;

    @Mock
    private AuthResult mMockAuthResult;

    @Mock
    private AdditionalUserInfo mockAdditionalUserInfo;

    @Mock
    private ActionCodeResult mMockActionCodeResult;

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_differentDeviceLinkWithForceSameDeviceTrue_expectWrongDeviceError() {
        String differentSessionId = SessionUtils.generateRandomAlphaNumericString(10);
        initializeHandlerWithSessionInfo(differentSessionId, null, null, true);
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mHandler.startSignIn();
        Mockito.verify(mMockAuth).isSignInWithEmailLink(ArgumentMatchers.any(String.class));
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        FirebaseUiException exception = ((FirebaseUiException) (captor.getValue().getException()));
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(EMAIL_LINK_WRONG_DEVICE_ERROR);
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_differentDeviceLinkWithNoSessionId_expectInvalidLinkError() {
        initializeHandlerWithSessionInfo(null, null, null, true);
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mHandler.startSignIn();
        Mockito.verify(mMockAuth).isSignInWithEmailLink(ArgumentMatchers.any(String.class));
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        FirebaseUiException exception = ((FirebaseUiException) (captor.getValue().getException()));
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_EMAIL_LINK_ERROR);
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_differentDeviceLinkWithValidSessionInfo_expectPromptForEmailError() {
        String differentSessionId = SessionUtils.generateRandomAlphaNumericString(10);
        initializeHandlerWithSessionInfo(differentSessionId, null, null, false);
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        Mockito.when(mMockAuth.checkActionCode(ArgumentMatchers.any(String.class))).thenReturn(AutoCompleteTask.forSuccess(mMockActionCodeResult));
        mHandler.startSignIn();
        Mockito.verify(mMockAuth).isSignInWithEmailLink(ArgumentMatchers.any(String.class));
        Mockito.verify(mMockAuth).checkActionCode(ArgumentMatchers.any(String.class));
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        FirebaseUiException exception = ((FirebaseUiException) (captor.getValue().getException()));
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR);
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_differentDeviceLinkWithValidSessionInfo_expectInvalidLinkError() {
        String differentSessionId = SessionUtils.generateRandomAlphaNumericString(10);
        initializeHandlerWithSessionInfo(differentSessionId, null, null, false);
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        Mockito.when(mMockAuth.checkActionCode(ArgumentMatchers.any(String.class))).thenReturn(AutoCompleteTask.<ActionCodeResult>forFailure(new Exception("foo")));
        mHandler.startSignIn();
        Mockito.verify(mMockAuth).isSignInWithEmailLink(ArgumentMatchers.any(String.class));
        Mockito.verify(mMockAuth).checkActionCode(ArgumentMatchers.any(String.class));
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        FirebaseUiException exception = ((FirebaseUiException) (captor.getValue().getException()));
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_EMAIL_LINK_ERROR);
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_differentDeviceLinkWithAnonymousUpgradeEnabled_expectInvalidLinkError() {
        String differentSessionId = SessionUtils.generateRandomAlphaNumericString(10);
        String anonUserId = SessionUtils.generateRandomAlphaNumericString(10);
        initializeHandlerWithSessionInfo(differentSessionId, anonUserId, null, false);
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mHandler.startSignIn();
        Mockito.verify(mMockAuth).isSignInWithEmailLink(ArgumentMatchers.any(String.class));
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        FirebaseUiException exception = ((FirebaseUiException) (captor.getValue().getException()));
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(EMAIL_LINK_WRONG_DEVICE_ERROR);
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_invalidLink_expectInvalidLinkError() {
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(false);
        mHandler.startSignIn();
        Mockito.verify(mMockAuth).isSignInWithEmailLink(ArgumentMatchers.any(String.class));
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        FirebaseUiException exception = ((FirebaseUiException) (captor.getValue().getException()));
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_EMAIL_LINK_ERROR);
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_normalFlow() {
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        Mockito.when(mMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(mMockAuthResult));
        mHandler.startSignIn();
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        IdpResponse response = captor.getValue().getValue();
        assertThat(response.getUser().getProviderId()).isEqualTo(EMAIL_LINK_PROVIDER);
        assertThat(response.getUser().getEmail()).isEqualTo(mMockAuthResult.getUser().getEmail());
        assertThat(response.getUser().getName()).isEqualTo(mMockAuthResult.getUser().getDisplayName());
        assertThat(response.getUser().getPhotoUri()).isEqualTo(mMockAuthResult.getUser().getPhotoUrl());
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_normalFlowWithAnonymousUpgrade_expectSuccessfulMerge() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(new AutoContinueTask(mMockAuthResult, mMockAuthResult, true, null));
        mHandler.startSignIn();
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        IdpResponse response = captor.getValue().getValue();
        assertThat(response.getUser().getProviderId()).isEqualTo(EMAIL_LINK_PROVIDER);
        assertThat(response.getUser().getEmail()).isEqualTo(mMockAuthResult.getUser().getEmail());
        assertThat(response.getUser().getName()).isEqualTo(mMockAuthResult.getUser().getDisplayName());
        assertThat(response.getUser().getPhotoUri()).isEqualTo(mMockAuthResult.getUser().getPhotoUrl());
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_normalFlowWithAnonymousUpgrade_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        Mockito.when(mMockAuth.getCurrentUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.<AuthResult>forFailure(new FirebaseAuthUserCollisionException("foo", "bar")));
        mHandler.startSignIn();
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getException()).isNotNull();
        FirebaseAuthAnonymousUpgradeException mergeException = ((FirebaseAuthAnonymousUpgradeException) (captor.getValue().getException()));
        assertThat(mergeException.getResponse().getCredentialForLinking()).isNotNull();
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_linkingFlow_expectSuccessfulLink() {
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        mPersistenceManager.saveIdpResponseForLinking(application, buildFacebookIdpResponse());
        Mockito.when(mMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(mMockAuthResult));
        // Mock linking with Facebook to always work
        Mockito.when(mMockAuthResult.getUser().linkWithCredential(ArgumentMatchers.any(FacebookAuthCredential.class))).thenReturn(new AutoContinueTask(mMockAuthResult, mMockAuthResult, true, null));
        mHandler.startSignIn();
        // Validate regular sign in
        ArgumentCaptor<EmailAuthCredential> credentialCaptor = ArgumentCaptor.forClass(EmailAuthCredential.class);
        Mockito.verify(mMockAuth).signInWithCredential(credentialCaptor.capture());
        assertThat(credentialCaptor.getValue().getEmail()).isEqualTo(TestConstants.EMAIL);
        assertThat(credentialCaptor.getValue().getSignInMethod()).isEqualTo(EMAIL_LINK_SIGN_IN_METHOD);
        // Validate linking was called
        Mockito.verify(mMockAuthResult.getUser()).linkWithCredential(ArgumentMatchers.any(FacebookAuthCredential.class));
        // Validate that the data was cleared
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
        // Validate IdpResponse
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        IdpResponse response = captor.getValue().getValue();
        assertThat(captor.getValue().getState()).isEqualTo(SUCCESS);
        assertThat(response.getUser().getProviderId()).isEqualTo(EMAIL_LINK_PROVIDER);
        assertThat(response.getUser().getEmail()).isEqualTo(mMockAuthResult.getUser().getEmail());
        assertThat(response.getUser().getName()).isEqualTo(mMockAuthResult.getUser().getDisplayName());
        assertThat(response.getUser().getPhotoUri()).isEqualTo(mMockAuthResult.getUser().getPhotoUrl());
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_linkingFlow_expectUserCollisionException() {
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        mPersistenceManager.saveIdpResponseForLinking(application, buildFacebookIdpResponse());
        Mockito.when(mMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(mMockAuthResult));
        // Mock linking with Facebook to always work
        Mockito.when(mMockAuthResult.getUser().linkWithCredential(ArgumentMatchers.any(FacebookAuthCredential.class))).thenReturn(AutoCompleteTask.<AuthResult>forFailure(new FirebaseAuthUserCollisionException("foo", "bar")));
        mHandler.startSignIn();
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getException()).isNotNull();
        FirebaseAuthUserCollisionException collisionException = ((FirebaseAuthUserCollisionException) (captor.getValue().getException()));
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_linkingFlowWithAnonymousUpgradeEnabled_expectMergeFailure() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        mPersistenceManager.saveIdpResponseForLinking(application, buildFacebookIdpResponse());
        // Need to control FirebaseAuth's return values
        AuthOperationManager authOperationManager = AuthOperationManager.getInstance();
        authOperationManager.mScratchAuth = mScratchMockAuth;
        Mockito.when(mScratchMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(mMockAuthResult));
        // Mock linking with Facebook to always work
        Mockito.when(mMockAuthResult.getUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(new AutoContinueTask(mMockAuthResult, mMockAuthResult, true, null));
        mHandler.startSignIn();
        // Validate regular sign in
        ArgumentCaptor<EmailAuthCredential> credentialCaptor = ArgumentCaptor.forClass(EmailAuthCredential.class);
        Mockito.verify(mScratchMockAuth).signInWithCredential(credentialCaptor.capture());
        assertThat(credentialCaptor.getValue().getEmail()).isEqualTo(TestConstants.EMAIL);
        assertThat(credentialCaptor.getValue().getSignInMethod()).isEqualTo(EMAIL_LINK_SIGN_IN_METHOD);
        // Validate linking was called
        Mockito.verify(mMockAuthResult.getUser()).linkWithCredential(ArgumentMatchers.any(FacebookAuthCredential.class));
        // Validate that the data was cleared
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
        // Validate IdpResponse
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        FirebaseAuthAnonymousUpgradeException mergeException = ((FirebaseAuthAnonymousUpgradeException) (captor.getValue().getException()));
        IdpResponse response = mergeException.getResponse();
        assertThat(response.getCredentialForLinking()).isNotNull();
        assertThat(response.getCredentialForLinking().getProvider()).isEqualTo(PROVIDER_ID);
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_linkingFlowWithAnonymousUpgradeEnabled_failedSignInPropagated() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        mPersistenceManager.saveIdpResponseForLinking(application, buildFacebookIdpResponse());
        // Need to control FirebaseAuth's return values
        AuthOperationManager authOperationManager = AuthOperationManager.getInstance();
        authOperationManager.mScratchAuth = mScratchMockAuth;
        Mockito.when(mScratchMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoContinueTask.<AuthResult>forFailure(new Exception("FAILED")));
        mHandler.startSignIn();
        // Verify sign in was called
        Mockito.verify(mScratchMockAuth).signInWithCredential(ArgumentMatchers.any(AuthCredential.class));
        // Validate that the data was cleared
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
        // Validate failure
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getException()).isNotNull();
    }

    @Test
    @SuppressWarnings("all")
    public void testStartSignIn_linkingFlowWithAnonymousUpgradeEnabled_failedLinkPropagated() {
        mHandler.getOperation().observeForever(mResponseObserver);
        setupAnonymousUpgrade();
        Mockito.when(mMockAuth.isSignInWithEmailLink(ArgumentMatchers.any(String.class))).thenReturn(true);
        mPersistenceManager.saveEmail(application, TestConstants.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        mPersistenceManager.saveIdpResponseForLinking(application, buildFacebookIdpResponse());
        // Need to control FirebaseAuth's return values
        AuthOperationManager authOperationManager = AuthOperationManager.getInstance();
        authOperationManager.mScratchAuth = mScratchMockAuth;
        Mockito.when(mScratchMockAuth.signInWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoCompleteTask.forSuccess(mMockAuthResult));
        // Mock linking with Facebook to always work
        Mockito.when(mMockAuthResult.getUser().linkWithCredential(ArgumentMatchers.any(AuthCredential.class))).thenReturn(AutoContinueTask.<AuthResult>forFailure(new Exception("FAILED")));
        mHandler.startSignIn();
        // Verify sign in was called
        Mockito.verify(mScratchMockAuth).signInWithCredential(ArgumentMatchers.any(AuthCredential.class));
        // Validate linking was called
        Mockito.verify(mMockAuthResult.getUser()).linkWithCredential(ArgumentMatchers.any(FacebookAuthCredential.class));
        // Validate that the data was cleared
        assertThat(mPersistenceManager.retrieveSessionRecord(application)).isNull();
        // Validate failure
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getException()).isNotNull();
    }
}

