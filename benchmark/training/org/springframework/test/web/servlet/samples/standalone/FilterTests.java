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
package org.springframework.test.web.servlet.samples.standalone;


import MediaType.APPLICATION_JSON;
import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.Person;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Tests with {@link Filter}'s.
 *
 * @author Rob Winch
 */
public class FilterTests {
    @Test
    public void whenFiltersCompleteMvcProcessesRequest() throws Exception {
        MockMvcBuilders.standaloneSetup(new FilterTests.PersonController()).addFilters(new FilterTests.ContinueFilter()).build().perform(MockMvcRequestBuilders.post("/persons").param("name", "Andy")).andExpect(MockMvcResultMatchers.status().isFound()).andExpect(MockMvcResultMatchers.redirectedUrl("/person/1")).andExpect(MockMvcResultMatchers.model().size(1)).andExpect(MockMvcResultMatchers.model().attributeExists("id")).andExpect(MockMvcResultMatchers.flash().attributeCount(1)).andExpect(MockMvcResultMatchers.flash().attribute("message", "success!"));
    }

    @Test
    public void filtersProcessRequest() throws Exception {
        MockMvcBuilders.standaloneSetup(new FilterTests.PersonController()).addFilters(new FilterTests.ContinueFilter(), new FilterTests.RedirectFilter()).build().perform(MockMvcRequestBuilders.post("/persons").param("name", "Andy")).andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    public void filterMappedBySuffix() throws Exception {
        MockMvcBuilders.standaloneSetup(new FilterTests.PersonController()).addFilter(new FilterTests.RedirectFilter(), "*.html").build().perform(MockMvcRequestBuilders.post("/persons.html").param("name", "Andy")).andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    public void filterWithExactMapping() throws Exception {
        MockMvcBuilders.standaloneSetup(new FilterTests.PersonController()).addFilter(new FilterTests.RedirectFilter(), "/p", "/persons").build().perform(MockMvcRequestBuilders.post("/persons").param("name", "Andy")).andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    public void filterSkipped() throws Exception {
        MockMvcBuilders.standaloneSetup(new FilterTests.PersonController()).addFilter(new FilterTests.RedirectFilter(), "/p", "/person").build().perform(MockMvcRequestBuilders.post("/persons").param("name", "Andy")).andExpect(MockMvcResultMatchers.status().isFound()).andExpect(MockMvcResultMatchers.redirectedUrl("/person/1")).andExpect(MockMvcResultMatchers.model().size(1)).andExpect(MockMvcResultMatchers.model().attributeExists("id")).andExpect(MockMvcResultMatchers.flash().attributeCount(1)).andExpect(MockMvcResultMatchers.flash().attribute("message", "success!"));
    }

    @Test
    public void filterWrapsRequestResponse() throws Exception {
        MockMvcBuilders.standaloneSetup(new FilterTests.PersonController()).addFilters(new FilterTests.WrappingRequestResponseFilter()).build().perform(MockMvcRequestBuilders.post("/user")).andExpect(MockMvcResultMatchers.model().attribute("principal", FilterTests.WrappingRequestResponseFilter.PRINCIPAL_NAME));
    }

    // SPR-16067, SPR-16695
    @Test
    public void filterWrapsRequestResponseAndPerformsAsyncDispatch() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new FilterTests.PersonController()).addFilters(new FilterTests.WrappingRequestResponseFilter(), new ShallowEtagHeaderFilter()).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/persons/1").accept(APPLICATION_JSON)).andExpect(MockMvcResultMatchers.request().asyncStarted()).andExpect(MockMvcResultMatchers.request().asyncResult(new Person("Lukas"))).andReturn();
        mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.header().longValue("Content-Length", 53)).andExpect(MockMvcResultMatchers.header().string("ETag", "\"0e37becb4f0c90709cb2e1efcc61eaa00\"")).andExpect(MockMvcResultMatchers.content().string("{\"name\":\"Lukas\",\"someDouble\":0.0,\"someBoolean\":false}"));
    }

    @Controller
    private static class PersonController {
        @PostMapping(path = "/persons")
        public String save(@Valid
        Person person, Errors errors, RedirectAttributes redirectAttrs) {
            if (errors.hasErrors()) {
                return "person/add";
            }
            redirectAttrs.addAttribute("id", "1");
            redirectAttrs.addFlashAttribute("message", "success!");
            return "redirect:/person/{id}";
        }

        @PostMapping("/user")
        public ModelAndView user(Principal principal) {
            return new ModelAndView("user/view", "principal", principal.getName());
        }

        @GetMapping("/forward")
        public String forward() {
            return "forward:/persons";
        }

        @GetMapping("persons/{id}")
        @ResponseBody
        public CompletableFuture<Person> getPerson() {
            return CompletableFuture.completedFuture(new Person("Lukas"));
        }
    }

    private class ContinueFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(request, response);
        }
    }

    private static class WrappingRequestResponseFilter extends OncePerRequestFilter {
        public static final String PRINCIPAL_NAME = "WrapRequestResponseFilterPrincipal";

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(new HttpServletRequestWrapper(request) {
                @Override
                public Principal getUserPrincipal() {
                    return () -> FilterTests.WrappingRequestResponseFilter.PRINCIPAL_NAME;
                }

                // Like Spring Security does in HttpServlet3RequestFactory..
                @Override
                public AsyncContext getAsyncContext() {
                    return (super.getAsyncContext()) != null ? new FilterTests.AsyncContextWrapper(super.getAsyncContext()) : null;
                }
            }, new javax.servlet.http.HttpServletResponseWrapper(response));
        }
    }

    private class RedirectFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            response.sendRedirect("/login");
        }
    }

    private static class AsyncContextWrapper implements AsyncContext {
        private final AsyncContext delegate;

        public AsyncContextWrapper(AsyncContext delegate) {
            this.delegate = delegate;
        }

        @Override
        public ServletRequest getRequest() {
            return this.delegate.getRequest();
        }

        @Override
        public ServletResponse getResponse() {
            return this.delegate.getResponse();
        }

        @Override
        public boolean hasOriginalRequestAndResponse() {
            return this.delegate.hasOriginalRequestAndResponse();
        }

        @Override
        public void dispatch() {
            this.delegate.dispatch();
        }

        @Override
        public void dispatch(String path) {
            this.delegate.dispatch(path);
        }

        @Override
        public void dispatch(ServletContext context, String path) {
            this.delegate.dispatch(context, path);
        }

        @Override
        public void complete() {
            this.delegate.complete();
        }

        @Override
        public void start(Runnable run) {
            this.delegate.start(run);
        }

        @Override
        public void addListener(AsyncListener listener) {
            this.delegate.addListener(listener);
        }

        @Override
        public void addListener(AsyncListener listener, ServletRequest req, ServletResponse res) {
            this.delegate.addListener(listener, req, res);
        }

        @Override
        public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
            return this.delegate.createListener(clazz);
        }

        @Override
        public void setTimeout(long timeout) {
            this.delegate.setTimeout(timeout);
        }

        @Override
        public long getTimeout() {
            return this.delegate.getTimeout();
        }
    }
}

