package roboguice.inject;


import Robolectric.application;
import android.app.Activity;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Singleton;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;


@RunWith(RobolectricTestRunner.class)
public class ContextScopeTest {
    @Test
    public void shouldHaveContextInScopeMapAfterOnCreate() throws Exception {
        final ActivityController<ContextScopeTest.A> aController = Robolectric.buildActivity(ContextScopeTest.A.class);
        final ContextScopeTest.A a = aController.get();
        Assert.assertThat(getScopedObjectMap().size(), CoreMatchers.equalTo(0));
        aController.create();
        boolean found = false;
        for (Object o : getScopedObjectMap().values())
            if (o == a)
                found = true;


        Assert.assertTrue("Couldn't find context in scope map", found);
    }

    @Test
    public void shouldBeAbleToOpenMultipleScopes() {
        final ContextScope scope = RoboGuice.getOrCreateBaseApplicationInjector(application).getInstance(ContextScope.class);
        final Activity a = Robolectric.buildActivity(ContextScopeTest.A.class).get();
        final Activity b = Robolectric.buildActivity(ContextScopeTest.B.class).get();
        scope.enter(a);
        scope.enter(b);
        scope.exit(b);
        scope.exit(a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToExitTheWrongScope() {
        final ContextScope scope = RoboGuice.getOrCreateBaseApplicationInjector(application).getInstance(ContextScope.class);
        final Activity a = Robolectric.buildActivity(ContextScopeTest.A.class).get();
        final Activity b = Robolectric.buildActivity(ContextScopeTest.B.class).get();
        scope.enter(a);
        scope.enter(b);
        scope.exit(a);
    }

    @Test
    public void shouldHaveTwoItemsInScopeMapAfterOnCreate() throws Exception {
        final ActivityController<ContextScopeTest.B> bController = Robolectric.buildActivity(ContextScopeTest.B.class);
        final ContextScopeTest.B b = bController.get();
        Assert.assertThat(getScopedObjectMap().size(), CoreMatchers.equalTo(0));
        bController.create();
        boolean found = false;
        for (Object o : getScopedObjectMap().values())
            if (o == b)
                found = true;


        Assert.assertTrue("Couldn't find context in scope map", found);
        Assert.assertTrue(getScopedObjectMap().containsKey(Key.get(ContextScopeTest.C.class)));
    }

    public static class A extends RoboActivity {}

    public static class B extends RoboActivity {
        @Inject
        ContextScopeTest.C c;// context scoped


        @Inject
        ContextScopeTest.D d;// unscoped


        @Inject
        ContextScopeTest.E e;// singleton

    }

    @ContextSingleton
    public static class C {}

    public static class D {}

    @Singleton
    public static class E {}
}

