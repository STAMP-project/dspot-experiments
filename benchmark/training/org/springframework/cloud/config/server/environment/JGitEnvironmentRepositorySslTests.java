/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.config.server.environment;


import WebApplicationType.NONE;
import java.security.cert.CertificateException;
import org.eclipse.jgit.junit.http.SimpleHttpServer;
import org.junit.Test;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


public class JGitEnvironmentRepositorySslTests {
    private static SimpleHttpServer server;

    @Test(expected = CertificateException.class)
    public void selfSignedCertIsRejected() throws Throwable {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(JGitEnvironmentRepositorySslTests.TestConfiguration.class).properties(JGitEnvironmentRepositorySslTests.configServerProperties()).web(NONE).run();
        JGitEnvironmentRepository repository = context.getBean(JGitEnvironmentRepository.class);
        try {
            repository.findOne("bar", "staging", "master");
        } catch (Throwable e) {
            while ((e.getCause()) != null) {
                e = e.getCause();
                if (e instanceof CertificateException) {
                    break;
                }
            } 
            throw e;
        }
    }

    @Test
    public void selfSignedCertWithSkipSslValidationIsAccepted() {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(JGitEnvironmentRepositorySslTests.TestConfiguration.class).properties(JGitEnvironmentRepositorySslTests.configServerProperties("spring.cloud.config.server.git.skipSslValidation=true")).web(NONE).run();
        JGitEnvironmentRepository repository = context.getBean(JGitEnvironmentRepository.class);
        repository.findOne("bar", "staging", "master");
    }

    @Configuration
    @EnableConfigurationProperties(ConfigServerProperties.class)
    @Import({ PropertyPlaceholderAutoConfiguration.class, EnvironmentRepositoryConfiguration.class })
    static class TestConfiguration {}
}

