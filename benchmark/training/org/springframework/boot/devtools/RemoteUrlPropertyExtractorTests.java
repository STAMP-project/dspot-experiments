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
package org.springframework.boot.devtools;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link RemoteUrlPropertyExtractor}.
 *
 * @author Phillip Webb
 */
public class RemoteUrlPropertyExtractorTests {
    @Test
    public void missingUrl() {
        assertThatIllegalStateException().isThrownBy(this::doTest).withMessageContaining("No remote URL specified");
    }

    @Test
    public void malformedUrl() {
        assertThatIllegalStateException().isThrownBy(() -> doTest("::://wibble")).withMessageContaining("Malformed URL '::://wibble'");
    }

    @Test
    public void multipleUrls() {
        assertThatIllegalStateException().isThrownBy(() -> doTest("http://localhost:8080", "http://localhost:9090")).withMessageContaining("Multiple URLs specified");
    }

    @Test
    public void validUrl() {
        ApplicationContext context = doTest("http://localhost:8080");
        assertThat(context.getEnvironment().getProperty("remoteUrl")).isEqualTo("http://localhost:8080");
        assertThat(context.getEnvironment().getProperty("spring.thymeleaf.cache")).isNull();
    }

    @Test
    public void cleanValidUrl() {
        ApplicationContext context = doTest("http://localhost:8080/");
        assertThat(context.getEnvironment().getProperty("remoteUrl")).isEqualTo("http://localhost:8080");
    }

    @Configuration
    static class Config {}
}

