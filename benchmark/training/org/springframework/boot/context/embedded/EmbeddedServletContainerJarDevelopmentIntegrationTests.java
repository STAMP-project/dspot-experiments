/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.context.embedded;


import HttpStatus.OK;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.ResponseEntity;


/**
 * Integration tests for Spring Boot's embedded servlet container support when developing
 * a jar application.
 *
 * @author Andy Wilkinson
 */
@RunWith(Parameterized.class)
public class EmbeddedServletContainerJarDevelopmentIntegrationTests extends AbstractEmbeddedServletContainerIntegrationTests {
    public EmbeddedServletContainerJarDevelopmentIntegrationTests(String name, AbstractApplicationLauncher launcher) {
        super(name, launcher);
    }

    @Test
    public void metaInfResourceFromDependencyIsAvailableViaHttp() {
        ResponseEntity<String> entity = this.rest.getForEntity("/nested-meta-inf-resource.txt", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void metaInfResourceFromDependencyIsAvailableViaServletContext() {
        ResponseEntity<String> entity = this.rest.getForEntity("/servletContext?/nested-meta-inf-resource.txt", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }
}

