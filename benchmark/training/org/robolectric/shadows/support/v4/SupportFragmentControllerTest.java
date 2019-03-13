package org.robolectric.shadows.support.v4;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.R;
import org.robolectric.util.TestRunnerWithManifest;

import static org.robolectric.R.id.tacos;
import static org.robolectric.R.layout.fragment_contents;


@RunWith(TestRunnerWithManifest.class)
public class SupportFragmentControllerTest {
    private static final int VIEW_ID_CUSTOMIZED_LOGIN_ACTIVITY = 123;

    @Test
    public void initialNotAttached() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        SupportFragmentController.of(fragment);
        assertThat(getView()).isNull();
        assertThat(getActivity()).isNull();
        assertThat(isAdded()).isFalse();
    }

    @Test
    public void initialNotAttached_customActivity() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class);
        assertThat(getView()).isNull();
        assertThat(getActivity()).isNull();
        assertThat(isAdded()).isFalse();
    }

    @Test
    public void attachedAfterCreate() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        SupportFragmentController.of(fragment).create();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
    }

    @Test
    public void attachedAfterCreate_customActivity() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class).create();
        assertThat(getActivity()).isNotNull();
        assertThat(getActivity()).isInstanceOf(SupportFragmentControllerTest.LoginActivity.class);
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
    }

    @Test
    public void attachedAfterCreate_customizedViewId() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        SupportFragmentController.of(fragment, SupportFragmentControllerTest.CustomizedViewIdLoginActivity.class).create(SupportFragmentControllerTest.VIEW_ID_CUSTOMIZED_LOGIN_ACTIVITY, null).start();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void hasViewAfterStart() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        SupportFragmentController.of(fragment).create().start();
        assertThat(getView()).isNotNull();
    }

    @Test
    public void isResumed() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class).create().start().resume();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isTrue();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void isPaused() {
        final SupportFragmentControllerTest.LoginFragment fragment = Mockito.spy(new SupportFragmentControllerTest.LoginFragment());
        SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class).create().start().resume().pause();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
        onResume();
        onPause();
    }

    @Test
    public void isStopped() {
        final SupportFragmentControllerTest.LoginFragment fragment = Mockito.spy(new SupportFragmentControllerTest.LoginFragment());
        SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class).create().start().resume().pause().stop();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
        onStart();
        onResume();
        onPause();
        onStop();
    }

    @Test
    public void withIntent() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        Intent intent = new Intent("test_action");
        intent.putExtra("test_key", "test_value");
        SupportFragmentController<SupportFragmentControllerTest.LoginFragment> controller = SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class, intent).create();
        Intent intentInFragment = getActivity().getIntent();
        assertThat(intentInFragment.getAction()).isEqualTo("test_action");
        assertThat(intentInFragment.getExtras().getString("test_key")).isEqualTo("test_value");
    }

    @Test
    public void visible() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        final SupportFragmentController<SupportFragmentControllerTest.LoginFragment> controller = SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class);
        controller.create().start().resume();
        assertThat(isVisible()).isFalse();
        controller.visible();
        assertThat(isVisible()).isTrue();
    }

    @Test
    public void savesInstanceState() {
        final SupportFragmentControllerTest.LoginFragment fragment = new SupportFragmentControllerTest.LoginFragment();
        final SupportFragmentController<SupportFragmentControllerTest.LoginFragment> controller = SupportFragmentController.of(fragment, SupportFragmentControllerTest.LoginActivity.class);
        controller.create().start().resume().visible();
        SupportFragmentControllerTest.LoginActivity activity = ((SupportFragmentControllerTest.LoginActivity) (getActivity()));
        Bundle expectedState = new Bundle();
        expectedState.putBoolean("isRestored", true);
        activity.setState(expectedState);
        final Bundle savedInstanceState = new Bundle();
        controller.saveInstanceState(savedInstanceState);
        assertThat(savedInstanceState.getBoolean("isRestored")).isTrue();
    }

    public static class LoginFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(fragment_contents, container, false);
        }
    }

    public static class LoginActivity extends FragmentActivity {
        private Bundle state = new Bundle();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(1);
            setContentView(view);
        }

        @Override
        protected void onSaveInstanceState(Bundle savedInstanceState) {
            super.onSaveInstanceState(savedInstanceState);
            savedInstanceState.putAll(state);
        }

        public void setState(Bundle state) {
            this.state = state;
        }
    }

    public static class CustomizedViewIdLoginActivity extends FragmentActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(SupportFragmentControllerTest.VIEW_ID_CUSTOMIZED_LOGIN_ACTIVITY);
            setContentView(view);
        }
    }
}

