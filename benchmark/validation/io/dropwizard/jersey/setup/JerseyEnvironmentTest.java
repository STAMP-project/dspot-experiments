package io.dropwizard.jersey.setup;


import io.dropwizard.jersey.DropwizardResourceConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class JerseyEnvironmentTest {
    private final JerseyContainerHolder holder = Mockito.mock(JerseyContainerHolder.class);

    private final DropwizardResourceConfig config = new DropwizardResourceConfig();

    private final JerseyEnvironment jerseyEnvironment = new JerseyEnvironment(holder, config);

    @Test
    public void urlPatternEndsWithSlashStar() {
        assertPatternEndsWithSlashStar("/missing/slash/star");
    }

    @Test
    public void urlPatternEndsWithStar() {
        assertPatternEndsWithSlashStar("/missing/star/");
    }

    @Test
    public void urlPatternSuffixNoop() {
        String slashStarPath = "/slash/star/*";
        jerseyEnvironment.setUrlPattern(slashStarPath);
        assertThat(jerseyEnvironment.getUrlPattern()).isEqualTo(slashStarPath);
    }
}

