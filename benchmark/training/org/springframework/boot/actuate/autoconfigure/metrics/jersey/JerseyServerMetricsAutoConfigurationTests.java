/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.autoconfigure.metrics.jersey;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.jersey2.server.JerseyTagsProvider;
import io.micrometer.jersey2.server.MetricsApplicationEventListener;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link JerseyServerMetricsAutoConfiguration}.
 *
 * @author Michael Weirauch
 * @author Michael Simons
 */
public class JerseyServerMetricsAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().with(MetricsRun.simple()).withConfiguration(AutoConfigurations.of(JerseyServerMetricsAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner(AnnotationConfigServletWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(JerseyAutoConfiguration.class, JerseyServerMetricsAutoConfiguration.class, ServletWebServerFactoryAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class, MetricsAutoConfiguration.class)).withUserConfiguration(JerseyServerMetricsAutoConfigurationTests.ResourceConfiguration.class).withPropertyValues("server.port:0");

    @Test
    public void shouldOnlyBeActiveInWebApplicationContext() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void shouldProvideAllNecessaryBeans() {
        this.webContextRunner.run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class));
    }

    @Test
    public void shouldHonorExistingTagProvider() {
        this.webContextRunner.withUserConfiguration(JerseyServerMetricsAutoConfigurationTests.CustomJerseyTagsProviderConfiguration.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void httpRequestsAreTimed() {
        this.webContextRunner.run(( context) -> {
            doRequest(context);
            MeterRegistry registry = context.getBean(.class);
            Timer timer = registry.get("http.server.requests").tag("uri", "/users/{id}").timer();
            assertThat(timer.count()).isEqualTo(1);
        });
    }

    @Test
    public void noHttpRequestsTimedWhenJerseyInstrumentationMissingFromClasspath() {
        this.webContextRunner.withClassLoader(new FilteredClassLoader(MetricsApplicationEventListener.class)).run(( context) -> {
            doRequest(context);
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("http.server.requests").timer()).isNull();
        });
    }

    @Configuration
    static class ResourceConfiguration {
        @Bean
        ResourceConfig resourceConfig() {
            return new ResourceConfig().register(new JerseyServerMetricsAutoConfigurationTests.ResourceConfiguration.TestResource());
        }

        @Path("/users")
        public class TestResource {
            @GET
            @Path("/{id}")
            public String getUser(@PathParam("id")
            String id) {
                return id;
            }
        }
    }

    @Configuration
    static class CustomJerseyTagsProviderConfiguration {
        @Bean
        JerseyTagsProvider customJerseyTagsProvider() {
            return new JerseyServerMetricsAutoConfigurationTests.CustomJerseyTagsProvider();
        }
    }

    static class CustomJerseyTagsProvider implements JerseyTagsProvider {
        @Override
        public Iterable<Tag> httpRequestTags(RequestEvent event) {
            return null;
        }

        @Override
        public Iterable<Tag> httpLongRequestTags(RequestEvent event) {
            return null;
        }
    }
}

