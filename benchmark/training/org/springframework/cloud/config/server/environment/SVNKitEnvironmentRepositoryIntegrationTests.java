/**
 * Copyright 2013-2019 the original author or authors.
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
import java.io.File;
import org.junit.Test;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.EnvironmentRepositoryConfiguration;
import org.springframework.cloud.config.server.test.ConfigServerTestUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 *
 *
 * @author Michael Prankl
 * @author Roy Clarkson
 */
public class SVNKitEnvironmentRepositoryIntegrationTests {
    private ConfigurableApplicationContext context;

    private File workingDir;

    @Test
    public void vanilla() throws Exception {
        String uri = ConfigServerTestUtils.prepareLocalSvnRepo("src/test/resources/svn-config-repo", "target/config");
        this.context = new SpringApplicationBuilder(SVNKitEnvironmentRepositoryIntegrationTests.TestConfiguration.class).web(NONE).profiles("subversion").run(("--spring.cloud.config.server.svn.uri=" + uri));
        EnvironmentRepository repository = this.context.getBean(EnvironmentRepository.class);
        repository.findOne("bar", "staging", "trunk");
        Environment environment = repository.findOne("bar", "staging", "trunk");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
    }

    @Test
    public void update() throws Exception {
        String uri = ConfigServerTestUtils.prepareLocalSvnRepo("src/test/resources/svn-config-repo", "target/config");
        this.context = new SpringApplicationBuilder(SVNKitEnvironmentRepositoryIntegrationTests.TestConfiguration.class).web(NONE).profiles("subversion").run(("--spring.cloud.config.server.svn.uri=" + uri));
        EnvironmentRepository repository = this.context.getBean(EnvironmentRepository.class);
        repository.findOne("bar", "staging", "trunk");
        Environment environment = repository.findOne("bar", "staging", "trunk");
        assertThat(environment.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
        updateRepoForUpdate(uri);
        environment = repository.findOne("bar", "staging", "trunk");
        assertThat(environment.getPropertySources().get(0).getSource().get("foo")).isEqualTo("foo");
    }

    @Test
    public void defaultLabel() throws Exception {
        String uri = ConfigServerTestUtils.prepareLocalSvnRepo("src/test/resources/svn-config-repo", "target/config");
        this.context = new SpringApplicationBuilder(SVNKitEnvironmentRepositoryIntegrationTests.TestConfiguration.class).web(NONE).profiles("subversion").run(("--spring.cloud.config.server.svn.uri=" + uri));
        SvnKitEnvironmentRepository repository = this.context.getBean(SvnKitEnvironmentRepository.class);
        assertThat(repository.getDefaultLabel()).isEqualTo("trunk");
    }

    @Test(expected = NoSuchLabelException.class)
    public void invalidLabel() throws Exception {
        String uri = ConfigServerTestUtils.prepareLocalSvnRepo("src/test/resources/svn-config-repo", "target/config");
        this.context = new SpringApplicationBuilder(SVNKitEnvironmentRepositoryIntegrationTests.TestConfiguration.class).web(NONE).profiles("subversion").run(("--spring.cloud.config.server.svn.uri=" + uri));
        EnvironmentRepository repository = this.context.getBean(EnvironmentRepository.class);
        repository.findOne("bar", "staging", "unknownlabel");
        Environment environment = repository.findOne("bar", "staging", "unknownlabel");
        assertThat(environment.getPropertySources().size()).isEqualTo(0);
    }

    @Test
    public void branchLabel() throws Exception {
        String uri = ConfigServerTestUtils.prepareLocalSvnRepo("src/test/resources/svn-config-repo", "target/config");
        this.context = new SpringApplicationBuilder(SVNKitEnvironmentRepositoryIntegrationTests.TestConfiguration.class).web(NONE).profiles("subversion").run(("--spring.cloud.config.server.svn.uri=" + uri));
        EnvironmentRepository repository = this.context.getBean(EnvironmentRepository.class);
        Environment environment = repository.findOne("bar", "staging", "demobranch");
        assertThat(environment.getPropertySources().get(0).getName().contains("bar.properties")).isTrue();
        assertThat(environment.getPropertySources().size()).isEqualTo(1);
    }

    @Configuration
    @EnableConfigurationProperties(ConfigServerProperties.class)
    @Import({ PropertyPlaceholderAutoConfiguration.class, EnvironmentRepositoryConfiguration.class })
    protected static class TestConfiguration {}
}

