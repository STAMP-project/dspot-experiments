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
package org.springframework.boot.context.properties.migrator;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link PropertiesMigrationListener}.
 *
 * @author Stephane Nicoll
 */
public class PropertiesMigrationListenerTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    private ConfigurableApplicationContext context;

    @Test
    public void sampleReport() {
        this.context = createSampleApplication().run("--banner.charset=UTF8");
        assertThat(this.output.toString()).contains("commandLineArgs").contains("spring.banner.charset").contains("Each configuration key has been temporarily mapped").doesNotContain("Please refer to the migration guide");
    }

    @Configuration
    public static class TestApplication {}
}

