package samples.powermockito.junit4.rule.objenesis;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;


@PowerMockIgnore({ "org.mockito.*", "org.powermock.api.mockito.repackaged.*" })
@PrepareForTest(Foo.class)
@MockPolicy(PowerMockRuleTest.CustomPolicy.class)
public class PowerMockRuleTest {
    @Rule
    public final PowerMockRule rule = new PowerMockRule();

    @Test
    public void test1() {
        Assert.assertEquals(999, new Foo().m().getI());
    }

    @Test
    public void test2() {
        Assert.assertEquals(999, new Foo().m().getI());
    }

    public static class CustomPolicy implements PowerMockPolicy {
        @Override
        public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
        }

        @Override
        public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
            final Foo.Bar barMock = Mockito.mock(Foo.Bar.class);
            Mockito.when(barMock.getI()).thenReturn(999);
            settings.stubMethod(Whitebox.getMethod(Foo.class, "m"), barMock);
        }
    }
}

