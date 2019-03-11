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
package org.springframework.security.messaging.context;


import SimpMessageHeaderAccessor.USER_HEADER;
import java.security.Principal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;


@RunWith(MockitoJUnitRunner.class)
public class SecurityContextChannelInterceptorTests {
    @Mock
    MessageChannel channel;

    @Mock
    MessageHandler handler;

    @Mock
    Principal principal;

    MessageBuilder<String> messageBuilder;

    Authentication authentication;

    SecurityContextChannelInterceptor interceptor;

    AnonymousAuthenticationToken expectedAnonymous;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullHeader() {
        new SecurityContextChannelInterceptor(null);
    }

    @Test
    public void preSendCustomHeader() throws Exception {
        String headerName = "header";
        interceptor = new SecurityContextChannelInterceptor(headerName);
        messageBuilder.setHeader(headerName, authentication);
        interceptor.preSend(messageBuilder.build(), channel);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
    }

    @Test
    public void preSendUserSet() throws Exception {
        messageBuilder.setHeader(USER_HEADER, authentication);
        interceptor.preSend(messageBuilder.build(), channel);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAnonymousAuthenticationNull() {
        interceptor.setAnonymousAuthentication(null);
    }

    @Test
    public void preSendUsesCustomAnonymous() throws Exception {
        expectedAnonymous = new AnonymousAuthenticationToken("customKey", "customAnonymous", AuthorityUtils.createAuthorityList("ROLE_CUSTOM"));
        interceptor.setAnonymousAuthentication(expectedAnonymous);
        interceptor.preSend(messageBuilder.build(), channel);
        assertAnonymous();
    }

    // SEC-2845
    @Test
    public void preSendUserNotAuthentication() throws Exception {
        messageBuilder.setHeader(USER_HEADER, principal);
        interceptor.preSend(messageBuilder.build(), channel);
        assertAnonymous();
    }

    // SEC-2845
    @Test
    public void preSendUserNotSet() throws Exception {
        interceptor.preSend(messageBuilder.build(), channel);
        assertAnonymous();
    }

    // SEC-2845
    @Test
    public void preSendUserNotSetCustomAnonymous() throws Exception {
        interceptor.preSend(messageBuilder.build(), channel);
        assertAnonymous();
    }

    @Test
    public void afterSendCompletion() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        interceptor.afterSendCompletion(messageBuilder.build(), channel, true, null);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void afterSendCompletionNullAuthentication() throws Exception {
        interceptor.afterSendCompletion(messageBuilder.build(), channel, true, null);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void beforeHandleUserSet() throws Exception {
        messageBuilder.setHeader(USER_HEADER, authentication);
        interceptor.beforeHandle(messageBuilder.build(), channel, handler);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
    }

    // SEC-2845
    @Test
    public void beforeHandleUserNotAuthentication() throws Exception {
        messageBuilder.setHeader(USER_HEADER, principal);
        interceptor.beforeHandle(messageBuilder.build(), channel, handler);
        assertAnonymous();
    }

    // SEC-2845
    @Test
    public void beforeHandleUserNotSet() throws Exception {
        interceptor.beforeHandle(messageBuilder.build(), channel, handler);
        assertAnonymous();
    }

    @Test
    public void afterMessageHandledUserNotSet() throws Exception {
        interceptor.afterMessageHandled(messageBuilder.build(), channel, handler, null);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void afterMessageHandled() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        interceptor.afterMessageHandled(messageBuilder.build(), channel, handler, null);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // SEC-2829
    @Test
    public void restoresOriginalContext() throws Exception {
        TestingAuthenticationToken original = new TestingAuthenticationToken("original", "original", "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(original);
        messageBuilder.setHeader(USER_HEADER, authentication);
        interceptor.beforeHandle(messageBuilder.build(), channel, handler);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
        interceptor.afterMessageHandled(messageBuilder.build(), channel, handler, null);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(original);
    }

    /**
     * If a user sends a websocket when processing another websocket
     *
     * @throws Exception
     * 		
     */
    @Test
    public void restoresOriginalContextNestedThreeDeep() throws Exception {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken origional = new TestingAuthenticationToken("original", "origional", "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(origional);
        messageBuilder.setHeader(USER_HEADER, authentication);
        interceptor.beforeHandle(messageBuilder.build(), channel, handler);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
        // start send websocket
        messageBuilder.setHeader(USER_HEADER, null);
        interceptor.beforeHandle(messageBuilder.build(), channel, handler);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(anonymous.getName());
        interceptor.afterMessageHandled(messageBuilder.build(), channel, handler, null);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
        // end send websocket
        interceptor.afterMessageHandled(messageBuilder.build(), channel, handler, null);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(origional);
    }
}

