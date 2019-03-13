/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.config.web.server;


import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.reactive.CorsConfigurationSource;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class CorsSpecTests {
    @Mock
    private CorsConfigurationSource source;

    @Mock
    private ApplicationContext context;

    ServerHttpSecurity http;

    HttpHeaders expectedHeaders = new HttpHeaders();

    Set<String> headerNamesNotPresent = new HashSet<>();

    @Test
    public void corsWhenEnabledThenAccessControlAllowOriginAndSecurityHeaders() {
        this.http.cors().configurationSource(this.source);
        this.expectedHeaders.set("Access-Control-Allow-Origin", "*");
        this.expectedHeaders.set("X-Frame-Options", "DENY");
        assertHeaders();
    }

    @Test
    public void corsWhenCorsConfigurationSourceBeanThenAccessControlAllowOriginAndSecurityHeaders() {
        Mockito.when(this.context.getBeanNamesForType(ArgumentMatchers.any(ResolvableType.class))).thenReturn(new String[]{ "source" }, new String[0]);
        Mockito.when(this.context.getBean("source")).thenReturn(this.source);
        this.expectedHeaders.set("Access-Control-Allow-Origin", "*");
        this.expectedHeaders.set("X-Frame-Options", "DENY");
        assertHeaders();
    }

    @Test
    public void corsWhenNoConfigurationSourceThenNoCorsHeaders() {
        Mockito.when(this.context.getBeanNamesForType(ArgumentMatchers.any(ResolvableType.class))).thenReturn(new String[0]);
        this.headerNamesNotPresent.add("Access-Control-Allow-Origin");
        assertHeaders();
    }
}

