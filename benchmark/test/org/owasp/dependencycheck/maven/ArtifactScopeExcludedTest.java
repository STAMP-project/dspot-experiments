/**
 * This file is part of dependency-check-maven.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2017 Josh Cain. All Rights Reserved.
 */
package org.owasp.dependencycheck.maven;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.owasp.dependencycheck.utils.Filter;


@RunWith(Parameterized.class)
public class ArtifactScopeExcludedTest {
    private final boolean skipTestScope;

    private final boolean skipProvidedScope;

    private final boolean skipSystemScope;

    private final boolean skipRuntimeScope;

    private final String testString;

    private final boolean expectedResult;

    public ArtifactScopeExcludedTest(final ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder builder) {
        this.skipTestScope = builder.skipTestScope;
        this.skipProvidedScope = builder.skipProvidedScope;
        this.skipSystemScope = builder.skipSystemScope;
        this.skipRuntimeScope = builder.skipRuntimeScope;
        this.testString = builder.testString;
        this.expectedResult = builder.expectedResult;
    }

    @Test
    public void shouldExcludeArtifact() {
        final Filter<String> artifactScopeExcluded = new ArtifactScopeExcluded(skipTestScope, skipProvidedScope, skipSystemScope, skipRuntimeScope);
        MatcherAssert.assertThat(expectedResult, Is.is(IsEqual.equalTo(artifactScopeExcluded.passes(testString))));
    }

    public static final class ArtifactScopeExcludedTestBuilder {
        private boolean skipTestScope;

        private boolean skipProvidedScope;

        private boolean skipSystemScope;

        private boolean skipRuntimeScope;

        private String testString;

        private boolean expectedResult;

        private ArtifactScopeExcludedTestBuilder() {
        }

        public static ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder pluginDefaults() {
            return new ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder().withSkipTestScope(true).withSkipProvidedScope(false).withSkipRuntimeScope(false).withSkipSystemScope(false);
        }

        public ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder withSkipTestScope(final boolean skipTestScope) {
            this.skipTestScope = skipTestScope;
            return this;
        }

        public ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder withSkipProvidedScope(final boolean skipProvidedScope) {
            this.skipProvidedScope = skipProvidedScope;
            return this;
        }

        public ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder withSkipSystemScope(final boolean skipSystemScope) {
            this.skipSystemScope = skipSystemScope;
            return this;
        }

        public ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder withSkipRuntimeScope(final boolean skipRuntimeScope) {
            this.skipRuntimeScope = skipRuntimeScope;
            return this;
        }

        public ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder withTestString(final String testString) {
            this.testString = testString;
            return this;
        }

        public ArtifactScopeExcludedTest.ArtifactScopeExcludedTestBuilder withExpectedResult(final boolean expectedResult) {
            this.expectedResult = expectedResult;
            return this;
        }

        @Override
        public String toString() {
            return String.format("new ArtifactScopeExcluded(%s, %s, %s, %s).passes(\"%s\") == %s;", skipTestScope, skipProvidedScope, skipSystemScope, skipRuntimeScope, testString, expectedResult);
        }
    }
}

