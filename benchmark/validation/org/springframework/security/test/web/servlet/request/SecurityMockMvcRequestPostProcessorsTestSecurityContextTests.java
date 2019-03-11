/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.security.test.web.servlet.request;


import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.web.support.WebTestUtils;
import org.springframework.security.web.context.SecurityContextRepository;


@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(WebTestUtils.class)
public class SecurityMockMvcRequestPostProcessorsTestSecurityContextTests {
    @Mock
    private SecurityContext context;

    @Mock
    private SecurityContextRepository repository;

    private MockHttpServletRequest request;

    @Test
    public void testSecurityContextSaves() {
        TestSecurityContextHolder.setContext(context);
        SecurityMockMvcRequestPostProcessors.testSecurityContext().postProcessRequest(request);
        Mockito.verify(repository).saveContext(ArgumentMatchers.eq(context), ArgumentMatchers.eq(request), ArgumentMatchers.any(HttpServletResponse.class));
    }

    // Ensure it does not fail if TestSecurityContextHolder is not initialized
    @Test
    public void testSecurityContextNoContext() {
        SecurityMockMvcRequestPostProcessors.testSecurityContext().postProcessRequest(request);
        Mockito.verify(repository, Mockito.never()).saveContext(ArgumentMatchers.any(SecurityContext.class), ArgumentMatchers.eq(request), ArgumentMatchers.any(HttpServletResponse.class));
    }
}

