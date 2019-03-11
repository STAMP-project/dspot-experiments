/**
 * Copyright 2012-2016 the original author or authors.
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
package org.springframework.security.web.csrf;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class LazyCsrfTokenRepositoryTests {
    @Mock
    CsrfTokenRepository delegate;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    LazyCsrfTokenRepository repository;

    DefaultCsrfToken token;

    @Test(expected = IllegalArgumentException.class)
    public void constructNullDelegateThrowsIllegalArgumentException() {
        new LazyCsrfTokenRepository(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateTokenNullResponseAttribute() {
        this.repository.generateToken(Mockito.mock(HttpServletRequest.class));
    }

    @Test
    public void generateTokenGetTokenSavesToken() {
        CsrfToken newToken = this.repository.generateToken(this.request);
        newToken.getToken();
        Mockito.verify(this.delegate).saveToken(this.token, this.request, this.response);
    }

    @Test
    public void saveNonNullDoesNothing() {
        this.repository.saveToken(this.token, this.request, this.response);
        Mockito.verifyZeroInteractions(this.delegate);
    }

    @Test
    public void saveNullDelegates() {
        this.repository.saveToken(null, this.request, this.response);
        Mockito.verify(this.delegate).saveToken(null, this.request, this.response);
    }

    @Test
    public void loadTokenDelegates() {
        Mockito.when(this.delegate.loadToken(this.request)).thenReturn(this.token);
        CsrfToken loadToken = this.repository.loadToken(this.request);
        assertThat(loadToken).isSameAs(this.token);
        Mockito.verify(this.delegate).loadToken(this.request);
    }
}

