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
package org.springframework.security.web.server.csrf;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.WebFilterChain;


/**
 *
 *
 * @author Eric Deandrea
 * @since 5.1
 */
@RunWith(MockitoJUnitRunner.class)
public class CsrfServerLogoutHandlerTests {
    @Mock
    private ServerCsrfTokenRepository csrfTokenRepository;

    @Mock
    private WebFilterChain filterChain;

    private MockServerWebExchange exchange;

    private WebFilterExchange filterExchange;

    private CsrfServerLogoutHandler handler;

    @Test
    public void constructorNullCsrfTokenRepository() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new CsrfServerLogoutHandler(null)).withMessage("csrfTokenRepository cannot be null").withNoCause();
    }

    @Test
    public void logoutRemovesCsrfToken() {
        this.handler.logout(this.filterExchange, new TestingAuthenticationToken("user", "password", "ROLE_USER")).block();
        Mockito.verify(this.csrfTokenRepository).saveToken(this.exchange, null);
    }
}

