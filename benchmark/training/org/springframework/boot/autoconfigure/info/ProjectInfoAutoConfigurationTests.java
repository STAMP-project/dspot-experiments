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
package org.springframework.boot.autoconfigure.info;


import java.util.Properties;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ProjectInfoAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class ProjectInfoAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class, ProjectInfoAutoConfiguration.class));

    @Test
    public void gitPropertiesUnavailableIfResourceNotAvailable() {
        this.contextRunner.run(( context) -> assertThat(context.getBeansOfType(.class)).isEmpty());
    }

    @Test
    public void gitPropertiesWithNoData() {
        this.contextRunner.withPropertyValues(("spring.info.git.location=" + "classpath:/org/springframework/boot/autoconfigure/info/git-no-data.properties")).run(( context) -> {
            GitProperties gitProperties = context.getBean(.class);
            assertThat(gitProperties.getBranch()).isNull();
        });
    }

    @Test
    public void gitPropertiesFallbackWithGitPropertiesBean() {
        this.contextRunner.withUserConfiguration(ProjectInfoAutoConfigurationTests.CustomInfoPropertiesConfiguration.class).withPropertyValues(("spring.info.git.location=" + "classpath:/org/springframework/boot/autoconfigure/info/git.properties")).run(( context) -> {
            GitProperties gitProperties = context.getBean(.class);
            assertThat(gitProperties).isSameAs(context.getBean("customGitProperties"));
        });
    }

    @Test
    public void gitPropertiesUsesUtf8ByDefault() {
        this.contextRunner.withPropertyValues("spring.info.git.location=classpath:/org/springframework/boot/autoconfigure/info/git.properties").run(( context) -> {
            GitProperties gitProperties = context.getBean(.class);
            assertThat(gitProperties.get("commit.charset")).isEqualTo("test?");
        });
    }

    @Test
    public void gitPropertiesEncodingCanBeConfigured() {
        this.contextRunner.withPropertyValues("spring.info.git.encoding=US-ASCII", "spring.info.git.location=classpath:/org/springframework/boot/autoconfigure/info/git.properties").run(( context) -> {
            GitProperties gitProperties = context.getBean(.class);
            assertThat(gitProperties.get("commit.charset")).isNotEqualTo("test?");
        });
    }

    @Test
    public void buildPropertiesDefaultLocation() {
        this.contextRunner.run(( context) -> {
            BuildProperties buildProperties = context.getBean(.class);
            assertThat(buildProperties.getGroup()).isEqualTo("com.example");
            assertThat(buildProperties.getArtifact()).isEqualTo("demo");
            assertThat(buildProperties.getName()).isEqualTo("Demo Project");
            assertThat(buildProperties.getVersion()).isEqualTo("0.0.1-SNAPSHOT");
            assertThat(buildProperties.getTime().toEpochMilli()).isEqualTo(1457100965000L);
        });
    }

    @Test
    public void buildPropertiesCustomLocation() {
        this.contextRunner.withPropertyValues(("spring.info.build.location=" + "classpath:/org/springframework/boot/autoconfigure/info/build-info.properties")).run(( context) -> {
            BuildProperties buildProperties = context.getBean(.class);
            assertThat(buildProperties.getGroup()).isEqualTo("com.example.acme");
            assertThat(buildProperties.getArtifact()).isEqualTo("acme");
            assertThat(buildProperties.getName()).isEqualTo("acme");
            assertThat(buildProperties.getVersion()).isEqualTo("1.0.1-SNAPSHOT");
            assertThat(buildProperties.getTime().toEpochMilli()).isEqualTo(1457088120000L);
        });
    }

    @Test
    public void buildPropertiesCustomInvalidLocation() {
        this.contextRunner.withPropertyValues(("spring.info.build.location=" + "classpath:/org/acme/no-build-info.properties")).run(( context) -> assertThat(context.getBeansOfType(.class)).hasSize(0));
    }

    @Test
    public void buildPropertiesFallbackWithBuildInfoBean() {
        this.contextRunner.withUserConfiguration(ProjectInfoAutoConfigurationTests.CustomInfoPropertiesConfiguration.class).run(( context) -> {
            BuildProperties buildProperties = context.getBean(.class);
            assertThat(buildProperties).isSameAs(context.getBean("customBuildProperties"));
        });
    }

    @Test
    public void buildPropertiesUsesUtf8ByDefault() {
        this.contextRunner.withPropertyValues("spring.info.build.location=classpath:/org/springframework/boot/autoconfigure/info/build-info.properties").run(( context) -> {
            BuildProperties buildProperties = context.getBean(.class);
            assertThat(buildProperties.get("charset")).isEqualTo("test?");
        });
    }

    @Test
    public void buildPropertiesEncodingCanBeConfigured() {
        this.contextRunner.withPropertyValues("spring.info.build.encoding=US-ASCII", "spring.info.build.location=classpath:/org/springframework/boot/autoconfigure/info/build-info.properties").run(( context) -> {
            BuildProperties buildProperties = context.getBean(.class);
            assertThat(buildProperties.get("charset")).isNotEqualTo("test?");
        });
    }

    @Configuration
    static class CustomInfoPropertiesConfiguration {
        @Bean
        public GitProperties customGitProperties() {
            return new GitProperties(new Properties());
        }

        @Bean
        public BuildProperties customBuildProperties() {
            return new BuildProperties(new Properties());
        }
    }
}

