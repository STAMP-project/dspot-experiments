/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.security.config.method;


import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 *
 *
 * @author Rob Winch
 */
public class Sec2196Tests {
    private ConfigurableApplicationContext context;

    @Test(expected = AccessDeniedException.class)
    public void genericMethodsProtected() {
        loadContext(((("<global-method-security secured-annotations=\"enabled\" pre-post-annotations=\"enabled\"/>" + "<b:bean class='") + (Sec2196Tests.Service.class.getName())) + "'/>"));
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("test", "pass", "ROLE_USER"));
        Sec2196Tests.Service service = context.getBean(Sec2196Tests.Service.class);
        service.save(new Sec2196Tests.User());
    }

    @Test
    public void genericMethodsAllowed() {
        loadContext(((("<global-method-security secured-annotations=\"enabled\" pre-post-annotations=\"enabled\"/>" + "<b:bean class='") + (Sec2196Tests.Service.class.getName())) + "'/>"));
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("test", "pass", "saveUsers"));
        Sec2196Tests.Service service = context.getBean(Sec2196Tests.Service.class);
        service.save(new Sec2196Tests.User());
    }

    public static class Service {
        @PreAuthorize("hasAuthority('saveUsers')")
        public <T extends Sec2196Tests.User> T save(T dto) {
            return dto;
        }
    }

    static class User {}
}

