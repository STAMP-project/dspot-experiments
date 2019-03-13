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
package org.springframework.boot.actuate.autoconfigure.info;


import java.util.Map;
import java.util.Properties;
import org.junit.Test;
import org.springframework.boot.actuate.info.BuildInfoContributor;
import org.springframework.boot.actuate.info.GitInfoContributor;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link InfoContributorAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class InfoContributorAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void disableEnvContributor() {
        load("management.info.env.enabled:false");
        Map<String, InfoContributor> beans = this.context.getBeansOfType(InfoContributor.class);
        assertThat(beans).hasSize(0);
    }

    @Test
    public void defaultInfoContributorsDisabled() {
        load("management.info.defaults.enabled:false");
        Map<String, InfoContributor> beans = this.context.getBeansOfType(InfoContributor.class);
        assertThat(beans).hasSize(0);
    }

    @Test
    public void defaultInfoContributorsDisabledWithCustomOne() {
        load(InfoContributorAutoConfigurationTests.CustomInfoContributorConfiguration.class, "management.info.defaults.enabled:false");
        Map<String, InfoContributor> beans = this.context.getBeansOfType(InfoContributor.class);
        assertThat(beans).hasSize(1);
        assertThat(this.context.getBean("customInfoContributor")).isSameAs(beans.values().iterator().next());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void gitPropertiesDefaultMode() {
        load(InfoContributorAutoConfigurationTests.GitPropertiesConfiguration.class);
        Map<String, InfoContributor> beans = this.context.getBeansOfType(InfoContributor.class);
        assertThat(beans).containsKeys("gitInfoContributor");
        Map<String, Object> content = invokeContributor(this.context.getBean("gitInfoContributor", InfoContributor.class));
        Object git = content.get("git");
        assertThat(git).isInstanceOf(Map.class);
        Map<String, Object> gitInfo = ((Map<String, Object>) (git));
        assertThat(gitInfo).containsOnlyKeys("branch", "commit");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void gitPropertiesFullMode() {
        load(InfoContributorAutoConfigurationTests.GitPropertiesConfiguration.class, "management.info.git.mode=full");
        Map<String, Object> content = invokeContributor(this.context.getBean("gitInfoContributor", InfoContributor.class));
        Object git = content.get("git");
        assertThat(git).isInstanceOf(Map.class);
        Map<String, Object> gitInfo = ((Map<String, Object>) (git));
        assertThat(gitInfo).containsOnlyKeys("branch", "commit", "foo");
        assertThat(gitInfo.get("foo")).isEqualTo("bar");
    }

    @Test
    public void customGitInfoContributor() {
        load(InfoContributorAutoConfigurationTests.CustomGitInfoContributorConfiguration.class);
        assertThat(this.context.getBean(GitInfoContributor.class)).isSameAs(this.context.getBean("customGitInfoContributor"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void buildProperties() {
        load(InfoContributorAutoConfigurationTests.BuildPropertiesConfiguration.class);
        Map<String, InfoContributor> beans = this.context.getBeansOfType(InfoContributor.class);
        assertThat(beans).containsKeys("buildInfoContributor");
        Map<String, Object> content = invokeContributor(this.context.getBean("buildInfoContributor", InfoContributor.class));
        Object build = content.get("build");
        assertThat(build).isInstanceOf(Map.class);
        Map<String, Object> buildInfo = ((Map<String, Object>) (build));
        assertThat(buildInfo).containsOnlyKeys("group", "artifact", "foo");
        assertThat(buildInfo.get("foo")).isEqualTo("bar");
    }

    @Test
    public void customBuildInfoContributor() {
        load(InfoContributorAutoConfigurationTests.CustomBuildInfoContributorConfiguration.class);
        assertThat(this.context.getBean(BuildInfoContributor.class)).isSameAs(this.context.getBean("customBuildInfoContributor"));
    }

    @Configuration
    static class GitPropertiesConfiguration {
        @Bean
        public GitProperties gitProperties() {
            Properties properties = new Properties();
            properties.put("branch", "master");
            properties.put("commit.id", "abcdefg");
            properties.put("foo", "bar");
            return new GitProperties(properties);
        }
    }

    @Configuration
    static class BuildPropertiesConfiguration {
        @Bean
        public BuildProperties buildProperties() {
            Properties properties = new Properties();
            properties.put("group", "com.example");
            properties.put("artifact", "demo");
            properties.put("foo", "bar");
            return new BuildProperties(properties);
        }
    }

    @Configuration
    static class CustomInfoContributorConfiguration {
        @Bean
        public InfoContributor customInfoContributor() {
            return ( builder) -> {
            };
        }
    }

    @Configuration
    static class CustomGitInfoContributorConfiguration {
        @Bean
        public GitInfoContributor customGitInfoContributor() {
            return new GitInfoContributor(new GitProperties(new Properties()));
        }
    }

    @Configuration
    static class CustomBuildInfoContributorConfiguration {
        @Bean
        public BuildInfoContributor customBuildInfoContributor() {
            return new BuildInfoContributor(new BuildProperties(new Properties()));
        }
    }
}

