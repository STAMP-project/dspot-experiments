package org.robolectric.shadows.support.v4;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.util.TestRunnerWithManifest;

import static org.robolectric.R.id.tacos;
import static org.robolectric.R.layout.fragment_contents;


@RunWith(TestRunnerWithManifest.class)
public class SupportFragmentTestUtilTest {
    @Test
    public void startFragment_shouldStartSupportFragment() {
        final SupportFragmentTestUtilTest.LoginSupportFragment fragment = new SupportFragmentTestUtilTest.LoginSupportFragment();
        SupportFragmentTestUtil.startFragment(fragment);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void startVisibleFragment_shouldStartSupportFragment() {
        final SupportFragmentTestUtilTest.LoginSupportFragment fragment = new SupportFragmentTestUtilTest.LoginSupportFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
    }

    @Test
    public void startVisibleFragment_shouldAttachSupportFragmentToActivity() {
        final SupportFragmentTestUtilTest.LoginSupportFragment fragment = new SupportFragmentTestUtilTest.LoginSupportFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment);
        assertThat(getView().getWindowToken()).isNotNull();
    }

    @Test
    public void startFragment_shouldStartSupportFragmentWithSpecifiedActivityClass() {
        final SupportFragmentTestUtilTest.LoginSupportFragment fragment = new SupportFragmentTestUtilTest.LoginSupportFragment();
        SupportFragmentTestUtil.startFragment(fragment, SupportFragmentTestUtilTest.LoginFragmentActivity.class);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
        assertThat(getActivity()).isInstanceOf(SupportFragmentTestUtilTest.LoginFragmentActivity.class);
    }

    @Test
    public void startVisibleFragment_shouldStartSupportFragmentWithSpecifiedActivityClass() {
        final SupportFragmentTestUtilTest.LoginSupportFragment fragment = new SupportFragmentTestUtilTest.LoginSupportFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment, SupportFragmentTestUtilTest.LoginFragmentActivity.class, 1);
        assertThat(getView()).isNotNull();
        assertThat(getActivity()).isNotNull();
        assertThat(((android.widget.TextView) (getView().findViewById(tacos)))).isNotNull();
        assertThat(getActivity()).isInstanceOf(SupportFragmentTestUtilTest.LoginFragmentActivity.class);
    }

    @Test
    public void startVisibleFragment_shouldAttachSupportFragmentToActivityWithSpecifiedActivityClass() {
        final SupportFragmentTestUtilTest.LoginSupportFragment fragment = new SupportFragmentTestUtilTest.LoginSupportFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment, SupportFragmentTestUtilTest.LoginFragmentActivity.class, 1);
        assertThat(getView().getWindowToken()).isNotNull();
        assertThat(getActivity()).isInstanceOf(SupportFragmentTestUtilTest.LoginFragmentActivity.class);
    }

    public static class LoginSupportFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(fragment_contents, container, false);
        }
    }

    public static class LoginFragmentActivity extends FragmentActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(1);
            setContentView(view);
        }
    }
}

