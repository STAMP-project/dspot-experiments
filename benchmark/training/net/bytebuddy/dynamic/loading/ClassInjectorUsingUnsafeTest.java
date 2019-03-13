package net.bytebuddy.dynamic.loading;


import java.util.Collections;
import net.bytebuddy.test.utility.ClassUnsafeInjectionAvailableRule;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingUnsafe.isAvailable;


public class ClassInjectorUsingUnsafeTest {
    @Rule
    public MethodRule classUnsafeInjectionAvailableRule = new ClassUnsafeInjectionAvailableRule();

    private ClassLoader classLoader;

    @Test
    @ClassUnsafeInjectionAvailableRule.Enforce
    public void testUnsafeInjection() throws Exception {
        MatcherAssert.assertThat(new ClassInjector.UsingUnsafe(classLoader).inject(Collections.singletonMap(of(ClassInjectorUsingUnsafeTest.Foo.class), read(ClassInjectorUsingUnsafeTest.Foo.class))).get(of(ClassInjectorUsingUnsafeTest.Foo.class)), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(Class.forName(ClassInjectorUsingUnsafeTest.Foo.class.getName(), false, classLoader).getName(), CoreMatchers.is(ClassInjectorUsingUnsafeTest.Foo.class.getName()));
    }

    @Test
    @ClassUnsafeInjectionAvailableRule.Enforce
    public void testAvailability() throws Exception {
        MatcherAssert.assertThat(isAvailable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(isAlive(), CoreMatchers.is(true));
        MatcherAssert.assertThat(new ClassInjector.UsingUnsafe.Dispatcher.Unavailable(null).isAvailable(), CoreMatchers.is(false));
    }

    @Test(expected = RuntimeException.class)
    public void testUnavailableThrowsException() throws Exception {
        new ClassInjector.UsingUnsafe.Dispatcher.Unavailable("foo").initialize();
    }

    @Test
    public void testHelperMethods() throws Exception {
        MatcherAssert.assertThat(ofBootLoader(), FieldByFieldComparison.hasPrototype(((ClassInjector) (new ClassInjector.UsingUnsafe(ClassLoadingStrategy.BOOTSTRAP_LOADER)))));
        MatcherAssert.assertThat(ofPlatformLoader(), FieldByFieldComparison.hasPrototype(((ClassInjector) (new ClassInjector.UsingUnsafe(ClassLoader.getSystemClassLoader().getParent())))));
        MatcherAssert.assertThat(ofSystemLoader(), FieldByFieldComparison.hasPrototype(((ClassInjector) (new ClassInjector.UsingUnsafe(ClassLoader.getSystemClassLoader())))));
    }

    /* empty */
    private static class Foo {}
}

