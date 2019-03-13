package roboguice.activity;


import ActionBarSherlock.Implementation;
import R.id;
import R.id.summary;
import R.id.text1;
import R.id.text2;
import R.id.title;
import R.string;
import R.string.no;
import R.string.ok;
import Robolectric.application;
import Stage.DEVELOPMENT;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.internal.ActionBarSherlockNative;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Key;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.RoboGuice;
import roboguice.inject.ContextScopedProvider;
import roboguice.inject.ContextSingleton;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectPreference;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;


@RunWith(RobolectricTestRunner.class)
public class SherlockActivityInjectionTest {
    protected SherlockActivityInjectionTest.DummySherlockActivity activity;

    @Test
    public void shouldInjectUsingDefaultConstructor() {
        Assert.assertThat(activity.emptyString, CoreMatchers.is(""));
    }

    @Test
    public void shouldInjectView() {
        Assert.assertThat(activity.text1, CoreMatchers.is(activity.findViewById(text1)));
    }

    @Test
    public void shouldInjectStringResource() {
        Assert.assertThat(activity.cancel, CoreMatchers.is("Cancel"));
    }

    @Test
    public void shouldInjectExtras() {
        Assert.assertThat(activity.foobar, CoreMatchers.is("goober"));
    }

    @Test
    public void shouldStaticallyInject() {
        Assert.assertThat(SherlockActivityInjectionTest.ModuleA.A.t, CoreMatchers.equalTo(""));
    }

    @Test
    public void shouldInjectActivityAndRoboSherlockActivity() {
        Assert.assertEquals(activity, activity.activity);
        Assert.assertEquals(activity, activity.roboSherlockActivity);
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectViews() {
        RoboGuice.getOrCreateBaseApplicationInjector(application, DEVELOPMENT, RoboGuice.newDefaultRoboModule(application), new SherlockActivityInjectionTest.ModuleB());
        @SuppressWarnings("unused")
        final SherlockActivityInjectionTest.ModuleB.B b = Robolectric.buildActivity(SherlockActivityInjectionTest.ModuleB.B.class).create().get();
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectExtras() {
        RoboGuice.getOrCreateBaseApplicationInjector(application, DEVELOPMENT, RoboGuice.newDefaultRoboModule(application), new SherlockActivityInjectionTest.ModuleD());
        @SuppressWarnings("unused")
        final SherlockActivityInjectionTest.ModuleD.D d = Robolectric.buildActivity(SherlockActivityInjectionTest.ModuleD.D.class).create().get();
    }

    @Test(expected = ConfigurationException.class)
    public void shouldNotStaticallyInjectPreferenceViews() {
        RoboGuice.getOrCreateBaseApplicationInjector(application, DEVELOPMENT, RoboGuice.newDefaultRoboModule(application), new SherlockActivityInjectionTest.ModuleC());
        @SuppressWarnings("unused")
        final SherlockActivityInjectionTest.ModuleC.C c = Robolectric.buildActivity(SherlockActivityInjectionTest.ModuleC.C.class).create().get();
    }

    @Test
    public void shouldInjectApplication() {
        final SherlockActivityInjectionTest.G g = Robolectric.buildActivity(SherlockActivityInjectionTest.G.class).create().get();
        Assert.assertThat(g.application, CoreMatchers.equalTo(application));
    }

    @Test
    public void shouldAllowBackgroundThreadsToFinishUsingContextAfterOnDestroy() throws Exception {
        final SoftReference<SherlockActivityInjectionTest.F> ref = new SoftReference<SherlockActivityInjectionTest.F>(Robolectric.buildActivity(SherlockActivityInjectionTest.F.class).create().get());
        final BlockingQueue<Context> queue = new ArrayBlockingQueue<Context>(1);
        new Thread() {
            final Context context = RoboGuice.getInjector(ref.get()).getInstance(Context.class);

            @Override
            public void run() {
                queue.add(context);
            }
        }.start();
        ref.get().onDestroy();
        // Force an OoM
        // http://stackoverflow.com/questions/3785713/how-to-make-the-java-system-release-soft-references/3810234
        boolean oomHappened = false;
        try {
            @SuppressWarnings({ "MismatchedQueryAndUpdateOfCollection" })
            final ArrayList<Object[]> allocations = new ArrayList<Object[]>();
            int size;
            while ((size = Math.min(Math.abs(((int) (Runtime.getRuntime().freeMemory()))), Integer.MAX_VALUE)) > 0)
                allocations.add(new Object[size]);

        } catch (OutOfMemoryError e) {
            // Yeah!
            oomHappened = true;
        }
        Assert.assertTrue(oomHappened);
        Assert.assertNotNull(queue.poll(10, TimeUnit.SECONDS));
    }

    @Test
    public void shouldBeAbleToGetContextProvidersInBackgroundThreads() throws Exception {
        final SherlockActivityInjectionTest.F f = Robolectric.buildActivity(SherlockActivityInjectionTest.F.class).create().get();
        final FutureTask<Context> future = new FutureTask<Context>(new Callable<Context>() {
            final ContextScopedProvider<Context> contextProvider = RoboGuice.getInjector(f).getInstance(Key.get(new com.google.inject.TypeLiteral<ContextScopedProvider<Context>>() {}));

            @Override
            public Context call() throws Exception {
                return contextProvider.get(f);
            }
        });
        Executors.newSingleThreadExecutor().execute(future);
        future.get();
    }

    public static class DummySherlockActivity extends RoboSherlockActivity {
        @Inject
        protected String emptyString;

        @Inject
        protected Activity activity;

        @Inject
        protected RoboSherlockActivity roboSherlockActivity;

        @InjectView(id.text1)
        protected TextView text1;

        @InjectResource(string.cancel)
        protected String cancel;

        @InjectExtra("foobar")
        protected String foobar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final LinearLayout root = new LinearLayout(this);
            final TextView text1 = new TextView(this);
            root.addView(text1);
            text1.setId(R.id.text1);
            final LinearLayout included1 = addIncludedView(summary, ok);
            root.addView(included1);
            final LinearLayout included2 = addIncludedView(title, no);
            root.addView(included2);
            setContentView(root);
        }

        protected LinearLayout addIncludedView(int includedRootId, int stringResId) {
            LinearLayout container = new LinearLayout(this);
            container.setId(includedRootId);
            TextView textView = new TextView(this);
            container.addView(textView);
            textView.setId(text2);
            textView.setText(stringResId);
            return container;
        }
    }

    public static class BaseModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(RoboSherlockActivity.class).toProvider(Key.get(new com.google.inject.TypeLiteral<roboguice.inject.NullProvider<RoboSherlockActivity>>() {})).in(ContextSingleton.class);
        }
    }

    public static class ModuleA extends SherlockActivityInjectionTest.BaseModule {
        @Override
        protected void configure() {
            super.configure();
            requestStaticInjection(SherlockActivityInjectionTest.ModuleA.A.class);
        }

        public static class A {
            @InjectResource(string.cancel)
            static String s;

            @Inject
            static String t;
        }
    }

    public static class ModuleB extends SherlockActivityInjectionTest.BaseModule {
        @Override
        protected void configure() {
            super.configure();
            requestStaticInjection(SherlockActivityInjectionTest.ModuleB.B.class);
        }

        public static class B extends RoboSherlockActivity {
            @InjectView(0)
            static View v;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }

    public static class ModuleC extends SherlockActivityInjectionTest.BaseModule {
        @Override
        public void configure() {
            super.configure();
            requestStaticInjection(SherlockActivityInjectionTest.ModuleC.C.class);
        }

        public static class C extends RoboSherlockActivity {
            @InjectPreference("xxx")
            static Preference v;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }

    public static class ModuleD extends SherlockActivityInjectionTest.BaseModule {
        @Override
        public void configure() {
            super.configure();
            requestStaticInjection(SherlockActivityInjectionTest.ModuleD.D.class);
        }

        public static class D extends RoboSherlockActivity {
            @InjectExtra("xxx")
            static String s;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        }
    }

    public static class F extends RoboSherlockActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }
    }

    public static class G extends RoboSherlockActivity {
        @Inject
        Application application;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    @Implementation(api = 0)
    public static class ActionBarSherlockRobolectric extends ActionBarSherlockNative {
        public ActionBarSherlockRobolectric(Activity activity, int flags) {
            super(activity, flags);
        }

        @Override
        public void setContentView(int layoutResId) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            View contentView = layoutInflater.inflate(layoutResId, null);
            shadowOf(mActivity).getWindow().setContentView(contentView);
        }

        @Override
        public void setContentView(View view) {
            shadowOf(mActivity).getWindow().setContentView(view);
        }
    }
}

