package com.firebase.ui.auth.viewmodel;


import RuntimeEnvironment.application;
import State.FAILURE;
import State.SUCCESS;
import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.util.data.EmailLinkPersistenceManager;
import com.firebase.ui.auth.viewmodel.email.EmailLinkSendEmailHandler;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link EmailLinkSendEmailHandler}.
 */
@RunWith(RobolectricTestRunner.class)
public class EmailLinkSendEmailHandlerTest {
    private static String URL = "https://google.com";

    private EmailLinkPersistenceManager mPersistenceManager;

    private EmailLinkSendEmailHandler mHandler;

    @Mock
    private FirebaseAuth mMockAuth;

    @Mock
    private Observer<Resource<String>> mResponseObserver;

    @Test
    @SuppressWarnings("unchecked")
    public void testSendSignInLinkToEmail_normalFlow_expectSuccess() {
        mHandler.getOperation().observeForever(mResponseObserver);
        boolean forceSameDevice = false;
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder().setUrl(EmailLinkSendEmailHandlerTest.URL).build();
        Mockito.when(mMockAuth.sendSignInLinkToEmail(ArgumentMatchers.any(String.class), ArgumentMatchers.any(ActionCodeSettings.class))).thenReturn(AutoCompleteTask.<Void>forSuccess(null));
        mHandler.sendSignInLinkToEmail(TestConstants.EMAIL, actionCodeSettings, null, forceSameDevice);
        ArgumentCaptor<ActionCodeSettings> acsCaptor = ArgumentCaptor.forClass(ActionCodeSettings.class);
        Mockito.verify(mMockAuth).sendSignInLinkToEmail(ArgumentMatchers.eq(TestConstants.EMAIL), acsCaptor.capture());
        assertThat(acsCaptor.getValue()).isNotEqualTo(actionCodeSettings);
        validateSessionIdAddedToContinueUrl(acsCaptor.getValue(), null, forceSameDevice);
        String email = mPersistenceManager.retrieveSessionRecord(application).getEmail();
        assertThat(email).isNotNull();
        assertThat(email).isEqualTo(TestConstants.EMAIL);
        ArgumentCaptor<Resource<String>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<String>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getState()).isEqualTo(SUCCESS);
        assertThat(captor.getValue().getValue()).isEqualTo(TestConstants.EMAIL);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendSignInLinkToEmail_normalFlow_expectFailure() {
        mHandler.getOperation().observeForever(mResponseObserver);
        boolean forceSameDevice = true;
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder().setUrl(EmailLinkSendEmailHandlerTest.URL).build();
        Mockito.when(mMockAuth.sendSignInLinkToEmail(ArgumentMatchers.any(String.class), ArgumentMatchers.any(ActionCodeSettings.class))).thenReturn(AutoCompleteTask.<Void>forFailure(new Exception()));
        mHandler.sendSignInLinkToEmail(TestConstants.EMAIL, actionCodeSettings, null, forceSameDevice);
        ArgumentCaptor<ActionCodeSettings> acsCaptor = ArgumentCaptor.forClass(ActionCodeSettings.class);
        Mockito.verify(mMockAuth).sendSignInLinkToEmail(ArgumentMatchers.eq(TestConstants.EMAIL), acsCaptor.capture());
        assertThat(acsCaptor.getValue()).isNotEqualTo(actionCodeSettings);
        validateSessionIdAddedToContinueUrl(acsCaptor.getValue(), null, forceSameDevice);
        ArgumentCaptor<Resource<String>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<String>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getState()).isEqualTo(FAILURE);
        assertThat(captor.getValue().getException()).isNotNull();
    }

    @Test
    public void testSendSignInLinkToEmail_linkingFlow_expectSuccess() {
        mHandler.getOperation().observeForever(mResponseObserver);
        boolean forceSameDevice = true;
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder().setUrl(EmailLinkSendEmailHandlerTest.URL).build();
        Mockito.when(mMockAuth.sendSignInLinkToEmail(ArgumentMatchers.any(String.class), ArgumentMatchers.any(ActionCodeSettings.class))).thenReturn(AutoCompleteTask.<Void>forFailure(new Exception()));
        IdpResponse idpResponseForLinking = buildFacebookIdpResponseForLinking();
        mHandler.sendSignInLinkToEmail(TestConstants.EMAIL, actionCodeSettings, idpResponseForLinking, forceSameDevice);
        ArgumentCaptor<ActionCodeSettings> acsCaptor = ArgumentCaptor.forClass(ActionCodeSettings.class);
        Mockito.verify(mMockAuth).sendSignInLinkToEmail(ArgumentMatchers.eq(TestConstants.EMAIL), acsCaptor.capture());
        validateSessionIdAddedToContinueUrl(acsCaptor.getValue(), idpResponseForLinking.getProviderType(), forceSameDevice);
        ArgumentCaptor<Resource<String>> captor = ArgumentCaptor.forClass(Resource.class);
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<String>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getState()).isEqualTo(FAILURE);
        assertThat(captor.getValue().getException()).isNotNull();
    }
}

