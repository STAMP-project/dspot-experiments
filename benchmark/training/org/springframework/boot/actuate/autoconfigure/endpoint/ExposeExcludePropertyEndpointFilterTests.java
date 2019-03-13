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
package org.springframework.boot.actuate.autoconfigure.endpoint;


import org.junit.Test;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link ExposeExcludePropertyEndpointFilter}.
 *
 * @author Phillip Webb
 */
public class ExposeExcludePropertyEndpointFilterTests {
    private ExposeExcludePropertyEndpointFilter<?> filter;

    @Test
    public void createWhenEndpointTypeIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ExposeExcludePropertyEndpointFilter<>(null, new MockEnvironment(), "foo")).withMessageContaining("EndpointType must not be null");
    }

    @Test
    public void createWhenEnvironmentIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ExposeExcludePropertyEndpointFilter<>(.class, null, "foo")).withMessageContaining("Environment must not be null");
    }

    @Test
    public void createWhenPrefixIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ExposeExcludePropertyEndpointFilter<>(.class, new MockEnvironment(), null)).withMessageContaining("Prefix must not be empty");
    }

    @Test
    public void createWhenPrefixIsEmptyShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ExposeExcludePropertyEndpointFilter<>(.class, new MockEnvironment(), "")).withMessageContaining("Prefix must not be empty");
    }

    @Test
    public void matchWhenExposeIsEmptyAndExcludeIsEmptyAndInDefaultShouldMatch() {
        setupFilter("", "");
        assertThat(match(EndpointId.of("def"))).isTrue();
    }

    @Test
    public void matchWhenExposeIsEmptyAndExcludeIsEmptyAndNotInDefaultShouldNotMatch() {
        setupFilter("", "");
        assertThat(match(EndpointId.of("bar"))).isFalse();
    }

    @Test
    public void matchWhenExposeMatchesAndExcludeIsEmptyShouldMatch() {
        setupFilter("bar", "");
        assertThat(match(EndpointId.of("bar"))).isTrue();
    }

    @Test
    public void matchWhenExposeDoesNotMatchAndExcludeIsEmptyShouldNotMatch() {
        setupFilter("bar", "");
        assertThat(match(EndpointId.of("baz"))).isFalse();
    }

    @Test
    public void matchWhenExposeMatchesAndExcludeMatchesShouldNotMatch() {
        setupFilter("bar,baz", "baz");
        assertThat(match(EndpointId.of("baz"))).isFalse();
    }

    @Test
    public void matchWhenExposeMatchesAndExcludeDoesNotMatchShouldMatch() {
        setupFilter("bar,baz", "buz");
        assertThat(match(EndpointId.of("baz"))).isTrue();
    }

    @Test
    public void matchWhenExposeMatchesWithDifferentCaseShouldMatch() {
        setupFilter("bar", "");
        assertThat(match(EndpointId.of("bAr"))).isTrue();
    }

    @Test
    public void matchWhenDiscovererDoesNotMatchShouldMatch() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("foo.include", "bar");
        environment.setProperty("foo.exclude", "");
        this.filter = new ExposeExcludePropertyEndpointFilter(ExposeExcludePropertyEndpointFilterTests.DifferentTestExposableWebEndpoint.class, environment, "foo");
        assertThat(match(EndpointId.of("baz"))).isTrue();
    }

    @Test
    public void matchWhenIncludeIsAsteriskShouldMatchAll() {
        setupFilter("*", "buz");
        assertThat(match(EndpointId.of("bar"))).isTrue();
        assertThat(match(EndpointId.of("baz"))).isTrue();
        assertThat(match(EndpointId.of("buz"))).isFalse();
    }

    @Test
    public void matchWhenExcludeIsAsteriskShouldMatchNone() {
        setupFilter("bar,baz,buz", "*");
        assertThat(match(EndpointId.of("bar"))).isFalse();
        assertThat(match(EndpointId.of("baz"))).isFalse();
        assertThat(match(EndpointId.of("buz"))).isFalse();
    }

    @Test
    public void matchWhenMixedCaseShouldMatch() {
        setupFilter("foo-bar", "");
        assertThat(match(EndpointId.of("fooBar"))).isTrue();
    }

    private abstract static class TestExposableWebEndpoint implements ExposableWebEndpoint {}

    private abstract static class DifferentTestExposableWebEndpoint implements ExposableWebEndpoint {}
}

