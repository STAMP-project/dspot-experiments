package cucumber.runtime.java.needle;


import CucumberNeedleConfiguration.RESOURCE_CUCUMBER_NEEDLE;
import cucumber.runtime.java.needle.test.injectionprovider.SimpleNameGetterProvider;
import de.akquinet.jbosscc.needle.injection.InjectionProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NeedleFactoryTest {
    @Test
    public void shouldSetUpInjectionProviders() throws Exception {
        final InjectionProvider<?>[] injectionProviders = NeedleFactory.setUpInjectionProviders(RESOURCE_CUCUMBER_NEEDLE);
        Assert.assertNotNull(injectionProviders);
        Assert.assertThat(injectionProviders.length, CoreMatchers.is(1));
        Assert.assertThat(injectionProviders[0].getClass().getCanonicalName(), CoreMatchers.is(SimpleNameGetterProvider.class.getCanonicalName()));
    }
}

