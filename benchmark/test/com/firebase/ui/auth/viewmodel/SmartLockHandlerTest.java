package com.firebase.ui.auth.viewmodel;


import Activity.RESULT_OK;
import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.PendingIntentRequiredException;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.testhelpers.TestHelper;
import com.firebase.ui.auth.viewmodel.smartlock.SmartLockHandler;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.common.api.ResolvableApiException;
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
 * Unit tests for {@link SmartLockHandler}.
 */
@RunWith(RobolectricTestRunner.class)
public class SmartLockHandlerTest {
    @Mock
    FirebaseAuth mMockAuth;

    @Mock
    CredentialsClient mMockCredentials;

    @Mock
    Observer<Resource<IdpResponse>> mResultObserver;

    private SmartLockHandler mHandler;

    @Test
    public void testSaveCredentials_success() {
        mHandler.getOperation().observeForever(mResultObserver);
        Mockito.when(mMockCredentials.save(ArgumentMatchers.any(Credential.class))).thenReturn(AutoCompleteTask.<Void>forSuccess(null));
        mHandler.saveCredentials(TestHelper.getMockFirebaseUser(), TestConstants.PASSWORD, null);
        Mockito.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        Mockito.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSaveCredentials_resolution() {
        mHandler.getOperation().observeForever(mResultObserver);
        // Mock credentials to throw an RAE
        ResolvableApiException mockRae = Mockito.mock(ResolvableApiException.class);
        Mockito.when(mMockCredentials.save(ArgumentMatchers.any(Credential.class))).thenReturn(AutoCompleteTask.<Void>forFailure(mockRae));
        // Kick off save
        mHandler.saveCredentials(TestHelper.getMockFirebaseUser(), TestConstants.PASSWORD, null);
        InOrder inOrder = Mockito.inOrder(mResultObserver);
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        // Make sure we get a resolution
        ArgumentCaptor<Resource<IdpResponse>> resolveCaptor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResultObserver).onChanged(resolveCaptor.capture());
        // Call activity result
        PendingIntentRequiredException e = ((PendingIntentRequiredException) (resolveCaptor.getValue().getException()));
        mHandler.onActivityResult(e.getRequestCode(), RESULT_OK);
        // Make sure we get success
        inOrder.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isSuccess()));
    }

    @Test
    public void testSaveCredentials_failure() {
        mHandler.getOperation().observeForever(mResultObserver);
        Mockito.when(mMockCredentials.save(ArgumentMatchers.any(Credential.class))).thenReturn(AutoCompleteTask.<Void>forFailure(new Exception("FAILED")));
        mHandler.saveCredentials(TestHelper.getMockFirebaseUser(), TestConstants.PASSWORD, null);
        Mockito.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        Mockito.verify(mResultObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isFailure()));
    }
}

