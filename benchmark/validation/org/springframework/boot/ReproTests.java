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
package org.springframework.boot;


import WebApplicationType.NONE;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Profiles;


/**
 * Tests to reproduce reported issues.
 *
 * @author Phillip Webb
 * @author Dave Syer
 */
public class ReproTests {
    private ConfigurableApplicationContext context;

    @Test
    public void enableProfileViaApplicationProperties() {
        // gh-308
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        this.context = application.run("--spring.config.name=enableprofileviaapplicationproperties", "--spring.profiles.active=dev");
        assertThat(this.context.getEnvironment().acceptsProfiles(Profiles.of("dev"))).isTrue();
        assertThat(this.context.getEnvironment().acceptsProfiles(Profiles.of("a"))).isTrue();
    }

    @Test
    public void activeProfilesWithYamlAndCommandLine() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro";
        this.context = application.run(configName, "--spring.profiles.active=B");
        assertVersionProperty(this.context, "B", "B");
    }

    @Test
    public void activeProfilesWithYamlOnly() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro";
        this.context = application.run(configName);
        assertVersionProperty(this.context, "B", "B");
    }

    @Test
    public void orderActiveProfilesWithYamlOnly() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro-ordered";
        this.context = application.run(configName);
        assertVersionProperty(this.context, "B", "A", "B");
    }

    @Test
    public void commandLineBeatsProfilesWithYaml() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro";
        this.context = application.run(configName, "--spring.profiles.active=C");
        assertVersionProperty(this.context, "C", "C");
    }

    @Test
    public void orderProfilesWithYaml() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro";
        this.context = application.run(configName, "--spring.profiles.active=A,C");
        assertVersionProperty(this.context, "C", "A", "C");
    }

    @Test
    public void reverseOrderOfProfilesWithYaml() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro";
        this.context = application.run(configName, "--spring.profiles.active=C,A");
        assertVersionProperty(this.context, "A", "C", "A");
    }

    @Test
    public void activeProfilesWithYamlAndCommandLineAndNoOverride() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro-without-override";
        this.context = application.run(configName, "--spring.profiles.active=B");
        assertVersionProperty(this.context, "B", "B");
    }

    @Test
    public void activeProfilesWithYamlOnlyAndNoOverride() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro-without-override";
        this.context = application.run(configName);
        assertVersionProperty(this.context, null);
    }

    @Test
    public void commandLineBeatsProfilesWithYamlAndNoOverride() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro-without-override";
        this.context = application.run(configName, "--spring.profiles.active=C");
        assertVersionProperty(this.context, "C", "C");
    }

    @Test
    public void orderProfilesWithYamlAndNoOverride() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro-without-override";
        this.context = application.run(configName, "--spring.profiles.active=A,C");
        assertVersionProperty(this.context, "C", "A", "C");
    }

    @Test
    public void reverseOrderOfProfilesWithYamlAndNoOverride() {
        // gh-322, gh-342
        SpringApplication application = new SpringApplication(ReproTests.Config.class);
        application.setWebApplicationType(NONE);
        String configName = "--spring.config.name=activeprofilerepro-without-override";
        this.context = application.run(configName, "--spring.profiles.active=C,A");
        assertVersionProperty(this.context, "A", "C", "A");
    }

    @Configuration
    public static class Config {}
}

