package io.dropwizard.testing.app;


import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;


public class ResourceTestRuleWithoutLoggingBootstrap {
    @SuppressWarnings("deprecation")
    @Rule
    public final ResourceTestRule resourceTestRule = ResourceTestRule.builder().addResource(TestResource::new).bootstrapLogging(false).build();

    @Test
    public void testResource() {
        assertThat(resourceTestRule.target("test").request().get(String.class)).isEqualTo("Default message");
    }
}

