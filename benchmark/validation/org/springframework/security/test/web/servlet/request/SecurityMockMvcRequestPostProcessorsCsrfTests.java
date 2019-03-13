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


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class SecurityMockMvcRequestPostProcessorsCsrfTests {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    SecurityMockMvcRequestPostProcessorsCsrfTests.Config.TheController controller;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    MockMvc mockMvc;

    // gh-3881
    @Test
    public void csrfWithStandalone() throws Exception {
        // @formatter:off
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller).apply(SecurityMockMvcConfigurers.springSecurity(this.springSecurityFilterChain)).build();
        this.mockMvc.perform(post("/").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().is2xxSuccessful()).andExpect(SecurityMockMvcRequestPostProcessorsCsrfTests.csrfAsParam());
        // @formatter:on
    }

    @Test
    public void csrfWithParam() throws Exception {
        // @formatter:off
        this.mockMvc.perform(post("/").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().is2xxSuccessful()).andExpect(SecurityMockMvcRequestPostProcessorsCsrfTests.csrfAsParam());
        // @formatter:on
    }

    @Test
    public void csrfWithHeader() throws Exception {
        // @formatter:off
        this.mockMvc.perform(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader())).andExpect(status().is2xxSuccessful()).andExpect(SecurityMockMvcRequestPostProcessorsCsrfTests.csrfAsHeader());
        // @formatter:on
    }

    @Test
    public void csrfWithInvalidParam() throws Exception {
        // @formatter:off
        this.mockMvc.perform(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().useInvalidToken())).andExpect(status().isForbidden()).andExpect(SecurityMockMvcRequestPostProcessorsCsrfTests.csrfAsParam());
        // @formatter:on
    }

    @Test
    public void csrfWithInvalidHeader() throws Exception {
        // @formatter:off
        this.mockMvc.perform(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader().useInvalidToken())).andExpect(status().isForbidden()).andExpect(SecurityMockMvcRequestPostProcessorsCsrfTests.csrfAsHeader());
        // @formatter:on
    }

    // SEC-3097
    @Test
    public void csrfWithWrappedRequest() throws Exception {
        // @formatter:off
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(new SecurityMockMvcRequestPostProcessorsCsrfTests.SessionRepositoryFilter()).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        this.mockMvc.perform(post("/").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().is2xxSuccessful()).andExpect(SecurityMockMvcRequestPostProcessorsCsrfTests.csrfAsParam());
        // @formatter:on
    }

    // gh-4016
    @Test
    public void csrfWhenUsedThenDoesNotImpactOriginalRepository() throws Exception {
        // @formatter:off
        this.mockMvc.perform(post("/").with(SecurityMockMvcRequestPostProcessors.csrf()));
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
        CsrfToken token = repo.generateToken(request);
        repo.saveToken(token, request, new MockHttpServletResponse());
        MockHttpServletRequestBuilder requestWithCsrf = post("/").param(token.getParameterName(), token.getToken()).session(((MockHttpSession) (request.getSession())));
        this.mockMvc.perform(requestWithCsrf).andExpect(status().isOk());
        // @formatter:on
    }

    static class CsrfParamResultMatcher implements ResultMatcher {
        @Override
        public void match(MvcResult result) throws Exception {
            MockHttpServletRequest request = result.getRequest();
            assertThat(request.getParameter("_csrf")).isNotNull();
            assertThat(request.getHeader("X-CSRF-TOKEN")).isNull();
        }
    }

    static class CsrfHeaderResultMatcher implements ResultMatcher {
        @Override
        public void match(MvcResult result) throws Exception {
            MockHttpServletRequest request = result.getRequest();
            assertThat(request.getParameter("_csrf")).isNull();
            assertThat(request.getHeader("X-CSRF-TOKEN")).isNotNull();
        }
    }

    static class SessionRepositoryFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(new SecurityMockMvcRequestPostProcessorsCsrfTests.SessionRepositoryFilter.SessionRequestWrapper(request), response);
        }

        static class SessionRequestWrapper extends HttpServletRequestWrapper {
            HttpSession session = new MockHttpSession();

            public SessionRequestWrapper(HttpServletRequest request) {
                super(request);
            }

            @Override
            public HttpSession getSession(boolean create) {
                return this.session;
            }

            @Override
            public HttpSession getSession() {
                return this.session;
            }
        }
    }

    @EnableWebSecurity
    static class Config extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
        }

        @RestController
        static class TheController {
            @RequestMapping("/")
            String index() {
                return "Hi";
            }
        }
    }
}

