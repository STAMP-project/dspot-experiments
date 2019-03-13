package roboguice.inject;


import android.content.Context;
import android.os.Bundle;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.inject.Inject;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import roboguice.activity.RoboActivity;


@RunWith(RobolectricTestRunner.class)
public class ProviderTest {
    @Test(expected = AssertionError.class)
    public void shouldNotReturnProperContext() throws Exception {
        final ProviderTest.A a = Robolectric.buildActivity(ProviderTest.A.class).create().get();
        final ProviderTest.B b = Robolectric.buildActivity(ProviderTest.B.class).create().get();
        final FutureTask<Context> future = new FutureTask<Context>(new Callable<Context>() {
            @Override
            public Context call() throws Exception {
                return a.contextProvider.get(b);
            }
        });
        Executors.newSingleThreadExecutor().execute(future);
        Assert.assertThat(future.get(), IsEqual.equalTo(((Context) (a))));
    }

    @Test
    public void shouldReturnProperContext() throws Exception {
        // noinspection UnusedDeclaration
        @SuppressWarnings("unused")
        final ProviderTest.B b = Robolectric.buildActivity(ProviderTest.B.class).create().get();
        final ProviderTest.C c = Robolectric.buildActivity(ProviderTest.C.class).create().get();
        final FutureTask<Context> future = new FutureTask<Context>(new Callable<Context>() {
            @Override
            public Context call() throws Exception {
                return c.contextProvider.get(c);
            }
        });
        Executors.newSingleThreadExecutor().execute(future);
        Assert.assertThat(future.get(), IsEqual.equalTo(((Context) (c))));
    }

    public static class A extends RoboActivity {
        @Inject
        ContextScopedProvider<Context> contextProvider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class B extends RoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    public static class C extends RoboActivity {
        @Inject
        ContextScopedProvider<Context> contextProvider;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}

