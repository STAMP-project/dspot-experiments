package cucumber.runtime.java.needle.config;


import cucumber.api.needle.InjectionProviderInstancesSupplier;
import cucumber.runtime.java.needle.test.injectionprovider.SimpleNameGetterProvider;
import de.akquinet.jbosscc.needle.injection.InjectionProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CucumberNeedleConfigurationTest {
    public abstract static class A implements InjectionProviderInstancesSupplier {}

    @Test
    public void shouldReturnEmptyInstances() {
        final InjectionProvider<?>[] allInjectionProviders = new CucumberNeedleConfiguration("resource-bundles/empty").getInjectionProviders();
        Assert.assertNotNull(allInjectionProviders);
        Assert.assertThat(allInjectionProviders.length, CoreMatchers.is(0));
    }

    @Test
    public void shouldEvaluateIfTypeIsInjectionProviderOrSupplier() throws Exception {
        Assert.assertTrue(CucumberNeedleConfiguration.isInjectionProvider(SimpleNameGetterProvider.class));
        Assert.assertFalse(CucumberNeedleConfiguration.isInjectionProviderInstanceSupplier(SimpleNameGetterProvider.class));
        Assert.assertFalse(CucumberNeedleConfiguration.isInjectionProvider(CucumberNeedleConfigurationTest.A.class));
        Assert.assertTrue(CucumberNeedleConfiguration.isInjectionProviderInstanceSupplier(CucumberNeedleConfigurationTest.A.class));
    }
}

