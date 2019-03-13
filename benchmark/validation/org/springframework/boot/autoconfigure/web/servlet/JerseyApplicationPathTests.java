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
package org.springframework.boot.autoconfigure.web.servlet;


import org.junit.Test;


/**
 * Tests for {@link JerseyApplicationPath}.
 *
 * @author Madhura Bhave
 */
public class JerseyApplicationPathTests {
    @Test
    public void getRelativePathReturnsRelativePath() {
        assertThat(getRelativePath("boot")).isEqualTo("spring/boot");
        assertThat(getRelativePath("boot")).isEqualTo("spring/boot");
        assertThat(getRelativePath("/boot")).isEqualTo("spring/boot");
        assertThat(getRelativePath("/boot")).isEqualTo("spring/boot");
    }

    @Test
    public void getPrefixWhenHasSimplePathReturnPath() {
        assertThat(getPrefix()).isEqualTo("spring");
    }

    @Test
    public void getPrefixWhenHasPatternRemovesPattern() {
        assertThat(getPrefix()).isEqualTo("spring");
    }

    @Test
    public void getPrefixWhenPathEndsWithSlashRemovesSlash() {
        assertThat(getPrefix()).isEqualTo("spring");
    }

    @Test
    public void getUrlMappingWhenPathIsEmptyReturnsSlash() {
        assertThat(getUrlMapping()).isEqualTo("/*");
    }

    @Test
    public void getUrlMappingWhenPathIsSlashReturnsSlash() {
        assertThat(getUrlMapping()).isEqualTo("/*");
    }

    @Test
    public void getUrlMappingWhenPathContainsStarReturnsPath() {
        assertThat(getUrlMapping()).isEqualTo("/spring/*.do");
    }

    @Test
    public void getUrlMappingWhenHasPathNotEndingSlashReturnsSlashStarPattern() {
        assertThat(getUrlMapping()).isEqualTo("/spring/boot/*");
    }

    @Test
    public void getUrlMappingWhenHasPathDoesNotStartWithSlashPrependsSlash() {
        assertThat(getUrlMapping()).isEqualTo("/spring/boot/*");
    }

    @Test
    public void getUrlMappingWhenHasPathEndingWithSlashReturnsSlashStarPattern() {
        assertThat(getUrlMapping()).isEqualTo("/spring/boot/*");
    }
}

