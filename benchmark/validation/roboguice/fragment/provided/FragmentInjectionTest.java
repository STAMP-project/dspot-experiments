package roboguice.fragment.provided;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;
import roboguice.activity.RoboActivity;
import roboguice.fragment.provided.shadow.ShadowNativeFragment;
import roboguice.fragment.provided.shadow.ShadowNativeFragmentActivity;
import roboguice.inject.InjectView;


@RunWith(RobolectricTestRunner.class)
@Config(shadows = { ShadowNativeFragment.class, ShadowNativeFragmentActivity.class })
public class FragmentInjectionTest {
    @Test
    public void shadowActivityGetApplicationContextShouldNotReturnNull() {
        Assert.assertNotNull(new Activity().getApplicationContext());
    }

    @Test
    public void shouldInjectPojosAndViewsIntoFragments() {
        final FragmentInjectionTest.ActivityA activity = Robolectric.buildActivity(FragmentInjectionTest.ActivityA.class).create().start().resume().get();
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null, null, null), null);
        Assert.assertNotNull(activity.fragmentRef.ref);
        Assert.assertThat(activity.fragmentRef.v, CoreMatchers.equalTo(activity.fragmentRef.ref));
        Assert.assertThat(activity.fragmentRef.context, CoreMatchers.equalTo(((Context) (activity))));
    }

    @Test
    public void shouldBeAbleToInjectViewsIntoActivityAndFragment() {
        final FragmentInjectionTest.ActivityB activity = Robolectric.buildActivity(FragmentInjectionTest.ActivityB.class).create().get();
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null, null, null), null);
        Assert.assertNotNull(activity.fragmentRef.viewRef);
        Assert.assertNotNull(activity.viewRef);
        Assert.assertThat(activity.fragmentRef.v, CoreMatchers.equalTo(activity.fragmentRef.viewRef));
        Assert.assertThat(activity.v, CoreMatchers.equalTo(activity.viewRef));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotBeAbleToInjectFragmentViewsIntoActivity() {
        final FragmentInjectionTest.ActivityC activity = new FragmentInjectionTest.ActivityC();
        activity.onCreate(null);
        activity.fragmentRef.onViewCreated(activity.fragmentRef.onCreateView(null, null, null), null);
    }

    @Test
    public void shouldNotCrashWhenRotatingScreen() {
        final ActivityController<FragmentInjectionTest.ActivityD> activityD1Controller = Robolectric.buildActivity(FragmentInjectionTest.ActivityD.class).create().resume();
        final FragmentInjectionTest.ActivityD activity1 = activityD1Controller.get();
        final ActivityController<FragmentInjectionTest.ActivityD> activityD2Controller = Robolectric.buildActivity(FragmentInjectionTest.ActivityD.class);
        final FragmentInjectionTest.ActivityD activity2 = activityD2Controller.get();
        activity1.fragmentRef.onViewCreated(activity1.fragmentRef.onCreateView(null, null, null), null);
        Assert.assertNotNull(activity1.fragmentRef.ref);
        Assert.assertThat(activity1.fragmentRef.v, CoreMatchers.equalTo(activity1.fragmentRef.ref));
        activityD1Controller.pause();
        activityD2Controller.create().resume();
        activity2.fragmentRef.onViewCreated(activity2.fragmentRef.onCreateView(null, null, null), null);
        Assert.assertNotNull(activity2.fragmentRef.ref);
        Assert.assertThat(activity2.fragmentRef.v, CoreMatchers.equalTo(activity2.fragmentRef.ref));
    }

    public static class ActivityA extends RoboActivity {
        FragmentInjectionTest.ActivityA.FragmentA fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            fragmentRef = new FragmentInjectionTest.ActivityA.FragmentA();
            fragmentRef.onAttach(this);
            fragmentRef.onCreate(null);
        }

        public static class FragmentA extends RoboFragment {
            @InjectView(101)
            View v;

            @Inject
            Context context;

            View ref;

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public void onViewCreated(View view, Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                ref = new View(getActivity());
                ref.setId(101);
                return ref;
            }
        }
    }

    public static class ActivityB extends RoboActivity {
        @InjectView(100)
        View v;

        View viewRef;

        FragmentInjectionTest.ActivityB.FragmentB fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            viewRef = new View(this);
            viewRef.setId(100);
            setContentView(viewRef);
            fragmentRef = new FragmentInjectionTest.ActivityB.FragmentB();
            fragmentRef.onAttach(this);
            fragmentRef.onCreate(null);
        }

        public static class FragmentB extends RoboFragment {
            @InjectView(101)
            View v;

            View viewRef;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                viewRef = new View(getActivity());
                viewRef.setId(101);
                return viewRef;
            }
        }
    }

    public static class ActivityC extends RoboActivity {
        @InjectView(101)
        View v;

        View viewRef;

        FragmentInjectionTest.ActivityC.FragmentC fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new View(this));
            fragmentRef = new FragmentInjectionTest.ActivityC.FragmentC();
            fragmentRef.onAttach(this);
            fragmentRef.onCreate(null);
        }

        public static class FragmentC extends RoboFragment {
            @InjectView(101)
            View v;

            View viewRef;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                viewRef = new View(getActivity());
                viewRef.setId(101);
                return viewRef;
            }
        }
    }

    public static class ActivityD extends RoboActivity {
        FragmentInjectionTest.ActivityD.FragmentD fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            fragmentRef = new FragmentInjectionTest.ActivityD.FragmentD();
            onAttach(this);
            fragmentRef.onCreate(null);
            setContentView(new FrameLayout(this));
        }

        @Override
        protected void onPause() {
            super.onPause();
        }

        @Override
        protected void onResume() {
            super.onResume();
        }

        public static class FragmentD extends RoboFragment {
            @InjectView(101)
            View v;

            View ref;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                ref = new View(getActivity());
                ref.setId(101);
                return ref;
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }
}

