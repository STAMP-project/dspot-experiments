package com.firebase.ui.auth.viewmodel;


import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.viewmodel.email.RecoverPasswordHandler;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link RecoverPasswordHandler}.
 */
@RunWith(RobolectricTestRunner.class)
public class RecoverPasswordHandlerTest {
    @Mock
    FirebaseAuth mMockAuth;

    @Mock
    Observer<Resource<String>> mObserver;

    private RecoverPasswordHandler mHandler;

    @Test
    public void testReset_sendsRecoverEmail() {
        // Send password email succeeds
        Mockito.when(mMockAuth.sendPasswordResetEmail(TestConstants.EMAIL)).thenReturn(AutoCompleteTask.<Void>forSuccess(null));
        // Begin observation, then send the email
        mHandler.getOperation().observeForever(mObserver);
        mHandler.startReset(TestConstants.EMAIL);
        // Should get in-progress resource
        Mockito.verify(mObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<String>isLoading()));
        // Firebase auth should be called
        Mockito.verify(mMockAuth).sendPasswordResetEmail(TestConstants.EMAIL);
        // Should get the success resource
        Mockito.verify(mObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<String>isSuccess()));
    }

    @Test
    public void testReset_propagatesFailure() {
        // Send password email fails
        Mockito.when(mMockAuth.sendPasswordResetEmail(TestConstants.EMAIL)).thenReturn(AutoCompleteTask.<Void>forFailure(new Exception("FAILED")));
        // Begin observation, then send the email
        mHandler.getOperation().observeForever(mObserver);
        mHandler.startReset(TestConstants.EMAIL);
        // Should get in-progress resource
        Mockito.verify(mObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<String>isLoading()));
        // Firebase auth should be called
        Mockito.verify(mMockAuth).sendPasswordResetEmail(TestConstants.EMAIL);
        // Should get the success resource
        Mockito.verify(mObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<String>isFailure()));
    }
}

