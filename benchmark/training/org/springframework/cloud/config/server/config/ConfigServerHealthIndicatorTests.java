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
package org.springframework.cloud.config.server.config;


import Status.DOWN;
import Status.UP;
import java.util.Collections;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.config.ConfigServerHealthIndicator.Repository;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;


/**
 *
 *
 * @author Spencer Gibb
 */
public class ConfigServerHealthIndicatorTests {
    @Mock
    private EnvironmentRepository repository;

    @Mock(answer = Answers.RETURNS_MOCKS)
    private Environment environment;

    private ConfigServerHealthIndicator indicator;

    @Test
    public void defaultStatusWorks() {
        Mockito.when(this.repository.findOne(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), Mockito.<String>isNull())).thenReturn(this.environment);
        assertThat(this.indicator.health().getStatus()).as("wrong default status").isEqualTo(UP);
    }

    @Test
    public void exceptionStatusIsDown() {
        Mockito.when(this.repository.findOne(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), Mockito.<String>isNull())).thenThrow(new RuntimeException());
        assertThat(this.indicator.health().getStatus()).as("wrong exception status").isEqualTo(DOWN);
    }

    @Test
    public void customLabelWorks() {
        Repository repo = new Repository();
        repo.setName("myname");
        repo.setProfiles("myprofile");
        repo.setLabel("mylabel");
        this.indicator.setRepositories(Collections.singletonMap("myname", repo));
        Mockito.when(this.repository.findOne("myname", "myprofile", "mylabel")).thenReturn(this.environment);
        assertThat(this.indicator.health().getStatus()).as("wrong default status").isEqualTo(UP);
    }
}

