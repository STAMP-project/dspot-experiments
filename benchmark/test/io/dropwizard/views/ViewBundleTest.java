package io.dropwizard.views;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ViewBundleTest {
    private JerseyEnvironment jerseyEnvironment = Mockito.mock(JerseyEnvironment.class);

    private Environment environment = Mockito.mock(Environment.class);

    private static class MyConfiguration extends Configuration {
        @NotNull
        private Map<String, Map<String, String>> viewRendererConfiguration = Collections.emptyMap();

        @JsonProperty("viewRendererConfiguration")
        public Map<String, Map<String, String>> getViewRendererConfiguration() {
            return viewRendererConfiguration;
        }

        @JsonProperty("viewRendererConfiguration")
        public void setViewRendererConfiguration(Map<String, Map<String, String>> viewRendererConfiguration) {
            this.viewRendererConfiguration = viewRendererConfiguration;
        }
    }

    @Test
    public void addsTheViewMessageBodyWriterToTheEnvironment() throws Exception {
        new ViewBundle().run(new ViewBundleTest.MyConfiguration(), environment);
        Mockito.verify(jerseyEnvironment).register(ArgumentMatchers.any(ViewMessageBodyWriter.class));
    }

    @Test
    public void addsTheViewMessageBodyWriterWithSingleViewRendererToTheEnvironment() throws Exception {
        final String configurationKey = "freemarker";
        final String testKey = "testKey";
        final Map<String, String> freeMarkerConfig = Collections.singletonMap(testKey, "yes");
        final Map<String, Map<String, String>> viewRendererConfig = Collections.singletonMap(configurationKey, freeMarkerConfig);
        final ViewBundleTest.MyConfiguration myConfiguration = new ViewBundleTest.MyConfiguration();
        myConfiguration.setViewRendererConfiguration(viewRendererConfig);
        ViewRenderer renderer = new ViewRenderer() {
            @Override
            public boolean isRenderable(View view) {
                return false;
            }

            @Override
            public void render(View view, Locale locale, OutputStream output) throws IOException, WebApplicationException {
                // nothing to do
            }

            @Override
            public void configure(Map<String, String> options) {
                assertThat(options).containsKey(testKey);
            }

            @Override
            public String getConfigurationKey() {
                return configurationKey;
            }
        };
        new ViewBundle<ViewBundleTest.MyConfiguration>(Collections.singletonList(renderer)) {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(ViewBundleTest.MyConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        }.run(myConfiguration, environment);
        final ArgumentCaptor<ViewMessageBodyWriter> captor = ArgumentCaptor.forClass(ViewMessageBodyWriter.class);
        Mockito.verify(jerseyEnvironment).register(captor.capture());
        final ViewMessageBodyWriter capturedRenderer = captor.getValue();
        final Iterable<ViewRenderer> configuredRenderers = capturedRenderer.getRenderers();
        assertThat(configuredRenderers).hasSize(1);
        assertThat(configuredRenderers).contains(renderer);
    }
}

