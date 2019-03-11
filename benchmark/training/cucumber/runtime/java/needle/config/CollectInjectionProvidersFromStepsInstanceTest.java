package cucumber.runtime.java.needle.config;


import cucumber.api.needle.InjectionProviderInstancesSupplier;
import cucumber.api.needle.NeedleInjectionProvider;
import cucumber.runtime.java.needle.injection.NamedInjectionProvider;
import de.akquinet.jbosscc.needle.NeedleTestcase;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.injection.InjectionProvider;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static CollectInjectionProvidersFromStepsInstance.INSTANCE;


public class CollectInjectionProvidersFromStepsInstanceTest {
    private final CollectInjectionProvidersFromStepsInstance function = INSTANCE;

    @NeedleInjectionProvider
    private final InjectionProviderInstancesSupplier supplier = new InjectionProviderInstancesSupplier() {
        @Override
        public Set<InjectionProvider<?>> get() {
            final HashSet<InjectionProvider<?>> result = new HashSet<InjectionProvider<?>>();
            result.add(NamedInjectionProvider.forNamedValue("foo", "bar"));
            return result;
        }
    };

    private static class MyNeedleTestcase extends NeedleTestcase {
        public MyNeedleTestcase(final InjectionProvider<?>... injectionProvider) {
            super(injectionProvider);
        }

        protected void initMyTestcase(final Object test) throws Exception {
            initTestcase(test);
        }
    }

    public static class A {
        @Inject
        @Named("foo")
        private String bar;
    }

    @ObjectUnderTest
    private CollectInjectionProvidersFromStepsInstanceTest.A a;

    @Test
    public void shouldAddInjectionProviders() throws Exception {
        final InjectionProvider<?>[] injectionProviders = function.apply(this);
        Assert.assertThat(injectionProviders.length, CoreMatchers.is(1));
        new CollectInjectionProvidersFromStepsInstanceTest.MyNeedleTestcase(injectionProviders).initMyTestcase(this);
        Assert.assertThat(a.bar, CoreMatchers.is("bar"));
    }
}

