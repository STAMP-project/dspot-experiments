/**
 * Copyright 2014-2018 the original author or authors.
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
package org.springframework.session.security.web.authentication;


import HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link SpringSessionRememberMeServices}.
 *
 * @author Vedran Pavic
 */
public class SpringSessionRememberMeServicesTests {
    private SpringSessionRememberMeServices rememberMeServices;

    @Test
    public void create() {
        this.rememberMeServices = new SpringSessionRememberMeServices();
        assertThat(ReflectionTestUtils.getField(this.rememberMeServices, "rememberMeParameterName")).isEqualTo("remember-me");
        assertThat(ReflectionTestUtils.getField(this.rememberMeServices, "alwaysRemember")).isEqualTo(false);
        assertThat(ReflectionTestUtils.getField(this.rememberMeServices, "validitySeconds")).isEqualTo(2592000);
    }

    @Test
    public void createWithCustomParameter() {
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.setRememberMeParameterName("test-param");
        assertThat(ReflectionTestUtils.getField(this.rememberMeServices, "rememberMeParameterName")).isEqualTo("test-param");
    }

    @Test
    public void createWithNullParameter() {
        this.rememberMeServices = new SpringSessionRememberMeServices();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> this.rememberMeServices.setRememberMeParameterName(null)).withMessage("rememberMeParameterName cannot be empty or null");
    }

    @Test
    public void createWithAlwaysRemember() {
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.setAlwaysRemember(true);
        assertThat(ReflectionTestUtils.getField(this.rememberMeServices, "alwaysRemember")).isEqualTo(true);
    }

    @Test
    public void createWithCustomValidity() {
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.setValiditySeconds(100000);
        assertThat(ReflectionTestUtils.getField(this.rememberMeServices, "validitySeconds")).isEqualTo(100000);
    }

    @Test
    public void autoLogin() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.autoLogin(request, response);
        Mockito.verifyZeroInteractions(request, response);
    }

    // gh-752
    @Test
    public void loginFailRemoveSecurityContext() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        BDDMockito.given(request.getSession(ArgumentMatchers.eq(false))).willReturn(session);
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.loginFail(request, response);
        Mockito.verify(request, Mockito.times(1)).getSession(ArgumentMatchers.eq(false));
        Mockito.verify(session, Mockito.times(1)).removeAttribute(SPRING_SECURITY_CONTEXT_KEY);
        Mockito.verifyZeroInteractions(request, response, session);
    }

    @Test
    public void loginSuccess() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        BDDMockito.given(request.getParameter(ArgumentMatchers.eq("remember-me"))).willReturn("true");
        BDDMockito.given(request.getSession()).willReturn(session);
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.loginSuccess(request, response, authentication);
        Mockito.verify(request, Mockito.times(1)).getParameter(ArgumentMatchers.eq("remember-me"));
        Mockito.verify(request, Mockito.times(1)).getSession();
        Mockito.verify(request, Mockito.times(1)).setAttribute(ArgumentMatchers.eq(REMEMBER_ME_LOGIN_ATTR), ArgumentMatchers.eq(true));
        Mockito.verify(session, Mockito.times(1)).setMaxInactiveInterval(ArgumentMatchers.eq(2592000));
        Mockito.verifyZeroInteractions(request, response, session, authentication);
    }

    @Test
    public void loginSuccessWithCustomParameter() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        BDDMockito.given(request.getParameter(ArgumentMatchers.eq("test-param"))).willReturn("true");
        BDDMockito.given(request.getSession()).willReturn(session);
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.setRememberMeParameterName("test-param");
        this.rememberMeServices.loginSuccess(request, response, authentication);
        Mockito.verify(request, Mockito.times(1)).getParameter(ArgumentMatchers.eq("test-param"));
        Mockito.verify(request, Mockito.times(1)).getSession();
        Mockito.verify(request, Mockito.times(1)).setAttribute(ArgumentMatchers.eq(REMEMBER_ME_LOGIN_ATTR), ArgumentMatchers.eq(true));
        Mockito.verify(session, Mockito.times(1)).setMaxInactiveInterval(ArgumentMatchers.eq(2592000));
        Mockito.verifyZeroInteractions(request, response, session, authentication);
    }

    @Test
    public void loginSuccessWithAlwaysRemember() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        BDDMockito.given(request.getSession()).willReturn(session);
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.setAlwaysRemember(true);
        this.rememberMeServices.loginSuccess(request, response, authentication);
        Mockito.verify(request, Mockito.times(1)).getSession();
        Mockito.verify(request, Mockito.times(1)).setAttribute(ArgumentMatchers.eq(REMEMBER_ME_LOGIN_ATTR), ArgumentMatchers.eq(true));
        Mockito.verify(session, Mockito.times(1)).setMaxInactiveInterval(ArgumentMatchers.eq(2592000));
        Mockito.verifyZeroInteractions(request, response, session, authentication);
    }

    @Test
    public void loginSuccessWithCustomValidity() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        BDDMockito.given(request.getParameter(ArgumentMatchers.eq("remember-me"))).willReturn("true");
        BDDMockito.given(request.getSession()).willReturn(session);
        this.rememberMeServices = new SpringSessionRememberMeServices();
        this.rememberMeServices.setValiditySeconds(100000);
        this.rememberMeServices.loginSuccess(request, response, authentication);
        Mockito.verify(request, Mockito.times(1)).getParameter(ArgumentMatchers.eq("remember-me"));
        Mockito.verify(request, Mockito.times(1)).getSession();
        Mockito.verify(request, Mockito.times(1)).setAttribute(ArgumentMatchers.eq(REMEMBER_ME_LOGIN_ATTR), ArgumentMatchers.eq(true));
        Mockito.verify(session, Mockito.times(1)).setMaxInactiveInterval(ArgumentMatchers.eq(100000));
        Mockito.verifyZeroInteractions(request, response, session, authentication);
    }
}

