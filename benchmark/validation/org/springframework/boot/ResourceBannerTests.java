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
package org.springframework.boot;


import AnsiOutput.Enabled.ALWAYS;
import AnsiOutput.Enabled.NEVER;
import org.junit.Test;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;


/**
 * Tests for {@link ResourceBanner}.
 *
 * @author Phillip Webb
 * @author Vedran Pavic
 */
public class ResourceBannerTests {
    @Test
    public void renderVersions() {
        Resource resource = new ByteArrayResource("banner ${a} ${spring-boot.version} ${application.version}".getBytes());
        String banner = printBanner(resource, "10.2", "2.0", null);
        assertThat(banner).startsWith("banner 1 10.2 2.0");
    }

    @Test
    public void renderWithoutVersions() {
        Resource resource = new ByteArrayResource("banner ${a} ${spring-boot.version} ${application.version}".getBytes());
        String banner = printBanner(resource, null, null, null);
        assertThat(banner).startsWith("banner 1  ");
    }

    @Test
    public void renderFormattedVersions() {
        Resource resource = new ByteArrayResource("banner ${a}${spring-boot.formatted-version}${application.formatted-version}".getBytes());
        String banner = printBanner(resource, "10.2", "2.0", null);
        assertThat(banner).startsWith("banner 1 (v10.2) (v2.0)");
    }

    @Test
    public void renderWithoutFormattedVersions() {
        Resource resource = new ByteArrayResource("banner ${a}${spring-boot.formatted-version}${application.formatted-version}".getBytes());
        String banner = printBanner(resource, null, null, null);
        assertThat(banner).startsWith("banner 1");
    }

    @Test
    public void renderWithColors() {
        Resource resource = new ByteArrayResource("${Ansi.RED}This is red.${Ansi.NORMAL}".getBytes());
        AnsiOutput.setEnabled(ALWAYS);
        String banner = printBanner(resource, null, null, null);
        assertThat(banner).startsWith("\u001b[31mThis is red.\u001b[0m");
    }

    @Test
    public void renderWithColorsButDisabled() {
        Resource resource = new ByteArrayResource("${Ansi.RED}This is red.${Ansi.NORMAL}".getBytes());
        AnsiOutput.setEnabled(NEVER);
        String banner = printBanner(resource, null, null, null);
        assertThat(banner).startsWith("This is red.");
    }

    @Test
    public void renderWithTitle() {
        Resource resource = new ByteArrayResource("banner ${application.title} ${a}".getBytes());
        String banner = printBanner(resource, null, null, "title");
        assertThat(banner).startsWith("banner title 1");
    }

    @Test
    public void renderWithoutTitle() {
        Resource resource = new ByteArrayResource("banner ${application.title} ${a}".getBytes());
        String banner = printBanner(resource, null, null, null);
        assertThat(banner).startsWith("banner  1");
    }

    private static class MockResourceBanner extends ResourceBanner {
        private final String bootVersion;

        private final String applicationVersion;

        private final String applicationTitle;

        MockResourceBanner(Resource resource, String bootVersion, String applicationVersion, String applicationTitle) {
            super(resource);
            this.bootVersion = bootVersion;
            this.applicationVersion = applicationVersion;
            this.applicationTitle = applicationTitle;
        }

        @Override
        protected String getBootVersion() {
            return this.bootVersion;
        }

        @Override
        protected String getApplicationVersion(Class<?> sourceClass) {
            return this.applicationVersion;
        }

        @Override
        protected String getApplicationTitle(Class<?> sourceClass) {
            return this.applicationTitle;
        }
    }
}

