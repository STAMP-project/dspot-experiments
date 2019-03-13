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


import org.junit.Test;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.core.env.StandardEnvironment;


/**
 *
 *
 * @author Spencer Gibb
 * @author Dave Syer
 */
public class MultipleJGitEnvironmentLabelPlaceholderRepositoryTests {
    private StandardEnvironment environment = new StandardEnvironment();

    private MultipleJGitEnvironmentRepository repository = new MultipleJGitEnvironmentRepository(this.environment, new MultipleJGitEnvironmentProperties());

    private String defaultUri;

    @Test
    public void defaultRepo() {
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(1);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.defaultUri) + "application.yml"));
        assertVersion(environment);
    }

    @Test
    public void missingRepo() {
        Environment environment = this.repository.findOne("missing-config-repo", "staging", "master");
        assertThat(environment.getPropertySources().size()).as(("Wrong property sources: " + environment)).isEqualTo(1);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.defaultUri) + "application.yml"));
        assertVersion(environment);
    }

    @Test
    public void defaultLabelRepo() {
        Environment environment = this.repository.findOne("bar", "staging", null);
        assertThat(environment.getPropertySources().size()).isEqualTo(1);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.defaultUri) + "application.yml"));
        assertVersion(environment);
    }
}

