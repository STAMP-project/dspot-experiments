package samples.powermockito.junit4.agent.github512;


import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.singleton.StaticService;


@PowerMockIgnore("org.assertj.*")
public class Github512Test {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    @PrepareForTest(StaticService.class)
    public void shouldSuppressMethodWithPrepareForTestOnMethod() {
        suppress(method(StaticService.class, "calculate"));
        assertThat(StaticService.calculate(1, 5)).isEqualTo(0);
    }
}

