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
package org.springframework.security.web.server.authentication.logout;


import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;


/**
 *
 *
 * @author Eric Deandrea
 * @since 5.1
 */
public class HttpStatusReturningServerLogoutSuccessHandlerTests {
    @Test
    public void defaultHttpStatusBeingReturned() {
        WebFilterExchange filterExchange = HttpStatusReturningServerLogoutSuccessHandlerTests.buildFilterExchange();
        new HttpStatusReturningServerLogoutSuccessHandler().onLogoutSuccess(filterExchange, Mockito.mock(Authentication.class)).block();
        assertThat(filterExchange.getExchange().getResponse().getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void customHttpStatusBeingReturned() {
        WebFilterExchange filterExchange = HttpStatusReturningServerLogoutSuccessHandlerTests.buildFilterExchange();
        new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.NO_CONTENT).onLogoutSuccess(filterExchange, Mockito.mock(Authentication.class)).block();
        assertThat(filterExchange.getExchange().getResponse().getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    public void nullHttpStatusThrowsException() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new HttpStatusReturningServerLogoutSuccessHandler(null)).withMessage("The provided HttpStatus must not be null.");
    }
}

