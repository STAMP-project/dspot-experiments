/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.web.context;


import HttpServletResponse.SC_BAD_REQUEST;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 *
 *
 * @author Luke Taylor
 * @author Rob Winch
 */
public class HttpSessionSecurityContextRepositoryTests {
    private final TestingAuthenticationToken testToken = new TestingAuthenticationToken("someone", "passwd", "ROLE_A");

    @Test
    public void startAsyncDisablesSaveOnCommit() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        repo.loadContext(holder);
        Mockito.reset(request);
        holder.getRequest().startAsync();
        holder.getResponse().sendError(SC_BAD_REQUEST);
        // ensure that sendError did cause interaction with the HttpSession
        Mockito.verify(request, Mockito.never()).getSession(ArgumentMatchers.anyBoolean());
        Mockito.verify(request, Mockito.never()).getSession();
    }

    @Test
    public void startAsyncRequestResponseDisablesSaveOnCommit() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        repo.loadContext(holder);
        Mockito.reset(request);
        holder.getRequest().startAsync(request, response);
        holder.getResponse().sendError(SC_BAD_REQUEST);
        // ensure that sendError did cause interaction with the HttpSession
        Mockito.verify(request, Mockito.never()).getSession(ArgumentMatchers.anyBoolean());
        Mockito.verify(request, Mockito.never()).getSession();
    }

    @Test
    public void sessionIsntCreatedIfContextDoesntChange() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        assertThat(request.getSession(false)).isNull();
        repo.saveContext(context, holder.getRequest(), holder.getResponse());
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    public void sessionIsntCreatedIfAllowSessionCreationIsFalse() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setAllowSessionCreation(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        // Change context
        context.setAuthentication(testToken);
        repo.saveContext(context, holder.getRequest(), holder.getResponse());
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    public void existingContextIsSuccessFullyLoadedFromSessionAndSavedBack() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        SecurityContextHolder.getContext().setAuthentication(testToken);
        request.getSession().setAttribute("imTheContext", SecurityContextHolder.getContext());
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        assertThat(context).isNotNull();
        assertThat(context.getAuthentication()).isEqualTo(testToken);
        // Won't actually be saved as it hasn't changed, but go through the use case
        // anyway
        repo.saveContext(context, holder.getRequest(), holder.getResponse());
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(context);
    }

    // SEC-1528
    @Test
    public void saveContextCallsSetAttributeIfContextIsModifiedDirectlyDuringRequest() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        // Set up an existing authenticated context, mocking that it is in the session
        // already
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(testToken);
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)).thenReturn(ctx);
        request.setSession(session);
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, new MockHttpServletResponse());
        assertThat(repo.loadContext(holder)).isSameAs(ctx);
        // Modify context contents. Same user, different role
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("someone", "passwd", "ROLE_B"));
        repo.saveContext(ctx, holder.getRequest(), holder.getResponse());
        // Must be called even though the value in the local VM is already the same
        Mockito.verify(session).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);
    }

    @Test
    public void nonSecurityContextInSessionIsIgnored() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        SecurityContextHolder.getContext().setAuthentication(testToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, "NotASecurityContextInstance");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        assertThat(context).isNotNull();
        assertThat(context.getAuthentication()).isNull();
    }

    @Test
    public void sessionIsCreatedAndContextStoredWhenContextChanges() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        assertThat(request.getSession(false)).isNull();
        // Simulate authentication during the request
        context.setAuthentication(testToken);
        repo.saveContext(context, holder.getRequest(), holder.getResponse());
        assertThat(request.getSession(false)).isNotNull();
        assertThat(request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)).isEqualTo(context);
    }

    @Test
    public void redirectCausesEarlySaveOfContext() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().sendRedirect("/doesntmatter");
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
        assertThat(isContextSaved()).isTrue();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        // Check it's still the same
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
    }

    @Test
    public void sendErrorCausesEarlySaveOfContext() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().sendError(404);
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
        assertThat(isContextSaved()).isTrue();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        // Check it's still the same
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
    }

    // SEC-2005
    @Test
    public void flushBufferCausesEarlySaveOfContext() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().flushBuffer();
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
        assertThat(isContextSaved()).isTrue();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        // Check it's still the same
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
    }

    // SEC-2005
    @Test
    public void writerFlushCausesEarlySaveOfContext() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().getWriter().flush();
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
        assertThat(isContextSaved()).isTrue();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        // Check it's still the same
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
    }

    // SEC-2005
    @Test
    public void writerCloseCausesEarlySaveOfContext() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().getWriter().close();
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
        assertThat(isContextSaved()).isTrue();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        // Check it's still the same
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
    }

    // SEC-2005
    @Test
    public void outputStreamFlushCausesEarlySaveOfContext() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().getOutputStream().flush();
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
        assertThat(isContextSaved()).isTrue();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        // Check it's still the same
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
    }

    // SEC-2005
    @Test
    public void outputStreamCloseCausesEarlySaveOfContext() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().getOutputStream().close();
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
        assertThat(isContextSaved()).isTrue();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        // Check it's still the same
        assertThat(request.getSession().getAttribute("imTheContext")).isEqualTo(SecurityContextHolder.getContext());
    }

    // SEC-SEC-2055
    @Test
    public void outputStreamCloseDelegate() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputstream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputstream);
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().getOutputStream().close();
        Mockito.verify(outputstream).close();
    }

    // SEC-SEC-2055
    @Test
    public void outputStreamFlushesDelegate() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputstream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputstream);
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        holder.getResponse().getOutputStream().flush();
        Mockito.verify(outputstream).flush();
    }

    @Test
    public void noSessionIsCreatedIfSessionWasInvalidatedDuringTheRequest() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(testToken);
        request.getSession().invalidate();
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        assertThat(request.getSession(false)).isNull();
    }

    // SEC-1315
    @Test
    public void noSessionIsCreatedIfAnonymousTokenIsUsed() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextHolder.setContext(repo.loadContext(holder));
        SecurityContextHolder.getContext().setAuthentication(new org.springframework.security.authentication.AnonymousAuthenticationToken("key", "anon", AuthorityUtils.createAuthorityList("ANON")));
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        assertThat(request.getSession(false)).isNull();
    }

    // SEC-1587
    @Test
    public void contextIsRemovedFromSessionIfCurrentContextIsAnonymous() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        SecurityContext ctxInSession = SecurityContextHolder.createEmptyContext();
        ctxInSession.setAuthentication(testToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctxInSession);
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, new MockHttpServletResponse());
        repo.loadContext(holder);
        SecurityContextHolder.getContext().setAuthentication(new org.springframework.security.authentication.AnonymousAuthenticationToken("x", "x", testToken.getAuthorities()));
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        assertThat(request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)).isNull();
    }

    @Test
    public void contextIsRemovedFromSessionIfCurrentContextIsEmpty() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey("imTheContext");
        MockHttpServletRequest request = new MockHttpServletRequest();
        SecurityContext ctxInSession = SecurityContextHolder.createEmptyContext();
        ctxInSession.setAuthentication(testToken);
        request.getSession().setAttribute("imTheContext", ctxInSession);
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, new MockHttpServletResponse());
        repo.loadContext(holder);
        // Save an empty context
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        assertThat(request.getSession().getAttribute("imTheContext")).isNull();
    }

    // SEC-1735
    @Test
    public void contextIsNotRemovedFromSessionIfContextBeforeExecutionDefault() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, new MockHttpServletResponse());
        repo.loadContext(holder);
        SecurityContext ctxInSession = SecurityContextHolder.createEmptyContext();
        ctxInSession.setAuthentication(testToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctxInSession);
        SecurityContextHolder.getContext().setAuthentication(new org.springframework.security.authentication.AnonymousAuthenticationToken("x", "x", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        repo.saveContext(SecurityContextHolder.getContext(), holder.getRequest(), holder.getResponse());
        assertThat(ctxInSession).isSameAs(request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    // SEC-3070
    @Test
    public void logoutInvalidateSessionFalseFails() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        SecurityContext ctxInSession = SecurityContextHolder.createEmptyContext();
        ctxInSession.setAuthentication(testToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctxInSession);
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, new MockHttpServletResponse());
        repo.loadContext(holder);
        ctxInSession.setAuthentication(null);
        repo.saveContext(ctxInSession, holder.getRequest(), holder.getResponse());
        assertThat(request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)).isNull();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void sessionDisableUrlRewritingPreventsSessionIdBeingWrittenToUrl() throws Exception {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        final String sessionId = ";jsessionid=id";
        MockHttpServletResponse response = new MockHttpServletResponse() {
            @Override
            public String encodeRedirectUrl(String url) {
                return url + sessionId;
            }

            @Override
            public String encodeRedirectURL(String url) {
                return url + sessionId;
            }

            @Override
            public String encodeUrl(String url) {
                return url + sessionId;
            }

            @Override
            public String encodeURL(String url) {
                return url + sessionId;
            }
        };
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        repo.loadContext(holder);
        String url = "/aUrl";
        assertThat(holder.getResponse().encodeRedirectUrl(url)).isEqualTo((url + sessionId));
        assertThat(holder.getResponse().encodeRedirectURL(url)).isEqualTo((url + sessionId));
        assertThat(holder.getResponse().encodeUrl(url)).isEqualTo((url + sessionId));
        assertThat(holder.getResponse().encodeURL(url)).isEqualTo((url + sessionId));
        repo.setDisableUrlRewriting(true);
        holder = new HttpRequestResponseHolder(request, response);
        repo.loadContext(holder);
        assertThat(holder.getResponse().encodeRedirectUrl(url)).isEqualTo(url);
        assertThat(holder.getResponse().encodeRedirectURL(url)).isEqualTo(url);
        assertThat(holder.getResponse().encodeUrl(url)).isEqualTo(url);
        assertThat(holder.getResponse().encodeURL(url)).isEqualTo(url);
    }

    @Test
    public void saveContextCustomTrustResolver() {
        SecurityContext contextToSave = SecurityContextHolder.createEmptyContext();
        contextToSave.setAuthentication(testToken);
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, new MockHttpServletResponse());
        repo.loadContext(holder);
        AuthenticationTrustResolver trustResolver = mock(AuthenticationTrustResolver.class);
        repo.setTrustResolver(trustResolver);
        repo.saveContext(contextToSave, holder.getRequest(), holder.getResponse());
        Mockito.verify(trustResolver).isAnonymous(contextToSave.getAuthentication());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTrustResolverNull() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setTrustResolver(null);
    }

    // SEC-2578
    @Test
    public void traverseWrappedRequests() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        assertThat(request.getSession(false)).isNull();
        // Simulate authentication during the request
        context.setAuthentication(testToken);
        repo.saveContext(context, new javax.servlet.http.HttpServletRequestWrapper(holder.getRequest()), new javax.servlet.http.HttpServletResponseWrapper(holder.getResponse()));
        assertThat(request.getSession(false)).isNotNull();
        assertThat(request.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)).isEqualTo(context);
    }

    @Test(expected = IllegalStateException.class)
    public void failsWithStandardResponse() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(testToken);
        repo.saveContext(context, request, response);
    }

    @Test
    public void saveContextWhenTransientAuthenticationThenSkipped() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        HttpSessionSecurityContextRepositoryTests.SomeTransientAuthentication authentication = new HttpSessionSecurityContextRepositoryTests.SomeTransientAuthentication();
        context.setAuthentication(authentication);
        repo.saveContext(context, holder.getRequest(), holder.getResponse());
        MockHttpSession session = ((MockHttpSession) (request.getSession(false)));
        assertThat(session).isNull();
    }

    @Test
    public void saveContextWhenTransientAuthenticationSubclassThenSkipped() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        HttpSessionSecurityContextRepositoryTests.SomeTransientAuthenticationSubclass authentication = new HttpSessionSecurityContextRepositoryTests.SomeTransientAuthenticationSubclass();
        context.setAuthentication(authentication);
        repo.saveContext(context, holder.getRequest(), holder.getResponse());
        MockHttpSession session = ((MockHttpSession) (request.getSession(false)));
        assertThat(session).isNull();
    }

    @Test
    public void saveContextWhenTransientAuthenticationWithCustomAnnotationThenSkipped() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext context = repo.loadContext(holder);
        HttpSessionSecurityContextRepositoryTests.SomeOtherTransientAuthentication authentication = new HttpSessionSecurityContextRepositoryTests.SomeOtherTransientAuthentication();
        context.setAuthentication(authentication);
        repo.saveContext(context, holder.getRequest(), holder.getResponse());
        MockHttpSession session = ((MockHttpSession) (request.getSession(false)));
        assertThat(session).isNull();
    }

    @Transient
    private static class SomeTransientAuthentication extends AbstractAuthenticationToken {
        public SomeTransientAuthentication() {
            super(null);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }
    }

    private static class SomeTransientAuthenticationSubclass extends HttpSessionSecurityContextRepositoryTests.SomeTransientAuthentication {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Transient
    public @interface TestTransientAuthentication {}

    @HttpSessionSecurityContextRepositoryTests.TestTransientAuthentication
    private static class SomeOtherTransientAuthentication extends AbstractAuthenticationToken {
        public SomeOtherTransientAuthentication() {
            super(null);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }
    }
}

