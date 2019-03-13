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
package org.springframework.security.messaging.access.expression;


import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.messaging.Message;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;


@RunWith(MockitoJUnitRunner.class)
public class MessageExpressionVoterTests {
    @Mock
    Authentication authentication;

    @Mock
    Message<Object> message;

    Collection<ConfigAttribute> attributes;

    @Mock
    Expression expression;

    @Mock
    SecurityExpressionHandler<Message> expressionHandler;

    @Mock
    EvaluationContext evaluationContext;

    MessageExpressionVoter voter;

    @Test
    public void voteGranted() {
        Mockito.when(expression.getValue(ArgumentMatchers.any(EvaluationContext.class), ArgumentMatchers.eq(Boolean.class))).thenReturn(true);
        assertThat(voter.vote(authentication, message, attributes)).isEqualTo(ACCESS_GRANTED);
    }

    @Test
    public void voteDenied() {
        Mockito.when(expression.getValue(ArgumentMatchers.any(EvaluationContext.class), ArgumentMatchers.eq(Boolean.class))).thenReturn(false);
        assertThat(voter.vote(authentication, message, attributes)).isEqualTo(ACCESS_DENIED);
    }

    @Test
    public void voteAbstain() {
        attributes = Arrays.<ConfigAttribute>asList(new SecurityConfig("ROLE_USER"));
        assertThat(voter.vote(authentication, message, attributes)).isEqualTo(ACCESS_ABSTAIN);
    }

    @Test
    public void supportsObjectClassFalse() {
        assertThat(voter.supports(Object.class)).isFalse();
    }

    @Test
    public void supportsMessageClassTrue() {
        assertThat(voter.supports(Message.class)).isTrue();
    }

    @Test
    public void supportsSecurityConfigFalse() {
        assertThat(voter.supports(new SecurityConfig("ROLE_USER"))).isFalse();
    }

    @Test
    public void supportsMessageExpressionConfigAttributeTrue() {
        assertThat(voter.supports(new MessageExpressionConfigAttribute(expression))).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setExpressionHandlerNull() {
        voter.setExpressionHandler(null);
    }

    @Test
    public void customExpressionHandler() {
        voter.setExpressionHandler(expressionHandler);
        Mockito.when(expressionHandler.createEvaluationContext(authentication, message)).thenReturn(evaluationContext);
        Mockito.when(expression.getValue(evaluationContext, Boolean.class)).thenReturn(true);
        assertThat(voter.vote(authentication, message, attributes)).isEqualTo(ACCESS_GRANTED);
        Mockito.verify(expressionHandler).createEvaluationContext(authentication, message);
    }
}

