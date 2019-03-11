package io.dropwizard.logging;


import ch.qos.logback.classic.LoggerContext;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class DropwizardLayoutTest {
    private final LoggerContext context = Mockito.mock(LoggerContext.class);

    private final TimeZone timeZone = TimeZone.getTimeZone("UTC");

    private final DropwizardLayout layout = new DropwizardLayout(context, timeZone);

    @Test
    public void prefixesThrowables() throws Exception {
        assertThat(layout.getDefaultConverterMap().get("ex")).isEqualTo(PrefixedThrowableProxyConverter.class.getName());
    }

    @Test
    public void prefixesExtendedThrowables() throws Exception {
        assertThat(layout.getDefaultConverterMap().get("xEx")).isEqualTo(PrefixedExtendedThrowableProxyConverter.class.getName());
    }

    @Test
    public void hasAContext() throws Exception {
        assertThat(layout.getContext()).isEqualTo(context);
    }

    @Test
    public void hasAPatternWithATimeZoneAndExtendedThrowables() throws Exception {
        assertThat(layout.getPattern()).isEqualTo("%-5p [%d{ISO8601,UTC}] %c: %m%n%rEx");
    }
}

