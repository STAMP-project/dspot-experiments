package org.robolectric.android;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.util.FragmentTestUtil;

import static org.robolectric.R.id.tacos;
import static org.robolectric.R.layout.fragment_contents;


@RunWith(AndroidJUnit4.class)
public class FragmentTestUtilTest {
    @Test
    public void startFragment_shouldStartFragment() {
        final FragmentTestUtilTest.LoginFragment fragment = new FragmentTestUtilTest.LoginFragment();
        FragmentTestUtil.startFragment(fragment);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void startVisibleFragment_shouldStartFragment() {
        final FragmentTestUtilTest.LoginFragment fragment = new FragmentTestUtilTest.LoginFragment();
        FragmentTestUtil.startVisibleFragment(fragment);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void startVisibleFragment_shouldAttachFragmentToActivity() {
        final FragmentTestUtilTest.LoginFragment fragment = new FragmentTestUtilTest.LoginFragment();
        FragmentTestUtil.startVisibleFragment(fragment);
        assertThat(getView().getWindowToken()).isNotNull();
    }

    @Test
    public void startFragment_shouldStartFragmentWithSpecifiedActivityClass() {
        final FragmentTestUtilTest.LoginFragment fragment = new FragmentTestUtilTest.LoginFragment();
        FragmentTestUtil.startFragment(fragment, FragmentTestUtilTest.LoginActivity.class);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
        assertThat(getActivity()).isInstanceOf(FragmentTestUtilTest.LoginActivity.class);
    }

    @Test
    public void startVisibleFragment_shouldStartFragmentWithSpecifiedActivityClass() {
        final FragmentTestUtilTest.LoginFragment fragment = new FragmentTestUtilTest.LoginFragment();
        FragmentTestUtil.startVisibleFragment(fragment, FragmentTestUtilTest.LoginActivity.class, 1);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
        assertThat(getActivity()).isInstanceOf(FragmentTestUtilTest.LoginActivity.class);
    }

    @Test
    public void startVisibleFragment_shouldAttachFragmentToActivityWithSpecifiedActivityClass() {
        final FragmentTestUtilTest.LoginFragment fragment = new FragmentTestUtilTest.LoginFragment();
        FragmentTestUtil.startVisibleFragment(fragment, FragmentTestUtilTest.LoginActivity.class, 1);
        assertThat(getView().getWindowToken()).isNotNull();
        assertThat(getActivity()).isInstanceOf(FragmentTestUtilTest.LoginActivity.class);
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
}

