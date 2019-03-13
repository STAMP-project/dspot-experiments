/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.logging;


import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.actuate.logging.LogFileWebEndpoint;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;


/**
 * Tests for {@link LogFileWebEndpointAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Christian Carriere-Tisseur
 */
public class LogFileWebEndpointAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withUserConfiguration(LogFileWebEndpointAutoConfiguration.class);

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void logFileWebEndpointIsAutoConfiguredWhenLoggingFileIsSet() {
        this.contextRunner.withPropertyValues("logging.file.name:test.log").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    @Deprecated
    public void logFileWebEndpointIsAutoConfiguredWhenLoggingFileIsSetWithDeprecatedProperty() {
        this.contextRunner.withPropertyValues("logging.file:test.log").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void logFileWebEndpointIsAutoConfiguredWhenLoggingPathIsSet() {
        this.contextRunner.withPropertyValues("logging.file.path:test/logs").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    @Deprecated
    public void logFileWebEndpointIsAutoConfiguredWhenLoggingPathIsSetWithDeprecatedProperty() {
        this.contextRunner.withPropertyValues("logging.path:test/logs").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void logFileWebEndpointIsAutoConfiguredWhenExternalFileIsSet() {
        this.contextRunner.withPropertyValues("management.endpoint.logfile.external-file:external.log").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void logFileWebEndpointCanBeDisabled() {
        this.contextRunner.withPropertyValues("logging.file.name:test.log", "management.endpoint.logfile.enabled:false").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void logFileWebEndpointUsesConfiguredExternalFile() throws IOException {
        File file = this.temp.newFile();
        FileCopyUtils.copy("--TEST--".getBytes(), file);
        this.contextRunner.withPropertyValues(("management.endpoint.logfile.external-file:" + (file.getAbsolutePath()))).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            LogFileWebEndpoint endpoint = context.getBean(.class);
            Resource resource = endpoint.logFile();
            assertThat(resource).isNotNull();
            assertThat(StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)).isEqualTo("--TEST--");
        });
    }
}

