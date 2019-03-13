/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.test.context.support;


import TestExecutionEvent.TEST_EXECUTION;
import TestExecutionEvent.TEST_METHOD;
import org.junit.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import static TestExecutionEvent.TEST_EXECUTION;
import static TestExecutionEvent.TEST_METHOD;


public class WithUserDetailsTests {
    @Test
    public void defaults() {
        WithUserDetails userDetails = AnnotationUtils.findAnnotation(WithUserDetailsTests.Annotated.class, WithUserDetails.class);
        assertThat(userDetails.value()).isEqualTo("user");
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(WithUserDetailsTests.Annotated.class, WithSecurityContext.class);
        assertThat(context.setupBefore()).isEqualTo(TEST_METHOD);
    }

    @WithUserDetails
    private static class Annotated {}

    @Test
    public void findMergedAnnotationWhenSetupExplicitThenOverridden() {
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(WithUserDetailsTests.SetupExplicit.class, WithSecurityContext.class);
        assertThat(context.setupBefore()).isEqualTo(TEST_METHOD);
    }

    @WithUserDetails(setupBefore = TEST_METHOD)
    private class SetupExplicit {}

    @Test
    public void findMergedAnnotationWhenSetupOverriddenThenOverridden() {
        WithSecurityContext context = AnnotatedElementUtils.findMergedAnnotation(WithUserDetailsTests.SetupOverridden.class, WithSecurityContext.class);
        assertThat(context.setupBefore()).isEqualTo(TEST_EXECUTION);
    }

    @WithUserDetails(setupBefore = TEST_EXECUTION)
    private class SetupOverridden {}
}

