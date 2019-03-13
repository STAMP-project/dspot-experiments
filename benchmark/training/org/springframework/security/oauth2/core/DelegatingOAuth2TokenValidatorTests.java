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
package org.springframework.security.oauth2.core;


import java.util.Arrays;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for verifying {@link DelegatingOAuth2TokenValidator}
 *
 * @author Josh Cummings
 */
public class DelegatingOAuth2TokenValidatorTests {
    private static final OAuth2Error DETAIL = new OAuth2Error("error", "description", "uri");

    @Test
    public void validateWhenNoValidatorsConfiguredThenReturnsSuccessfulResult() {
        DelegatingOAuth2TokenValidator<AbstractOAuth2Token> tokenValidator = new DelegatingOAuth2TokenValidator();
        AbstractOAuth2Token token = Mockito.mock(AbstractOAuth2Token.class);
        assertThat(tokenValidator.validate(token).hasErrors()).isFalse();
    }

    @Test
    public void validateWhenAnyValidatorFailsThenReturnsFailureResultContainingDetailFromFailingValidator() {
        OAuth2TokenValidator<AbstractOAuth2Token> success = Mockito.mock(OAuth2TokenValidator.class);
        OAuth2TokenValidator<AbstractOAuth2Token> failure = Mockito.mock(OAuth2TokenValidator.class);
        Mockito.when(success.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.success());
        Mockito.when(failure.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.failure(DelegatingOAuth2TokenValidatorTests.DETAIL));
        DelegatingOAuth2TokenValidator<AbstractOAuth2Token> tokenValidator = new DelegatingOAuth2TokenValidator(Arrays.asList(success, failure));
        AbstractOAuth2Token token = Mockito.mock(AbstractOAuth2Token.class);
        OAuth2TokenValidatorResult result = tokenValidator.validate(token);
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).containsExactly(DelegatingOAuth2TokenValidatorTests.DETAIL);
    }

    @Test
    public void validateWhenMultipleValidatorsFailThenReturnsFailureResultContainingAllDetails() {
        OAuth2TokenValidator<AbstractOAuth2Token> firstFailure = Mockito.mock(OAuth2TokenValidator.class);
        OAuth2TokenValidator<AbstractOAuth2Token> secondFailure = Mockito.mock(OAuth2TokenValidator.class);
        OAuth2Error otherDetail = new OAuth2Error("another-error");
        Mockito.when(firstFailure.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.failure(DelegatingOAuth2TokenValidatorTests.DETAIL));
        Mockito.when(secondFailure.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.failure(otherDetail));
        DelegatingOAuth2TokenValidator<AbstractOAuth2Token> tokenValidator = new DelegatingOAuth2TokenValidator(firstFailure, secondFailure);
        AbstractOAuth2Token token = Mockito.mock(AbstractOAuth2Token.class);
        OAuth2TokenValidatorResult result = tokenValidator.validate(token);
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors()).containsExactly(DelegatingOAuth2TokenValidatorTests.DETAIL, otherDetail);
    }

    @Test
    public void validateWhenAllValidatorsSucceedThenReturnsSuccessfulResult() {
        OAuth2TokenValidator<AbstractOAuth2Token> firstSuccess = Mockito.mock(OAuth2TokenValidator.class);
        OAuth2TokenValidator<AbstractOAuth2Token> secondSuccess = Mockito.mock(OAuth2TokenValidator.class);
        Mockito.when(firstSuccess.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.success());
        Mockito.when(secondSuccess.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.success());
        DelegatingOAuth2TokenValidator<AbstractOAuth2Token> tokenValidator = new DelegatingOAuth2TokenValidator(Arrays.asList(firstSuccess, secondSuccess));
        AbstractOAuth2Token token = Mockito.mock(AbstractOAuth2Token.class);
        OAuth2TokenValidatorResult result = tokenValidator.validate(token);
        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    public void constructorWhenInvokedWithNullValidatorListThenThrowsIllegalArgumentException() {
        assertThatCode(() -> new DelegatingOAuth2TokenValidator<>(((Collection<OAuth2TokenValidator<AbstractOAuth2Token>>) (null)))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void constructorsWhenInvokedWithSameInputsThenResultInSameOutputs() {
        OAuth2TokenValidator<AbstractOAuth2Token> firstSuccess = Mockito.mock(OAuth2TokenValidator.class);
        OAuth2TokenValidator<AbstractOAuth2Token> secondSuccess = Mockito.mock(OAuth2TokenValidator.class);
        Mockito.when(firstSuccess.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.success());
        Mockito.when(secondSuccess.validate(ArgumentMatchers.any(AbstractOAuth2Token.class))).thenReturn(OAuth2TokenValidatorResult.success());
        DelegatingOAuth2TokenValidator<AbstractOAuth2Token> firstValidator = new DelegatingOAuth2TokenValidator(Arrays.asList(firstSuccess, secondSuccess));
        DelegatingOAuth2TokenValidator<AbstractOAuth2Token> secondValidator = new DelegatingOAuth2TokenValidator(firstSuccess, secondSuccess);
        AbstractOAuth2Token token = Mockito.mock(AbstractOAuth2Token.class);
        firstValidator.validate(token);
        secondValidator.validate(token);
        Mockito.verify(firstSuccess, Mockito.times(2)).validate(token);
        Mockito.verify(secondSuccess, Mockito.times(2)).validate(token);
    }
}

