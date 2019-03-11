package org.robolectric.android.controller;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.R;

import static org.robolectric.R.id.tacos;
import static org.robolectric.R.layout.fragment_contents;


@RunWith(AndroidJUnit4.class)
public class FragmentControllerTest {
    private static final int VIEW_ID_CUSTOMIZED_LOGIN_ACTIVITY = 123;

    @Test
    public void initialNotAttached() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        FragmentController.of(fragment);
        assertThat(getView()).isNull();
        assertThat(getActivity()).isNull();
        assertThat(isAdded()).isFalse();
    }

    @Test
    public void initialNotAttached_customActivity() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class);
        assertThat(getView()).isNull();
        assertThat(getActivity()).isNull();
        assertThat(isAdded()).isFalse();
    }

    @Test
    public void attachedAfterCreate() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        FragmentController.of(fragment).create();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void attachedAfterCreate_customizedViewId() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        FragmentController.of(fragment, FragmentControllerTest.CustomizedViewIdLoginActivity.class).create(FragmentControllerTest.VIEW_ID_CUSTOMIZED_LOGIN_ACTIVITY, null);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void attachedAfterCreate_customActivity() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class).create();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(getActivity()).isInstanceOf(FragmentControllerTest.LoginActivity.class);
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void isResumed() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class).create().start().resume();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isTrue();
    }

    @Test
    public void isPaused() {
        final FragmentControllerTest.LoginFragment fragment = Mockito.spy(new FragmentControllerTest.LoginFragment());
        FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class).create().start().resume().pause();
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(isAdded()).isTrue();
        assertThat(fragment.isResumed()).isFalse();
        onResume();
        onPause();
    }

    @Test
    public void isStopped() {
        final FragmentControllerTest.LoginFragment fragment = Mockito.spy(new FragmentControllerTest.LoginFragment());
        FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class).create().start().resume().pause().stop();
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
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        Intent intent = new Intent("test_action");
        intent.putExtra("test_key", "test_value");
        FragmentController<FragmentControllerTest.LoginFragment> controller = FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class, intent).create();
        Intent intentInFragment = getActivity().getIntent();
        assertThat(intentInFragment.getAction()).isEqualTo("test_action");
        assertThat(intentInFragment.getExtras().getString("test_key")).isEqualTo("test_value");
    }

    @Test
    public void withArguments() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        Bundle arguments = new Bundle();
        arguments.putString("test_argument", "test_value");
        FragmentController<FragmentControllerTest.LoginFragment> controller = FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class, arguments).create();
        Bundle argumentsInFragment = controller.get().getArguments();
        assertThat(argumentsInFragment.getString("test_argument")).isEqualTo("test_value");
    }

    @Test
    public void visible() {
        final FragmentControllerTest.LoginFragment fragment = new FragmentControllerTest.LoginFragment();
        final FragmentController<FragmentControllerTest.LoginFragment> controller = FragmentController.of(fragment, FragmentControllerTest.LoginActivity.class);
        controller.create();
        assertThat(getView()).isNotNull();
        controller.start().resume();
        assertThat(isVisible()).isFalse();
        controller.visible();
        assertThat(isVisible()).isTrue();
    }

    public static class LoginFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(fragment_contents, container, false);
        }
    }

    private static class LoginActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(1);
            setContentView(view);
        }
    }

    private static class CustomizedViewIdLoginActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(FragmentControllerTest.VIEW_ID_CUSTOMIZED_LOGIN_ACTIVITY);
            setContentView(view);
        }
    }
}

