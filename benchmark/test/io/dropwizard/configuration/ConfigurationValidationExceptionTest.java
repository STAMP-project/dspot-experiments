package io.dropwizard.configuration;


import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;


public class ConfigurationValidationExceptionTest {
    private static class Example {
        // Weird combination, but Hibernate Validator is not good with the compile nullable checks
        @NotNull
        @Nullable
        String woo;
    }

    private ConfigurationValidationException e;

    @Test
    public void formatsTheViolationsIntoAHumanReadableMessage() {
        assertThat(e.getMessage()).isEqualTo(String.format(("config.yml has an error:%n" + "  * woo must not be null%n")));
    }

    @Test
    public void retainsTheSetOfExceptions() {
        assertThat(e.getConstraintViolations()).isNotEmpty();
    }
}

