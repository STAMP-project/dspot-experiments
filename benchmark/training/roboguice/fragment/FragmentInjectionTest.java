package roboguice.fragment;


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
import org.robolectric.util.ActivityController;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;


@RunWith(RobolectricTestRunner.class)
public class FragmentInjectionTest {
    @Test
    public void shouldInjectPojosAndViewsIntoFragments() {
        final FragmentInjectionTest.ActivityA activityA = Robolectric.buildActivity(FragmentInjectionTest.ActivityA.class).create().start().resume().get();
        Assert.assertNotNull(activityA.fragmentRef.ref);
        Assert.assertThat(activityA.fragmentRef.v, CoreMatchers.equalTo(activityA.fragmentRef.ref));
        Assert.assertThat(activityA.fragmentRef.context, CoreMatchers.equalTo(((Context) (activityA))));
    }

    @Test
    public void shouldBeAbleToInjectViewsIntoActivityAndFragment() {
        final FragmentInjectionTest.ActivityB activityB = Robolectric.buildActivity(FragmentInjectionTest.ActivityB.class).create().start().resume().get();
        Assert.assertNotNull(activityB.fragmentRef.viewRef);
        Assert.assertNotNull(activityB.viewRef);
        Assert.assertThat(activityB.fragmentRef.v, CoreMatchers.equalTo(activityB.fragmentRef.viewRef));
        Assert.assertThat(activityB.v, CoreMatchers.equalTo(activityB.viewRef));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotBeAbleToInjectFragmentViewsIntoActivity() {
        Robolectric.buildActivity(FragmentInjectionTest.ActivityC.class).create().start().resume().get();
    }

    @Test
    public void shouldNotCrashWhenRotatingScreen() {
        final ActivityController<FragmentInjectionTest.ActivityD> activityD1Controller = Robolectric.buildActivity(FragmentInjectionTest.ActivityD.class).create().resume();
        final FragmentInjectionTest.ActivityD activityD1 = activityD1Controller.get();
        final ActivityController<FragmentInjectionTest.ActivityD> activityD2Controller = Robolectric.buildActivity(FragmentInjectionTest.ActivityD.class);
        final FragmentInjectionTest.ActivityD activityD2 = activityD2Controller.get();
        Assert.assertNotNull(activityD1.fragmentRef.ref);
        Assert.assertThat(activityD1.fragmentRef.v, CoreMatchers.equalTo(activityD1.fragmentRef.ref));
        activityD1Controller.pause();
        activityD2Controller.create().resume();
        Assert.assertNotNull(activityD2.fragmentRef.ref);
        Assert.assertThat(activityD2.fragmentRef.v, CoreMatchers.equalTo(activityD2.fragmentRef.ref));
    }

    public static class ActivityA extends RoboFragmentActivity {
        FragmentInjectionTest.ActivityA.FragmentA fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // fragmentRef = new FragmentA();
            // fragmentRef.onAttach(this);
            // fragmentRef.onCreate(null);
            fragmentRef = new FragmentInjectionTest.ActivityA.FragmentA();
            FragmentInjectionTest.startFragment(this, fragmentRef);
        }

        public static class FragmentA extends RoboFragment {
            @InjectView(101)
            View v;

            @Inject
            Context context;

            View ref;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                ref = new View(getActivity());
                ref.setId(101);
                return ref;
            }
        }
    }

    public static class ActivityB extends RoboFragmentActivity {
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
            FragmentInjectionTest.startFragment(this, fragmentRef);
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

    public static class ActivityC extends RoboFragmentActivity {
        @InjectView(101)
        View v;

        View viewRef;

        FragmentInjectionTest.ActivityC.FragmentC fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new View(this));
            fragmentRef = new FragmentInjectionTest.ActivityC.FragmentC();
            FragmentInjectionTest.startFragment(this, fragmentRef);
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

    public static class ActivityD extends RoboFragmentActivity {
        FragmentInjectionTest.ActivityD.FragmentD fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            fragmentRef = new FragmentInjectionTest.ActivityD.FragmentD();
            FragmentInjectionTest.startFragment(this, fragmentRef);
            setContentView(new FrameLayout(this));
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
        }
    }
}

