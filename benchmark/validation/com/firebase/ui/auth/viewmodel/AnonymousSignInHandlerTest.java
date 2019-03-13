package com.firebase.ui.auth.viewmodel;


import State.SUCCESS;
import android.arch.lifecycle.Observer;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.Resource;
import com.firebase.ui.auth.data.remote.AnonymousSignInHandler;
import com.firebase.ui.auth.testhelpers.AutoCompleteTask;
import com.firebase.ui.auth.testhelpers.FakeAuthResult;
import com.firebase.ui.auth.testhelpers.ResourceMatchers;
import com.google.firebase.auth.AuthResult;
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
 * Unit tests for {@link AnonymousSignInHandler}.
 */
@RunWith(RobolectricTestRunner.class)
public class AnonymousSignInHandlerTest {
    @Mock
    FirebaseAuth mMockAuth;

    AnonymousSignInHandler mHandler;

    @Mock
    Observer<Resource<IdpResponse>> mResponseObserver;

    @Test
    public void testStartSignIn_expectSuccess() {
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.signInAnonymously()).thenReturn(AutoCompleteTask.forSuccess(FakeAuthResult.INSTANCE));
        mHandler.startSignIn(null);
        Mockito.verify(mMockAuth).signInAnonymously();
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        ArgumentCaptor<Resource<IdpResponse>> captor = ArgumentCaptor.forClass(Resource.class);
        inOrder.verify(mResponseObserver).onChanged(captor.capture());
        assertThat(captor.getValue().getState()).isEqualTo(SUCCESS);
        IdpResponse response = captor.getValue().getValue();
        assertThat(response.isNewUser()).isFalse();
    }

    @Test
    public void testStartSignIn_expectFailure() {
        mHandler.getOperation().observeForever(mResponseObserver);
        Mockito.when(mMockAuth.signInAnonymously()).thenReturn(AutoCompleteTask.<AuthResult>forFailure(new Exception("FAILED")));
        mHandler.startSignIn(null);
        Mockito.verify(mMockAuth).signInAnonymously();
        InOrder inOrder = Mockito.inOrder(mResponseObserver);
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isLoading()));
        inOrder.verify(mResponseObserver).onChanged(ArgumentMatchers.argThat(ResourceMatchers.<IdpResponse>isFailure()));
    }
}

