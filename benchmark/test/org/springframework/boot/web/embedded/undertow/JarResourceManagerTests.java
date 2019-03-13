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
package org.springframework.boot.web.embedded.undertow;


import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceManager;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for {@link JarResourceManager}.
 *
 * @author Andy Wilkinson
 */
public class JarResourceManagerTests {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ResourceManager resourceManager;

    @Test
    public void emptyPathIsHandledCorrectly() throws IOException {
        Resource resource = this.resourceManager.getResource("");
        assertThat(resource).isNotNull();
        assertThat(resource.isDirectory()).isTrue();
    }

    @Test
    public void rootPathIsHandledCorrectly() throws IOException {
        Resource resource = this.resourceManager.getResource("/");
        assertThat(resource).isNotNull();
        assertThat(resource.isDirectory()).isTrue();
    }

    @Test
    public void resourceIsFoundInJarFile() throws IOException {
        Resource resource = this.resourceManager.getResource("/hello.txt");
        assertThat(resource).isNotNull();
        assertThat(resource.isDirectory()).isFalse();
        assertThat(resource.getContentLength()).isEqualTo(5);
    }

    @Test
    public void resourceIsFoundInJarFileWithoutLeadingSlash() throws IOException {
        Resource resource = this.resourceManager.getResource("hello.txt");
        assertThat(resource).isNotNull();
        assertThat(resource.isDirectory()).isFalse();
        assertThat(resource.getContentLength()).isEqualTo(5);
    }
}

