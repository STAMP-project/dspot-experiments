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


import java.io.File;
import org.junit.Test;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.SearchPathLocator.Locations;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;


/**
 *
 *
 * @author Dave Syer
 */
public class MultipleJGitEnvironmentProfilePlaceholderRepositoryTests {
    private StandardEnvironment environment = new StandardEnvironment();

    private MultipleJGitEnvironmentRepository repository = new MultipleJGitEnvironmentRepository(this.environment, new MultipleJGitEnvironmentProperties());

    @Test
    public void defaultRepo() {
        Environment environment = this.repository.findOne("bar", "staging", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(2);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((this.repository.getUri()) + "/bar.properties"));
        assertVersion(environment);
    }

    @Test
    public void mappingRepo() {
        Environment environment = this.repository.findOne("application", "test1-config-repo", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(1);
        String uri = getUri("*").replace("{profile}", "test1-config-repo");
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo((uri + "/application.yml"));
        assertVersion(environment);
        assertThat(StringUtils.cleanPath(getRepository(uri).getBasedir().toString())).contains("target/repos");
    }

    @Test
    public void otherMappingRepo() {
        Environment environment = this.repository.findOne("application", "test2-config-repo", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(1);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((getUri("*").replace("{profile}", "test2-config-repo")) + "/application.properties"));
        assertVersion(environment);
    }

    @Test
    public void locationsTwoProfiles() throws Exception {
        Locations locations = this.repository.getLocations("application", "test1-config-repo,test2-config-repo", "master");
        assertThat(locations.getLocations().length).isEqualTo(1);
        assertThat(new File(locations.getLocations()[0].replace("file:", "")).getCanonicalPath()).isEqualTo(new File(getUri("*").replace("{profile}", "test2-config-repo").replace("file:", "")).getCanonicalPath());
    }

    @Test
    public void locationsMissingProfile() throws Exception {
        Locations locations = this.repository.getLocations("application", "not-there,another-not-there", "master");
        assertThat(locations.getLocations().length).isEqualTo(1);
        assertThat(new File(locations.getLocations()[0].replace("file:", "")).getCanonicalPath()).isEqualTo(new File(this.repository.getUri().replace("file:", "")).getCanonicalPath());
    }

    @Test
    public void twoMappingRepos() {
        Environment environment = this.repository.findOne("application", "test1-config-repo,test2-config-repo,missing-config-repo", "master");
        assertThat(environment.getPropertySources().size()).isEqualTo(1);
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo(((getUri("*").replace("{profile}", "test2-config-repo")) + "/application.properties"));
        assertVersion(environment);
        assertThat(new String[]{ "test1-config-repo", "test2-config-repo", "missing-config-repo" }).isEqualTo(environment.getProfiles());
    }
}

