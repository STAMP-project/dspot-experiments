package com.baeldung.chainofresponsibility;


import com.baeldung.pattern.chainofresponsibility.AuthenticationProcessor;
import com.baeldung.pattern.chainofresponsibility.OAuthTokenProvider;
import com.baeldung.pattern.chainofresponsibility.SamlAuthenticationProvider;
import com.baeldung.pattern.chainofresponsibility.UsernamePasswordProvider;
import org.junit.Assert;
import org.junit.Test;


public class ChainOfResponsibilityIntegrationTest {
    @Test
    public void givenOAuthProvider_whenCheckingAuthorized_thenSuccess() {
        AuthenticationProcessor authProcessorChain = ChainOfResponsibilityIntegrationTest.getChainOfAuthProcessor();
        boolean isAuthorized = authProcessorChain.isAuthorized(new OAuthTokenProvider());
        Assert.assertTrue(isAuthorized);
    }

    @Test
    public void givenUsernamePasswordProvider_whenCheckingAuthorized_thenSuccess() {
        AuthenticationProcessor authProcessorChain = ChainOfResponsibilityIntegrationTest.getChainOfAuthProcessor();
        boolean isAuthorized = authProcessorChain.isAuthorized(new UsernamePasswordProvider());
        Assert.assertTrue(isAuthorized);
    }

    @Test
    public void givenSamlAuthProvider_whenCheckingAuthorized_thenFailure() {
        AuthenticationProcessor authProcessorChain = ChainOfResponsibilityIntegrationTest.getChainOfAuthProcessor();
        boolean isAuthorized = authProcessorChain.isAuthorized(new SamlAuthenticationProvider());
        Assert.assertTrue((!isAuthorized));
    }
}

